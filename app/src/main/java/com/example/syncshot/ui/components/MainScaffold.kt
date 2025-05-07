package com.example.syncshot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.syncshot.ui.nav.Routes

// This composable is the bottom navigation bar that lets the user go home, create a new game, and go to settings
//it has been hoisted to its own file so that it can be used multiple places, and changing it here updates it globally
@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {
    var showNewGameDialog by remember { mutableStateOf(false) }
    var showExtrasDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate(Routes.GameList)  }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = { showNewGameDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = { showExtrasDialog = true }) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Extras")
                    }
                    IconButton(onClick = { navController.navigate(Routes.Settings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }
    ) { padding ->
        content(Modifier.padding(padding))
    }

    //opens popup to choose between scanning a new game and entering a new game manually
    if (showNewGameDialog) {
        NewGameDialog(
            onDismiss = { showNewGameDialog = false },
            onManualClick = {
                showNewGameDialog = false
                navController.navigate(Routes.NewGameSetup)
            },
            onScanClick = {
                showNewGameDialog = false
                navController.navigate(Routes.NewGameScan)
            }
        )
    }

    //opens popup to navigate to achievements or acknowledgements
    if (showExtrasDialog) {
        ExtrasDialog(
            onDismiss = { showNewGameDialog = false },
            onAchievementsClick = {
                showExtrasDialog = false
                navController.navigate(Routes.Achievements)
            },
            onAcknowledgementsClick = {
                showExtrasDialog = false
                navController.navigate(Routes.Acknowledgements)
            }
        )
    }
}

//defines what the new game popup looks like
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

//defines what the extras popup looks like
@Composable
fun ExtrasDialog(onDismiss: () -> Unit, onAchievementsClick: () -> Unit, onAcknowledgementsClick: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Extras", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAchievementsClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Achievements")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onAcknowledgementsClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Acknowledgements")
                }
            }
        }
    }
}