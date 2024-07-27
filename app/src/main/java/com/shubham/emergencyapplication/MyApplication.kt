package com.shubham.emergencyapplication

import android.app.Application
import android.content.Intent
import com.google.firebase.FirebaseApp
import com.shubham.emergencyapplication.Services.PowerButtonMonitorService

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
//        val intent = Intent(this, PowerButtonMonitorService::class.java)
//        startService(intent)

    }
}
