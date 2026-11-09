package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoiceAssetManifest
import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveLearningEngine
import com.example.data.CustomMediaOverrideEntity
import com.example.data.FamilyMemberEntity
import com.example.data.ProfileSettingsEntity
import com.example.data.SavedArtworkEntity
import com.example.model.AgeBand
import com.example.model.LearningBuddy
import com.example.ui.theme.ActionContinueBlue
import com.example.ui.theme.ActionListenAmber
import com.example.ui.theme.ActionPlayGreen
import com.example.ui.theme.ActionRetryOrange
import com.example.ui.theme.CalmCoolBlueBg
import com.example.ui.theme.DeepSlateInk
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.PlaySurfaceWhite
import com.example.ui.theme.SoftCreamCard
import com.example.viewmodel.LandProgressSummary
import com.example.viewmodel.WonderViewModel
import kotlin.random.Random

/**
 * Parent Corner Screen:
 * - Protected by a child-resistant rotating offline arithmetic gate.
 * - Scrollable layout with 4 comprehensive sections:
 *   1. Visual Progress by Learning Land & World (activities tried, completions, stars, and adaptive comfort tier—zero diagnostic/developmental claims).
 *   2. Age Band, Speaking Buddy & Adaptive Learning Path Controls (including per-world comfort tuning & media/accessibility toggles).
 *   3. My Family Album Manager (up to 16 local photos imported via zero-permission PickVisualMedia).
 *   4. Real World Media Manifest Inspector & Google Flow Video/Image Importer + Deliberate 2-Step Reset Confirmation.
 */
@Composable
fun ParentCornerScreen(
    initiallyUnlocked: Boolean,
    viewModel: WonderViewModel,
    settings: ProfileSettingsEntity,
    progressList: List<ActivityProgressEntity>,
    familyMembers: List<FamilyMemberEntity>,
    savedArtworks: List<SavedArtworkEntity>,
    mediaOverrides: List<CustomMediaOverrideEntity>
) {
    var isUnlocked by remember { mutableStateOf(initiallyUnlocked) }

    if (!isUnlocked) {
        GrownUpArithmeticGate(
            onBack = { viewModel.navigateBack() },
            onUnlocked = { isUnlocked = true }
        )
    } else {
        ParentCornerUnlockedContent(
            viewModel = viewModel,
            settings = settings,
            progressList = progressList,
            familyMembers = familyMembers,
            savedArtworks = savedArtworks,
            mediaOverrides = mediaOverrides
        )
    }
}

