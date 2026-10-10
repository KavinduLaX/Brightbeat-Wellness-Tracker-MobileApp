package com.example.brightbeat.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.brightbeat.MainActivity
import com.example.brightbeat.R
import com.example.brightbeat.utils.SharedPreferencesManager

class LoginActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var guestTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this)
        
        // Check if user is already logged in
        if (sharedPreferencesManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        initViews()
        setClickListeners()
    }
    
    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerTextView = findViewById(R.id.registerTextView)
        guestTextView = findViewById(R.id.guestTextView)
    }
    
    private fun setClickListeners() {
        loginButton.setOnClickListener {
            attemptLogin()
        }
        
        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        guestTextView.setOnClickListener {
            // Continue as guest
            sharedPreferencesManager.setIsLoggedIn(true)
            sharedPreferencesManager.setUserEmail("guest")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    
    private fun attemptLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        
        if (validateInput(email, password)) {
            // In a real app, you would authenticate with a server
            // For this app, we'll just check if the user exists in SharedPreferences
            val savedEmail = sharedPreferencesManager.getUserEmail()
            
            // If no user has registered yet, treat this as first-time registration
            // Otherwise, check if the email matches the registered email
            if (savedEmail == null) {
                // First time user, save their email and log them in
                sharedPreferencesManager.setUserEmail(email)
                sharedPreferencesManager.setIsLoggedIn(true)
                Toast.makeText(this, "Welcome! Account created.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else if (savedEmail == email) {
                // Existing user with correct email
                sharedPreferencesManager.setIsLoggedIn(true)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Email doesn't match registered email
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                emailEditText.error = getString(R.string.email_required)
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailEditText.error = getString(R.string.invalid_email)
                false
            }
            password.isEmpty() -> {
                passwordEditText.error = getString(R.string.password_required)
                false
            }
            password.length < 6 -> {
                passwordEditText.error = getString(R.string.password_too_short)
                false
            }
            else -> true
        }
    }
}