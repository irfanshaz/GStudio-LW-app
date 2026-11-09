package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Toys
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivityProgressEntity
import com.example.data.SavedArtworkEntity
import com.example.data.WonderCatalogRegistry
import com.example.model.ActivityConfig
import com.example.model.AgeBand
import com.example.model.EngineType
import com.example.model.LandId
import com.example.model.LearningBuddy
import com.example.model.RealMediaCategory
import com.example.model.WorldSpec
import com.example.ui.engines.decodeStrokesPayload
import com.example.ui.theme.ActionContinueBlue
import com.example.ui.theme.ActionFavoritePink
import com.example.ui.theme.ActionListenAmber
import com.example.ui.theme.ActionPlayGreen
import com.example.ui.theme.CalmCoolBlueBg
import com.example.ui.theme.CalmSkyGradientBottom
import com.example.ui.theme.CalmSkyGradientTop
import com.example.ui.theme.DeepSlateInk
import com.example.ui.theme.LandCreativeViolet
import com.example.ui.theme.LandThinkingEmerald
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.PlaySurfaceWhite
import com.example.ui.theme.SoftCreamCard
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel

@Composable
fun OnboardingScreen(
    initialAge: AgeBand,
    initialBuddy: LearningBuddy,
    onListenPreview: (String) -> Unit,
    onFinishOnboarding: (AgeBand, LearningBuddy) -> Unit
) {
    var selectedAge by remember { mutableStateOf(initialAge) }
    var selectedBuddy by remember { mutableStateOf(initialBuddy) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CalmSkyGradientTop, CalmSkyGradientBottom)))
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("onboarding_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨🌈", fontSize = 30.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Little Wonder World",
                        style = MaterialTheme.typography.headlineLarge,
                        color = DeepSlateInk
                    )
                }
                Button(
                    onClick = { onListenPreview(com.example.audio.VoiceAssetManifest.STANDARD_PREVIEW_SENTENCE) },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("onboarding_listen_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Preview Teacher Voice", tint = DeepSlateInk)
                    Spacer(Modifier.width(6.dp))
                    Text("Preview Teacher Voice", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }
            }

            // Middle: Age Bands + 4 Visual Play Buddy Companions side by side in Landscape
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Step 1: Age Band Column
                Card(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "1. Choose Age Band (Adaptive)",
                            style = MaterialTheme.typography.titleLarge,
                            color = DeepSlateInk
                        )
                        AgeBand.entries.forEach { age ->
                            val isSelected = selectedAge == age
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedAge = age
                                        onListenPreview(age.label)
                                    }
                                    .testTag("age_band_${age.id}"),
                                color = if (isSelected) ActionContinueBlue.copy(alpha = 0.14f) else SoftCreamCard,
                                border = BorderStroke(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) ActionContinueBlue else Color(0xFFCBD5E1)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = when (age) {
                                                AgeBand.TODDLER_2_3 -> "🐣"
                                                AgeBand.PRESCHOOL_4_5 -> "🦊"
                                                AgeBand.KINDER_5_6 -> "🚀"
                                            },
                                            fontSize = 30.sp
                                        )
                                        Text(
                                            text = age.label,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepSlateInk
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = "Selected Age",
                                            tint = ActionContinueBlue,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Step 2: 4 Visual Play Buddy Companions
                Card(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "2. Pick Your Play Companion",
                            style = MaterialTheme.typography.titleLarge,
                            color = DeepSlateInk
                        )
                        val buddies = LearningBuddy.entries
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (rowIdx in 0..1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (colIdx in 0..1) {
                                        val buddy = buddies[rowIdx * 2 + colIdx]
                                        val isSelected = selectedBuddy == buddy
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(18.dp))
                                                .clickable {
                                                    selectedBuddy = buddy
                                                    onListenPreview(buddy.greeting)
                                                }
                                                .testTag("buddy_${buddy.id}"),
                                            color = if (isSelected) buddy.accentColor.copy(alpha = 0.18f) else SoftCreamCard,
                                            border = BorderStroke(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) buddy.accentColor else Color(0xFFCBD5E1)
                                            ),
                                            shape = RoundedCornerShape(18.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(CircleShape)
                                                        .background(buddy.accentColor.copy(alpha = 0.25f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(text = buddy.emoji, fontSize = 26.sp)
                                                }
                                                Spacer(Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = buddy.displayName,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = DeepSlateInk
                                                    )
                                                    Text(
                                                        text = buddy.species,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontSize = 12.sp,
                                                        color = MutedSlateText
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

            // Bottom Start Adventure CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onFinishOnboarding(selectedAge, selectedBuddy) },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("start_playing_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Start Exploring with ${selectedBuddy.displayName}!",
                        style = MaterialTheme.typography.titleLarge,
                        color = PlaySurfaceWhite
                    )
                }
            }
        }
    }
}

