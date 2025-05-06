package com.example.syncshot.ui.newgamesetup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.syncshot.ui.newgame.NewGameSetupViewModel
import com.example.syncshot.ui.newgame.NewGameViewModel
import com.example.syncshot.ui.nav.Routes
import com.example.syncshot.ui.newgamescores.NewGameViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameSetupScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    val newGameViewModel: NewGameViewModel = viewModel(factory = NewGameViewModelFactory(context))
    val setupViewModel: NewGameSetupViewModel = viewModel()

    // Retrieve current values from setupViewModel
    var numberOfPlayersText by remember { mutableStateOf(setupViewModel.numberOfPlayersText) }
    var gameDateText by remember { mutableStateOf(setupViewModel.gameDateText) }
    var gameLocationText by remember { mutableStateOf(setupViewModel.gameLocationText) }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Game Setup:")
        Spacer(modifier = Modifier.height(16.dp))

        // Number of Players
        TextField(
            value = numberOfPlayersText,
            onValueChange = { newText ->
                setupViewModel.updateNumberOfPlayersText(newText)
                numberOfPlayersText = newText // Update local state
                val numPlayers = newText.toIntOrNull() ?: 0
                newGameViewModel.updateNumberOfPlayers(numPlayers)
            },
            label = { Text("Number of Players") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Game Date
        TextField(
            value = gameDateText,
            onValueChange = { newText ->
                setupViewModel.updateGameDateText(newText)
                gameDateText = newText // Update local state
                newGameViewModel.updateGameDate(newText)
            },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Game Location
        TextField(
            value = gameLocationText,
            onValueChange = { newText ->
                setupViewModel.updateGameLocationText(newText)
                gameLocationText = newText // Update local state
                newGameViewModel.updateGameLocation(newText)
            },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Next Button
        Button(
            onClick = {
                val numPlayers = numberOfPlayersText.toIntOrNull() ?: 0
                // Use the helper function from Routes to build the route string
                navController.navigate(Routes.newGameScoresRoute(numPlayers, gameDateText, gameLocationText))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}