package com.example.dailyhealthwellness.data.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dailyhealthwellness.data.models.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    val tasks: MutableLiveData<MutableList<Task>> = MutableLiveData(mutableListOf())//task list eka live data vidiyt thiya gannva. live data venas venakot auto update venva
    private val prefs = application.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE)//TaskPrefs namin shared preference vala permanently save karanva
    private val gson = Gson()// object convert json

    init {// app start venakot shared preference valin data load venva
        loadTasks()
    }

    fun addTask(task: Task) {// new task add
        val currentList = tasks.value ?: mutableListOf()
        currentList.add(task)
        tasks.value = currentList
        saveTasks(currentList)// save in shared preference
    }

    fun updateTask(oldTask: Task, newTask: Task) {// update old task
        val currentList = tasks.value ?: mutableListOf()
        val index = currentList.indexOf(oldTask)//list eke index eka hoyagen update karala aye save karanva
        if (index != -1) {
            currentList[index] = newTask
            tasks.value = currentList
            saveTasks(currentList)
        }
    }

    fun deleteTask(task: Task) {// delete task
        val currentList = tasks.value ?: mutableListOf()
        currentList.remove(task)// task eka delete karanava
        tasks.value = currentList
        saveTasks(currentList)// current list eka save karanva
    }

    private fun saveTasks(list: List<Task>) {//list eka json valat convert karala shared preference vala save karanva
        val json = gson.toJson(list)
        prefs.edit().putString("task_list", json).apply()
    }

    fun loadTasks() {
        val json = prefs.getString("task_list", null)// shared preference vala task_list eka load karanva
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Task>>() {}.type//json convert into MutableList<Task>
            val savedList: MutableList<Task> = gson.fromJson(json, type)
            tasks.value = savedList// assign live data
        }
    }
}
