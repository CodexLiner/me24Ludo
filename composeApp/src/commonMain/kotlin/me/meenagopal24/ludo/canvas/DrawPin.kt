package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawPin(
    center: Offset,
    boardCellsSize: Float,
    color: Color = Color.Blue,
    overlappingState: List<Pair<Offset, Int?>>,
    pinDrawTracker: MutableMap<Offset, Int>
) {
    val diameter = boardCellsSize * 0.65f
    val radius = diameter / 2
    val circleRadius = radius * 0.5f

    val numberOfOverlappingContent = overlappingState.find { it.first == center }?.second ?: 1
    val currentIndex = pinDrawTracker.getOrPut(center) { 0 }

    val gridSpacing = boardCellsSize * 0.2f
    val positionOffsets = listOf(
        Offset(-gridSpacing, -gridSpacing),
        Offset(gridSpacing, -gridSpacing),
        Offset(-gridSpacing, gridSpacing),
        Offset(gridSpacing, gridSpacing)
    )

    val adjustedCenter = when (numberOfOverlappingContent) {
        1 -> {
            center.copy(y = center.y - boardCellsSize / 2.5f)
        }
        in 3..4 -> {
            val offset = positionOffsets.getOrElse(currentIndex % 4) { Offset.Zero }
            Offset(center.x + offset.x, center.y - boardCellsSize / 2f + offset.y)
        }
        else -> {
            val maxOffset = boardCellsSize * 0.15f
            val angle = (currentIndex * (360f / numberOfOverlappingContent)) * (PI / 180).toFloat()
            val shiftX = (maxOffset * cos(angle))
            val shiftY = (maxOffset * sin(angle)) * 0.5f

            Offset(center.x + shiftX, center.y - boardCellsSize / 2.5f + shiftY)
        }
    }

    pinDrawTracker[center] = (currentIndex + 1) % numberOfOverlappingContent

    val startX = adjustedCenter.x - radius
    val endX = adjustedCenter.x + radius
    val archBottomY = adjustedCenter.y + radius
    val triangleBottomY = adjustedCenter.y + radius * 2.5f

    val arcGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A3A3A), Color(0xFF272635)),
        startY = adjustedCenter.y,
        endY = adjustedCenter.y + diameter
    )

    drawArc(
        brush = arcGradient,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(startX, adjustedCenter.y),
        size = Size(diameter, diameter * 1.02f)
    )

    // Gradient for the triangle path
    val pathGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF3A3A3A), Color(0xFF272635)),
        start = Offset(startX, archBottomY),
        end = Offset(adjustedCenter.x, triangleBottomY)
    )

    val path = Path().apply {
        moveTo(startX, archBottomY)
        lineTo(adjustedCenter.x, triangleBottomY)
        lineTo(endX, archBottomY)
        close()
    }

    drawPath(path = path, brush = pathGradient, style = Fill)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.8f), color),
            center = Offset(adjustedCenter.x, adjustedCenter.y + radius * 0.9f),
            radius = circleRadius * 1.1f
        ),
        radius = circleRadius,
        center = Offset(adjustedCenter.x, adjustedCenter.y + radius * 0.9f)
    )
}

