package com.example.brightbeat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class BrightBeatApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create hydration reminder channel
            val reminderChannel = NotificationChannel(
                HYDRATION_REMINDER_CHANNEL_ID,
                getString(R.string.hydration_reminder),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.time_to_drink_water)
            }
            
            // Create goal reached channel
            val goalReachedChannel = NotificationChannel(
                GOAL_REACHED_CHANNEL_ID,
                getString(R.string.hydration_goal_reached),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.great_job_staying_hydrated)
            }
            
            // Register both channels
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(goalReachedChannel)
        }
    }
    
    companion object {
        const val HYDRATION_REMINDER_CHANNEL_ID = "hydration_reminder_channel"
        const val GOAL_REACHED_CHANNEL_ID = "hydration_goal_channel"
    }
}