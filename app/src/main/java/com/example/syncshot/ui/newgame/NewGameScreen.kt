package com.example.syncshot.ui.newgame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider

@Composable
fun NewGameScreen(
    onGameSaved: () -> Unit = {}
) {
    val owner = LocalViewModelStoreOwner.current
    val context = LocalContext.current
    val viewModel = remember(owner) {
        ViewModelProvider(owner!!, ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application))
            .get(NewGameViewModel::class.java)
    }

    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("New Game", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
                viewModel.setLocation(it)
            },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = {
                date = it
                viewModel.setDate(it)
            },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (viewModel.validate()) {
                    onGameSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Game")
        }
    }
}


