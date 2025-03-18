package me.meenagopal24.ludo


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.compose.rememberNavController
import me.meenagopal24.ludo.theme.AppTheme
import me.meenagopal24.ludo.ui.navigation.LudoAppNavHost
import multiplatform_app.composeapp.generated.resources.Res
import multiplatform_app.composeapp.generated.resources.square_bg
import org.jetbrains.compose.resources.painterResource

fun String.toColor(): Color {
    val hex = removePrefix("#")
    return Color(hex.toLong(16) or (if (hex.length == 6) 0xFF000000 else 0x00000000))
}

@Composable
internal fun App() = AppTheme {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().paint(painterResource(Res.drawable.square_bg) , contentScale = ContentScale.Crop).background(
                color = Color.Black.copy(0.65f)
            )
        ) {
            val navController = rememberNavController()
            LudoAppNavHost(navController)
        }
    }
}