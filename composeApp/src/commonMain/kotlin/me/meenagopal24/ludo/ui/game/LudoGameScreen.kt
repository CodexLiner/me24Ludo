package me.meenagopal24.ludo.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.meenagopal24.ludo.game.Me24LudoBoard
import me.meenagopal24.ludo.toColor

@Composable
fun LudoGameScreen(playerCount: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val homeColors = listOf(
            "#fed80e".toColor(), // Yellow
            "#068e44".toColor(), // Green
            "#fd3221".toColor(), // Red
            "#1da4fe".toColor()  // Blue
        )
        Me24LudoBoard(
            Modifier.background(Color.Transparent, RoundedCornerShape(10.dp)),
            homeColors, players = playerCount
        )
    }
}
