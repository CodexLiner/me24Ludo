package me.meenagopal24.ludo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath
import me.meenagopal24.ludo.theme.AppTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun String.toColor(): Color {
    val hex = removePrefix("#")
    return Color(hex.toLong(16) or (if (hex.length == 6) 0xFF000000 else 0x00000000))
}


@Composable
internal fun App() = AppTheme {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val homeColors = listOf("#eb7434".toColor(), "#09b55c".toColor(), "#adcf17".toColor(), "#9294e8".toColor())
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
            LudoBoardWithJump(Modifier.background(Color.Transparent , RoundedCornerShape(10.dp)), homeColors){ offset, boardCellSize ->

            }

        }
    }
}

@Composable
fun LudoBoardWithJump(
    background: Modifier,
    homeColors: List<Color>,
    radius: Dp = 10.dp,
    ludoBoardSize: Dp = 450.dp,
    setOffSet: (Offset, Float) -> Unit
) {
    val player1Path = remember { getPlayerOnePath() }
    val player2Path = remember { getPlayerTwoPath() }
    val player3Path = remember { getPlayerThreePath() }
    val player4Path = remember { getPlayerFourPath() }

    val player1Index = remember { mutableStateOf(0) }
    val player2Index = remember { mutableStateOf(0) }
    val player3Index = remember { mutableStateOf(0) }
    val player4Index = remember { mutableStateOf(0) }

    val boardCellsSize = with(LocalDensity.current) { (ludoBoardSize / 15).toPx() }

    @Composable
    fun getAnimatedOffset(path: List<Pair<Int, Int>>, index: Int): State<Offset> {
        val (row, col) = path.getOrNull(index) ?: Pair(0, 0)
        return animateOffsetAsState(
            targetValue = Offset(col * boardCellsSize + boardCellsSize / 2, row * boardCellsSize + boardCellsSize / 2),
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
    }

    // **Each Token Gets Its Own Jump Animation**
    val jumpAnim1 = remember { Animatable(0f) }
    val jumpAnim2 = remember { Animatable(0f) }
    val jumpAnim3 = remember { Animatable(0f) }
    val jumpAnim4 = remember { Animatable(0f) }

    val coroutineScope = rememberCoroutineScope()

    suspend fun triggerJump(anim: Animatable<Float, AnimationVector1D>) {
        anim.animateTo(-boardCellsSize / 3, animationSpec = tween(150, easing = FastOutSlowInEasing))
        anim.animateTo(0f, animationSpec = tween(150, easing = FastOutSlowInEasing))
    }

    val player1Offset = getAnimatedOffset(player1Path, player1Index.value)
    val player2Offset = getAnimatedOffset(player2Path, player2Index.value)
    val player3Offset = getAnimatedOffset(player3Path, player3Index.value)
    val player4Offset = getAnimatedOffset(player4Path, player4Index.value)

    Column(modifier = background.wrapContentSize() , horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(ludoBoardSize).clip(RoundedCornerShape(radius))) {
            val startX = (size.width - size.minDimension) / 2
            val startY = (size.height - size.minDimension) / 2
            setOffSet(Offset(startX, startY), boardCellsSize)

            drawLudoBoardGrid(startX, startY, boardCellsSize, Size(boardCellsSize, boardCellsSize))
            drawHomePaths(startX, startY, boardCellsSize, homeColors)
            drawSafeAreas(startX, startY, boardCellsSize, homeColors)
            drawHomeAreas(startX, startY, boardCellsSize, homeColors)
            drawHomeTokens(startX, startY, boardCellsSize, homeColors)
            drawPathArrows(startX, startY, boardCellsSize, homeColors)
            drawHomeEntriesArrows(startX, startY, boardCellsSize, homeColors)

            fun drawAnimatedPlayer(offset: Offset, color: Color) {
                drawCircle(
                    color = color,
                    radius = boardCellsSize / 5f, // Reduce head size to fit
                    center = Offset(offset.x, offset.y - (boardCellsSize / 4f)) // Move head slightly up
                )

                val path = Path().apply {
                    moveTo(offset.x - (boardCellsSize / 4f), offset.y - (boardCellsSize / 8f)) // Left top
                    lineTo(offset.x + (boardCellsSize / 4f), offset.y - (boardCellsSize / 8f)) // Right top
                    lineTo(offset.x + (boardCellsSize / 3.2f), offset.y + (boardCellsSize / 4.2f)) // Bottom right
                    lineTo(offset.x - (boardCellsSize / 3.2f), offset.y + (boardCellsSize / 4.2f)) // Bottom left
                    close()
                }

                drawPath(
                    path = path,
                    color = color
                )

                drawOval(
                    color = color,
                    topLeft = Offset(offset.x - (boardCellsSize / 4f), offset.y + (boardCellsSize / 4.2f) - (boardCellsSize / 10f)),
                    size = Size(boardCellsSize / 2f, boardCellsSize / 8f) // Smaller to fit inside the cell
                )


            }

            val homeColors = listOf(
                Color(0xFFFF9F43), // Orange
                Color(0xFF9B59B6), // Purple
                Color(0xFF1E90FF), // Dodger Blue
                Color(0xFFFFC0CB)  // Pink
            )


            drawAnimatedPlayer(player1Offset.value, homeColors[0])
            drawAnimatedPlayer(player2Offset.value, homeColors[1])
            drawAnimatedPlayer(player3Offset.value, homeColors[2])
            drawAnimatedPlayer(player4Offset.value, homeColors[3])


        }

        Spacer(modifier = Modifier.height(16.dp))

        val haptic = LocalHapticFeedback.current // Get haptic instance

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (player1Index.value < player1Path.size - 1) {
                    player1Index.value++
                } else {
                    player1Index.value = 0 // Reset to start
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Trigger haptic feedback
                coroutineScope.launch { triggerJump(jumpAnim1) }
            }) {
                Text(text = "Move Player 1")
            }

            Button(onClick = {
                if (player2Index.value < player2Path.size - 1) {
                    player2Index.value++
                } else {
                    player2Index.value = 0 // Reset to start
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                coroutineScope.launch { triggerJump(jumpAnim2) }
            }) {
                Text(text = "Move Player 2")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (player3Index.value < player3Path.size - 1) {
                    player3Index.value++
                } else {
                    player3Index.value = 0 // Reset to start
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                coroutineScope.launch { triggerJump(jumpAnim3) }
            }) {
                Text(text = "Move Player 3")
            }

            Button(onClick = {
                if (player4Index.value < player4Path.size - 1) {
                    player4Index.value++
                } else {
                    player4Index.value = 0 // Reset to start
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                coroutineScope.launch { triggerJump(jumpAnim4) }
            }) {
                Text(text = "Move Player 4")
            }
        }
    }
}



