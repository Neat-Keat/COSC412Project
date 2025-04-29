package com.example.syncshot.ui.newgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class NewGameSetupViewModel() : ViewModel() {
    var numberOfPlayersText = ""
    var gameDateText = ""
    var gameLocationText = ""

    fun updateNumberOfPlayersText(text: String) {
        numberOfPlayersText = text
    }

    fun updateGameDateText(text: String) {
        gameDateText = text
    }

    fun updateGameLocationText(text: String) {
        gameLocationText = text
    }
}