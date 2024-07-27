package com.shubham.emergencyapplication.Services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.shubham.emergencyapplication.BroadCastReceivers.SOSReceiver
import com.shubham.emergencyapplication.BroadCastReceivers.ScreenStateReceiver
import com.shubham.emergencyapplication.Utils.Constants.ACTION_SOS

class PowerButtonMonitorService : Service() {

    private val handler = Handler()
    private var pressCount = 0
    private var lastScreenOffTime: Long = 0
    private val maxPresses = 4
    private val pressInterval = 2000L // Time interval to detect multiple presses in milliseconds
    lateinit var sosReceiver: SOSReceiver

    private val screenStateReceiver = object : ScreenStateReceiver() {
        override fun onScreenStateChanged() {
            checkScreenState()
            Log.d("PowerButtonMonitor", "change in screen state")

        }
    }

    override fun onCreate() {
        super.onCreate()
        // Register the receiver to listen for power button presses
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(screenStateReceiver, filter)
        sosReceiver = SOSReceiver()
        val intentFilter = IntentFilter(ACTION_SOS)
        registerReceiver(sosReceiver, intentFilter)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun checkScreenState() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScreenOffTime < pressInterval) {
            pressCount++
            Log.d("PowerButtonMonitor", "count increased $pressCount")

        } else {
            pressCount = 1
        }
        lastScreenOffTime = currentTime

        if (pressCount >= maxPresses) {
            Log.d("PowerButtonMonitor", "Power button pressed $maxPresses times")
//            Toast.makeText(this, "Power button pressed $maxPresses times", Toast.LENGTH_SHORT).show()
            sendSOSTrigger()
            pressCount = 0
        }
    }

    private fun sendSOSTrigger() {
        val broadcastIntent = Intent(ACTION_SOS)
        broadcastIntent.putExtra("sos", true)
        sendBroadcast(broadcastIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(sosReceiver)
        handler.removeCallbacksAndMessages(null)
    }
}
