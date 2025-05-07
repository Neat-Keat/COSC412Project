package com.example.syncshot.ui.newgame

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.syncshot.ocr.ImageRecognition // Import your ImageRecognition class
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewGameViewModel(private val context: Context) : ViewModel() {

    // Inject the ImageRecognition dependency
    private val ocrProcessor = ImageRecognition(context)

    // StateFlows to hold the UI state
    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()

    private val _scanStatus = MutableStateFlow<String?>(null)
    val scanStatus: StateFlow<String?> = _scanStatus.asStateFlow()

    // StateFlows to hold the scanned data
    private val _playerNames = MutableStateFlow<Array<String>>(emptyArray())
    val playerNames: StateFlow<Array<String>> = _playerNames.asStateFlow()

    private val _strokes = MutableStateFlow<Array<IntArray>>(emptyArray())
    val strokes: StateFlow<Array<IntArray>> = _strokes.asStateFlow()

    private val _par = MutableStateFlow<IntArray>(IntArray(18) { -1 })
    val par: StateFlow<IntArray> = _par.asStateFlow()

    private val _numberOfPlayers = MutableStateFlow(0)
    val numberOfPlayers: StateFlow<Int> = _numberOfPlayers.asStateFlow()

    // You might also want to store game date and location if collected elsewhere
    // These could be initialized or set by another function if needed before scan
    var gameDate: String? = null
    var gameLocation: String? = null

    fun setCameraPermissionStatus(isGranted: Boolean) {
        _hasCameraPermission.value = isGranted
    }

    /**
     * Processes the image using the OCR engine and updates ViewModel state.
     */
    fun processSelectedImage(imageUri: Uri) {
        _scanStatus.value = "Processing image..."
        // Clear previous scan results while processing
        _playerNames.value = emptyArray()
        _strokes.value = emptyArray()
        _par.value = IntArray(18) { -1 }
        _numberOfPlayers.value = 0

        viewModelScope.launch {
            try {
                // Call the OCR processor
                ocrProcessor.processScorecardImage(
                    imageUri = imageUri,
                    onResult = { playerRounds ->
                        // When OCR is successful, update the ViewModel's state
                        val names = playerRounds.map { it.name }.toTypedArray()
                        val strokes = playerRounds.map { it.scores }.toTypedArray()
                        // Assuming par is the same for all players, take it from the first one
                        val par = playerRounds.firstOrNull()?.par ?: IntArray(18) { -1 }

                        _playerNames.value = names
                        _strokes.value = strokes
                        _par.value = par
                        _numberOfPlayers.value = playerRounds.size // Update number of players

                        _scanStatus.value = "Scan complete! Found ${playerRounds.size} players."
                    },
                    onError = { exception ->
                        // Handle OCR errors
                        _scanStatus.value = "Scan failed: ${exception.message}"
                        Log.e("NewGameViewModel", "OCR Error: ${exception.message}", exception)
                    }
                )
            } catch (e: Exception) {
                // Handle potential errors before the OCR process starts (e.g., invalid URI)
                _scanStatus.value = "Scan failed: ${e.message}"
                Log.e("NewGameViewModel", "Error initiating scan: ${e.message}", e)
            }
        }
    }

    // You might add other functions here for saving game data, etc.
}

// ViewModel Factory (likely already exists based on your ScanScreen code)
// This is needed to pass the Context to the ViewModel
class NewGameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewGameViewModel(context.applicationContext) as T // Use applicationContext to avoid memory leaks
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}