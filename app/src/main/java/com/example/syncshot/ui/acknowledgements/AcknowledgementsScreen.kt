package com.example.syncshot.ui.acknowledgements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.syncshot.ui.theme.ThemeViewModel

@Composable
fun AcknowledgementsScreen(themeViewModel: ThemeViewModel, modifier: Modifier = Modifier) {

    Scaffold(modifier = Modifier.padding(16.dp)){
        Column(modifier = modifier.fillMaxSize()){
            Text(text = "Acknowledgements:", modifier = Modifier.padding(it).align(androidx.compose.ui.Alignment.CenterHorizontally), style = MaterialTheme.typography.headlineMedium)

            Text("Game Creation, Scoring Logic", modifier = Modifier.padding(0.dp, 16.dp, 16.dp, 2.dp), style = MaterialTheme.typography.headlineSmall)
            Text("Dilan Pais")

            Text("UI, Storyboarding", modifier = Modifier.padding(0.dp, 16.dp, 16.dp, 2.dp), style = MaterialTheme.typography.headlineSmall)
            Text("Gavin Marshall")

            Text("UI", modifier = Modifier.padding(0.dp, 16.dp, 16.dp, 2.dp), style = MaterialTheme.typography.headlineSmall)
            Text("Ivory Sarpong")

            Text("Image Recognition", modifier = Modifier.padding(0.dp, 16.dp, 16.dp, 2.dp), style = MaterialTheme.typography.headlineSmall)
            Text("Matthew Sotelo")

            //TODO add what Michael did here
            Text("Data Persistence, Navigation", modifier = Modifier.padding(0.dp, 16.dp, 16.dp, 2.dp), style = MaterialTheme.typography.headlineSmall)
            Text("Michael Opoku")

            Text("Database, UI", modifier = Modifier.padding(0.dp, 16.dp, 16.dp, 2.dp), style = MaterialTheme.typography.headlineSmall)
            Text("Nathan Ketterlinus")
        }
    }
}