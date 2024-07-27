package com.shubham.emergencyapplication.Services

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.shubham.emergencyapplication.Ui.Activities.DashboardActivity
import com.shubham.emergencyapplication.Utils.Constants.ACCELERATION_TRESOLD
import com.shubham.emergencyapplication.Utils.Constants.GYROSCOPE_THRESOLD
import kotlin.math.pow
import kotlin.math.sqrt

class CrashDetectionManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var lastAccelerometer: FloatArray? = null
    private var lastGyroscope: FloatArray? = null

    init {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                lastAccelerometer = event.values.copyOf()
                checkCrashDetection()
            }
            Sensor.TYPE_GYROSCOPE -> {
                lastGyroscope = event.values.copyOf()
                checkCrashDetection()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun checkCrashDetection() {
        val accelerometerValues = lastAccelerometer ?: return
        val accelerationMagnitude = sqrt(
            accelerometerValues[0].toDouble().pow(2.0) +
                    accelerometerValues[1].toDouble().pow(2.0) +
                    accelerometerValues[2].toDouble().pow(2.0)
        )

        if (gyroscope != null) {
            val gyroscopeValues = lastGyroscope ?: return
            val gyroscopeMagnitude = sqrt(
                gyroscopeValues[0].toDouble().pow(2.0) +
                        gyroscopeValues[1].toDouble().pow(2.0) +
                        gyroscopeValues[2].toDouble().pow(2.0)
            )

            Log.d("CrashDetection", "Accelerometer: $accelerationMagnitude, Gyroscope: $gyroscopeMagnitude")

            // Thresholds
            val accelerationThreshold = ACCELERATION_TRESOLD  // m/s²
            val gyroscopeThreshold = GYROSCOPE_THRESOLD       // rad/s

            if (accelerationMagnitude > accelerationThreshold ) {
                // Detect crash
                Log.d("CrashDetectiond", "Crash detected with both sensors!")
                sendBroadcast()
            }
        } else {
            // If gyroscope is not available, use only accelerometer data
            val accelerationThreshold = ACCELERATION_TRESOLD

            if (accelerationMagnitude > accelerationThreshold) {
                // Detect crash using only accelerometer
                Log.d("CrashDetectiond", "Crash detected with accelerometer!")
                sendBroadcast()
            }
        }
    }

    private fun sendBroadcast() {
        val intent = Intent(DashboardActivity.ACTION_CRASH_DETECTED).apply {
            // Optionally add extra data to the intent
            // putExtra("location", "some_location_data")
        }
        context.sendBroadcast(intent)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }
}
