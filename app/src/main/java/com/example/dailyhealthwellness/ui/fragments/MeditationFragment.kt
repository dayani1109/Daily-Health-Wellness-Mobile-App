package com.example.dailyhealthwellness.ui.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dailyhealthwellness.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MeditationFragment : Fragment() {

    private lateinit var backArrow: ImageView
    private lateinit var dateButton: Button
    private lateinit var timeInput: EditText
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var timerText: TextView
    private lateinit var meditationChart: BarChart

    private var countDownTimer: CountDownTimer? = null//Meditation timer handle
    private var remainingTime: Long = 0//remainingTime → timer pause/stop handle use

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val historyKey = "meditation_history"//SharedPreferences key for storing meditation minutes per day.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meditation, container, false)

        // Bind views
        backArrow = view.findViewById(R.id.meditation_back_arrow)
        dateButton = view.findViewById(R.id.btnSelectDate)
        timeInput = view.findViewById(R.id.etMeditationTime)
        startButton = view.findViewById(R.id.btnStartMeditation)
        stopButton = view.findViewById(R.id.btnStopMeditation)
        progressBar = view.findViewById(R.id.progressMeditation)
        timerText = view.findViewById(R.id.tvTimer)
        meditationChart = view.findViewById(R.id.barChartMeditation)

        // Back button
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Date picker
        dateButton.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)//Month +1 because Calendar.MONTH 0-11 use
                    dateButton.text = selectedDate
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Start button
        startButton.setOnClickListener {
            val minutes = timeInput.text.toString().toIntOrNull() ?: 0
            if (minutes > 0) startMeditation(minutes)
            else Toast.makeText(context, "Enter minutes!", Toast.LENGTH_SHORT).show()
        }

        // Stop button
        stopButton.setOnClickListener { stopMeditation() }

        setupChart()
        return view
    }

    private fun startMeditation(minutes: Int) {//countdown timer start karan function ek
        remainingTime = minutes * 60 * 1000L
        progressBar.max = (remainingTime / 1000).toInt()

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                val seconds = (millisUntilFinished / 1000).toInt()
                progressBar.progress = seconds//progressBar & timerText update every second
                timerText.text = formatTime(seconds)
            }

            override fun onFinish() {//Finish → bell sound play & progress save & chart update.
                if (!isAdded) return  // prevent crash if fragment closed
                timerText.text = "Done!"
                playBellSound()
                saveProgress(minutes)
                setupChart()
            }
        }.start()
    }

    private fun stopMeditation() {//timer stop
        countDownTimer?.cancel()
        timerText.text = "Stopped"
    }

    private fun formatTime(seconds: Int): String {//Seconds → mm:ss format
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }

    private fun playBellSound() {
        val ctx = context ?: return  // prevent crash if fragment is detached
        val mediaPlayer = MediaPlayer.create(ctx, R.raw.meditation_bell)

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener {
                it.release()
            }
            mediaPlayer.start()
        } else {
            Toast.makeText(ctx, "Bell sound not found!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveProgress(minutes: Int) {//Meditation minutes per date save
        val prefs = requireContext().getSharedPreferences("MeditationPrefs", Context.MODE_PRIVATE)
        val history = HashMap<String, Int>()

        // Load old data
        val saved = prefs.getString(historyKey, "") ?: ""
        if (saved.isNotEmpty()) {
            saved.split(";").forEach {
                val parts = it.split(":")
                if (parts.size == 2) history[parts[0]] = parts[1].toInt()
            }
        }

        val today = dateButton.text.toString().ifEmpty { sdf.format(Date()) }
        history[today] = history.getOrDefault(today, 0) + minutes

        // Save back
        val newData = history.entries.joinToString(";") { "${it.key}:${it.value}" }
        prefs.edit().putString(historyKey, newData).apply()
    }

//    Last 7 days meditation minutes show bar chart.
//    X-axis = day name, Y-axis = minutes.
    private fun setupChart() {
        val prefs = requireContext().getSharedPreferences("MeditationPrefs", Context.MODE_PRIVATE)
        val history = HashMap<String, Int>()

        val saved = prefs.getString(historyKey, "") ?: ""
        if (saved.isNotEmpty()) {
            saved.split(";").forEach {
                val parts = it.split(":")
                if (parts.size == 2) history[parts[0]] = parts[1].toInt()
            }
        }

        // Last 7 days
        val cal = Calendar.getInstance()
        val entries = ArrayList<BarEntry>()
        val days = ArrayList<String>()

        for (i in 6 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = sdf.format(cal.time)
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)

            days.add(dayName)
            entries.add(BarEntry((6 - i).toFloat(), history[dateStr]?.toFloat() ?: 0f))
        }

        val dataSet = BarDataSet(entries, "Minutes")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.teal_700)
        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        meditationChart.data = barData
        val xAxis = meditationChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(days)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        meditationChart.axisLeft.axisMinimum = 0f
        meditationChart.axisRight.isEnabled = false
        meditationChart.description.isEnabled = false
        meditationChart.setFitBars(true)
        meditationChart.invalidate()
    }

    override fun onDestroyView() {//fragment destroy unam timer cancel
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}
