package com.example.brightbeat.ui.hydration

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.brightbeat.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class HydrationGoalDialog : DialogFragment() {
    
    private var listener: OnGoalSetListener? = null
    private var currentGoal: Int = 2000
    
    interface OnGoalSetListener {
        fun onGoalSet(goal: Int)
    }
    
    companion object {
        private const val ARG_CURRENT_GOAL = "current_goal"
        
        fun newInstance(currentGoal: Int): HydrationGoalDialog {
            val dialog = HydrationGoalDialog()
            val args = Bundle()
            args.putInt(ARG_CURRENT_GOAL, currentGoal)
            dialog.arguments = args
            return dialog
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentGoal = it.getInt(ARG_CURRENT_GOAL)
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_hydration_goal, null)
        
        val goalEditText = view.findViewById<EditText>(R.id.goalEditText)
        val quickGoalsChipGroup = view.findViewById<ChipGroup>(R.id.quickGoalsChipGroup)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        
        // Set current goal in the edit text
        goalEditText.setText(currentGoal.toString())
        
        // Set up quick goal chips
        val chips = mapOf(
            R.id.chip1500ml to 1500,
            R.id.chip2000ml to 2000,
            R.id.chip2500ml to 2500,
            R.id.chip3000ml to 3000
        )
        
        // Set click listeners for each chip
        chips.forEach { (chipId, goal) ->
            val chip = view.findViewById<Chip>(chipId)
            chip.setOnClickListener {
                goalEditText.setText(goal.toString())
                // Clear checked state of all chips and set this one as checked
                for ((id, _) in chips) {
                    val c = view.findViewById<Chip>(id)
                    c.isChecked = (id == chipId)
                }
            }
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        
        saveButton.setOnClickListener {
            val goalText = goalEditText.text.toString()
            if (goalText.isNotEmpty()) {
                val goal = goalText.toIntOrNull() ?: 2000
                if (goal > 0) {
                    listener?.onGoalSet(goal)
                    dismiss()
                }
            }
        }
        
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        return dialog
    }
    
    fun setOnGoalSetListener(listener: OnGoalSetListener) {
        this.listener = listener
    }
}