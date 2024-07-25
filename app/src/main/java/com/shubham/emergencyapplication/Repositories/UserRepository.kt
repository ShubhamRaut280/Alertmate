package com.shubham.emergencyapplication.Repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.SharedPref.FamilySharedPref.setFamilyMemList
import com.shubham.emergencyapplication.Utils.Constants.FAMILY_MEM
import com.shubham.emergencyapplication.Utils.Constants.USERS_COLLECTION

object UserRepository {
    private val db : FirebaseFirestore  = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private var familyMembersListener: ListenerRegistration? = null

    fun saveFamilyMembers(context: Context) {
        val userId = auth.currentUser?.uid
        Log.d("repository", "user id $userId")
        if (userId != null) {
            Log.d("repository", "inside$userId")
            // Remove any previous listener
            familyMembersListener?.remove()

            // Set up a new listener
            familyMembersListener = db.collection(USERS_COLLECTION)
                .document(userId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.w("repository", "Listen failed.", exception)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val familyMembers = snapshot.get(FAMILY_MEM) as List<String>?
                        setFamilyMemList(context, FAMILY_MEM, familyMembers ?: emptyList())
                    } else {
                        Log.d("repository", "Current data: null")
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
}