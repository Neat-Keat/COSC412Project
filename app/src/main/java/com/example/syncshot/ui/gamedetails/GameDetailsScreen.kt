package com.example.syncshot.ui.gamedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            if (game == null) {
                Text("Loading...")
            } else {
                Text(
                    text = "Game Details",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = "Location: ${game.location}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = "Date: ${game.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = "Scores",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
                for (i in 0 until game.names.size){
                    Text(
                        text = "${game.names[i]}: ${game.finalScores[i]}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(2.dp)
                    )
                }

                // TODO: display scores for each hole for each player
            }
        }
    }
}