/**
 * Fixed child-facing Landscape Home screen with no vertical scrolling required:
 * - Top bar: Buddy Greeting + Age Band indicator + Toy Box + Surprise Play + Real World Media & Reels + Parent Corner
 *   (Sized so all 5 top-bar controls have full visible width and >= 48dp height on phone landscape 1080x1920 @ 420dpi)
 * - Left Featured Column: Prominent Creative Garden card & Featured Thinking Forest card + Real World & Wonder Reels banner
 * - Right 6 Learning Lands Grid: All 6 Learning Lands with progress stars and instant navigation
 */
@Composable
fun HomeScreen(
    viewModel: WonderViewModel,
    ageBand: AgeBand,
    buddy: LearningBuddy,
    progressList: List<ActivityProgressEntity>,
    reducedMotion: Boolean
) {
    val totalStars = remember(progressList) { progressList.sumOf { it.starsEarned } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CalmSkyGradientTop, CalmCoolBlueBg)))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Navigation & Action Strip (Compact spacing so Parents button is 100% visible on phone landscape)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PlaySurfaceWhite,
                shape = RoundedCornerShape(22.dp),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Buddy + Age pill (tapping speaks greeting)
                    Row(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .clip(RoundedCornerShape(16.dp))
                            .background(buddy.accentColor.copy(alpha = 0.16f))
                            .clickable { viewModel.audio.speakInstruction(buddy.greeting) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("home_buddy_banner"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = buddy.emoji, fontSize = 22.sp)
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                text = buddy.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepSlateInk,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${ageBand.label} • ⭐ $totalStars",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedSlateText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    // Quick Action Buttons: Toy Box, Surprise Play, Real World & Reels, Parent Corner
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(WonderScreen.ToyBox) },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("home_toy_box_button")
                        ) {
                            Icon(Icons.Default.Toys, contentDescription = "Toy Box", tint = DeepSlateInk, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Toy Box", color = DeepSlateInk, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.launchSurprisePlay() },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("home_surprise_play_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Surprise Play", tint = PlaySurfaceWhite, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Surprise Play", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.navigateTo(
                                    WonderScreen.RealWorldHub(
                                        category = RealMediaCategory.ANIMAL,
                                        isReelsMode = false
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("home_real_world_button")
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = "Real World & Wonder Reels", tint = PlaySurfaceWhite, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Real World", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Surface(
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.navigateTo(WonderScreen.ParentCorner(unlocked = false)) }
                                .testTag("home_parent_corner_button"),
                            color = SoftCreamCard,
                            border = BorderStroke(1.5.dp, Color(0xFF94A3B8)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = "Parent Corner", tint = DeepSlateInk, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Parents", color = DeepSlateInk, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // 2. Main Fixed Landscape Play Hub: Left Featured Cards + Right 6 Learning Lands Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Spotlight Column: Prominent Creative Garden, Featured Thinking Forest, and Wonder Reels Entry
                Column(
                    modifier = Modifier
                        .weight(0.36f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeaturedSpotlightCard(
                        title = "Creative Garden",
                        subtitle = "My Sketch Pad, Music Piano & 30 Storytime Books!",
                        emoji = "🎨🎹",
                        accentColor = LandCreativeViolet,
                        badgeText = "Featured Studio",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("featured_creative_garden_card"),
                        onClick = { viewModel.navigateTo(WonderScreen.LandWorlds(LandId.CREATIVE_GARDEN)) }
                    )

                    FeaturedSpotlightCard(
                        title = "Thinking Forest",
                        subtitle = "Color & Shape Baskets, Memory Pairs, Puzzles & Coding!",
                        emoji = "🌲🧺",
                        accentColor = LandThinkingEmerald,
                        badgeText = "Featured Logic",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("featured_thinking_forest_card"),
                        onClick = { viewModel.navigateTo(WonderScreen.LandWorlds(LandId.THINKING_FOREST)) }
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.navigateTo(
                                    WonderScreen.RealWorldHub(
                                        category = RealMediaCategory.ANIMAL,
                                        isReelsMode = true
                                    )
                                )
                            }
                            .testTag("featured_wonder_reels_card"),
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🦁🚂", fontSize = 26.sp)
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Wonder Reels",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PlaySurfaceWhite
                                )
                            }
                            Surface(
                                color = ActionPlayGreen,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Watch Reels", tint = PlaySurfaceWhite)
                                }
                            }
                        }
                    }
                }

                // Right Column: All 6 Learning Lands (2 rows x 3 columns fixed landscape grid)
                Column(
                    modifier = Modifier
                        .weight(0.64f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val lands = LandId.entries
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0..2) {
                                val land = lands[row * 3 + col]
                                val worldsInLand = remember(land) { WonderCatalogRegistry.getWorldsForLand(land) }
                                val landStars = remember(progressList, land) {
                                    progressList.filter { it.landId == land.id }.sumOf { it.starsEarned }
                                }
                                LandTileCard(
                                    land = land,
                                    worldCount = worldsInLand.size,
                                    starsEarned = landStars,
                                    reducedMotion = reducedMotion,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .testTag("land_card_${land.id}"),
                                    onClick = { viewModel.navigateTo(WonderScreen.LandWorlds(land)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedSpotlightCard(
    title: String,
    subtitle: String,
    emoji: String,
    accentColor: Color,
    badgeText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        color = PlaySurfaceWhite,
        border = BorderStroke(3.dp, accentColor),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(accentColor.copy(alpha = 0.16f), PlaySurfaceWhite)
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 26.sp)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepSlateInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                color = accentColor,
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = title, tint = PlaySurfaceWhite)
                }
            }
        }
    }
}

