package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import me.meenagopal24.ludo.utils.BOARD_STROKE

fun DrawScope.drawLudoBoardGrid(startX: Float, startY: Float, boardCellsSize: Float, boxSize: Size) {
    for (row in 0 until 15) {
        for (col in 0 until 15) {
            val currentX = startX + col * boardCellsSize
            val currentY = startY + row * boardCellsSize
            drawRect(Color.Black, Offset(currentX, currentY), boxSize, style = Stroke(BOARD_STROKE))
        }
    }
}
