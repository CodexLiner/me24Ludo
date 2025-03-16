package me.meenagopal24.ludo.game

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.media.AudioPlayer
import me.meenagopal24.ludo.media.createAudioPlayer
import me.meenagopal24.ludo.move.PlayerMovement
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath
import me.meenagopal24.ludo.utils.nextPlayer
import me.meenagopal24.ludo.utils.safeZones
import multiplatform_app.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class Me24LudoBoardViewModel : ViewModel() {
    val audioPlayer: AudioPlayer = createAudioPlayer()
    val currentPlayer = MutableStateFlow(0)
    val currentMove = MutableStateFlow(-1)
    val movementInProgress = MutableStateFlow(false)
    private var playersCount = 4

    val tokenPositions = MutableStateFlow(List(4) { mutableStateListOf(-1, -1, -1, -1) })
    val playerPaths =  listOf(getPlayerOnePath(), getPlayerTwoPath(), getPlayerThreePath(), getPlayerFourPath())


    fun setCurrentPlayer(player: Int) {
        currentPlayer.value = player
    }

    fun resetCurrentMove() {
        currentMove.value = -1
    }

    fun updateCurrentMove(move: Int) {
        currentMove.value = move
    }

    fun isMoving(b: Boolean) {
        movementInProgress.value = b
    }
    fun setPlayerCount(playersCount: Int) {
        this.playersCount = playersCount
    }

    @OptIn(ExperimentalResourceApi::class)
    fun autoMovePlayer(tokenIndex : Int) {
        viewModelScope.launch {
            isMoving(true)
            val startPos = tokenPositions.value[currentPlayer.value][tokenIndex]
            val endPos = (startPos + currentMove.value.coerceAtMost(playerPaths[currentPlayer.value].size - 1))
            for (pos in startPos..endPos) {
                audioPlayer.play(Res.getUri("files/step.mp3"))
                tokenPositions.value[currentPlayer.value][tokenIndex] = pos
                delay(300)
            }
            tokenPositions.emit(tokenPositions.value)
            isMoving(false)
            setCurrentPlayer(currentPlayer.value.nextPlayer(playersCount, currentMove.value))
            resetCurrentMove()
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    fun movePlayer(
        currentPlayer: Int, boardCellsSize: Float, offset: Offset
    ) {
        val (col, row) = offset.x / boardCellsSize to offset.y / boardCellsSize
        PlayerMovement.movePlayer(
            currentPlayer = currentPlayer,
            offset = Offset(row, col),
            onFirstMove = { index ->
                if (currentMove.value == 6 && tokenPositions.value[currentPlayer][index] == -1) {
                    tokenPositions.value[currentPlayer][index] = 0
                    resetCurrentMove()
                } else setCurrentPlayer(currentPlayer + 1)
            },
            onMove = { newPosition ->
                tokenPositions.value[currentPlayer].indexOf(newPosition).takeIf { it >= 0 }
                    ?.let { tokenIndex ->
                        autoMovePlayer(tokenIndex)
                    }
            })

    }

    fun handleCollisions(collisions: MutableMap<Pair<Int, Int>, MutableList<Triple<Int, Int, Int>>>) {
        collisions.forEach { (position, tripletList) ->
            val (row, col) = position

            if (Pair(col, row) in safeZones) return@forEach

            if (tripletList.size == 2) {
                val (first, second) = tripletList
                val (player1, token1, _) = first
                val (player2, token2, _) = second
                if (player1 != player2) {
                    tokenPositions.value[if (player1 == currentPlayer.value) player2 else player1][if (player1 == currentPlayer.value) token2 else token1] =
                        -1
                }
            }
        }
    }

}