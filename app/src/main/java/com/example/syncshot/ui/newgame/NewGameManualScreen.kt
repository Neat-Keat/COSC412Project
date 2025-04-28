package com.example.syncshot.ui.newgame

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding

@Composable
fun NewGameManualScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = Modifier.padding(16.dp)){
        Text(text = "New Game Manual", modifier = Modifier.padding(it), style = MaterialTheme.typography.headlineSmall)
    }
    // TODO: Add your UI for manual game input here
}



