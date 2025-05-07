// app/src/main/java/com/example/syncshot/ui/newgame/NewGameViewModel.kt
package com.example.syncshot.ui.newgame

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncshot.data.model.Game
import com.example.syncshot.data.repository.GameRepository
import com.example.syncshot.ocr.TesseractHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class NewGameViewModel(context: Context) : ViewModel() {

    private val repository = GameRepository(context)

    // State variables for the game setup
    var numberOfPlayers = 2
    var gameDate: String? = null
    var gameLocation: String? = null

    // State variables for game scores
    var playerNames = Array(numberOfPlayers) { "Player ${it + 1}" }
    var strokes: Array<IntArray> = Array(numberOfPlayers) { IntArray(18) { 0 } }
    var par: IntArray = IntArray(18) { 4 }

    // Function to update the number of players
    fun updateNumberOfPlayers(numPlayers: Int) {
        numberOfPlayers = numPlayers
        playerNames = Array(numberOfPlayers) { "Player ${it + 1}" }
        strokes = Array(numberOfPlayers) { IntArray(18) { 0 } }
    }

    // Function to update the game date
    fun updateGameDate(date: String?) {
        gameDate = date
    }

    // Function to update the game location
    fun updateGameLocation(location: String?) {
        gameLocation = location
    }

    fun updatePar(index: Int, newPar: Int) {
        par[index] = newPar
    }

    fun updateStrokes(playerIndex: Int, holeIndex: Int, newStrokes: Int) {
        strokes[playerIndex][holeIndex] = newStrokes
    }

    fun updatePlayerName(index: Int, newName: String) {
        playerNames[index] = newName
    }

    fun insertGame() {
        viewModelScope.launch {
            val newGame = Game(
                id = UUID.randomUUID().toString(), // Generate a unique ID
                names = playerNames,
                strokes = strokes,
                par = par,
                date = gameDate,
                location = gameLocation
            )
            repository.insertGame(newGame)
        }
    }

    /**
     * New: Call this when your scan screen hands you a Bitmap.
     * Runs OCR, parses ID/title/desc, and immediately inserts a new Game row.
     */
    fun onImageCaptured(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1) OCR the image
            val rawText = TesseractHelper.extractText(bitmap)

            // 2) Split into non‑blank lines
            val lines = rawText
                .lines()
                .map(String::trim)
                .filter { it.isNotEmpty() }

            // 3) Parse ID, title, and description
            val id    = lines.getOrNull(0) ?: UUID.randomUUID().toString()
            val title = lines.getOrNull(1) ?: "Scanned Card"
            val desc  = if (lines.size > 2) lines.drop(2).joinToString(" ") else ""

            // 4) Build and save a minimal Game (we stash title/desc into `location`)
            val scannedGame = Game(
                id       = id,
                names    = playerNames,
                strokes  = strokes,
                par      = par,
                date     = gameDate,
                location = "$title\n$desc"
            )
            repository.insertGame(scannedGame)
        }
    }
}

class GameListViewModel(context: Context) : ViewModel() {
    private val repository = GameRepository(context)
    suspend fun getAllGames(): List<Game> {
        return repository.getAllGames()
    }
}
