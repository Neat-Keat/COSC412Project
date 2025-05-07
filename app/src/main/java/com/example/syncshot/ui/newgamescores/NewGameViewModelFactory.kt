package com.example.syncshot.ui.newgamescores // This package might need to be adjusted based on where your factory is

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.syncshot.data.repository.GameRepository // Import your repository if needed for other ViewModels
import com.example.syncshot.ocr.ImageRecognition // Import your ImageRecognition if needed elsewhere
import com.example.syncshot.ui.newgame.NewGameViewModel // Import your NewGameViewModel

class NewGameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is NewGameViewModel
        if (modelClass.isAssignableFrom(NewGameViewModel::class.java)) {
            // Pass the application context to the ViewModel
            // Using applicationContext helps prevent memory leaks
            val applicationContext = context.applicationContext
            return NewGameViewModel(applicationContext) as T
        }
        // If the requested ViewModel is not NewGameViewModel,
        // you might handle other ViewModel types here or throw an exception.
        // For this specific factory intended for NewGameViewModel,
        // throwing an exception is appropriate if it's not the correct class.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}