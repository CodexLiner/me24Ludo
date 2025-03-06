package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import me.meenagopal24.ludo.toColor

fun DrawScope.drawPin(center: Offset, boardCellsSize: Float, color: Color = Color.Blue) {
    val diameter = boardCellsSize * 0.6f
    val radius = diameter / 2
    val centerX = center.x
    val centerY = center.y - boardCellsSize / 2

    // Arch start and end points
    val startX = centerX - radius
    val endX = centerX + radius
    val archBottomY = centerY + radius
    val triangleBottomY = centerY + radius * 2.5f // Adjust the bottom point of the pin

    // Draw Half-Circle Arch
    drawArc(
        color = "#272635".toColor(),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(startX, centerY),
        size = Size(diameter, diameter),
    )

    // Create and Draw Filled Triangle Path
    val path = Path().apply {
        moveTo(startX, archBottomY) // Left bottom of the arch
        lineTo(centerX, triangleBottomY) // Bottom center point
        lineTo(endX, archBottomY) // Right bottom of the arch
        close() // Close the triangle
    }
    drawPath(path = path, color ="#272635".toColor(), style = Fill)

    // Draw Center Circle inside the Arch
    val circleRadius = radius * 0.5f  // Adjust the size of the circle
    drawCircle(
        color = color, // Make it distinct from the arch
        radius = circleRadius,
        center = Offset(centerX, centerY + radius * 0.8f) // Position inside the arch
    )
}
