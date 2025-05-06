package com.example.syncshot.ui.newgamescores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.syncshot.ui.newgame.NewGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScoresScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    numPlayers: Int,
    date: String,
    location: String
) {
    val context = LocalContext.current
    val viewModel: NewGameViewModel = viewModel(factory = NewGameViewModelFactory(context))

    // Update ViewModel with the passed arguments
    viewModel.updateNumberOfPlayers(numPlayers)
    viewModel.updateGameDate(date)
    viewModel.updateGameLocation(location)

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            Text("Enter Scores:")
        }
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Player columns
                LazyRow(modifier = Modifier.weight(1f)) {
                    items(viewModel.numberOfPlayers) { playerIndex ->
                        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(
                                text = viewModel.playerNames[playerIndex],
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center
                            )
                            for (holeIndex in 0 until 18) {
                                var strokesText by remember {
                                    mutableStateOf(
                                        viewModel.strokes[playerIndex][holeIndex].toString()
                                    )
                                }
                                TextField(
                                    value = strokesText,
                                    onValueChange = { newText ->
                                        strokesText = newText
                                        val newStrokes = newText.toIntOrNull() ?: 0
                                        viewModel.updateStrokes(
                                            playerIndex,
                                            holeIndex,
                                            newStrokes
                                        )
                                    },
                                    label = { Text("Hole ${holeIndex + 1}") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Par column
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = "Par",
                        modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                    for (parIndex in 0 until 18) {
                        var parText by remember { mutableStateOf(viewModel.par[parIndex].toString()) }
                        TextField(
                            value = parText,
                            onValueChange = { newText ->
                                parText = newText
                                val newPar = newText.toIntOrNull() ?: 0
                                viewModel.updatePar(parIndex, newPar)
                            },
                            label = { Text("Hole ${parIndex + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.insertGame()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Game")
            }
        }
    }
}