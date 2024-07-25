package com.shubham.emergencyapplication.SharedPref

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shubham.emergencyapplication.Utils.Constants.FAMILY_SHARED_PREF

object FamilySharedPref {



    fun setFamilyMemList(context: Context, key: String, list: List<String>) {
        val gson = Gson()
        val json = gson.toJson(list)
        val sharedPreferences = context.getSharedPreferences(FAMILY_SHARED_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, json)
        editor.apply()
    }
    fun getFamilyMemList(context: Context, key: String): List<String>? {
        val sharedPreferences = context.getSharedPreferences(FAMILY_SHARED_PREF, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }
}