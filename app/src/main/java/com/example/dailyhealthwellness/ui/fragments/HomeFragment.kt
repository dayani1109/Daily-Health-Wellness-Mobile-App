package com.example.dailyhealthwellness

import android.os.Bundle
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dailyhealthwellness.ui.fragments.DailyHabitTaskFragment
import com.example.dailyhealthwellness.ui.fragments.MainMoodJournalFragment
import com.example.dailyhealthwellness.ui.fragments.HydrationFragment
import com.example.dailyhealthwellness.ui.fragments.MeditationFragment
import com.example.dailyhealthwellness.ui.fragments.ProfileFragment
import com.example.dailyhealthwellness.ui.fragments.SettingsFragment
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // ---------------- Week Dates display----------------
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.SUNDAY//first day sunday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val dayViews = listOf<TextView>(
            view.findViewById(R.id.tv_day1),
            view.findViewById(R.id.tv_day2),
            view.findViewById(R.id.tv_day3),
            view.findViewById(R.id.tv_day4),
            view.findViewById(R.id.tv_day5),
            view.findViewById(R.id.tv_day6),
            view.findViewById(R.id.tv_day7)
        )

        val navHome = view.findViewById<LinearLayout>(R.id.nav_home)
        val navSettings = view.findViewById<LinearLayout>(R.id.nav_settings)
        val navProfile = view.findViewById<LinearLayout>(R.id.nav_profile)

        for (i in 0 until 7) {
            val date = calendar.time
            dayViews[i].text = "${dayFormat.format(date)}\n${dateFormat.format(date)}"//text view ekt day + date format set karanva

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            ) {
                dayViews[i].setBackgroundResource(R.drawable.bg_today_circle)//today highlight karala circle karanva
                dayViews[i].setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            } else {
                dayViews[i].setBackgroundResource(0)// otherwise normal
                dayViews[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // ---------------- Card Clicks ----------------

        // Daily Habit
        view.findViewById<CardView>(R.id.cv_daily_habit).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, DailyHabitTaskFragment())
                .addToBackStack(null)
                .commit()
        }

        // Mood Journal
        view.findViewById<CardView>(R.id.cv_mood_journal).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, MainMoodJournalFragment())
                .addToBackStack(null)
                .commit()
        }

        // Hydrating -> HydrationFragment
        view.findViewById<CardView>(R.id.cv_hydrating).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, HydrationFragment())
                .addToBackStack(null)
                .commit()
        }

        // Meditation (optional)
        view.findViewById<CardView>(R.id.cv_meditation).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, MeditationFragment())
                .addToBackStack(null)
                .commit()
        }

        navHome.setOnClickListener {
            // Already on Home, maybe show a Toast or refresh
            // Or you can replace fragment if hosted in main container
        }

        navSettings.setOnClickListener {
            // Replace container with SettingsFragment
            val settingsFragment = SettingsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, settingsFragment)
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

        return view
    }
}
