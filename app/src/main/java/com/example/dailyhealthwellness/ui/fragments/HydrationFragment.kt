package com.example.dailyhealthwellness.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.work.*
import com.example.dailyhealthwellness.R
import com.example.dailyhealthwellness.notifications.HydrationReminderWorker
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HydrationFragment : Fragment() {

    private lateinit var prefs: SharedPreferences//ser hydration data save & load
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    private lateinit var chart: LineChart
    private lateinit var tvSelectedDateTime: TextView
//    private lateinit var btnDeleteReminder: Button

    private var reminderCalendar: Calendar = Calendar.getInstance()//reminder time ek store karagannva

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("HydrationPrefs", 0)//SharedPreferences init karanva

        val etWaterAmount = view.findViewById<EditText>(R.id.etWaterAmount)
        val btnLogWater = view.findViewById<Button>(R.id.btnLogWater)
        val btnPickDateTime = view.findViewById<Button>(R.id.btnPickDateTime)
        val btnSetReminder = view.findViewById<Button>(R.id.btnSetReminder)
        tvSelectedDateTime = view.findViewById(R.id.tvSelectedDateTime)
        chart = view.findViewById(R.id.waterLineChart)

//        // Create Delete button programmatically
//        btnDeleteReminder = Button(requireContext()).apply {
//            text = "Delete Reminder"
//            setBackgroundColor(resources.getColor(R.color.MainGreen, null))
//        }
//
//        // Add Delete button below Set Reminder button
//        (btnSetReminder.parent as ViewGroup).addView(btnDeleteReminder)

        // Back button
        view.findViewById<ImageView>(R.id.hydration_back_arrow).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Load saved reminder if exists
        loadSavedReminder()

        // Pick reminder date & time
        btnPickDateTime.setOnClickListener { pickReminderDateTime() }

        // Set reminder
        btnSetReminder.setOnClickListener {
            val timeStr = dateTimeFormat.format(reminderCalendar.time)
            prefs.edit().putString("reminder_time", timeStr).apply()
            scheduleHydrationReminder(reminderCalendar)
            tvSelectedDateTime.text = "Reminder set at: $timeStr"
            Toast.makeText(requireContext(), "Reminder set at $timeStr", Toast.LENGTH_SHORT).show()
        }

//        // Delete reminder
//        btnDeleteReminder.setOnClickListener {
//            cancelReminder()
//            prefs.edit().remove("reminder_time").apply()
//            tvSelectedDateTime.text = "No reminder set"
//            Toast.makeText(requireContext(), "Reminder deleted", Toast.LENGTH_SHORT).show()
//        }

        // Log water intake
        btnLogWater.setOnClickListener {
            val amountText = etWaterAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toInt()
                addWaterIntake(amount)
                etWaterAmount.text.clear()
                Toast.makeText(requireContext(), "Logged $amount ml", Toast.LENGTH_SHORT).show()
                updateChart()
            }
        }

        // Show chart when fragment opens
        updateChart()
    }

    private fun loadSavedReminder() {//user set karapu reminder save vela thiyenm load karanva nathnm no reminder
        val savedTime = prefs.getString("reminder_time", null)
        if (!savedTime.isNullOrEmpty()) {
            val date = dateTimeFormat.parse(savedTime)
            if (date != null) {
                reminderCalendar.time = date
                tvSelectedDateTime.text = "Reminder set at: $savedTime"
            }
        } else {
            tvSelectedDateTime.text = "No reminder set"
        }
    }

    //Date + Time picker dialog ekk open venva.
    // User select kala date & time reminderCalendar object ekt save venva.
    //TextView  update
    private fun pickReminderDateTime() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                reminderCalendar.set(year, month, dayOfMonth, hour, minute)
                tvSelectedDateTime.text = dateTimeFormat.format(reminderCalendar.time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun scheduleHydrationReminder(calendar: Calendar) {//reminder ek set karanva
        val now = Calendar.getInstance()
        var delay = calendar.timeInMillis - now.timeInMillis
        if (delay < 0) delay += TimeUnit.DAYS.toMillis(1) // schedule next day if time passed

        val workRequest = OneTimeWorkRequestBuilder<HydrationReminderWorker>()//work manager user karala notification trigger karanva
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "hydration_reminder",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelReminder() {//cancel remind
        WorkManager.getInstance(requireContext()).cancelUniqueWork("hydration_reminder")
    }

    private fun addWaterIntake(amount: Int) {
        val today = dateFormat.format(Date())
        val current = prefs.getInt(today, 0)//user input karana amount ek shared preferense vala save karanva
        prefs.edit().putInt(today, current + amount).apply()
    }

    //Last 7 days water intake collect karala chart ekt use karanva
    private fun getLast7Days(): Map<String, Int> {
        val result = LinkedHashMap<String, Int>()
        val calendar = Calendar.getInstance()
        for (i in 6 downTo 0) {
            val date = dateFormat.format(calendar.time)
            result[date] = prefs.getInt(date, 0)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return result
    }

//    Last 7 days data chart ekt show karan function ek
//    Line chart use  (MPAndroidChart library).
//    X-axis = date, Y-axis = water intake in ml.
    private fun updateChart() {
        val data = getLast7Days()
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        var index = 0f
        data.entries.reversed().forEach {
            entries.add(Entry(index, it.value.toFloat()))
            labels.add(it.key.substring(5))
            index++
        }

        val dataSet = LineDataSet(entries, "Water Intake (ml)")
        dataSet.color = resources.getColor(R.color.teal_700, null)
        dataSet.setCircleColor(resources.getColor(R.color.MainGreen, null))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
        }
        chart.axisLeft.axisMinimum = 0f
        chart.invalidate()
    }
}
