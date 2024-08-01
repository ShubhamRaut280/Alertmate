package com.shubham.emergencyapplication.Utils

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date

object DateUtils {
    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS")
        return sdf.format(Date())
    }

    fun greetBasedOnTime(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]

        return if (timeOfDay >= 0 && timeOfDay < 12) {
            "Good Morning"
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            "Good Afternoon"
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            "Good Evening"
        } else {
            "Hello"
        }

    }
}