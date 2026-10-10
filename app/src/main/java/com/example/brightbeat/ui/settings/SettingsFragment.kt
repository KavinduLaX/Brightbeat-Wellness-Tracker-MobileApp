package com.example.brightbeat.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.RadioGroup
import android.widget.Toast
import com.example.brightbeat.R
import com.example.brightbeat.auth.LoginActivity
import com.example.brightbeat.utils.SharedPreferencesManager
import java.util.Calendar

class SettingsFragment : Fragment() {
    
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var nameEditText: TextView
    private lateinit var emailEditText: TextView
    private lateinit var saveProfileButton: Button
    private lateinit var themeRadioGroup: RadioGroup
    private lateinit var logoutButton: Button
    private lateinit var appVersionTextView: TextView
    private lateinit var hydrationReminderSwitch: com.google.android.material.switchmaterial.SwitchMaterial
    private lateinit var hydrationSettingsLayout: LinearLayout
    private lateinit var notificationSettingsItem: LinearLayout
    private lateinit var dataBackupItem: LinearLayout
    private lateinit var achievementSettingsItem: LinearLayout
    private lateinit var aboutItem: LinearLayout
    private lateinit var hydrationProgressText: TextView
    private lateinit var hydrationProgressPercent: TextView
    private lateinit var hydrationProgressBar: ProgressBar
    private lateinit var moodEntriesText: TextView
    private lateinit var moodEntriesCount: TextView
    private lateinit var moodProgressBar: ProgressBar
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        initViews(view)
        setClickListeners()
        loadUserData()
        loadThemeSettings()
        loadHydrationSettings()
        setupAdditionalSettings()
        loadProgressData()
    }
    
    private fun initViews(view: View) {
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        saveProfileButton = view.findViewById(R.id.saveProfileButton)
        themeRadioGroup = view.findViewById(R.id.themeRadioGroup)
        logoutButton = view.findViewById(R.id.logoutButton)
        appVersionTextView = view.findViewById(R.id.appVersionTextView)
        hydrationReminderSwitch = view.findViewById(R.id.hydrationReminderSwitch)
        hydrationSettingsLayout = view.findViewById(R.id.hydrationSettingsLayout)
        notificationSettingsItem = view.findViewById(R.id.notificationSettingsItem)
        dataBackupItem = view.findViewById(R.id.dataBackupItem)
        achievementSettingsItem = view.findViewById(R.id.achievementSettingsItem)
        aboutItem = view.findViewById(R.id.aboutItem)
        hydrationProgressText = view.findViewById(R.id.hydrationProgressText)
        hydrationProgressPercent = view.findViewById(R.id.hydrationProgressPercent)
        hydrationProgressBar = view.findViewById(R.id.hydrationProgressBar)
        moodEntriesText = view.findViewById(R.id.moodEntriesText)
        moodEntriesCount = view.findViewById(R.id.moodEntriesCount)
        moodProgressBar = view.findViewById(R.id.moodProgressBar)
    }
    
    private fun setClickListeners() {
        saveProfileButton.setOnClickListener {
            saveProfile()
        }
        
        logoutButton.setOnClickListener {
            sharedPreferencesManager.clearUserData()
            sharedPreferencesManager.setIsLoggedIn(false)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        
        hydrationReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesManager.setHydrationAlarmEnabled(isChecked)
            hydrationSettingsLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        notificationSettingsItem.setOnClickListener {
            // TODO: Implement notification settings
            Toast.makeText(requireContext(), "Notification settings clicked", Toast.LENGTH_SHORT).show()
        }
        
        dataBackupItem.setOnClickListener {
            // TODO: Implement data backup/restore
            Toast.makeText(requireContext(), "Data backup clicked", Toast.LENGTH_SHORT).show()
        }
        
        achievementSettingsItem.setOnClickListener {
            // TODO: Implement achievement settings
            Toast.makeText(requireContext(), "Achievement settings clicked", Toast.LENGTH_SHORT).show()
        }
        
        aboutItem.setOnClickListener {
            // TODO: Implement about section
            Toast.makeText(requireContext(), "About clicked", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadUserData() {
        val email = sharedPreferencesManager.getUserEmail()
        emailEditText.text = email
        
        // Set app version (hardcoded for now)
        appVersionTextView.text = "Version 1.0.0"
    }
    
    private fun loadThemeSettings() {
        when (sharedPreferencesManager.getTheme()) {
            "red" -> themeRadioGroup.check(R.id.lightModeRadioButton)
            "orange" -> themeRadioGroup.check(R.id.darkModeRadioButton)
            // Add more theme options as needed
        }
    }
    
    private fun loadHydrationSettings() {
        val isAlarmEnabled = sharedPreferencesManager.getHydrationAlarmEnabled()
        hydrationReminderSwitch.isChecked = isAlarmEnabled
        hydrationSettingsLayout.visibility = if (isAlarmEnabled) View.VISIBLE else View.GONE
    }
    
    private fun setupAdditionalSettings() {
        // Additional settings are already set up with click listeners
    }
    
    private fun loadProgressData() {
        // Load hydration progress
        loadHydrationProgress()
        
        // Load mood progress
        loadMoodProgress()
    }
    
    private fun loadHydrationProgress() {
        val hydrationRecords = sharedPreferencesManager.getHydrationRecords()
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        
        val todayRecords = hydrationRecords.filter { record ->
            val recordDate = Calendar.getInstance()
            recordDate.timeInMillis = record.timestamp
            recordDate.set(Calendar.HOUR_OF_DAY, 0)
            recordDate.set(Calendar.MINUTE, 0)
            recordDate.set(Calendar.SECOND, 0)
            recordDate.set(Calendar.MILLISECOND, 0)
            
            recordDate.timeInMillis == today.timeInMillis
        }
        
        val totalConsumed = todayRecords.sumOf { it.amount }
        val goal = sharedPreferencesManager.getHydrationGoal()
        val percentage = if (goal > 0) (totalConsumed * 100 / goal) else 0
        
        hydrationProgressText.text = "$totalConsumed/$goal ml today"
        hydrationProgressPercent.text = "$percentage%"
        hydrationProgressBar.progress = percentage.coerceAtMost(100)
    }
    
    private fun loadMoodProgress() {
        val moodEntries = sharedPreferencesManager.getMoodEntries()
        
        // Get entries from the last 7 days
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.timeInMillis
        
        val recentMoodEntries = moodEntries.filter { it.timestamp >= oneWeekAgo }
        val entryCount = recentMoodEntries.size
        
        // For progress bar, we'll use a scale of 0-20 entries as 0-100%
        val maxEntries = 20
        val percentage = if (entryCount >= maxEntries) 100 else (entryCount * 100 / maxEntries)
        
        moodEntriesText.text = "$entryCount mood entries this week"
        moodEntriesCount.text = entryCount.toString()
        moodProgressBar.progress = percentage
    }
    
    private fun saveProfile() {
        // In a real app, you would save the profile data
        // For this app, we'll just show a toast
        Toast.makeText(requireContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show()
    }
}