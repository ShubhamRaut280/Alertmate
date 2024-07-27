package com.shubham.emergencyapplication.BroadCastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
class SOSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getBooleanExtra("sos", false)
        if (message) {
            Log.d("OverlayService", " in receiver Received SOS message")
//            val serviceIntent = Intent(context, OverlayService::class.java)
//            context.startForegroundService(serviceIntent)
        }
    }
}
