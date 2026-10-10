package com.example.brightbeat.repository

import android.content.Context
import com.example.brightbeat.data.Achievement
import com.example.brightbeat.utils.SharedPreferencesManager

class AchievementRepository private constructor(context: Context) {
    
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance(context)
    
    companion object {
        @Volatile
        private var INSTANCE: AchievementRepository? = null
        
        fun getInstance(context: Context): AchievementRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AchievementRepository(context).also { INSTANCE = it }
            }
        }
    }
    
    fun getAllAchievements(): List<Achievement> {
        return sharedPreferencesManager.getAchievements()
    }
    
    fun unlockAchievement(achievementId: String) {
        val achievements = getAllAchievements().toMutableList()
        val index = achievements.indexOfFirst { it.id == achievementId }
        if (index != -1) {
            val achievement = achievements[index]
            if (!achievement.isUnlocked) {
                val updatedAchievement = achievement.copy(isUnlocked = true)
                achievements[index] = updatedAchievement
                sharedPreferencesManager.saveAchievements(achievements)
            }
        }
    }
    
    fun getUnlockedAchievements(): List<Achievement> {
        return getAllAchievements().filter { it.isUnlocked }
    }
    
    fun getLockedAchievements(): List<Achievement> {
        return getAllAchievements().filter { !it.isUnlocked }
    }
}