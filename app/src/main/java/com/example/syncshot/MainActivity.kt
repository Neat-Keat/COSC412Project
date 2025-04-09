package com.example.syncshot

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.*
import com.example.syncshot.ui.gamelist.GameListScreen
import com.example.syncshot.ui.newgame.NewGameScreen
import com.example.syncshot.ui.theme.SyncShotTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncShotTheme {
                Log.d("SyncShotApp", "SyncShotApp launched") // add this log
                SyncShotApp()
            }
        }
    }
}

@Composable
fun SyncShotApp() {
    val navController = rememberNavController()
    Log.d("SyncShotApp", "Creating NavHost...")

    NavHost(navController, startDestination = "gameList") {
        composable("gameList") {
            GameListScreen(
                onAddManualGame = { Log.d("GameListScreen", "Add manual game tapped") },
                onAddScanGame = { Log.d("GameListScreen", "Add scan game tapped") },
                onGameClick = { game -> Log.d("GameListScreen", "Clicked game ${game.id}") }
            )
        }

    }
}


