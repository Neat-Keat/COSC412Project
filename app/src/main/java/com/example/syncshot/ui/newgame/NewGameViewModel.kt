package com.example.syncshot.ui.newgame

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.syncshot.data.model.Game

class NewGameViewModel : ViewModel() {

    var location = mutableStateOf("")
        private set

    var date = mutableStateOf("")
        private set

    var names = mutableStateOf(listOf<String>())
        private set

    var holes = mutableStateOf(emptyList<IntArray>())
        private set

    var finalScores = mutableStateOf(IntArray(0))
        private set

    fun setLocation(newLocation: String) {
        location.value = newLocation
    }

    fun setDate(newDate: String) {
        date.value = newDate
    }

    fun setNames(newNames: List<String>) {
        names.value = newNames
    }

    fun setFinalScores(scores: IntArray) {
        finalScores.value = scores
    }

    fun validate(): Boolean {
        return location.value.isNotBlank() && date.value.isNotBlank()
    }

    fun toGame(): Game {
        return Game(
            id = java.util.UUID.randomUUID().toString(), // generates a unique ID
            location = location.value,
            date = date.value,
            names = names.value.toTypedArray(),
            holes = holes.value.toTypedArray(),
            finalScores = finalScores.value
        )
    }

}


