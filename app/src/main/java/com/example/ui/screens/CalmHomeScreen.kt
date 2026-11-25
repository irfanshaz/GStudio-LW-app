package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivityProgressEntity
import com.example.model.AgeBand
import com.example.model.LearningBuddy
import com.example.ui.theme.*
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel

@Composable
fun HomeScreen(viewModel: WonderViewModel, ageBand: AgeBand, buddy: LearningBuddy,
    progressList: List<ActivityProgressEntity>, reducedMotion: Boolean) {
    var catalog by remember { mutableStateOf(false) }
    if (catalog) {
        DetailedHomeScreen(viewModel, ageBand, buddy, progressList, reducedMotion)
        return
    }
    BoxWithConstraints(Modifier.fillMaxSize().background(SoftCreamCard).testTag("home_screen")) {
        val portrait = maxHeight > maxWidth
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Let's play", style = MaterialTheme.typography.headlineMedium,
                    color = DeepSlateInk, modifier = Modifier.weight(1f))
                ParentHoldTarget("home_parent_hold") { viewModel.navigateTo(WonderScreen.ParentCorner(unlocked = true)) }
            }
            val labels = listOf("My toys", "Pictures & videos", "Story adventure", "All activities")
            val symbols = listOf("●", "▣", "✦", "+")
            val colors = listOf(ActionPlayGreen, ActionContinueBlue, LandCreativeViolet, ActionListenAmber)
            for (row in 0..1) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    for (col in 0..1) {
                        val i = row * 2 + col
                        Button(onClick = {
                            when (i) {
                                0 -> viewModel.navigateTo(WonderScreen.ToyBox)
                                1 -> viewModel.navigateTo(WonderScreen.RealWorldHub())
                                2 -> viewModel.startGarden()
                                else -> catalog = true
                            }
                        }, modifier = Modifier.weight(1f).heightIn(min = if (portrait) 150.dp else 100.dp)
                            .testTag("calm_home_tile_$i"), shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors[i].copy(alpha = 0.14f),
                                contentColor = DeepSlateInk)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(symbols[i], fontSize = 30.sp, color = colors[i])
                                Text(labels[i], fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
