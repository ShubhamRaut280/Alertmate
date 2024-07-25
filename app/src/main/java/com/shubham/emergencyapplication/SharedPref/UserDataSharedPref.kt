package com.shubham.emergencyapplication.SharedPref

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shubham.emergencyapplication.Utils.Constants.FAMILY_SHARED_PREF
import com.shubham.emergencyapplication.Utils.Constants.USER_SHARED_PREF

object UserDataSharedPref {



    fun setProfileUpdated(context: Context, key : Boolean) {
        val sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isProfileUpdated", key)
        editor.apply()
    }
    fun isProfileUpdated(context: Context): Boolean{
        val sharedPreferences = context.getSharedPreferences(USER_SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isProfileUpdated", false)
    }
}