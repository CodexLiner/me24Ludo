package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import me.meenagopal24.ludo.Direction

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
