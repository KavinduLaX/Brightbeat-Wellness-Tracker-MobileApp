package com.example.brightbeat.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.data.MoodEntry
import com.example.brightbeat.repository.MoodRepository
import com.example.brightbeat.utils.SharedPreferencesManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MoodFragment : Fragment(), LogMoodDialog.OnMoodLoggedListener {
    
    private lateinit var moodRepository: MoodRepository
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var recentMoodRecyclerView: RecyclerView
    private lateinit var addMoodFab: FloatingActionButton
    private lateinit var listViewButton: MaterialButton
    private lateinit var calendarViewButton: MaterialButton
    private lateinit var moodDistributionChart: PieChart
    private lateinit var totalMoodsTextView: TextView
    private lateinit var mostCommonMoodTextView: TextView
    private lateinit var currentStreakTextView: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        moodRepository = MoodRepository.getInstance(requireContext())
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        initViews(view)
        setupRecyclerView()
        setClickListeners()
        loadMoodEntries()
        updateStatistics()
    }
    
    private fun initViews(view: View) {
        recentMoodRecyclerView = view.findViewById(R.id.recentMoodRecyclerView)
        addMoodFab = view.findViewById(R.id.addMoodFab)

        calendarViewButton = view.findViewById(R.id.calendarViewButton)
        moodDistributionChart = view.findViewById(R.id.moodDistributionChart)
        totalMoodsTextView = view.findViewById(R.id.totalMoodsTextView)
        mostCommonMoodTextView = view.findViewById(R.id.mostCommonMoodTextView)
        currentStreakTextView = view.findViewById(R.id.currentStreakTextView)
        
        setupMoodChart()
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            emptyList(),
            onMoodClick = { moodEntry ->
                // Handle mood entry click (e.g., share)
                shareMoodEntry(moodEntry)
            },
            onMoodDelete = { moodEntry ->
                showDeleteConfirmationDialog(moodEntry)
            }
        )
        
        recentMoodRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentMoodRecyclerView.adapter = moodAdapter
    }
    
    private fun setupMoodChart() {
        moodDistributionChart.description.isEnabled = false
        moodDistributionChart.legend.isEnabled = true
        moodDistributionChart.holeRadius = 40f
        moodDistributionChart.transparentCircleRadius = 45f
        moodDistributionChart.setDrawEntryLabels(false)
        moodDistributionChart.setUsePercentValues(true)
    }
    
    private fun setClickListeners() {
        addMoodFab.setOnClickListener {
            showLogMoodDialog()
        }
        
        calendarViewButton.setOnClickListener {
            // Navigate to calendar view
            findNavController().navigate(R.id.action_moodFragment_to_moodCalendarFragment)
        }
    }
    
    private fun loadMoodEntries() {
        val moodEntries = moodRepository.getAllMoodEntries()
        moodAdapter.updateMoodEntries(moodEntries)
        updateMoodChart(moodEntries)
    }
    
    private fun updateMoodChart(moodEntries: List<MoodEntry>) {
        if (moodEntries.isEmpty()) {
            moodDistributionChart.clear()
            return
        }
        
        // Group moods by type
        val moodCounts = mutableMapOf<String, Int>()
        for (entry in moodEntries) {
            val count = moodCounts.getOrDefault(entry.mood, 0)
            moodCounts[entry.mood] = count + 1
        }
        
        // Create chart entries
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()
        
        for ((mood, count) in moodCounts) {
            entries.add(PieEntry(count.toFloat(), mood))
            // Add different colors for different moods using available colors
            when (mood) {
                "😊" -> colors.add(resources.getColor(R.color.retro_yellow))
                "😄" -> colors.add(resources.getColor(R.color.retro_teal))
                "😐" -> colors.add(resources.getColor(R.color.retro_purple))
                "😔" -> colors.add(resources.getColor(R.color.retro_orange))
                "😢" -> colors.add(resources.getColor(R.color.retro_pink))
                else -> colors.add(resources.getColor(R.color.retro_purple))
            }
        }
        
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.valueTextSize = 12f
        
        val data = PieData(dataSet)
        moodDistributionChart.data = data
        moodDistributionChart.invalidate() // refresh
    }
    
    private fun updateStatistics() {
        val moodEntries = moodRepository.getAllMoodEntries()
        
        // Total entries
        totalMoodsTextView.text = moodEntries.size.toString()
        
        // Most common mood
        if (moodEntries.isNotEmpty()) {
            val moodCounts = mutableMapOf<String, Int>()
            for (entry in moodEntries) {
                val count = moodCounts.getOrDefault(entry.mood, 0)
                moodCounts[entry.mood] = count + 1
            }
            
            val mostCommonMood = moodCounts.maxByOrNull { it.value }?.key ?: "😊"
            mostCommonMoodTextView.text = mostCommonMood
        } else {
            mostCommonMoodTextView.text = "😊"
        }
        
        // Current streak (simplified implementation)
        currentStreakTextView.text = calculateCurrentStreak(moodEntries).toString()
    }
    
    private fun calculateCurrentStreak(moodEntries: List<MoodEntry>): Int {
        if (moodEntries.isEmpty()) return 0
        
        // Sort by timestamp descending
        val sortedEntries = moodEntries.sortedByDescending { it.timestamp }
        var streak = 1
        val today = Date()
        
        // Check if we have an entry for today
        val hasTodayEntry = sortedEntries.any { 
            isSameDay(it.timestamp, today.time) 
        }
        
        if (!hasTodayEntry) {
            // If no entry today, streak is 0
            return 0
        }
        
        // Calculate consecutive days
        var currentDate = today.time
        for (i in 0 until sortedEntries.size - 1) {
            val currentEntry = sortedEntries[i]
            val nextEntry = sortedEntries[i + 1]
            
            if (isSameDay(currentEntry.timestamp, currentDate)) {
                val nextDate = addDays(currentDate, -1)
                if (isSameDay(nextEntry.timestamp, nextDate)) {
                    streak++
                    currentDate = nextDate
                } else {
                    break
                }
            } else {
                break
            }
        }
        
        return streak
    }
    
    private fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = timestamp1
        cal2.timeInMillis = timestamp2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    private fun addDays(timestamp: Long, days: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.timeInMillis
    }
    
    private fun showLogMoodDialog() {
        val dialog = LogMoodDialog.newInstance()
        dialog.setOnMoodLoggedListener(this)
        dialog.show(parentFragmentManager, "LogMoodDialog")
    }
    
    private fun shareMoodEntry(moodEntry: MoodEntry) {
        val shareText = "I'm feeling ${moodEntry.mood} today! #ZenSteps"
        val sendIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
    
    private fun showDeleteConfirmationDialog(moodEntry: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_mood)
            .setMessage(R.string.delete_mood_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                moodRepository.deleteMoodEntry(moodEntry.id)
                loadMoodEntries()
                updateStatistics()
                Toast.makeText(requireContext(), R.string.mood_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onMoodLogged(moodEntry: MoodEntry) {
        moodRepository.addMoodEntry(moodEntry)
        Toast.makeText(requireContext(), R.string.mood_logged, Toast.LENGTH_SHORT).show()
        loadMoodEntries()
        updateStatistics()
    }
}