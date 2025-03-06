package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

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
