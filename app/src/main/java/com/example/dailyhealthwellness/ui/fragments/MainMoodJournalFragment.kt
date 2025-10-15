package com.example.dailyhealthwellness.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhealthwellness.HomeFragment
import com.example.dailyhealthwellness.R
import com.example.dailyhealthwellness.data.models.MoodEntry
import com.example.dailyhealthwellness.data.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainMoodJournalFragment : Fragment() {

    private lateinit var dateRowMood: LinearLayout
    private lateinit var btnAddMood: ImageButton
    private lateinit var rvMoodEntries: RecyclerView
    private lateinit var adapter: MoodAdapter
    private var selectedDate: Calendar = Calendar.getInstance()//current selected day

    private val moodViewModel: MoodViewModel by activityViewModels() // shared ViewModel use -, moods app-wide maintain karann.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main_mood_journal, container, false)

        dateRowMood = view.findViewById(R.id.dateRowMood)//top row of week days
        btnAddMood = view.findViewById(R.id.btnAddMood)//add new mood entry
        rvMoodEntries = view.findViewById(R.id.rv_mood_entries)//RecyclerView to show moods

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

        val backArrow: ImageView = view.findViewById(R.id.moodJournal_arrow)
        backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.homeactivity_fragment_container, HomeFragment())
                .commit()
        }


        // Calendar icon click
        val calendarIcon = view.findViewById<ImageView>(R.id.icon_calander)
        calendarIcon.setOnClickListener {
            val calendarFragment = CalendarFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, calendarFragment)
                .addToBackStack(null)
                .commit()
        }


        setupWeekRow()
        setupRecyclerView()

        // Open CreateMoodFragment
        btnAddMood.setOnClickListener {
            CreateMoodFragment().show(parentFragmentManager, "createMood")
        }

        // ✅ Load previously saved moods from SharedPreferences
        moodViewModel.loadMoods()

        // ✅ Observe moods from ViewModel (persistent)
        moodViewModel.moods.observe(viewLifecycleOwner) {
            adapter.updateListForDate(selectedDate.time)
        }

        return view
    }

//    Top week row 7 days show
//    Click → selectedDate update , list filter
//    Highlight today or selected day with background color.
    private fun setupWeekRow() {
        dateRowMood.removeAllViews()
        val sdfDay = SimpleDateFormat("EEE", Locale.getDefault())
        val sdfDate = SimpleDateFormat("dd", Locale.getDefault())
        val weekStart = Calendar.getInstance()
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.firstDayOfWeek)

        for (i in 0..6) {
            val dayCalendar = weekStart.clone() as Calendar
            dayCalendar.add(Calendar.DAY_OF_MONTH, i)

            val dayText = TextView(requireContext())
            dayText.layoutParams = LinearLayout.LayoutParams(140, 140)
            dayText.gravity = android.view.Gravity.CENTER
            dayText.text = "${sdfDay.format(dayCalendar.time)}\n${sdfDate.format(dayCalendar.time)}"
            dayText.textSize = 14f
            dayText.setTextColor(resources.getColor(R.color.black, null))
            dayText.setBackgroundColor(
                if (isSameDay(dayCalendar.time, selectedDate.time))
                    resources.getColor(R.color.highlightLight, null)
                else
                    resources.getColor(R.color.dayNormal, null)
            )

            dayText.setOnClickListener {
                selectedDate = dayCalendar
                adapter.updateListForDate(selectedDate.time)
                setupWeekRow()
            }

            dateRowMood.addView(dayText)
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

//    RecyclerView bind & Adapter set
//    LinearLayoutManager → vertical list
    private fun setupRecyclerView() {
        adapter = MoodAdapter(moodViewModel.moods.value ?: mutableListOf())
        rvMoodEntries.layoutManager = LinearLayoutManager(requireContext())
        rvMoodEntries.adapter = adapter
    }


    inner class MoodAdapter(private val allMoods: MutableList<MoodEntry>) :
        RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

        private var displayMoods = mutableListOf<MoodEntry>()

//        RecyclerView adapter for mood entries.
//        displayMoods → filtered by selectedDate.
//        ViewHolder binds emoji, moodName, notes, date, time, intensity, color.
        inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val emoji: TextView = itemView.findViewById(R.id.iv_mood_emoji)
            val moodName: TextView = itemView.findViewById(R.id.tv_mood_name)
            val notes: TextView = itemView.findViewById(R.id.tv_mood_notes)
            val date: TextView = itemView.findViewById(R.id.tv_mood_date)
            val time: TextView = itemView.findViewById(R.id.tv_mood_time)
            val intensity: TextView = itemView.findViewById(R.id.tv_mood_intensity)
            val colorView: View = itemView.findViewById(R.id.v_mood_color)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mood, parent, false)
            return MoodViewHolder(view)
        }

        override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
            val mood = displayMoods[position]
            holder.emoji.text = mood.emoji
            holder.moodName.text = mood.moodName
            holder.notes.text = mood.notes
            holder.date.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(mood.date)
            holder.time.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(mood.date)
            holder.intensity.text = when (mood.intensity) {
                1 -> "Low"
                2 -> "Medium"
                else -> "High"
            }
            holder.colorView.setBackgroundColor(
                when (mood.intensity) {
                    1 -> resources.getColor(android.R.color.holo_red_light, null)
                    2 -> resources.getColor(android.R.color.holo_orange_light, null)
                    else -> resources.getColor(android.R.color.holo_green_light, null)
                }
            )

            // ✅ Click item to update or delete
            holder.itemView.setOnClickListener {
                showMoodOptionsDialog(mood)
            }
        }

        override fun getItemCount(): Int = displayMoods.size

        fun updateListForDate(date: Date) {//filter mood by date, only selected day mood display
            displayMoods = (moodViewModel.moods.value ?: listOf()).filter { isSameDay(it.date, date) }
                .toMutableList()
            notifyDataSetChanged()
        }
    }

    // ✅ Show dialog with Update/Delete options, update delete popup eka penvana function ek
    private fun showMoodOptionsDialog(mood: MoodEntry) {
        val options = arrayOf("Update", "Delete")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Select Action")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openUpdateMoodDialog(mood) // Update
                1 -> {
                    moodViewModel.deleteMood(mood) // Delete
                    Toast.makeText(requireContext(), "Mood deleted successfully", Toast.LENGTH_SHORT).show()
                }// delete eka thiyenne viewmodele eke
            }
        }
        builder.show()
    }

    // ✅ Open CreateMoodFragment for updating
    private fun openUpdateMoodDialog(mood: MoodEntry) {
        val dialog = CreateMoodFragment.newInstanceForUpdate(mood)
        dialog.show(parentFragmentManager, "updateMood")
    }
}
