package com.example.syncshot.ui.gamelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syncshot.data.model.Game
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*


@Composable
fun GameListScreen(
    viewModel: GameListViewModel = viewModel(),
    onAddManualGame: () -> Unit,
    onAddScanGame: () -> Unit,
    onGameClick: (Game) -> Unit
) {
    val gameList by viewModel.games.collectAsState(initial = emptyList())
    if (gameList.isEmpty()) {
        Text("No games yet — try adding one!", modifier = Modifier.padding(16.dp))
    }

    var showNewGameDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewGameDialog = true }) {
                Text("+")
            }
        },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { /* TODO: Navigate home */ }) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showNewGameDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* TODO: Extras */ }) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "Extras")
                }
                IconButton(onClick = { /* TODO: Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(gameList) { game ->
                GameCard(game = game, onClick = { onGameClick(game) })
            }
        }

        if (showNewGameDialog) {
            NewGameDialog(
                onDismiss = { showNewGameDialog = false },
                onManualClick = {
                    showNewGameDialog = false
                    onAddManualGame()
                },
                onScanClick = {
                    showNewGameDialog = false
                    onAddScanGame()
                }
            )
        }
    }
}

@Composable
fun GameCard(game: Game, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Game ${game.id.take(4)}") // Shortened ID or display name
        }
    }
}

@Composable
fun NewGameDialog(onDismiss: () -> Unit, onManualClick: () -> Unit, onScanClick: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("New Game?", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onScanClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Automatic Scan")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onManualClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Input Manually")
                }
            }
        }
    }
}

