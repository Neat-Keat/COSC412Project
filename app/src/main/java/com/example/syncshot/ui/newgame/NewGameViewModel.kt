package com.example.syncshot.ui.newgame

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.syncshot.ocr.ImageRecognition
import com.example.syncshot.ocr.PlayerRound
import com.example.syncshot.data.model.Game
import com.example.syncshot.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class NewGameViewModel(private val context: Context) : ViewModel() {

    private val ocrProcessor = ImageRecognition(context)
    private val repository = GameRepository(context)

    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()

    private val _scanStatus = MutableStateFlow<String?>(null)
    val scanStatus: StateFlow<String?> = _scanStatus.asStateFlow()

    private val _playerNames = MutableStateFlow<Array<String>>(emptyArray())
    val playerNames: StateFlow<Array<String>> = _playerNames.asStateFlow()

    private val _strokes = MutableStateFlow<Array<IntArray>>(emptyArray())
    val strokes: StateFlow<Array<IntArray>> = _strokes.asStateFlow()

    private val _par = MutableStateFlow<IntArray>(IntArray(18) { -1 })
    val par: StateFlow<IntArray> = _par.asStateFlow()

    private val _numberOfPlayers = MutableStateFlow(0)
    val numberOfPlayers: StateFlow<Int> = _numberOfPlayers.asStateFlow()

    var gameDate: String? = null
    var gameLocation: String? = null

    fun setCameraPermissionStatus(isGranted: Boolean) {
        _hasCameraPermission.value = isGranted
    }

    fun updateNumberOfPlayers(count: Int) {
        _numberOfPlayers.value = count
        _playerNames.value = Array(count) { "Player ${it + 1}" }
        _strokes.value = Array(count) { IntArray(18) { 0 } }
    }

    fun updateGameDate(date: String?) {
        gameDate = date
    }

    fun updateGameLocation(location: String?) {
        gameLocation = location
    }

    fun updatePlayerName(index: Int, name: String) {
        val updated = _playerNames.value.copyOf()
        if (index in updated.indices) {
            updated[index] = name
            _playerNames.value = updated
        }
    }

    fun updateStrokes(playerIndex: Int, holeIndex: Int, value: Int) {
        val updated = _strokes.value.map { it.copyOf() }.toTypedArray()
        if (playerIndex in updated.indices && holeIndex in updated[playerIndex].indices) {
            updated[playerIndex][holeIndex] = value
            _strokes.value = updated
        }
    }

    fun updatePar(holeIndex: Int, value: Int) {
        val updated = _par.value.copyOf()
        if (holeIndex in updated.indices) {
            updated[holeIndex] = value
            _par.value = updated
        }
    }

    fun insertGame() {
        viewModelScope.launch {
            val names = _playerNames.value
            val scores = _strokes.value
            val parValues = _par.value

            if (names.any { it.isBlank() } || scores.any { it.all { s -> s <= 0 } }) {
                _scanStatus.value = "Invalid game data. Please review before saving."
                return@launch
            }

            val newGame = Game(
                id = UUID.randomUUID().toString(),
                names = names,
                strokes = scores,
                par = parValues,
                date = gameDate,
                location = gameLocation
            )

            repository.insertGame(newGame)
            _scanStatus.value = "Game saved successfully."
        }
    }

    fun processSelectedImage(imageUri: Uri) {
        _scanStatus.value = "Processing image..."
        _playerNames.value = emptyArray()
        _strokes.value = emptyArray()
        _par.value = IntArray(18) { -1 }
        _numberOfPlayers.value = 0

        viewModelScope.launch {
            try {
                ocrProcessor.processScorecardImage(
                    imageUri = imageUri,
                    onResult = { playerRounds: List<PlayerRound> ->
                        val names = playerRounds.map { it.name }.toTypedArray()
                        val strokes = playerRounds.map { it.scores }.toTypedArray()
                        val par = playerRounds.firstOrNull()?.par ?: IntArray(18) { -1 }

                        _playerNames.value = names
                        _strokes.value = strokes
                        _par.value = par
                        _numberOfPlayers.value = playerRounds.size

                        _scanStatus.value = "Scan complete! Found ${playerRounds.size} players."
                    },
                    onError = { e ->
                        _scanStatus.value = "Scan failed: ${e.message}"
                        Log.e("NewGameViewModel", "OCR Error: ${e.message}", e)
                    }
                )
            } catch (e: Exception) {
                _scanStatus.value = "Scan failed: ${e.message}"
                Log.e("NewGameViewModel", "Error initiating scan: ${e.message}", e)
            }
        }
    }
}