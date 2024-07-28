package com.shubham.emergencyapplication.Utils

import android.Manifest

object Constants {
    val ACCELERATION_TRESOLD = 20.0   // m/s²
    val GYROSCOPE_THRESOLD = 5.0       // rad/s


    const val ACTION_CRASH_DETECTED = "com.shubham.emergencyapplication.CRASH_DETECTED"
    const val ACTION_SOS = "com.shubham.emergencyapplication.SOS_ACTION"

    const val SOS_COUNTDOWN = 10000L

    const val USERS_COLLECTION = "Users"
    const val EMAIL = "email"
    const val PHONE = "phone"
    const val NAME = "name"
    const val FAMILY_MEM = "family_members"
    const val  IMAGE_URL = "image_url"

    const val FAMILY_SHARED_PREF   = "FAMILY_SHARED_PREF"
    const val USER_SHARED_PREF   = "USER_SHARED_PREF"

    const val LOCATION_REF = "LocationsUpdates"


     val REQ_PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
         Manifest.permission.READ_MEDIA_IMAGES,
//         Manifest.permission.READ_MEDIA_VIDEO,
//         Manifest.permission.READ_MEDIA_AUDIO
    )
}