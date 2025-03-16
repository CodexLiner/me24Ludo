package me.meenagopal24.ludo


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieAnimatable
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.canvas.drawHomeAreas
import me.meenagopal24.ludo.canvas.drawHomeEntriesArrows
import me.meenagopal24.ludo.canvas.drawHomePaths
import me.meenagopal24.ludo.canvas.drawHomeTokens
import me.meenagopal24.ludo.canvas.drawLudoBoardGrid
import me.meenagopal24.ludo.canvas.drawPathArrows
import me.meenagopal24.ludo.canvas.drawPin
import me.meenagopal24.ludo.canvas.drawSafeAreas
import me.meenagopal24.ludo.game.Me24LudoBoard
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath
import me.meenagopal24.ludo.theme.AppTheme
import me.meenagopal24.ludo.utils.detectOverlaps
import me.meenagopal24.ludo.utils.getAnimatedOffset
import me.meenagopal24.ludo.utils.getHomeOffset
import me.meenagopal24.ludo.utils.homeOffsets
import me.meenagopal24.ludo.utils.safeZones
import multiplatform_app.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

fun String.toColor(): Color {
    val hex = removePrefix("#")
    return Color(hex.toLong(16) or (if (hex.length == 6) 0xFF000000 else 0x00000000))
}


@Composable
internal fun App() = AppTheme {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val homeColors = listOf(
            "#eb7434".toColor(),
            "#09b55c".toColor(),
            "#adcf17".toColor(),
            "#9294e8".toColor()
        )
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFBC2EB), Color(0xFFA6C1EE))
                )
                )) {
            Me24LudoBoard(
                Modifier.background(Color.Transparent , RoundedCornerShape(10.dp)),
                homeColors
            )

        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ComposeLottie(onDiceRoll: (Int) -> Unit = {}) {
    val scope = rememberCoroutineScope()
    var diceNumber by remember { mutableStateOf(1) }

    val composition by key(diceNumber) {
        rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/dice_$diceNumber.json").decodeToString()
            )
        }
    }

    val animation = rememberLottieAnimatable()
    Image(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            scope.launch {
                diceNumber = (1..6).random()
                animation.animate(composition, initialProgress = 0f)
                onDiceRoll(diceNumber)
            }
        },
        painter = rememberLottiePainter(
            forceOffscreenRendering = true,
            composition = composition,
            progress = { animation.progress },
        ),
        contentDescription = "Lottie dice animation"
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LudoBoardWithFourTokens(
    background: Modifier,
    homeColors: List<Color>,
    ludoBoardSize: Dp = 350.dp,
    setOffSet: (Offset, Float) -> Unit
) {
    var currentPlayer by remember { mutableStateOf(0) } // Track current player
    var overlappingState = remember { mutableListOf<Pair<Offset, Int?>>() }
    val overlappingOffsets = remember { mutableMapOf<Offset, Int>() }
    val tempSafeZone = remember { mutableMapOf<Pair<Int, Int>, Boolean>() }

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
        currentPlayer = player // Update current player when moving
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

    Column(
        modifier = background.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Canvas(
            modifier = Modifier.size(ludoBoardSize).clip(RoundedCornerShape(10.dp))
                .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
        ) {
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

                detectOverlaps(tokenPositions) { collisions ->
                    for ((position, tripletList) in collisions) {
                        val (row, col) = position
                        if (safeZones.contains(Pair(col, row))) continue

                        if (tripletList.size == 2) {
                            val (first, second) = tripletList

                            val (player1, token1, _) = first
                            val (player2, token2, _) = second

                            if (player1 != player2) {
                                if (player1 == currentPlayer) {
                                    tokenPositions[player2][token2] = -1
                                } else {
                                    tokenPositions[player1][token1] = -1
                                }
                            }
                        }
                    }

                    val overlappingStateList = collisions.keys.map { (row, col) ->
                        Pair(
                            Offset(
                                col * boardCellsSize + boardCellsSize / 2,
                                row * boardCellsSize + boardCellsSize / 2
                            ),
                            collisions[Pair(row, col)]?.size
                        )
                    }
                    overlappingState = overlappingStateList.toMutableStateList()
                }

                drawPin(
                    center = tokenOffset,
                    boardCellsSize = boardCellsSize,
                    color = color,
                    overlappingState = overlappingState,
                    pinDrawTracker = overlappingOffsets
                )
            }

            for (player in 0 until 4) {
                for (i in 0 until 4) {
                    drawAnimatedToken(player, i, tokenOffsets[player][i].value, homeColors[player])
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val haptic = LocalHapticFeedback.current
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (player in 0 until 4) {
                for (i in 0 until 4) {
                    Button(onClick = {
                        coroutineScope.launch {
                            moveToken(player, i)
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }) {
                        Text(text = "Move Player ${player + 1} Token ${i + 1}")
                    }
                }
            }
        }
    }
}
