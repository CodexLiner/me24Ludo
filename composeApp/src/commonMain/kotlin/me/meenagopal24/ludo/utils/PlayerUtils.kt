package me.meenagopal24.ludo.utils
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset

val homeOffsets = listOf(0 to 0, 9 to 0, 9 to 9, 0 to 9)

fun getHomeOffset(
    startX : Float,
    startY: Float,
    pair: Pair<Int , Int>,
    boardCellsSize: Float
): List<Offset> {
    val homeOffset = Offset(startX + pair.first * boardCellsSize, startY + pair.second * boardCellsSize)
    val innerBoxSize = 4 * boardCellsSize
    val innerStart = homeOffset + Offset(boardCellsSize, boardCellsSize)

    val paddingFactor = 1f
    val padding = boardCellsSize * paddingFactor
    return listOf(
        innerStart + Offset(padding, padding),
        innerStart + Offset(innerBoxSize - padding, padding),
        innerStart + Offset(padding, innerBoxSize - padding),
        innerStart + Offset(innerBoxSize - padding, innerBoxSize - padding)
    )
}

@Composable
fun getAnimatedOffset(path: List<Pair<Int, Int>>, index: Int, boardCellsSize: Float): State<Offset> {
    val (row, col) = path.getOrNull(index) ?: Pair(0, 0)
    val targetOffset = Offset(col * boardCellsSize + boardCellsSize / 2, row * boardCellsSize + boardCellsSize / 2)

    return animateOffsetAsState(
        targetValue = targetOffset,
        animationSpec = keyframes {
            durationMillis = 120
            targetOffset.copy(y = targetOffset.y - boardCellsSize / 2) at 150 with FastOutLinearInEasing
            targetOffset.copy(y = targetOffset.y + boardCellsSize / 4) at 200 with LinearOutSlowInEasing
            targetOffset.copy(y = targetOffset.y - boardCellsSize / 8) at 300 with FastOutSlowInEasing
            targetOffset at 100
        }
    )
}

