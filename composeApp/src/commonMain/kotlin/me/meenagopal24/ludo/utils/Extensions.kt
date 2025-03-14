package me.meenagopal24.ludo.utils

import androidx.compose.ui.graphics.Brush

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
