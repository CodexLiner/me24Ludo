package me.meenagopal24.ludo.canvas


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

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
