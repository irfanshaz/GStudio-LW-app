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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Rect
import androidx.compose.runtime.mutableStateMapOf
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
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Shared Activity Play Shell:
 * - Compact Top Instruction Strip (Home, Back, Buddy Speaker, Adaptive Comfort Badge, Show Me Demo, Favorite Heart, Replay, Next)
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
    val adaptiveParams = remember(activity.id, progress, sessionRetries, sessionSmoothWins) {
        viewModel.getAdaptiveParametersFor(activity)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalmCoolBlueBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
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
                    .padding(horizontal = 10.dp, vertical = 6.dp),
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
                            .size(44.dp)
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("activity_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${activity.emoji} ${activity.title}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if ((!settings.narrationEnabled || !ttsAvailable) && lastSpokenCaption.isNotBlank()) {
                            Text(
                                text = "💬 $lastSpokenCaption",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActionContinueBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("speech_fallback_visible_caption")
                            )
                        }
                    }
                }

                // Right: 5-Star Milestone Indicator + Listen + Show Me + Favorite + Replay + Next
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 5-Star Gentle Milestone Strip
                    Surface(
                        color = SoftCreamCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ActionListenAmber)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (s in 1..5) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (s <= starsEarned) ActionListenAmber else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Listen Instruction Icon Button
                    IconButton(
                        onClick = { viewModel.audio.speakInstruction(activity.instructionText) },
                        modifier = Modifier
                            .size(44.dp)
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
                            .size(44.dp)
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
                            .size(44.dp)
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("activity_replay_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Replay", tint = DeepSlateInk)
                    }

                    // Next Activity Button
                    Button(
                        onClick = { viewModel.launchNextActivityInWorld(activity) },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("activity_next_button")
                    ) {
                        Text("Next", color = PlaySurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = PlaySurfaceWhite, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Optional "Show Me" Demonstration Banner
        AnimatedVisibility(visible = showDemoBanner) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .testTag("show_me_demo_banner"),
                color = ActionPlayGreen.copy(alpha = 0.16f),
                border = BorderStroke(2.dp, ActionPlayGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("👆✨", fontSize = 22.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Show Me Demo: ${activity.showMeDemoHint}",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 14.sp,
                            color = DeepSlateInk
                        )
                    }
                    Button(
                        onClick = { viewModel.dismissShowMeDemo() },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Got It!", color = PlaySurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 2. Large White Play Surface hosting the 14 Reusable Game Engines
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("activity_play_surface"),
            color = PlaySurfaceWhite,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
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
 * - Gentle recovery when dropped outside target
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
        allItems.map { it.categoryOrTarget }.distinct().take(adaptive.effectiveChoiceCount.coerceIn(2, 3))
    }
    val activeItems = remember(allItems, baskets, replayKey) {
        allItems.filter { it.categoryOrTarget in baskets }
    }

    val basketBounds = remember(baskets) { mutableStateMapOf<String, Rect>() }
    val sortedIds = remember(activity.id, replayKey) { mutableStateListOf<String>() }
    var selectedItem by remember(activity.id, replayKey) { mutableStateOf<GameItemOption?>(null) }
    var feedbackMessage by remember(activity.id, replayKey) {
        mutableStateOf("Tap an object (or drag it), then tap its matching basket!")
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
            selectedItem = null
            feedbackMessage = "✨ Great sorting! ${item.spokenText}"
            viewModel.audio.playNoteHz(587.33f, 220, isXylophone = true)
            viewModel.audio.speakInstruction(item.spokenText)
            if (sortedIds.size >= activeItems.size) {
                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = hadRetries)
                feedbackMessage = "🌟 All sorted! Tap Replay or keep sorting a fresh batch!"
            }
        } else {
            hadRetries = true
            feedbackMessage = "Gentle try! ${item.label} belongs in the ${item.categoryOrTarget}."
            viewModel.onGentleRetryNeeded(activity, "Try putting ${item.label} in the ${item.categoryOrTarget}!")
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
                color = DeepSlateInk
            )
            if (remainingItems.isEmpty()) {
                Button(
                    onClick = {
                        sortedIds.clear()
                        hadRetries = false
                        feedbackMessage = "Fresh basket items ready to sort!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("sorting_replenish_button")
                ) {
                    Text("Sort Another Batch! ⭐", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Top Row: Large Draggable & Tappable Objects with Distinct Size Scales
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            remainingItems.forEach { item ->
                val isSelected = selectedItem?.id == item.id
                var dragStart by remember(item.id) { mutableStateOf(Offset.Zero) }
                var coordinates by remember(item.id) { mutableStateOf<LayoutCoordinates?>(null) }
                var dragOffset by remember(item.id) { mutableStateOf(Offset.Zero) }
                val cardSizeDp = (108f * item.sizeScale.coerceIn(0.75f, 1.32f)).dp

                Surface(
                    modifier = Modifier
                        .size(cardSizeDp)
                        .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
                        .onGloballyPositioned { coordinates = it }
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            selectedItem = item
                            feedbackMessage = "Selected ${item.label} — now tap the ${item.categoryOrTarget} below!"
                            viewModel.audio.speakInstruction(item.label)
                        }
                        .pointerInput(item.id, baskets) {
                            detectDragGestures(
                                onDragStart = { position ->
                                    selectedItem = item
                                    dragStart = coordinates?.takeIf { it.isAttached }
                                        ?.localToWindow(position) ?: Offset.Unspecified
                                },
                                onDragEnd = {
                                    val release = dragStart + dragOffset
                                    val basket = baskets.firstOrNull { basketBounds[it]?.contains(release) == true }
                                    if (basket != null) attemptSort(item, basket)
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
                        width = if (isSelected) 4.dp else 2.dp,
                        color = Color(item.primaryColorHex)
                    ),
                    shape = RoundedCornerShape(22.dp),
                    shadowElevation = if (isSelected) 8.dp else 3.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (item.drawableRes != null) {
                            Image(
                                painter = painterResource(id = item.drawableRes),
                                contentDescription = item.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        } else {
                            Text(
                                text = item.emoji,
                                fontSize = (32f * item.sizeScale.coerceIn(0.8f, 1.25f)).sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 13.sp,
                            color = DeepSlateInk,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Bottom Row: Visually Distinct Target Baskets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
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
                        .clip(RoundedCornerShape(26.dp))
                        .clickable {
                            val current = selectedItem ?: remainingItems.firstOrNull()
                            if (current != null) {
                                attemptSort(current, basketName)
                            }
                        }
                        .onGloballyPositioned { basketBounds[basketName] = it.boundsInWindow() }
                        .testTag("sort_basket_$index"),
                    color = if (highlightScaffold) Color(matchingColor).copy(alpha = 0.28f) else Color(matchingColor).copy(alpha = 0.12f),
                    border = BorderStroke(
                        width = if (highlightScaffold) 4.dp else 3.dp,
                        color = Color(matchingColor)
                    ),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🧺 $basketName",
                            style = MaterialTheme.typography.headlineMedium,
                            color = DeepSlateInk
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (highlightScaffold) "✨ Tap Here to Place!" else "Sorted: $countInBasket items",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (highlightScaffold) ActionPlayGreen else MutedSlateText
                        )
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 2: Flagship Alphabet, Number & Shape Tracing Studio
 * - Offers Show Me animated finger path, start star marker, guide path, and effort progress indicator
 * - Age-adjusted tolerance (adaptive.tracingToleranceDp) and coverage check
 * - Anti-scribble check: random full-screen scribbling does NOT count as success
 * - Saves position in alphabet/number journeys locally
 * - Provides Next, Retry, and Skip controls
 */
@Composable
fun TracingStudioEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    savedStepIndex: Int,
    showDemo: Boolean,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val glyphsSequence = remember(activity.id) {
        when {
            activity.id.contains("w04_tracing_4") || activity.id.contains("w04_tracing_5") ->
                ('a'..'z').map { it.toString() to "Lowercase $it" }
            activity.id.contains("w04_tracing_6") || activity.id.contains("w04_tracing_7") ->
                (1..10).map { it.toString() to "Number $it" }
            activity.id.contains("w04_tracing_8") ->
                listOf("◯" to "Circle", "△" to "Triangle", "□" to "Square", "☆" to "Star", "♡" to "Heart")
            else ->
                listOf(
                    "A" to "Apple", "B" to "Bear", "C" to "Cat", "D" to "Dolphin",
                    "E" to "Elephant", "F" to "Fire Truck", "G" to "Giraffe", "H" to "Hat",
                    "I" to "Igloo", "J" to "Jellyfish", "K" to "Kite", "L" to "Lion",
                    "M" to "Moon", "N" to "Nest", "O" to "Owl", "P" to "Puppy",
                    "Q" to "Queen", "R" to "Rainbow", "S" to "Sun", "T" to "Train",
                    "U" to "Umbrella", "V" to "Violin", "W" to "Whale", "X" to "Xylophone",
                    "Y" to "Yak", "Z" to "Zebra"
                )
        }
    }

    var currentStep by remember(activity.id) {
        mutableIntStateOf(savedStepIndex.coerceIn(0, glyphsSequence.lastIndex))
    }
    val (activeGlyph, activeWord) = glyphsSequence[currentStep % glyphsSequence.size]

    val userPoints = remember(currentStep, replayKey) { mutableStateListOf<Offset>() }
    var statusMessage by remember(currentStep, replayKey) {
        mutableStateOf("Start at the green star ⭐ and trace $activeGlyph for $activeWord!")
    }
    var hadRetries by remember(currentStep, replayKey) { mutableStateOf(false) }
    var isCompleted by remember(currentStep, replayKey) { mutableStateOf(false) }

    // Normalized guide checkpoints (0.2..0.8 in canvas coordinates) for the active glyph
    val normalizedCheckpoints = remember(activeGlyph) {
        buildGuideCheckpointsForGlyph(activeGlyph)
    }

    val demoTransition = rememberInfiniteTransition(label = "trace_demo")
    val demoProgress by demoTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "demo_cursor"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_tracing_studio"),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Left: Large Interactive Tracing Canvas
        var canvasSize by remember { mutableStateOf(Offset(600f, 360f)) }
        val checkpointPixels = remember(normalizedCheckpoints, canvasSize) {
            normalizedCheckpoints.map { Offset(it.x * canvasSize.x, it.y * canvasSize.y) }
        }

        val tolerancePx = adaptive.tracingToleranceDp * 2.2f
        val coveredCount = remember(userPoints.size, checkpointPixels, tolerancePx) {
            if (userPoints.isEmpty()) 0
            else checkpointPixels.count { cp ->
                userPoints.any { up -> hypot(up.x - cp.x, up.y - cp.y) <= tolerancePx }
            }
        }
        val offPathPointsCount = remember(userPoints.size, checkpointPixels, tolerancePx) {
            userPoints.count { up ->
                checkpointPixels.none { cp -> hypot(up.x - cp.x, up.y - cp.y) <= tolerancePx * 1.45f }
            }
        }
        val coverageRatio = if (checkpointPixels.isNotEmpty()) {
            coveredCount.toFloat() / checkpointPixels.size.toFloat()
        } else 0f
        val scribbleRatio = if (userPoints.isNotEmpty()) {
            offPathPointsCount.toFloat() / userPoints.size.toFloat()
        } else 0f

        Box(
            modifier = Modifier
                .weight(0.68f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(SoftCreamCard)
                .pointerInput(currentStep, replayKey) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            userPoints.add(startOffset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            userPoints.add(change.position)
                        },
                        onDragEnd = {
                            if (scribbleRatio > 0.55f && userPoints.size > 25) {
                                // Reject random scribbling gently
                                hadRetries = true
                                statusMessage = "Oops, that went outside the lines! Try following the dotted path from the green star."
                                viewModel.onGentleRetryNeeded(activity, "Stay right on the glowing guide path!")
                            } else if (coverageRatio >= adaptive.tracingCoverageRequired && !isCompleted) {
                                isCompleted = true
                                val accuracyPct = ((1f - scribbleRatio.coerceIn(0f, 0.5f)) * 100f).roundToInt()
                                statusMessage = "🌟 Wonderful tracing of $activeGlyph ($activeWord)!"
                                viewModel.onActivityMilestoneCompleted(
                                    activity = activity,
                                    hadRetriesInRound = hadRetries,
                                    nextStepIndex = (currentStep + 1) % glyphsSequence.size,
                                    tracingAccuracyPercent = accuracyPct
                                )
                            } else if (!isCompleted) {
                                statusMessage = "Keep going! Trace all the way to the finish dot (${(coverageRatio * 100).roundToInt()}%)."
                            }
                        }
                    )
                }
                .pointerInput(currentStep, replayKey) {
                    // Accessible tap-along-checkpoints alternative for children who prefer tapping stars
                    detectTapGestures { tapOffset ->
                        userPoints.add(tapOffset)
                        val nearest = checkpointPixels.minByOrNull { hypot(it.x - tapOffset.x, it.y - tapOffset.y) }
                        if (nearest != null && hypot(nearest.x - tapOffset.x, nearest.y - tapOffset.y) <= tolerancePx * 1.4f) {
                            userPoints.add(nearest)
                            viewModel.audio.playNoteHz(440f + coveredCount * 28f, 120, isXylophone = true)
                        }
                        if (coverageRatio >= adaptive.tracingCoverageRequired && !isCompleted) {
                            isCompleted = true
                            statusMessage = "🌟 Wonderful tracing of $activeGlyph ($activeWord)!"
                            viewModel.onActivityMilestoneCompleted(
                                activity = activity,
                                hadRetriesInRound = hadRetries,
                                nextStepIndex = (currentStep + 1) % glyphsSequence.size,
                                tracingAccuracyPercent = 92
                            )
                        }
                    }
                }
                .testTag("tracing_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                canvasSize = Offset(size.width, size.height)
                val pts = normalizedCheckpoints.map { Offset(it.x * size.width, it.y * size.height) }

                // 1. Guide Track & Dotted Centerline
                if (pts.size >= 2) {
                    val guidePath = Path().apply {
                        moveTo(pts.first().x, pts.first().y)
                        for (i in 1 until pts.size) {
                            lineTo(pts[i].x, pts[i].y)
                        }
                    }
                    drawPath(
                        path = guidePath,
                        color = Color(0xFFCBD5E1),
                        style = Stroke(width = tolerancePx * 1.15f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = guidePath,
                        color = Color(0xFF64748B),
                        style = Stroke(
                            width = 6f,
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 18f), 0f)
                        )
                    )
                }

                // 2. Checkpoints & Start/Finish Markers
                pts.forEachIndexed { idx, pt ->
                    val isCovered = userPoints.any { up -> hypot(up.x - pt.x, up.y - pt.y) <= tolerancePx }
                    drawCircle(
                        color = when {
                            idx == 0 -> ActionPlayGreen
                            idx == pts.lastIndex -> ActionListenAmber
                            isCovered -> ActionContinueBlue
                            else -> Color.White
                        },
                        radius = if (idx == 0 || idx == pts.lastIndex) 22f else 12f,
                        center = pt
                    )
                }

                // 3. Child's Drawn Stroke
                if (userPoints.size >= 2) {
                    val childPath = Path().apply {
                        moveTo(userPoints.first().x, userPoints.first().y)
                        for (i in 1 until userPoints.size) {
                            lineTo(userPoints[i].x, userPoints[i].y)
                        }
                    }
                    drawPath(
                        path = childPath,
                        color = if (isCompleted) ActionPlayGreen else ActionContinueBlue,
                        style = Stroke(width = 28f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // 4. Optional "Show Me" Animated Guide Cursor
                if (showDemo && pts.isNotEmpty()) {
                    val demoIdx = ((pts.size - 1) * demoProgress).toInt().coerceIn(0, pts.lastIndex)
                    val demoPt = pts[demoIdx]
                    drawCircle(
                        color = ActionListenAmber,
                        radius = 28f,
                        center = demoPt
                    )
                }
            }

            // Large Watermark Letter Label in Corner
            Text(
                text = "$activeGlyph • $activeWord",
                style = MaterialTheme.typography.headlineLarge,
                color = DeepSlateInk.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
            )
        }

        // Right: Effort Indicator, Phonics Cue & Next / Retry / Skip Controls
        Column(
            modifier = Modifier
                .weight(0.32f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = SoftCreamCard,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, ActionContinueBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = activeGlyph,
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 54.sp,
                        color = ActionContinueBlue
                    )
                    Text(
                        text = "$activeGlyph is for $activeWord",
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepSlateInk
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Effort & Path Coverage: ${(coverageRatio * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedSlateText
                    )
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { coverageRatio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = ActionPlayGreen,
                        trackColor = Color(0xFFCBD5E1)
                    )
                }
            }

            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = DeepSlateInk
            )

            // Retry, Skip & Next Step Buttons (Guaranteed by Specification)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val ch = activeGlyph.firstOrNull() ?: 'A'
                        if (ch.isLetter()) {
                            viewModel.audio.speakPhonicsLetter(ch, speakSoundNotName = true)
                        } else {
                            viewModel.audio.speakInstruction("$activeGlyph for $activeWord!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = DeepSlateInk)
                    Spacer(Modifier.width(6.dp))
                    Text("Hear Sound", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            userPoints.clear()
                            isCompleted = false
                            statusMessage = "Canvas cleared—try tracing $activeGlyph again!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionRetryOrange),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("tracing_retry_button")
                    ) {
                        Text("Retry", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            currentStep = (currentStep + 1) % glyphsSequence.size
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("tracing_skip_button")
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Skip", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("Skip", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        currentStep = (currentStep + 1) % glyphsSequence.size
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("tracing_next_glyph_button")
                ) {
                    Text("Next Letter / Shape ▶", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun buildGuideCheckpointsForGlyph(glyph: String): List<Offset> {
    return when (glyph.uppercase()) {
        "A" -> listOf(
            Offset(0.50f, 0.16f), Offset(0.38f, 0.48f), Offset(0.26f, 0.82f),
            Offset(0.50f, 0.16f), Offset(0.62f, 0.48f), Offset(0.74f, 0.82f),
            Offset(0.35f, 0.56f), Offset(0.50f, 0.56f), Offset(0.65f, 0.56f)
        )
        "◯", "O", "0" -> (0..11).map { i ->
            val angle = (i.toFloat() / 11f) * 2f * PI.toFloat() - (PI.toFloat() / 2f)
            Offset(0.50f + 0.26f * kotlin.math.cos(angle), 0.50f + 0.32f * sin(angle))
        }
        "｜", "1", "I" -> (0..8).map { i ->
            Offset(0.50f, 0.16f + (i / 8f) * 0.68f)
        }
        "⌒" -> (0..10).map { i ->
            val t = i / 10f
            Offset(0.20f + t * 0.60f, 0.72f - sin(t * PI.toFloat()) * 0.48f)
        }
        else -> listOf(
            Offset(0.28f, 0.18f), Offset(0.28f, 0.50f), Offset(0.28f, 0.82f),
            Offset(0.48f, 0.22f), Offset(0.66f, 0.36f), Offset(0.48f, 0.50f),
            Offset(0.68f, 0.66f), Offset(0.50f, 0.82f)
        )
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
    val labItems = remember(activity.id) { activity.items }
    var currentIdx by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    val currentObject = labItems[currentIdx % labItems.size]

    var prediction by remember(currentIdx, replayKey) { mutableStateOf<Boolean?>(null) } // false = Float, true = Sink
    var droppedInWater by remember(currentIdx, replayKey) { mutableStateOf(false) }

    val objectYFraction by animateFloatAsState(
        targetValue = when {
            !droppedInWater -> 0.14f
            currentObject.sinksInWater -> 0.82f
            else -> 0.42f // Floats right on the water surface!
        },
        animationSpec = tween(durationMillis = 900),
        label = "water_drop_y"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_water_lab"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left: Interactive Animated Water Tank
        Box(
            modifier = Modifier
                .weight(0.58f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE0F2FE))
                .clickable {
                    if (!droppedInWater) {
                        if (prediction == null) prediction = !currentObject.sinksInWater
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
                // Sky above water
                drawRect(
                    color = Color(0xFFE0F2FE),
                    size = androidx.compose.ui.geometry.Size(size.width, waterTop)
                )
                // Deep water below surface
                drawRect(
                    color = Color(0xFF0284C7).copy(alpha = 0.45f),
                    topLeft = Offset(0f, waterTop),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height - waterTop)
                )
                // Sandy tank bottom
                drawRect(
                    color = Color(0xFFFDE68A),
                    topLeft = Offset(0f, size.height * 0.88f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.12f)
                )
            }

            // Surface Label & Bottom Label
            Text(
                text = "🌊 Water Surface (Float Zone)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0369A1),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )

            // Tested Object Inside Tank
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (objectYFraction * 240f).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = PlaySurfaceWhite,
                    border = BorderStroke(3.dp, Color(currentObject.primaryColorHex)),
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = currentObject.emoji, fontSize = 44.sp)
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
                        fontSize = 13.sp,
                        color = DeepSlateInk,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Right: Prediction Buttons (Float vs. Sink) & Scientific Explanation
        Column(
            modifier = Modifier
                .weight(0.42f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Water Lab Experiment #${(currentIdx % labItems.size) + 1}",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepSlateInk
                )
                Text(
                    text = "Step 1: Predict if the ${currentObject.label} will Float on top or Sink to the bottom!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlateText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            prediction = false
                            droppedInWater = true
                            viewModel.audio.playWaterSplash(currentObject.sinksInWater)
                            viewModel.audio.speakInstruction(currentObject.spokenText)
                            viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prediction == false) ActionPlayGreen else ActionContinueBlue
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("water_predict_float_button")
                    ) {
                        Text("🛶 Floats!", style = MaterialTheme.typography.titleMedium, color = PlaySurfaceWhite)
                    }

                    Button(
                        onClick = {
                            prediction = true
                            droppedInWater = true
                            viewModel.audio.playWaterSplash(currentObject.sinksInWater)
                            viewModel.audio.speakInstruction(currentObject.spokenText)
                            viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (prediction == true) ActionPlayGreen else Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("water_predict_sink_button")
                    ) {
                        Text("⚓ Sinks!", style = MaterialTheme.typography.titleMedium, color = PlaySurfaceWhite)
                    }
                }
            }

            // Result & Explanation Card
            Surface(
                color = SoftCreamCard,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(2.dp, Color(currentObject.primaryColorHex)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (droppedInWater) "🔬 Observation: ${currentObject.explanation}" else "Tap Floats! or Sinks! (or tap the tank) to drop the ${currentObject.label} in!",
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepSlateInk
                    )
                    if (droppedInWater) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = currentObject.spokenText,
                            fontSize = 13.sp,
                            color = MutedSlateText
                        )
                    }
                }
            }

            Button(
                onClick = {
                    currentIdx = (currentIdx + 1) % labItems.size
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("water_next_object_button")
            ) {
                Text("Test Next Object ▶", style = MaterialTheme.typography.titleMedium, color = PlaySurfaceWhite)
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


