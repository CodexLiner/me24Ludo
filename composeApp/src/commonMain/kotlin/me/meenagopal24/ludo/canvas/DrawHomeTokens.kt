package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import me.meenagopal24.ludo.utils.homeOffsets
import me.meenagopal24.ludo.utils.modify

fun DrawScope.drawHomeTokens(
    startX: Float,
    startY: Float,
    boardCellsSize: Float,
    homeColors: List<Color>,
    borderThickness: Float = 4f,
    paddingFactor: Float = 1f,
    chanceScale: Float = 1.7f
) {
    homeOffsets.forEachIndexed { index, (dx, dy) ->
        val homeOffset = Offset(startX + dx * boardCellsSize, startY + dy * boardCellsSize)
        val innerBoxSize = 4 * boardCellsSize
        val innerStart = homeOffset + Offset(boardCellsSize, boardCellsSize)
        val padding = boardCellsSize * paddingFactor
        val chanceRadius = boardCellsSize / chanceScale

        // Background with border
        drawRoundRect(
            cornerRadius = CornerRadius(50f),
            color = Color.White,
            topLeft = innerStart,
            size = Size(innerBoxSize, innerBoxSize)
        )

        drawRoundRect(
            cornerRadius = CornerRadius(50f),
            brush = Brush.linearGradient(
                colors = listOf(Color.Black, homeColors[index]),
                start = innerStart,
                end = innerStart + Offset(innerBoxSize, innerBoxSize)
            ),
            topLeft = innerStart,
            size = Size(innerBoxSize, innerBoxSize),
            style = Stroke(borderThickness)
        )

        // Token (chance) positions
        listOf(
            Offset(padding, padding),
            Offset(innerBoxSize - padding, padding),
            Offset(padding, innerBoxSize - padding),
            Offset(innerBoxSize - padding, innerBoxSize - padding)
        ).map { innerStart + it }.forEach { pos ->
            val modifiedColor = homeColors[index].modify()

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(homeColors[index], modifiedColor),
                    center = pos,
                    radius = chanceRadius
                ),
                radius = chanceRadius,
                center = pos
            )

            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(modifiedColor, homeColors[index]),
                    start = innerStart,
                    end = innerStart + Offset(innerBoxSize, innerBoxSize)
                ),
                radius = chanceRadius,
                center = pos,
                style = Stroke(borderThickness   , 2f)
            )
        }
    }
}


