package com.example.brightbeat.ui.mood

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.brightbeat.R

class MoodGridAdapter(
    private val context: Context,
    private val moodEmojis: List<String>,
    private val selectedMood: String,
    private val onMoodSelected: (String) -> Unit
) : BaseAdapter() {
    
    override fun getCount(): Int = moodEmojis.size
    
    override fun getItem(position: Int): String = moodEmojis[position]
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_mood_grid, parent, false)
        
        val moodEmoji = moodEmojis[position]
        val moodTextView = view.findViewById<TextView>(R.id.moodTextView)
        
        moodTextView.text = moodEmoji
        
        if (moodEmoji == selectedMood) {
            moodTextView.setBackgroundResource(R.drawable.mood_selected_background)
        } else {
            moodTextView.setBackgroundResource(R.drawable.mood_normal_background)
        }
        
        view.setOnClickListener {
            onMoodSelected(moodEmoji)
        }
        
        return view
    }
}