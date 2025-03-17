package me.meenagopal24.ludo.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.alexzhirkevich.compottie.LottieClipSpec
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieAnimatable
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.launch
import me.meenagopal24.ludo.media.createAudioPlayer
import me.meenagopal24.ludo.utils.debounceClickable
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
    val onDiceRolled by viewModel.onDiceRolled.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = padding.dp, vertical = (padding / 2).dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DiceBox(
            homeColors = homeColors,
            player = startIndex,
            onDiceRolled = onDiceRolled,
            isActive = currentPlayer == startIndex && viewModel.currentMove.value == -1
        ) {
            viewModel.updateCurrentMove(it)
            viewModel.setOnDiceRolled(true)
        }
        DiceBox(
            player = endIndex,
            homeColors = homeColors,
            onDiceRolled = onDiceRolled,
            isActive = currentPlayer == endIndex && viewModel.currentMove.value == -1
        ) {
            viewModel.updateCurrentMove(it)
            viewModel.setOnDiceRolled(true)
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun DiceBox(
    player: Int,
    homeColors: List<Color>,
    isActive: Boolean = true,
    onDiceRolled: Boolean,
    onDiceRoll: (Int) -> Unit,
) {
    val Images = listOf(
        "https://static.vecteezy.com/system/resources/thumbnails/019/900/306/small_2x/happy-young-cute-illustration-face-profile-png.png",
        "https://img.freepik.com/premium-photo/3d-close-up-portrait-smiling-man_175690-201.jpg?semt=ais_hybrid",
        "https://static.vecteezy.com/system/resources/thumbnails/011/490/381/small_2x/happy-smiling-young-man-avatar-3d-portrait-of-a-man-cartoon-character-people-illustration-isolated-on-white-background-vector.jpg",
        "https://static.vecteezy.com/system/resources/previews/009/397/835/non_2x/man-avatar-clipart-illustration-free-png.png"
    )
    val scope = rememberCoroutineScope()
    var diceNumber by remember { mutableStateOf(0) }
    val audioPlayer = remember { createAudioPlayer() }
    val diceRollUri = Res.getUri("files/diceroll_1.mp3")
    var isRolling = onDiceRolled

    val composition by key(diceNumber) {
        rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/dice_$diceNumber.json").decodeToString()
            )
        }
    }

    val animation = rememberLottieAnimatable()
    val animatedBorder = getAnimatedBorderColor()

    var blinkAlpha by remember { mutableStateOf(1f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                blinkAlpha = 0.6f
                kotlinx.coroutines.delay(400)
                blinkAlpha = 1f
                kotlinx.coroutines.delay(400)
            }
        }
    }

    LaunchedEffect(diceNumber) {
        if (diceNumber == 0) {
            animation.snapTo(composition, 1f)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        if (player == 1 || player == 2) ProfileImage(url = Images[player])

        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
        ) {
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .background(Color.Black.copy(0.5f), RoundedCornerShape(10.dp))
                    .size(70.dp).border(
                        width = if (isActive) 2.dp else 1.dp,
                        color = if (isActive) animatedBorder else Color.Gray,
                        shape = RoundedCornerShape(10.dp)
                    ).then(
                        if (isActive) Modifier.debounceClickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            if (isRolling.not()) {
                                scope.launch {
                                    diceNumber = listOf(1, 2, 6, 3, 4, 5, 6).random()
                                    isRolling = true
                                    audioPlayer.stop()
                                    audioPlayer.play(diceRollUri)
                                    animation.animate(
                                        composition,
                                        initialProgress = 0f,
                                        speed = 2f,
                                        clipSpec = LottieClipSpec.Progress(0f, 0.85f)
                                    )
                                    onDiceRoll(diceNumber)
                                }
                            }

                        } else Modifier.alpha(0.5f)), painter = rememberLottiePainter(
                    forceOffscreenRendering = true,
                    clipTextToBoundingBoxes = false,
                    clipToCompositionBounds = false,
                    composition = composition,
                    progress = { animation.progress },
                ), contentDescription = "Lottie dice animation"
            )
        }

        if (player == 0 || player == 3) ProfileImage(url = Images[player])
    }
}


@Composable
fun ProfileImage(
    url: String = "https://images.pexels.com/photos/29821200/pexels-photo-29821200/free-photo-of-silhouette-of-man-against-a-pink-sky-at-sunset.jpeg",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentScale = ContentScale.Crop,
            contentDescription = "",
            modifier = Modifier.matchParentSize()
        )
    }
}
