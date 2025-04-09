package com.example.syncshot.ui.gamelist

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syncshot.data.model.Game


//Added a feature snackbar
//brief messages to the user at the bottom of the screen, such as success or failure
//Specified the duration for the Snackbar to show using SnackbarDuration.Short
//Added error handling as well
@Composable
fun GameListScreen(
    onAddGameClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: GameListViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)
    )

    val gameList by viewModel.games.collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = remember { mutableStateOf<String?>(null) }

    // Show Snackbar when snackbarMessage changes
    LaunchedEffect(snackbarMessage.value) {
        snackbarMessage.value?.let { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            snackbarMessage.value = null
        }
    }

    // Show error message if there's an error
    val errorMessage by viewModel.error.collectAsState()
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.clearError() // Clear the error after showing
        }
    }

    // State for confirmation dialog
    var gameToDelete by remember { mutableStateOf<Game?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog && gameToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete ${gameToDelete!!.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    gameToDelete?.let {
                        viewModel.deleteGame(it)
                        snackbarMessage.value = "Deleted: ${it.name}"
                    }
                    showDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGameClick) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Game List", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(gameList) { game ->
                    GameItem(game = game, onDelete = { gameToDelete = game; showDialog = true })
                }
            }
        }
    }
}

@Composable
fun GameItem(game: Game, onDelete: (Game) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = game.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Genre: ${game.genre}")
            Text(text = "Released: ${game.releaseYear}")
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onDelete(game) }) {
                Text("Delete")
            }
        }
    }
}