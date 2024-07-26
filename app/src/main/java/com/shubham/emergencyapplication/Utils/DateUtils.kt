package com.shubham.emergencyapplication.Utils

import java.text.SimpleDateFormat
import java.util.Date

object DateUtils {
    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS")
        return sdf.format(Date())
    }
}