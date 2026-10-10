package com.example.brightbeat.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brightbeat.R

class OnboardingAdapter(
    private val onboardingScreens: List<OnboardingScreen>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {
    
    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        
        fun bind(onboardingScreen: OnboardingScreen) {
            titleTextView.setText(onboardingScreen.title)
            descriptionTextView.setText(onboardingScreen.description)
            imageView.setImageResource(onboardingScreen.image)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingScreens[position])
    }
    
    override fun getItemCount(): Int = onboardingScreens.size
}