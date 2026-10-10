package com.example.brightbeat.ui.mood

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.brightbeat.R
import com.example.brightbeat.data.MoodEntry

class LogMoodDialog : DialogFragment() {
    
    private var listener: OnMoodLoggedListener? = null
    
    interface OnMoodLoggedListener {
        fun onMoodLogged(moodEntry: MoodEntry)
    }
    
    companion object {
        fun newInstance(): LogMoodDialog {
            return LogMoodDialog()
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_log_mood, null)
        
        val moodSelectionTextView = view.findViewById<TextView>(R.id.moodSelectionTextView)
        val noteEditText = view.findViewById<EditText>(R.id.noteEditText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        
        var selectedMood = "😊"
        
        // Setup mood selection
        val moodEmojis = arrayOf("😊", "😃", "😐", "😔", "😢", "😡", "😴", "🤩")
        moodSelectionTextView.text = selectedMood
        
        moodSelectionTextView.setOnClickListener {
            val moodDialog = MoodSelectionDialog.newInstance(selectedMood) { mood ->
                selectedMood = mood
                moodSelectionTextView.text = mood
            }
            moodDialog.show(parentFragmentManager, "MoodSelectionDialog")
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        
        saveButton.setOnClickListener {
            val note = noteEditText.text.toString().trim()
            
            val moodEntry = MoodEntry(
                mood = selectedMood,
                note = note
            )
            
            listener?.onMoodLogged(moodEntry)
            dismiss()
        }
        
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        return dialog
    }
    
    fun setOnMoodLoggedListener(listener: OnMoodLoggedListener) {
        this.listener = listener
    }
}