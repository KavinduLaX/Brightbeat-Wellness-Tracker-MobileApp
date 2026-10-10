package com.example.brightbeat.repository

import android.content.Context
import com.example.brightbeat.data.Habit
import com.example.brightbeat.utils.SharedPreferencesManager
import java.util.UUID

class HabitRepository private constructor(context: Context) {
    
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance(context)
    
    companion object {
        @Volatile
        private var INSTANCE: HabitRepository? = null
        
        fun getInstance(context: Context): HabitRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HabitRepository(context).also { INSTANCE = it }
            }
        }
    }
    
    fun getAllHabits(): List<Habit> {
        return sharedPreferencesManager.getHabits()
    }
    
    fun addHabit(habit: Habit): Habit {
        val habits = getAllHabits().toMutableList()
        val newHabit = habit.copy(id = UUID.randomUUID().toString())
        habits.add(newHabit)
        sharedPreferencesManager.saveHabits(habits)
        return newHabit
    }
    
    fun updateHabit(updatedHabit: Habit) {
        val habits = getAllHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            sharedPreferencesManager.saveHabits(habits)
        }
    }
    
    fun deleteHabit(habitId: String) {
        val habits = getAllHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        sharedPreferencesManager.saveHabits(habits)
    }
    
    fun toggleHabitCompletion(habitId: String): Habit? {
        val habits = getAllHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habitId }
        if (index != -1) {
            val habit = habits[index]
            val updatedHabit = habit.copy(
                isCompleted = !habit.isCompleted,
                lastCompleted = if (!habit.isCompleted) System.currentTimeMillis() else habit.lastCompleted
            )
            // Update streak if habit was completed today
            if (!habit.isCompleted) {
                updatedHabit.streak = calculateStreak(habit)
            }
            habits[index] = updatedHabit
            sharedPreferencesManager.saveHabits(habits)
            return updatedHabit
        }
        return null
    }
    
    private fun calculateStreak(habit: Habit): Int {
        val lastCompleted = habit.lastCompleted
        val today = System.currentTimeMillis()
        
        // If never completed before, start streak at 1
        if (lastCompleted == 0L) {
            return 1
        }
        
        // Calculate days between last completion and today
        val lastCompletedDate = java.util.Date(lastCompleted)
        val todayDate = java.util.Date(today)
        
        val diff = todayDate.time - lastCompletedDate.time
        val daysDiff = (diff / (1000 * 60 * 60 * 24)).toInt()
        
        // If completed today, return current streak
        if (daysDiff == 0) {
            return habit.streak
        }
        
        // If completed yesterday, increment streak
        if (daysDiff == 1) {
            return habit.streak + 1
        }
        
        // If more than a day has passed, reset streak
        return 1
    }
    
    fun getCompletedHabitsCount(): Int {
        return getAllHabits().count { it.isCompleted }
    }
    
    fun getTotalHabitsCount(): Int {
        return getAllHabits().size
    }
}