fun DrawScope.drawHomeEntriesArrows(
    startX: Float,
    startY: Float,
    boardCellsSize: Float,
    homeColors: List<Color>
) {
    val arrowLength = boardCellsSize / 1.3f
    val arrowHeadSize = boardCellsSize * 0.25f
    val centerOffset = 7.5f * boardCellsSize

    fun drawArrow(start: Offset, end: Offset, color: Color, direction: Direction) {
        drawLine(color, start, end, strokeWidth = 5f)

        val (arrowEndX, arrowEndY) = end
        val (head1, head2) = when (direction) {
            Direction.UP -> listOf(
                end.copy(x = arrowEndX - arrowHeadSize, y = arrowEndY + arrowHeadSize),
                end.copy(x = arrowEndX + arrowHeadSize, y = arrowEndY + arrowHeadSize)
            )
            Direction.DOWN -> listOf(
                end.copy(x = arrowEndX - arrowHeadSize, y = arrowEndY - arrowHeadSize),
                end.copy(x = arrowEndX + arrowHeadSize, y = arrowEndY - arrowHeadSize)
            )
            Direction.LEFT -> listOf(
                end.copy(x = arrowEndX + arrowHeadSize, y = arrowEndY - arrowHeadSize),
                end.copy(x = arrowEndX + arrowHeadSize, y = arrowEndY + arrowHeadSize)
            )
            Direction.RIGHT -> listOf(
                end.copy(x = arrowEndX - arrowHeadSize, y = arrowEndY - arrowHeadSize),
                end.copy(x = arrowEndX - arrowHeadSize, y = arrowEndY + arrowHeadSize)
            )
        }

        drawLine(color, end, head1, strokeWidth = 5f)
        drawLine(color, end, head2, strokeWidth = 5f)
    }

    // Precompute start positions
    val arrowStartX = startX + centerOffset
    val arrowStartY = startY + centerOffset

    drawArrow(
        Offset(arrowStartX, startY),
        Offset(arrowStartX, startY + arrowLength),
        homeColors[1],
        Direction.DOWN
    )
    drawArrow(
        Offset(startX, arrowStartY),
        Offset(startX + arrowLength, arrowStartY),
        homeColors[0],
        Direction.RIGHT
    )
    drawArrow(
        Offset(arrowStartX, startY + 15f * boardCellsSize),
        Offset(arrowStartX, startY + 15f * boardCellsSize - arrowLength),
        homeColors[3],
        Direction.UP
    )
    drawArrow(
        Offset(startX + 15f * boardCellsSize, arrowStartY),
        Offset(startX + 15f * boardCellsSize - arrowLength, arrowStartY),
        homeColors[2],
        Direction.LEFT
    )
}


