package com.example.brightbeat.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import com.example.brightbeat.R

class Onboard1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboard1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Set up navigation
        findViewById<TextView>(R.id.skipButton)?.setOnClickListener {
            // Navigate to MainActivity (skip onboarding)
            val intent = Intent(this, com.example.brightbeat.MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        findViewById<Button>(R.id.nextButton)?.setOnClickListener {
            // Navigate to Onboard2
            val intent = Intent(this, Onboard2::class.java)
            startActivity(intent)
            finish()
        }
    }
}