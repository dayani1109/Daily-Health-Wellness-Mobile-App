package com.example.dailyhealthwellness.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyhealthwellness.R
import com.example.dailyhealthwellness.data.models.MoodEntry
import com.example.dailyhealthwellness.utils.AppUtils//date/time formatting and utility functions.

class MoodsAdapter(//RecyclerView adapter
    private var moods: List<MoodEntry>,//mood entries list
    private val onMoodClick: (MoodEntry) -> Unit//mood item click callback
) : RecyclerView.Adapter<MoodsAdapter.MoodViewHolder>() {
    
    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiIcon: ImageView = itemView.findViewById(R.id.iv_mood_emoji)
        val moodName: TextView = itemView.findViewById(R.id.tv_mood_name)
        val moodNotes: TextView = itemView.findViewById(R.id.tv_mood_notes)
        val dateText: TextView = itemView.findViewById(R.id.tv_mood_date)
        val timeText: TextView = itemView.findViewById(R.id.tv_mood_time)
        val intensityText: TextView = itemView.findViewById(R.id.tv_mood_intensity)
        val moodColor: View = itemView.findViewById(R.id.v_mood_color)
    }

    //XML layout inflate karala ViewHolder create
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    //List eke current position eke mood data access karanva.
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moods[position]
        
        // Set mood emoji (you'll need to create a method to convert emoji string to drawable)
        // For now, we'll use a placeholder
        holder.emojiIcon.setImageResource(R.drawable.ic_launcher_foreground)

        //set mood name and notes
        holder.moodName.text = mood.moodName
        holder.moodNotes.text = mood.notes
        
        // Set date and time
        holder.dateText.text = AppUtils.formatDate(mood.date)
        holder.timeText.text = AppUtils.formatTime(mood.date)
        
        // Set intensity
        holder.intensityText.text = mood.getIntensityDescription()
        
        // Set mood color
        holder.moodColor.setBackgroundColor(mood.getMoodColor())
        
        // Set click listener
        holder.itemView.setOnClickListener {
            onMoodClick(mood)
        }
    }
    
    override fun getItemCount(): Int = moods.size//RecyclerView eke item count return
    
    //Update the moods list
    fun updateMoods(newMoods: List<MoodEntry>) {
        moods = newMoods
        notifyDataSetChanged()
    }
    

     // Add a new mood entry, and list ekt add krnva
    fun addMood(mood: MoodEntry) {
        val newList = moods.toMutableList()
        newList.add(0, mood) // Add to beginning for chronological order
        moods = newList
        notifyItemInserted(0)
    }
    

     // Remove a mood entry and RecyclerView update
    fun removeMood(position: Int) {
        val newList = moods.toMutableList()
        newList.removeAt(position)
        moods = newList
        notifyItemRemoved(position)
    }
    
    //Update a specific mood entry, RecyclerView refresh
    fun updateMood(position: Int, mood: MoodEntry) {
        val newList = moods.toMutableList()
        newList[position] = mood
        moods = newList
        notifyItemChanged(position)
    }
    
    //Get moods for a specific date(filter)
    fun getMoodsForDate(date: java.util.Date): List<MoodEntry> {
        return moods.filter { AppUtils.isToday(it.date) }
    }
    
    //Get average mood for today, Today moods average intensity calculate
    fun getTodayAverageMood(): Float {
        val todayMoods = moods.filter { AppUtils.isToday(it.date) }
        return if (todayMoods.isNotEmpty()) {
            todayMoods.map { it.intensity }.average().toFloat()
        } else {
            5.0f // Default neutral mood
        }
    }
}
