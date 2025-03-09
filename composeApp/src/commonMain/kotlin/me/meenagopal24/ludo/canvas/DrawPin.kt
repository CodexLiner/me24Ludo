package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import me.meenagopal24.ludo.toColor
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

    val adjustedCenter = if (numberOfOverlappingContent == 1) {
        center.copy(y = center.y - boardCellsSize / 2.5f)
    } else if (numberOfOverlappingContent in 3..4) {
        val offset = positionOffsets.getOrElse(currentIndex % 4) { Offset.Zero }
        Offset(center.x + offset.x, center.y - boardCellsSize / 2f + offset.y)
    } else {
        val maxOffset = boardCellsSize * 0.15f
        val angle = (currentIndex * (360f / numberOfOverlappingContent)) * (PI / 180).toFloat()
        val shiftX = (maxOffset * cos(angle))
        val shiftY = (maxOffset * sin(angle)) * 0.5f

        Offset(center.x + shiftX, center.y - boardCellsSize / 2.5f + shiftY)
    }

    pinDrawTracker[center] = (currentIndex + 1) % numberOfOverlappingContent

    val startX = adjustedCenter.x - radius
    val endX = adjustedCenter.x + radius
    val archBottomY = adjustedCenter.y + radius
    val triangleBottomY = adjustedCenter.y + radius * 2.5f

    drawArc(
        color = "#272635".toColor(),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(startX, adjustedCenter.y),
        size = Size(diameter, diameter),
    )

    val path = Path().apply {
        moveTo(startX, archBottomY)
        lineTo(adjustedCenter.x, triangleBottomY)
        lineTo(endX, archBottomY)
        close()
    }
    drawPath(path = path, color = "#272635".toColor(), style = Fill)

    drawCircle(
        color = color,
        radius = circleRadius,
        center = Offset(adjustedCenter.x, adjustedCenter.y + radius * 0.9f)
    )
}

