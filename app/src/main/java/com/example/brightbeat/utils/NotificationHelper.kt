package com.example.brightbeat.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.brightbeat.MainActivity
import com.example.brightbeat.R

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "hydration_reminder",
                "Hydration Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to drink water"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showHydrationReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val drinkNowIntent = Intent(context, MainActivity::class.java).apply {
            action = "DRINK_NOW"
        }
        
        val drinkNowPendingIntent = PendingIntent.getActivity(
            context, 1, drinkNowIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val snoozeIntent = Intent(context, MainActivity::class.java).apply {
            action = "SNOOZE"
        }
        
        val snoozePendingIntent = PendingIntent.getActivity(
            context, 2, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, "hydration_reminder")
            .setSmallIcon(R.drawable.ic_hydration)
            .setContentTitle(context.getString(R.string.hydration_reminder))
            .setContentText(context.getString(R.string.time_to_drink_water))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_hydration, context.getString(R.string.drink_now), drinkNowPendingIntent)
            .addAction(R.drawable.ic_settings, context.getString(R.string.snooze), snoozePendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(1, notification)
    }
}