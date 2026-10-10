package com.example.brightbeat.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
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
import com.example.brightbeat.utils.DateTimeUtils
import com.example.brightbeat.utils.SharedPreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.*

class MoodCalendarFragment : Fragment(), LogMoodDialog.OnMoodLoggedListener {
    
    private lateinit var moodRepository: MoodRepository
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateTextView: TextView
    private lateinit var calendarMoodRecyclerView: RecyclerView
    private lateinit var addMoodFab: ExtendedFloatingActionButton
    private lateinit var emptyStateLayout: View
    private lateinit var logMoodButton: View
    private lateinit var listViewButton: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_calendar, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        moodRepository = MoodRepository.getInstance(requireContext())
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        initViews(view)
        setupRecyclerView()
        setClickListeners()
        loadMoodEntriesForSelectedDate()
    }
    
    private fun initViews(view: View) {
        calendarView = view.findViewById(R.id.calendarView)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)
        calendarMoodRecyclerView = view.findViewById(R.id.calendarMoodRecyclerView)
        addMoodFab = view.findViewById(R.id.addMoodFab)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        logMoodButton = view.findViewById(R.id.logMoodButton)
        listViewButton = view.findViewById(R.id.listViewButton)
        
        // Set initial selected date text
        val selectedDate = Date(calendarView.date)
        selectedDateTextView.text = DateTimeUtils.formatDate(selectedDate.time)
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
        
        calendarMoodRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        calendarMoodRecyclerView.adapter = moodAdapter
    }
    
    private fun setClickListeners() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDateTextView.text = DateTimeUtils.formatDate(calendar.timeInMillis)
            loadMoodEntriesForSelectedDate()
        }
        
        addMoodFab.setOnClickListener {
            showLogMoodDialog()
        }
        
        logMoodButton.setOnClickListener {
            showLogMoodDialog()
        }
        
        listViewButton.setOnClickListener {
            // Navigate to mood list view
            findNavController().navigate(R.id.action_moodCalendarFragment_to_moodFragment)
        }
    }
    
    private fun loadMoodEntriesForSelectedDate() {
        val selectedDate = Date(calendarView.date)
        val moodEntries = moodRepository.getMoodEntriesForDate(selectedDate.time)
        moodAdapter.updateMoodEntries(moodEntries)
        
        // Show/hide empty state
        if (moodEntries.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            calendarMoodRecyclerView.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            calendarMoodRecyclerView.visibility = View.VISIBLE
        }
    }
    
    private fun showLogMoodDialog() {
        val dialog = LogMoodDialog.newInstance()
        dialog.setOnMoodLoggedListener(this)
        dialog.show(parentFragmentManager, "LogMoodDialog")
    }
    
    private fun shareMoodEntry(moodEntry: MoodEntry) {
        val shareText = "I'm feeling ${moodEntry.mood} on ${DateTimeUtils.formatDate(moodEntry.timestamp)}! #ZenSteps"
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
                loadMoodEntriesForSelectedDate()
                Toast.makeText(requireContext(), R.string.mood_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onMoodLogged(moodEntry: MoodEntry) {
        // Add the timestamp for the selected date
        val selectedDate = Date(calendarView.date)
        val newMoodEntry = moodEntry.copy(timestamp = selectedDate.time)
        
        moodRepository.addMoodEntry(newMoodEntry)
        Toast.makeText(requireContext(), R.string.mood_logged, Toast.LENGTH_SHORT).show()
        loadMoodEntriesForSelectedDate()
    }
}