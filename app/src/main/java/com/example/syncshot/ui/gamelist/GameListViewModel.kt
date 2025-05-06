package com.example.syncshot.ui.gamelist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncshot.data.model.Game
import com.example.syncshot.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameListViewModel(application: Application) : AndroidViewModel(application) {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val repository = GameRepository(application)


    init {
        fetchGames()
    }

    init {
        Log.d("GameListViewModel", "ViewModel created, fetching games...")
        fetchGames()
    }


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    private fun fetchGames() {
        viewModelScope.launch {
            try {
                val result = repository.getAllGames()
                _games.value = result
                Log.d("GameListViewModel", "Fetched ${result.size} games")
            } catch (e: Exception) {
                Log.e("GameListViewModel", "Failed to fetch games", e)
            }
        }
    }
    fun clearError() {
        _errorMessage.value = null
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            repository.deleteGame(game)
            fetchGames()
        }
    }
}