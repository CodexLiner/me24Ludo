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
import me.meenagopal24.ludo.utils.playerOrders
import me.meenagopal24.ludo.utils.safeZoneIndexed
import me.meenagopal24.ludo.utils.safeZones
import multiplatform_app.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class Me24LudoBoardViewModel : ViewModel() {
    private val audioPlayer: AudioPlayer = createAudioPlayer()
    private var stepUri = Res.getUri("files/step.mp3")
    private var safeZoneUri = Res.getUri("files/safe.mp3")
    private var winningZoneUri = Res.getUri("files/panta.mp3")
    private var deathUri = Res.getUri("files/death.mp3")

    val currentPlayer = MutableStateFlow(0)
    val currentMove = MutableStateFlow(-1)
    val movementInProgress = MutableStateFlow(false)
    private val keepAlive = MutableStateFlow(false)
    val onDiceRolled = MutableStateFlow(false)
    var playersCount = MutableStateFlow(4)
    private val tempSafeZones = MutableStateFlow<MutableSet<Pair<Int, Int>>>(mutableSetOf())

    val tokenPositions = MutableStateFlow(List(4) { mutableStateListOf(-1, -1, -1, -1) })
    val playerPaths =  listOf(getPlayerOnePath(), getPlayerTwoPath(), getPlayerThreePath(), getPlayerFourPath())


    fun setCurrentPlayer(player: Int) {
        currentPlayer.value = player
    }

    fun resetCurrentMove(value : Int = -1) {
        currentMove.value = value
        setOnDiceRolled(false)
    }

    fun updateCurrentMove(move: Int) {
        currentMove.value = move
    }

    fun setOnDiceRolled(b: Boolean) {
        onDiceRolled.value = b
    }

    private fun isMoving(b: Boolean , keepAlive : Boolean = false) {
        movementInProgress.value = b
        this.keepAlive.value = keepAlive
    }
    fun setPlayerCount(playersCount: Int) {
        this.playersCount.value = playersCount
        currentPlayer.value = playersCount.playerOrders().first()
    }

    fun autoMovePlayer(tokenIndex: Int) {
        viewModelScope.launch {
            isMoving(b = true , keepAlive = false)
            val startPos = tokenPositions.value[currentPlayer.value][tokenIndex]
            val calculatedEndPos = startPos + currentMove.value
            val pathSize = playerPaths[currentPlayer.value].size
            if (calculatedEndPos >= pathSize) {
                isMoving(false)
                return@launch
            }

            val endPos = calculatedEndPos.coerceAtMost(pathSize - 1)

            for (pos in (startPos + 1)..endPos) {
                tokenPositions.value[currentPlayer.value][tokenIndex] = pos
                when (pos) {
                    in setOf(56) -> audioPlayer.play(winningZoneUri)
                    in safeZoneIndexed -> audioPlayer.play(if (pos == endPos) safeZoneUri else stepUri)
                    else -> audioPlayer.play(stepUri)
                }
                delay(400)
            }

            isMoving(false)
            delay(50) // little delay for checking collisions
            if (keepAlive.value.not()) setCurrentPlayer(currentPlayer.value.nextPlayer(playersCount.value, currentMove.value))
            resetCurrentMove()
        }
    }

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
                }
            },
            onMove = { newPosition ->
                tokenPositions.value[currentPlayer].indexOf(newPosition).takeIf { it >= 0 }
                    ?.let { tokenIndex ->
                        autoMovePlayer(tokenIndex)
                    }
            })

    }

    fun handleCollisions(collisions: MutableMap<Pair<Int, Int>, MutableList<Triple<Int, Int, Int>>>) {
        Logger.d("CollisionAre : $collisions and safe ${tempSafeZones.value}")

        /**
         * clear temp safe zones
         */
        if (collisions.isEmpty()) tempSafeZones.value = mutableSetOf()
        tempSafeZones.value = tempSafeZones.value.filter { pair -> pair in collisions.keys }.toMutableSet()

        /**
         * handle collision zones
         */
        collisions.forEach { (position, tripletList) ->
            if (position in safeZones) return@forEach
            if (tripletList.size == 2 && tempSafeZones.value.contains(position).not()) {
                val (first, second) = tripletList
                val (player1, token1, _) = first
                val (player2, token2, _) = second

                if (player1 != player2) {
                    val rowIndex = if (player1 == currentPlayer.value) player2 else player1
                    val colIndex = if (player1 == currentPlayer.value) token2 else token1

                    val currentValue = tokenPositions.value[rowIndex][colIndex]
                    isMoving(b = true, keepAlive = true)
                    viewModelScope.launch {
                        audioPlayer.stop()
                        audioPlayer.play(deathUri)
                        /**
                         * to keep current player still on game
                         */
                        for (i in currentValue downTo -1) {
                            tokenPositions.value[rowIndex][colIndex] = i
                            delay(2)
                        }
                        isMoving(false)

                    }
                }
            } else if (tripletList.size > 2) {
                tempSafeZones.value.add(position)
            }
        }
    }

}