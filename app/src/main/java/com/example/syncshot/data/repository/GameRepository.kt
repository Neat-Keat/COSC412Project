package com.example.syncshot.data.repository

import com.example.syncshot.data.AppDatabase
import com.example.syncshot.data.model.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context

class GameRepository(context: Context) {

    private val gameDao = AppDatabase.getDatabase(context).gameDao()

    //These Functions should be called in a VIEWMODEL to update the database!

    fun insertGame(game: Game) {
        CoroutineScope(Dispatchers.IO).launch {
            gameDao.insertGame(game)
        }
    }

    suspend fun getGame(id: String): Game? {
        return gameDao.getGameById(id)
    }

    suspend fun deleteGame(gameToDelete: Game) {
        val rowsDeleted = gameDao.delete(gameToDelete)
        if (rowsDeleted > 0) {
            println("Game deleted successfully.")
        } else {
            println("Game not found or delete failed.")
        }
    }

    suspend fun getAllGames(): List<Game> {
        return gameDao.getAllGames()
    }
}