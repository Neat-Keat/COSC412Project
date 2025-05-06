package com.example.syncshot.ui.gamedetails

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.syncshot.data.model.Game
import com.example.syncshot.data.repository.GameRepository
import kotlinx.coroutines.launch

class GameDetailsViewModel(
    private val gameRepository: GameRepository,
    private val gameId: String
) : ViewModel() {

    var game by mutableStateOf<Game?>(null)
        private set

    init {
        loadGame()
    }

    private fun loadGame() {
        viewModelScope.launch {
            game = gameRepository.getGame(gameId)
        }
    }
}

class GameDetailsViewModelFactory(
    private val context: Context,
    private val gameId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameDetailsViewModel::class.java)) {
            ("UNCHECKED_CAST")
            val gameRepository = GameRepository(context)
            return GameDetailsViewModel(gameRepository, gameId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}