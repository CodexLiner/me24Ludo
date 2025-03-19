package me.meenagopal24.ludo.utils

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

fun Modifier.progressBorder(
    progress: Float,
    density: Density,
    strokeWidth: Dp = 4.dp,
    strokeColor: Color = Color.Black,
    progressColor: Color = Color.Blue
): Modifier = this.drawBehind {
    val shapePath = Path().apply {
        addRoundRect(
            RoundRect(
                Rect(offset = Offset.Zero, size = Size(size.width, size.height)),
                cornerRadius = CornerRadius(with(density) { 10.dp.toPx() }, with(density) { 10.dp.toPx() })
            )
        )
    }

    val pathMeasure = PathMeasure().apply { setPath(shapePath, forceClosed = false) }
    val pathWithProgress = Path()

    pathMeasure.getSegment(
        startDistance = 0f,
        stopDistance = pathMeasure.length * progress / 100f,
        pathWithProgress,
        startWithMoveTo = true
    )

    drawPath(
        path = shapePath,
        style = Stroke(with(density) { strokeWidth.toPx() }),
        color = strokeColor
    )

    drawPath(
        path = pathWithProgress,
        style = Stroke(with(density) { strokeWidth.toPx() }),
        color = progressColor
    )
}
