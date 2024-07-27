package com.shubham.emergencyapplication.Repositories

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.type.DateTime
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.FamilyLocation
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.SharedPref.FamilySharedPref.getFamilyMemList
import com.shubham.emergencyapplication.SharedPref.FamilySharedPref.setFamilyMemList
import com.shubham.emergencyapplication.SharedPref.UserDataSharedPref.getUserDetails
import com.shubham.emergencyapplication.SharedPref.UserDataSharedPref.setUserDetails
import com.shubham.emergencyapplication.Utils.Constants.FAMILY_MEM
import com.shubham.emergencyapplication.Utils.Constants.LOCATION_REF
import com.shubham.emergencyapplication.Utils.Constants.NAME
import com.shubham.emergencyapplication.Utils.Constants.USERS_COLLECTION
import com.shubham.emergencyapplication.Utils.DateUtils.getCurrentDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object UserRepository {
    private val db : FirebaseFirestore  = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "UserRepository"
    private val realtimeDB : FirebaseDatabase = FirebaseDatabase.getInstance()

    private var familyMembersListener: ListenerRegistration? = null

    fun getFamilyMembers(context: Context, callback: ResponseCallBack<List<User>>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Remove any existing listener
            familyMembersListener?.remove()

            // Listen for changes in the user's family members list
            familyMembersListener = db.collection(USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.d(TAG, "Error fetching family members list: $exception")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val name = snapshot.get(NAME) as String?
                        if(!name.isNullOrEmpty()) setUserDetails(context, NAME, name)
                        val familyMembers = snapshot.get(FAMILY_MEM) as List<String>? ?: listOf()
                        setFamilyMemList(context, FAMILY_MEM, familyMembers)

                        // Fetch the user documents only once
                        fetchUserDocuments(familyMembers, callback)

                        setupUserDocumentListeners(familyMembers, callback)
                    }
                }
        }
    }

    private fun fetchUserDocuments(familyMembers: List<String>, callback: ResponseCallBack<List<User>>) {
        val userRefs = familyMembers.map { db.collection(USERS_COLLECTION).document(it) }
        val users = mutableListOf<User>()

        val tasks = userRefs.map { userRef ->
            userRef.get().continueWith { task ->
                val snapshot = task.result
                val user = snapshot?.toObject(User::class.java)
                user?.let { users.add(it) }
            }
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            callback.onSuccess(users)
        }.addOnFailureListener {
            callback.onError(it.message)
        }
    }

    private fun setupUserDocumentListeners(familyMembers: List<String>, callback: ResponseCallBack<List<User>>) {
        val userRefs = familyMembers.map { db.collection(USERS_COLLECTION).document(it) }
        val users = mutableListOf<User>()

        userRefs.forEach { userRef ->
            userRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d(TAG, "Error fetching user: $error")
                    return@addSnapshotListener
                }

                val updatedUser = snapshot?.toObject(User::class.java)
                updatedUser?.let {
                    val index = users.indexOfFirst { user -> user.id == it.id }
                    if (index != -1) {
                        users[index] = it
                    } else {
                        users.add(it)
                    }
                    Log.d(TAG, "Updated user: $it")
                    callback.onSuccess(users)
                }
            }
        }
    }

    fun stopListening() {
        familyMembersListener?.remove()
    }

    fun getSpecificUserInfo(context: Context, id : String, callBack: ResponseCallBack<User>){

            db.collection(USERS_COLLECTION)
                .document(id)
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
                "name" to getUserDetails(context, NAME),
                "latitude" to latitude,
                "longitude" to longitude,
                "timestamp" to getCurrentDateTime()
            )
            realtimeDB.reference.child(LOCATION_REF).child(userId)
                .setValue(location)
        }
    }



    fun getLocation(context: Context, userid: String?, callBack: ResponseCallBack<Pair<String,Pair<Double, Double>>>){
        if(!userid.isNullOrEmpty()){
            realtimeDB.reference.child(LOCATION_REF).child(userid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        try {
                            val name = snapshot.child("name").value as String
                            val lat = snapshot.child("latitude").value as Double
                            val lng = snapshot.child("longitude").value as Double
                            val timestamp = snapshot.child("timestamp").value as String
                            callBack.onSuccess(Pair(timestamp, Pair(lat, lng)))
                        } catch (e: Exception) {
                            Log.d("UserRepository", "Error getting location: ${e.message}")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callBack.onError(error.message)
                }
            })
        }
    }

    fun getFamilyMembersList(context: Context, callback: ResponseCallBack<MutableList<String>>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        callback.onError(exception.message)
                        Log.d(TAG, "Error fetching family members list: $exception")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val familyMembers = snapshot.get(FAMILY_MEM) as MutableList<String>??: mutableListOf()
                        callback.onSuccess(familyMembers)
                    }
                }
        }
    }

    fun getLocationOnce(context: Context, userid: String?, callBack: ResponseCallBack<FamilyLocation>?) {
        if (!userid.isNullOrEmpty()) {
            realtimeDB.reference.child(LOCATION_REF).child(userid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val name = snapshot.child("name").value as String
                            val lat = snapshot.child("latitude").value as Double
                            val lng = snapshot.child("longitude").value as Double
                            val timestamp = snapshot.child("timestamp").value as String
                            callBack?.onSuccess(FamilyLocation(name, lat, lng, timestamp))
                        } catch (e: Exception) {
                            Log.d("UserRepository", "Error getting location: ${e.message} ${e.printStackTrace()}for userid $userid")
                            callBack?.onError("Error processing data")
                        }
                    } else {
                        callBack?.onError("No data available")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callBack?.onError(error.message)
                }
            })
        } else {
            callBack?.onError("User ID is null or empty")
        }
    }

}