package me.meenagopal24.ludo.ui.navigation

sealed class Screens(val route : String) {
    data object HomeScreen : Screens("home_screen")
    data object LudoGameScreen : Screens("ludo_game_screen")
}