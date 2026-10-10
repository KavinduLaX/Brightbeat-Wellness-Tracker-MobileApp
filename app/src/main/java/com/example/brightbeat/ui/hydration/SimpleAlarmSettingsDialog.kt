package com.example.brightbeat.ui.hydration

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.brightbeat.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*

class SimpleAlarmSettingsDialog : DialogFragment() {
    
    private var listener: OnAlarmSettingsSavedListener? = null
    private var isAlarmEnabled = false
    private var reminderHour = 9
    private var reminderMinute = 0
    private var intervalMinutes = 60
    
    interface OnAlarmSettingsSavedListener {
        fun onAlarmSettingsSaved(
            isEnabled: Boolean,
            hour: Int,
            minute: Int,
            interval: Int
        )
    }
    
    companion object {
        private const val ARG_IS_ENABLED = "is_enabled"
        private const val ARG_HOUR = "hour"
        private const val ARG_MINUTE = "minute"
        private const val ARG_INTERVAL = "interval"
        
        fun newInstance(
            isEnabled: Boolean,
            hour: Int,
            minute: Int,
            interval: Int
        ): SimpleAlarmSettingsDialog {
            val dialog = SimpleAlarmSettingsDialog()
            val args = Bundle()
            args.putBoolean(ARG_IS_ENABLED, isEnabled)
            args.putInt(ARG_HOUR, hour)
            args.putInt(ARG_MINUTE, minute)
            args.putInt(ARG_INTERVAL, interval)
            dialog.arguments = args
            return dialog
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isAlarmEnabled = it.getBoolean(ARG_IS_ENABLED)
            reminderHour = it.getInt(ARG_HOUR)
            reminderMinute = it.getInt(ARG_MINUTE)
            intervalMinutes = it.getInt(ARG_INTERVAL)
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_alarm_settings_simple, null)
        
        val alarmSwitch = view.findViewById<SwitchMaterial>(R.id.alarmSwitch)
        val timePickerTextView = view.findViewById<TextView>(R.id.timePickerTextView)
        val intervalChipGroup = view.findViewById<ChipGroup>(R.id.intervalChipGroup)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        
        // Initialize views with current values
        alarmSwitch.isChecked = isAlarmEnabled
        updateTimeText(timePickerTextView, reminderHour, reminderMinute)
        updateIntervalChips(intervalChipGroup, intervalMinutes)
        
        // Set up click listeners
        timePickerTextView.setOnClickListener {
            showTimePicker(reminderHour, reminderMinute) { hour, minute ->
                reminderHour = hour
                reminderMinute = minute
                updateTimeText(timePickerTextView, hour, minute)
            }
        }
        
        // Set click listeners for interval chips
        val intervalChips = mapOf(
            R.id.chip30min to 30,
            R.id.chip1hour to 60,
            R.id.chip2hours to 120,
            R.id.chip4hours to 240
        )
        
        intervalChips.forEach { (chipId, interval) ->
            val chip = view.findViewById<Chip>(chipId)
            chip.setOnClickListener {
                intervalMinutes = interval
                // Clear checked state of all chips and set this one as checked
                for ((id, _) in intervalChips) {
                    val c = view.findViewById<Chip>(id)
                    c.isChecked = (id == chipId)
                }
            }
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        
        saveButton.setOnClickListener {
            listener?.onAlarmSettingsSaved(
                alarmSwitch.isChecked,
                reminderHour,
                reminderMinute,
                intervalMinutes
            )
            dismiss()
        }
        
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        return dialog
    }
    
    private fun showTimePicker(
        currentHour: Int,
        currentMinute: Int,
        onTimeSet: (hour: Int, minute: Int) -> Unit
    ) {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                onTimeSet(hour, minute)
            },
            currentHour,
            currentMinute,
            false // is24HourView
        )
        timePickerDialog.show()
    }
    
    private fun updateTimeText(textView: TextView, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        textView.text = timeFormat.format(calendar.time)
    }
    
    private fun updateIntervalChips(chipGroup: ChipGroup, interval: Int) {
        val chipId = when (interval) {
            30 -> R.id.chip30min
            60 -> R.id.chip1hour
            120 -> R.id.chip2hours
            240 -> R.id.chip4hours
            else -> R.id.chip1hour
        }
        
        val chip = chipGroup.findViewById<Chip>(chipId)
        chip?.isChecked = true
    }
    
    fun setOnAlarmSettingsSavedListener(listener: OnAlarmSettingsSavedListener) {
        this.listener = listener
    }
}