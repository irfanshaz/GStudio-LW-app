package com.example.ui.engines

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveChallengeParameters
import com.example.data.FamilyMemberEntity
import com.example.model.ActivityConfig
import com.example.model.AgeBand
import com.example.model.EngineType
import com.example.model.GameItemOption
import com.example.ui.theme.ActionContinueBlue
import com.example.ui.theme.ActionFavoritePink
import com.example.ui.theme.ActionListenAmber
import com.example.ui.theme.ActionPlayGreen
import com.example.ui.theme.ActionRetryOrange
import com.example.ui.theme.CalmCoolBlueBg
import com.example.ui.theme.DeepSlateInk
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.PlaySurfaceWhite
import com.example.ui.theme.SoftCreamCard
import com.example.viewmodel.WonderViewModel
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * Shared Activity Play Shell:
 * - Compact Top Instruction Strip (Home, Back, Buddy Speaker, 5-Star Milestone, Show Me Demo, Favorite Heart, Replay, Next Game)
 * - Optional Show Me Demonstration Overlay Banner (closed first on Android Back)
 * - Large White Play Surface hosting the active Engine
 */
@Composable
fun ActivityPlayScreen(
    activity: ActivityConfig,
    viewModel: WonderViewModel,
    progressList: List<ActivityProgressEntity>,
    familyMembers: List<FamilyMemberEntity>,
    showDemoBanner: Boolean,
    reducedMotion: Boolean
) {
    val progress = remember(progressList, activity.id) {
        progressList.find { it.activityId == activity.id }
    }
    val starsEarned = progress?.starsEarned ?: 0
    val isFavorite = progress?.isFavorite == true
    var replayKey by remember(activity.id) { mutableIntStateOf(0) }

    val settings by viewModel.settingsState.collectAsState()
    val savedArtworks by viewModel.savedArtworksState.collectAsState()
    val ttsAvailable by viewModel.audio.ttsAvailable.collectAsState()
    val lastSpokenCaption by viewModel.audio.lastSpokenCaption.collectAsState()

    // Stop speech immediately when leaving the activity
    androidx.compose.runtime.DisposableEffect(activity.id) {
        onDispose {
            viewModel.audio.stopSpeechOnly()
        }
    }

    // Real-time adaptive parameters combining AgeBand + local performance indicators
    val sessionRetries by viewModel.sessionRetries.collectAsState()
    val sessionSmoothWins by viewModel.sessionSmoothWins.collectAsState()
    val adaptiveParams = remember(activity.id, progress, sessionRetries, sessionSmoothWins, settings.ageBandId, settings.autoAdaptiveDifficulty) {
        viewModel.getAdaptiveParametersFor(activity)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalmCoolBlueBg)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("activity_play_screen_${activity.id}")
    ) {
        // 1. Compact Top Instruction Strip (maximizes large play surface below)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PlaySurfaceWhite,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Back, Home, Title & Visible Caption when speech is muted/unavailable
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("activity_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        onClick = { viewModel.navigateHome() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("activity_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${activity.emoji} ${activity.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if ((!settings.narrationEnabled || !ttsAvailable) && lastSpokenCaption.isNotBlank()) {
                            Text(
                                text = "💬 $lastSpokenCaption",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActionContinueBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("speech_fallback_visible_caption")
                            )
                        }
                    }
                }

                // Right: 5-Star Milestone Indicator + Listen + Show Me + Favorite + Replay + Next Game
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    // 5-Star Gentle Milestone Strip
                    Surface(
                        color = SoftCreamCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ActionListenAmber)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (s in 1..5) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (s <= starsEarned) ActionListenAmber else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Listen Instruction Icon Button
                    IconButton(
                        onClick = { viewModel.audio.speakInstruction(activity.instructionText) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ActionListenAmber)
                            .testTag("activity_listen_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = DeepSlateInk)
                    }

                    // Show Me Demonstration Icon Button
                    IconButton(
                        onClick = { viewModel.triggerShowMeDemo(activity) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (showDemoBanner) ActionPlayGreen else SoftCreamCard)
                            .testTag("activity_show_me_button")
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Show Me",
                            tint = if (showDemoBanner) PlaySurfaceWhite else DeepSlateInk
                        )
                    }

                    // Favorite Heart Button (saves to My Shelf)
                    IconButton(
                        onClick = { viewModel.toggleFavorite(activity) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("activity_favorite_button")
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save to My Shelf",
                            tint = if (isFavorite) ActionFavoritePink else MutedSlateText
                        )
                    }

                    // Replay Button
                    IconButton(
                        onClick = {
                            replayKey += 1
                            viewModel.audio.speakInstruction("Let's play ${activity.title} again!")
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("activity_replay_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = DeepSlateInk)
                    }

                    // Top-Level Next Activity Button (distinct from in-deck Next step)
                    Button(
                        onClick = { viewModel.launchNextActivityInWorld(activity) },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("activity_next_button")
                    ) {
                        Text("Next Game", color = PlaySurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Game", tint = PlaySurfaceWhite, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Optional "Show Me" Demonstration Banner
        AnimatedVisibility(visible = showDemoBanner) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .testTag("show_me_demo_banner"),
                color = ActionPlayGreen.copy(alpha = 0.16f),
                border = BorderStroke(2.dp, ActionPlayGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("👆✨", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Show Me: ${activity.showMeDemoHint}",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 13.sp,
                            color = DeepSlateInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = { viewModel.dismissShowMeDemo() },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Got It!", color = PlaySurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // 2. Large White Play Surface hosting the 14 Reusable Game Engines
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("activity_play_surface"),
            color = PlaySurfaceWhite,
            shape = RoundedCornerShape(22.dp),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                when (activity.engineType) {
                    EngineType.SORTING_BASKETS -> SortingBasketsEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        showDemo = showDemoBanner,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.TRACING_STUDIO -> TracingStudioEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        savedStepIndex = progress?.savedStepIndex ?: 0,
                        showDemo = showDemoBanner,
                        reducedMotion = reducedMotion,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.WATER_LAB_SCIENCE -> WaterLabScienceEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.BALLOON_POP -> BalloonPopEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        reducedMotion = reducedMotion,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.ANIMAL_SAFARI_SOUNDS -> AnimalSafariSoundsEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.COLORING_CANVAS -> ColoringCanvasEngine(
                        activity = activity,
                        savedArtworks = savedArtworks,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.MUSIC_INSTRUMENTS -> MusicInstrumentsEngine(
                        activity = activity,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.MEMORY_MATCH -> MemoryMatchEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.PHONICS_WORD_QUIZ -> PhonicsWordQuizEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        savedStepIndex = progress?.savedStepIndex ?: 0,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.NUMBERS_MATH_COUNTER -> NumbersMathCounterEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        savedStepIndex = progress?.savedStepIndex ?: 0,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.STORYBOOK_READER -> StorybookReaderEngine(
                        activity = activity,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.CODING_ARROW_MAZE -> CodingArrowMazeEngine(
                        activity = activity,
                        adaptive = adaptiveParams,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.PRETEND_STEP_CARE -> PretendStepCareEngine(
                        activity = activity,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                    EngineType.FLASHCARD_EXPLORER -> FlashcardAndFamilyEngine(
                        activity = activity,
                        familyMembers = familyMembers,
                        adaptive = adaptiveParams,
                        replayKey = replayKey,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

/**
 * ENGINE 1: Flagship Sorting & Peg Puzzles Engine
 * - Visually distinct baskets & clearly sized objects (sizeScale)
 * - Supports both Drag-and-Drop AND 1-Tap Select + 1-Tap Basket placement
 * - Adaptive layout guarantees every retained item and basket has generous visible width & height on phone and tablet
 */
@Composable
fun SortingBasketsEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    showDemo: Boolean,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val allItems = remember(activity.id) {
        activity.items.ifEmpty {
            listOf(
                GameItemOption("def_1", "Red Apple", "🍎", "Red Basket", "Red Apple!", primaryColorHex = 0xFFEF4444),
                GameItemOption("def_2", "Blueberry", "🫐", "Blue Basket", "Blueberry!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("def_3", "Yellow Star", "⭐", "Yellow Basket", "Yellow Star!", primaryColorHex = 0xFFEAB308)
            )
        }
    }
    val baskets = remember(allItems, adaptive.effectiveChoiceCount) {
        allItems.map { it.categoryOrTarget }.distinct().take(adaptive.effectiveChoiceCount.coerceIn(2, 4))
    }
    val activeItems = remember(allItems, baskets, replayKey) {
        allItems.filter { it.categoryOrTarget in baskets }
    }

    val sortedIds = remember(activity.id, replayKey) { mutableStateListOf<String>() }
    var selectedItem by remember(activity.id, replayKey) { mutableStateOf<GameItemOption?>(null) }
    var feedbackMessage by remember(activity.id, replayKey) {
        mutableStateOf("Tap an item (or drag it), then tap its matching target below!")
    }
    var hadRetries by remember(activity.id, replayKey) { mutableStateOf(false) }

    val remainingItems = activeItems.filterNot { it.id in sortedIds }

    LaunchedEffect(showDemo, remainingItems) {
        if (showDemo && remainingItems.isNotEmpty()) {
            selectedItem = remainingItems.first()
            feedbackMessage = "Show Me: Watch ${remainingItems.first().label} go into ${remainingItems.first().categoryOrTarget}!"
        }
    }

    fun attemptSort(item: GameItemOption, targetBasket: String) {
        if (item.categoryOrTarget == targetBasket) {
            sortedIds.add(item.id)
            selectedItem = remainingItems.firstOrNull { it.id != item.id }
            feedbackMessage = "✨ Great sorting! ${item.spokenText}"
            viewModel.audio.playNoteHz(587.33f, 220, isXylophone = true)
            viewModel.audio.speakInstruction(item.spokenText)
            if (sortedIds.size >= activeItems.size) {
                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = hadRetries)
                feedbackMessage = "🌟 All sorted! Tap Replay or Sort Another Batch!"
            }
        } else {
            hadRetries = true
            feedbackMessage = "Gentle try! ${item.label} belongs in ${item.categoryOrTarget}."
            viewModel.onGentleRetryNeeded(activity, "Try putting ${item.label} in ${item.categoryOrTarget}!")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_sorting_baskets"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Feedback & Endless Replenish Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = feedbackMessage,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 14.sp,
                color = DeepSlateInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (remainingItems.isEmpty()) {
                Button(
                    onClick = {
                        sortedIds.clear()
                        hadRetries = false
                        feedbackMessage = "Fresh items ready to sort!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("sorting_replenish_button")
                ) {
                    Text("Sort Another Batch! ⭐", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Top Area: Responsive Draggable & Tappable Objects (Guaranteed visible width for up to 8 items)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.52f)
                .padding(vertical = 4.dp)
        ) {
            val count = remainingItems.size.coerceAtLeast(1)
            val spacingDp = 8.dp
            val availablePerItem = ((maxWidth - spacingDp * (count - 1)) / count).coerceIn(68.dp, 118.dp)
            val maxCardHeight = (maxHeight - 4.dp).coerceIn(68.dp, 118.dp)

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(spacingDp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                remainingItems.forEach { item ->
                    val isSelected = selectedItem?.id == item.id
                    var dragOffset by remember(item.id) { mutableStateOf(Offset.Zero) }
                    val scaleFactor = item.sizeScale.coerceIn(0.82f, 1.15f)
                    val itemWidth = (availablePerItem * scaleFactor).coerceIn(64.dp, availablePerItem)
                    val itemHeight = (maxCardHeight * scaleFactor).coerceIn(64.dp, maxCardHeight)

                    Surface(
                        modifier = Modifier
                            .size(width = itemWidth, height = itemHeight)
                            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                selectedItem = item
                                feedbackMessage = "Selected ${item.label} — now tap ${item.categoryOrTarget} below!"
                                viewModel.audio.speakInstruction(item.label)
                            }
                            .pointerInput(item.id, baskets) {
                                detectDragGestures(
                                    onDragStart = { selectedItem = item },
                                    onDragEnd = {
                                        if (dragOffset.y > 48f) {
                                            val basketIndex = when {
                                                baskets.size == 2 -> if (dragOffset.x < 0f) 0 else 1
                                                else -> when {
                                                    dragOffset.x < -60f -> 0
                                                    dragOffset.x > 60f -> (baskets.size - 1)
                                                    else -> (baskets.size / 2)
                                                }
                                            }
                                            val targetBasket = baskets.getOrElse(basketIndex) { item.categoryOrTarget }
                                            attemptSort(item, targetBasket)
                                        }
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = { dragOffset = Offset.Zero },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount
                                    }
                                )
                            }
                            .testTag("sort_item_${item.id}"),
                        color = if (isSelected) Color(item.primaryColorHex).copy(alpha = 0.24f) else SoftCreamCard,
                        border = BorderStroke(
                            width = if (isSelected) 3.5.dp else 2.dp,
                            color = Color(item.primaryColorHex)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = if (isSelected) 6.dp else 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (item.drawableRes != null) {
                                Image(
                                    painter = painterResource(id = item.drawableRes),
                                    contentDescription = item.label,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                Text(
                                    text = item.emoji,
                                    fontSize = 26.sp
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateInk,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Bottom Row: Visually Distinct Target Baskets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            baskets.forEachIndexed { index, basketName ->
                val matchingColor = activeItems.firstOrNull { it.categoryOrTarget == basketName }?.primaryColorHex ?: 0xFF2563EB
                val highlightScaffold = adaptive.showVisualScaffoldGlow && selectedItem?.categoryOrTarget == basketName
                val countInBasket = activeItems.count { it.categoryOrTarget == basketName && it.id in sortedIds }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            val current = selectedItem ?: remainingItems.firstOrNull()
                            if (current != null) {
                                attemptSort(current, basketName)
                            }
                        }
                        .testTag("sort_basket_$index"),
                    color = if (highlightScaffold) Color(matchingColor).copy(alpha = 0.28f) else Color(matchingColor).copy(alpha = 0.12f),
                    border = BorderStroke(
                        width = if (highlightScaffold) 4.dp else 2.5.dp,
                        color = Color(matchingColor)
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🧺 $basketName",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (highlightScaffold) "✨ Tap Here to Place!" else "Sorted: $countInBasket",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (highlightScaffold) ActionPlayGreen else MutedSlateText,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 2: Flagship Alphabet, Number, Shape, Pre-Writing & Word Tracing Studio (Stage 1 Repair)
 * - Uses explicit deck definitions keyed to stable activity IDs (w04_tracing_1..12)
 * - Multi-stroke glyphs with explicit pen lifts, direction cues, and aspect-ratio-safe canvas scaling
 * - Unified evaluation so Effort & Path Coverage meter and status feedback always agree
 * - Distinct Assisted Tap-Along mode without invented accuracy scores
 * - Guaranteed >= 48.dp visible height for Retry, Skip, and Next controls on phone and tablet
 * - Explicit deck completion state with Replay Deck or Return to World
 */
@Composable
fun TracingStudioEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    savedStepIndex: Int,
    showDemo: Boolean,
    reducedMotion: Boolean = false,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val deck = remember(activity.id) {
        TracingStudioDeckRegistry.getDeckForActivity(activity.id)
    }
    val initialStep = remember(activity.id, savedStepIndex, deck.size) {
        TracingStudioDeckRegistry.reconcileSavedStepIndex(savedStepIndex, deck.size)
    }

    // If legacy savedStepIndex was out of range for the repaired deck, persist the reconciled valid index
    LaunchedEffect(activity.id, savedStepIndex, initialStep) {
        if (savedStepIndex != initialStep) {
            viewModel.saveActivityCheckpoint(activity, initialStep)
        }
    }

    var currentStep by remember(activity.id, replayKey) {
        mutableIntStateOf(initialStep)
    }
    var isDeckComplete by remember(activity.id, replayKey) {
        mutableStateOf(false)
    }

    val safeStep = currentStep.coerceIn(0, deck.lastIndex)
    val activeGlyphSpec = deck[safeStep]
    val activeGlyph = activeGlyphSpec.glyph
    val activeWord = activeGlyphSpec.wordHint
    val strokes = activeGlyphSpec.strokes

    var currentStrokeIdx by remember(safeStep, replayKey) { mutableIntStateOf(0) }
    val safeStrokeIdx = currentStrokeIdx.coerceIn(0, strokes.lastIndex)
    val activeStrokeSpec = strokes[safeStrokeIdx]

    // Track completed strokes in the current glyph and user segments (preserving pen lifts!)
    val strokeAccuracies = remember(safeStep, replayKey) { mutableStateListOf<Int>() }
    val completedStrokeIndices = remember(safeStep, replayKey) { mutableStateListOf<Int>() }
    val userSegmentsForCurrentStroke = remember(safeStep, safeStrokeIdx, replayKey) {
        mutableStateListOf<List<Offset>>()
    }
    val liveDragPoints = remember(safeStep, safeStrokeIdx, replayKey) {
        mutableStateListOf<Offset>()
    }

    // Track which glyph indices in this session already awarded a star so Retry practices without duplicate credit
    val awardedStepsInSession = remember(activity.id, replayKey) { mutableStateListOf<Int>() }
    var hadRetries by remember(safeStep, replayKey) { mutableStateOf(false) }
    var isGlyphCompleted by remember(safeStep, replayKey) { mutableStateOf(false) }
    var usedAssistance by remember(safeStep, replayKey) { mutableStateOf(false) }
    var isAssistedTapMode by remember(safeStep, replayKey) { mutableStateOf(false) }
    var assistedTapCheckpointIdx by remember(safeStep, safeStrokeIdx, replayKey) { mutableIntStateOf(0) }

    // Single source of truth for BOTH the coverage meter and feedback message
    var currentEval by remember(safeStep, safeStrokeIdx, replayKey) {
        mutableStateOf(
            TracingEvaluationResult(
                coverageRatio = 0f,
                orderedRatio = 0f,
                offPathRatio = 0f,
                startedNearStart = false,
                reachedNearFinish = false,
                isShortcutOrTapCluster = false,
                isReversed = false,
                strokePassed = false,
                accuracyPercent = 0,
                feedbackMessage = "Start here ⭐ — ${activeStrokeSpec.directionCue} (0%)"
            )
        )
    }

    val density = LocalDensity.current
    val tolerancePx = with(density) { (adaptive.tracingToleranceDp * 0.3f).coerceIn(10f, 20f).dp.toPx() }
    val isToddlerBand = adaptive.baseAgeBand == AgeBand.TODDLER_2_3

    var canvasSize by remember { mutableStateOf(Offset(600f, 340f)) }

    val allStrokesPixels = remember(strokes, canvasSize) {
        strokes.map { stroke ->
            stroke.points.map { pt ->
                TracingStudioDeckRegistry.mapNormalizedToCanvas(pt, canvasSize.x, canvasSize.y)
            }
        }
    }
    val activeCheckpointPixels = allStrokesPixels.getOrElse(safeStrokeIdx) { emptyList() }

    // Keep latest references for gesture callbacks so they NEVER read stale composition state
    val latestStrokeSpec by rememberUpdatedState(activeStrokeSpec)
    val latestCheckpoints by rememberUpdatedState(activeCheckpointPixels)
    val latestTolerancePx by rememberUpdatedState(tolerancePx)
    val latestRequiredCoverage by rememberUpdatedState(adaptive.tracingCoverageRequired)
    val latestIsToddler by rememberUpdatedState(isToddlerBand)
    val latestSafeStep by rememberUpdatedState(safeStep)
    val latestSafeStrokeIdx by rememberUpdatedState(safeStrokeIdx)
    val latestTotalStrokes by rememberUpdatedState(strokes.size)
    val latestDeckSize by rememberUpdatedState(deck.size)
    val latestActiveGlyph by rememberUpdatedState(activeGlyph)
    val latestActiveWord by rememberUpdatedState(activeWord)

    fun recomputeEvaluation(segments: List<List<Offset>>): TracingEvaluationResult {
        val singleStrokeEval = TracingStudioDeckRegistry.evaluateStrokeAttempt(
            strokeSpec = latestStrokeSpec,
            checkpointPixels = latestCheckpoints,
            rawSegments = segments,
            tolerancePx = latestTolerancePx,
            requiredCoverageRatio = latestRequiredCoverage,
            isToddlerBand = latestIsToddler
        )
        // Combine completed strokes with active stroke coverage so multi-stroke progress is smooth and accurate
        val completedFraction = completedStrokeIndices.size.toFloat() / latestTotalStrokes.coerceAtLeast(1).toFloat()
        val currentContribution = singleStrokeEval.coverageRatio / latestTotalStrokes.coerceAtLeast(1).toFloat()
        val combinedCoverage = if (isGlyphCompleted) 1.0f else (completedFraction + currentContribution).coerceIn(0f, 0.99f)
        val combinedPct = (combinedCoverage * 100f).roundToInt()

        val baseMsg = singleStrokeEval.feedbackMessage.substringBeforeLast("(").trim()
        return singleStrokeEval.copy(
            coverageRatio = combinedCoverage,
            feedbackMessage = "$baseMsg ($combinedPct%)"
        )
    }

    fun handleStrokeSuccess(eval: TracingEvaluationResult, fromAssistedTap: Boolean) {
        usedAssistance = usedAssistance || fromAssistedTap
        if (!completedStrokeIndices.contains(latestSafeStrokeIdx)) {
            completedStrokeIndices.add(latestSafeStrokeIdx)
            strokeAccuracies.add(eval.accuracyPercent)
        }
        if (latestSafeStrokeIdx < latestTotalStrokes - 1) {
            val nextStroke = latestSafeStrokeIdx + 1
            currentStrokeIdx = nextStroke
            val nextPct = ((completedStrokeIndices.size.toFloat() / latestTotalStrokes.toFloat()) * 100f).roundToInt()
            currentEval = eval.copy(
                coverageRatio = completedStrokeIndices.size.toFloat() / latestTotalStrokes.toFloat(),
                feedbackMessage = "✋ Pen lift! Now start Stroke ${nextStroke + 1} of $latestTotalStrokes at ⭐ ($nextPct%)"
            )
            viewModel.audio.playNoteHz(523.25f, 160, isXylophone = true)
            viewModel.audio.speakInstruction("Nice! Lift your finger and start the next stroke at the green star.")
        } else if (!isGlyphCompleted) {
            isGlyphCompleted = true
            val finalEval = eval.copy(
                coverageRatio = 1.0f,
                strokePassed = true,
                accuracyPercent = strokeAccuracies.average().roundToInt()
                    .coerceAtMost(if (strokeAccuracies.all { it == 100 }) 100 else 99),
                feedbackMessage = if (usedAssistance) {
                    "🌟 Assisted Tap-Along complete for $latestActiveGlyph ($latestActiveWord)!"
                } else {
                    "🌟 Wonderful tracing of $latestActiveGlyph ($latestActiveWord)!"
                }
            )
            currentEval = finalEval
            val nextSavedIdx = if (latestSafeStep + 1 < latestDeckSize) latestSafeStep + 1 else 0
            if (!awardedStepsInSession.contains(latestSafeStep)) {
                awardedStepsInSession.add(latestSafeStep)
                viewModel.onActivityMilestoneCompleted(
                    activity = activity,
                    hadRetriesInRound = hadRetries,
                    nextStepIndex = nextSavedIdx,
                    tracingAccuracyPercent = if (usedAssistance) null else finalEval.accuracyPercent
                )
            } else {
                viewModel.audio.playSuccessChime()
                viewModel.audio.speakInstruction("Great practice tracing $latestActiveGlyph again!")
                viewModel.saveActivityCheckpoint(activity, nextSavedIdx)
            }
        }
    }

    val demoTransition = rememberInfiniteTransition(label = "trace_demo")
    val demoProgress by demoTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (reducedMotion) 6000 else 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "demo_cursor"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_tracing_studio"),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Left: Large Dominant Interactive Tracing Canvas
        Box(
            modifier = Modifier
                .weight(0.66f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(SoftCreamCard)
                .onSizeChanged { intSize ->
                    if (intSize.width > 0 && intSize.height > 0) {
                        canvasSize = Offset(intSize.width.toFloat(), intSize.height.toFloat())
                    }
                }
                .pointerInput(safeStep, safeStrokeIdx, replayKey, isDeckComplete) {
                    if (isDeckComplete) return@pointerInput
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            isAssistedTapMode = false
                            liveDragPoints.clear()
                            liveDragPoints.add(startOffset)
                            val candidateSegments = userSegmentsForCurrentStroke.toList() + listOf(liveDragPoints.toList())
                            currentEval = recomputeEvaluation(candidateSegments)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            liveDragPoints.add(change.position)
                            val candidateSegments = userSegmentsForCurrentStroke.toList() + listOf(liveDragPoints.toList())
                            currentEval = recomputeEvaluation(candidateSegments)
                        },
                        onDragEnd = {
                            if (liveDragPoints.isNotEmpty()) {
                                userSegmentsForCurrentStroke.add(liveDragPoints.toList())
                                liveDragPoints.clear()
                            }
                            val freshEval = recomputeEvaluation(userSegmentsForCurrentStroke.toList())
                            currentEval = freshEval

                            if (freshEval.strokePassed) {
                                handleStrokeSuccess(freshEval, fromAssistedTap = false)
                            } else if (freshEval.offPathRatio > 0.45f || freshEval.isReversed || freshEval.isShortcutOrTapCluster) {
                                hadRetries = true
                                viewModel.onGentleRetryNeeded(activity, freshEval.feedbackMessage.substringBeforeLast("(").trim())
                            }
                        },
                        onDragCancel = {
                            liveDragPoints.clear()
                            currentEval = recomputeEvaluation(userSegmentsForCurrentStroke.toList())
                        }
                    )
                }
                .pointerInput(safeStep, safeStrokeIdx, replayKey, isDeckComplete) {
                    if (isDeckComplete) return@pointerInput
                    // Separately identified Assisted Tap-Along interaction (no invented accuracy score)
                    detectTapGestures { tapOffset ->
                        val cps = latestCheckpoints
                        if (cps.isEmpty()) return@detectTapGestures
                        val targetIdx = assistedTapCheckpointIdx.coerceIn(0, cps.lastIndex)
                        val targetPt = cps[targetIdx]
                        val distToNext = hypot(targetPt.x - tapOffset.x, targetPt.y - tapOffset.y)

                        if (distToNext <= latestTolerancePx * 1.6f) {
                            isAssistedTapMode = true
                            val prevPt = if (targetIdx > 0) cps[targetIdx - 1] else targetPt
                            userSegmentsForCurrentStroke.add(listOf(prevPt, targetPt))
                            assistedTapCheckpointIdx = targetIdx + 1
                            viewModel.audio.playNoteHz(440f + targetIdx * 28f, 120, isXylophone = true)

                            val progressRatio = assistedTapCheckpointIdx.toFloat() / cps.size.toFloat()
                            val combined = ((completedStrokeIndices.size.toFloat() + progressRatio) / latestTotalStrokes.toFloat()).coerceIn(0f, 1f)
                            val pct = (combined * 100f).roundToInt()

                            if (assistedTapCheckpointIdx >= cps.size) {
                                val tapEval = TracingEvaluationResult(
                                    coverageRatio = combined,
                                    orderedRatio = 1f,
                                    offPathRatio = 0f,
                                    startedNearStart = true,
                                    reachedNearFinish = true,
                                    isShortcutOrTapCluster = false,
                                    isReversed = false,
                                    strokePassed = true,
                                    accuracyPercent = 100,
                                    feedbackMessage = "Assisted Tap-Along complete! ($pct%)"
                                )
                                currentEval = tapEval
                                handleStrokeSuccess(tapEval, fromAssistedTap = true)
                            } else {
                                currentEval = currentEval.copy(
                                    coverageRatio = combined,
                                    feedbackMessage = "Assisted Tap-Along: dot $assistedTapCheckpointIdx of ${cps.size} ($pct%)"
                                )
                            }
                        } else {
                            // Tapping far from the current checkpoint does not award fake accuracy
                            val currentPct = currentEval.coveragePercent
                            currentEval = currentEval.copy(
                                feedbackMessage = "Start at the green star ⭐ and slide along the dots! ($currentPct%)"
                            )
                        }
                    }
                }
                .testTag("tracing_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 1. Draw all strokes of the glyph (completed in green, upcoming in subtle slate, active in bold guide)
                allStrokesPixels.forEachIndexed { sIdx, pts ->
                    if (pts.size >= 2) {
                        val strokePath = Path().apply {
                            moveTo(pts.first().x, pts.first().y)
                            for (i in 1 until pts.size) {
                                lineTo(pts[i].x, pts[i].y)
                            }
                        }
                        val isDoneStroke = sIdx in completedStrokeIndices || isGlyphCompleted
                        val isActiveStroke = sIdx == safeStrokeIdx && !isGlyphCompleted

                        drawPath(
                            path = strokePath,
                            color = when {
                                isDoneStroke -> ActionPlayGreen.copy(alpha = 0.45f)
                                isActiveStroke -> Color(0xFFCBD5E1)
                                else -> Color(0xFFE2E8F0)
                            },
                            style = Stroke(
                                width = if (isActiveStroke) tolerancePx * 0.95f else tolerancePx * 0.65f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                        drawPath(
                            path = strokePath,
                            color = when {
                                isDoneStroke -> ActionPlayGreen
                                isActiveStroke -> Color(0xFF475569)
                                else -> Color(0xFF94A3B8)
                            },
                            style = Stroke(
                                width = if (isActiveStroke) 6f else 4f,
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 14f), 0f)
                            )
                        )
                    } else if (pts.size == 1) {
                        drawCircle(
                            color = if (sIdx in completedStrokeIndices || isGlyphCompleted) ActionPlayGreen else Color(0xFF475569),
                            radius = 18f,
                            center = pts.first()
                        )
                    }
                }

                // 2. Active Stroke Checkpoints, Start Star (Green) & Finish Dot (Amber)
                val resampledUser = TracingStudioDeckRegistry.resampleStrokeSegments(
                    userSegmentsForCurrentStroke.toList() + if (liveDragPoints.isNotEmpty()) listOf(liveDragPoints.toList()) else emptyList()
                )
                activeCheckpointPixels.forEachIndexed { idx, pt ->
                    val isCovered = resampledUser.any { up -> hypot(up.x - pt.x, up.y - pt.y) <= tolerancePx }
                    drawCircle(
                        color = when {
                            idx == 0 -> ActionPlayGreen
                            idx == activeCheckpointPixels.lastIndex -> ActionListenAmber
                            isCovered -> ActionContinueBlue
                            else -> Color.White
                        },
                        radius = if (idx == 0 || idx == activeCheckpointPixels.lastIndex) 20f else 10f,
                        center = pt
                    )
                }

                // 3. Child's Drawn Stroke Segments (each segment drawn separately to respect pen lifts!)
                val allUserSegments = userSegmentsForCurrentStroke.toList() +
                    if (liveDragPoints.isNotEmpty()) listOf(liveDragPoints.toList()) else emptyList()
                allUserSegments.forEach { seg ->
                    if (seg.size >= 2) {
                        val segPath = Path().apply {
                            moveTo(seg.first().x, seg.first().y)
                            for (i in 1 until seg.size) {
                                lineTo(seg[i].x, seg[i].y)
                            }
                        }
                        drawPath(
                            path = segPath,
                            color = if (isGlyphCompleted) ActionPlayGreen else ActionContinueBlue,
                            style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    } else if (seg.size == 1) {
                        drawCircle(
                            color = if (isGlyphCompleted) ActionPlayGreen else ActionContinueBlue,
                            radius = 12f,
                            center = seg.first()
                        )
                    }
                }

                // 4. "Show Me" Animated Multi-Stroke Demo with Visible Pen Lift (or Static Sequence when reducedMotion)
                if (showDemo && allStrokesPixels.isNotEmpty()) {
                    if (reducedMotion) {
                        // Static numbered sequence markers at start and finish of each stroke
                        allStrokesPixels.forEachIndexed { sIdx, pts ->
                            if (pts.isNotEmpty()) {
                                drawCircle(
                                    color = ActionListenAmber,
                                    radius = 24f,
                                    center = pts.first()
                                )
                            }
                        }
                    } else {
                        // Divide demoProgress across strokes with a 15% pen-lift pause between strokes
                        val strokeCount = allStrokesPixels.size
                        val slotSize = 1f / strokeCount.toFloat()
                        val activeDemoStroke = (demoProgress / slotSize).toInt().coerceIn(0, strokeCount - 1)
                        val localT = ((demoProgress - activeDemoStroke * slotSize) / slotSize).coerceIn(0f, 1f)
                        val pts = allStrokesPixels[activeDemoStroke]
                        if (pts.isNotEmpty()) {
                            val isLiftingPen = strokeCount > 1 && localT > 0.84f
                            val moveT = (localT / 0.84f).coerceIn(0f, 1f)
                            val ptIdx = ((pts.size - 1) * moveT).toInt().coerceIn(0, pts.lastIndex)
                            val demoPt = pts[ptIdx]
                            drawCircle(
                                color = if (isLiftingPen) ActionPlayGreen.copy(alpha = 0.55f) else ActionListenAmber,
                                radius = if (isLiftingPen) 34f else 26f,
                                center = demoPt
                            )
                        }
                    }
                }
            }

            // Top-Left Header Overlay: Current Glyph, Word Context (for First Words CAT/SUN/DOG/STAR), and Stroke Counter
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Surface(
                    color = PlaySurfaceWhite.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, ActionContinueBlue.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$activeGlyph • $activeWord",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk,
                            modifier = Modifier.testTag("tracing_active_glyph_label")
                        )
                        if (strokes.size > 1) {
                            Text(
                                text = "Stroke ${safeStrokeIdx + 1}/${strokes.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActionContinueBlue
                            )
                        }
                    }
                }

                if (activeGlyphSpec.wordContext != null) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = ActionListenAmber.copy(alpha = 0.24f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, ActionListenAmber),
                        modifier = Modifier.testTag("tracing_word_banner_${activeGlyphSpec.wordContext}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Word:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateInk
                            )
                            activeGlyphSpec.wordContext.forEachIndexed { idx, ch ->
                                val isCurrentLetter = idx == activeGlyphSpec.letterIndexInWord
                                val isPastLetter = idx < activeGlyphSpec.letterIndexInWord
                                Text(
                                    text = ch.toString(),
                                    fontSize = if (isCurrentLetter) 18.sp else 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        isCurrentLetter -> ActionContinueBlue
                                        isPastLetter -> ActionPlayGreen
                                        else -> MutedSlateText
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Explicit Deck Finished Celebration Banner inside Canvas
            if (isDeckComplete) {
                Surface(
                    color = PlaySurfaceWhite.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(3.dp, ActionPlayGreen),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .testTag("tracing_deck_complete_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌟🎉", fontSize = 36.sp)
                        Text(
                            text = "Deck Complete! You finished ${activity.title}!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk,
                            textAlign = TextAlign.Center
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    isDeckComplete = false
                                    currentStep = 0
                                    viewModel.saveActivityCheckpoint(activity, 0)
                                    viewModel.audio.speakInstruction("Let's trace from the beginning!")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("tracing_replay_deck_button")
                            ) {
                                Text("Replay Deck 🔁", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.navigateBack() },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("tracing_finish_world_button")
                            ) {
                                Text("Back to World", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Right: Compact, Guaranteed-Fit Effort Indicator, Feedback & Controls (Retry, Skip, Next)
        Column(
            modifier = Modifier
                .weight(0.34f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Info Card (Compact so it never crowds out buttons on phone landscape)
            Surface(
                color = SoftCreamCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, ActionContinueBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeGlyph,
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ActionContinueBlue
                        )
                        Text(
                            text = "${safeStep + 1} / ${deck.size} • $activeWord",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isGlyphCompleted) "Completed" else "Stroke progress: ${currentEval.coveragePercent}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedSlateText,
                            modifier = Modifier.testTag("tracing_coverage_meter_text")
                        )
                        if (isAssistedTapMode) {
                            Text(
                                text = "Assisted Tap",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActionContinueBlue
                            )
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { currentEval.coverageRatio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = ActionPlayGreen,
                        trackColor = Color(0xFFCBD5E1)
                    )
                }
            }

            // Unified Status & Direction Message (always shows the exact same percentage as the meter)
            Text(
                text = currentEval.feedbackMessage,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepSlateInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .testTag("tracing_feedback_status_text")
            )

            // Bottom Action Controls: Hear Sound, Retry, Skip, Next Glyph (all >= 48.dp height!)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        val ch = activeGlyph.firstOrNull() ?: 'A'
                        if (activeGlyph.length == 1 && ch.isLetter()) {
                            viewModel.audio.speakPhonicsLetter(ch, speakSoundNotName = true)
                        } else {
                            viewModel.audio.speakInstruction("$activeGlyph for $activeWord! ${activeStrokeSpec.directionCue}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("tracing_hear_sound_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Hear Sound", tint = DeepSlateInk, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Hear Sound", color = DeepSlateInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Retry clears current attempt without re-enabling duplicate star awards
                    Button(
                        onClick = {
                            userSegmentsForCurrentStroke.clear()
                            liveDragPoints.clear()
                            completedStrokeIndices.clear()
                            strokeAccuracies.clear()
                            currentStrokeIdx = 0
                            assistedTapCheckpointIdx = 0
                            isAssistedTapMode = false
                            isGlyphCompleted = false
                            usedAssistance = false
                            isDeckComplete = false
                            currentEval = TracingEvaluationResult(
                                coverageRatio = 0f,
                                orderedRatio = 0f,
                                offPathRatio = 0f,
                                startedNearStart = false,
                                reachedNearFinish = false,
                                isShortcutOrTapCluster = false,
                                isReversed = false,
                                strokePassed = false,
                                accuracyPercent = 0,
                                feedbackMessage = "Canvas cleared—trace $activeGlyph from ⭐ (0%)"
                            )
                            viewModel.audio.speakInstruction("Try tracing $activeGlyph again from the green star!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionRetryOrange),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("tracing_retry_button")
                    ) {
                        Text("Retry", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    }

                    // Skip moves on without a completion reward and saves checkpoint
                    Button(
                        onClick = {
                            if (safeStep < deck.lastIndex) {
                                val nextIdx = safeStep + 1
                                currentStep = nextIdx
                                isDeckComplete = false
                                viewModel.saveActivityCheckpoint(activity, nextIdx)
                                val nextGlyph = deck[nextIdx]
                                viewModel.audio.speakInstruction("Skipped to ${nextGlyph.glyph} for ${nextGlyph.wordHint}!")
                            } else {
                                isDeckComplete = true
                                viewModel.saveActivityCheckpoint(activity, 0)
                                viewModel.audio.speakInstruction("You reached the end of ${activity.title}! Tap Replay Deck to trace again.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("tracing_skip_button")
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Skip", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("Skip", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    }
                }

                // Next advances in-deck with spoken/visual transition and explicit deck end
                Button(
                    onClick = {
                        if (safeStep < deck.lastIndex) {
                            val nextIdx = safeStep + 1
                            currentStep = nextIdx
                            isDeckComplete = false
                            viewModel.saveActivityCheckpoint(activity, nextIdx)
                            val nextGlyph = deck[nextIdx]
                            viewModel.audio.speakInstruction("Next up: ${nextGlyph.glyph} for ${nextGlyph.wordHint}!")
                        } else {
                            isDeckComplete = true
                            viewModel.saveActivityCheckpoint(activity, 0)
                            viewModel.audio.speakInstruction("Hooray! You finished all of ${activity.title}!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("tracing_next_glyph_button")
                ) {
                    Text(
                        text = if (safeStep < deck.lastIndex) "Next Letter / Shape ▶" else "Finish Deck 🌟",
                        color = PlaySurfaceWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * ENGINE 3: Flagship Water Lab (Predict & Observe Buoyancy: Float or Sink)
 */
@Composable
fun WaterLabScienceEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val labItems = remember(activity.id) {
        activity.items.ifEmpty {
            listOf(
                GameItemOption("water_duck", "Rubber Duck", "🦆", "Float", "The hollow rubber duck floats on top!", sinksInWater = false, explanation = "Full of light air — Floats!", primaryColorHex = 0xFFEAB308),
                GameItemOption("water_stone", "River Stone", "🪨", "Sink", "The heavy river stone sinks to the bottom!", sinksInWater = true, explanation = "Heavy & dense — Sinks!", primaryColorHex = 0xFF64748B)
            )
        }
    }
    var currentIdx by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    val currentObject = labItems[currentIdx % labItems.size]

    var prediction by remember(currentIdx, replayKey) { mutableStateOf<Boolean?>(null) } // false = Float, true = Sink
    var droppedInWater by remember(currentIdx, replayKey) { mutableStateOf(false) }

    val objectYFraction by animateFloatAsState(
        targetValue = when {
            !droppedInWater -> 0.14f
            currentObject.sinksInWater -> 0.80f
            else -> 0.40f // Floats right on the water surface!
        },
        animationSpec = tween(durationMillis = 800),
        label = "water_drop_y"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_water_lab"),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Left: Interactive Animated Water Tank
        Box(
            modifier = Modifier
                .weight(0.56f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFE0F2FE))
                .clickable {
                    if (!droppedInWater) {
                        if (prediction == null) prediction = currentObject.sinksInWater
                        droppedInWater = true
                        viewModel.audio.playWaterSplash(currentObject.sinksInWater)
                        viewModel.audio.speakInstruction(currentObject.spokenText)
                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                    }
                }
                .testTag("water_lab_tank")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val waterTop = size.height * 0.44f
                drawRect(
                    color = Color(0xFFE0F2FE),
                    size = androidx.compose.ui.geometry.Size(size.width, waterTop)
                )
                drawRect(
                    color = Color(0xFF0284C7).copy(alpha = 0.45f),
                    topLeft = Offset(0f, waterTop),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height - waterTop)
                )
                drawRect(
                    color = Color(0xFFFDE68A),
                    topLeft = Offset(0f, size.height * 0.88f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.12f)
                )
            }

            Text(
                text = "🌊 Water Surface (Float Zone)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0369A1),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            )

            // Tested Object Inside Tank
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (objectYFraction * 200f).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = PlaySurfaceWhite,
                    border = BorderStroke(3.dp, Color(currentObject.primaryColorHex)),
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = currentObject.emoji, fontSize = 40.sp)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = PlaySurfaceWhite.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = currentObject.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DeepSlateInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Right: Prediction Buttons (Float vs. Sink) & Scientific Explanation
        Column(
            modifier = Modifier
                .weight(0.44f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Water Lab Experiment #${(currentIdx % labItems.size) + 1} of ${labItems.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepSlateInk
                )
                Text(
                    text = "Predict: Will the ${currentObject.label} Float on top or Sink to the bottom?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlateText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            prediction = false
                            droppedInWater = true
                            val rightGuess = !currentObject.sinksInWater
                            viewModel.audio.playWaterSplash(currentObject.sinksInWater)
                            if (rightGuess) {
                                viewModel.audio.speakInstruction("You guessed Floats! ${currentObject.spokenText}")
                                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                            } else {
                                viewModel.onGentleRetryNeeded(activity, "Look closely! ${currentObject.spokenText}")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prediction == false) ActionPlayGreen else ActionContinueBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("water_predict_float_button")
                    ) {
                        Text("🛶 Floats!", fontWeight = FontWeight.Bold, color = PlaySurfaceWhite)
                    }

                    Button(
                        onClick = {
                            prediction = true
                            droppedInWater = true
                            val rightGuess = currentObject.sinksInWater
                            viewModel.audio.playWaterSplash(currentObject.sinksInWater)
                            if (rightGuess) {
                                viewModel.audio.speakInstruction("You guessed Sinks! ${currentObject.spokenText}")
                                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                            } else {
                                viewModel.onGentleRetryNeeded(activity, "Look closely! ${currentObject.spokenText}")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prediction == true) ActionPlayGreen else Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("water_predict_sink_button")
                    ) {
                        Text("⚓ Sinks!", fontWeight = FontWeight.Bold, color = PlaySurfaceWhite)
                    }
                }
            }

            // Result & Explanation Card
            val predictionRight = prediction != null && (prediction == currentObject.sinksInWater)
            Surface(
                color = SoftCreamCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(currentObject.primaryColorHex)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (droppedInWater) {
                            val prefix = if (predictionRight) "🌟 Great prediction!" else "🔬 Let's observe!"
                            "$prefix ${currentObject.explanation}"
                        } else {
                            "Tap Floats! or Sinks! (or tap the tank) to test the ${currentObject.label}!"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepSlateInk
                    )
                    if (droppedInWater) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = currentObject.spokenText,
                            fontSize = 12.sp,
                            color = MutedSlateText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Button(
                onClick = {
                    currentIdx = (currentIdx + 1) % labItems.size
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("water_next_object_button")
            ) {
                Text("Test Next Object ▶", fontWeight = FontWeight.Bold, color = PlaySurfaceWhite)
            }
        }
    }
}

/**
 * ENGINE 4: Flagship Balloon Pop Learning Adventure Engine
 * Delegates to the full 9-World, 13-Mode, Multilingual (EN/TE/HI), 60 FPS BalloonPopAdventureComposable.
 */
@Composable
fun BalloonPopEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    reducedMotion: Boolean,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    BalloonPopAdventureComposable(
        activity = activity,
        adaptive = adaptive,
        reducedMotion = reducedMotion,
        replayKey = replayKey,
        viewModel = viewModel
    )
}
