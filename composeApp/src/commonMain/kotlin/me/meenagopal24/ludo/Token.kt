package me.meenagopal24.ludo
enum class Direction { UP, DOWN, LEFT, RIGHT }

data class Token(
    val player: Player,
    var position: Int = -1, // -1 means in home, 0 is start, 56 is goal
    var isAtHome: Boolean = true
)

enum class Player { RED, GREEN, BLUE, YELLOW }

data class PlayerState(
    val player: Player,
    val tokens: List<Token> = List(4) { Token(player) },
    var hasTurn: Boolean = false
)

data class GameState(
    val players: List<PlayerState> = listOf(
        PlayerState(Player.RED),
        PlayerState(Player.GREEN),
        PlayerState(Player.BLUE),
        PlayerState(Player.YELLOW)
    ),
    var currentPlayerIndex: Int = 0,
    var diceValue: Int = 0
)
