package me.meenagopal24.ludo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.canvas.*
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath
import me.meenagopal24.ludo.theme.AppTheme
import me.meenagopal24.ludo.utils.getAnimatedOffset
import me.meenagopal24.ludo.utils.getHomeOffset
import me.meenagopal24.ludo.utils.homeOffsets

fun String.toColor(): Color {
    val hex = removePrefix("#")
    return Color(hex.toLong(16) or (if (hex.length == 6) 0xFF000000 else 0x00000000))
}


@Composable
internal fun App() = AppTheme {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val homeColors = listOf("#eb7434".toColor(), "#09b55c".toColor(), "#adcf17".toColor(), "#9294e8".toColor())
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize() .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFBC2EB), Color(0xFFA6C1EE))
                )
                )) {
            LudoBoardWithFourTokens(
                Modifier.background(Color.Transparent , RoundedCornerShape(10.dp)),
                homeColors
            ){ offset, boardCellSize ->

            }

        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LudoBoardWithFourTokens(
    background: Modifier,
    homeColors: List<Color>,
    ludoBoardSize: Dp = 450.dp,
    setOffSet: (Offset, Float) -> Unit
) {
    val playerPaths = remember {
        listOf(
            getPlayerOnePath(),
            getPlayerTwoPath(),
            getPlayerThreePath(),
            getPlayerFourPath()
        )
    }

    val tokenPositions = remember { List(4) { mutableStateListOf(-1, -1, -1, -1) } }

    val boardCellsSize = with(LocalDensity.current) { (ludoBoardSize / 15).toPx() }
    val coroutineScope = rememberCoroutineScope()

    fun moveToken(player: Int, index: Int) {
        val path = playerPaths[player]
        if (tokenPositions[player][index] == -1) {
            tokenPositions[player][index] = 0
        } else if (tokenPositions[player][index] < path.size - 1) {
            tokenPositions[player][index] += 1
        } else {
            tokenPositions[player][index] = -1
        }
    }

    val tokenOffsets = playerPaths.mapIndexed { playerIndex, path ->
        tokenPositions[playerIndex].map { position ->
            getAnimatedOffset(path, position.coerceAtLeast(0), boardCellsSize)
        }
    }

    Column(modifier = background.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        Canvas(modifier = Modifier.size(ludoBoardSize).clip(RoundedCornerShape(10.dp)).border(2.dp, Color.Black, RoundedCornerShape(10.dp))) {
            val startX = (size.width - size.minDimension) / 2
            val startY = (size.height - size.minDimension) / 2

            setOffSet(Offset(startX, startY), boardCellsSize)

            drawLudoBoardGrid(startX, startY, boardCellsSize, Size(boardCellsSize, boardCellsSize))
            drawHomePaths(startX, startY, boardCellsSize, homeColors)
            drawSafeAreas(startX, startY, boardCellsSize, homeColors)
            drawHomeAreas(startX, startY, boardCellsSize, homeColors)
            drawHomeEntriesArrows(startX, startY, boardCellsSize, homeColors)
            drawPathArrows(startX, startY, boardCellsSize, homeColors)
            drawHomeTokens(startX, startY, boardCellsSize, homeColors)

            fun drawAnimatedToken(player: Int, index: Int, offset: Offset, color: Color) {
                val tokenOffset = if (tokenPositions[player][index] == -1) {
                    getHomeOffset(startX, startY, homeOffsets[player], boardCellsSize)[index]
                } else offset

                drawPin(center = tokenOffset, boardCellsSize = boardCellsSize, color)
            }

            for (player in 0 until 4) {
                for (i in 0 until 4) {
                    drawAnimatedToken(player, i, tokenOffsets[player][i].value, homeColors[player])
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        val haptic = LocalHapticFeedback.current
        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (player in 0 until 4) {
                for (i in 0 until 4) {
                    Button(onClick = {
                        coroutineScope.launch {
                            moveToken(player, i)
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }) {
                        Text(text = "Move PLayer ${player + 1} Token ${i + 1}")
                    }
                }
            }
        }
    }
}