fun DrawScope.drawPathArrows(startX: Float, startY: Float, boardCellsSize: Float, homeColors: List<Color>) {
    val centerX = startX + 7.5f * boardCellsSize
    val centerY = startY + 7.5f * boardCellsSize

    val arrowWidth = boardCellsSize * 3
    val arrowHeight = boardCellsSize * 3f

    fun drawTriangle(color: Color, x1: Float, y1: Float, x2: Float, y2: Float) {
        Path().apply {
            moveTo(centerX, centerY)
            lineTo(x1, y1)
            lineTo(x2, y2)
            close()
        }.let { drawPath(it, color) }
    }

    val topLeftX = centerX - arrowWidth / 2
    val topLeftY = centerY - arrowHeight / 2
    val topRightX = centerX + arrowWidth / 2
    val bottomLeftY = centerY + arrowHeight / 2

    drawTriangle(homeColors[0], topLeftX, bottomLeftY, topLeftX, topLeftY)
    drawTriangle(homeColors[1], topLeftX, topLeftY, topRightX, topLeftY)
    drawTriangle(homeColors[2], topRightX, topLeftY, topRightX, bottomLeftY)
    drawTriangle(homeColors[3], topLeftX, bottomLeftY, topRightX, bottomLeftY)

}

fun DrawScope.drawHomeTokens(
    startX: Float,
    startY: Float,
    boardCellsSize: Float,
    homeColors: List<Color>,
    borderThickness: Float = 4f,
    paddingFactor: Float = 1f,
    chanceScale: Float = 1.7f
) {
    val homeOffsets = listOf(0 to 0, 9 to 0, 9 to 9, 0 to 9)

    homeOffsets.forEachIndexed { index, (dx, dy) ->
        val homeOffset = Offset(startX + dx * boardCellsSize, startY + dy * boardCellsSize)
        val innerBoxSize = 4 * boardCellsSize
        val innerStart = homeOffset + Offset(boardCellsSize, boardCellsSize)

        // Draw white background and black border
        drawRoundRect(cornerRadius = CornerRadius(50f), color = Color.White, topLeft = innerStart, size = Size(innerBoxSize, innerBoxSize))
        drawRoundRect(cornerRadius = CornerRadius(50f) , color = Color.Black, topLeft = innerStart, size = Size(innerBoxSize, innerBoxSize), style = Stroke(borderThickness))

        // Draw four tokens (chances) inside home
        val padding = boardCellsSize * paddingFactor
        val chanceRadius = boardCellsSize / chanceScale

        val offsets = listOf(
            innerStart + Offset(padding, padding),
            innerStart + Offset(innerBoxSize - padding, padding),
            innerStart + Offset(padding, innerBoxSize - padding),
            innerStart + Offset(innerBoxSize - padding, innerBoxSize - padding)
        )

        offsets.forEach { pos ->
            drawCircle(homeColors[index], radius = chanceRadius, center = pos)
        }
    }
}


