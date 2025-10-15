package com.example.dailyhealthwellness.ui.receivers

import android.content.BroadcastReceiver//broadcast message reciver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {//system broadcasts listen karanva
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

        }
    }
}
