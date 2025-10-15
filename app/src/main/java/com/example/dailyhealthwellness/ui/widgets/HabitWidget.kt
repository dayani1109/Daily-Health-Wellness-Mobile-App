package com.example.dailyhealthwellness.ui.widgets

import android.appwidget.AppWidgetManager//handle widget create update delete
import android.appwidget.AppWidgetProvider//widgets manage
import android.content.ComponentName//specific component
import android.content.Context
import android.widget.RemoteViews//widgets layout update
import com.example.dailyhealthwellness.R

class HabitWidget : AppWidgetProvider() {//inherit AppWidgetProvider

    override fun onUpdate(//widget ek update venkot automatically call venva
        context: Context,
        appWidgetManager: AppWidgetManager,//manage widget
        appWidgetIds: IntArray//update  widget id list
    ) {
        updateWidget(context, appWidgetManager)// widget update karann call karanva
    }

    companion object {//static block ekak, class ekt object ekk create krala nathnm me methods call karann puluvam

        // Call this from anywhere to update widget
        fun updateWidget(context: Context, manager: AppWidgetManager? = null) {
            val prefs = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
            val totalTasks = prefs.getInt("total_tasks", 1)
            val completedTasks = prefs.getInt("completed_tasks", 0)
            val progressPercent = if (totalTasks == 0) 0 else (completedTasks * 100 / totalTasks)//percentage formula, totalTasks == 0 nm divide by zero error eka remove karanna 0 assign

            val views = RemoteViews(context.packageName, R.layout.widget_habit)
            views.setProgressBar(R.id.widgetProgress, 100, progressPercent, false)
            views.setTextViewText(R.id.widgetPercentage, "$progressPercent%")
            views.setTextViewText(R.id.widgetTitle, "Today's Habit Progress")
            views.setTextViewText(R.id.widgetProgressText, "$completedTasks/$totalTasks habits")

            val appWidgetManager = manager ?: AppWidgetManager.getInstance(context)
            val widget = ComponentName(context, HabitWidget::class.java)
            val ids = appWidgetManager.getAppWidgetIds(widget)
            for (id in ids) {
                appWidgetManager.updateAppWidget(id, views)
            }
        }
    }
}
