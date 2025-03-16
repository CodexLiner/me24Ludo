package me.meenagopal24.ludo.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.compottie.LottieClipSpec
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieAnimatable
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.utils.getAnimatedBorderColor
import multiplatform_app.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

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


@OptIn(ExperimentalResourceApi::class)
@Composable
fun DiceBox(
    player: Int, homeColors: List<Color>, isActive: Boolean = true, onDiceRoll: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var diceNumber by remember { mutableStateOf(1) }

    val composition by key(diceNumber) {
        rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/dice_$diceNumber.json").decodeToString()
            )
        }
    }

    val animation = rememberLottieAnimatable()

    val animatedBorder = getAnimatedBorderColor()

    Box(
        modifier = Modifier.size(70.dp).background(
            brush = Brush.verticalGradient(
                colors = listOf(homeColors[player], homeColors[player].copy(0.8f))
            ), shape = RoundedCornerShape(10.dp)
        ).border(
            width = 1.dp,
            color = if (isActive) animatedBorder else Color.Gray,
            shape = RoundedCornerShape(10.dp)
        ).graphicsLayer { alpha = if (isActive) 1f else 0.5f },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize().then(
                if (isActive) Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    scope.launch {
                        diceNumber = (1..6).random()
                        animation.animate(composition, initialProgress = 0f , speed = 2f , clipSpec = LottieClipSpec.Progress(0f, 0.9f))
                        onDiceRoll(diceNumber)
                    }
                } else Modifier), painter = rememberLottiePainter(
                forceOffscreenRendering = true,
                composition = composition,
                progress = { animation.progress },
            ), contentDescription = "Lottie dice animation"
        )
    }
}