fun DrawScope.drawSafeAreas(startX: Float, startY: Float, boardCellsSize: Float, homeColors: List<Color>) {
    for (row in 0 until 15) {
        for (col in 0 until 15) {
            val currentX = startX + col * boardCellsSize
            val currentY = startY + row * boardCellsSize
            val defaultColor = Color.Transparent

            val pathColor = when {
                col == 1 && row == 6 -> homeColors[0]
                col == 6 && row == 2 -> defaultColor
                col == 8 && row == 1 -> homeColors[1]
                col == 12 && row == 6 -> defaultColor
                col == 13 && row == 8 -> homeColors[2]
                col == 8 && row == 12 -> defaultColor
                col == 6 && row == 13 -> homeColors[3]
                col == 2 && row == 8 -> defaultColor
                else -> null
            }

            pathColor?.let {

                drawRect(pathColor, Offset(currentX, currentY), Size(boardCellsSize -2f, boardCellsSize -2f))

                /**
                 * draw star need to understand later
                 */
                val starPath = Path().apply {
                    val center = Offset(currentX + boardCellsSize / 2, currentY + boardCellsSize / 2)
                    val outerRadius = boardCellsSize / 2.5f
                    val innerRadius = outerRadius / 2.5f

                    val angle = (PI / 180) * 72
                    val halfAngle = angle / 2

                    for (i in 0 until 5) {
                        val outerX = (center.x + outerRadius * cos(i * angle)).toFloat()
                        val outerY = (center.y + outerRadius * sin(i * angle)).toFloat()
                        val innerX = (center.x + innerRadius * cos(i * angle + halfAngle)).toFloat()
                        val innerY = (center.y + innerRadius * sin(i * angle + halfAngle)).toFloat()
                        if (i == 0) moveTo(outerX, outerY) else lineTo(outerX, outerY)
                        lineTo(innerX, innerY)
                    }
                    close()
                }
                drawPath(starPath, Color.Black, style = Stroke(2f))

            }

        }
    }
}

fun DrawScope.drawLudoBoardGrid(startX: Float, startY: Float, boardCellsSize: Float, boxSize: Size) {
    for (row in 0 until 15) {
        for (col in 0 until 15) {
            val currentX = startX + col * boardCellsSize
            val currentY = startY + row * boardCellsSize
            drawRect(Color.Black, Offset(currentX, currentY), boxSize, style = Stroke(2f))
        }
    }
}

fun DrawScope.drawHomePaths(startX: Float, startY: Float, boardCellsSize: Float, homeColors: List<Color>) {
    for (row in 0 until 15) {
        for (col in 0 until 15) {
            val currentX = startX + col * boardCellsSize
            val currentY = startY + row * boardCellsSize

            val pathColor = when {
                row == 7 && col in 1..5 -> homeColors[0]
                col == 7 && row in 1..5 -> homeColors[1]
                row == 7 && col in 9..13 -> homeColors[2]
                row in 9..13 && col == 7 -> homeColors[3]
                row in 6..8 && col in 6..8 -> Color.LightGray
                else -> Color.White
            }

            drawRect(pathColor, Offset(currentX, currentY), Size(boardCellsSize -2f, boardCellsSize-2f))
        }
    }
}

fun DrawScope.drawHomeAreas(startX: Float, startY: Float, boardCellsSize: Float, homeColors: List<Color>) {
    val homeSize = Size(6 * boardCellsSize, 6 * boardCellsSize)

    listOf(
        Offset(startX, startY) to homeColors[0],  // Top-left
        Offset(startX + 9 * boardCellsSize, startY) to homeColors[1], // Top-right
        Offset(startX + 9 * boardCellsSize, startY + 9 * boardCellsSize) to homeColors[2], // Bottom-right
        Offset(startX, startY + 9 * boardCellsSize) to homeColors[3] // Bottom-left
    ).forEach { (offset, color) ->
        drawRect(color, offset, homeSize)
    }
}
