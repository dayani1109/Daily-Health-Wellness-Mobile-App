package com.example.dailyhealthwellness.ui.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dailyhealthwellness.HomeFragment
import com.example.dailyhealthwellness.R
import com.example.dailyhealthwellness.data.models.Task
import com.example.dailyhealthwellness.ui.adapters.TaskAdapter
import com.example.dailyhealthwellness.ui.activities.AddTaskActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class DailyHabitTaskFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView//display task list
    private lateinit var adapter: TaskAdapter//recycle view ekt data connect karanva
    private lateinit var tvSelectedDate: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressPercent: TextView

    private val taskList = mutableListOf<Task>()//all task store vena list ek
    private var selectedDate: String = getTodayDate()//default today date
    private val prefsKey = "DailyTaskPrefs"//shared preference key ek
    private val gson = Gson()// object convert json

    // Activity Result API, launcher
    private val addTaskLauncher =//add task screen ek open karala ethanin ena result ek catch karagannva
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val taskName = result.data?.getStringExtra("taskName") ?: return@registerForActivityResult
                val taskDate = result.data?.getStringExtra("taskDate") ?: selectedDate

                val newTask = Task(taskName, taskDate)
                taskList.add(newTask)//new task ek task list ekt add venva
                saveTasks()//save

                adapter.updateDate(selectedDate)//update adapter
                updateProgress()//update progress
                Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //xml layout load
        val view = inflater.inflate(R.layout.fragment_daily_habit_task, container, false)

        recyclerView = view.findViewById(R.id.rvTasks)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        progressBar = view.findViewById(R.id.progressBar)
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent)
        val btnAdd: ImageView = view.findViewById(R.id.btnAddTask)
        val btnPickDate: ImageButton = view.findViewById(R.id.btnPickDate)

        //navigation bar
        val navHome = view.findViewById<LinearLayout>(R.id.nav_home)
        val navSettings = view.findViewById<LinearLayout>(R.id.nav_settings)
        val navProfile = view.findViewById<LinearLayout>(R.id.nav_profile)

        navHome.setOnClickListener {
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, homeFragment)
                .addToBackStack(null) // optional, so user can press back
                .commit()
        }
        navProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, profileFragment)
                .addToBackStack(null) // optional, so user can press back
                .commit()
        }

        navSettings.setOnClickListener {
            // Replace container with SettingsFragment
            val settingsFragment = SettingsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, settingsFragment)
                .addToBackStack(null) // optional, so user can press back
                .commit()
        }

        tvSelectedDate.text = selectedDate

        loadTasks() // shared preference eke save task list ek load karanva

        val backArrow: ImageView = view.findViewById(R.id.dailyTask_arrow)
        backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.homeactivity_fragment_container, HomeFragment())
                .commit()
        }



        adapter = TaskAdapter(// recycle view ekt data bind karan class ek
            requireContext(),
            taskList,
            selectedDate,
            onTaskChanged = {
                saveTasks() // ✅ Save when task is completed/updated
                updateProgress()
            },
            onDeleteClick = { task ->
                taskList.remove(task)
                saveTasks() // ✅ Save deletion
                adapter.notifyDataSetChanged()
                updateProgress()
                Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            }
        )


        // list eka vertically scroll ven vidiyt set karanva
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {//addtask activity open
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            addTaskLauncher.launch(intent)
        }

        btnPickDate.setOnClickListener { pickDate() }//open date picker, call pickdate function

        updateProgress()
        return view
    }

    private fun pickDate() {//calander ekem usert date select karann dialog ekk open venva
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                tvSelectedDate.text = selectedDate
                adapter.updateDate(selectedDate)// selected date update venva
                updateProgress()//ui and progress ek update venva
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateProgress() {
        val progress = adapter.getProgress()//adapter ek progress precentage ek gannva
        progressBar.progress = progress
        tvProgressPercent.text = "$progress%"

        // 🔑 Calculate total & completed tasks
        val totalTasks = taskList.size
        val completedTasks = taskList.count { it.isCompleted }

        // 🔑 Update widget immediately
        com.example.dailyhealthwellness.utils.HabitUtils.updateHabitProgress(
            requireContext(),
            totalTasks,
            completedTasks
        )
    }


    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // ✅ SharedPreferences: Save task list
    private fun saveTasks() {//ada date ek string vidiyt format karala return karanva
        val prefs = requireContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
        val json = gson.toJson(taskList)
        prefs.edit().putString("task_list", json).apply()
    }

    // ✅ SharedPreferences: Load task list
    private fun loadTasks() {//shared preference eke task list ek load karanva
        val prefs = requireContext().getSharedPreferences(prefsKey, Context.MODE_PRIVATE)
        val json = prefs.getString("task_list", null)//tasklist json string ekk vidiyt save venva
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            taskList.clear()
            taskList.addAll(gson.fromJson(json, type))//load veddi object ekk vidiyt convert venva
        }
    }
}
