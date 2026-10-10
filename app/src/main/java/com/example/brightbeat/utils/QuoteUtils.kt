package com.example.brightbeat.utils

import android.content.Context
import com.example.brightbeat.R
import java.util.Calendar

class QuoteUtils {
    
    companion object {
        fun getDailyQuote(context: Context, lastQuoteIndex: Int): Pair<String, Int> {
            val quotes = context.resources.getStringArray(R.array.motivational_quotes)
            val calendar = Calendar.getInstance()
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            
            // Use day of year to rotate quotes, or use last index + 1 if provided
            val index = if (lastQuoteIndex >= 0 && lastQuoteIndex < quotes.size - 1) {
                lastQuoteIndex + 1
            } else {
                dayOfYear % quotes.size
            }
            
            return Pair(quotes[index], index)
        }
    }
}