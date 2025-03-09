package me.meenagopal24.ludo.move

import androidx.compose.ui.geometry.Offset
import co.touchlab.kermit.Logger
import me.meenagopal24.ludo.paths.getPlayerFourPath
import me.meenagopal24.ludo.paths.getPlayerOnePath
import me.meenagopal24.ludo.paths.getPlayerThreePath
import me.meenagopal24.ludo.paths.getPlayerTwoPath

object PlayerMovement {
    private val playerPaths = listOf(
        getPlayerOnePath(),
        getPlayerTwoPath(),
        getPlayerThreePath(),
        getPlayerFourPath()
    )
    private val playerHomePaths = listOf(
        listOf(Pair(1, 1), Pair(1, 3), Pair(3, 1), Pair(3, 3)),
        listOf(Pair(1, 10), Pair(1, 12), Pair(3, 10), Pair(3, 12)),
        listOf(Pair(10, 10), Pair(10, 12), Pair(12, 10), Pair(12, 12)),
        listOf(Pair(10, 1), Pair(10, 3), Pair(12, 1), Pair(12, 3)),
    )

    fun movePlayer(currentPlayer: Int, offset: Offset, onFirstMove : (Int) -> Unit, onMove: (Int) -> Unit) {
        val path = playerPaths[currentPlayer]
        if (path.isEmpty()) return
        if (path.indexOf(Pair(offset.x.toInt() , offset.y.toInt())) >= 0)
            onMove(path.indexOf(Pair(offset.x.toInt() , offset.y.toInt())))
        else {
            val homePath = playerHomePaths[currentPlayer]
            if (homePath.indexOf(Pair(offset.x.toInt() , offset.y.toInt())) >= 0){
                onFirstMove(homePath.indexOf(Pair(offset.x.toInt() , offset.y.toInt())))
            }
        }
    }
}