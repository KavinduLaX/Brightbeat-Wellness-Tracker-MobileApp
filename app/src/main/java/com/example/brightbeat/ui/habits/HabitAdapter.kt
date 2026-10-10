package com.example.brightbeat.ui.habits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.data.Habit

class HabitAdapter(
    private var habits: List<Habit>,
    private val onHabitToggle: (Habit) -> Unit,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {
    
    inner class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitNameTextView: TextView = view.findViewById(R.id.habitNameTextView)
        val habitDescriptionTextView: TextView = view.findViewById(R.id.habitDescriptionTextView)
        val streakTextView: TextView = view.findViewById(R.id.streakTextView)
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        
        holder.habitNameTextView.text = habit.name
        holder.habitDescriptionTextView.text = habit.description
        holder.streakTextView.text = "${habit.streak} days"
        holder.checkBox.isChecked = habit.isCompleted
        
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onHabitToggle(habit)
        }
        
        holder.editButton.setOnClickListener {
            onHabitClick(habit)
        }
        
        holder.deleteButton.setOnClickListener {
            onHabitDelete(habit)
        }
        
        holder.itemView.setOnClickListener {
            onHabitClick(habit)
        }
    }
    
    override fun getItemCount(): Int = habits.size
    
    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}