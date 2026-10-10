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

class CustomIntakeDialog : DialogFragment() {
    
    private var listener: OnIntakeAddedListener? = null
    
    interface OnIntakeAddedListener {
        fun onIntakeAdded(amount: Int)
    }
    
    companion object {
        fun newInstance(): CustomIntakeDialog {
            return CustomIntakeDialog()
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_custom_intake, null)
        
        val amountEditText = view.findViewById<EditText>(R.id.amountEditText)
        val quickAmountsChipGroup = view.findViewById<ChipGroup>(R.id.quickAmountsChipGroup)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        
        // Set up quick amount chips
        val chips = mapOf(
            R.id.chip100ml to 100,
            R.id.chip250ml to 250,
            R.id.chip500ml to 500,
            R.id.chip750ml to 750,
            R.id.chip1000ml to 1000
        )
        
        // Set click listeners for each chip
        chips.forEach { (chipId, amount) ->
            val chip = view.findViewById<Chip>(chipId)
            chip.setOnClickListener {
                amountEditText.setText(amount.toString())
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
        
        addButton.setOnClickListener {
            val amountText = amountEditText.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toIntOrNull() ?: 0
                if (amount > 0) {
                    listener?.onIntakeAdded(amount)
                    dismiss()
                }
            }
        }
        
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        return dialog
    }
    
    fun setOnIntakeAddedListener(listener: OnIntakeAddedListener) {
        this.listener = listener
    }
}