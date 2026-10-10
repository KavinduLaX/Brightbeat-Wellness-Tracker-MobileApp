package com.example.brightbeat

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.brightbeat.auth.LoginActivity
import com.example.brightbeat.onboarding.Onboard1
import com.example.brightbeat.utils.SharedPreferencesManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    
    // Permission request launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Permission denied
            Toast.makeText(this, "Notification permission is required for hydration reminders", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before super.onCreate()
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this)
        applyTheme()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Check if onboarding is completed
        if (!sharedPreferencesManager.isOnboardingCompleted()) {
            startActivity(Intent(this, Onboard1::class.java))
            finish()
            return
        }
        
        // Check if user is logged in
        if (!sharedPreferencesManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Request notification permission on Android 13+
        requestNotificationPermission()
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle intent for hydration alarm
        handleHydrationAlarmIntent(intent)
    }
    
    private fun handleHydrationAlarmIntent(intent: Intent) {
        // Check if this intent is for a hydration alarm
        if (intent.action == "com.example.zensteps.HYDRATION_ALARM") {
            // Handle hydration alarm
            // This will be handled by the HydrationAlarmReceiver
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Setup navigation when the activity resumes
        setupNavigation()
    }
    
    private fun setupNavigation() {
        try {
            // Use post to ensure the view is fully created
            findViewById<BottomNavigationView>(R.id.bottom_navigation)?.post {
                try {
                    val navController = findNavController(R.id.nav_host_fragment)
                    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                    bottomNavigationView?.setupWithNavController(navController)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    
    private fun applyTheme() {
        when (sharedPreferencesManager.getTheme()) {
            "red" -> setTheme(R.style.Theme_ZenSteps_RetroOrange)
            "orange" -> setTheme(R.style.Theme_ZenSteps_RetroTeal)
            "yellow" -> setTheme(R.style.Theme_ZenSteps_RetroPurple)
            else -> setTheme(R.style.Theme_ZenSteps)
        }
    }
}