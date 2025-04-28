package com.example.syncshot

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.*
import com.example.syncshot.ui.gamelist.GameListScreen
import com.example.syncshot.ui.theme.SyncShotTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.syncshot.ui.components.MainScaffold
import com.example.syncshot.ui.gamedetails.GameDetailsScreen
import com.example.syncshot.ui.nav.Routes
import com.example.syncshot.ui.newgame.NewGameManualScreen
import com.example.syncshot.ui.newgame.NewGameScanScreen
import com.example.syncshot.ui.settings.SettingsScreen
import com.example.syncshot.ui.theme.ThemeViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // Create ThemeViewModel here, for passing current app theme from settings
            val themeViewModel: ThemeViewModel = viewModel()

            SyncShotTheme(themeViewModel){
                Log.d("SyncShotApp", "SyncShotApp launched") // add this log
                SyncShotApp(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun SyncShotApp(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    Log.d("SyncShotApp", "Creating NavHost...")

    NavHost(
        navController,
        startDestination = Routes.GameList
    ) {
        composable(Routes.GameList) {
            MainScaffold(navController = navController){ mainScaffoldModifier ->
                GameListScreen(
                    modifier = mainScaffoldModifier, // Pass the padding modifier
                    onAddManualGame = {
                        navController.navigate(Routes.NewGameManual)
                    },
                    onAddScanGame = {
                        navController.navigate(Routes.NewGameScan)
                    },
                    onGameClick = { game ->
                        navController.navigate("${Routes.GameDetails}/${game.id}")
                    },
                    onSettingsClick = {
                        navController.navigate(Routes.Settings)
                    }
                )
            }
        }
        composable(Routes.NewGameManual) {
            MainScaffold(navController = navController){ mainScaffoldModifier ->
                NewGameManualScreen(modifier = mainScaffoldModifier)
            }
        }
        composable(Routes.NewGameScan) {
            MainScaffold(navController = navController){ mainScaffoldModifier ->
                NewGameScanScreen(modifier = mainScaffoldModifier)
            }
        }
        composable(
            route = "${Routes.GameDetails}/{gameId}",
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            if (gameId != null) {
                MainScaffold(navController = navController){ mainScaffoldModifier ->
                    GameDetailsScreen(gameId = gameId, modifier = mainScaffoldModifier)
                }
            }
        }
        composable(Routes.Settings) {
            MainScaffold(navController = navController){ mainScaffoldModifier ->
                SettingsScreen(themeViewModel, modifier = mainScaffoldModifier)
            }
        }
    }
}