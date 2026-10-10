package com.example.brightbeat.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.brightbeat.MainActivity
import com.example.brightbeat.R
import com.example.brightbeat.auth.LoginActivity
import com.example.brightbeat.utils.SharedPreferencesManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var skipButton: TextView
    private lateinit var nextButton: Button
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    
    private val onboardingScreens = listOf(
        OnboardingScreen(
            R.string.onboarding_title_1,
            R.string.onboarding_desc_1,
            R.drawable.ic_launcher_foreground // Replace with actual drawable
        ),
        OnboardingScreen(
            R.string.onboarding_title_2,
            R.string.onboarding_desc_2,
            R.drawable.ic_launcher_foreground // Replace with actual drawable
        ),
        OnboardingScreen(
            R.string.onboarding_title_3,
            R.string.onboarding_desc_3,
            R.drawable.ic_launcher_foreground // Replace with actual drawable
        )
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this)
        
        initViews()
        setupViewPager()
        setClickListeners()
    }
    
    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        skipButton = findViewById(R.id.skipButton)
        nextButton = findViewById(R.id.nextButton)
    }
    
    private fun setupViewPager() {
        val adapter = OnboardingAdapter(onboardingScreens)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
    }
    
    private fun setClickListeners() {
        skipButton.setOnClickListener {
            finishOnboarding()
        }
        
        nextButton.setOnClickListener {
            if (viewPager.currentItem < onboardingScreens.size - 1) {
                viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                nextButton.text = if (position == onboardingScreens.size - 1) {
                    getString(R.string.get_started)
                } else {
                    getString(R.string.next)
                }
                
                skipButton.visibility = if (position == onboardingScreens.size - 1) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        })
    }
    
    private fun finishOnboarding() {
        sharedPreferencesManager.setOnboardingCompleted(true)
        // Check if user is logged in
        if (sharedPreferencesManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}