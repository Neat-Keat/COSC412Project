package com.example.syncshot.ui.nav

object Routes {
    const val GameList = "gameList"
    const val NewGameSetup = "newGameSetup"
    // NewGameScores route with arguments
    const val NewGameScores = "newGameScores/{numPlayers}/{date}/{location}"
    const val NewGameScan = "newGameScan"
    const val GameDetails = "gameDetails"
    const val Settings = "settings"
    const val Achievements = "achievements"
    const val Acknowledgements = "acknowledgements"

    // Helper function to create the NewGameScores route with arguments
    fun newGameScoresRoute(numPlayers: Int, date: String, location: String): String {
        return "newGameScores/$numPlayers/$date/$location"
    }
}
