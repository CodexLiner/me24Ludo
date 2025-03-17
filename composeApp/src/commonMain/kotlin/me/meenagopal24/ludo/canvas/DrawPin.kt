package me.meenagopal24.ludo.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import me.meenagopal24.ludo.utils.modify
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawPin(
    center: Offset,
    boardCellsSize: Float,
    color: Color = Color.Blue,
    overlappingState: List<Pair<Offset, Int?>>,
    pinDrawTracker: MutableMap<Offset, Int>,
    tokenAlpha: Float,
    isActive: Boolean
) {

    val numberOfOverlappingContent = overlappingState.find { it.first == center }?.second ?: 1
    val scaleFactor = if (isActive.not()) when (numberOfOverlappingContent) {
        1 -> 0.9f
        2 -> 0.9f
        in 3..4 -> 0.4f
        else -> 0.7f
    } else 0.9f

    val pawnHeight = boardCellsSize * 0.8f * scaleFactor
    val pawnWidth = boardCellsSize * 0.6f * scaleFactor
    val headRadius = pawnWidth * 0.30f

    val currentIndex = pinDrawTracker.getOrPut(center) { 0 }

    val gridSpacing = boardCellsSize * 0.2f
    val positionOffsets = listOf(
        Offset(-gridSpacing, -gridSpacing),
        Offset(gridSpacing, -gridSpacing),
        Offset(-gridSpacing, gridSpacing),
        Offset(gridSpacing, gridSpacing)
    )

    val adjustedCenter = when (numberOfOverlappingContent) {
        1 -> center.copy(y = center.y - boardCellsSize / 4f)
        in 3..4 -> {
            val offset = positionOffsets.getOrElse(currentIndex % 4) { Offset.Zero }
            Offset(center.x + offset.x, center.y - (boardCellsSize / if (isActive) 3f else 8f) + offset.y)
        }

        else -> {
            val maxOffset = boardCellsSize * 0.15f
            val angle = (currentIndex * (360f / numberOfOverlappingContent)) * (PI / 180).toFloat()
            val shiftX = (maxOffset * cos(angle))
            val shiftY = (maxOffset * sin(angle)) * 0.5f

            Offset(center.x + shiftX, center.y - boardCellsSize / 3f + shiftY)
        }
    }

    pinDrawTracker[center] = (currentIndex + 1) % numberOfOverlappingContent

    val gradient = Brush.verticalGradient(
        colors = listOf(color.copy(alpha = 1f).modify(), color),
        startY = adjustedCenter.y - pawnHeight / 2,
        endY = adjustedCenter.y + pawnHeight / 2
    )

    val path = Path().apply {
        addOval(
            Rect(
                center = Offset(adjustedCenter.x, adjustedCenter.y - pawnHeight * 0.35f),
                radius = headRadius
            )
        )

        moveTo(adjustedCenter.x - pawnWidth * 0.18f, adjustedCenter.y - pawnHeight * 0.2f)

        quadraticTo(
            adjustedCenter.x - pawnWidth * 0.25f, adjustedCenter.y - pawnHeight * 0.05f,
            adjustedCenter.x - pawnWidth * 0.40f, adjustedCenter.y + pawnHeight * 0.3f
        )

        quadraticTo(
            adjustedCenter.x - pawnWidth * 0.52f, adjustedCenter.y + pawnHeight * 0.55f,
            adjustedCenter.x - pawnWidth * 0.40f, adjustedCenter.y + pawnHeight * 0.65f
        )

        lineTo(adjustedCenter.x + pawnWidth * 0.40f, adjustedCenter.y + pawnHeight * 0.65f)

        quadraticTo(
            adjustedCenter.x + pawnWidth * 0.52f, adjustedCenter.y + pawnHeight * 0.55f,
            adjustedCenter.x + pawnWidth * 0.40f, adjustedCenter.y + pawnHeight * 0.3f
        )

        quadraticTo(
            adjustedCenter.x + pawnWidth * 0.25f, adjustedCenter.y - pawnHeight * 0.05f,
            adjustedCenter.x + pawnWidth * 0.18f, adjustedCenter.y - pawnHeight * 0.2f
        )

        close()
    }

    val borderGradient = Brush.radialGradient(
        colors = listOf(
            Color.Gray.copy(alpha = 1f),
            Color.Black.copy(alpha = tokenAlpha)  // to add blinking
        ),
        center = adjustedCenter,
        radius = pawnWidth
    )

    // Draw Border
    drawPath(path, brush = borderGradient, style = Stroke(width = 5f * tokenAlpha * 2))
    drawPath(path, brush = gradient)

}

