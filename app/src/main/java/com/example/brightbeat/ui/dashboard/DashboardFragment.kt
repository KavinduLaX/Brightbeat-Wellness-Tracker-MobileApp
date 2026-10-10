package com.example.brightbeat.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.repository.HabitRepository
import com.example.brightbeat.repository.HydrationRepository
import com.example.brightbeat.utils.QuoteUtils
import com.example.brightbeat.utils.SharedPreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class DashboardFragment : Fragment() {
    
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var habitRepository: HabitRepository
    private lateinit var hydrationRepository: HydrationRepository
    private lateinit var welcomeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var quoteTextView: TextView
    private lateinit var todayProgressValue: TextView
    private lateinit var streakValue: TextView
    private lateinit var totalHabitsValue: TextView
    private lateinit var addHabitButton: MaterialButton
    private lateinit var fab: FloatingActionButton
    private lateinit var todaysHabitsRecyclerView: RecyclerView
    private lateinit var dashboardHabitAdapter: DashboardHabitAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        habitRepository = HabitRepository.getInstance(requireContext())
        hydrationRepository = HydrationRepository.getInstance(requireContext())
        
        initViews(view)
        setupRecyclerView()
        setClickListeners()
        loadDashboardData()
    }
    
    private fun initViews(view: View) {
        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
        quoteTextView = view.findViewById(R.id.quoteTextView)
        todayProgressValue = view.findViewById(R.id.todayProgressValue)
        streakValue = view.findViewById(R.id.streakValue)
        totalHabitsValue = view.findViewById(R.id.totalHabitsValue)
        addHabitButton = view.findViewById(R.id.addHabitButton)
        fab = view.findViewById(R.id.fab)
        todaysHabitsRecyclerView = view.findViewById(R.id.todaysHabitsRecyclerView)
    }
    
    private fun setupRecyclerView() {
        dashboardHabitAdapter = DashboardHabitAdapter(
            emptyList(),
            onHabitToggle = { habit ->
                habitRepository.toggleHabitCompletion(habit.id)
                loadDashboardData()
            }
        )
        
        todaysHabitsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        todaysHabitsRecyclerView.adapter = dashboardHabitAdapter
    }
    
    private fun setClickListeners() {
        fab.setOnClickListener {
            // Navigate to the mood fragment using the action
            findNavController().navigate(R.id.action_dashboardFragment_to_moodFragment)
        }
        
        addHabitButton.setOnClickListener {
            // Navigate to the habits fragment using the action
            findNavController().navigate(R.id.action_dashboardFragment_to_habitsFragment)
        }
    }
    
    private fun loadDashboardData() {
        // Set current date
        val calendar = Calendar.getInstance()
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> ""
        }
        
        val month = when (calendar.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "January"
            Calendar.FEBRUARY -> "February"
            Calendar.MARCH -> "March"
            Calendar.APRIL -> "April"
            Calendar.MAY -> "May"
            Calendar.JUNE -> "June"
            Calendar.JULY -> "July"
            Calendar.AUGUST -> "August"
            Calendar.SEPTEMBER -> "September"
            Calendar.OCTOBER -> "October"
            Calendar.NOVEMBER -> "November"
            Calendar.DECEMBER -> "December"
            else -> ""
        }
        
        dateTextView.text = "$dayOfWeek, ${month} ${calendar.get(Calendar.DAY_OF_MONTH)}"
        
        // Load motivational quote
        val lastQuoteIndex = sharedPreferencesManager.getLastQuoteIndex()
        val (quote, newIndex) = QuoteUtils.getDailyQuote(requireContext(), lastQuoteIndex)
        quoteTextView.text = quote
        sharedPreferencesManager.setLastQuoteIndex(newIndex)
        
        // Update habit data
        val allHabits = habitRepository.getAllHabits()
        val completedHabits = allHabits.count { it.isCompleted }
        
        // Update stats
        todayProgressValue.text = "$completedHabits/${allHabits.size}"
        streakValue.text = "7" // This would come from habit data in a real implementation
        totalHabitsValue.text = allHabits.size.toString()
        
        // Update habits list
        dashboardHabitAdapter.updateHabits(allHabits)
    }
}