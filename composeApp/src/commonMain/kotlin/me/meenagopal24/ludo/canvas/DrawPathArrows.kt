package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import me.meenagopal24.ludo.utils.BOARD_STROKE
import me.meenagopal24.ludo.utils.modify

fun DrawScope.drawPathArrows(startX: Float, startY: Float, boardCellsSize: Float, homeColors: List<Color>) {
    val centerX = startX + 7.5f * boardCellsSize
    val centerY = startY + 7.5f * boardCellsSize

    val arrowWidth = boardCellsSize * 3
    val arrowHeight = boardCellsSize * 3f

    fun getGradientBrush(color: Color, x1: Float, y1: Float, x2: Float, y2: Float): Brush {


        return Brush.linearGradient(
            colors = listOf(color, color.modify()),
            start = Offset(x1, y1),
            end = Offset(x2, y2)
        )
    }

    fun drawTriangle(color: Color, x1: Float, y1: Float, x2: Float, y2: Float) {
        Path().apply {
            moveTo(centerX, centerY)
            lineTo(x1, y1)
            lineTo(x2, y2)
            close()
        }.let { drawPath(it, getGradientBrush(color, x1, y1, x2, y2)) }
    }

    val topLeftX = centerX - arrowWidth / 2
    val topLeftY = centerY - arrowHeight / 2
    val topRightX = centerX + arrowWidth / 2
    val bottomLeftY = centerY + arrowHeight / 2

    drawTriangle(homeColors[0], topLeftX, bottomLeftY, topLeftX, topLeftY)
    drawTriangle(homeColors[1], topLeftX, topLeftY, topRightX, topLeftY)
    drawTriangle(homeColors[2], topRightX - BOARD_STROKE, topLeftY, topRightX - BOARD_STROKE, bottomLeftY)
    drawTriangle(homeColors[3], topLeftX, bottomLeftY - BOARD_STROKE, topRightX, bottomLeftY - BOARD_STROKE)
}