@Composable
private fun LandTileCard(
    land: LandId,
    worldCount: Int,
    starsEarned: Int,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reducedMotion) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "land_scale"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .clickable {
                pressed = !pressed
                onClick()
            },
        color = PlaySurfaceWhite,
        border = BorderStroke(2.5.dp, land.accentColor),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(land.accentColor.copy(alpha = 0.16f), PlaySurfaceWhite)
                    )
                )
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    color = SoftCreamCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, land.accentColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "⭐ $starsEarned",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepSlateInk,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(land.accentColor.copy(alpha = 0.24f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = land.emoji, fontSize = 30.sp)
            }

            Text(
                text = land.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = DeepSlateInk,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Toy Box & My Shelf Screen:
 * - Large picture-first shortcuts to favorite tactile games
 * - My Shelf tabs: Favorites, Recently Played (1-tap replay), and Saved Artworks Gallery with live multi-color stroke preview
 */
@Composable
fun ToyBoxScreen(
    viewModel: WonderViewModel,
    progressList: List<ActivityProgressEntity>,
    savedArtworks: List<SavedArtworkEntity>
) {
    var shelfTab by remember { mutableStateOf(0) } // 0 = Toy Box Shortcuts, 1 = My Shelf, 2 = Saved Art Gallery
    val shortcuts = remember { WonderCatalogRegistry.getToyBoxShortcuts() }
    val favorites = remember(progressList) {
        progressList.filter { it.isFavorite }.mapNotNull { WonderCatalogRegistry.getActivityById(it.activityId) }
    }
    val recents = remember(progressList) {
        progressList.filter { it.lastPlayedEpochMs > 0L }
            .sortedByDescending { it.lastPlayedEpochMs }
            .take(8)
            .mapNotNull { WonderCatalogRegistry.getActivityById(it.activityId) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalmCoolBlueBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("toy_box_screen")
    ) {
        // Top Header Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PlaySurfaceWhite,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
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
                            .testTag("toy_box_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "🧸 Toy Box & My Shelf",
                        style = MaterialTheme.typography.headlineSmall,
                        color = DeepSlateInk
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        0 to "🎯 Favorites Box",
                        1 to "⭐ My Shelf (${favorites.size + recents.size})",
                        2 to "🖼️ Saved Art (${savedArtworks.size})"
                    ).forEach { (idx, label) ->
                        Button(
                            onClick = { shelfTab = idx },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (shelfTab == idx) ActionContinueBlue else SoftCreamCard,
                                contentColor = if (shelfTab == idx) PlaySurfaceWhite else DeepSlateInk
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("toy_box_tab_$idx")
                        ) {
                            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        when (shelfTab) {
            0 -> {
                // 8 Large Picture-First Toy Box Shortcuts (2x4 fixed grid)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (col in 0..3) {
                                val index = row * 4 + col
                                val activity = shortcuts.getOrNull(index)
                                if (activity != null) {
                                    ToyBoxShortcutTile(
                                        activity = activity,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .testTag("toybox_shortcut_${activity.id}"),
                                        onClick = { viewModel.navigateTo(WonderScreen.ActivityPlay(activity)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // My Shelf: Favorites & One-Tap Recent Replay
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "❤️ My Favorite Activities (Tap Heart inside any game to save here)",
                                style = MaterialTheme.typography.titleMedium,
                                color = DeepSlateInk
                            )
                            Spacer(Modifier.height(6.dp))
                            if (favorites.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "No favorites saved yet! Tap the ❤️ Heart inside any game to keep it on your shelf.",
                                        color = MutedSlateText
                                    )
                                }
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(favorites, key = { it.id }) { act ->
                                        ShelfActivityChip(act) {
                                            viewModel.navigateTo(WonderScreen.ActivityPlay(act))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "🔁 Recently Played • One-Tap Replay",
                                style = MaterialTheme.typography.titleMedium,
                                color = DeepSlateInk
                            )
                            Spacer(Modifier.height(6.dp))
                            if (recents.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Play any game and it will appear right here for instant one-tap replay!",
                                        color = MutedSlateText
                                    )
                                }
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(recents, key = { it.id }) { act ->
                                        ShelfActivityChip(act) {
                                            viewModel.navigateTo(WonderScreen.ActivityPlay(act))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                // Saved Art Gallery with Live Stroke Rendering & One-Tap Reopen
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaySurfaceWhite)
                ) {
                    if (savedArtworks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎨", fontSize = 44.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Your Art Gallery is waiting for your first painting!",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DeepSlateInk
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val sketchPad = WonderCatalogRegistry.getActivityById("w05_coloring_1")
                                        if (sketchPad != null) {
                                            viewModel.navigateTo(WonderScreen.ActivityPlay(sketchPad))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text("Open My Sketch Pad")
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(savedArtworks, key = { it.id }) { art ->
                                val decodedStrokes = remember(art.id, art.strokesPayload) {
                                    decodeStrokesPayload(art.strokesPayload)
                                }
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color(art.backgroundColorHex),
                                    border = BorderStroke(2.dp, LandCreativeViolet),
                                    modifier = Modifier
                                        .height(145.dp)
                                        .clickable { viewModel.openSavedArtwork(art) }
                                        .testTag("saved_artwork_card_${art.id}")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "🎨 ${art.title}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepSlateInk,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { viewModel.deleteArtwork(art.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Art", tint = MutedSlateText)
                                            }
                                        }

                                        // Mini Canvas rendering the actual saved strokes & colors
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .padding(vertical = 4.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFFFFBEB))
                                        ) {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val cw = size.width.coerceAtLeast(1f)
                                                val ch = size.height.coerceAtLeast(1f)
                                                decodedStrokes.forEach { stroke ->
                                                    if (stroke.points.size >= 2) {
                                                        val path = Path().apply {
                                                            moveTo(stroke.points.first().x * cw, stroke.points.first().y * ch)
                                                            for (i in 1 until stroke.points.size) {
                                                                lineTo(stroke.points[i].x * cw, stroke.points[i].y * ch)
                                                            }
                                                        }
                                                        drawPath(
                                                            path = path,
                                                            color = Color(stroke.colorHex),
                                                            style = Stroke(
                                                                width = (stroke.strokeWidth * 0.45f).coerceAtLeast(4f),
                                                                cap = StrokeCap.Round,
                                                                join = StrokeJoin.Round
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Text(
                                            text = "${decodedStrokes.size} strokes • ${decodedStrokes.map { it.colorHex }.distinct().size} colors • Tap to Edit",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ActionContinueBlue
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
private fun ToyBoxShortcutTile(
    activity: ActivityConfig,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        color = PlaySurfaceWhite,
        border = BorderStroke(3.dp, activity.landId.accentColor),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(activity.landId.accentColor.copy(alpha = 0.18f), PlaySurfaceWhite)
                    )
                )
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(activity.landId.accentColor.copy(alpha = 0.24f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = activity.emoji, fontSize = 26.sp)
            }
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
                color = DeepSlateInk,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                color = ActionPlayGreen,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "▶ Play Now",
                    color = PlaySurfaceWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ShelfActivityChip(
    activity: ActivityConfig,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(210.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = SoftCreamCard,
        border = BorderStroke(2.dp, activity.landId.accentColor),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = activity.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = DeepSlateInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Tap to Replay ▶",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActionPlayGreen
                )
            }
        }
    }
}

/**
 * Land Worlds Screen & Paginated World Activities Screen (up to 9 large activity tiles per page).
 */
@Composable
fun LandWorldsScreen(
    landId: LandId,
    viewModel: WonderViewModel,
    progressList: List<ActivityProgressEntity>
) {
    val worlds = remember(landId) { WonderCatalogRegistry.getWorldsForLand(landId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalmCoolBlueBg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("land_worlds_screen_${landId.id}")
    ) {
        // Header strip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PlaySurfaceWhite,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
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
                            .testTag("land_back_button")
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
                            .testTag("land_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "${landId.emoji} ${landId.title}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DeepSlateInk
                    )
                }

                Button(
                    onClick = {
                        viewModel.audio.speakInstruction("Welcome to ${landId.title}! Pick a world to explore.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = DeepSlateInk)
                    Spacer(Modifier.width(6.dp))
                    Text("Listen", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Worlds Grid
        val cols = if (worlds.size <= 4) 2 else 3
        val rows = (worlds.size + cols - 1) / cols
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (r in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (c in 0 until cols) {
                        val idx = r * cols + c
                        val world = worlds.getOrNull(idx)
                        if (world != null) {
                            val earnedStars = remember(progressList, world.id) {
                                progressList.filter { it.worldId == world.id }.sumOf { it.starsEarned }
                            }
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable {
                                        viewModel.navigateTo(WonderScreen.WorldActivities(world, pageIndex = 0))
                                    }
                                    .testTag("world_card_${world.id}"),
                                color = PlaySurfaceWhite,
                                border = BorderStroke(3.dp, world.accentColor),
                                shape = RoundedCornerShape(24.dp),
                                shadowElevation = 4.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(world.accentColor.copy(alpha = 0.18f), PlaySurfaceWhite)
                                            )
                                        )
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Surface(
                                            color = world.accentColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "⭐ $earnedStars",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DeepSlateInk,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(world.accentColor.copy(alpha = 0.24f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = world.emoji, fontSize = 32.sp)
                                    }
                                    Text(
                                        text = world.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepSlateInk,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * World Activities Screen:
 * Strictly displays up to 9 large activity tiles per page (3x3 landscape grid) with visible pagination controls.
 * Uses a compact top badge row + multi-line title layout so every card across all 37 catalog pages
 * has a non-zero, readable title height AND distinguishing glyph/range preview on both phone and tablet landscape.
 */
@Composable
fun WorldActivitiesScreen(
    world: WorldSpec,
    pageIndex: Int,
    viewModel: WonderViewModel,
    progressList: List<ActivityProgressEntity>
) {
    val allWorldActivities = remember(world.id) { WonderCatalogRegistry.getActivitiesForWorld(world.id) }
    val pageSize = 9
    val totalPages = ((allWorldActivities.size + pageSize - 1) / pageSize).coerceAtLeast(1)
    val safePage = pageIndex.coerceIn(0, totalPages - 1)
    val pageItems = remember(allWorldActivities, safePage) {
        allWorldActivities.drop(safePage * pageSize).take(pageSize)
    }
    val progressMap = remember(progressList) { progressList.associateBy { it.activityId } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalmCoolBlueBg)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("world_activities_screen_${world.id}")
    ) {
        // Top Header with visible pagination controls
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PlaySurfaceWhite,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SoftCreamCard)
                            .testTag("world_back_button")
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
                            .testTag("world_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = DeepSlateInk)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${world.emoji} ${world.title}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${allWorldActivities.size} Activities • Page ${safePage + 1} of $totalPages",
                            fontSize = 11.sp,
                            color = MutedSlateText,
                            maxLines = 1
                        )
                    }
                }

                // Visible Pagination Controls when totalPages > 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (totalPages > 1) {
                        Button(
                            onClick = { if (safePage > 0) viewModel.updateWorldPage(world, safePage - 1) },
                            enabled = safePage > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("world_prev_page_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Page", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Prev", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            for (p in 0 until totalPages) {
                                Box(
                                    modifier = Modifier
                                        .size(if (p == safePage) 24.dp else 12.dp, 12.dp)
                                        .clip(CircleShape)
                                        .background(if (p == safePage) world.accentColor else Color(0xFFCBD5E1))
                                        .clickable { viewModel.updateWorldPage(world, p) }
                                )
                            }
                        }

                        Button(
                            onClick = { if (safePage < totalPages - 1) viewModel.updateWorldPage(world, safePage + 1) },
                            enabled = safePage < totalPages - 1,
                            colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("world_next_page_button")
                        ) {
                            Text("Next", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Page", modifier = Modifier.size(18.dp))
                        }
                    }

                    Button(
                        onClick = { viewModel.audio.speakInstruction("${world.title}. Pick any activity tile to play!") },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = DeepSlateInk)
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // Fixed 3x3 Landscape Grid of up to 9 Large Activity Tiles per Page
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..2) {
                        val idx = row * 3 + col
                        val activity = pageItems.getOrNull(idx)
                        if (activity != null) {
                            val progress = progressMap[activity.id]
                            val stars = progress?.starsEarned ?: 0
                            val isFav = progress?.isFavorite == true
                            val showGlyphBadge = activity.engineType == EngineType.TRACING_STUDIO ||
                                activity.id == "w07_phonics_11" ||
                                activity.id == "w07_phonics_12"

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable {
                                        viewModel.navigateTo(WonderScreen.ActivityPlay(activity))
                                    }
                                    .testTag("activity_tile_${activity.id}"),
                                color = PlaySurfaceWhite,
                                border = BorderStroke(2.5.dp, world.accentColor),
                                shape = RoundedCornerShape(18.dp),
                                shadowElevation = 3.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Top Row: Emoji Circle + Index/Glyph Badge on left, Favorite + Stars on right
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(world.accentColor.copy(alpha = 0.22f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = activity.emoji, fontSize = 18.sp)
                                            }
                                            Text(
                                                text = "#${activity.indexInWorld}",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = MutedSlateText
                                            )
                                            if (showGlyphBadge) {
                                                Surface(
                                                    color = world.accentColor.copy(alpha = 0.18f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.testTag("activity_glyph_${activity.id}")
                                                ) {
                                                    Text(
                                                        text = activity.traceGlyph,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 11.sp,
                                                        color = DeepSlateInk,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isFav) {
                                                Icon(
                                                    Icons.Default.Favorite,
                                                    contentDescription = "Favorite",
                                                    tint = ActionFavoritePink,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(Modifier.width(3.dp))
                                            }
                                            Text(
                                                text = "⭐ $stars",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = DeepSlateInk
                                            )
                                        }
                                    }

                                    // Bottom Area: Dedicated multi-line readable title (Guaranteed non-zero height on phone & tablet)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = activity.title,
                                            fontSize = 13.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepSlateInk,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("activity_title_${activity.id}")
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
