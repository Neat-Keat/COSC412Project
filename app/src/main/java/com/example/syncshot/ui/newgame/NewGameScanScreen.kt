package com.example.syncshot.ui.newgame

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding

@Composable
fun NewGameScanScreen() {
    Scaffold(modifier = Modifier.padding(16.dp)){
        Text(text = "New Game Scan", modifier = Modifier.padding(it), style = MaterialTheme.typography.headlineSmall)
    }
    // Add your UI for game scanning here
    //TODO: request camera functionality from user, then use it to get a picture for tesseract!
}