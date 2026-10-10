package com.example.brightbeat.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.brightbeat.MainActivity
import com.example.brightbeat.R
import com.example.brightbeat.repository.HydrationRepository
import com.example.brightbeat.utils.SharedPreferencesManager

class HydrationAlarmReceiver : BroadcastReceiver() {
    
    companion object {
        private const val CHANNEL_ID = "hydration_reminder_channel"
        private const val REMINDER_NOTIFICATION_ID = 1001
        private const val GOAL_REACHED_NOTIFICATION_ID = 1002
        private const val ACTION_HYDRATION_ALARM = "com.example.zensteps.HYDRATION_ALARM"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        // Handle hydration alarm
        if (intent.action == ACTION_HYDRATION_ALARM) {
            // Check if hydration goal is reached
            if (isHydrationGoalReached(context)) {
                createNotificationChannel(context)
                showGoalReachedNotification(context)
            } else {
                createNotificationChannel(context)
                showHydrationNotification(context)
            }
        }
        // Handle boot completed
        else if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAlarms(context)
        }
    }
    
    private fun rescheduleAlarms(context: Context) {
        val sharedPreferencesManager = SharedPreferencesManager.getInstance(context)
        
        // Check if alarms were enabled
        if (sharedPreferencesManager.getHydrationAlarmEnabled()) {
            // Reschedule alarms
            // Note: In a real implementation, you would recreate the alarms here
            // For now, we'll just show a notification that alarms need to be re-enabled
        }
    }
    
    private fun isHydrationGoalReached(context: Context): Boolean {
        val sharedPreferencesManager = SharedPreferencesManager.getInstance(context)
        val hydrationRepository = HydrationRepository.getInstance(context)
        
        val dailyGoal = sharedPreferencesManager.getHydrationGoal()
        val consumed = hydrationRepository.getTotalHydrationForToday()
        
        return consumed >= dailyGoal
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.hydration_reminder)
            val descriptionText = context.getString(R.string.time_to_drink_water)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showHydrationNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hydration)
            .setContentTitle(context.getString(R.string.hydration_reminder))
            .setContentText(context.getString(R.string.time_to_drink_water))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_hydration,
                context.getString(R.string.drink_now),
                pendingIntent
            )
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            // Check for permission before showing notification
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || 
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notify(REMINDER_NOTIFICATION_ID, notification)
            }
        }
    }
    
    private fun showGoalReachedNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hydration)
            .setContentTitle(context.getString(R.string.hydration_goal_reached))
            .setContentText(context.getString(R.string.great_job_staying_hydrated))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            // Check for permission before showing notification
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || 
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notify(GOAL_REACHED_NOTIFICATION_ID, notification)
            }
        }
    }
}