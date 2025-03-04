package me.meenagopal24.ludo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import me.meenagopal24.ludo.theme.AppTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun App() = AppTheme {
    Column(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val homeColors = listOf(Color.Red, Color.Cyan, Color.Blue, Color.Green)

        LudoBoardMyOwn(Modifier.background(Color.Black), homeColors)
    }
}


@Composable
fun LudoBoardMyOwn(background: Modifier, homeColors: List<Color>) {
    Canvas(background.fillMaxSize()) {
        val ludoBoardSize = size.minDimension * 0.9f
        val boardCellsSize = ludoBoardSize / 15
        val boxSize = Size(boardCellsSize, boardCellsSize)
        val startX = (size.width - ludoBoardSize) / 2
        val startY = (size.height - ludoBoardSize) / 2

        drawLudoBoardGrid(startX, startY, boardCellsSize, boxSize)
        drawHomePaths(startX, startY, boardCellsSize, homeColors)
        drawSafeAreas(startX , startY , boardCellsSize , homeColors)
        drawHomeAreas(startX, startY, boardCellsSize, homeColors)
        drawHomeTokens(startX , startY , boardCellsSize , homeColors)
        drawPathArrows(startX , startY , boardCellsSize , homeColors)
        drawHomeEntriesArrows(startX , startY , boardCellsSize , homeColors)
    }
}

