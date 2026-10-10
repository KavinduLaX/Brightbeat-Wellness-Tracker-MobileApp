package com.example.brightbeat.ui.mood

import android.app.Dialog
import android.os.Bundle
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.brightbeat.R
import com.google.android.material.card.MaterialCardView

class MoodSelectionDialog : DialogFragment() {
    
    private var selectedMood: String = "😊"
    private var listener: ((String) -> Unit)? = null
    
    interface OnMoodSelectedListener {
        fun onMoodSelected(mood: String)
    }
    
    companion object {
        private const val ARG_SELECTED_MOOD = "selected_mood"
        
        fun newInstance(selectedMood: String, listener: (String) -> Unit): MoodSelectionDialog {
            val dialog = MoodSelectionDialog()
            val args = Bundle()
            args.putString(ARG_SELECTED_MOOD, selectedMood)
            dialog.arguments = args
            dialog.listener = listener
            return dialog
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedMood = arguments?.getString(ARG_SELECTED_MOOD) ?: "😊"
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_mood_selection, null)
        
        val gridLayout = view.findViewById<GridLayout>(R.id.moodGridLayout)
        val moodEmojis = arrayOf("😊", "😃", "😐", "😔", "😢", "😡", "😴", "🤩")
        
        // Set up click listeners for each mood item
        for (i in 0 until gridLayout.childCount) {
            val cardView = gridLayout.getChildAt(i) as? MaterialCardView
            cardView?.setOnClickListener {
                val mood = moodEmojis[i]
                listener?.invoke(mood)
                dismiss()
            }
        }
        
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}