package com.example.brightbeat.data

import java.io.Serializable

data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val frequency: String = "daily", // daily or weekly
    val createdAt: Long = System.currentTimeMillis(),
    var isCompleted: Boolean = false,
    var streak: Int = 0,
    var lastCompleted: Long = 0L
) : Serializable