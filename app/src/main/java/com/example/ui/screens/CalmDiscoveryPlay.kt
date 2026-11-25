package com.example.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CustomMediaOverrideEntity
import com.example.model.RealWorldMediaItem
import com.example.ui.theme.*
import com.example.viewmodel.WonderViewModel
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.first

@Composable
private fun DiscoveryPicture(item: RealWorldMediaItem, override: CustomMediaOverrideEntity?, modifier: Modifier) {
    val custom = override?.localImagePath?.let(::File)?.takeIf { it.exists() }
    when {
        custom != null -> AsyncImage(custom, item.title, modifier, contentScale = ContentScale.Fit)
        item.bundledDrawableRes != null -> Image(painterResource(item.bundledDrawableRes), item.title,
            modifier, contentScale = ContentScale.Fit)
        else -> Box(modifier, contentAlignment = Alignment.Center) {
            Text(item.title, color = DeepSlateInk, fontSize = 28.sp)
        }
    }
}

@Composable
fun CalmFlashcards(items: List<RealWorldMediaItem>, overrides: Map<String, CustomMediaOverrideEntity>,
    viewModel: WonderViewModel) {
    if (items.isEmpty()) { Text("No cards in this collection yet."); return }
    val pager = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()
    var sequence by remember { mutableStateOf<Job?>(null) }
    DisposableEffect(Unit) { onDispose { sequence?.cancel(); viewModel.audio.stopAllAudio() } }
    var lastTap by remember { mutableLongStateOf(0L) }
    fun speak(item: RealWorldMediaItem) {
        val now = SystemClock.elapsedRealtime()
        if (sequence?.isActive == true || now - lastTap < 500) return
        lastTap = now
        sequence = scope.launch {
            viewModel.audio.speakInstruction(item.title)
            delay(300)
            withTimeoutOrNull(15000) { viewModel.audio.isTeacherSpeaking.first { !it } }
            item.bundledSoundRes?.let { viewModel.audio.playDiscoveryRecording(it) }
            while (viewModel.audio.isDiscoveryRecordingPlaying()) delay(100)
        }
    }
    LaunchedEffect(pager.settledPage) {
        sequence?.cancel()
        viewModel.audio.stopAllAudio()
        lastTap = 0L
        speak(items[pager.settledPage])
    }
    HorizontalPager(pager, modifier = Modifier.fillMaxSize().testTag("calm_flashcards"),
        beyondViewportPageCount = 1) { index ->
        val item = items[index]
        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(Modifier.weight(1f).fillMaxWidth().clickable { speak(item) },
                shape = RoundedCornerShape(28.dp), color = SoftCreamCard) {
                DiscoveryPicture(item, overrides[item.id], Modifier.fillMaxSize().padding(24.dp))
            }
            Text(item.title, color = DeepSlateInk, fontSize = 22.sp, modifier = Modifier.padding(12.dp))
        }
    }
}

/** Only picture-backed cards participate: a reading label is never used as a quiz answer. */
@Composable
fun CalmPictureChoices(items: List<RealWorldMediaItem>, overrides: Map<String, CustomMediaOverrideEntity>,
    viewModel: WonderViewModel) {
    val pool = remember(items, overrides) { items.filter {
        it.bundledDrawableRes != null || overrides[it.id]?.localImagePath?.let(::File)?.exists() == true
    } }
    if (pool.size < 2) { Text("Add at least two pictures to play this collection.", color = DeepSlateInk); return }
    var round by remember(pool) { mutableIntStateOf(0) }
    var answered by remember(round) { mutableStateOf(false) }
    var feedback by remember(round) { mutableStateOf("") }
    val target = pool[round % pool.size]
    val choices = remember(round, pool) { listOf(target, pool[(round + 1) % pool.size]).shuffled() }
    LaunchedEffect(round) { viewModel.audio.speakInstruction("Where is ${target.title}?") }
    LaunchedEffect(answered) { if (answered) {
        delay(1400)
        withTimeoutOrNull(15000) { viewModel.audio.isTeacherSpeaking.first { !it } }
        round++
    } }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(feedback.ifBlank { "Where is ${target.title}?" }, color = DeepSlateInk,
            style = MaterialTheme.typography.titleLarge)
        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            choices.forEachIndexed { index, item ->
                Surface(Modifier.weight(1f).fillMaxHeight().heightIn(min = 90.dp)
                    .clip(RoundedCornerShape(28.dp)).clickable(enabled = !answered) {
                        if (item.id == target.id) {
                            answered = true; feedback = "You found it!"
                            viewModel.audio.speakInstruction(item.title)
                        } else {
                            feedback = "Let's try again"
                            viewModel.audio.speakInstruction("That's ${item.title}. Find ${target.title}.")
                        }
                    }.testTag("picture_choice_$index"), color = SoftCreamCard,
                    shape = RoundedCornerShape(28.dp)) {
                    DiscoveryPicture(item, overrides[item.id], Modifier.fillMaxSize().padding(24.dp))
                }
            }
        }
    }
}
