package com.example.syncshot.ui.gamedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.syncshot.data.model.Game

@Composable
fun GameDetailsScreen(
    game: Game?,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.padding(16.dp)
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (game == null) {
                Text("Loading...")
            } else {
                Text(
                    text = "Game Details",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Location: ${game.location}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Date: ${game.date}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Players",
                    style = MaterialTheme.typography.bodyMedium
                )
                for (i in 0 until game.names.size){
                    Text(
                        text = "${game.names[i]}: ${game.finalScores[i]}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // TODO: display scores for each hole for each player
            }
        }
    }
}