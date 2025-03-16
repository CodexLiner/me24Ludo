package me.meenagopal24.ludo.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Boolean?.ifNotTrue(default: () -> Unit): Boolean? {
    if (this != true) {
        default()
    }
    return this
}

fun Int?.nextPlayer(playersCount: Int, currentMove: Int): Int {
    if (currentMove == 6) {
        return this ?: 0
    }
    if ((this ?: 0) < playersCount - 1) {
        return (this ?: 0) + 1
    }
    return 0
}

fun Color.modify(): Color {
   return this.copy(
        red = (this.red * 0.8f).coerceIn(0f, 1f),
        green = (this.green * 0.8f).coerceIn(0f, 1f),
        blue = (this.blue * 0.8f).coerceIn(0f, 1f)
    )
}