fun DrawScope.drawHomeEntriesArrows(startX: Float, startY: Float, boardCellsSize: Float, homeColors: List<Color>) {



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
    chanceScale: Float = 2.5f
) {
    val homeOffsets = listOf(0 to 0, 9 to 0, 9 to 9, 0 to 9)

    homeOffsets.forEachIndexed { index, (dx, dy) ->
        val homeOffset = Offset(startX + dx * boardCellsSize, startY + dy * boardCellsSize)
        val innerBoxSize = 4 * boardCellsSize
        val innerStart = homeOffset + Offset(boardCellsSize, boardCellsSize)

        // Draw white background and black border
        drawRect(Color.White, topLeft = innerStart, size = Size(innerBoxSize, innerBoxSize))
        drawRect(Color.Black, topLeft = innerStart, size = Size(innerBoxSize, innerBoxSize), style = Stroke(borderThickness))

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

                drawRect(pathColor, Offset(currentX, currentY), Size(boardCellsSize, boardCellsSize))

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
            drawRect(Color.Red, Offset(currentX, currentY), boxSize, style = Stroke(2f))
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
            drawRect(pathColor, Offset(currentX, currentY), Size(boardCellsSize, boardCellsSize))
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


@Composable
fun LudoBoard(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {

        val boardSize = size.minDimension * 0.9f
        val cellSize = boardSize / 15
        val startX = (size.width - boardSize) / 2
        val startY = (size.height - boardSize) / 2

        val homeColors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow)
        val pathColors = listOf(homeColors[0], homeColors[1], homeColors[3], homeColors[2])

        // Draw paths and cell borders
        for (row in 0 until 15) {
            for (col in 0 until 15) {
                val x = startX + col * cellSize
                val y = startY + row * cellSize
                val cellColor = when {
                    row == 7 && col in 1..5 -> homeColors[0]
                    row in 1..5 && col == 7 -> homeColors[1]
                    row == 7 && col in 9..13 -> homeColors[2]
                    row in 9..13 && col == 7 -> homeColors[3]
                    row in 6..8 && col in 6..8 -> Color.White
                    else -> Color.White
                }

                drawRect(cellColor, Offset(x, y), Size(cellSize, cellSize))

                drawRect(Color.Black, Offset(x, y), Size(cellSize, cellSize), style = Stroke(2f))

            }
        }

        // Center Area Triangles
        val centerX = startX + 6 * cellSize
        val centerY = startY + 6 * cellSize
        val centerSquareSize = 3 * cellSize

        drawRect(Color.White, Offset(centerX, centerY), Size(centerSquareSize, centerSquareSize))

        val trianglePaths = listOf(
            Triple(
                homeColors[0],
                centerX + 1.5f * cellSize to centerY,
                centerX + 3 * cellSize to centerY + 1.5f * cellSize
            ), Triple(
                homeColors[1],
                centerX + 1.5f * cellSize to centerY + 3 * cellSize,
                centerX + 3 * cellSize to centerY + 1.5f * cellSize
            ), Triple(
                homeColors[2],
                centerX to centerY + 1.5f * cellSize,
                centerX + 1.5f * cellSize to centerY + 3 * cellSize
            ), Triple(
                homeColors[3],
                centerX to centerY + 1.5f * cellSize,
                centerX + 1.5f * cellSize to centerY
            )
        )

        for ((color, p1, p2) in trianglePaths) {
            val path = Path().apply {
                moveTo(centerX + 1.5f * cellSize, centerY + 1.5f * cellSize)
                lineTo(p1.first, p1.second)
                lineTo(p2.first, p2.second)
                close()
            }
            drawPath(path, color)
        }


        // Safe zones (stars where players cannot be killed) with their respective home colors
        val safeZones = listOf(
            Triple(1, 6, homeColors[0]),  // Red
            Triple(6, 2, Color.Transparent),  // Red
            Triple(8, 1, homeColors[1]),  // Green
            Triple(12, 6, Color.Transparent), // Green
            Triple(2, 8, Color.Transparent),  // Blue
            Triple(6, 13, homeColors[2]), // Blue
            Triple(8, 12, Color.Transparent), // Yellow
            Triple(13, 8, homeColors[3])  // Yellow
        )

        safeZones.forEach { (col, row, homeColor) ->
            val x = startX + col * cellSize
            val y = startY + row * cellSize

            // Draw background rectangle with home color
            drawRect(
                color = homeColor, topLeft = Offset(x, y), size = Size(cellSize, cellSize)
            )

            // Draw Safe Zone Star (Symbol)
            val starPath = Path().apply {
                val center = Offset(x + cellSize / 2, y + cellSize / 2)
                val r = cellSize / 3
                for (i in 0 until 5) {
                    val angle = (i * 144) * (PI / 180)  // Convert degrees to radians
                    val px = (center.x + r * kotlin.math.cos(angle)).toFloat()
                    val py = (center.y + r * kotlin.math.sin(angle)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(starPath, Color.Black)
        }

        // Draw home areas
        homeColors.forEachIndexed { index, color ->
            val (x, y) = when (index) {
                0 -> startX to startY
                1 -> startX + 9 * cellSize to startY
                2 -> startX to startY + 9 * cellSize
                3 -> startX + 9 * cellSize to startY + 9 * cellSize
                else -> startX to startY
            }
            drawRect(color, Offset(x, y), Size(6 * cellSize, 6 * cellSize))

            // 📦 Draw inner white box (with spacing)
            val innerBoxSize = 4 * cellSize
            val innerStartX = x + cellSize
            val innerStartY = y + cellSize
            drawRect(
                color = Color.White,
                topLeft = Offset(innerStartX, innerStartY),
                size = Size(innerBoxSize, innerBoxSize),
            )

            drawRect(
                color = Color.Black,
                topLeft = Offset(innerStartX, innerStartY),
                size = Size(innerBoxSize, innerBoxSize),
                style = Stroke(4f)
            )

            // 🎲 Draw four chances inside home (now with better spacing)
            val padding = cellSize * 1 // Space from inner box
            val chanceRadius = cellSize / 2.5f // Smaller radius for better fit

            val offsets = listOf(
                Offset(innerStartX + padding, innerStartY + padding),
                Offset(innerStartX + innerBoxSize - padding, innerStartY + padding),
                Offset(innerStartX + padding, innerStartY + innerBoxSize - padding),
                Offset(innerStartX + innerBoxSize - padding, innerStartY + innerBoxSize - padding)
            )

            offsets.forEach { pos ->
                drawCircle(
                    color = Color.Black, radius = chanceRadius, center = pos
                )
            }
        }

    }
}
