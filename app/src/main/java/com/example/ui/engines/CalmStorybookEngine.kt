package com.example.ui.engines

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RealWorldMediaRegistry
import com.example.model.ActivityConfig
import com.example.model.StoryPageSpec
import com.example.ui.screens.CalmAction
import com.example.ui.screens.CalmParentSheet
import com.example.ui.screens.ParentHoldTarget
import com.example.ui.theme.*
import com.example.viewmodel.WonderViewModel
import kotlinx.coroutines.delay

/** Original pages and read-along assessment remain available; narrated play needs no reading. */
@Composable
fun StorybookReaderEngine(activity: ActivityConfig, replayKey: Int, viewModel: WonderViewModel) {
    var readAlong by remember(activity.id) { mutableStateOf(false) }
    val toolsState: MutableState<Boolean> = com.example.ui.screens.LocalActivityTools.current ?: remember(activity.id) { mutableStateOf(false) }
    var options by toolsState
    var page by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var acted by remember(activity.id, page, replayKey) { mutableStateOf(false) }
    var finished by remember(activity.id, replayKey) { mutableStateOf(false) }
    var awarded by remember(activity.id, replayKey) { mutableStateOf(false) }
    val settings by viewModel.settingsState.collectAsState()
    val pages = activity.storyPages.ifEmpty { listOf(StoryPageSpec(1, activity.title,
        activity.emoji, activity.instructionText, "Tap to explore", listOf("Explore"))) }
    val current = pages[page.coerceIn(0, pages.lastIndex)]
    val doorSafety = activity.id == "w20_safety_1"
    val action = if (doorSafety) when (page) {
        0 -> "Keep the door closed"
        1 -> "Call a grown-up"
        else -> "Together with a grown-up"
    } else current.choiceOptions.getOrNull(current.correctChoiceIndex)
        ?.takeUnless { it.contains("Read Again", true) } ?: "Turn the page"

    LaunchedEffect(activity.id, page, replayKey, readAlong) {
        if (!readAlong) viewModel.audio.speakStoryPage(current.narrationText,
            if (doorSafety) "$action. Tap the picture." else "Tap the picture. $action")
    }
    DisposableEffect(activity.id) { onDispose { viewModel.audio.stopSpeechOnly() } }
    LaunchedEffect(acted) {
        if (acted) {
            delay(if (settings.reducedMotion) 600 else 1200)
            if (page == pages.lastIndex) {
                if (!awarded) {
                    awarded = true
                    viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false, speakMilestonePraise = false)
                }
                finished = true
            } else page++
        }
    }

    Column(Modifier.fillMaxSize().testTag("engine_storybook_reader")) {
        if (readAlong) {
            LegacyStorybookReaderEngine(activity, replayKey, viewModel)
        } else {
            Crossfade(targetState = page, modifier = Modifier.weight(1f), label = "story_scene",
                animationSpec = tween(if (settings.reducedMotion) 0 else 350)) { scenePage ->
                val scene = pages[scenePage.coerceIn(0, pages.lastIndex)]
                val art = RealWorldMediaRegistry.allMediaItems.firstOrNull {
                    scene.emojiScene.contains(it.emoji) && it.bundledDrawableRes != null
                }?.bundledDrawableRes
                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(28.dp)).background(SoftCreamCard)
                    .semantics { contentDescription = action }
                    .clickable(enabled = !acted) { acted = true }
                    .testTag("story_scene_action"), contentAlignment = Alignment.Center) {
                    if (doorSafety) {
                        DoorSafetyScene(scenePage, acted, settings.reducedMotion)
                    } else if (art != null) {
                        Image(painterResource(art), scene.title, modifier = Modifier.fillMaxSize().padding(24.dp),
                            contentScale = ContentScale.Fit)
                    } else {
                        // Calm vector story stage, no animated emoji animals standing in for supplied art.
                        StoryStage()
                        Icon(Icons.Default.TouchApp, action, tint = ActionContinueBlue,
                            modifier = Modifier.size(110.dp))
                    }
                    if (acted && scenePage == pages.lastIndex) {
                        Surface(Modifier.align(Alignment.BottomCenter).padding(24.dp),
                            shape = RoundedCornerShape(24.dp), color = PlaySurfaceWhite) {
                            Text("Well done!", modifier = Modifier.padding(24.dp), fontSize = 24.sp, color = DeepSlateInk)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            if (finished) {
                CalmAction("Again", "story_read_again_button") { page = 0; acted = false; finished = false }
            } else {
                CalmAction(action, "story_action_button") { if (!acted) acted = true }
            }
        }
    }
    if (options) CalmParentSheet("Story options", { options = false }) {
        Text(current.narrationText, color = DeepSlateInk)
        if (current.parentTip.isNotBlank()) Text(current.parentTip, color = DeepSlateInk)
        CalmAction(if (readAlong) "Listen and interact" else "Read-along questions") {
            readAlong = !readAlong; options = false
        }
        CalmAction("Hear this page", "story_read_aloud_button") {
            viewModel.audio.speakStoryPage(current.narrationText, current.interactivePrompt); options = false
        }
    }
}

@Composable
private fun StoryStage() {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(Brush.verticalGradient(listOf(Color(0xFFEAF3F4), Color(0xFFFFF7E9))))
        drawCircle(Color(0xFFE8C688).copy(alpha = .3f), size.minDimension * .3f,
            Offset(size.width * .8f, size.height * .2f))
        drawRoundRect(Color.White.copy(alpha = .6f), Offset(size.width * .12f, size.height * .18f),
            Size(size.width * .76f, size.height * .64f), CornerRadius(32f))
    }
}

