package com.example.ui.screens

import android.net.Uri
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.widget.VideoView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.CustomMediaOverrideEntity
import com.example.data.RealWorldMediaRegistry
import com.example.model.RealMediaCategory
import com.example.model.RealWorldMediaItem
import com.example.model.ReelMotionStyle
import com.example.ui.theme.ActionContinueBlue
import com.example.ui.theme.ActionListenAmber
import com.example.ui.theme.ActionPlayGreen
import com.example.ui.theme.CalmCoolBlueBg
import com.example.ui.theme.DeepSlateInk
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.PlaySurfaceWhite
import com.example.ui.theme.ReelTheaterDark
import com.example.ui.theme.SoftCreamCard
import com.example.viewmodel.WonderViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Real World Discovery Hub & TikTok/Reels-Style "Wonder Reels" Video Feed:
 * - Kids can choose whether to scroll through Animals (64), Objects (71), or Films/Lessons (22).
 * - Kids can toggle between:
 *   1. Scroll Cards Mode: Rich visual cards with photos/illustrations, "Hear Sound", "Hear Fact", and "Watch Reel".
 *   2. Wonder Reels Mode: Full-screen vertical swipe feed (VerticalPager) playing Google Flow MP4 clips
 *      or Living Motion Reels with synchronized subtitles, authentic audio signatures, and accessible Up/Down controls.
 */
@Composable
fun RealWorldAndReelsScreen(
    category: RealMediaCategory,
    isReelsMode: Boolean,
    initialReelIndex: Int,
    viewModel: WonderViewModel,
    mediaOverrides: List<CustomMediaOverrideEntity>,
    reducedMotion: Boolean
) {
    val items = remember(category) { RealWorldMediaRegistry.getItemsByCategory(category) }
    val gridState = androidx.compose.runtime.key(category) { rememberLazyGridState() }
    val overridesMap = remember(mediaOverrides) { mediaOverrides.associateBy { it.mediaId } }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.audio.stopAllAudio()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isReelsMode) ReelTheaterDark else CalmCoolBlueBg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("real_world_screen")
    ) {
        // Top Header with Category Filter (Animals vs. Objects vs. Films) + Mode Switcher (Scroll Cards vs. Wonder Reels)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isReelsMode) Color(0xFF1E293B) else PlaySurfaceWhite,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.size(48.dp).testTag("real_world_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = if (isReelsMode) PlaySurfaceWhite else DeepSlateInk)
                    }
                    IconButton(
                        onClick = { viewModel.navigateHome() },
                        modifier = Modifier.size(48.dp).testTag("real_world_home_button")
                    ) {
                        Icon(Icons.Default.Home, "Home", tint = if (isReelsMode) PlaySurfaceWhite else DeepSlateInk)
                    }
                    listOf(false to "Cards", true to "Reels").forEach { (reels, label) ->
                        Button(
                            onClick = { viewModel.updateRealWorldMode(category, reels, 0) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReelsMode == reels) ActionContinueBlue else SoftCreamCard,
                                contentColor = if (isReelsMode == reels) PlaySurfaceWhite else DeepSlateInk
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp),
                            modifier = Modifier.weight(1f).height(48.dp).testTag(
                                if (reels) "mode_wonder_reels_button" else "mode_scroll_cards_button"
                            )
                        ) { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        RealMediaCategory.ANIMAL to "Animals",
                        RealMediaCategory.OBJECT to "Objects",
                        RealMediaCategory.FILM_LESSON to "Lessons",
                        RealMediaCategory.ALL to "All"
                    ).forEach { (cat, label) ->
                        Button(
                            onClick = { viewModel.updateRealWorldMode(cat, isReelsMode, 0) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (category == cat) ActionContinueBlue else SoftCreamCard,
                                contentColor = if (category == cat) PlaySurfaceWhite else DeepSlateInk
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("real_world_cat_${cat.id}")
                        ) { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        if (!isReelsMode) {
            // SCROLL CARDS MODE: Kids can scroll smoothly through Animal or Object cards
            LazyVerticalGrid(
                columns = GridCells.Adaptive(220.dp),
                state = gridState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("real_world_scroll_grid"),
                contentPadding = PaddingValues(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items, key = { _, item -> item.id }) { idx, item ->
                    val override = overridesMap[item.id]
                    RealWorldScrollCard(
                        item = item,
                        overrideEntity = override,
                        onHearFact = { viewModel.audio.speakInstruction(item.spokenDescription()) },
                        onHearRecording = item.bundledSoundRes?.let { recording ->
                            { viewModel.audio.playDiscoveryRecording(recording) }
                        },
                        onWatchReel = {
                            viewModel.updateRealWorldMode(
                                category = category,
                                isReelsMode = true,
                                initialReelIndex = idx
                            )
                        }
                    )
                }
            }
        } else {
            // WONDER REELS MODE: TikTok/Reels-style vertical swipe video & living motion feed
            androidx.compose.runtime.key(category, initialReelIndex) {
            WonderReelsVerticalFeed(
                items = items,
                initialIndex = initialReelIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)),
                overridesMap = overridesMap,
                reducedMotion = reducedMotion,
                viewModel = viewModel
            )
            }
        }
    }
}

