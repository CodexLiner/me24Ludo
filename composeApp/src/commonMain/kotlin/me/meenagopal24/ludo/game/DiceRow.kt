package me.meenagopal24.ludo.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.meenagopal24.ludo.utils.getAnimatedBorderColor

@Composable
fun DiceRow(
    startIndex: Int = 0,
    endIndex: Int = 1,
    padding: Float,
    homeColors: List<Color>,
    currentPlayer: Int,
    viewModel: Me24LudoBoardViewModel,
) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = padding.dp , vertical = (padding / 2).dp), horizontalArrangement = Arrangement.SpaceBetween) {
        DiceBox(homeColors = homeColors ,player = startIndex , isActive = currentPlayer == startIndex) {
            viewModel.updateCurrentMove(it)
        }
        DiceBox(player = endIndex, homeColors = homeColors , isActive = currentPlayer == endIndex) {
            viewModel.updateCurrentMove(it)
        }
    }
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