package com.example.dailyhealthwellness.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView//show calander
    private lateinit var rvPastMoods: RecyclerView//recycle view ek mood list display karanna
    private lateinit var adapter: PastMoodAdapter // mood list connect recycle view

    private val moodViewModel: MoodViewModel by activityViewModels() // ✅ Shared ViewModel access, view model ekem moods data labagannva
    private val displayList = mutableListOf<MoodEntry>()//thorala thiyen day ekat adala mood meke thiyenne

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)// load fragment and xml views connect karanva

        calendarView = view.findViewById(R.id.fullCalendarView)//calander view
        rvPastMoods = view.findViewById(R.id.rvPastMoods)//recycle view

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


        adapter = PastMoodAdapter(displayList)//mood show karan adapter ek assign karanva
        rvPastMoods.layoutManager = LinearLayoutManager(requireContext())//list ek vertical format ekt pennanva
        rvPastMoods.adapter = adapter

        // Back arrow
        val backArrow: ImageView = view.findViewById(R.id.moodCalander_arrow)
        backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,  // enter
                    android.R.anim.slide_out_right // exit
                )
                .replace(R.id.homeactivity_fragment_container, MainMoodJournalFragment())
                .commit()
        }

        // Observe shared moods, viewmodel ek observe karala mood data venas venkot calander ek update venva
        moodViewModel.moods.observe(viewLifecycleOwner) {
            filterMoodsByDate(Calendar.getInstance().time)
        }

        // Default: show today's moods first
        filterMoodsByDate(Calendar.getInstance().time)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->//calander eke select karana day
            val selectedDate = "$dayOfMonth/${month + 1}/$year"//date ek format karanva
            Toast.makeText(requireContext(), "Selected: $selectedDate", Toast.LENGTH_SHORT).show()

            val sdf = SimpleDateFormat("d/M/yyyy", Locale.getDefault())//date ek object ekk karala date ekt adala mood filter karanva
            val parsedDate = sdf.parse(selectedDate)
            if (parsedDate != null) {
                filterMoodsByDate(parsedDate)
            }
        }

        return view
    }

    private fun filterMoodsByDate(date: Date) {//view model ekem moods aragena filter karala adala day ekt thiyena mood vitrak displaylist ekt danva
        displayList.clear()
        displayList.addAll((moodViewModel.moods.value ?: listOf()).filter { isSameDay(it.date, date) })
        adapter.notifyDataSetChanged()//adapter notify and refresh
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {// check same year mon day
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    inner class PastMoodAdapter(private val moods: List<MoodEntry>) ://recycle view ekt moods bind karan adapter ek
        RecyclerView.Adapter<PastMoodAdapter.PastMoodViewHolder>() {

        inner class PastMoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val emoji: TextView = itemView.findViewById(R.id.iv_mood_emoji)
            val name: TextView = itemView.findViewById(R.id.tv_mood_name)
            val date: TextView = itemView.findViewById(R.id.tv_mood_date)
        }

        //layout ek load karanva
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastMoodViewHolder {//item mood ek use karala mood ekk create karanva
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mood, parent, false)
            return PastMoodViewHolder(view)
        }

        //mood data adala ui ekt assign karanva
        override fun onBindViewHolder(holder: PastMoodViewHolder, position: Int) {//mood data ui ekt bind karanva
            val mood = moods[position]
            holder.emoji.text = mood.emoji
            holder.name.text = mood.moodName
            holder.date.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(mood.date)
        }

        override fun getItemCount(): Int = moods.size//moods gana return karanva


    }
}
