package com.example.brightbeat.ui.habits

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.brightbeat.R
import com.example.brightbeat.data.Habit

class AddEditHabitDialog : DialogFragment() {
    
    private var habit: Habit? = null
    private var listener: OnHabitSavedListener? = null
    
    interface OnHabitSavedListener {
        fun onHabitSaved(habit: Habit)
    }
    
    companion object {
        private const val ARG_HABIT = "habit"
        
        fun newInstance(habit: Habit? = null): AddEditHabitDialog {
            val dialog = AddEditHabitDialog()
            val args = Bundle()
            args.putSerializable(ARG_HABIT, habit)
            dialog.arguments = args
            return dialog
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        habit = arguments?.getSerializable(ARG_HABIT) as? Habit
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_edit_habit, null)
        
        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val descriptionEditText = view.findViewById<EditText>(R.id.descriptionEditText)
        val frequencySpinner = view.findViewById<Spinner>(R.id.frequencySpinner)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        
        // Setup frequency spinner
        val frequencies = arrayOf("Daily", "Weekly")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner.adapter = adapter
        
        // Populate fields if editing
        habit?.let { habit ->
            nameEditText.setText(habit.name)
            descriptionEditText.setText(habit.description)
            frequencySpinner.setSelection(if (habit.frequency == "weekly") 1 else 0)
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val frequency = if (frequencySpinner.selectedItemPosition == 1) "weekly" else "daily"
            
            if (name.isNotEmpty()) {
                val habitToSave = habit?.copy(
                    name = name,
                    description = description,
                    frequency = frequency
                ) ?: Habit(
                    name = name,
                    description = description,
                    frequency = frequency
                )
                
                listener?.onHabitSaved(habitToSave)
                dismiss()
            }
        }
        
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        return dialog
    }
    
    fun setOnHabitSavedListener(listener: OnHabitSavedListener) {
        this.listener = listener
    }
}