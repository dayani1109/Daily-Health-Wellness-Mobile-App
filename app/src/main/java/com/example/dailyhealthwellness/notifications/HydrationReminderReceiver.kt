package com.example.dailyhealthwellness.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.dailyhealthwellness.R

class HydrationReminderReceiver : BroadcastReceiver() {// inherit class->BroadcastReceiver -  use water drinking reminder notification
    override fun onReceive(context: Context, intent: Intent) {//function call system, Reminder trigger veddi weda karanva- notification create
        val channelId = "hydration_channel"// notification channel name
        val notificationManager =//notification manage karana service ek
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager//notification ek control karann system service ek laba gannva

        // Create notification channel (required for Android 8+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",// crete channel using this name
                NotificationManager.IMPORTANCE_HIGH//notification importance HIGH. notification ek prominent vidiyt display karanva
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)// notification build part
            .setSmallIcon(R.drawable.ic_water) // replace with your drawable
            .setContentTitle("Hydration Reminder 💧")
            .setContentText("Time to drink water!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)//notification ek tap karam remove venva
            .build()

        notificationManager.notify(1, notification)//notification screen ekt display karnva
    }
}