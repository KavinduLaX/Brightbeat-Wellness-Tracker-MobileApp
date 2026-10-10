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

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this)
        
        initViews()
        setClickListeners()
    }
    
    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginTextView = findViewById(R.id.loginTextView)
    }
    
    private fun setClickListeners() {
        registerButton.setOnClickListener {
            attemptRegister()
        }
        
        loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun attemptRegister() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        
        if (validateInput(email, password, confirmPassword)) {
            // Save user data to SharedPreferences
            sharedPreferencesManager.setUserEmail(email)
            sharedPreferencesManager.setIsLoggedIn(true)
            
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    
    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
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
            confirmPassword.isEmpty() -> {
                confirmPasswordEditText.error = getString(R.string.password_required)
                false
            }
            password != confirmPassword -> {
                confirmPasswordEditText.error = getString(R.string.passwords_do_not_match)
                false
            }
            else -> true
        }
    }
}