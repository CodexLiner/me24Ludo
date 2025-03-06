package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

                drawRect(pathColor, Offset(currentX, currentY), Size(boardCellsSize -2f, boardCellsSize -2f))

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
