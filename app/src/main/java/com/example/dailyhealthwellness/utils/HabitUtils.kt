package com.example.dailyhealthwellness.utils

import android.content.Context
import com.example.dailyhealthwellness.ui.widgets.HabitWidget

object HabitUtils {//kotlin singleton class

    fun updateHabitProgress(context: Context, total: Int, completed: Int) {//userge habit prohress update karala widget refresh karanva
        val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)//use habit shared preference file
        prefs.edit()
            .putInt("total_tasks", total)
            .putInt("completed_tasks", completed)
            .apply()

        // Force widget update immediately and refresh
        HabitWidget.updateWidget(context)
    }
}
