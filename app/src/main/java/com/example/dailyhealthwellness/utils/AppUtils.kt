package com.example.dailyhealthwellness.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for common app functions
 */
object AppUtils {
    
    /**
     * Format date to readable string
     */
    fun formatDate(date: Date, pattern: String = "MMM dd, yyyy"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Format time to readable string
     */
    fun formatTime(date: Date, pattern: String = "HH:mm"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Get current date as string
     */
    fun getCurrentDateString(): String {
        return formatDate(Date())
    }
    
    /**
     * Get current time as string
     */
    fun getCurrentTimeString(): String {
        return formatTime(Date())
    }
    
    /**
     * Check if date is today
     */
    fun isToday(date: Date): Boolean {
        val today = Date()
        return date.date == today.date && 
               date.month == today.month && 
               date.year == today.year
    }
    
    /**
     * Check if date is yesterday
     */
    fun isYesterday(date: Date): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return date.date == yesterday.get(Calendar.DAY_OF_MONTH) && 
               date.month == yesterday.get(Calendar.MONTH) && 
               date.year == yesterday.get(Calendar.YEAR)
    }
    
    /**
     * Generate unique ID
     */
    fun generateId(): String {
        return UUID.randomUUID().toString()
    }
    
    /**
     * Show toast message
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    
    /**
     * Share text content
     */
    fun shareText(context: Context, text: String, title: String = "Share") {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, title))
    }
    
    /**
     * Open URL in browser
     */
    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast(context, "Cannot open URL: $url")
        }
    }
    
    /**
     * Send email
     */
    fun sendEmail(context: Context, email: String, subject: String = "", body: String = "") {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast(context, "Cannot open email client")
        }
    }
    
    /**
     * Convert dp to pixels
     */
    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
    
    /**
     * Convert pixels to dp
     */
    fun pxToDp(context: Context, px: Int): Int {
        val density = context.resources.displayMetrics.density
        return (px / density).toInt()
    }
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Get emoji by name
     */
    fun getEmojiByName(name: String): String {
        return when (name.lowercase()) {
            "happy", "joy" -> "😊"
            "sad", "depressed" -> "😢"
            "angry", "mad" -> "😠"
            "anxious", "worried" -> "😰"
            "calm", "peaceful" -> "😌"
            "tired", "exhausted" -> "😴"
            "excited" -> "🤩"
            "confused" -> "😕"
            "love" -> "😍"
            "surprised" -> "😮"
            "water" -> "💧"
            "meditation" -> "🧘"
            "exercise" -> "🏃"
            "sleep" -> "😴"
            "food" -> "🍎"
            else -> "😐"
        }
    }
    
    /**
     * Get mood emojis list
     */
    fun getMoodEmojis(): List<String> {
        return listOf("😊", "😢", "😠", "😰", "😌", "😴", "🤩", "😕", "😍", "😮", "😐")
    }
    
    /**
     * Get habit emojis list
     */
    fun getHabitEmojis(): List<String> {
        return listOf("💧", "🧘", "🏃", "😴", "🍎", "📚", "🎵", "🌱", "💪", "🧠", "❤️", "🎯")
    }
}
