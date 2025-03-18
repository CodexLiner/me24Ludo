package me.meenagopal24.ludo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.meenagopal24.ludo.ui.game.LudoGameScreen
import me.meenagopal24.ludo.ui.home.LudoHomeScreen
import me.meenagopal24.ludo.utils.PLAYER_COUNT

@Composable
fun LudoAppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Screens.HomeScreen.route) {
        composable(Screens.HomeScreen.route) { LudoHomeScreen(navController) }
        composable("{${Screens.LudoGameScreen.route}}/{${PLAYER_COUNT}}") { backStackEntry ->
            val playerCount = backStackEntry.arguments?.getString(PLAYER_COUNT)?.toIntOrNull() ?: 2
            LudoGameScreen(playerCount)
        }
    }
}