package me.meenagopal24.ludo.utils

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.delay

fun Boolean?.ifNotTrue(default: () -> Unit): Boolean? {
    if (this != true) {
        default()
    }
    return this
}

fun Int.playerOrders() = when (this) {
    2 -> listOf(1, 3)
    3 -> listOf(3, 2, 1)
    else -> (this - 1 downTo 0).toList()
}

fun Int?.nextPlayer(playersCount: Int, currentMove: Int = Int.MAX_VALUE): Int {
    val order = playersCount.playerOrders()
    val currentPlayer = this ?: order.first()
    if (currentMove == 6 || currentMove == Int.MIN_VALUE) return currentPlayer
    val nextIndex = (order.indexOf(currentPlayer) + 1) % order.size
    return order[nextIndex]
}


fun Color.modify(): Color {
    return this.copy(
        red = (this.red * 0.8f).coerceIn(0f, 1f),
        green = (this.green * 0.8f).coerceIn(0f, 1f),
        blue = (this.blue * 0.8f).coerceIn(0f, 1f)
    )
}

fun Modifier.debounceClickable(
    debounceInterval: Long = 400,
    onClick: () -> Unit
): Modifier = composed {
    var isClickable by remember { mutableStateOf(true) }

    LaunchedEffect(isClickable) {
        if (!isClickable) {
            delay(debounceInterval)
            isClickable = true
        }
    }

    this.clickable {
        if (isClickable) {
            isClickable = false
            onClick()
        }
    }
}


inline fun Modifier.debounceClickable(
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    debounceInterval: Long = 1000,
    crossinline onClick: () -> Unit
): Modifier = composed {
    var isClickable by remember { mutableStateOf(true) }

    LaunchedEffect(isClickable) {
        if (!isClickable) {
            delay(debounceInterval)
            isClickable = true
        }
    }

    this.clickable(
        interactionSource, indication
    ) {
        if (isClickable) {
            isClickable = false
            onClick()
        }
    }
}

inline fun repeatMirroredOrder(playersCount: Int, action: (Int) -> Unit) {
    val order = playersCount.playerOrders()
    order.forEach(action)
}
