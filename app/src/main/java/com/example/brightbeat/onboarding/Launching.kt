package com.example.brightbeat.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.brightbeat.R

class Launching : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launching)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Add a 3-second delay before navigating to the next screen
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000) // 3 seconds delay
    }
    
    private fun navigateToNextScreen() {
        // Always show onboarding when app starts
        val intent = Intent(this, Onboard1::class.java)
        startActivity(intent)
        
        // Finish this activity so user can't come back to it
        finish()
    }
}