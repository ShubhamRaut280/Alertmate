package com.shubham.emergencyapplication.Utils

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date

object DateUtils {
    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS")
        return sdf.format(Date())
    }

    fun greetBasedOnTime(): String {
        val currentTime = LocalTime.now()
        return when {
            currentTime.isBefore(LocalTime.NOON) -> "Good Morning"
            currentTime.isBefore(LocalTime.of(17, 0)) -> "Good Afternoon"
            currentTime.isBefore(LocalTime.of(20, 0)) -> "Good Evening"
            else -> "Good Night"
        }
    }
}