package com.example.syncshot.ui.achievements

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
fun AchievementsScreen(themeViewModel: ThemeViewModel, modifier: Modifier = Modifier) {
    Scaffold(modifier = Modifier.padding(16.dp)){
        // TODO: Add your UI for Achievements here; currently says "coming soon"
        Column(modifier = modifier.fillMaxSize()){
            Text(text = "Achievements", modifier = Modifier.padding(it), style = MaterialTheme.typography.headlineSmall)
            Text("Coming soon!")
        }
    }
}