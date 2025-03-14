package me.meenagopal24.ludo.move

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath
import kotlin.math.roundToInt

object PlayerMovement {
    private val playerPaths = listOf(
        getPlayerOnePath(),
        getPlayerTwoPath(),
        getPlayerThreePath(),
        getPlayerFourPath()
    )
    private val playerHomePaths = listOf(
        listOf(Pair(2, 2), Pair(2, 4), Pair(4, 2), Pair(4, 4)),
        listOf(Pair(2, 11), Pair(2, 13), Pair(4, 11), Pair(4, 13)),
        listOf(Pair(11, 11), Pair(11, 13), Pair(13, 11), Pair(13, 13)),
        listOf(Pair(11, 2), Pair(11, 4), Pair(13, 2), Pair(13, 4)),
    )

    fun movePlayer(currentPlayer: Int, offset: Offset, onFirstMove : (Int) -> Unit, onMove: (Int) -> Unit) {
        val path = playerPaths[currentPlayer]
        if (path.isEmpty()) return
        if (path.indexOf(Pair(offset.x.toInt() , offset.y.toInt())) >= 0)
            onMove(path.indexOf(Pair(offset.x.toInt() , offset.y.toInt())))
        else {
            val homePath = playerHomePaths[currentPlayer]
            val roundedOffset = Pair(offset.x.roundToInt(), offset.y.roundToInt())
            val matchedIndex = homePath.indexOf(roundedOffset)
            if (matchedIndex >= 0) {
                onFirstMove(matchedIndex)
            }
        }
    }
}