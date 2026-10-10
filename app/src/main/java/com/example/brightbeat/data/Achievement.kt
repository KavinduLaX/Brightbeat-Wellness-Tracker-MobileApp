package com.example.brightbeat.data

import java.io.Serializable

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    var isUnlocked: Boolean = false,
    val requiredStreak: Int = 0,
    val type: String = "" // habit, hydration, etc.
) : Serializable