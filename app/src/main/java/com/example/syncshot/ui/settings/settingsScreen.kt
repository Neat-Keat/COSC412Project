package com.example.syncshot.ui.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.syncshot.ui.theme.ThemeViewModel

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        //toggle app between light and dark mode
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            IconButton(
                onClick = {
                    // Toggle the theme when the button is clicked
                    //TODO: logic is written, just need to actually apply it properly
                    themeViewModel.toggleTheme()
                }
            ) { // Toggle theme here
                Icon(
                    if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }

            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "Toggle Theme"
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            //TODO: other settings stuff
            Text("this is a placeholder")
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            //TODO: even more settings stuff here
            Text("this is a placeholder")
        }
    }
}