@Composable
private fun RealWorldScrollCard(
    item: RealWorldMediaItem,
    overrideEntity: CustomMediaOverrideEntity?,
    onHearFact: () -> Unit,
    onHearRecording: (() -> Unit)?,
    onWatchReel: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 232.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onHearFact)
            .testTag("media_card_${item.id}"),
        color = PlaySurfaceWhite,
        border = BorderStroke(2.5.dp, item.primaryColor),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Visual Thumbnail (Bundled AI Art, User Custom Image, or Vivid Habitat Banner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(item.primaryColor.copy(alpha = 0.85f), item.secondaryColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val customImgPath = overrideEntity?.localImagePath
                if (customImgPath != null && File(customImgPath).exists()) {
                    AsyncImage(
                        model = File(customImgPath),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (item.bundledDrawableRes != null) {
                    Image(
                        painter = painterResource(id = item.bundledDrawableRes),
                        contentDescription = item.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(text = item.emoji, fontSize = 50.sp)
                }

                // Badge for habitat/group
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = if (overrideEntity?.localVideoPath != null) "🎬 Google Flow Reel" else item.habitatOrGroup,
                        color = PlaySurfaceWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Card Body & Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column {
                    Text(
                        text = "${item.emoji} ${item.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepSlateInk,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.funFacts.firstOrNull() ?: item.subtitle,
                        fontSize = 12.sp,
                        color = MutedSlateText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onHearRecording ?: onHearFact,
                        colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = DeepSlateInk, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (onHearRecording != null) "Sound" else "Listen", color = DeepSlateInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onWatchReel,
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("watch_reel_${item.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Watch Reel", tint = PlaySurfaceWhite, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (item.hasVideo(overrideEntity)) "Watch" else "Explore", color = PlaySurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Full-Screen Swipeable "Wonder Reels" Feed (TikTok / Reels style for ages 2–6):
 * - VerticalPager for effortless up/down swiping + large accessible Up/Down buttons.
 * - Plays imported/bundled MP4 video (Google Flow) via VideoView when available,
 *   or renders a rich Living Motion Reel Canvas with animated habitat waves/particles,
 *   synchronized cycling subtitles, and automatic voice/sound playback on page settle.
 */
@Composable
private fun WonderReelsVerticalFeed(
    items: List<RealWorldMediaItem>,
    initialIndex: Int,
    overridesMap: Map<String, CustomMediaOverrideEntity>,
    reducedMotion: Boolean,
    viewModel: WonderViewModel
) {
    if (items.isEmpty()) return
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, items.lastIndex),
        pageCount = { items.size }
    )
    val coroutineScope = rememberCoroutineScope()
    var subtitlesVisible by remember { mutableStateOf(true) }
    val settings by viewModel.settingsState.collectAsState()

    // Auto-speak and trigger sound signature whenever the child settles on a new Reel
    LaunchedEffect(pagerState.settledPage, items) {
        viewModel.audio.stopAllAudio()
        val currentItem = items.getOrNull(pagerState.settledPage) ?: return@LaunchedEffect
        if (currentItem.hasVideo(overridesMap[currentItem.id])) return@LaunchedEffect
        viewModel.audio.speakInstruction(currentItem.spokenDescription())
    }

    BoxWithConstraints(Modifier.fillMaxSize().testTag("wonder_reels_pager_container")) {
        val compact = maxWidth < 520.dp
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Main Reel Stage (VerticalPager)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF090D16))
        ) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val item = items[pageIndex]
                val override = overridesMap[item.id]
                SingleWonderReelStage(
                    item = item,
                    overrideEntity = override,
                    isActivePage = pagerState.settledPage == pageIndex,
                    subtitlesVisible = subtitlesVisible,
                    reducedMotion = reducedMotion,
                    narrationEnabled = settings.narrationEnabled
                )
            }
        }

            val active = items.getOrNull(pagerState.settledPage) ?: items.first()
            val activeOverride = overridesMap[active.id]
            val hasVideo = active.hasVideo(activeOverride)
            Surface(
                modifier = Modifier.width(if (compact) 64.dp else 128.dp).fillMaxHeight(),
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, active.primaryColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${pagerState.settledPage + 1}/${items.size}",
                        color = PlaySurfaceWhite,
                        fontSize = 13.sp
                    )
                    ReelControl(
                        Icons.Default.KeyboardArrowUp, "Previous", compact, ActionContinueBlue,
                        Modifier.testTag("reel_prev_button"), enabled = pagerState.settledPage > 0
                    ) {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.settledPage - 1) }
                    }
                    ReelControl(
                        Icons.Default.KeyboardArrowDown, "Next", compact, ActionContinueBlue,
                        Modifier.testTag("reel_next_button")
                    ) {
                        val next = (pagerState.settledPage + 1) % items.size
                        coroutineScope.launch { pagerState.animateScrollToPage(next) }
                    }
                    val recording = active.bundledSoundRes
                    if (recording != null && !activeOverride.hasCustomVideo()) {
                        ReelControl(
                            Icons.AutoMirrored.Filled.VolumeUp, "Sound", compact, ActionListenAmber,
                            Modifier.testTag("reel_hear_sound_button"), ink = DeepSlateInk
                        ) { viewModel.audio.playDiscoveryRecording(recording) }
                    }
                    if (!hasVideo) {
                        ReelControl(
                            Icons.Default.Replay, "Hear fact", compact, ActionPlayGreen,
                            Modifier.testTag("reel_next_fact_button")
                        ) { viewModel.audio.speakInstruction(active.spokenDescription()) }
                    }
                    if (!hasVideo || (active.bundledCaptionsRes != null && !activeOverride.hasCustomVideo())) {
                        ReelControl(
                            Icons.Default.Subtitles, if (subtitlesVisible) "Hide captions" else "Show captions",
                            compact, Color(0xFF475569)
                        ) { subtitlesVisible = !subtitlesVisible }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReelControl(
    icon: ImageVector,
    label: String,
    compact: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    ink: Color = PlaySurfaceWhite,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = ink),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = modifier.fillMaxWidth().height(48.dp)
    ) {
        Icon(icon, contentDescription = if (compact) label else null, modifier = Modifier.size(20.dp))
        if (!compact) {
            Spacer(Modifier.width(4.dp))
            Text(label, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SingleWonderReelStage(
    item: RealWorldMediaItem,
    overrideEntity: CustomMediaOverrideEntity?,
    isActivePage: Boolean,
    subtitlesVisible: Boolean,
    reducedMotion: Boolean,
    narrationEnabled: Boolean
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val overrideVideo = overrideEntity?.localVideoPath?.let(::File)?.takeIf { it.isFile }
    val videoUri = overrideVideo?.let(Uri::fromFile)
        ?: item.bundledVideoRes?.let { Uri.parse("android.resource://${context.packageName}/$it") }
    var videoView by remember(videoUri) { mutableStateOf<VideoView?>(null) }
    var player by remember(videoUri) { mutableStateOf<MediaPlayer?>(null) }
    var videoFailed by remember(videoUri) { mutableStateOf(false) }
    var positionMs by remember(videoUri) { mutableIntStateOf(0) }
    val timedCaptions = remember(context, item.bundledCaptionsRes, overrideVideo) {
        if (overrideVideo != null) emptyList()
        else item.bundledCaptionsRes?.let { res ->
            context.resources.openRawResource(res).bufferedReader().use { parseReelCaptions(it.readText()) }
        }.orEmpty()
    }
    LaunchedEffect(videoView, isActivePage, subtitlesVisible) {
        val view = videoView ?: return@LaunchedEffect
        while (isActivePage && subtitlesVisible) {
            positionMs = view.currentPosition
            delay(200)
        }
    }
    LaunchedEffect(player, narrationEnabled) {
        player?.setVolume(if (narrationEnabled) 1f else 0f, if (narrationEnabled) 1f else 0f)
    }
    DisposableEffect(lifecycle, videoView, isActivePage) {
        val view = videoView
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) view?.pause()
            else if (event == Lifecycle.Event.ON_START && isActivePage) view?.start()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    val transition = rememberInfiniteTransition(label = "living_reel_motion")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (reducedMotion) 12000 else 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reel_phase"
    )
    val zoomScale by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (reducedMotion) 1f else 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reel_zoom"
    )

    var subtitleIndex by remember(item.id) { mutableIntStateOf(0) }
    val captionList = remember(item) {
        (item.subtitles + item.funFacts).distinct().ifEmpty { listOf(item.subtitle) }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        val customImagePath = overrideEntity?.localImagePath
        if (videoUri != null && isActivePage && !videoFailed) {
            androidx.compose.runtime.key(videoUri) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setOnPreparedListener { mp ->
                                player = mp
                                mp.isLooping = overrideVideo != null || item.category == RealMediaCategory.ANIMAL
                                val volume = if (narrationEnabled) 1f else 0f
                                mp.setVolume(volume, volume)
                                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) start()
                            }
                            setOnErrorListener { _, _, _ ->
                                videoFailed = true
                                true
                            }
                            setVideoURI(videoUri)
                            videoView = this
                        }
                    },
                    modifier = Modifier.align(Alignment.Center).fillMaxSize().testTag("reel_video_surface"),
                    onRelease = { view ->
                        player = null
                        videoView = null
                        view.stopPlayback()
                    }
                )
            }
        } else {
            // 2. Living Motion Reel Renderer (Bundled AI Artwork or Custom Photo + Dynamic Habitat Motion Canvas)
            if (customImagePath != null && File(customImagePath).exists()) {
                AsyncImage(
                    model = File(customImagePath),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = zoomScale
                            scaleY = zoomScale
                            translationX = if (reducedMotion) 0f else sin(phase) * 14f
                        }
                )
            } else if (item.bundledDrawableRes != null) {
                Image(
                    painter = painterResource(id = item.bundledDrawableRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = zoomScale
                            scaleY = zoomScale
                            translationX = if (reducedMotion) 0f else sin(phase) * 16f
                            translationY = if (reducedMotion) 0f else cos(phase) * 8f
                        }
                )
            } else {
                // Rich Animated Habitat Stage for Procedural Atlas & Object Reels
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(item.primaryColor.copy(alpha = 0.78f), item.secondaryColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.emoji,
                        fontSize = 128.sp,
                        modifier = Modifier.graphicsLayer {
                            val offsetX = if (reducedMotion) 0f else sin(phase) * 34f
                            val offsetY = if (reducedMotion) 0f else cos(phase * 2f) * 18f
                            translationX = offsetX
                            translationY = offsetY
                            scaleX = zoomScale
                            scaleY = zoomScale
                        }
                    )
                }
            }

            // Animated Habitat Particles / Waves / Sound Rings Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                when (item.motionStyle) {
                    ReelMotionStyle.OCEAN_SWIM -> {
                        for (i in 0..10) {
                            val bx = (w * ((i * 0.11f + phase / 12f) % 1f))
                            val by = h - ((h * ((i * 0.17f + phase / (2f * PI.toFloat())) % 1f)))
                            drawCircle(
                                color = Color.White.copy(alpha = 0.32f),
                                radius = 10f + (i % 4) * 5f,
                                center = Offset(bx, by)
                            )
                        }
                    }
                    ReelMotionStyle.SAVANNA_PROWL, ReelMotionStyle.SKY_FLIGHT, ReelMotionStyle.MEADOW_BOUNCE -> {
                        for (i in 0..8) {
                            val sx = (w * ((i * 0.14f + sin(phase + i) * 0.05f).coerceIn(0.05f, 0.95f)))
                            val sy = (h * (0.18f + (i % 5) * 0.14f + cos(phase + i) * 0.03f))
                            drawCircle(
                                color = Color(0xFFFFFBEB).copy(alpha = 0.28f),
                                radius = 8f + (i % 3) * 4f,
                                center = Offset(sx, sy)
                            )
                        }
                    }
                    ReelMotionStyle.VEHICLE_DRIVE, ReelMotionStyle.SOUND_WAVE_PULSE, ReelMotionStyle.STORY_LESSON_SCENE -> {
                        val pulseRadius = (minOf(w, h) * 0.18f) * (1f + 0.25f * sin(phase * 2f))
                        drawCircle(
                            color = item.primaryColor.copy(alpha = 0.22f),
                            radius = pulseRadius,
                            center = Offset(w * 0.5f, h * 0.45f)
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            color = Color.Black.copy(alpha = 0.72f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (videoFailed) "${item.title} · Video unavailable" else item.title,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = PlaySurfaceWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Bottom Synchronized Subtitles & Narration Strip
        val caption = if (videoUri != null && !videoFailed) {
            timedCaptions.firstOrNull { positionMs in it.startMs until it.endMs }?.text
        } else captionList[subtitleIndex % captionList.size]
        if (subtitlesVisible && !caption.isNullOrBlank()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(14.dp),
                color = Color.Black.copy(alpha = 0.76f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.5.dp, item.primaryColor.copy(alpha = 0.8f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Subtitles,
                        contentDescription = null,
                        tint = ActionListenAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaySurfaceWhite
                    )
                }
            }
        }
    }
}

