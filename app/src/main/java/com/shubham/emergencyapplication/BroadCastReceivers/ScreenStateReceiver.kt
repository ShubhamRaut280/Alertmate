package com.shubham.emergencyapplication.BroadCastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

abstract class ScreenStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF || intent.action == Intent.ACTION_SCREEN_ON) {
            onScreenStateChanged()
        }
    }

    abstract fun onScreenStateChanged()
}
