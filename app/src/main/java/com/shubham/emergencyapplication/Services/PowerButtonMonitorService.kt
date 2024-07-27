package com.shubham.emergencyapplication.Services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.shubham.emergencyapplication.BroadCastReceivers.SOSReceiver
import com.shubham.emergencyapplication.BroadCastReceivers.ScreenStateReceiver
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Utils.Constants.ACTION_SOS

class PowerButtonMonitorService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
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

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager


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
        Log.d("OverlayService", " in service Received SOS message")
        showOverlay()
    }


    private fun showOverlay() {
        if (!this::windowManager.isInitialized) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        }

        val themedContext = ContextThemeWrapper(this, R.style.Theme_EmergencyApplication)
        overlayView = LayoutInflater.from(themedContext).inflate(R.layout.success_dialog, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        windowManager.addView(overlayView, params)
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(sosReceiver)
        handler.removeCallbacksAndMessages(null)
        if (::windowManager.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}