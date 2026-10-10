package com.example.brightbeat.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.data.Habit
import com.google.android.material.card.MaterialCardView

class DashboardHabitAdapter(
    private var habits: List<Habit>,
    private val onHabitToggle: (Habit) -> Unit
) : RecyclerView.Adapter<DashboardHabitAdapter.HabitViewHolder>() {
    
    inner class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitIconContainer: MaterialCardView = view.findViewById(R.id.habitIconContainer)
        val habitIconTextView: TextView = view.findViewById(R.id.habitIconTextView)
        val habitNameTextView: TextView = view.findViewById(R.id.habitNameTextView)
        val habitStreakTextView: TextView = view.findViewById(R.id.habitStreakTextView)
        val habitCompletedCheckBox: CheckBox = view.findViewById(R.id.habitCompletedCheckBox)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_habit, parent, false)
        return HabitViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        
        holder.habitNameTextView.text = habit.name
        holder.habitStreakTextView.text = "${habit.streak} day streak"
        holder.habitCompletedCheckBox.isChecked = habit.isCompleted
        
        // Set a random emoji for the habit icon
        val emojis = listOf("🏃", "📚", "🧘", "💧", "✍️", "🎵", "🍳", "🦷", "💊", "📱")
        val randomEmoji = emojis.random()
        holder.habitIconTextView.text = randomEmoji
        
        // Set icon background color based on emoji
        val colors = listOf(
            R.color.primary_red,
            R.color.primary_orange,
            R.color.primary_yellow,
            R.color.primary_blue,
            R.color.success_green
        )
        val randomColor = colors.random()
        holder.habitIconContainer.setCardBackgroundColor(
            holder.itemView.context.getColor(randomColor)
        )
        
        holder.habitCompletedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Create a copy of the habit with updated completion status
            val updatedHabit = habit.copy(isCompleted = isChecked)
            onHabitToggle(updatedHabit)
        }
    }
    
    override fun getItemCount(): Int = habits.size
    
    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}