/** The child never opens an unchecked door: only the final, grown-up-assisted scene opens. */
@Composable
private fun DoorSafetyScene(page: Int, acted: Boolean, reducedMotion: Boolean) {
    val opening by animateFloatAsState(if (page >= 2 && acted) 1f else 0f,
        animationSpec = tween(if (reducedMotion) 0 else 900), label = "adult_opens_door")
    val glow by animateFloatAsState(if (acted) 1f else 0f,
        animationSpec = tween(if (reducedMotion) 0 else 450), label = "safe_action_glow")
    Canvas(Modifier.fillMaxSize().testTag("door_safety_scene")) {
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(Color(0xFFF9F1E5), Color(0xFFE7DDD0))))
        drawRect(Color(0xFFCEC0AE), Offset(0f, h * .86f), Size(w, h * .14f))
        val dh = h * .76f; val dw = minOf(w * .28f, dh * .53f)
        val x = w * .36f - dw / 2f; val y = h * .1f
        drawRoundRect(Color(0xFFB6A794), Offset(x - 10, y - 10), Size(dw + 20, dh + 10), CornerRadius(14f))
        drawRect(Color(0xFFB5D2CA), Offset(x, y), Size(dw, dh))
        val doorWidth = dw * (1f - opening * .82f)
        drawRoundRect(Brush.horizontalGradient(listOf(Color(0xFF257F82), Color(0xFF185B62))),
            Offset(x, y), Size(doorWidth, dh), CornerRadius(8f))
        drawRoundRect(Color.White.copy(alpha = .18f), Offset(x + doorWidth * .12f, y + dh * .1f),
            Size(doorWidth * .74f, dh * .35f), CornerRadius(8f), style = Stroke(3f))
        drawRoundRect(Color.White.copy(alpha = .14f), Offset(x + doorWidth * .12f, y + dh * .55f),
            Size(doorWidth * .74f, dh * .32f), CornerRadius(8f), style = Stroke(3f))
        drawCircle(Color(0xFFE8C888), 7f, Offset(x + doorWidth * .84f, y + dh * .51f))
        // Bell ring is an illustrated cue; no flashing or continuous motion.
        drawRoundRect(Color(0xFFBDA984), Offset(x + dw + 24, y + dh * .42f), Size(22f, 42f), CornerRadius(8f))
        drawCircle(Color(0xFFFFE9AE), 5f, Offset(x + dw + 35, y + dh * .42f + 21))
        if (page == 0) {
            drawCircle(ActionListenAmber.copy(alpha = .2f + glow * .1f), dw * .26f,
                Offset(x + dw * .5f, y + dh * .4f), style = Stroke(5f))
        } else {
            // Deliberate adult silhouette, proportioned consistently with the room.
            val ax = w * .73f; val ay = h * .38f
            drawCircle(Color(0xFFB87A52), h * .07f, Offset(ax, ay))
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF6C65A8), Color(0xFF49446F))),
                Offset(ax - h * .085f, ay + h * .09f), Size(h * .17f, h * .3f), CornerRadius(h * .05f))
            drawLine(Color(0xFF49446F), Offset(ax - h * .04f, ay + h * .35f),
                Offset(ax - h * .05f, h * .86f), h * .035f)
            drawLine(Color(0xFF49446F), Offset(ax + h * .04f, ay + h * .35f),
                Offset(ax + h * .05f, h * .86f), h * .035f)
            if (acted) drawCircle(ActionPlayGreen.copy(alpha = .25f), h * .14f,
                Offset(ax, ay), style = Stroke(6f))
        }
    }
}
