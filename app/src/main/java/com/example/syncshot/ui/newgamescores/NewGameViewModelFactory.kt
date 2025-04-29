package com.example.syncshot.ui.newgamescores

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.syncshot.ui.newgame.NewGameViewModel

class NewGameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewGameViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}