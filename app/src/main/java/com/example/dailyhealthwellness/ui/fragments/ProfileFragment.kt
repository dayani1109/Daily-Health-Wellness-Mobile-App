package com.example.dailyhealthwellness.ui.fragments

import android.app.AlertDialog//popup
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.dailyhealthwellness.HomeFragment
import com.example.dailyhealthwellness.R
import com.example.dailyhealthwellness.ui.activities.Login

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvHabitsCompleted: TextView
    private lateinit var tvSuccessRate: TextView
    private lateinit var tvStreak: TextView
    private lateinit var btnLogout: Button
    private lateinit var backArrow: ImageView
    private lateinit var btnCompleteHabit: Button

    private val userPrefsName = "user_prefs"//shared preference eke data save karala thiyenne me name ekem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        imgProfile = view.findViewById(R.id.imgProfile)
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvHabitsCompleted = view.findViewById(R.id.tvHabitsCompleted)
        tvSuccessRate = view.findViewById(R.id.tvSuccessRate)
        tvStreak = view.findViewById(R.id.tvStreak)
        btnLogout = view.findViewById(R.id.btnLogout)
        backArrow = view.findViewById(R.id.profile_back)
        btnCompleteHabit = view.findViewById(R.id.btnCompleteHabit)

        val navHome = view.findViewById<LinearLayout>(R.id.nav_home)
        val navSettings = view.findViewById<LinearLayout>(R.id.nav_settings)

        // Load saved user data(name, email)- shared preference eke save karala thibunu
        val prefs = requireContext().getSharedPreferences(userPrefsName, Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "John Doe")
        val email = prefs.getString("user_email", "john@example.com")

        tvName.text = name
        tvEmail.text = email

        // Load progress stats
        loadProgress()

        // Edit username dialog
        tvName.setOnClickListener {//name ek click kalam popup ekk enva
            val editText = EditText(requireContext())
            editText.setText(tvName.text.toString())
            AlertDialog.Builder(requireContext())//usert new name ekk type karala save karann puluvam
                .setTitle("Edit Name")
                .setView(editText)
                .setPositiveButton("Save") { _, _ ->
                    val newName = editText.text.toString().trim()
                    if (newName.isNotEmpty()) {
                        tvName.text = newName
                        prefs.edit().putString("user_name", newName).apply()//shared preference valath update venva
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Complete Habit button → updates stats
        btnCompleteHabit.setOnClickListener {
            incrementStreak()
            updateProgress()
        }

        // Logout, shared preference data clear venva, redirect login, clear screen histroy
        btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // Back arrow → go HomeFragment
        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, HomeFragment())
                .commit()
        }

        // Bottom nav
        navHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        navSettings.setOnClickListener {
            val settingsFragment = SettingsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, settingsFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    /** Load statistics from SharedPreferences */
    private fun loadProgress() {
        val prefs = requireContext().getSharedPreferences(userPrefsName, Context.MODE_PRIVATE)
        val completed = prefs.getInt("completed_count", 0)
        val successRate = prefs.getInt("success_rate", 0)
        val streak = prefs.getInt("streak_days", 0)

        tvHabitsCompleted.text = "Habits Completed: $completed"
        tvSuccessRate.text = "Success Rate: $successRate%"
        tvStreak.text = "Current Streak: $streak days"
    }

    /** Update habits completed and success rate */
    private fun updateProgress() {
        val prefs = requireContext().getSharedPreferences(userPrefsName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val totalHabits = prefs.getInt("total_habits", 5) // default total habits
        val completed = prefs.getInt("completed_count", 0) + 1// habit complete kaloth total ekt 1k add venva
        val successRate = (completed * 100) / totalHabits// success rate ek precentage ekk vidiyt update venva

        editor.putInt("completed_count", completed)
        editor.putInt("success_rate", successRate)
        editor.apply()//data shared preference ekt save venva

        tvHabitsCompleted.text = "Habits Completed: $completed"//new values ui eke save venva
        tvSuccessRate.text = "Success Rate: $successRate%"
    }

    /** Update streak days */
    private fun incrementStreak() {
        val prefs = requireContext().getSharedPreferences(userPrefsName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val lastDate = prefs.getLong("last_habit_date", 0L)
        val today = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000

        var streak = prefs.getInt("streak_days", 0)
        streak = if (today - lastDate <= oneDay + 10000) {
            streak + 1
        } else {
            1
        }

        editor.putInt("streak_days", streak)
        editor.putLong("last_habit_date", today)
        editor.apply()

        tvStreak.text = "Current Streak: $streak days"
    }
}
