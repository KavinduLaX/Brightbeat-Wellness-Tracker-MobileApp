package com.example.brightbeat.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingScreen(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
)