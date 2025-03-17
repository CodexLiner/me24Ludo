package me.meenagopal24.ludo.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import me.meenagopal24.ludo.canvas.drawHomeAreas
import me.meenagopal24.ludo.canvas.drawHomeEntriesArrows
import me.meenagopal24.ludo.canvas.drawHomePaths
import me.meenagopal24.ludo.canvas.drawHomeTokens
import me.meenagopal24.ludo.canvas.drawLudoBoardGrid
import me.meenagopal24.ludo.canvas.drawPathArrows
import me.meenagopal24.ludo.canvas.drawSafeAreas

@Composable
fun drawLudoBoard(
    modifier: Modifier,
    width: Float,
    boardCellsSize: Float,
    homeColors: List<Color>,
    boardOffset: DrawScope.(Float, Float) -> Unit = { _, _ -> },
) {
    Box(Modifier.width(width.dp).aspectRatio(1f)) {
        Canvas(modifier = modifier.matchParentSize()) {
            val startX = (size.width - size.minDimension) / 2
            val startY = (size.height - size.minDimension) / 2

            drawLudoBoardGrid(startX, startY, boardCellsSize, Size(boardCellsSize, boardCellsSize))
            drawHomePaths(startX, startY, boardCellsSize, homeColors)
            drawSafeAreas(startX, startY, boardCellsSize, homeColors)
            drawHomeAreas(startX, startY, boardCellsSize, homeColors)
            drawHomeEntriesArrows(startX, startY, boardCellsSize, homeColors)
            drawPathArrows(startX, startY, boardCellsSize, homeColors)
            drawHomeTokens(startX, startY, boardCellsSize, homeColors)
        }
        Canvas(modifier = Modifier.matchParentSize()) {
            val startX = (size.width - size.minDimension) / 2
            val startY = (size.height - size.minDimension) / 2
            boardOffset(startX, startY)
        }
    }
}