package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import me.meenagopal24.ludo.Direction
import me.meenagopal24.ludo.utils.modify

fun DrawScope.drawHomeEntriesArrows(
    startX: Float,
    startY: Float,
    boardCellsSize: Float,
    homeColors: List<Color>
) {
    val arrowLength = boardCellsSize / 1.3f
    val arrowHeadSize = boardCellsSize * 0.25f
    val centerOffset = 7.5f * boardCellsSize
    val shaftPadding = boardCellsSize * 0.15f
    val headPadding = boardCellsSize * 0.2f

    fun drawArrow(start: Offset, end: Offset, color: Color, direction: Direction) {
        val modifiedColor = color.modify()

        val arrowStart = when (direction) {
            Direction.UP -> start.copy(y = start.y - shaftPadding)
            Direction.DOWN -> start.copy(y = start.y + shaftPadding)
            Direction.LEFT -> start.copy(x = start.x - shaftPadding)
            Direction.RIGHT -> start.copy(x = start.x + shaftPadding)
        }

        val arrowEnd = when (direction) {
            Direction.UP -> end.copy(y = end.y + headPadding)
            Direction.DOWN -> end.copy(y = end.y - headPadding)
            Direction.LEFT -> end.copy(x = end.x + headPadding)
            Direction.RIGHT -> end.copy(x = end.x - headPadding)
        }

        // Gradient for arrow shaft
        val arrowGradient = Brush.linearGradient(
            colors = listOf(color, modifiedColor),
            start = arrowStart,
            end = arrowEnd
        )

        drawLine(brush = arrowGradient, arrowStart, arrowEnd, strokeWidth = 5f, cap = StrokeCap.Round)

        val (arrowEndX, arrowEndY) = end
        val arrowHeadPath = Path().apply {
            moveTo(arrowEndX, arrowEndY)
            when (direction) {
                Direction.UP -> {
                    lineTo(arrowEndX - arrowHeadSize, arrowEndY + arrowHeadSize)
                    lineTo(arrowEndX + arrowHeadSize, arrowEndY + arrowHeadSize)
                }
                Direction.DOWN -> {
                    lineTo(arrowEndX - arrowHeadSize, arrowEndY - arrowHeadSize)
                    lineTo(arrowEndX + arrowHeadSize, arrowEndY - arrowHeadSize)
                }
                Direction.LEFT -> {
                    lineTo(arrowEndX + arrowHeadSize, arrowEndY - arrowHeadSize)
                    lineTo(arrowEndX + arrowHeadSize, arrowEndY + arrowHeadSize)
                }
                Direction.RIGHT -> {
                    lineTo(arrowEndX - arrowHeadSize, arrowEndY - arrowHeadSize)
                    lineTo(arrowEndX - arrowHeadSize, arrowEndY + arrowHeadSize)
                }
            }
            close()
        }

        // Gradient for arrowhead
        val arrowHeadGradient = Brush.linearGradient(
            colors = listOf(modifiedColor, color),
            start = Offset(arrowEndX - arrowHeadSize, arrowEndY),
            end = Offset(arrowEndX + arrowHeadSize, arrowEndY)
        )

        drawPath(arrowHeadPath, brush = arrowHeadGradient)
    }

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

