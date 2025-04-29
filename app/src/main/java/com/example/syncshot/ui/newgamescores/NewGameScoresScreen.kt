package com.example.syncshot.ui.newgamescores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.syncshot.ui.newgame.NewGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScoresScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: NewGameViewModel = viewModel(factory = NewGameViewModelFactory(context))

    Text("Enter Scores:")

    LazyColumn(modifier = modifier.padding(16.dp)) {

        items(viewModel.numberOfPlayers) { playerIndex ->
            Text("Player ${playerIndex + 1} Name:")
            TextField(
                value = viewModel.playerNames[playerIndex],
                onValueChange = { viewModel.updatePlayerName(playerIndex, it) },
                label = { Text("Player ${playerIndex + 1} Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                for (holeIndex in 0 until 18) {
                    TextField(
                        value = viewModel.strokes[playerIndex][holeIndex].toString(),
                        onValueChange = {
                            val newStrokes = it.toIntOrNull() ?: 0
                            viewModel.updateStrokes(playerIndex, holeIndex, newStrokes)
                        },
                        label = { Text("Hole ${holeIndex + 1} Strokes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                for (parIndex in 0 until 18){
                    TextField(
                        value = viewModel.par[parIndex].toString(),
                        onValueChange = {
                            val newPar = it.toIntOrNull() ?: 0
                            viewModel.updatePar(parIndex, newPar)
                        },
                        label = { Text("Hole ${parIndex+1} Par") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        item{
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