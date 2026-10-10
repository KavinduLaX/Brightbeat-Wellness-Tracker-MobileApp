package com.example.brightbeat.data

import java.io.Serializable

data class MoodEntry(
    val id: String = "",
    val mood: String = "", // emoji representation
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable