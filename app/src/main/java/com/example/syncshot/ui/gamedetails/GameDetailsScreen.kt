package com.example.syncshot.ui.gamedetails

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding

@Composable
fun GameDetailsScreen(gameId: String) {
    Scaffold(modifier = Modifier.padding(16.dp)){
        Text(text = "Game Details $gameId", modifier = Modifier.padding(it), style = MaterialTheme.typography.headlineSmall)
    }
    // TODO: Add your UI to display game details here
}