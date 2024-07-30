package com.shubham.emergencyapplication.Services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.Response
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.shubham.emergencyapplication.BroadCastReceivers.SOSReceiver
import com.shubham.emergencyapplication.BroadCastReceivers.ScreenStateReceiver
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.FamilyLocation
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.UserRepository.getFamilyMembers
import com.shubham.emergencyapplication.Repositories.UserRepository.getLocation
import com.shubham.emergencyapplication.Repositories.UserRepository.getLocationOnce
import com.shubham.emergencyapplication.Repositories.UserRepository.setUserInfo
import com.shubham.emergencyapplication.Utils.Constants.ACTION_CRASH_DETECTED
import com.shubham.emergencyapplication.Utils.Constants.ACTION_SOS
import com.shubham.emergencyapplication.Utils.Constants.SOS_COUNTDOWN
import com.shubham.emergencyapplication.Utils.SMS.smsManager.generateMessage
import com.shubham.emergencyapplication.Utils.SMS.smsManager.sendSms

class PowerButtonMonitorService : Service() {
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
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
        val sosIntentFilter = IntentFilter(ACTION_SOS)
        val crashIntentFilter = IntentFilter(ACTION_CRASH_DETECTED)
        if (SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sosReceiver, sosIntentFilter)
            registerReceiver(crashReceiver, crashIntentFilter)
        }else {
            registerReceiver(sosReceiver, sosIntentFilter, RECEIVER_NOT_EXPORTED)
            registerReceiver(crashReceiver, crashIntentFilter, Context.RECEIVER_EXPORTED)
        }

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
        overlayView = LayoutInflater.from(themedContext).inflate(R.layout.emergency_dialog, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        val counter: TextView = overlayView.findViewById(R.id.countDown)
        val cancel: MaterialButton = overlayView.findViewById(R.id.cancel)
        val trigger: MaterialButton = overlayView.findViewById(R.id.trigger)

        startCountdown(counter)
        cancel.setOnClickListener {
            cancelCountdown()
            removeOverlay()
        }
        trigger.setOnClickListener {
            cancelCountdown()
            triggerSOS()
            removeOverlay()
        }

        windowManager.addView(overlayView, params)
    }

    private fun removeOverlay() {
        if (::windowManager.isInitialized && ::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }

    private fun triggerSOS() {
        cancelCountdown()
        getLocationOnce(this,FirebaseAuth.getInstance().currentUser?.uid, object :ResponseCallBack<FamilyLocation>{
            override fun onSuccess(response: FamilyLocation?) {
                if (response != null) {
                    setMessageWithLocation(response.latitude, response.longitude, response.name)
                }
            }
            override fun onError(error: String?) {
                triggerSOS()
            }
        })
    }

    private fun setMessageWithLocation(lat : Double, lng : Double, name : String) {
        getFamilyMembers(this, object : ResponseCallBack<List<User>> {
            override fun onSuccess(response: List<User>?) {
                if (response != null) {
                    for (user in response) {
                        val message = generateMessage(lat, lng, name, user.name.toString())
                        Log.d("sms", "message : $message")
                        sendSms(this@PowerButtonMonitorService,user.phone.toString(), message  )
                    }
                }
            }

            override fun onError(error: String?) {
                setMessageWithLocation(lat, lng, name)
            }
        })



        setEmergenctStatus()
    }

    private fun setEmergenctStatus() {
        // setting emergency in firebase
        val map = mapOf(
            "emergency" to true
        )
        setUserInfo(this, map, object : ResponseCallBack<String> {
            override fun onSuccess(response: String?) {
//                Toast.makeText(this@PowerButtonMonitorService, response, Toast.LENGTH_SHORT).show()
                Log.d("sms", "message : $response")
            }
            override fun onError(error: String?) {
                setEmergenctStatus()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(sosReceiver)
        unregisterReceiver(crashReceiver)
        handler.removeCallbacksAndMessages(null)
        removeOverlay()
    }

    private fun startCountdown(timerTextView: TextView) {
        if (isTimerRunning) return

        // 10-second countdown with 1-second interval
        countDownTimer = object : CountDownTimer(SOS_COUNTDOWN, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerTextView.text = "${millisUntilFinished / 1000}"
                Log.d("Countdown", "Countdown: ${millisUntilFinished / 1000}")
            }

            override fun onFinish() {
                onCountdownFinished()
            }
        }.start()

        isTimerRunning = true
    }

    private fun cancelCountdown() {
        countDownTimer?.cancel()
        countDownTimer = null
        isTimerRunning = false
    }

    private fun onCountdownFinished() {
        triggerSOS()
        removeOverlay()
    }

    // for detected crashes
    private val crashReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action == ACTION_CRASH_DETECTED) {
                // Handle the crash detected event
                Toast.makeText(context, "Crash detected!", Toast.LENGTH_LONG).show()
                cancelCountdown()
                sendSOSTrigger()

            }
        }
    }


}