private fun RealWorldMediaItem.hasVideo(override: CustomMediaOverrideEntity?) =
    bundledVideoRes != null || override.hasCustomVideo()

private fun CustomMediaOverrideEntity?.hasCustomVideo() =
    this?.localVideoPath?.let { File(it).isFile } == true

private fun RealWorldMediaItem.spokenDescription() =
    "$title. ${funFacts.firstOrNull() ?: subtitle}"

internal data class ReelCaption(val startMs: Int, val endMs: Int, val text: String)

internal fun parseReelCaptions(source: String): List<ReelCaption> {
    val timePattern = Regex("(\\d{2}):(\\d{2}):(\\d{2}),(\\d{3})")
    fun milliseconds(match: MatchResult): Int {
        val (hour, minute, second, ms) = match.destructured
        return ((hour.toInt() * 60 + minute.toInt()) * 60 + second.toInt()) * 1000 + ms.toInt()
    }
    return source.trim().split(Regex("\\r?\\n\\s*\\r?\\n")).mapNotNull { block ->
        val lines = block.lines()
        val timing = lines.indexOfFirst { "-->" in it }
        if (timing < 0) return@mapNotNull null
        val times = timePattern.findAll(lines[timing]).toList()
        if (times.size != 2) return@mapNotNull null
        ReelCaption(milliseconds(times[0]), milliseconds(times[1]), lines.drop(timing + 1).joinToString(" "))
    }
}
