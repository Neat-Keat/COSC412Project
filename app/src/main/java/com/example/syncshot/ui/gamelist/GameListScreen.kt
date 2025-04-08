package com.example.syncshot.ui.gamelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.syncshot.data.model.Game

@Composable
fun GameListScreen(
    onAddGameClick: () -> Unit = {}
) {
    val owner = LocalViewModelStoreOwner.current
    val context = LocalContext.current
    val viewModel = remember(owner) {
        ViewModelProvider(owner!!, ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application))
            .get(GameListViewModel::class.java)
    }

    val gameList by viewModel.games.collectAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Game List", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(gameList) { game ->
                GameItem(game = game, onDelete = { viewModel.deleteGame(it) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddGameClick, modifier = Modifier.fillMaxWidth()) {
            Text("Add Game")
        }
    }
}

@Composable
fun GameItem(game: Game, onDelete: (Game) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = game.toString())
        TextButton(onClick = { onDelete(game) }) {
            Text("Delete")
        }
    }
}
