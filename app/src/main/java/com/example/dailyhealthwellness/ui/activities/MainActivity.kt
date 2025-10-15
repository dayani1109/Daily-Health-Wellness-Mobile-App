package com.example.dailyhealthwellness.ui.activities

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dailyhealthwellness.R

class MainActivity : AppCompatActivity() {

    private val REQUEST_NOTIF = 1001//permission request track id
    private val SPLASH_DELAY = 3000L // 3 seconds. L mean long data type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply theme
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)//get user preferences
        when (prefs.getString("theme_preference", "light")) {//default light
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Notification channel and permission
        createNotificationChannel()// notification chanel create part
        requestNotificationPermissionIfNeeded()

        // Handle insets (status/nav bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Splash screen → perDailyHabit activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, perDailyHabit::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "hydration_channel",//chanel id
                "Hydration Reminders",
                android.app.NotificationManager.IMPORTANCE_HIGH//notification ek sound and popup eka samaga penvanava
            ).apply {
                description = "Channel for hydration reminder notifications"
            }
            val manager = getSystemService(android.app.NotificationManager::class.java)
            manager.createNotificationChannel(channel)//os ekt channel ek register karanva
        }
    }

    private fun requestNotificationPermissionIfNeeded() {//check user permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIF
                )
            }
        }
    }
}
