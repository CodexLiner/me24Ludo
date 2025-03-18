package me.meenagopal24.ludo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import me.meenagopal24.ludo.ui.navigation.Screens

@Composable
fun LudoHomeScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Play Ludo", fontSize = 20.sp)
        }
        
        if (showDialog) {
            PlayerSelectionDialog(
                onDismiss = { showDialog = false },
                onPlayerSelected = { playerCount ->
                    showDialog = false
                    navController.navigate("${Screens.LudoGameScreen.route}/$playerCount")
                }
            )
        }
    }
}

@Composable
fun PlayerSelectionDialog(
    onDismiss: () -> Unit,
    onPlayerSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        title = { Text("Select Number of Players", fontSize = 20.sp) },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (2..4).forEach { count ->
                    Button(
                        onClick = { onPlayerSelected(count) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("${count}P", fontSize = 18.sp)
                    }
                }
            }
        },
        confirmButton = {}
    )
}

