package com.example.brightbeat.ui.hydration

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.data.HydrationRecord
import com.example.brightbeat.receivers.HydrationAlarmReceiver
import com.example.brightbeat.repository.HydrationRepository
import com.example.brightbeat.utils.SharedPreferencesManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.button.MaterialButton
import java.util.*

class HydrationFragment : Fragment(), 
    CustomIntakeDialog.OnIntakeAddedListener, 
    SimpleAlarmSettingsDialog.OnAlarmSettingsSavedListener,
    HydrationGoalDialog.OnGoalSetListener {
    
    private lateinit var hydrationRepository: HydrationRepository
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var hydrationHistoryAdapter: HydrationHistoryAdapter
    private lateinit var hydrationLineChart: LineChart
    private lateinit var hydrationProgressTextView: TextView
    private lateinit var drinkNowButton: Button
    private lateinit var snoozeButton: Button
    private lateinit var settingsButton: MaterialButton
    private lateinit var hydrationHistoryRecyclerView: RecyclerView
    private lateinit var historyEmptyState: View
    
    // Hydration goal in ml
    private var dailyHydrationGoal = 2000
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        hydrationRepository = HydrationRepository.getInstance(requireContext())
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        // Load the saved hydration goal
        dailyHydrationGoal = sharedPreferencesManager.getHydrationGoal()
        
        initViews(view)
        setupRecyclerView()
        setClickListeners()
        loadHydrationData()
        setupChart()
    }
    
    private fun initViews(view: View) {
        hydrationLineChart = view.findViewById(R.id.hydrationLineChart)
        hydrationProgressTextView = view.findViewById(R.id.hydrationProgressTextView)
        drinkNowButton = view.findViewById(R.id.drinkNowButton)
        snoozeButton = view.findViewById(R.id.snoozeButton)
        settingsButton = view.findViewById(R.id.settingsButton)
        hydrationHistoryRecyclerView = view.findViewById(R.id.hydrationHistoryRecyclerView)
        historyEmptyState = view.findViewById(R.id.historyEmptyState)
    }
    
    private fun setupRecyclerView() {
        hydrationHistoryAdapter = HydrationHistoryAdapter(
            emptyList(),
            onDeleteClick = { record ->
                deleteHydrationRecord(record)
            }
        )
        hydrationHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        hydrationHistoryRecyclerView.adapter = hydrationHistoryAdapter
    }
    
    private fun setClickListeners() {
        drinkNowButton.setOnClickListener {
            showCustomIntakeDialog()
        }
        
        snoozeButton.setOnClickListener {
            showHydrationGoalDialog()
        }
        
        settingsButton.setOnClickListener {
            showAlarmSettingsDialog()
        }
    }
    
    private fun loadHydrationData() {
        val records = hydrationRepository.getHydrationRecordsForToday()
        hydrationHistoryAdapter.updateHydrationRecords(records)
        
        // Show/hide empty state
        if (records.isEmpty()) {
            historyEmptyState.visibility = View.VISIBLE
            hydrationHistoryRecyclerView.visibility = View.GONE
        } else {
            historyEmptyState.visibility = View.GONE
            hydrationHistoryRecyclerView.visibility = View.VISIBLE
        }
        
        updateProgressText()
        updateChart()
        
        // Check if goal is reached
        checkHydrationGoalReached()
    }
    
    private fun checkHydrationGoalReached() {
        val consumed = hydrationRepository.getTotalHydrationForToday()
        if (consumed >= dailyHydrationGoal) {
            // Show goal reached notification
            showGoalReachedNotification()
        }
    }
    
    private fun showGoalReachedNotification() {
        val intent = Intent(requireContext(), HydrationAlarmReceiver::class.java).apply {
            action = "com.example.zensteps.HYDRATION_ALARM"
        }
        requireContext().sendBroadcast(intent)
    }
    
    private fun showCustomIntakeDialog() {
        val dialog = CustomIntakeDialog.newInstance()
        dialog.setOnIntakeAddedListener(this)
        dialog.show(parentFragmentManager, "CustomIntakeDialog")
    }
    
    private fun showHydrationGoalDialog() {
        val dialog = HydrationGoalDialog.newInstance(dailyHydrationGoal)
        dialog.setOnGoalSetListener(this)
        dialog.show(parentFragmentManager, "HydrationGoalDialog")
    }
    
    private fun showAlarmSettingsDialog() {
        // Get current alarm settings from SharedPreferences
        val isAlarmEnabled = sharedPreferencesManager.getHydrationAlarmEnabled()
        val hour = sharedPreferencesManager.getHydrationAlarmHour()
        val minute = sharedPreferencesManager.getHydrationAlarmMinute()
        val interval = sharedPreferencesManager.getHydrationAlarmInterval()
        
        val dialog = SimpleAlarmSettingsDialog.newInstance(
            isAlarmEnabled, hour, minute, interval
        )
        dialog.setOnAlarmSettingsSavedListener(this)
        dialog.show(parentFragmentManager, "SimpleAlarmSettingsDialog")
    }
    
    private fun addHydrationRecord(amount: Int) {
        val record = HydrationRecord(amount = amount)
        hydrationRepository.addHydrationRecord(record)
        Toast.makeText(requireContext(), R.string.water_consumed, Toast.LENGTH_SHORT).show()
        loadHydrationData()
    }
    
    private fun deleteHydrationRecord(record: HydrationRecord) {
        hydrationRepository.deleteHydrationRecord(record.id)
        Toast.makeText(requireContext(), "Hydration record deleted", Toast.LENGTH_SHORT).show()
        loadHydrationData()
    }
    
    private fun updateProgressText() {
        val consumed = hydrationRepository.getTotalHydrationForToday()
        hydrationProgressTextView.text = "${consumed}ml of ${dailyHydrationGoal}ml"
    }
    
    private fun setupChart() {
        hydrationLineChart.description.isEnabled = false
        hydrationLineChart.legend.isEnabled = false
        hydrationLineChart.axisRight.isEnabled = false
        hydrationLineChart.xAxis.isEnabled = false
        
        // Set up chart appearance
        hydrationLineChart.setTouchEnabled(true)
        hydrationLineChart.isDragEnabled = true
        hydrationLineChart.setScaleEnabled(true)
        hydrationLineChart.setPinchZoom(true)
        
        // Set chart background
        hydrationLineChart.setBackgroundColor(requireContext().getColor(R.color.surface_card_vintage))
    }
    
    private fun updateChart() {
        val records = hydrationRepository.getHydrationRecordsForToday()
        
        // Create data points for the line chart
        val entries = mutableListOf<Entry>()
        var cumulativeAmount = 0f
        
        // Add initial point at 0
        entries.add(Entry(0f, 0f))
        
        // Add data points for each record
        for ((index, record) in records.withIndex()) {
            cumulativeAmount += record.amount.toFloat()
            entries.add(Entry((index + 1).toFloat(), cumulativeAmount))
        }
        
        // Create dataset
        val dataSet = LineDataSet(entries, "Hydration")
        dataSet.color = requireContext().getColor(R.color.retro_teal)
        dataSet.setCircleColor(requireContext().getColor(R.color.retro_teal))
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = requireContext().getColor(R.color.text_primary_vintage)
        
        // Create line data
        val lineData = LineData(dataSet)
        hydrationLineChart.data = lineData
        
        // Refresh chart
        hydrationLineChart.invalidate()
    }
    
    override fun onIntakeAdded(amount: Int) {
        addHydrationRecord(amount)
    }
    
    override fun onGoalSet(goal: Int) {
        dailyHydrationGoal = goal
        sharedPreferencesManager.setHydrationGoal(goal)
        loadHydrationData() // This will update progress text and chart as well
        Toast.makeText(requireContext(), "Hydration goal updated to ${goal}ml", Toast.LENGTH_SHORT).show()
    }
    
    override fun onAlarmSettingsSaved(
        isEnabled: Boolean,
        hour: Int,
        minute: Int,
        interval: Int
    ) {
        // Save settings to SharedPreferences
        sharedPreferencesManager.setHydrationAlarmEnabled(isEnabled)
        sharedPreferencesManager.setHydrationAlarmHour(hour)
        sharedPreferencesManager.setHydrationAlarmMinute(minute)
        sharedPreferencesManager.setHydrationAlarmInterval(interval)
        
        // Set up or cancel alarms based on settings
        if (isEnabled) {
            scheduleHydrationAlarms()
            Toast.makeText(requireContext(), "Hydration reminders enabled", Toast.LENGTH_SHORT).show()
        } else {
            cancelHydrationAlarms()
            Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun scheduleHydrationAlarms() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Check for alarm scheduling permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Show dialog to request permission
                Toast.makeText(
                    requireContext(),
                    "Please grant permission to schedule exact alarms",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                return
            }
        }
        
        // Create intent for the alarm receiver with custom action
        val intent = Intent(requireContext(), HydrationAlarmReceiver::class.java).apply {
            action = "com.example.zensteps.HYDRATION_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Get alarm time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, sharedPreferencesManager.getHydrationAlarmHour())
        calendar.set(Calendar.MINUTE, sharedPreferencesManager.getHydrationAlarmMinute())
        calendar.set(Calendar.SECOND, 0)
        
        // If the alarm time is in the past, set it for the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        // Set repeating alarm
        val intervalMillis = sharedPreferencesManager.getHydrationAlarmInterval() * 60 * 1000L
        
        try {
            // Use setAlarmClock for more reliable alarms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmInfo, pendingIntent)
            } else {
                // Use setInexactRepeating for better battery optimization on older devices
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    intervalMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Toast.makeText(
                requireContext(),
                "Failed to schedule alarm. Please check permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun cancelHydrationAlarms() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Create intent for the alarm receiver with custom action
        val intent = Intent(requireContext(), HydrationAlarmReceiver::class.java).apply {
            action = "com.example.zensteps.HYDRATION_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
}