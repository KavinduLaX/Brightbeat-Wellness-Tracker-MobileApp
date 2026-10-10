package com.example.brightbeat.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTimeUtils {
    
    companion object {
        fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
        
        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
        
        fun formatDateTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
        
        fun getStartOfDay(timestamp: Long): Long {
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = timestamp
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
        
        fun getEndOfDay(timestamp: Long): Long {
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = timestamp
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
            calendar.set(java.util.Calendar.MINUTE, 59)
            calendar.set(java.util.Calendar.SECOND, 59)
            calendar.set(java.util.Calendar.MILLISECOND, 999)
            return calendar.timeInMillis
        }
    }
}