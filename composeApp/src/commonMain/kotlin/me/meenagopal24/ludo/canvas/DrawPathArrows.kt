package me.meenagopal24.ludo.canvas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

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
