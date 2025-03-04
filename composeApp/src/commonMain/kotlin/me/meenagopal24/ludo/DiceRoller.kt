package me.meenagopal24.ludo

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import multiplatform_app.composeapp.generated.resources.Res
import multiplatform_app.composeapp.generated.resources.dice_1
import multiplatform_app.composeapp.generated.resources.dice_2
import multiplatform_app.composeapp.generated.resources.dice_3
import multiplatform_app.composeapp.generated.resources.dice_4
import multiplatform_app.composeapp.generated.resources.dice_5
import multiplatform_app.composeapp.generated.resources.dice_6
import org.jetbrains.compose.resources.painterResource

@Composable
fun LudoDice(modifier: Modifier = Modifier, onRollEnd: (Int) -> Unit = {}) {
    var diceNumber by remember { mutableStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isRolling) 720f else 0f, // Rotate twice for effect
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        finishedListener = {
            isRolling = false
            diceNumber = (1..6).random() // Set new dice value after animation
            onRollEnd(diceNumber)
        }
    )

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer(rotationX = rotation, rotationY = rotation)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .clickable {
                if (!isRolling) {
                    isRolling = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        DiceFace(number = diceNumber)
    }
}

@Composable
fun DiceFace(number: Int) {
    val diceImages = listOf(
        Res.drawable.dice_1, Res.drawable.dice_2, Res.drawable.dice_3,
        Res.drawable.dice_4, Res.drawable.dice_5, Res.drawable.dice_6
    )

    Image(
        painter = painterResource(diceImages[number - 1]),
        contentDescription = "Dice $number",
        modifier = Modifier.size(80.dp)
    )
}


fun DrawScope.drawDiceDots(value: Int, size: Size) {
    val dotRadius = size.minDimension / 10
    val positions = mapOf(
        1 to listOf(Offset(size.width / 2, size.height / 2)),
        2 to listOf(Offset(size.width / 4, size.height / 4), Offset(3 * size.width / 4, 3 * size.height / 4)),
        3 to listOf(Offset(size.width / 4, size.height / 4), Offset(size.width / 2, size.height / 2), Offset(3 * size.width / 4, 3 * size.height / 4)),
        4 to listOf(Offset(size.width / 4, size.height / 4), Offset(3 * size.width / 4, size.height / 4), Offset(size.width / 4, 3 * size.height / 4), Offset(3 * size.width / 4, 3 * size.height / 4)),
        5 to listOf(Offset(size.width / 4, size.height / 4), Offset(3 * size.width / 4, size.height / 4), Offset(size.width / 2, size.height / 2), Offset(size.width / 4, 3 * size.height / 4), Offset(3 * size.width / 4, 3 * size.height / 4)),
        6 to listOf(Offset(size.width / 4, size.height / 6), Offset(3 * size.width / 4, size.height / 6), Offset(size.width / 4, size.height / 2), Offset(3 * size.width / 4, size.height / 2), Offset(size.width / 4, 5 * size.height / 6), Offset(3 * size.width / 4, 5 * size.height / 6))
    )
    positions[value]?.forEach { drawCircle(Color.Black, dotRadius, it) }
}