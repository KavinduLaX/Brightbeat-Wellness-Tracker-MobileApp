package com.example.brightbeat.repository

import android.content.Context
import com.example.brightbeat.data.HydrationRecord
import com.example.brightbeat.utils.SharedPreferencesManager
import java.util.UUID

class HydrationRepository private constructor(context: Context) {
    
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance(context)
    
    companion object {
        @Volatile
        private var INSTANCE: HydrationRepository? = null
        
        fun getInstance(context: Context): HydrationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HydrationRepository(context).also { INSTANCE = it }
            }
        }
    }
    
    fun getAllHydrationRecords(): List<HydrationRecord> {
        return sharedPreferencesManager.getHydrationRecords()
    }
    
    fun addHydrationRecord(record: HydrationRecord): HydrationRecord {
        val records = getAllHydrationRecords().toMutableList()
        val newRecord = record.copy(id = UUID.randomUUID().toString())
        records.add(newRecord)
        sharedPreferencesManager.saveHydrationRecords(records)
        return newRecord
    }
    
    fun deleteHydrationRecord(recordId: String) {
        val records = getAllHydrationRecords().toMutableList()
        records.removeAll { it.id == recordId }
        sharedPreferencesManager.saveHydrationRecords(records)
    }
    
    fun getTotalHydrationForToday(): Int {
        val today = System.currentTimeMillis()
        val startOfDay = getStartOfDay(today)
        val endOfDay = getEndOfDay(today)
        
        return getAllHydrationRecords().filter { record ->
            record.timestamp >= startOfDay && record.timestamp <= endOfDay
        }.sumOf { it.amount }
    }
    
    fun getHydrationRecordsForToday(): List<HydrationRecord> {
        val today = System.currentTimeMillis()
        val startOfDay = getStartOfDay(today)
        val endOfDay = getEndOfDay(today)
        
        return getAllHydrationRecords().filter { record ->
            record.timestamp >= startOfDay && record.timestamp <= endOfDay
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