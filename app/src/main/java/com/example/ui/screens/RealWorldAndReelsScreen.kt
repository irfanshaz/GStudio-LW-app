package com.example.ui.screens

import android.net.Uri
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
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("real_world_screen")
    ) {
        // Top Header with Category Filter (Animals vs. Objects vs. Films) + Mode Switcher (Scroll Cards vs. Wonder Reels)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isReelsMode) Color(0xFF1E293B) else PlaySurfaceWhite,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back & Home
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("real_world_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.navigateHome() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("real_world_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = DeepSlateInk)
                    }
                }

                // Category Filter Pills: Animals | Objects | Films & Lessons | All
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        RealMediaCategory.ANIMAL,
                        RealMediaCategory.OBJECT,
                        RealMediaCategory.FILM_LESSON,
                        RealMediaCategory.ALL
                    ).forEach { cat ->
                        val selected = category == cat
                        Button(
                            onClick = {
                                viewModel.updateRealWorldMode(
                                    category = cat,
                                    isReelsMode = isReelsMode,
                                    initialReelIndex = 0
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) ActionContinueBlue else SoftCreamCard,
                                contentColor = if (selected) PlaySurfaceWhite else DeepSlateInk
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("real_world_cat_${cat.id}")
                        ) {
                            Text("${cat.emoji} ${cat.label}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                // View Mode Toggle: Scroll Cards vs. Wonder Reels (TikTok/Reels style)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.updateRealWorldMode(
                                category = category,
                                isReelsMode = false,
                                initialReelIndex = 0
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isReelsMode) ActionPlayGreen else SoftCreamCard,
                            contentColor = if (!isReelsMode) PlaySurfaceWhite else DeepSlateInk
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("mode_scroll_cards_button")
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = "Scroll Cards", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Scroll Cards", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.updateRealWorldMode(
                                category = category,
                                isReelsMode = true,
                                initialReelIndex = 0
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isReelsMode) ActionListenAmber else SoftCreamCard,
                            contentColor = DeepSlateInk
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("mode_wonder_reels_button")
                    ) {
                        Icon(Icons.Default.Movie, contentDescription = "Wonder Reels", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Wonder Reels", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        if (!isReelsMode) {
            // SCROLL CARDS MODE: Kids can scroll smoothly through Animal or Object cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
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
                        onHearSoundAndFact = {
                            viewModel.audio.playAnimalOrObjectSignature(
                                baseFreqHz = item.soundFreqHz,
                                label = item.title,
                                spokenFact = item.funFacts.firstOrNull() ?: item.subtitle
                            )
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

@Composable
private fun RealWorldScrollCard(
    item: RealWorldMediaItem,
    overrideEntity: CustomMediaOverrideEntity?,
    onHearSoundAndFact: () -> Unit,
    onWatchReel: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onHearSoundAndFact() }
            .testTag("media_card_${item.id}"),
        color = PlaySurfaceWhite,
        border = BorderStroke(2.5.dp, item.primaryColor),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        contentScale = ContentScale.Crop,
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
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${item.emoji} ${item.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepSlateInk,
                        maxLines = 1,
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
                        onClick = onHearSoundAndFact,
                        colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Hear Sound", tint = DeepSlateInk, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Sound", color = DeepSlateInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onWatchReel,
                        colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Watch Reel", tint = PlaySurfaceWhite, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reel", color = PlaySurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

    // Auto-speak and trigger sound signature whenever the child settles on a new Reel
    LaunchedEffect(pagerState.settledPage, items) {
        val currentItem = items.getOrNull(pagerState.settledPage) ?: return@LaunchedEffect
        viewModel.audio.playAnimalOrObjectSignature(
            baseFreqHz = currentItem.soundFreqHz,
            label = currentItem.title,
            spokenFact = currentItem.funFacts.firstOrNull() ?: currentItem.subtitle
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wonder_reels_pager_container"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Reel Stage (VerticalPager)
        Box(
            modifier = Modifier
                .weight(0.76f)
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
                    reducedMotion = reducedMotion
                )
            }
        }

        // Right-side TikTok/Reels Tactile Control Rail for Preschoolers
        val activeItem = items.getOrNull(pagerState.settledPage) ?: items.first()
        var currentFactIdx by remember(activeItem.id) { mutableIntStateOf(0) }

        Surface(
            modifier = Modifier
                .weight(0.24f)
                .fillMaxHeight(),
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, activeItem.primaryColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Reel Position Counter
                Surface(
                    color = activeItem.primaryColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Reel ${pagerState.settledPage + 1} of ${items.size}",
                        color = PlaySurfaceWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Accessible Swipe Up / Previous Reel Button
                Button(
                    onClick = {
                        if (pagerState.settledPage > 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.settledPage - 1)
                            }
                        }
                    },
                    enabled = pagerState.settledPage > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reel_prev_button")
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous Reel")
                    Spacer(Modifier.width(4.dp))
                    Text("Prev Reel", fontWeight = FontWeight.Bold)
                }

                // Hear Sound Button
                Button(
                    onClick = {
                        viewModel.audio.playAnimalOrObjectSignature(
                            baseFreqHz = activeItem.soundFreqHz,
                            label = activeItem.title,
                            spokenFact = activeItem.soundDescription
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reel_hear_sound_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Hear Sound", tint = DeepSlateInk)
                    Spacer(Modifier.width(6.dp))
                    Text("Hear Sound", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }

                // Next Fun Fact Button
                Button(
                    onClick = {
                        val facts = activeItem.funFacts
                        if (facts.isNotEmpty()) {
                            currentFactIdx = (currentFactIdx + 1) % facts.size
                            viewModel.audio.speakInstruction(facts[currentFactIdx])
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reel_next_fact_button")
                ) {
                    Icon(Icons.Default.Replay, contentDescription = "Tell Me More", tint = PlaySurfaceWhite)
                    Spacer(Modifier.width(6.dp))
                    Text("Fun Fact", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }

                // Toggle Subtitles Button
                Button(
                    onClick = { subtitlesVisible = !subtitlesVisible },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (subtitlesVisible) Color(0xFF475569) else Color(0xFF334155)
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Subtitles, contentDescription = "Subtitles", tint = PlaySurfaceWhite)
                    Spacer(Modifier.width(6.dp))
                    Text(if (subtitlesVisible) "Captions ON" else "Captions OFF", color = PlaySurfaceWhite, fontSize = 13.sp)
                }

                // Accessible Swipe Down / Next Reel Button
                Button(
                    onClick = {
                        val next = (pagerState.settledPage + 1) % items.size
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(next)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reel_next_button")
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next Reel")
                    Spacer(Modifier.width(4.dp))
                    Text("Next Reel", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SingleWonderReelStage(
    item: RealWorldMediaItem,
    overrideEntity: CustomMediaOverrideEntity?,
    isActivePage: Boolean,
    subtitlesVisible: Boolean,
    reducedMotion: Boolean
) {
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
        targetValue = if (reducedMotion) 1.02f else 1.12f,
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

    LaunchedEffect(isActivePage, item.id) {
        if (isActivePage && captionList.size > 1) {
            while (true) {
                delay(3800)
                subtitleIndex = (subtitleIndex + 1) % captionList.size
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val customVideoPath = overrideEntity?.localVideoPath
        val customImagePath = overrideEntity?.localImagePath

        if (customVideoPath != null && File(customVideoPath).exists() && isActivePage) {
            // 1. Native Offline MP4 Video Playback for user-imported Google Flow clips
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(Uri.fromFile(File(customVideoPath)))
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            start()
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                onRelease = { videoView ->
                    runCatching { videoView.stopPlayback() }
                }
            )
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
                    contentScale = ContentScale.Crop,
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

        // Top Overlay Badge: Title, Category & Manifest Status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.emoji, fontSize = 26.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = PlaySurfaceWhite
                        )
                        Text(
                            text = "${item.habitatOrGroup} • ${item.soundDescription}",
                            fontSize = 12.sp,
                            color = Color(0xFFBAE6FD)
                        )
                    }
                }
            }

            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (overrideEntity?.localVideoPath != null) "Google Flow MP4" else item.manifest.status.label,
                    color = Color(0xFFA7F3D0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Bottom Synchronized Subtitles & Narration Strip
        if (subtitlesVisible) {
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
                        text = captionList[subtitleIndex % captionList.size],
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaySurfaceWhite
                    )
                }
            }
        }
    }
}
