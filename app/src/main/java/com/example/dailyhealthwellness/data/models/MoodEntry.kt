package com.example.dailyhealthwellness.data.models

import android.graphics.Color
import java.io.Serializable

data class MoodEntry(
    val emoji: String,
    val moodName: String,   // you can use feeling here
    val notes: String,
    val date: java.util.Date,
    val intensity: Int // 1 to 10
) : Serializable { //object එක serialize කරලා (convert කරලා) save කරන්න හෝ intent එකකින් වෙන Activity එකකට pass කරන්න පුළුවන් කියලා
    fun getIntensityDescription(): String {
        return when (intensity) {
            in 1..3 -> "Low"
            in 4..7 -> "Medium"
            in 8..10 -> "High"
            else -> "Medium"
        }
    }

    fun getMoodColor(): Int {
        return when (moodName.lowercase()) {
            "happy" -> Color.parseColor("#FFD700") // Gold
            "sad" -> Color.parseColor("#1E90FF")   // DodgerBlue
            "angry" -> Color.parseColor("#FF4500") // OrangeRed
            "neutral" -> Color.parseColor("#808080") // Gray
            "excited" -> Color.parseColor("#FF69B4") // HotPink
            else -> Color.parseColor("#90EE90") // LightGreen
        }
    }
}
