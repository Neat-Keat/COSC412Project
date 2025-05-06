package com.example.syncshot.ui.newgamesetup

import androidx.lifecycle.ViewModel

class NewGameSetupViewModel : ViewModel() {
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