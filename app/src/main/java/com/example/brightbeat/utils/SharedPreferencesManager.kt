package com.example.brightbeat.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.brightbeat.data.Habit
import com.example.brightbeat.data.MoodEntry
import com.example.brightbeat.data.HydrationRecord
import com.example.brightbeat.data.Achievement

class SharedPreferencesManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "zensteps_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_HABITS = "habits"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_HYDRATION_RECORDS = "hydration_records"
        private const val KEY_ACHIEVEMENTS = "achievements"
        private const val KEY_THEME = "theme"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_LAST_QUOTE_INDEX = "last_quote_index"
        
        // Hydration alarm settings
        private const val KEY_HYDRATION_ALARM_ENABLED = "hydration_alarm_enabled"
        private const val KEY_HYDRATION_ALARM_HOUR = "hydration_alarm_hour"
        private const val KEY_HYDRATION_ALARM_MINUTE = "hydration_alarm_minute"
        private const val KEY_HYDRATION_ALARM_INTERVAL = "hydration_alarm_interval"
        private const val KEY_HYDRATION_QUIET_START_HOUR = "hydration_quiet_start_hour"
        private const val KEY_HYDRATION_QUIET_START_MINUTE = "hydration_quiet_start_minute"
        private const val KEY_HYDRATION_QUIET_END_HOUR = "hydration_quiet_end_hour"
        private const val KEY_HYDRATION_QUIET_END_MINUTE = "hydration_quiet_end_minute"
        
        // Hydration goal
        private const val KEY_HYDRATION_GOAL = "hydration_goal"
        
        @Volatile
        private var INSTANCE: SharedPreferencesManager? = null
        
        fun getInstance(context: Context): SharedPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPreferencesManager(context).also { INSTANCE = it }
            }
        }
    }
    
    // Authentication
    fun setIsLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun setUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    
    fun clearUserData() {
        prefs.edit().clear().apply()
    }
    
    // Onboarding
    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }
    
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    // Theme
    fun setTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
    }
    
    fun getTheme(): String {
        return prefs.getString(KEY_THEME, "default") ?: "default"
    }
    
    // Habits
    fun saveHabits(habits: List<Habit>) {
        val habitsString = serializeHabits(habits)
        prefs.edit().putString(KEY_HABITS, habitsString).apply()
    }
    
    fun getHabits(): List<Habit> {
        val habitsString = prefs.getString(KEY_HABITS, null)
        return if (habitsString != null) {
            deserializeHabits(habitsString)
        } else {
            emptyList()
        }
    }
    
    private fun serializeHabits(habits: List<Habit>): String {
        val sb = StringBuilder()
        for (habit in habits) {
            sb.append("${habit.id}|${habit.name}|${habit.description}|${habit.frequency}|${habit.createdAt}|${habit.isCompleted}|${habit.streak}|${habit.lastCompleted}#")
        }
        return sb.toString()
    }
    
    private fun deserializeHabits(habitsString: String): List<Habit> {
        val habits = mutableListOf<Habit>()
        val habitStrings = habitsString.split("#").filter { it.isNotEmpty() }
        
        for (habitString in habitStrings) {
            val parts = habitString.split("|")
            if (parts.size == 8) {
                habits.add(
                    Habit(
                        id = parts[0],
                        name = parts[1],
                        description = parts[2],
                        frequency = parts[3],
                        createdAt = parts[4].toLong(),
                        isCompleted = parts[5].toBoolean(),
                        streak = parts[6].toInt(),
                        lastCompleted = parts[7].toLong()
                    )
                )
            }
        }
        return habits
    }
    
    // Mood Entries
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val moodString = serializeMoodEntries(moodEntries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, moodString).apply()
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        val moodString = prefs.getString(KEY_MOOD_ENTRIES, null)
        return if (moodString != null) {
            deserializeMoodEntries(moodString)
        } else {
            emptyList()
        }
    }
    
    private fun serializeMoodEntries(moodEntries: List<MoodEntry>): String {
        val sb = StringBuilder()
        for (moodEntry in moodEntries) {
            sb.append("${moodEntry.id}|${moodEntry.mood}|${moodEntry.note}|${moodEntry.timestamp}#")
        }
        return sb.toString()
    }
    
    private fun deserializeMoodEntries(moodString: String): List<MoodEntry> {
        val moodEntries = mutableListOf<MoodEntry>()
        val moodStrings = moodString.split("#").filter { it.isNotEmpty() }
        
        for (moodString in moodStrings) {
            val parts = moodString.split("|")
            if (parts.size == 4) {
                moodEntries.add(
                    MoodEntry(
                        id = parts[0],
                        mood = parts[1],
                        note = parts[2],
                        timestamp = parts[3].toLong()
                    )
                )
            }
        }
        return moodEntries
    }
    
    // Hydration Records
    fun saveHydrationRecords(records: List<HydrationRecord>) {
        val recordsString = serializeHydrationRecords(records)
        prefs.edit().putString(KEY_HYDRATION_RECORDS, recordsString).apply()
    }
    
    fun getHydrationRecords(): List<HydrationRecord> {
        val recordsString = prefs.getString(KEY_HYDRATION_RECORDS, null)
        return if (recordsString != null) {
            deserializeHydrationRecords(recordsString)
        } else {
            emptyList()
        }
    }
    
    private fun serializeHydrationRecords(records: List<HydrationRecord>): String {
        val sb = StringBuilder()
        for (record in records) {
            sb.append("${record.id}|${record.amount}|${record.timestamp}#")
        }
        return sb.toString()
    }
    
    private fun deserializeHydrationRecords(recordsString: String): List<HydrationRecord> {
        val records = mutableListOf<HydrationRecord>()
        val recordStrings = recordsString.split("#").filter { it.isNotEmpty() }
        
        for (recordString in recordStrings) {
            val parts = recordString.split("|")
            if (parts.size == 3) {
                records.add(
                    HydrationRecord(
                        id = parts[0],
                        amount = parts[1].toInt(),
                        timestamp = parts[2].toLong()
                    )
                )
            }
        }
        return records
    }
    
    // Hydration Goal
    fun setHydrationGoal(goal: Int) {
        prefs.edit().putInt(KEY_HYDRATION_GOAL, goal).apply()
    }
    
    fun getHydrationGoal(): Int {
        return prefs.getInt(KEY_HYDRATION_GOAL, 2000)
    }
    
    // Hydration Alarm Settings
    fun setHydrationAlarmEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HYDRATION_ALARM_ENABLED, enabled).apply()
    }
    
    fun getHydrationAlarmEnabled(): Boolean {
        return prefs.getBoolean(KEY_HYDRATION_ALARM_ENABLED, false)
    }
    
    fun setHydrationAlarmHour(hour: Int) {
        prefs.edit().putInt(KEY_HYDRATION_ALARM_HOUR, hour).apply()
    }
    
    fun getHydrationAlarmHour(): Int {
        return prefs.getInt(KEY_HYDRATION_ALARM_HOUR, 9)
    }
    
    fun setHydrationAlarmMinute(minute: Int) {
        prefs.edit().putInt(KEY_HYDRATION_ALARM_MINUTE, minute).apply()
    }
    
    fun getHydrationAlarmMinute(): Int {
        return prefs.getInt(KEY_HYDRATION_ALARM_MINUTE, 0)
    }
    
    fun setHydrationAlarmInterval(interval: Int) {
        prefs.edit().putInt(KEY_HYDRATION_ALARM_INTERVAL, interval).apply()
    }
    
    fun getHydrationAlarmInterval(): Int {
        return prefs.getInt(KEY_HYDRATION_ALARM_INTERVAL, 60)
    }
    
    fun setHydrationQuietStartHour(hour: Int) {
        prefs.edit().putInt(KEY_HYDRATION_QUIET_START_HOUR, hour).apply()
    }
    
    fun getHydrationQuietStartHour(): Int {
        return prefs.getInt(KEY_HYDRATION_QUIET_START_HOUR, 21)
    }
    
    fun setHydrationQuietStartMinute(minute: Int) {
        prefs.edit().putInt(KEY_HYDRATION_QUIET_START_MINUTE, minute).apply()
    }
    
    fun getHydrationQuietStartMinute(): Int {
        return prefs.getInt(KEY_HYDRATION_QUIET_START_MINUTE, 0)
    }
    
    fun setHydrationQuietEndHour(hour: Int) {
        prefs.edit().putInt(KEY_HYDRATION_QUIET_END_HOUR, hour).apply()
    }
    
    fun getHydrationQuietEndHour(): Int {
        return prefs.getInt(KEY_HYDRATION_QUIET_END_HOUR, 7)
    }
    
    fun setHydrationQuietEndMinute(minute: Int) {
        prefs.edit().putInt(KEY_HYDRATION_QUIET_END_MINUTE, minute).apply()
    }
    
    fun getHydrationQuietEndMinute(): Int {
        return prefs.getInt(KEY_HYDRATION_QUIET_END_MINUTE, 0)
    }
    
    // Achievements
    fun saveAchievements(achievements: List<Achievement>) {
        val achievementsString = serializeAchievements(achievements)
        prefs.edit().putString(KEY_ACHIEVEMENTS, achievementsString).apply()
    }
    
    fun getAchievements(): List<Achievement> {
        val achievementsString = prefs.getString(KEY_ACHIEVEMENTS, null)
        return if (achievementsString != null) {
            deserializeAchievements(achievementsString)
        } else {
            initializeDefaultAchievements()
        }
    }
    
    private fun initializeDefaultAchievements(): List<Achievement> {
        val achievements = listOf(
            Achievement(
                id = "habit_7_days",
                title = "7-Day Habit Streak",
                description = "Complete a habit for 7 consecutive days",
                icon = "🔥",
                requiredStreak = 7,
                type = "habit"
            ),
            Achievement(
                id = "hydration_7_days",
                title = "Hydration Hero",
                description = "Drink water for 7 consecutive days",
                icon = "💧",
                requiredStreak = 7,
                type = "hydration"
            ),
            Achievement(
                id = "mood_30_entries",
                title = "Emotional Explorer",
                description = "Log your mood 30 times",
                icon = "😊",
                requiredStreak = 30,
                type = "mood"
            )
        )
        saveAchievements(achievements)
        return achievements
    }
    
    private fun serializeAchievements(achievements: List<Achievement>): String {
        val sb = StringBuilder()
        for (achievement in achievements) {
            sb.append("${achievement.id}|${achievement.title}|${achievement.description}|${achievement.icon}|${achievement.isUnlocked}|${achievement.requiredStreak}|${achievement.type}#")
        }
        return sb.toString()
    }
    
    private fun deserializeAchievements(achievementsString: String): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        val achievementStrings = achievementsString.split("#").filter { it.isNotEmpty() }
        
        for (achievementString in achievementStrings) {
            val parts = achievementString.split("|")
            if (parts.size == 7) {
                achievements.add(
                    Achievement(
                        id = parts[0],
                        title = parts[1],
                        description = parts[2],
                        icon = parts[3],
                        isUnlocked = parts[4].toBoolean(),
                        requiredStreak = parts[5].toInt(),
                        type = parts[6]
                    )
                )
            }
        }
        return achievements
    }
    
    // Motivational Quotes
    fun setLastQuoteIndex(index: Int) {
        prefs.edit().putInt(KEY_LAST_QUOTE_INDEX, index).apply()
    }
    
    fun getLastQuoteIndex(): Int {
        return prefs.getInt(KEY_LAST_QUOTE_INDEX, -1)
    }
}