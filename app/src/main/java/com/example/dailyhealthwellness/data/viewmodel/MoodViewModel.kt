package com.example.dailyhealthwellness.data.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dailyhealthwellness.data.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    val moods: MutableLiveData<MutableList<MoodEntry>> = MutableLiveData(mutableListOf())//mood -> livedata variable ekak. moodentry object list ekak thiyenne. mood list ek change unoth update venva

    private val prefs = application.getSharedPreferences("MoodPrefs", Context.MODE_PRIVATE)//sharedpreference. mooddata locally store using MoodPrefs
    private val gson = Gson()//Gson library. object list convert to json format

    init {// create moodviewmodel then old mood load karala live data ekt danna
        loadMoods()
    }

    fun addMood(mood: MoodEntry) {
        val currentList = moods.value ?: mutableListOf()//get old mood list
        currentList.add(mood)//add new mood
        moods.value = currentList//update live data
        saveMoods(currentList)//call sharedpreference and save data
    }

    fun updateMood(oldMood: MoodEntry, newMood: MoodEntry) {
        val currentList = moods.value ?: mutableListOf()
        val index = currentList.indexOf(oldMood)//check old mood in list it replace into new mood
        if (index != -1) {
            currentList[index] = newMood
            moods.value = currentList
            saveMoods(currentList)
        }
    }

    fun deleteMood(mood: MoodEntry) {//delete mood
        val currentList = moods.value ?: mutableListOf()
        currentList.remove(mood)
        moods.value = currentList
        saveMoods(currentList)
    }

    private fun saveMoods(list: List<MoodEntry>) {
        val json = gson.toJson(list)//convert mood list into json format
        prefs.edit().putString("mood_list", json).apply()//json ek mood_list kiyana namin shared preference ekt save karanva
    }

    fun loadMoods() {//old mood data shared preference use karala load karanva
        val json = prefs.getString("mood_list", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            val savedList: MutableList<MoodEntry> = gson.fromJson(json, type)
            moods.value = savedList
        }
    }
}
