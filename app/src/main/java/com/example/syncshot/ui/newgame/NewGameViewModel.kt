package com.example.syncshot.ui.newgame

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncshot.ocr.ImageRecognition
import com.example.syncshot.data.model.Game
import com.example.syncshot.data.repository.GameRepository
import com.example.syncshot.ocr.TesseractHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileNotFoundException

class NewGameViewModel(private val context: Context) : ViewModel() {

    // Initialize both OCR processors if needed, or choose one based on input type
    private val ocrImageRecognition = ImageRecognition(context)
    private val ocrTesseractHelper = TesseractHelper

    private val repository = GameRepository(context)

    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()

    private val _scanStatus = MutableStateFlow<String?>(null)
    val scanStatus: StateFlow<String?> = _scanStatus.asStateFlow()

    private val _playerNames = MutableStateFlow<Array<String>>(emptyArray())
    val playerNames: StateFlow<Array<String>> = _playerNames.asStateFlow()

    private val _strokes = MutableStateFlow<Array<IntArray>>(emptyArray())
    val strokes: StateFlow<Array<IntArray>> = _strokes.asStateFlow()

    private val _par = MutableStateFlow<IntArray>(IntArray(18) { 4 }) // assume all holes have default par 4
    val par: StateFlow<IntArray> = _par.asStateFlow()

    private val _numberOfPlayers = MutableStateFlow(0)
    val numberOfPlayers: StateFlow<Int> = _numberOfPlayers.asStateFlow()

    // Use StateFlow for game date and location if you need UI to react to changes
    private val _gameDate = MutableStateFlow<String?>(null)
    val gameDate: StateFlow<String?> = _gameDate.asStateFlow()

    private val _gameLocation = MutableStateFlow<String?>(null)
    val gameLocation: StateFlow<String?> = _gameLocation.asStateFlow()

    fun setCameraPermissionStatus(isGranted: Boolean) {
        _hasCameraPermission.value = isGranted
    }

    fun updateNumberOfPlayers(count: Int) {
        _numberOfPlayers.value = count
        // Reset other state when player count changes
        _playerNames.value = Array(count) { "Player ${it + 1}" }
        _strokes.value = Array(count) { IntArray(18) }
        _par.value = IntArray(18) { -1 } // Reset par as well
    }

    fun updateGameDate(date: String?) {
        _gameDate.value = date
    }

    fun updateGameLocation(location: String?) {
        _gameLocation.value = location
    }

    //unused
//    fun updatePlayerName(index: Int, name: String) {
//        val updated = _playerNames.value.copyOf()
//        if (index in updated.indices) {
//            updated[index] = name
//            _playerNames.value = updated
//        }
//    }

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
            val date = _gameDate.value
            val location = _gameLocation.value

            // Basic validation: ensure there are players and at least one stroke recorded (optional)
            if (names.isEmpty() || scores.isEmpty() || scores.any { it.all { s -> s == 0 } }) {
                _scanStatus.value = "Invalid game data. Please add players and scores before saving."
                return@launch
            }


            val newGame = Game(
                id = UUID.randomUUID().toString(),
                names = names,
                strokes = scores,
                par = parValues,
                date = date,
                location = location
            )

            repository.insertGame(newGame)
            _scanStatus.value = "Game saved successfully."
            // You might want to clear the state after saving
            resetGameState()
        }
    }

    /**
     * Processes an image from a given Uri using ImageRecognition.
     */
    fun processSelectedImage(imageUri: Uri) {
        _scanStatus.value = "Processing image..."
        resetGameState() // Reset state before processing

        Log.d("ImageSelect", "Selected URI: $imageUri")

        val safeUri = copyUriToFile(imageUri, context) ?: run {
            _scanStatus.value = "Failed to read image"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                ocrImageRecognition.processScorecardImage(
                    imageUri = safeUri,
                    onResult = { playerRounds ->
                        if (playerRounds.isNotEmpty()) {
                            _playerNames.value = playerRounds.map { it.name }.toTypedArray()
                            _strokes.value = playerRounds.map { it.scores }.toTypedArray()
                            _par.value =
                                playerRounds.first().par!! // Use par from the first player's round
                            _numberOfPlayers.value = playerRounds.size
                            _scanStatus.value = "Scan complete! Found ${playerRounds.size} players."
                        } else {
                            _scanStatus.value = "Scan complete, but no players found."
                        }

                    },
                    onError = { e ->
                        _scanStatus.value = "Scan failed: ${e.message}"
                        Log.e("NewGameViewModel", "ImageRecognition Error: ${e.message}", e)
                    }
                )
            } catch (e: Exception) {
                _scanStatus.value = "Scan failed: ${e.message}"
                Log.e("NewGameViewModel", "Error initiating ImageRecognition scan: ${e.message}", e)
            } finally {
                // Clean up the temporary file if it was created
                safeUri.path?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
        }
    }

    /**
     * Processes a Bitmap using TesseractHelper.
     */
    fun processImageBitmap(bitmap: Bitmap) {
        _scanStatus.value = "Processing image bitmap..."
        resetGameState() // Reset state before processing

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1) OCR the image
                val rawText = ocrTesseractHelper.extractText(bitmap)

                // 2) Split into non‑blank lines
                val lines = rawText
                    .lines()
                    .map(String::trim)
                    .filter { it.isNotEmpty() }

                // 3) Parse ID, title, and description (using the logic from the second ViewModel)
                val id    = lines.getOrNull(0) ?: UUID.randomUUID().toString()
                val title = lines.getOrNull(1) ?: "Scanned Card"
                val desc  = if (lines.size > 2) lines.drop(2).joinToString(" ") else ""

                // 4) Update state with the parsed information (adapting to StateFlows)
                // TesseractHelper example didn't extract player names, scores, or par
                // so we'll just set the location based on the scanned text.
                _gameLocation.value = "$title\n$desc"
                _scanStatus.value = "Bitmap scan complete. Location updated."

                // If TesseractHelper *could* extract player data, you would update
                // _playerNames, _strokes, _par, and _numberOfPlayers here.
                // For now, we assume it only extracts header information.

            } catch (e: Exception) {
                _scanStatus.value = "Bitmap scan failed: ${e.message}"
                Log.e("NewGameViewModel", "Error during TesseractHelper scan: ${e.message}", e)
            }
        }
    }


    /**
     * Copies a content Uri to a temporary file and returns a FileProvider Uri for it.
     * This is useful when OCR libraries require a file path or a Uri that can be
     * accessed from external storage (which content URIs sometimes cannot).
     */
    private fun copyUriToFile(uri: Uri, context: Context): Uri? {
        return try {
            // Use the application's cache directory for temporary files
            val tempFile = File.createTempFile("scorecard_", ".jpg", context.cacheDir)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw FileNotFoundException("Could not open input stream for URI: $uri")

            // Get a FileProvider Uri for the temporary file
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
        } catch (e: Exception) {
            Log.e("FileCopy", "Failed to copy URI: ${e.message}", e)
            null
        }
    }

    private fun resetGameState() {
        _playerNames.value = emptyArray()
        _strokes.value = emptyArray()
        _par.value = IntArray(18) { -1 }
        _numberOfPlayers.value = 0
        _gameDate.value = null
        _gameLocation.value = null
    }

    // Factory for creating the ViewModel with context
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewGameViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewGameViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}