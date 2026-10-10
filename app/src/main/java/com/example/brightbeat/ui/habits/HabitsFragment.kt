package com.example.brightbeat.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.data.Habit
import com.example.brightbeat.repository.HabitRepository
import com.example.brightbeat.utils.SharedPreferencesManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class HabitsFragment : Fragment(), AddEditHabitDialog.OnHabitSavedListener {
    
    private lateinit var habitRepository: HabitRepository
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitsRecyclerView: RecyclerView
    private lateinit var addHabitFab: ExtendedFloatingActionButton
    private lateinit var habitPieChart: PieChart
    private lateinit var habitProgressTextView: TextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        habitRepository = HabitRepository.getInstance(requireContext())
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        initViews(view)
        setupRecyclerView()
        setupChart()
        setClickListeners()
        loadHabits()
    }
    
    private fun initViews(view: View) {
        habitsRecyclerView = view.findViewById(R.id.habitsRecyclerView)
        addHabitFab = view.findViewById(R.id.addHabitFab)
        habitPieChart = view.findViewById(R.id.habitPieChart)
        habitProgressTextView = view.findViewById(R.id.habitProgressTextView)
    }
    
    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            emptyList(),
            onHabitToggle = { habit ->
                habitRepository.toggleHabitCompletion(habit.id)
                loadHabits()
            },
            onHabitClick = { habit ->
                showEditHabitDialog(habit)
            },
            onHabitDelete = { habit ->
                showDeleteConfirmationDialog(habit)
            }
        )
        
        habitsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        habitsRecyclerView.adapter = habitAdapter
    }
    
    private fun setupChart() {
        habitPieChart.description.isEnabled = false
        habitPieChart.legend.isEnabled = false
        habitPieChart.holeRadius = 70f
        habitPieChart.transparentCircleRadius = 75f
    }
    
    private fun setClickListeners() {
        addHabitFab.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        val habits = habitRepository.getAllHabits()
        habitAdapter.updateHabits(habits)
        updateChart()
    }
    
    private fun updateChart() {
        val habits = habitRepository.getAllHabits()
        val totalHabits = habits.size
        val completedHabits = habits.count { it.isCompleted }
        val remainingHabits = totalHabits - completedHabits
        
        // Update progress text
        habitProgressTextView.text = "$completedHabits of $totalHabits habits completed today"
        
        // Update chart
        val entries = listOf(
            PieEntry(completedHabits.toFloat(), "Completed"),
            PieEntry(remainingHabits.toFloat(), "Remaining")
        )
        
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            requireContext().getColor(R.color.success_green),
            requireContext().getColor(R.color.medium_gray)
        )
        dataSet.sliceSpace = 3f
        
        val data = PieData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(requireContext().getColor(R.color.white))
        
        habitPieChart.data = data
        habitPieChart.invalidate() // refresh
    }
    
    private fun showAddHabitDialog() {
        val dialog = AddEditHabitDialog.newInstance()
        dialog.setOnHabitSavedListener(this)
        dialog.show(parentFragmentManager, "AddHabitDialog")
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        val dialog = AddEditHabitDialog.newInstance(habit)
        dialog.setOnHabitSavedListener(this)
        dialog.show(parentFragmentManager, "EditHabitDialog")
    }
    
    private fun showDeleteConfirmationDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_habit)
            .setMessage(getString(R.string.delete_habit_confirmation, habit.name))
            .setPositiveButton(R.string.delete) { _, _ ->
                habitRepository.deleteHabit(habit.id)
                loadHabits()
                Toast.makeText(requireContext(), R.string.habit_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onHabitSaved(habit: Habit) {
        if (habit.id.isEmpty()) {
            // Adding new habit
            habitRepository.addHabit(habit)
            Toast.makeText(requireContext(), R.string.habit_added, Toast.LENGTH_SHORT).show()
        } else {
            // Updating existing habit
            habitRepository.updateHabit(habit)
            Toast.makeText(requireContext(), R.string.habit_updated, Toast.LENGTH_SHORT).show()
        }
        loadHabits()
    }
}