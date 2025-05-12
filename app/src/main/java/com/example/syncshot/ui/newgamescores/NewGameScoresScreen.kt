package com.example.syncshot.ui.newgamescores

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
    // Use the Factory to get the ViewModel instance
    val viewModel: NewGameViewModel = viewModel(factory = NewGameViewModel.Factory(context))

    // Collect StateFlows as State in the Composable
    val playerNames by viewModel.playerNames.collectAsState()
    val strokes by viewModel.strokes.collectAsState()
    val par by viewModel.par.collectAsState()
    val scanStatus by viewModel.scanStatus.collectAsState() // Collect scan status if you want to display it

    // Update ViewModel with the passed arguments (do this once, perhaps in a LaunchedEffect)
    // However, since these are passed as arguments, the ViewModel might be recreated,
    // so setting them here might be acceptable depending on your navigation setup.
    // If the ViewModel persists across destinations, consider updating in a LaunchedEffect
    // triggered by the arguments. For simplicity here, keeping it directly.
    viewModel.updateNumberOfPlayers(numPlayers)
    viewModel.updateGameDate(date)
    viewModel.updateGameLocation(location)

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            Text("Enter Scores:")
            scanStatus?.let { status -> // Display scan status if not null
                Text(status, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(numPlayers) { playerIndex ->
            // Pass the collected state to the composable
            PlayerInputRow(
                playerIndex = playerIndex,
                playerName = playerNames.getOrNull(playerIndex) ?: "Player ${playerIndex + 1}", // Use getOrNull for safety
                playerStrokes = strokes.getOrNull(playerIndex) ?: IntArray(18), // Use getOrNull for safety
                onStrokesChange = { holeIndex, value -> viewModel.updateStrokes(playerIndex, holeIndex, value) }
            )
        }

        item {
            // Pass the collected state to the composable
            ParInputRow(
                parValues = par,
                onParChange = { holeIndex, value -> viewModel.updatePar(holeIndex, value) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.insertGame()
                    // You might want to delay navigation until the game is actually saved
                    // and the ViewModel's state indicates success.
                    // For simplicity, navigating immediately after calling insertGame.
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
    playerName: String, // Receive player name as a parameter
    playerStrokes: IntArray, // Receive strokes as a parameter
    onStrokesChange: (holeIndex: Int, value: Int) -> Unit // Receive a callback for changes
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = playerName, // Use the passed player name
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 0 until 6) {
                HoleInput(
                    holeIndex = holeIndex,
                    strokes = playerStrokes.getOrNull(holeIndex) ?: 0, // Use getOrNull
                    onStrokesChange = { value -> onStrokesChange(holeIndex, value) } // Pass the callback
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 6 until 12) {
                HoleInput(
                    holeIndex = holeIndex,
                    strokes = playerStrokes.getOrNull(holeIndex) ?: 0,
                    onStrokesChange = { value -> onStrokesChange(holeIndex, value) }
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (holeIndex in 12 until 18) {
                HoleInput(
                    holeIndex = holeIndex,
                    strokes = playerStrokes.getOrNull(holeIndex) ?: 0,
                    onStrokesChange = { value -> onStrokesChange(holeIndex, value) }
                )
            }
        }
    }
}

@Composable
fun RowScope.HoleInput(
    holeIndex: Int,
    strokes: Int, // Receive strokes value as a parameter
    onStrokesChange: (value: Int) -> Unit // Receive a callback for changes
){
    // Use the passed strokes value as the initial state
    var strokesText by remember { mutableStateOf(strokes.toString()) }

    TextField(
        value = strokesText,
        onValueChange = { newText ->
            strokesText = newText
            val newStrokes = newText.toIntOrNull() ?: 0
            onStrokesChange(newStrokes) // Call the callback
        },
        label = { Text((holeIndex + 1).toString()) },
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 2.dp),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Suggest numeric keyboard
    )
}

@Composable
fun ParInputRow(
    parValues: IntArray, // Receive par values as a parameter
    onParChange: (holeIndex: Int, value: Int) -> Unit // Receive a callback for changes
) {
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
                ParHoleInput(
                    parIndex = parIndex,
                    par = parValues.getOrNull(parIndex) ?: -1, // Use getOrNull
                    onParChange = { value -> onParChange(parIndex, value) } // Pass the callback
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 6 until 12) {
                ParHoleInput(
                    parIndex = parIndex,
                    par = parValues.getOrNull(parIndex) ?: -1,
                    onParChange = { value -> onParChange(parIndex, value) }
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (parIndex in 12 until 18) {
                ParHoleInput(
                    parIndex = parIndex,
                    par = parValues.getOrNull(parIndex) ?: -1,
                    onParChange = { value -> onParChange(parIndex, value) }
                )
            }
        }
    }
}

@Composable
fun RowScope.ParHoleInput(
    parIndex: Int,
    par: Int, // Receive par value as a parameter
    onParChange: (value: Int) -> Unit // Receive a callback for changes
){
    // Use the passed par value as the initial state
    var parText by remember { mutableStateOf(par.toString()) }
    TextField(
        value = parText,
        onValueChange = { newText ->
            parText = newText
            val newPar = newText.toIntOrNull() ?: 0
            onParChange(newPar) // Call the callback
        },
        label = { Text((parIndex + 1).toString()) },
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 2.dp),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Suggest numeric keyboard
    )
}