package com.example.brightbeat.ui.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R
import com.example.brightbeat.data.MoodEntry
import com.example.brightbeat.utils.DateTimeUtils

class MoodAdapter(
    private var moodEntries: List<MoodEntry>,
    private val onMoodClick: (MoodEntry) -> Unit,
    private val onMoodDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    
    inner class MoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moodEmojiTextView: TextView = view.findViewById(R.id.moodEmojiTextView)
        val moodNoteTextView: TextView = view.findViewById(R.id.moodNoteTextView)
        val moodDateTextView: TextView = view.findViewById(R.id.moodDateTextView)
        val shareButton: ImageButton = view.findViewById(R.id.shareButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        
        holder.moodEmojiTextView.text = moodEntry.mood
        holder.moodNoteTextView.text = moodEntry.note.ifEmpty { "No note" }
        holder.moodDateTextView.text = DateTimeUtils.formatDateTime(moodEntry.timestamp)
        
        holder.shareButton.setOnClickListener {
            onMoodClick(moodEntry)
        }
        
        holder.deleteButton.setOnClickListener {
            onMoodDelete(moodEntry)
        }
        
        holder.itemView.setOnClickListener {
            onMoodClick(moodEntry)
        }
    }
    
    override fun getItemCount(): Int = moodEntries.size
    
    fun updateMoodEntries(newMoodEntries: List<MoodEntry>) {
        moodEntries = newMoodEntries
        notifyDataSetChanged()
    }
}