package com.example.dailyhealthwellness.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dailyhealthwellness.R

class HydrationReminderWorker(// use work manager
    context: Context,// app context
    workerParams: WorkerParameters// work managert avashsha internal data
) : Worker(context, workerParams) {// worker -> workmanager base class

    override fun doWork(): Result {// work manager run karana time ekt execute venva
        val channelId = "hydration_channel"// notification build
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager// android notification manager service ek gannva. meka notification ek build karala system eke penvanv a

        // Create channel for Android 8+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_water) // replace with your drawable
            .setContentTitle("Hydration Reminder 💧")
            .setContentText("Time to drink water!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
        return Result.success()// return karanva result eka success kiyala
    }
}
