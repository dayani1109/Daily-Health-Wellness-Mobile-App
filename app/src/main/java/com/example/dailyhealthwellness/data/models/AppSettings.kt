package com.example.dailyhealthwellness.data.models

/**
 * Data class representing app settings
 */
data class AppSettings(
    val hydrationReminderEnabled: Boolean = true,
    val hydrationIntervalMinutes: Int = 60,
    val habitReminderEnabled: Boolean = true,
    val habitReminderTime: String = "09:00",
    val darkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val dailyGoalWaterGlasses: Int = 8,
    val stepGoal: Int = 10000,
    val meditationGoalMinutes: Int = 10
) {
    /**
     * Get hydration interval in milliseconds
     */
    fun getHydrationIntervalMillis(): Long {
        return hydrationIntervalMinutes * 60 * 1000L
    }
    
    /**
     * Validate settings
     */
    fun isValid(): Boolean {
        return hydrationIntervalMinutes > 0 && 
               dailyGoalWaterGlasses > 0 && 
               stepGoal > 0 && 
               meditationGoalMinutes > 0
    }
}
