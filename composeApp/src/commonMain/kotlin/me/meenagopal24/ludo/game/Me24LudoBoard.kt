package me.meenagopal24.ludo.game

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import me.meenagopal24.ludo.canvas.drawPin
import me.meenagopal24.ludo.utils.calculateAlpha
import me.meenagopal24.ludo.utils.detectOverlaps
import me.meenagopal24.ludo.utils.getAnimatedActiveState
import me.meenagopal24.ludo.utils.getAnimatedBorderColor
import me.meenagopal24.ludo.utils.getAnimatedOffset
import me.meenagopal24.ludo.utils.getHomeOffset
import me.meenagopal24.ludo.utils.getScreenSize
import me.meenagopal24.ludo.utils.homeOffsets
import me.meenagopal24.ludo.utils.ifNotTrue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Me24LudoBoard(
    background: Modifier,
    homeColors: List<Color>,
    padding: Float = 10f,
    playersCount: Int = 4,
) {

    val viewModel : Me24LudoBoardViewModel = viewModel()
    val currentPlayer by viewModel.currentPlayer.collectAsState()
    val currentMove by viewModel.currentMove.collectAsState()
    val movementInProgress by viewModel.movementInProgress.collectAsState()
    val tokenPositions by viewModel.tokenPositions.collectAsState()


    val screenSize = remember { getScreenSize().apply { width -= padding } }
    val boardCellsSize = with(LocalDensity.current) { (screenSize.width.dp / 15).toPx() }
    val colorAlphaState = getAnimatedActiveState()


    var overlappingState = remember { mutableListOf<Pair<Offset, Int?>>() }
    val overlappingOffsets = remember { mutableMapOf<Offset, Int>() }


    val tokenOffsets = viewModel.playerPaths.mapIndexed { playerIndex, path ->
        tokenPositions[playerIndex].map { position ->
            getAnimatedOffset(path, position.coerceAtLeast(0), boardCellsSize)
        }
    }

    /**
     * launched effect to set player count
     */
    LaunchedEffect(Unit) {
        viewModel.setPlayerCount(playersCount)
    }

    /**
     * launched effect to set current player
     */
    LaunchedEffect(currentPlayer) {
        viewModel.setCurrentPlayer(currentPlayer % playersCount)
        viewModel.resetCurrentMove()
    }

    /**
     * launched effect to set current move
     */
    LaunchedEffect(currentMove) {
        val playerTokens = tokenPositions[currentPlayer]
        val availableTokens = playerTokens.filter { it != -1 }
        when {
            playerTokens.all { it == -1 } && currentMove in 1..5 -> viewModel.setCurrentPlayer((currentPlayer + 1) % playersCount)
            availableTokens.size == 1 && currentMove !in listOf(-1, 6) -> viewModel.autoMovePlayer(playerTokens.indexOf(availableTokens.first()))
        }
    }


    Column(
        modifier = background.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val modifier = Modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                viewModel.movePlayer(currentPlayer , boardCellsSize , offset)
            }
        }
        DicedBoard(padding , homeColors , currentPlayer , viewModel) {
            drawLudoBoard(
                modifier = modifier.width(screenSize.width.dp).aspectRatio(1f).clip(RoundedCornerShape(10.dp)).border(1.dp, getAnimatedBorderColor(), RoundedCornerShape(10.dp)),
                homeColors = homeColors,
                boardCellsSize = boardCellsSize,
            ) { startX, startY ->

                detectOverlaps(tokenPositions) { collisions ->
                    movementInProgress.ifNotTrue {
                        viewModel.handleCollisions(collisions)
                        overlappingState = collisions.keys.map { (row, col) ->
                            Pair(Offset(col * boardCellsSize + boardCellsSize / 2, row * boardCellsSize + boardCellsSize / 2), collisions[Pair(row, col)]?.size)
                        }.toMutableStateList()
                    }
                }

                /**
                 * draw players and their tokens
                 */
                repeat(playersCount) { player ->
                    repeat(4) { token ->
                        val alpha = calculateAlpha(
                            player = player,
                            token = token,
                            currentPlayerMove = currentMove,
                            tokenPositions = tokenPositions,
                            playerPaths = viewModel.playerPaths,
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
        }

        /**
         * is use less part for testing purpose only
         */

        DebugControls(viewModel = viewModel)

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

@Composable
private fun DicedBoard(padding: Float, homeColors: List<Color>, currentPlayer : Int, viewModel: Me24LudoBoardViewModel,  content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth() , horizontalAlignment = Alignment.CenterHorizontally) {
        DiceRow(padding = padding, homeColors = homeColors, currentPlayer = currentPlayer, viewModel = viewModel,)
        content()
        DiceRow(3 , endIndex = 2, padding = padding, homeColors = homeColors, currentPlayer = currentPlayer, viewModel = viewModel)
    }
}

/**
 * Todo remove later
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DebugControls(viewModel: Me24LudoBoardViewModel) {

    var expanded by remember { mutableStateOf(false) }
    val currentPlayer by viewModel.currentPlayer.collectAsState()

    Spacer(modifier = Modifier.height(16.dp))

    val haptic = LocalHapticFeedback.current
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        repeat(6) { index ->
            Button(onClick = {
                viewModel.updateCurrentMove(index + 1)
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
                        viewModel.setCurrentPlayer(it)
                        expanded = false
                    }, text = {
                        Text("$it")
                    })
                }
            }
        }
    }
}

