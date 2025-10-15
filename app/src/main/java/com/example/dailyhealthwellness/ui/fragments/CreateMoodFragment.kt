package com.example.dailyhealthwellness.ui.fragments

import android.app.DatePickerDialog//date eka thorana dialog ekk
import android.app.TimePickerDialog//time eka thorana dialog ekk
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment//popup dialog
import androidx.fragment.app.activityViewModels
import com.example.dailyhealthwellness.R
import com.example.dailyhealthwellness.data.models.MoodEntry
import com.example.dailyhealthwellness.data.viewmodel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

class CreateMoodFragment : DialogFragment() {//dialog fragment kiyanne popup form ekk vage

    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView
    private lateinit var etJournal: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLow: Button
    private lateinit var btnMedium: Button
    private lateinit var btnHigh: Button

    private lateinit var emojiHappy: TextView
    private lateinit var emojiSad: TextView
    private lateinit var emojiAngry: TextView
    private lateinit var emojiNeutral: TextView
    private lateinit var emojiExcited: TextView

    private var selectedEmoji: String? = null//select emoji feeling date track karanva
    private var selectedFeeling: String? = null
    private var selectedDate: Calendar = Calendar.getInstance()

    private val moodViewModel: MoodViewModel by activityViewModels() // Shared ViewModel with persistence, moodviewmodel connect karala thiyenne datastore ekk vage

    private var editingMood: MoodEntry? = null // ✅ For update mode, mood ekk update karankot meke save venva

    companion object {//aluth fragment ekk create karala edit karana mood pass karagann kramayak
        // Factory method to pass a MoodEntry for updating
        fun newInstanceForUpdate(mood: MoodEntry): CreateMoodFragment {
            val fragment = CreateMoodFragment()
            fragment.editingMood = mood
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_mood, container, false)//xml ek load karanva

        // Initialize views
        tvDate = view.findViewById(R.id.tvSelectedDate)
        tvTime = view.findViewById(R.id.tvSelectedTime)
        etJournal = view.findViewById(R.id.etMoodJournal)
        btnSave = view.findViewById(R.id.btnSaveMood)
        btnLow = view.findViewById(R.id.btnLow)
        btnMedium = view.findViewById(R.id.btnMedium)
        btnHigh = view.findViewById(R.id.btnHigh)

        emojiHappy = view.findViewById(R.id.emoji_happy)
        emojiSad = view.findViewById(R.id.emoji_sad)
        emojiAngry = view.findViewById(R.id.emoji_angry)
        emojiNeutral = view.findViewById(R.id.emoji_neutral)
        emojiExcited = view.findViewById(R.id.emoji_excited)

        // If editing, prefill values, mood editing mode nm kalim save karapu data fields valat danna
        editingMood?.let { mood ->
            selectedDate.time = mood.date
            selectedEmoji = mood.emoji
            selectedFeeling = mood.moodName
            etJournal.setText(mood.notes)
            highlightSelectedEmoji(mood.emoji)
            selectFeeling(mood.moodName)
        }

        // Set current date and time
        updateDateTime()

        // Open DatePicker when date clicked
        tvDate.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateTime()
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Open TimePicker when time clicked
        tvTime.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(requireContext(),
                { _, hour, minute ->
                    selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                    selectedDate.set(Calendar.MINUTE, minute)
                    updateDateTime()
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
            ).show()
        }

        // Emoji click listeners, emoji ekk select kaloth old backgroun color ek change vela selected ek highlight venva
        val emojiClickListener = View.OnClickListener { v ->
            resetEmojiBackground()
            v.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
            selectedEmoji = (v as TextView).text.toString()//mekt emoji ek assign venva
        }

        emojiHappy.setOnClickListener(emojiClickListener)
        emojiSad.setOnClickListener(emojiClickListener)
        emojiAngry.setOnClickListener(emojiClickListener)
        emojiNeutral.setOnClickListener(emojiClickListener)
        emojiExcited.setOnClickListener(emojiClickListener)

        // Feeling buttons
        btnLow.setOnClickListener { selectFeeling("Low") }
        btnMedium.setOnClickListener { selectFeeling("Medium") }
        btnHigh.setOnClickListener { selectFeeling("High") }

        // Save button
        btnSave.setOnClickListener {// save button ek click karam
            val journalText = etJournal.text.toString()
            if (selectedEmoji == null) {
                Toast.makeText(requireContext(), "Please select an emoji", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedFeeling == null) {
                Toast.makeText(requireContext(), "Please select your feeling intensity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (journalText.isEmpty()) {
                Toast.makeText(requireContext(), "Please write your mood details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intensity = when (selectedFeeling) {
                "Low" -> 1
                "Medium" -> 2
                else -> 3
            }

            if (editingMood != null) { //editing mood  null nathnm thiyena value valim updatemood valin save karanva
                // ✅ Create a new MoodEntry with updated values
                val updatedMood = MoodEntry(
                    emoji = selectedEmoji!!,
                    moodName = selectedFeeling!!,
                    notes = journalText,
                    date = selectedDate.time,
                    intensity = intensity
                )

                // ✅ Pass old and new MoodEntry to updateMood()
                moodViewModel.updateMood(editingMood!!, updatedMood)
                Toast.makeText(requireContext(), "Mood updated successfully", Toast.LENGTH_SHORT).show()
            } else {//nullnm
                // ✅ Add new mood
                val moodEntry = MoodEntry(
                    emoji = selectedEmoji!!,
                    moodName = selectedFeeling!!,
                    notes = journalText,
                    date = selectedDate.time,
                    intensity = intensity
                )
                moodViewModel.addMood(moodEntry)
                Toast.makeText(requireContext(), "Mood added successfully", Toast.LENGTH_SHORT).show()
            }

            dismiss()//dialog close
        }

        return view
    }

    private fun highlightSelectedEmoji(emoji: String) {//higlight selected emoji
        resetEmojiBackground()
        when (emoji) {
            emojiHappy.text.toString() -> emojiHappy.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
            emojiSad.text.toString() -> emojiSad.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
            emojiAngry.text.toString() -> emojiAngry.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
            emojiNeutral.text.toString() -> emojiNeutral.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
            emojiExcited.text.toString() -> emojiExcited.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
        }
    }

    // SimpleDateFormat use karala date/time text fields update
    private fun updateDateTime() {
        val sdfDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        tvDate.text = "Date: ${sdfDate.format(selectedDate.time)}"
        tvTime.text = "Time: ${sdfTime.format(selectedDate.time)}"
    }

    private fun resetEmojiBackground() {//moji eke background transparent karala reset karanva
        emojiHappy.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
        emojiSad.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
        emojiAngry.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
        emojiNeutral.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
        emojiExcited.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
    }

    private fun selectFeeling(feeling: String) {// feeling button highlight
        selectedFeeling = feeling
        btnLow.setBackgroundColor(resources.getColor(R.color.primaryGreen, null))
        btnMedium.setBackgroundColor(resources.getColor(R.color.primaryGreen, null))
        btnHigh.setBackgroundColor(resources.getColor(R.color.primaryGreen, null))

        when (feeling) {
            "Low" -> btnLow.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
            "Medium" -> btnMedium.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
            "High" -> btnHigh.setBackgroundColor(resources.getColor(R.color.lightGreen, null))
        }
    }

    // Keep dialog full screen width
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
