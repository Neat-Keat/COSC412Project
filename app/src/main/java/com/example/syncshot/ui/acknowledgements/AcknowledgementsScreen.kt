package com.example.syncshot.ui.acknowledgements

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
        Text(text = "Acknowledgements:", modifier = Modifier.padding(it), style = MaterialTheme.typography.headlineSmall)
    }
    // TODO: Add your UI for Acknowledgements here
}