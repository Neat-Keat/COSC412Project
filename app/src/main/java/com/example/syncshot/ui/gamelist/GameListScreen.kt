package com.example.syncshot.ui.gamelist

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syncshot.data.model.Game

@Composable
fun GameListScreen(
    modifier: Modifier = Modifier,
    viewModel: GameListViewModel = viewModel(),
    onGameClick: (Game) -> Unit,
) {
    val gameList by viewModel.games.collectAsState(initial = emptyList())
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.clearError()
        }
    }

    SnackbarHost(snackbarHostState)

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (gameList.isEmpty()) {
            Text(
                text = "No games yet — try adding one!",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                items(gameList) { game ->
                    GameCard(
                        game = game,
                        onClick = { onGameClick(game) },
                        onDelete = { gameToDelete ->
                            viewModel.deleteGame(gameToDelete)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: Game,
    onClick: () -> Unit,
    onDelete: (Game) -> Unit // Add a callback for deletion
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showDialog = true },
                    onTap = { onClick() }
                )
            }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Game ${game.id.take(4)}") // Shortened ID or display name
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Game") },
            text = { Text("Are you sure you want to delete Game ${game.id.take(4)}?") },
            confirmButton = {
                Button(onClick = {
                    onDelete(game) // Call the deletion callback
                    showDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}