package com.shubham.emergencyapplication.Utils.SMS

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log

object smsManager {

    fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val sentIntent = Intent("SMS_SENT")
            val sentPendingIntent = PendingIntent.getBroadcast(context, 0, sentIntent,
                PendingIntent.FLAG_IMMUTABLE)

            val deliveryIntent = Intent("SMS_DELIVERED")
            val deliveryPendingIntent = PendingIntent.getBroadcast(context, 0, deliveryIntent,
                PendingIntent.FLAG_IMMUTABLE)

            // Send SMS
            smsManager.sendTextMessage(phoneNumber, null, message, sentPendingIntent, deliveryPendingIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("sms", "Failed to send the SMS: ${e.message}")
        }
    }

    fun generateMessage(lat: Double, lng: Double, sender: String, receiver: String): String {
     return "Hello $receiver $sender is in some emergency situation. here is his location https://www.google.com/maps/search/?api=1&query=$lat,$lng\n" +
             "Take care thank you!"
    }
}
