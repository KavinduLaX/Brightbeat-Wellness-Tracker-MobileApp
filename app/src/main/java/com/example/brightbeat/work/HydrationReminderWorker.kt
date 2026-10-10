package com.example.brightbeat.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.brightbeat.utils.NotificationHelper

class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        return try {
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.showHydrationReminder()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}