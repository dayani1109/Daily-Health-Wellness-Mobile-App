package com.example.dailyhealthwellness.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Manager class for handling accelerometer sensor to detect steps and shake gestures
 */
class SensorManager(private val context: Context) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private var stepCount = 0
    private var lastStepTime = 0L
    private val stepThreshold = 10.0f // Minimum acceleration change to count as step
    private val stepTimeThreshold = 200L // Minimum time between steps (ms)
    
    private var shakeThreshold = 15.0f // Minimum acceleration for shake detection
    private var lastShakeTime = 0L
    private val shakeTimeThreshold = 1000L // Minimum time between shake detections (ms)
    
    private var onStepDetected: (() -> Unit)? = null
    private var onShakeDetected: (() -> Unit)? = null
    
    /**
     * Start listening for sensor events
     */
    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    /**
     * Stop listening for sensor events
     */
    fun stopListening() {
        sensorManager.unregisterListener(this)
    }
    
    /**
     * Set callback for step detection
     */
    fun setOnStepDetectedListener(callback: () -> Unit) {
        onStepDetected = callback
    }
    
    /**
     * Set callback for shake detection
     */
    fun setOnShakeDetectedListener(callback: () -> Unit) {
        onShakeDetected = callback
    }
    
    /**
     * Get current step count
     */
    fun getStepCount(): Int = stepCount
    
    /**
     * Reset step count
     */
    fun resetStepCount() {
        stepCount = 0
    }
    
    /**
     * Add steps manually
     */
    fun addSteps(steps: Int) {
        stepCount += steps
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = sensorEvent.values[0]
                val y = sensorEvent.values[1]
                val z = sensorEvent.values[2]
                
                // Calculate acceleration magnitude
                val acceleration = sqrt(x * x + y * y + z * z)
                
                // Detect steps
                detectSteps(acceleration)
                
                // Detect shake
                detectShake(acceleration)
            }
        }
    }
    
    /**
     * Detect steps based on acceleration changes
     */
    private fun detectSteps(acceleration: Float) {
        val currentTime = System.currentTimeMillis()
        
        if (acceleration > stepThreshold && 
            currentTime - lastStepTime > stepTimeThreshold) {
            stepCount++
            lastStepTime = currentTime
            onStepDetected?.invoke()
        }
    }
    
    /**
     * Detect shake gesture
     */
    private fun detectShake(acceleration: Float) {
        val currentTime = System.currentTimeMillis()
        
        if (acceleration > shakeThreshold && 
            currentTime - lastShakeTime > shakeTimeThreshold) {
            lastShakeTime = currentTime
            onShakeDetected?.invoke()
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
    
    /**
     * Check if accelerometer is available
     */
    fun isAccelerometerAvailable(): Boolean {
        return accelerometer != null
    }
    
    /**
     * Get sensor information
     */
    fun getSensorInfo(): String {
        return accelerometer?.let {
            "Sensor: ${it.name}\nVendor: ${it.vendor}\nVersion: ${it.version}\nMax Range: ${it.maximumRange}"
        } ?: "Accelerometer not available"
    }
}
