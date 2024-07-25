package com.shubham.emergencyapplication.Repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.SharedPref.FamilySharedPref.setFamilyMemList
import com.shubham.emergencyapplication.Utils.Constants.FAMILY_MEM
import com.shubham.emergencyapplication.Utils.Constants.LOCATION_REF
import com.shubham.emergencyapplication.Utils.Constants.USERS_COLLECTION

object UserRepository {
    private val db : FirebaseFirestore  = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "UserRepository"
    private val realtimeDB : FirebaseDatabase = FirebaseDatabase.getInstance()

    private var familyMembersListener: ListenerRegistration? = null

    fun saveFamilyMembers(context: Context) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            familyMembersListener?.remove()

            familyMembersListener = db.collection(USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val familyMembers = snapshot.get(FAMILY_MEM) as List<String>?
                        setFamilyMemList(context, FAMILY_MEM, familyMembers ?: emptyList())
                    }
                }
        }
    }

    fun stopListening() {
        familyMembersListener?.remove()
    }

    fun getUserInfo(context: Context, callBack: ResponseCallBack<User>){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener {
                    var userInfo = User(
                        it.getString("name"),
                        it.getString("email"),
                        it.getLong("phone") ,
                        it.id,
                        it.getString("image_url"),
                        it.get("family_members") as List<String>?
                    )
                    callBack.onSuccess(userInfo)
                }
                .addOnFailureListener {
                    callBack.onError(it.message)
                }

        }
    }
    fun checkIfUserExist(email : String, context: Context, callBack: ResponseCallBack<String>){
            db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener {
                    if(it.isEmpty){
                        callBack.onSuccess("")
                    }else{
                        callBack.onSuccess(it.documents[0].id)
                    }
                }


    }

    fun setUserInfo(context: Context, map: Map<String, Any>,callBack: ResponseCallBack<String>){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .set(map, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    callBack.onSuccess("Details Updated Successfully")
                }.addOnFailureListener {
                    callBack.onError(it.message)
                }

        }
    }

    fun addLocationToDb(context: Context, latitude: Double, longitude: Double){
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val location = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )
            realtimeDB.reference.child(LOCATION_REF).child(userId)
                .setValue(location)
        }
    }
    fun getLocation(context: Context, userid: String?, callBack: ResponseCallBack<Pair<Double, Double>>){
        if(!userid.isNullOrEmpty()){
            realtimeDB.reference.child(LOCATION_REF).child(userid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val lat = snapshot.child("latitude").value as Double
                        val lng = snapshot.child("longitude").value as Double
                        callBack.onSuccess(Pair(lat, lng))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callBack.onError(error.message)
                }
            })
        }
    }
}