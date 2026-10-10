package com.example.brightbeat.utils

import android.content.Context
import android.widget.Toast
import com.example.brightbeat.R
import com.example.brightbeat.repository.AchievementRepository
import com.example.brightbeat.repository.HabitRepository
import com.example.brightbeat.repository.HydrationRepository
import com.example.brightbeat.repository.MoodRepository

class AchievementUtils {
    
    companion object {
        fun checkAndUnlockAchievements(
            context: Context,
            achievementRepository: AchievementRepository,
            habitRepository: HabitRepository,
            hydrationRepository: HydrationRepository,
            moodRepository: MoodRepository
        ) {
            // Check for habit streak achievement
            val habits = habitRepository.getAllHabits()
            val maxHabitStreak = habits.maxOfOrNull { it.streak } ?: 0
            if (maxHabitStreak >= 7) {
                unlockAchievement(context, achievementRepository, "habit_7_days")
            }
            
            // Check for hydration streak achievement
            // This would require tracking consecutive days of hydration
            // For simplicity, we'll just check if user has logged hydration records
            val hydrationRecords = hydrationRepository.getAllHydrationRecords()
            if (hydrationRecords.size >= 7) {
                unlockAchievement(context, achievementRepository, "hydration_7_days")
            }
            
            // Check for mood logging achievement
            val moodEntries = moodRepository.getAllMoodEntries()
            if (moodEntries.size >= 30) {
                unlockAchievement(context, achievementRepository, "mood_30_entries")
            }
        }
        
        private fun unlockAchievement(
            context: Context,
            achievementRepository: AchievementRepository,
            achievementId: String
        ) {
            val achievement = achievementRepository.getAllAchievements()
                .firstOrNull { it.id == achievementId }
            
            if (achievement != null && !achievement.isUnlocked) {
                achievementRepository.unlockAchievement(achievementId)
                Toast.makeText(
                    context,
                    "${context.getString(R.string.congratulations)}\n${achievement.title}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}