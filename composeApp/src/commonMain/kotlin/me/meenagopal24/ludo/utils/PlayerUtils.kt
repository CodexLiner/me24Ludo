package me.meenagopal24.ludo.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath

val homeOffsets = listOf(0 to 0, 9 to 0, 9 to 9, 0 to 9)

val safeZones = listOf(
    Pair(1, 6),   // Home Color 0
    Pair(6, 2),   // Default Color
    Pair(8, 1),   // Home Color 1
    Pair(12, 6),  // Default Color
    Pair(13, 8),  // Home Color 2
    Pair(8, 12),  // Default Color
    Pair(6, 13),  // Home Color 3
    Pair(2, 8)    // Default Color
)

val safeZoneIndexed = listOf(
    0, 8, 13, 21, 26, 34, 39, 47
)

val winningZones = listOf(
    Pair(6, 6),
    Pair(6, 7),
    Pair(6, 8),
    Pair(7, 6),
    Pair(7, 7),
    Pair(7, 8),
    Pair(8, 6),
    Pair(8, 7),
    Pair(8, 8)
)

fun getHomeOffset(
    startX: Float,
    startY: Float,
    pair: Pair<Int, Int>,
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
fun getAnimatedOffset(
    path: List<Pair<Int, Int>>,
    index: Int,
    boardCellsSize: Float
): State<Offset> {
    val (row, col) = path.getOrNull(index) ?: Pair(0, 0)
    val targetOffset = Offset(col * boardCellsSize + boardCellsSize / 2, row * boardCellsSize + boardCellsSize / 2)

    return animateOffsetAsState(
        targetValue = targetOffset,
        animationSpec = keyframes {
            durationMillis = 180
            targetOffset.copy(y = targetOffset.y - boardCellsSize / 10) at 80 with FastOutLinearInEasing
            targetOffset at 180
        }
    )
}

@Composable
fun getAnimatedActiveState(): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    return alpha
}

@Composable
fun getAnimatedBorderColor(): Color {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedBorderColor by infiniteTransition.animateColor(
        initialValue = Color.White,
        targetValue = listOf(Color.Yellow, Color.Cyan, Color.Magenta, Color.Red).random(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    return animatedBorderColor
}

suspend fun animateTokenMovement(
    tokenPositions: SnapshotStateList<Int>,
    tokenIndex: Int,
    moveAmount: Int,
    playerPathSize: Int,
    updateDelay: Long = 250L
) {
    val startPos = tokenPositions[tokenIndex]
    val endPos = (startPos + moveAmount).coerceAtMost(playerPathSize - 1)
    for (pos in startPos..endPos) {
        tokenPositions[tokenIndex] = pos
        delay(updateDelay)
    }
}



fun detectOverlaps(
    tokenPositions: List<SnapshotStateList<Int>>,
    onCollision: (MutableMap<Pair<Int, Int>, MutableList<Triple<Int, Int, Int>>>) -> Unit
) {
    val allPlayersPositions: List<SnapshotStateList<Pair<Int, Int>>> = listOf(
        getPlayerOnePath(),
        getPlayerTwoPath(),
        getPlayerThreePath(),
        getPlayerFourPath()
    )

    val positionMap = mutableMapOf<Pair<Int, Int>, MutableList<Triple<Int, Int, Int>>>()

    for ((playerIndex, playerTokens) in tokenPositions.withIndex()) {
        val playerPath = allPlayersPositions[playerIndex] // Get the path for the player
        for ((tokenIndex, tokenPosIndex) in playerTokens.withIndex()) {
            if (tokenPosIndex >= 0 && tokenPosIndex in playerPath.indices) {
                val boardPosition = playerPath[tokenPosIndex] // Get actual board position
                val tokenInfo = Triple(playerIndex, tokenIndex, tokenPosIndex) // (Player number, Token number, Position index)
                positionMap.getOrPut(boardPosition) { mutableListOf() }.add(tokenInfo)
            }
        }
    }

    // Filter only positions where collisions occur
    val collisions = positionMap.filter { it.value.size > 1 }.toMutableMap()

    if (collisions.isNotEmpty()) {
        onCollision(collisions)
    }

//    // Debugging logs for each collision
//    for ((position, tokens) in collisions) {
//        val collisionDetails = tokens.joinToString { (player, token, posIndex) ->
//            "[Player: $player, Token: $token, BoardPosIndex: $posIndex]"
//        }
//        Logger.d("Collision detected at $position involving: $collisionDetails")
//    }
}


fun calculateAlpha(
    player: Int,
    token: Int,
    currentPlayerMove: Int,
    colorAlphaState: Float,
    tokenPositions: List<SnapshotStateList<Int>>,
    playerPaths: List<SnapshotStateList<Pair<Int, Int>>>
): Float {
    val tokenPosition = tokenPositions[player][token]

    val isWinningZone = if (tokenPosition != -1) {
        winningZones.contains(playerPaths[player][tokenPosition])
    } else {
        false
    }
    return when {
        tokenPosition == -1 && currentPlayerMove == 6 -> colorAlphaState
        tokenPosition != -1 && currentPlayerMove != -1 && !isWinningZone -> colorAlphaState
        else -> 1f
    }
}


