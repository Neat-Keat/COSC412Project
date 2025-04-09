package com.example.syncshot.ui.gamelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncshot.data.model.Game
import com.example.syncshot.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


//Added error handling
// Added a _loading state to indicate when data is being fetched.
// Added an _error state to capture any errors that occur during data operations.
class GameListViewModel(application: Application) : AndroidViewModel(application) {

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val repository = GameRepository(application)

    init {
        fetchGames()
    }

    private fun fetchGames() {
        _loading.value = true
        viewModelScope.launch {
            try {
                _games.value = repository.getAllGames()
            } catch (e: Exception) {
                _error.value = "Failed to fetch games: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addGame(game: Game) {
        viewModelScope.launch {
            try {
                repository.insertGame(game)
                fetchGames()
            } catch (e: Exception) {
                _error.value = "Failed to add game: ${e.message}"
            }
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            try {
                repository.deleteGame(game)
                fetchGames()
            } catch (e: Exception) {
                _error.value = "Failed to delete game: ${e.message}"
            }
        }
    }
}