package com.example.dailyhealthwellness.ui.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyhealthwellness.R
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var etTaskName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var tvTaskDate: TextView
    private lateinit var btnPickDate: ImageButton

    private var selectedDate: String? = null//user select karana date ek
    private val calendar = Calendar.getInstance()//default date today

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        etTaskName = findViewById(R.id.etTaskName)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        tvTaskDate = findViewById(R.id.tvTaskDate)
        btnPickDate = findViewById(R.id.btnPickDate)



        // Back arrow
        val backArrow: ImageView = findViewById(R.id.addTask_arrow)
        backArrow.setOnClickListener {
            // Close this activity and return to the previous fragment/activity
            finish()
            // Smooth slide animation
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Default date = today set karala textview eke display karanva
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = sdf.format(calendar.time)
        tvTaskDate.text = "Selected Date: $selectedDate"

        // Date Picker open, user data select kalam select date variable ek update venva also text view
        btnPickDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                calendar.set(y, m, d)
                selectedDate = sdf.format(calendar.time)
                tvTaskDate.text = "Selected Date: $selectedDate"
            }, year, month, day).show()
        }

        // Save button
        btnSave.setOnClickListener {// user task enter krala date select kalanm result eka return vela previou activity ekt yanva
            val taskName = etTaskName.text.toString().trim()
            if (taskName.isNotEmpty() && !selectedDate.isNullOrEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("taskName", taskName)
                resultIntent.putExtra("taskDate", selectedDate)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                etTaskName.error = "Please enter a task"
            }
        }

        // Cancel button. cancel click kalam result ek cancel vela previous ekt yanva
        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }


    }
}
