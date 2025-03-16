package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import me.meenagopal24.ludo.utils.BOARD_STROKE
import me.meenagopal24.ludo.utils.modify

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

            if (pathColor != Color.White) {
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        pathColor,
                        pathColor.modify()
                    ),
                    start = Offset(currentX, currentY),
                    end = Offset(currentX + boardCellsSize, currentY + boardCellsSize)
                )

                drawRect(
                    brush = gradientBrush,
                    topLeft = Offset(currentX, currentY),
                    size = Size(boardCellsSize - BOARD_STROKE, boardCellsSize - BOARD_STROKE)
                )
            } else drawRect(
                    color = pathColor,
                    topLeft = Offset(currentX, currentY),
                    size = Size(boardCellsSize - BOARD_STROKE, boardCellsSize - BOARD_STROKE)
                )

        }
    }
}
