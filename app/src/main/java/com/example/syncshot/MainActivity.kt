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
import com.example.syncshot.ui.gamedetails.GameDetailsScreen
import com.example.syncshot.ui.nav.Routes
import com.example.syncshot.ui.newgame.NewGameManualScreen
import com.example.syncshot.ui.newgame.NewGameScanScreen


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

    NavHost(
        navController,
        startDestination = Routes.GameList
    ) {
        composable(Routes.GameList) {
            GameListScreen(
                onAddManualGame = {
                    Log.d("GameListScreen", "Add manual game tapped")
                    navController.navigate(Routes.NewGameManual)
                                  },
                onAddScanGame = {
                    Log.d("GameListScreen", "Add scan game tapped")
                    navController.navigate(Routes.NewGameScan)
                                },
                onGameClick = {
                    game -> navController.navigate("${Routes.GameDetails}/${game.id}") // Navigate to GameDetails, passing the game ID
                    Log.d("GameListScreen", "Clicked game ${game.id}")
                }
            )
        }

        //when navController goes to NewGameManual, it calls NewGameManualScreen()
        composable(Routes.NewGameManual) {
            NewGameManualScreen()
        }

        //when navController goes to NewGameScan, it calls NewGameScanScreen()
        composable(Routes.NewGameScan) {
            NewGameScanScreen()
        }

        //must be passed a gameID, then navigates to GameDetailsScreen(gameID)
        composable(
            route = "${Routes.GameDetails}/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.StringType }) // Define the 'gameId' argument
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            if (gameId != null) {
                GameDetailsScreen(gameId = gameId)
            }
        }
    }
}


