package me.meenagopal24.ludo.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.canvas.drawPin
import me.meenagopal24.ludo.move.PlayerMovement
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath
import me.meenagopal24.ludo.utils.animateTokenMovement
import me.meenagopal24.ludo.utils.calculateAlpha
import me.meenagopal24.ludo.utils.detectOverlaps
import me.meenagopal24.ludo.utils.getAnimatedActiveState
import me.meenagopal24.ludo.utils.getAnimatedBorderColor
import me.meenagopal24.ludo.utils.getAnimatedOffset
import me.meenagopal24.ludo.utils.getHomeOffset
import me.meenagopal24.ludo.utils.getScreenSize
import me.meenagopal24.ludo.utils.homeOffsets
import me.meenagopal24.ludo.utils.ifNotTrue
import me.meenagopal24.ludo.utils.nextPlayer
import me.meenagopal24.ludo.utils.safeZones

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Me24LudoBoard(
    background: Modifier,
    homeColors: List<Color>,
    padding: Float = 10f,
    playersCount: Int = 4,
) {
    val screenSize = remember { getScreenSize().apply { width -= padding } }
    val boardCellsSize = with(LocalDensity.current) { (screenSize.width.dp / 15).toPx() }

    var currentPlayer by remember { mutableStateOf(0) }
    var currentPlayerMove by remember { mutableStateOf(-1) }
    var movementInProgress by remember { mutableStateOf(false) }

    var overlappingState = remember { mutableListOf<Pair<Offset, Int?>>() }
    val overlappingOffsets = remember { mutableMapOf<Offset, Int>() }
    val colorAlphaState = getAnimatedActiveState()

    val playerPaths = remember { listOf(getPlayerOnePath(), getPlayerTwoPath(), getPlayerThreePath(), getPlayerFourPath()) }

    val tokenPositions = remember { List(4) { mutableStateListOf(-1, -1, -1, -1) } }
    val coroutineScope = rememberCoroutineScope()

    val tokenOffsets = playerPaths.mapIndexed { playerIndex, path ->
        tokenPositions[playerIndex].map { position ->
            getAnimatedOffset(path, position.coerceAtLeast(0), boardCellsSize)
        }
    }

    LaunchedEffect(currentPlayer) {
        if (currentPlayer >= playersCount) currentPlayer = 0
        currentPlayerMove = -1
    }

    LaunchedEffect(currentPlayerMove) {
        val playerTokens = tokenPositions[currentPlayer]
        val availableTokens = playerTokens.filter { it != -1 }

        if (playerTokens.all { it == -1 } && currentPlayerMove > 0 && currentPlayerMove != 6) {
            currentPlayer = (currentPlayer + 1) % playersCount
        } else if (availableTokens.size == 1 && currentPlayerMove !in listOf(-1, 6)) {
            val tokenIndex = playerTokens.indexOf(availableTokens.first())
            coroutineScope.launch {
                movementInProgress = true
                coroutineScope.launch {
                    animateTokenMovement(
                        tokenPositions = tokenPositions[currentPlayer],
                        tokenIndex = tokenIndex,
                        moveAmount = currentPlayerMove,
                        playerPathSize = playerPaths[currentPlayer].size
                    )
                    movementInProgress = false
                    currentPlayer = currentPlayer.nextPlayer(playersCount, currentPlayerMove)
                    currentPlayerMove = -1
                }
            }
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
                    currentPlayer = currentPlayer,
                    offset = Offset(row, col),
                    onFirstMove = { index ->
                        if (currentPlayerMove == 6 && tokenPositions[currentPlayer][index] == -1) {
                            tokenPositions[currentPlayer][index] = 0
                            currentPlayerMove = -1
                        } else currentPlayer++
                    },
                    onMove = { newPosition ->
                        tokenPositions[currentPlayer].indexOf(newPosition).takeIf { it >= 0 }
                            ?.let { tokenIndex ->
                                movementInProgress = true
                                coroutineScope.launch {
                                    animateTokenMovement(
                                        tokenPositions = tokenPositions[currentPlayer],
                                        tokenIndex = tokenIndex,
                                        moveAmount = currentPlayerMove,
                                        playerPathSize = playerPaths[currentPlayer].size
                                    )
                                    movementInProgress = false
                                    currentPlayer = currentPlayer.nextPlayer(playersCount, currentPlayerMove)
                                    currentPlayerMove = -1
                                }
                            }
                    }
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = padding.dp ,
            vertical = (padding / 2).dp), horizontalArrangement = Arrangement.SpaceBetween) {
            DiceBox(homeColors = homeColors ,player = 0 , isActive = currentPlayer == 0) {
                currentPlayerMove = it
            }
            DiceBox(player = 1, homeColors = homeColors , isActive = currentPlayer == 1) {
                currentPlayerMove = it
            }
        }

        drawLudoBoard(
            modifier = modifier.width(screenSize.width.dp).aspectRatio(1f).clip(RoundedCornerShape(10.dp)).border(1.dp, getAnimatedBorderColor(), RoundedCornerShape(10.dp)),
            homeColors = homeColors,
            boardCellsSize = boardCellsSize,
        ) { startX, startY ->

            detectOverlaps(tokenPositions) { collisions ->
                movementInProgress.ifNotTrue {
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
                        Pair(
                            Offset(
                                col * boardCellsSize + boardCellsSize / 2,
                                row * boardCellsSize + boardCellsSize / 2
                            ), collisions[Pair(row, col)]?.size
                        )
                    }.toMutableStateList()
                }
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

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = padding.dp ,  vertical = (padding / 2).dp), horizontalArrangement = Arrangement.SpaceBetween) {
            DiceBox(player = 3, homeColors = homeColors , isActive = currentPlayer == 3){
                currentPlayerMove = it

            }
            DiceBox(player = 2, homeColors = homeColors , isActive = currentPlayer == 2){
                currentPlayerMove = it
            }
        }

        /**
         * is use less part for testing purpose only
         */

        var expanded by remember { mutableStateOf(false) }

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

@Composable
fun DiceBox(player: Int, homeColors: List<Color>, isActive: Boolean = true , onDiceRoll : (Int) -> Unit) {
    var diceRoll by remember { mutableStateOf(0) }
    val animatedBorder = getAnimatedBorderColor()

    Box(
        modifier = Modifier
            .size(65.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(homeColors[player], homeColors[player].copy(0.8f))
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .then(if (isActive) Modifier.clickable {
                diceRoll = (1..6).random()
                onDiceRoll(diceRoll)
            } else Modifier)
            .border(
                width = 1.dp,
                color = if (isActive) animatedBorder else Color.Gray,
                shape = RoundedCornerShape(10.dp)
            )
            .graphicsLayer {
                alpha = if (isActive) 1f else 0.5f // Fade effect for inactive dice
            },
        contentAlignment = Alignment.Center
    ) {
        // Animated number transition
        AnimatedContent(
            targetState = diceRoll,
            transitionSpec = {
                (scaleIn(initialScale = 0.5f) + fadeIn()) togetherWith fadeOut()
            },
            label = "Dice Roll Animation"
        ) { roll ->
            Text(
                text = roll.toString(),
                fontSize = 26.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


