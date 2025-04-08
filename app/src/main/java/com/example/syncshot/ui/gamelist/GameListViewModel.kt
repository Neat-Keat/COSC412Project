package com.example.syncshot.ui.gamelist

import android.app.Application
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

    private fun fetchGames() {
        viewModelScope.launch {
            _games.value = repository.getAllGames()
        }
    }

    fun addGame(game: Game) {
        viewModelScope.launch {
            repository.insertGame(game)
            fetchGames()
        }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch {
            repository.deleteGame(game)
            fetchGames()
        }
    }
}