@Composable
private fun GrownUpArithmeticGate(
    onBack: () -> Unit,
    onUnlocked: () -> Unit
) {
    var seed by remember { mutableIntStateOf(1) }
    val numA = remember(seed) { Random(System.currentTimeMillis() + seed).nextInt(6, 14) }
    val numB = remember(seed) { Random(System.currentTimeMillis() + seed * 31).nextInt(4, 9) }
    val correctSum = numA + numB
    val options = remember(numA, numB) {
        listOf(correctSum, correctSum + 2, correctSum - 3, correctSum + 5).shuffled()
    }
    var wrongNotice by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CalmCoolBlueBg)
            .padding(24.dp)
            .testTag("parent_gate_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(540.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("parent_gate_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepSlateInk)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = ActionContinueBlue)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Grown-Up Gate",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DeepSlateInk
                        )
                    }
                    IconButton(
                        onClick = {
                            seed += 1
                            wrongNotice = false
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "New Question", tint = MutedSlateText)
                    }
                }

                Text(
                    text = "For grown-ups only: tap the answer to open Parent Corner",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlateText
                )

                Surface(
                    color = SoftCreamCard,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, ActionContinueBlue)
                ) {
                    Text(
                        text = "What is $numA + $numB ?",
                        style = MaterialTheme.typography.displayMedium,
                        color = DeepSlateInk,
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp)
                    )
                }

                if (wrongNotice) {
                    Text(
                        text = "Oops! Try another answer or tap Refresh.",
                        color = ActionRetryOrange,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach { ans ->
                        Button(
                            onClick = {
                                if (ans == correctSum) {
                                    onUnlocked()
                                } else {
                                    wrongNotice = true
                                    seed += 1
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag(if (ans == correctSum) "parent_gate_correct_option" else "parent_gate_option_$ans")
                        ) {
                            Text(text = "$ans", style = MaterialTheme.typography.titleLarge, color = PlaySurfaceWhite)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentCornerUnlockedContent(
    viewModel: WonderViewModel,
    settings: ProfileSettingsEntity,
    progressList: List<ActivityProgressEntity>,
    familyMembers: List<FamilyMemberEntity>,
    savedArtworks: List<SavedArtworkEntity>,
    mediaOverrides: List<CustomMediaOverrideEntity>
) {
    var activeTab by remember { mutableIntStateOf(0) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    val landSummaries: List<LandProgressSummary> = remember(progressList) {
        viewModel.buildLandProgressSummaries(progressList)
    }
    val currentAge = AgeBand.fromId(settings.ageBandId)
    val currentBuddy = LearningBuddy.fromId(settings.buddyId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalmCoolBlueBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("parent_corner_unlocked_screen")
    ) {
        // Top Header & Tab Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PlaySurfaceWhite,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("parent_corner_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = ActionPlayGreen)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Parent Corner",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DeepSlateInk
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        0 to "📊 Land & World Progress",
                        1 to "🎯 Age, Buddy & Media",
                        2 to "👨‍👩‍👧 Family Album (${familyMembers.size}/16)",
                        3 to "🎬 Flow Media & Manifest"
                    ).forEach { (tabIdx, title) ->
                        Button(
                            onClick = { activeTab = tabIdx },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeTab == tabIdx) ActionContinueBlue else SoftCreamCard,
                                contentColor = if (activeTab == tabIdx) PlaySurfaceWhite else DeepSlateInk
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("parent_tab_$tabIdx")
                        ) {
                            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Scrollable Parent Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("parent_corner_scroll_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            when (activeTab) {
                0 -> {
                    // TAB 0: Visual Progress by Learning Land & World + Reset Data Control
                    item {
                        val totalTried = landSummaries.sumOf { it.activitiesTried }
                        val totalCompletions = landSummaries.sumOf { it.totalCompletions }
                        val totalStars = landSummaries.sumOf { it.starsEarned }
                        val totalCatalog = landSummaries.sumOf { it.totalActivities }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Play & Exploration Overview (Offline Local Record)",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = DeepSlateInk
                                        )
                                        Text(
                                            text = "Shows activities tried and completions across the 6 Lands and 27 Worlds. These metrics reflect play history only and make no developmental or diagnostic claims.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 13.sp,
                                            color = MutedSlateText
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Button(
                                        onClick = { showResetConfirmDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .testTag("parent_reset_data_button")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Reset Local Data")
                                        Spacer(Modifier.width(6.dp))
                                        Text("Reset Data", fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    MetricPillCard(
                                        label = "Activities Tried",
                                        value = "$totalTried / $totalCatalog",
                                        sub = "Across 27 Worlds",
                                        color = ActionContinueBlue,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricPillCard(
                                        label = "Total Completions",
                                        value = "$totalCompletions",
                                        sub = "Finished rounds",
                                        color = ActionPlayGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricPillCard(
                                        label = "Stars Collected",
                                        value = "⭐ $totalStars",
                                        sub = "Gentle milestones",
                                        color = ActionListenAmber,
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricPillCard(
                                        label = "Saved Drawings",
                                        value = "🎨 ${savedArtworks.size}",
                                        sub = "In local gallery",
                                        color = Color(0xFF8B5CF6),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Each Learning Land & its Worlds
                    items(landSummaries, key = { it.land.id }) { landSummary ->
                        LandProgressDetailCard(
                            landSummary = landSummary,
                            progressList = progressList,
                            onTuneWorldComfort = { worldId, delta ->
                                viewModel.setWorldAdaptiveComfortDelta(worldId, delta)
                            }
                        )
                    }
                }

                1 -> {
                    // TAB 1: Age Band, Speaking Buddy, Adaptive Learning & Media Controls
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Age Band & Adaptive Learning Path",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DeepSlateInk
                                )
                                val sampleAdaptive = AdaptiveLearningEngine.computeParameters(
                                    ageBand = currentAge,
                                    autoAdaptEnabled = settings.autoAdaptiveDifficulty,
                                    progress = null
                                )
                                Text(
                                    text = sampleAdaptive.parentExplanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedSlateText
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    AgeBand.entries.forEach { age ->
                                        val selected = age == currentAge
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable {
                                                    viewModel.updateAgeAndBuddy(age, currentBuddy)
                                                }
                                                .testTag("parent_age_${age.id}"),
                                            color = if (selected) ActionContinueBlue.copy(alpha = 0.15f) else SoftCreamCard,
                                            border = BorderStroke(
                                                width = if (selected) 3.dp else 1.dp,
                                                color = if (selected) ActionContinueBlue else Color(0xFFCBD5E1)
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(
                                                    text = age.label,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = DeepSlateInk
                                                )
                                                Text(
                                                    text = "${age.choiceCount} choices • ${age.tracingToleranceDp.toInt()}dp trace • ${age.memoryPairs} pairs",
                                                    fontSize = 12.sp,
                                                    color = MutedSlateText
                                                )
                                            }
                                        }
                                    }
                                }

                                // Visual Play Companion Buddy Selector
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Visual Play Companion Buddy",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DeepSlateInk
                                )
                                Text(
                                    text = "Selects the visual character companion on screen. All companions share the approved Warm Preschool Teacher voice rather than pitch-shifting the same voice.",
                                    fontSize = 12.sp,
                                    color = MutedSlateText
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    LearningBuddy.entries.forEach { buddy ->
                                        val selected = buddy == currentBuddy
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable {
                                                    viewModel.updateAgeAndBuddy(currentAge, buddy)
                                                }
                                                .testTag("parent_buddy_${buddy.id}"),
                                            color = if (selected) buddy.accentColor.copy(alpha = 0.2f) else SoftCreamCard,
                                            border = BorderStroke(
                                                width = if (selected) 3.dp else 1.dp,
                                                color = if (selected) buddy.accentColor else Color(0xFFCBD5E1)
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(text = buddy.emoji, fontSize = 28.sp)
                                                Text(
                                                    text = buddy.displayName,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontSize = 14.sp,
                                                    color = DeepSlateInk
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Honest Teacher Voice Selector + 6-Line Audition Studio + Line-to-File Manifest
                    item {
                        val availableVoices by viewModel.audio.availableTeacherVoices.collectAsState()
                        val lastAuditionId by viewModel.audio.lastAuditionClipId.collectAsState()
                        var showFullVoiceManifest by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("parent_voice_audition_card"),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Warm Preschool Teacher Voice & 6-Line Audition",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = DeepSlateInk
                                        )
                                        Text(
                                            text = "Honest Voice Selector: Uses the approved Warm Preschool Teacher voice as default. A second option appears ONLY if a genuinely distinct offline speaker exists on this device (never pitch/speed shifts).",
                                            fontSize = 12.sp,
                                            color = MutedSlateText
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.updateAudioMotionAndAdaptiveSettings(
                                                narrationEnabled = !settings.narrationEnabled,
                                                musicEnabled = settings.musicEnabled,
                                                sfxEnabled = settings.sfxEnabled,
                                                reducedMotion = settings.reducedMotion,
                                                autoAdaptiveDifficulty = settings.autoAdaptiveDifficulty
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (settings.narrationEnabled) ActionPlayGreen else ActionRetryOrange
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.testTag("teacher_voice_mute_button")
                                    ) {
                                        Text(
                                            text = if (settings.narrationEnabled) "🔊 Voice ON (Tap to Mute)" else "🔇 Voice Muted (Tap to Unmute)",
                                            color = PlaySurfaceWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                // Honest Voice Selector Options (Preview with identical sentence)
                                Text(
                                    text = "Preview Sentence: \"${VoiceAssetManifest.STANDARD_PREVIEW_SENTENCE}\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ActionContinueBlue
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    availableVoices.forEach { voiceOpt ->
                                        val isSelected = settings.selectedVoiceId == voiceOpt.id ||
                                            (availableVoices.size == 1 && voiceOpt.isDefaultTeacher)
                                        Surface(
                                            color = if (isSelected) ActionContinueBlue.copy(alpha = 0.12f) else SoftCreamCard,
                                            border = BorderStroke(
                                                width = if (isSelected) 2.5.dp else 1.dp,
                                                color = if (isSelected) ActionContinueBlue else Color(0xFFCBD5E1)
                                            ),
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.selectAndPreviewTeacherVoice(voiceOpt.id)
                                                }
                                                .testTag("voice_option_${voiceOpt.id}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = if (isSelected) "✓ ${voiceOpt.displayName}" else voiceOpt.displayName,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = DeepSlateInk
                                                    )
                                                    Text(
                                                        text = voiceOpt.description,
                                                        fontSize = 12.sp,
                                                        color = MutedSlateText
                                                    )
                                                    Text(
                                                        text = "Quality Status: ${voiceOpt.qualityStatus.label}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = ActionRetryOrange
                                                    )
                                                }
                                                Spacer(Modifier.width(10.dp))
                                                Button(
                                                    onClick = { viewModel.selectAndPreviewTeacherVoice(voiceOpt.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                                                    shape = RoundedCornerShape(14.dp),
                                                    modifier = Modifier.testTag("preview_voice_${voiceOpt.id}")
                                                ) {
                                                    Text(
                                                        text = "▶ Preview Voice",
                                                        color = DeepSlateInk,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // 6-Line Teacher Audition Suite (Review before mass-producing studio clips)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "6-Line Teacher Audition (Review Performance by Activity)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DeepSlateInk
                                )
                                Text(
                                    text = "Tap any line below to audition Instruction, Discovery, Patient Retry, Sincere Success, Storytime, and Schwa-Free Phonics (/m/ & /s/ pure phoneme clips).",
                                    fontSize = 12.sp,
                                    color = MutedSlateText
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    VoiceAssetManifest.sixAuditionLines.forEach { clip ->
                                        val isPlayingThis = lastAuditionId == clip.clipId
                                        Surface(
                                            color = if (isPlayingThis) ActionListenAmber.copy(alpha = 0.20f) else SoftCreamCard,
                                            border = BorderStroke(1.dp, if (isPlayingThis) ActionListenAmber else Color(0xFFE2E8F0)),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "${clip.category.displayName}: \"${clip.displayCaption}\"",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = DeepSlateInk
                                                    )
                                                    Text(
                                                        text = "Direction: ${clip.performanceDirection} • Target File: res/raw/${clip.expectedRawFileName}",
                                                        fontSize = 11.sp,
                                                        color = MutedSlateText
                                                    )
                                                }
                                                Spacer(Modifier.width(8.dp))
                                                Button(
                                                    onClick = { viewModel.playTeacherAuditionClip(clip) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.testTag("audition_play_${clip.clipId}")
                                                ) {
                                                    Text("▶ Audition", color = PlaySurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Toggleable Line-to-File Manifest & Screens Using Device Speech
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Manifest: ${VoiceAssetManifest.allManifestEntries.size} directed clips • 2 bundled pure phoneme WAVs (/m/, /s/)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MutedSlateText
                                    )
                                    TextButton(
                                        onClick = { showFullVoiceManifest = !showFullVoiceManifest },
                                        modifier = Modifier.testTag("toggle_voice_manifest_button")
                                    ) {
                                        Text(
                                            text = if (showFullVoiceManifest) "Hide Line-to-File Manifest ▲" else "View Line-to-File Manifest & Device Speech List ▼",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (showFullVoiceManifest) {
                                    Surface(
                                        color = SoftCreamCard,
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Screens Using Dynamic On-Device Speech (Fallback Inventory):",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = DeepSlateInk
                                            )
                                            VoiceAssetManifest.screensUsingDynamicDeviceSpeech.forEach { (screenName, note) ->
                                                Text(
                                                    text = "• $screenName: $note",
                                                    fontSize = 11.sp,
                                                    color = MutedSlateText
                                                )
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                text = "Schwa-Free Phonics & Core Line-to-File Manifest (A–Z Separate Letter Name + Pure Sound):",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = DeepSlateInk
                                            )
                                            VoiceAssetManifest.phonicsManifest.values.take(10).forEach { p ->
                                                Text(
                                                    text = "• ${p.letter}: ${p.letterNameFile} | ${p.letterSoundFile} -> ${p.ipaSymbol} (${p.pureSoundScript}) — ${p.teacherNote}",
                                                    fontSize = 11.sp,
                                                    color = MutedSlateText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Audio, Narration, Reduced Motion & Adaptive Scaffolding Switches
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Media, Sound & Accessibility Controls",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DeepSlateInk
                                )

                                ParentSettingToggleRow(
                                    title = "Adaptive Challenge Scaffolding (Non-Punitive)",
                                    subtitle = "Gently adjusts choice count, hint glow, and tracing tolerance based on play comfort",
                                    checked = settings.autoAdaptiveDifficulty,
                                    testTag = "toggle_auto_adaptive",
                                    onCheckedChange = {
                                        viewModel.updateAudioMotionAndAdaptiveSettings(
                                            narrationEnabled = settings.narrationEnabled,
                                            musicEnabled = settings.musicEnabled,
                                            sfxEnabled = settings.sfxEnabled,
                                            reducedMotion = settings.reducedMotion,
                                            autoAdaptiveDifficulty = it
                                        )
                                    }
                                )

                                val autoMediaAudio by viewModel.autoMediaAudio.collectAsStateWithLifecycle()
                                ParentSettingToggleRow(
                                    title = "Play media sound automatically",
                                    subtitle = "Listen stays available when automatic sound is off.",
                                    checked = autoMediaAudio,
                                    testTag = "toggle_auto_media_audio",
                                    onCheckedChange = viewModel::setAutoMediaAudio
                                )

                                ParentSettingToggleRow(
                                    title = "Spoken Narration & Phonics Voice (Offline TTS)",
                                    subtitle = "Reads instructions, stories, and phonetic letter sounds aloud",
                                    checked = settings.narrationEnabled,
                                    testTag = "toggle_narration",
                                    onCheckedChange = {
                                        viewModel.updateAudioMotionAndAdaptiveSettings(
                                            narrationEnabled = it,
                                            musicEnabled = settings.musicEnabled,
                                            sfxEnabled = settings.sfxEnabled,
                                            reducedMotion = settings.reducedMotion,
                                            autoAdaptiveDifficulty = settings.autoAdaptiveDifficulty
                                        )
                                    }
                                )

                                ParentSettingToggleRow(
                                    title = "Musical Instruments & Melodies",
                                    subtitle = "Enables polyphonic piano, xylophone, and musical chimes",
                                    checked = settings.musicEnabled,
                                    testTag = "toggle_music",
                                    onCheckedChange = {
                                        viewModel.updateAudioMotionAndAdaptiveSettings(
                                            narrationEnabled = settings.narrationEnabled,
                                            musicEnabled = it,
                                            sfxEnabled = settings.sfxEnabled,
                                            reducedMotion = settings.reducedMotion,
                                            autoAdaptiveDifficulty = settings.autoAdaptiveDifficulty
                                        )
                                    }
                                )

                                ParentSettingToggleRow(
                                    title = "Tactile Sound Effects (SFX)",
                                    subtitle = "Gentle bubble pops, water splashes, and milestone chimes",
                                    checked = settings.sfxEnabled,
                                    testTag = "toggle_sfx",
                                    onCheckedChange = {
                                        viewModel.updateAudioMotionAndAdaptiveSettings(
                                            narrationEnabled = settings.narrationEnabled,
                                            musicEnabled = settings.musicEnabled,
                                            sfxEnabled = it,
                                            reducedMotion = settings.reducedMotion,
                                            autoAdaptiveDifficulty = settings.autoAdaptiveDifficulty
                                        )
                                    }
                                )

                                ParentSettingToggleRow(
                                    title = "Reduced Motion Mode",
                                    subtitle = "Calms floating animations, parallax zoom, and bounce transitions",
                                    checked = settings.reducedMotion,
                                    testTag = "toggle_reduced_motion",
                                    onCheckedChange = {
                                        viewModel.updateAudioMotionAndAdaptiveSettings(
                                            narrationEnabled = settings.narrationEnabled,
                                            musicEnabled = settings.musicEnabled,
                                            sfxEnabled = settings.sfxEnabled,
                                            reducedMotion = it,
                                            autoAdaptiveDifficulty = settings.autoAdaptiveDifficulty
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: My Family Album Manager (Up to 16 Private On-Device Photos)
                    item {
                        FamilyAlbumManagerCard(
                            familyMembers = familyMembers,
                            onAddMember = { name, role, uri, emoji ->
                                viewModel.addFamilyMember(name, role, uri, emoji, 0xFF3B82F6)
                            },
                            onDeleteMember = { member ->
                                viewModel.deleteFamilyMember(member)
                            }
                        )
                    }
                }

                3 -> {
                    // TAB 3: Real World Media Manifest & Google Flow Video Importer
                    item {
                        MediaManifestAndGoogleFlowCard(
                            allMedia = viewModel.offlineMedia.items,
                            mediaOverrides = mediaOverrides,
                            onImportMedia = { mediaId, uri, isVideo ->
                                viewModel.importGoogleFlowMedia(mediaId, uri, isVideo)
                            },
                            onRemoveOverride = { mediaId ->
                                viewModel.removeGoogleFlowMediaOverride(mediaId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Deliberate 2-Step Confirmation Modal for Resetting Local Data
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text("Reset All Local Progress & Data?", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(
                    "This will permanently clear all local activity stars, completions, saved drawings, and My Family Album photos on this device. This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllLocalDataConfirmed()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Yes, Reset Everything", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_reset_button")
                ) {
                    Text("Keep My Progress")
                }
            }
        )
    }
}

@Composable
private fun MetricPillCard(
    label: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(2.dp, color),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MutedSlateText)
            Spacer(Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = DeepSlateInk)
            Text(text = sub, fontSize = 11.sp, color = MutedSlateText)
        }
    }
}

@Composable
private fun LandProgressDetailCard(
    landSummary: LandProgressSummary,
    progressList: List<ActivityProgressEntity>,
    onTuneWorldComfort: (String, Int) -> Unit
) {
    val land = landSummary.land
    val fraction = if (landSummary.totalActivities > 0) {
        landSummary.activitiesTried.toFloat() / landSummary.totalActivities.toFloat()
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("parent_land_progress_${land.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = land.emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = land.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = DeepSlateInk
                        )
                        Text(
                            text = "Activities Tried: ${landSummary.activitiesTried} / ${landSummary.totalActivities} • Completions: ${landSummary.totalCompletions} • ⭐ ${landSummary.starsEarned} Stars",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MutedSlateText
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { fraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = land.accentColor,
                trackColor = SoftCreamCard
            )

            // Worlds inside this Land
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                landSummary.worldSummaries.forEach { worldSum ->
                    val worldFrac = if (worldSum.totalActivities > 0) {
                        worldSum.activitiesTried.toFloat() / worldSum.totalActivities.toFloat()
                    } else 0f
                    val sampleProg = progressList.firstOrNull { it.worldId == worldSum.world.id }
                    val currentDelta = sampleProg?.adaptiveComfortDelta ?: 0

                    Surface(
                        color = SoftCreamCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(0.45f)) {
                                Text(
                                    text = "${worldSum.world.emoji} World ${worldSum.world.number}: ${worldSum.world.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 15.sp,
                                    color = DeepSlateInk
                                )
                                Text(
                                    text = "Tried: ${worldSum.activitiesTried}/${worldSum.totalActivities} • Completions: ${worldSum.totalCompletions} • ⭐ ${worldSum.starsEarned}",
                                    fontSize = 12.sp,
                                    color = MutedSlateText
                                )
                            }

                            LinearProgressIndicator(
                                progress = { worldFrac.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .weight(0.22f)
                                    .padding(horizontal = 12.dp)
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = worldSum.world.accentColor,
                                trackColor = Color(0xFFCBD5E1)
                            )

                            // Per-World Adaptive Comfort Selector (-1 Support, 0 Baseline, +1 Stretch)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf(
                                    -1 to "Gentle",
                                    0 to "Balanced",
                                    1 to "Stretch"
                                ).forEach { (delta, label) ->
                                    val isSelected = currentDelta == delta
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onTuneWorldComfort(worldSum.world.id, delta) },
                                        color = if (isSelected) ActionContinueBlue else PlaySurfaceWhite,
                                        border = BorderStroke(1.dp, ActionContinueBlue)
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PlaySurfaceWhite else DeepSlateInk,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentSettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = SoftCreamCard,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = DeepSlateInk)
                Text(text = subtitle, fontSize = 12.sp, color = MutedSlateText)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

@Composable
private fun FamilyAlbumManagerCard(
    familyMembers: List<FamilyMemberEntity>,
    onAddMember: (String, String, android.net.Uri?, String) -> Unit,
    onDeleteMember: (FamilyMemberEntity) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var roleInput by remember { mutableStateOf("Mom") }
    var selectedEmoji by remember { mutableStateOf("👩") }
    var pickedPhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedPhotoUri = uri
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "My Family Album (${familyMembers.size} / 16 Private On-Device Cards)",
                style = MaterialTheme.typography.titleLarge,
                color = DeepSlateInk
            )
            Text(
                text = "Import up to 16 family photos through the Android System Photo Picker. Photos are resized and stored privately on-device. Add at least 2 entries to unlock the Who's Who? Family Quiz.",
                fontSize = 13.sp,
                color = MutedSlateText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Name (e.g., Mama Sarah)") },
                    singleLine = true,
                    modifier = Modifier.weight(0.32f)
                )
                OutlinedTextField(
                    value = roleInput,
                    onValueChange = { roleInput = it },
                    label = { Text("Role (e.g., Mom, Grandpa)") },
                    singleLine = true,
                    modifier = Modifier.weight(0.28f)
                )

                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Pick Photo", tint = DeepSlateInk)
                    Spacer(Modifier.width(4.dp))
                    Text(if (pickedPhotoUri != null) "Photo Ready ✓" else "Pick Photo", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (familyMembers.size < 16) {
                            onAddMember(
                                nameInput.ifBlank { roleInput },
                                roleInput.ifBlank { "Family" },
                                pickedPhotoUri,
                                selectedEmoji
                            )
                            nameInput = ""
                            pickedPhotoUri = null
                        }
                    },
                    enabled = familyMembers.size < 16,
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("add_family_member_button")
                ) {
                    Text("+ Add Card", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }

            // Emoji avatar quick selector
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("👩", "👨", "👧", "👦", "👵", "👴", "👶", "🐶", "🐱").forEach { em ->
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { selectedEmoji = em },
                        color = if (selectedEmoji == em) ActionContinueBlue.copy(alpha = 0.25f) else SoftCreamCard,
                        border = BorderStroke(if (selectedEmoji == em) 2.dp else 1.dp, ActionContinueBlue)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(em, fontSize = 20.sp)
                        }
                    }
                }
            }

            if (familyMembers.isEmpty()) {
                Surface(
                    color = SoftCreamCard,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No custom family members added yet. Add 2 or more family members above so your child can browse photos and play Who's Who!",
                        modifier = Modifier.padding(14.dp),
                        color = MutedSlateText
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    familyMembers.forEach { member ->
                        Surface(
                            color = SoftCreamCard,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = member.avatarEmoji, fontSize = 28.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = member.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = DeepSlateInk
                                        )
                                        Text(
                                            text = "${member.relationshipRole} • ${if (member.localPhotoPath != null) "Custom Photo Saved" else "Illustrated Card"}",
                                            fontSize = 12.sp,
                                            color = MutedSlateText
                                        )
                                    }
                                }
                                IconButton(onClick = { onDeleteMember(member) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Family Member", tint = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaManifestAndGoogleFlowCard(
    allMedia: List<com.example.model.RealWorldMediaItem>,
    mediaOverrides: List<CustomMediaOverrideEntity>,
    onImportMedia: (String, android.net.Uri, Boolean) -> Unit,
    onRemoveOverride: (String) -> Unit
) {
    if (allMedia.isEmpty()) return
    var selectedTargetId by remember { mutableStateOf(allMedia.first().id) }
    var importAsVideo by remember { mutableStateOf(true) }

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImportMedia(selectedTargetId, uri, importAsVideo)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Real World Asset Manifest & Google Flow Video Importer",
                style = MaterialTheme.typography.titleLarge,
                color = DeepSlateInk
            )
            Text(
                text = "These counts reflect files packaged for offline playback. You can attach your own MP4 or picture to any listed item.",
                fontSize = 13.sp,
                color = MutedSlateText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricPillCard(
                    label = "Animals Pack",
                    value = "${allMedia.count { it.category.id == "animal" }}",
                    sub = "Cards with bundled media",
                    color = ActionPlayGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricPillCard(
                    label = "Objects Pack",
                    value = "${allMedia.count { it.category.id == "object" }}",
                    sub = "Cards with bundled media",
                    color = ActionContinueBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricPillCard(
                    label = "Lessons & Films",
                    value = "${allMedia.count { it.category.id == "film_lesson" && it.bundledVideoAssetPath != null }}",
                    sub = "Bundled lesson videos",
                    color = ActionListenAmber,
                    modifier = Modifier.weight(1f)
                )
                MetricPillCard(
                    label = "Bundled Audio",
                    value = "${allMedia.count { it.bundledAudioAssetPath != null }}",
                    sub = "${mediaOverrides.size} local overrides",
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }

            // Sample of first 12 Manifest Items with 1-Tap Google Flow Video / Image Import
            Text(
                text = "Attach Google Flow Video (.mp4) or Custom Image to Real World Reels:",
                style = MaterialTheme.typography.titleMedium,
                color = DeepSlateInk
            )

            allMedia.forEach { item ->
                val override = mediaOverrides.find { it.mediaId == item.id }
                Surface(
                    color = SoftCreamCard,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.emoji} ${item.title} (${item.id})",
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateInk
                            )
                            Text(
                                text = listOfNotNull(item.bundledImageAssetPath, item.bundledVideoAssetPath, item.bundledAudioAssetPath).joinToString(" • ") { it.substringAfterLast('/') },
                                fontSize = 11.sp,
                                color = MutedSlateText
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    selectedTargetId = item.id
                                    importAsVideo = true
                                    mediaPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(Icons.Default.VideoFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Flow MP4", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    selectedTargetId = item.id
                                    importAsVideo = false
                                    mediaPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text("Image", color = DeepSlateInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (override != null) {
                                IconButton(
                                    onClick = { onRemoveOverride(item.id) },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Reset to Bundled", tint = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
