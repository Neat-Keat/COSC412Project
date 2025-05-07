package com.example.syncshot.ui.newgamescores

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.syncshot.ui.newgame.NewGameViewModel
import com.example.syncshot.ui.newgamescores.NewGameViewModelFactory

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

    // Collect flows as Compose state
    val playerNames by viewModel.playerNames.collectAsState()
    val strokes by viewModel.strokes.collectAsState()
    val par by viewModel.par.collectAsState()

    viewModel.updateNumberOfPlayers(numPlayers)
    viewModel.updateGameDate(date)
    viewModel.updateGameLocation(location)

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            Text("Enter Scores:")
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(numPlayers) { playerIndex ->
            PlayerInputRow(playerIndex, playerNames, strokes, viewModel)
        }

        item {
            ParInputRow(par, viewModel)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.insertGame()
                    navController.navigate("gameList") {
                        popUpTo("gameList") { inclusive = true }
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
fun PlayerInputRow(
    playerIndex: Int,
    playerNames: Array<String>,
    strokes: Array<IntArray>,
    viewModel: NewGameViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = playerNames[playerIndex],
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 0 until 6) {
                HoleInput(playerIndex, holeIndex, strokes, viewModel)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 6 until 12) {
                HoleInput(playerIndex, holeIndex, strokes, viewModel)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 12 until 18) {
                HoleInput(playerIndex, holeIndex, strokes, viewModel)
            }
        }
    }
}

@Composable
fun RowScope.HoleInput(
    playerIndex: Int,
    holeIndex: Int,
    strokes: Array<IntArray>,
    viewModel: NewGameViewModel
) {
    var strokesText by remember {
        mutableStateOf(strokes[playerIndex][holeIndex].takeIf { it > 0 }?.toString() ?: "")
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
fun ParInputRow(par: IntArray, viewModel: NewGameViewModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = "Par",
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 0 until 6) {
                ParHoleInput(parIndex, par, viewModel)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 6 until 12) {
                ParHoleInput(parIndex, par, viewModel)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 12 until 18) {
                ParHoleInput(parIndex, par, viewModel)
            }
        }
    }
}

@Composable
fun RowScope.ParHoleInput(parIndex: Int, par: IntArray, viewModel: NewGameViewModel) {
    var parText by remember {
        mutableStateOf(par.getOrNull(parIndex)?.takeIf { it > 0 }?.toString() ?: "")
    }

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