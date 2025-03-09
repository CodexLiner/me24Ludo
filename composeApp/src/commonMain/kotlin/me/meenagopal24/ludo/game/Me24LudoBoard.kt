package me.meenagopal24.ludo.game

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.canvas.drawPin
import me.meenagopal24.ludo.move.PlayerMovement
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath
import me.meenagopal24.ludo.utils.calculateAlpha
import me.meenagopal24.ludo.utils.detectOverlaps
import me.meenagopal24.ludo.utils.getAnimatedActiveState
import me.meenagopal24.ludo.utils.getAnimatedOffset
import me.meenagopal24.ludo.utils.getHomeOffset
import me.meenagopal24.ludo.utils.getScreenSize
import me.meenagopal24.ludo.utils.homeOffsets
import me.meenagopal24.ludo.utils.safeZones

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Me24LudoBoard(
    background: Modifier,
    homeColors: List<Color>,
    padding: Float = 10f,
    playersCount: Int = 4,
) {
    val screenSize = getScreenSize().apply {
        width -= padding
    }

    val boardCellsSize = with(LocalDensity.current) { (screenSize.width.dp / 15).toPx() }

    var currentPlayer by remember { mutableStateOf(0) }
    var currentPlayerMove by remember { mutableStateOf(-1) }

    var expanded by remember { mutableStateOf(false) }
    var overlappingState = remember { mutableListOf<Pair<Offset, Int?>>() }
    val overlappingOffsets = remember { mutableMapOf<Offset, Int>() }
    val tempSafeZone = remember { mutableMapOf<Pair<Int, Int>, Boolean>() }
    val colorAlphaState = getAnimatedActiveState()

    val playerPaths = remember {
        listOf(
            getPlayerOnePath(),
            getPlayerTwoPath(),
            getPlayerThreePath(),
            getPlayerFourPath()
        )
    }

    val tokenPositions = remember { List(4) { mutableStateListOf(-1, -1, -1, -1) } }
    val coroutineScope = rememberCoroutineScope()

    fun moveToken(player: Int, index: Int) {
        currentPlayer = player
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
        val modifier = Modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                val (col, row) = offset.x / boardCellsSize to offset.y / boardCellsSize
                PlayerMovement.movePlayer(
                    currentPlayer, Offset(row, col),
                    onMove = { newPosition ->
                        tokenPositions[currentPlayer].indexOf(newPosition).takeIf { it >= 0 }
                            ?.let { tokenIndex ->
                                val pathSize = playerPaths[currentPlayer].size
                                val startPos = tokenPositions[currentPlayer][tokenIndex]
                                val endPos = (startPos + currentPlayerMove).coerceAtMost(pathSize - 1)
                                currentPlayerMove = -1
                                coroutineScope.launch {
                                    (startPos..endPos).forEach { pos ->
                                        tokenPositions[currentPlayer][tokenIndex] = pos
                                        delay(300)
                                    }
                                }
                            }
                    },
                    onFirstMove = {
                        if (currentPlayerMove == 6) {
                            moveToken(currentPlayer, it)
                            currentPlayerMove = -1
                        }
                    }
                )
            }
        }

        drawLudoBoard(
            modifier = modifier.width(screenSize.width.dp).aspectRatio(1f).clip(RoundedCornerShape(10.dp)).border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
            homeColors = homeColors,
            boardCellsSize = boardCellsSize,
        ) { startX, startY ->

            detectOverlaps(tokenPositions) { collisions ->
                collisions.forEach { (position, tripletList) ->
                    val (row, col) = position

                    if (Pair(col, row) in safeZones) return@forEach

                    if (tripletList.size == 2) {
                        val (first, second) = tripletList
                        val (player1, token1, _) = first
                        val (player2, token2, _) = second
                        if (player1 != player2) {
                            tokenPositions[if (player1 == currentPlayer) player2 else player1][if (player1 == currentPlayer) token2 else token1] = -1
                        }
                    }
                }

                overlappingState = collisions.keys.map { (row, col) ->
                    Pair(Offset(col * boardCellsSize + boardCellsSize / 2, row * boardCellsSize + boardCellsSize / 2), collisions[Pair(row, col)]?.size)
                }.toMutableStateList()
            }
            /** draw players and their tokens*/
            repeat(playersCount) { player ->
                repeat(4) { token ->

                    val alpha = calculateAlpha(
                        player = player,
                        token = token,
                        currentPlayerMove = currentPlayerMove,
                        tokenPositions = tokenPositions,
                        playerPaths = playerPaths,
                        colorAlphaState = colorAlphaState,
                    )

                    drawLudoTokens(
                        boardOffSet = Offset(startX, startY),
                        player = player,
                        token = token,
                        overlappingState = overlappingState,
                        tokenOffsets = tokenOffsets,
                        tokenColor = homeColors[player].copy(alpha = if (player == currentPlayer) alpha else 1f),
                        tokenPositions = tokenPositions,
                        boardCellsSize = boardCellsSize,
                        overlappingOffsets = overlappingOffsets,
                    )
                }
            }
        }


        /**
         * is use less part for testing purpose only
         */
        Spacer(modifier = Modifier.height(16.dp))

        val haptic = LocalHapticFeedback.current
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            repeat(6) { index ->
                Button(onClick = {
                    currentPlayerMove = index+1
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }) {
                    Text(text = "Move ${index + 1}")
                }
            }
            Button(onClick = {
                expanded = true
            }) {
                Text(text = "CurrentPlayer  $currentPlayer")
                DropdownMenu(expanded, onDismissRequest = {
                    expanded = false
                }) {
                    repeat(4) {
                        DropdownMenuItem(onClick = {
                            currentPlayer = it
                            expanded = false
                        }, text = {
                            Text("$it")
                        })
                    }
                }
            }
        }
    }
}

fun DrawScope.drawLudoTokens(
    boardOffSet: Offset,
    player: Int,
    token: Int,
    tokenColor: Color,
    boardCellsSize: Float,
    overlappingOffsets: MutableMap<Offset, Int>,
    tokenPositions: List<SnapshotStateList<Int>>,
    tokenOffsets: List<List<State<Offset>>>,
    overlappingState: MutableList<Pair<Offset, Int?>>
) {
    val tokenOffset = if (tokenPositions[player][token] == -1) getHomeOffset(
        boardOffSet.x,
        boardOffSet.y,
        homeOffsets[player],
        boardCellsSize
    )[token] else tokenOffsets[player][token].value
    drawPin(
        center = tokenOffset,
        boardCellsSize = boardCellsSize,
        color = tokenColor,
        overlappingState = overlappingState,
        pinDrawTracker = overlappingOffsets
    )
}