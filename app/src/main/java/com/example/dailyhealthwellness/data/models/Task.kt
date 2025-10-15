package com.example.dailyhealthwellness.data.models

data class Task(
    var name: String,
    var date: String,          // Added date for day-by-day tracking
    var isCompleted: Boolean = false
)
