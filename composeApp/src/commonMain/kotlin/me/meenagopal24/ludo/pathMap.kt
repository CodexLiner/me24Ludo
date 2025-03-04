package me.meenagopal24.ludo

val pathMap = mapOf(
    Player.RED to listOf(0, 1, 2, /* ..., */ 56),
    Player.GREEN to listOf(13, 14, 15, /* ..., */ 56),
    Player.BLUE to listOf(26, 27, 28, /* ..., */ 56),
    Player.YELLOW to listOf(39, 40, 41, /* ..., */ 56)
)

fun moveToken(token: Token, diceRoll: Int): Boolean {
    if (token.isAtHome) {
        if (diceRoll == 6) {
            token.position = 0 // Move out of home
            token.isAtHome = false
            return true
        }
        return false
    } else {
        val newPosition = token.position + diceRoll
        if (newPosition <= 56) {
            token.position = newPosition
            return true
        }
    }
    return false
}
