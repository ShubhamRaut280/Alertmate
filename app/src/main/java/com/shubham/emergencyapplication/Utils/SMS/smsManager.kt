package com.shubham.emergencyapplication.Utils.SMS

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object smsManager {

    // Function to send SMS
    fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val sentIntent = Intent("SMS_SENT")
            val sentPendingIntent = PendingIntent.getBroadcast(context, 0, sentIntent,
                PendingIntent.FLAG_IMMUTABLE)

            val deliveryIntent = Intent("SMS_DELIVERED")
            val deliveryPendingIntent = PendingIntent.getBroadcast(context, 0, deliveryIntent,
                PendingIntent.FLAG_IMMUTABLE)

            // Check if message needs to be split into multiple parts
            if (message.length > 160) {
                val parts = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
                Log.d("sms", "Multipart message sent")
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, sentPendingIntent, deliveryPendingIntent)
                Log.d("sms", "Single message sent")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("sms", "Failed to send the SMS: ${e.message}")
        }
    }

    fun generateMessage(lat: Double, lng: Double, sender: String, receiver: String): String {
        // URL-encode latitude and longitude to handle special characters
        val encodedLat = URLEncoder.encode(lat.toString(), StandardCharsets.UTF_8.toString())
        val encodedLng = URLEncoder.encode(lng.toString(), StandardCharsets.UTF_8.toString())

        return "Hello $receiver, $sender is in an emergency situation. Here is their location: https://www.google.com/maps/search/?api=1&query=$encodedLat%2C$encodedLng\n" +
                "Please take care. Thank you!"
    }
}
