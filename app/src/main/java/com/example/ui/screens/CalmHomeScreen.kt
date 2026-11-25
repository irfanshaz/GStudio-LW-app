package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
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
    var options by remember { mutableStateOf(false) }
    if (catalog) {
        LegacyHomeScreen(viewModel, ageBand, buddy, progressList, reducedMotion)
        return
    }
    BoxWithConstraints(Modifier.fillMaxSize().background(SoftCreamCard).testTag("home_screen")) {
        val columns = if (maxWidth >= 600.dp) 4 else 2
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Let's play", style = MaterialTheme.typography.headlineMedium,
                    color = DeepSlateInk, modifier = Modifier.weight(1f))
                ParentHoldTarget("home_parent_hold") { options = true }
            }
            val labels = listOf("My toys", "Pictures & videos", "Story adventure", "All activities")
            val pictures = listOf(R.drawable.atlas_everyday_ball, R.drawable.pack01_elephant, R.drawable.atlas_everyday_book, R.drawable.pack01_star)
            val colors = listOf(ActionPlayGreen, ActionContinueBlue, LandCreativeViolet, ActionListenAmber)
            for (row in 0 until (4 / columns)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    for (col in 0 until columns) {
                        val i = row * columns + col
                        Button(onClick = {
                            when (i) {
                                0 -> viewModel.navigateTo(WonderScreen.ToyBox)
                                1 -> viewModel.navigateTo(WonderScreen.RealWorldHub())
                                2 -> viewModel.startGarden()
                                else -> viewModel.navigateTo(WonderScreen.AllLands)
                            }
                        }, modifier = Modifier.weight(1f).heightIn(min = 140.dp)
                            .testTag("calm_home_tile_$i"), shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors[i].copy(alpha = 0.14f),
                                contentColor = DeepSlateInk)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Image(painterResource(pictures[i]), contentDescription = labels[i], modifier = Modifier.size(60.dp))
                                Text(labels[i], fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
    if (options) CalmParentSheet("Home options", { options = false }) {
        CalmAction("Parent Corner") { options = false; viewModel.navigateTo(WonderScreen.ParentCorner(unlocked = true)) }
        CalmAction("Surprise play") { options = false; viewModel.launchSurprisePlay() }
        CalmAction("Hear my learning buddy") { viewModel.audio.speakInstruction(buddy.greeting) }
        CalmAction("Classic home and progress view") { options = false; catalog = true }
    }

}
