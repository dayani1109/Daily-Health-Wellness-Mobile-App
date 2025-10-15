package com.example.dailyhealthwellness.ui.adapters

import android.app.AlertDialog//task update dialog show
import android.app.DatePickerDialog//date picker open for task date update
import android.content.Context
import android.view.LayoutInflater//XML layout view inflate
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView//tasks list display
import com.example.dailyhealthwellness.R
import com.example.dailyhealthwellness.data.models.Task
import java.util.*

class TaskAdapter(//RecyclerView adapter
    private val context: Context,
    private val tasks: MutableList<Task>,
    private var selectedDate: String,//user select date
    private val onTaskChanged: () -> Unit,//task complete or update notify
    private val onDeleteClick: (Task) -> Unit//delete button click handle
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbTask: CheckBox = itemView.findViewById(R.id.cbTask)
        val tvTaskName: TextView = itemView.findViewById(R.id.tvTaskName)
        val tvTaskDate: TextView = itemView.findViewById(R.id.tvTaskDate)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTask)
    }

    //Task item layout inflate  → ViewHolder return
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    //position valat task access karanva
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        if (task.date != selectedDate) {//task date ek selected date nemeyinm hide
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        } else {//task display karanva
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            holder.tvTaskName.text = task.name
            holder.tvTaskDate.text = task.date
            holder.cbTask.isChecked = task.isCompleted

            // Handle task completion. Checkbox click → task.isCompleted update + callback.
            holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                onTaskChanged()
            }

            // Delete task, Delete click → callback call + Toast show
            holder.btnDelete.setOnClickListener {
                onDeleteClick(task)

                // Show Toast
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
            }

            // Update task on long click
            holder.itemView.setOnLongClickListener {
                showUpdateDialog(task, position)
                true
            }
        }
    }

    override fun getItemCount(): Int = tasks.size//RecyclerView item count return

    fun updateDate(date: String) {//Date change  → selectedDate update + RecyclerView refresh.
        selectedDate = date
        notifyDataSetChanged()
    }

    fun getProgress(): Int {//Selected date tasks  progress percentage calculate
        val filteredTasks = tasks.filter { it.date == selectedDate }
        if (filteredTasks.isEmpty()) return 0
        val completed = filteredTasks.count { it.isCompleted }
        return (completed * 100) / filteredTasks.size
    }

    private fun showUpdateDialog(task: Task, position: Int) {//Update dialog inflate + UI elements find
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_task, null)
        val etName = dialogView.findViewById<EditText>(R.id.etUpdateTaskName)
        val etDate = dialogView.findViewById<EditText>(R.id.etUpdateTaskDate)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelUpdate)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveUpdate)

        //Current task values set
        etName.setText(task.name)
        etDate.setText(task.date)

        // Date picker for update
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, { _, year, month, day ->
                etDate.setText("%04d-%02d-%02d".format(year, month + 1, day))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        //Dialog create
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        //Cancel → dismiss dialog.
        //Save → task update + RecyclerView refresh + callback + Toast.
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener {
            task.name = etName.text.toString()
            task.date = etDate.text.toString()
            notifyItemChanged(position)
            onTaskChanged()
            dialog.dismiss()

            // Show Toast
            Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show()
        }

        dialog.show()//dialog screen eke show karanva
    }
}
