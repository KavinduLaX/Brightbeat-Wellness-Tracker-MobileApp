package com.example.brightbeat.repository

import android.content.Context
import com.example.brightbeat.data.MoodEntry
import com.example.brightbeat.utils.SharedPreferencesManager
import java.util.UUID

class MoodRepository private constructor(context: Context) {
    
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance(context)
    
    companion object {
        @Volatile
        private var INSTANCE: MoodRepository? = null
        
        fun getInstance(context: Context): MoodRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MoodRepository(context).also { INSTANCE = it }
            }
        }
    }
    
    fun getAllMoodEntries(): List<MoodEntry> {
        return sharedPreferencesManager.getMoodEntries()
    }
    
    fun addMoodEntry(moodEntry: MoodEntry): MoodEntry {
        val moodEntries = getAllMoodEntries().toMutableList()
        val newMoodEntry = moodEntry.copy(id = UUID.randomUUID().toString())
        moodEntries.add(newMoodEntry)
        sharedPreferencesManager.saveMoodEntries(moodEntries)
        return newMoodEntry
    }
    
    fun deleteMoodEntry(moodEntryId: String) {
        val moodEntries = getAllMoodEntries().toMutableList()
        moodEntries.removeAll { it.id == moodEntryId }
        sharedPreferencesManager.saveMoodEntries(moodEntries)
    }
    
    fun getMoodEntriesCount(): Int {
        return getAllMoodEntries().size
    }
    
    fun getMoodEntriesForDate(date: Long): List<MoodEntry> {
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)
        
        return getAllMoodEntries().filter { moodEntry ->
            moodEntry.timestamp >= startOfDay && moodEntry.timestamp <= endOfDay
        }
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getEndOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}