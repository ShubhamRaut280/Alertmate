package com.shubham.emergencyapplication.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.shubham.emergencyapplication.R

class CrashDetectionService : Service() {

    private lateinit var crashDetectionManager: CrashDetectionManager

    override fun onCreate() {
        super.onCreate()

        crashDetectionManager = CrashDetectionManager(this)
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Crash Detection Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Crash Detection Service")
            .setContentText("Detecting crashes...")
            .setSmallIcon(R.drawable.logo)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            crashDetectionManager.stop()
        } catch (e: Exception) {

        }
    }

    companion object {
        private const val CHANNEL_ID = "crash_detection_service_channel"
        private const val NOTIFICATION_ID = 1
    }
}
