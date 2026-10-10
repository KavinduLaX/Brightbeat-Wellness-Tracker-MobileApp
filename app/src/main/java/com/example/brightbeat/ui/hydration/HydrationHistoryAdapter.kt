package com.example.brightbeat.ui.hydration

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.data.HydrationRecord
import com.example.brightbeat.utils.DateTimeUtils

class HydrationHistoryAdapter(
    private var hydrationRecords: List<HydrationRecord>,
    private val onDeleteClick: (HydrationRecord) -> Unit
) : RecyclerView.Adapter<HydrationHistoryAdapter.HydrationViewHolder>() {
    
    inner class HydrationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountTextView: TextView = view.findViewById(R.id.amountTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HydrationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hydration_history, parent, false)
        return HydrationViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HydrationViewHolder, position: Int) {
        val record = hydrationRecords[position]
        
        holder.amountTextView.text = "${record.amount} ml"
        holder.timeTextView.text = DateTimeUtils.formatTime(record.timestamp)
        
        holder.deleteButton.setOnClickListener {
            onDeleteClick(record)
        }
    }
    
    override fun getItemCount(): Int = hydrationRecords.size
    
    fun updateHydrationRecords(newRecords: List<HydrationRecord>) {
        hydrationRecords = newRecords
        notifyDataSetChanged()
    }
}