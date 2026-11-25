package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ActivityConfig
import com.example.ui.theme.*
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel
import kotlinx.coroutines.withTimeoutOrNull

val LocalActivityTools = compositionLocalOf<MutableState<Boolean>?> { null }

/** A full sized target with a quiet visual mark. Unlock only after a continuous 3s hold. */
@Composable
fun ParentHoldTarget(tag: String = "parent_hold", onUnlock: () -> Unit) {
    val action by rememberUpdatedState(onUnlock)
    Box(Modifier.size(90.dp).testTag(tag)
        .semantics { contentDescription = "Grown-ups: hold for three seconds" }
        .pointerInput(Unit) {
            detectTapGestures(onPress = {
                val released = withTimeoutOrNull(3000L) { tryAwaitRelease() }
                if (released == null) action()
            })
        }, contentAlignment = Alignment.Center) {
        Icon(Icons.Default.Lock, contentDescription = null,
            tint = MutedSlateText, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun CalmAction(label: String, tag: String = label, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp).testTag(tag),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue)) {
        Text(label, fontSize = 18.sp)
    }
}

@Composable
fun CalmParentSheet(title: String, onDismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = SoftCreamCard) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = DeepSlateInk)
                content()
                CalmAction("Return to play", "calm_sheet_close", onDismiss)
            }
        }
    }
}

@Composable
fun CalmActivityHeader(activity: ActivityConfig, viewModel: WonderViewModel,
    isFavorite: Boolean, onTools: (() -> Unit)? = null, onReplay: () -> Unit) {
    var menu by remember(activity.id) { mutableStateOf(false) }
    val caption by viewModel.audio.lastSpokenCaption.collectAsState()
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        ParentHoldTarget("activity_parent_hold") { menu = true }
        Text(activity.title, modifier = Modifier.weight(1f), maxLines = 2,
            style = MaterialTheme.typography.titleMedium, color = DeepSlateInk)
        FilledIconButton(onClick = { viewModel.audio.speakInstruction(caption.ifBlank { activity.instructionText }) },
            modifier = Modifier.size(90.dp).testTag("activity_listen_button"),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = ActionListenAmber)) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, "Hear instructions", tint = DeepSlateInk,
                modifier = Modifier.size(34.dp))
        }
    }
    if (menu) CalmParentSheet("Activity options", { menu = false }) {
        if (caption.isNotBlank()) Text(caption, color = DeepSlateInk)
        if (onTools != null) CalmAction("Activity options", "activity_tools_button") { menu = false; onTools() }
        CalmAction("Show me", "activity_show_me_button") { menu = false; viewModel.triggerShowMeDemo(activity) }
        CalmAction("Play again", "activity_replay_button") { menu = false; onReplay() }
        CalmAction(if (isFavorite) "Remove from My Shelf" else "Save to My Shelf", "activity_favorite_button") {
            viewModel.toggleFavorite(activity)
        }
        CalmAction("Next activity", "activity_next_button") { menu = false; viewModel.launchNextActivityInWorld(activity) }
        CalmAction("Back", "activity_back_button") { menu = false; viewModel.navigateBack() }
        CalmAction("Home", "activity_home_button") { menu = false; viewModel.navigateHome() }
        CalmAction("Parent Corner") { menu = false; viewModel.navigateTo(WonderScreen.ParentCorner(unlocked = true)) }
    }
}
