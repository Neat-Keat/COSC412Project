package com.example.syncshot.ui.newgamescores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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

@Composable
fun NewGameScoresScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    numPlayers: Int,
    date: String?,
    location: String?
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
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display Player Input Rows
        items(numPlayers) { playerIndex ->
            PlayerInputRow(playerIndex, viewModel)
        }

        // Display Par Input Row
        item {
            ParInputRow(viewModel)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.insertGame()
                    navController.navigate("gameList") {
                        popUpTo("gameList") {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Game")
            }
        }
    }
}

@Composable
fun PlayerInputRow(playerIndex: Int, viewModel: NewGameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = viewModel.playerNames[playerIndex],
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        // First Row (Holes 1-6)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 0 until 6) {
                HoleInput(playerIndex, holeIndex, viewModel)
            }
        }
        // Second Row (Holes 7-12)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 6 until 12) {
                HoleInput(playerIndex, holeIndex, viewModel)
            }
        }
        // Third Row (Holes 13-18)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 12 until 18) {
                HoleInput(playerIndex, holeIndex, viewModel)
            }
        }
    }
}

@Composable
fun RowScope.HoleInput(playerIndex: Int, holeIndex: Int, viewModel: NewGameViewModel){
    var strokesText by remember {
        mutableStateOf(viewModel.strokes[playerIndex][holeIndex].toString())
    }
    TextField(
        value = strokesText,
        onValueChange = { newText ->
            strokesText = newText
            val newStrokes = newText.toIntOrNull() ?: 0
            viewModel.updateStrokes(playerIndex, holeIndex, newStrokes)
        },
        label = { Text((holeIndex + 1).toString()) },
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 2.dp),
        maxLines = 1
    )
}

@Composable
fun ParInputRow(viewModel: NewGameViewModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = "Par",
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        // First Row (Holes 1-6)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 0 until 6) {
                ParHoleInput(parIndex, viewModel)
            }
        }
        // Second Row (Holes 7-12)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 6 until 12) {
                ParHoleInput(parIndex, viewModel)
            }
        }
        // Third Row (Holes 13-18)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 12 until 18) {
                ParHoleInput(parIndex, viewModel)
            }
        }
    }
}

@Composable
fun RowScope.ParHoleInput(parIndex: Int, viewModel: NewGameViewModel){
    var parText by remember { mutableStateOf(viewModel.par[parIndex].toString()) }
    TextField(
        value = parText,
        onValueChange = { newText ->
            parText = newText
            val newPar = newText.toIntOrNull() ?: 0
            viewModel.updatePar(parIndex, newPar)
        },
        label = { Text((parIndex + 1).toString()) },
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 2.dp),
        maxLines = 1
    )
}