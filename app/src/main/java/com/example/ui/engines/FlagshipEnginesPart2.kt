package com.example.ui.engines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AdaptiveChallengeParameters
import com.example.data.FamilyMemberEntity
import com.example.model.ActivityConfig
import com.example.model.GameItemOption
import com.example.model.StoryPageSpec
import com.example.ui.theme.ActionContinueBlue
import com.example.ui.theme.ActionListenAmber
import com.example.ui.theme.ActionPlayGreen
import com.example.ui.theme.ActionRetryOrange
import com.example.ui.theme.DeepSlateInk
import com.example.ui.theme.MutedSlateText
import com.example.ui.theme.PlaySurfaceWhite
import com.example.ui.theme.SoftCreamCard
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel
import java.io.File

/**
 * ENGINE 5: Flagship Animal Sounds & Safari / Vehicle Sound Detective Engine
 * - Uses adaptive.effectiveChoiceCount to present 2, 3, or 4 large cards
 * - Highlights the target card gently if adaptive.showVisualScaffoldGlow is active
 */
@Composable
fun AnimalSafariSoundsEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val allItems = remember(activity.id) {
        activity.items.ifEmpty {
            listOf(
                GameItemOption("a1", "Savanna Lion", "🦁", "Savanna", "Roar! Savanna Lion!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("a2", "Ocean Dolphin", "🐬", "Ocean", "Splash! Ocean Dolphin!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("a3", "Gentle Elephant", "🐘", "Savanna", "Pawoo! Gentle Elephant!", primaryColorHex = 0xFF64748B),
                GameItemOption("a4", "Woodland Owl", "🦉", "Forest", "Hoo-hoo! Woodland Owl!", primaryColorHex = 0xFF6366F1)
            )
        }
    }
    var roundIdx by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var hadRetries by remember(roundIdx, replayKey) { mutableStateOf(false) }

    val choices = remember(allItems, roundIdx, adaptive.effectiveChoiceCount) {
        val count = adaptive.effectiveChoiceCount.coerceIn(2, allItems.size)
        val rotated = allItems.drop(roundIdx % allItems.size) + allItems.take(roundIdx % allItems.size)
        rotated.take(count)
    }
    val target = choices.first()
    var promptMessage by remember(roundIdx, replayKey) {
        mutableStateOf("Listen and tap: Where is the ${target.label}?")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_animal_safari"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = promptMessage,
                style = MaterialTheme.typography.titleLarge,
                color = DeepSlateInk
            )
            Button(
                onClick = {
                    viewModel.audio.playAnimalOrObjectSignature(
                        baseFreqHz = 320f,
                        label = target.label,
                        spokenFact = target.spokenText
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("animal_hear_call_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = DeepSlateInk)
                Spacer(Modifier.width(6.dp))
                Text("Hear ${target.label} Call", color = DeepSlateInk, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            choices.forEachIndexed { idx, option ->
                val isTarget = option.id == target.id
                val glow = isTarget && adaptive.showVisualScaffoldGlow

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            if (isTarget) {
                                promptMessage = "🎉 You found the ${option.label}! ${option.spokenText}"
                                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = hadRetries)
                                roundIdx += 1
                            } else {
                                hadRetries = true
                                promptMessage = "That's the ${option.label}! Let's look for the ${target.label}."
                                viewModel.onGentleRetryNeeded(activity, "That's ${option.label}. Try tapping ${target.label}!")
                            }
                        }
                        .testTag("animal_choice_$idx"),
                    color = if (glow) Color(option.primaryColorHex).copy(alpha = 0.22f) else SoftCreamCard,
                    border = BorderStroke(
                        width = if (glow) 4.dp else 2.5.dp,
                        color = Color(option.primaryColorHex)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (option.drawableRes != null) {
                            Image(
                                painter = painterResource(id = option.drawableRes),
                                contentDescription = option.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = option.emoji, fontSize = 64.sp)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.titleLarge,
                            color = DeepSlateInk
                        )
                        Text(
                            text = option.categoryOrTarget,
                            fontSize = 12.sp,
                            color = MutedSlateText
                        )
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 6: Flagship Coloring Studio & My Sketch Pad
 * - Full canvas with 8 bright colors, 3 brush sizes, eraser, undo, clear, and Save to Local Art Gallery
 */
data class DrawnStroke(
    val points: List<Offset>,
    val colorHex: Long,
    val strokeWidth: Float
)

@Composable
fun ColoringCanvasEngine(
    activity: ActivityConfig,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val paletteColors = listOf(
        0xFFEF4444L, 0xFFF97316L, 0xFFEAB308L, 0xFF10B981L,
        0xFF06B6D4L, 0xFF3B82F6L, 0xFF8B5CF6L, 0xFFEC4899L
    )
    var activeColorHex by remember { mutableLongStateOf(paletteColors.first()) }
    var activeBrushWidth by remember { mutableFloatStateOf(22f) }
    var isEraser by remember { mutableStateOf(false) }

    val strokes = remember(activity.id, replayKey) { mutableStateListOf<DrawnStroke>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_coloring_canvas"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left: Color Palette & Brush Size Bar
        Column(
            modifier = Modifier
                .width(116.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 8 Colors (2x4 grid)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (r in 0..3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (c in 0..1) {
                            val hex = paletteColors[r * 2 + c]
                            val selected = !isEraser && activeColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .clickable {
                                        isEraser = false
                                        activeColorHex = hex
                                    }
                                    .padding(if (selected) 4.dp else 0.dp)
                            )
                        }
                    }
                }
            }

            // Brush Sizes (Small, Medium, Giant) + Eraser
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(12f to "S", 24f to "M", 38f to "L").forEach { (w, lbl) ->
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                isEraser = false
                                activeBrushWidth = w
                            },
                        color = if (!isEraser && activeBrushWidth == w) ActionContinueBlue else SoftCreamCard
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = lbl,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isEraser && activeBrushWidth == w) PlaySurfaceWhite else DeepSlateInk
                            )
                        }
                    }
                }
            }

            // Eraser, Undo, Clear, Save Art
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { isEraser = !isEraser },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEraser) ActionListenAmber else SoftCreamCard,
                        contentColor = DeepSlateInk
                    ),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text(if (isEraser) "Eraser ON" else "🧽 Eraser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCreamCard, contentColor = DeepSlateInk),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("coloring_undo_button")
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                    }
                    Button(
                        onClick = { strokes.clear() },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCreamCard, contentColor = DeepSlateInk),
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("coloring_clear_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    }
                }

                Button(
                    onClick = {
                        val payload = strokes.joinToString(";") { "${it.colorHex}:${it.points.size}" }
                            .ifEmpty { "sketch_star" }
                        viewModel.saveChildArtwork(
                            title = activity.title,
                            templateName = activity.traceWordHint,
                            strokesPayload = payload,
                            bgHex = 0xFFFFFBEBL
                        )
                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("coloring_save_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save Art", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Save Art", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Right: Full-Screen Drawing & Coloring Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFFFFBEB))
                .pointerInput(isEraser, activeColorHex, activeBrushWidth) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints.clear()
                            currentPoints.add(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints.add(change.position)
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                strokes.add(
                                    DrawnStroke(
                                        points = currentPoints.toList(),
                                        colorHex = if (isEraser) 0xFFFFFBEBL else activeColorHex,
                                        strokeWidth = if (isEraser) 44f else activeBrushWidth
                                    )
                                )
                                currentPoints.clear()
                            }
                        }
                    )
                }
                .pointerInput(isEraser, activeColorHex, activeBrushWidth) {
                    detectTapGestures { tapOffset ->
                        strokes.add(
                            DrawnStroke(
                                points = listOf(tapOffset, Offset(tapOffset.x + 2f, tapOffset.y + 2f)),
                                colorHex = if (isEraser) 0xFFFFFBEBL else activeColorHex,
                                strokeWidth = if (isEraser) 44f else activeBrushWidth * 1.4f
                            )
                        )
                    }
                }
                .testTag("drawing_surface_canvas")
        ) {
            // Subtle background template silhouette for themed coloring pages
            Text(
                text = activity.emoji,
                fontSize = 150.sp,
                color = Color.Black.copy(alpha = 0.08f),
                modifier = Modifier.align(Alignment.Center)
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                strokes.forEach { stroke ->
                    if (stroke.points.size >= 2) {
                        val path = Path().apply {
                            moveTo(stroke.points.first().x, stroke.points.first().y)
                            for (i in 1 until stroke.points.size) {
                                lineTo(stroke.points[i].x, stroke.points[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(stroke.colorHex),
                            style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
                if (currentPoints.size >= 2) {
                    val livePath = Path().apply {
                        moveTo(currentPoints.first().x, currentPoints.first().y)
                        for (i in 1 until currentPoints.size) {
                            lineTo(currentPoints[i].x, currentPoints[i].y)
                        }
                    }
                    drawPath(
                        path = livePath,
                        color = if (isEraser) Color(0xFFFFFBEB) else Color(activeColorHex),
                        style = Stroke(width = if (isEraser) 44f else activeBrushWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }
    }
}

/**
 * ENGINE 7: Flagship Music & Rhythm Studio (Piano, Xylophone, Drums & Echo Beats)
 * - Supports large keys, slide-between-notes, distinct xylophone timbre, and drum pads
 */
@Composable
fun MusicInstrumentsEngine(
    activity: ActivityConfig,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    var instrumentMode by remember(activity.id, replayKey) {
        mutableIntStateOf(
            when {
                activity.title.contains("Xylophone", ignoreCase = true) -> 1
                activity.title.contains("Drum", ignoreCase = true) -> 2
                else -> 0
            }
        )
    }
    var notesPlayed by remember(activity.id, replayKey) { mutableIntStateOf(0) }

    val scaleNotes = listOf(
        Triple("C", 261.63f, Color(0xFFEF4444)),
        Triple("D", 293.66f, Color(0xFFF97316)),
        Triple("E", 329.63f, Color(0xFFEAB308)),
        Triple("F", 349.23f, Color(0xFF10B981)),
        Triple("G", 392.00f, Color(0xFF06B6D4)),
        Triple("A", 440.00f, Color(0xFF3B82F6)),
        Triple("B", 493.88f, Color(0xFF8B5CF6)),
        Triple("C5", 523.25f, Color(0xFFEC4899))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_music_instruments"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Instrument Switcher Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0 to "🎹 Grand Piano", 1 to "🎼 Xylophone", 2 to "🥁 Rhythm Drums").forEach { (idx, label) ->
                    Button(
                        onClick = { instrumentMode = idx },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (instrumentMode == idx) ActionContinueBlue else SoftCreamCard,
                            contentColor = if (instrumentMode == idx) PlaySurfaceWhite else DeepSlateInk
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(label, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = {
                    // Play Echo Melody Demo
                    viewModel.audio.playSuccessChime()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🎵 Play Echo Tune", color = DeepSlateInk, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(8.dp))

        if (instrumentMode == 2) {
            // 4 Large Jungle Drum Pads
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Triple("Bass Boom", 110f, Color(0xFFEF4444)),
                    Triple("Snare Snap", 220f, Color(0xFFF59E0B)),
                    Triple("Bongo High", 340f, Color(0xFF10B981)),
                    Triple("Star Chime", 660f, Color(0xFF8B5CF6))
                ).forEachIndexed { idx, (name, freq, col) ->
                    Surface(
                        modifier = Modifier
                            .size(145.dp)
                            .clip(CircleShape)
                            .clickable {
                                notesPlayed += 1
                                viewModel.audio.playNoteHz(freq, 220, isDrum = idx < 3, isXylophone = idx == 3)
                                if (notesPlayed % 8 == 0) {
                                    viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                                }
                            }
                            .testTag("drum_pad_$idx"),
                        color = col.copy(alpha = 0.25f),
                        border = BorderStroke(5.dp, col),
                        shape = CircleShape,
                        shadowElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("🥁", fontSize = 40.sp)
                            Text(name, style = MaterialTheme.typography.titleMedium, color = DeepSlateInk)
                        }
                    }
                }
            }
        } else {
            // 8 Large Responsive Piano or Xylophone Bars with generous hit areas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                scaleNotes.forEachIndexed { idx, (noteName, freq, color) ->
                    val heightFraction = if (instrumentMode == 1) (1f - idx * 0.055f) else 0.96f
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(heightFraction)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                notesPlayed += 1
                                viewModel.audio.playNoteHz(
                                    freqHz = freq,
                                    durationMs = 360,
                                    isXylophone = instrumentMode == 1
                                )
                                if (notesPlayed % 8 == 0) {
                                    viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                                }
                            }
                            .testTag("music_key_$noteName"),
                        color = color,
                        border = BorderStroke(3.dp, PlaySurfaceWhite),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 5.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = noteName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = PlaySurfaceWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 8: Memory Match Engine (Age-Adjusted Grid Complexity)
 */
@Composable
fun MemoryMatchEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val pairCount = adaptive.memoryPairCount.coerceIn(2, 6)
    val baseItems = remember(activity.id) {
        activity.items.ifEmpty {
            listOf(
                GameItemOption("m1", "Lion", "🦁", "1", "Lion!"),
                GameItemOption("m2", "Dolphin", "🐬", "2", "Dolphin!"),
                GameItemOption("m3", "Apple", "🍎", "3", "Apple!"),
                GameItemOption("m4", "Train", "🚂", "4", "Train!"),
                GameItemOption("m5", "Star", "⭐", "5", "Star!"),
                GameItemOption("m6", "Butterfly", "🦋", "6", "Butterfly!")
            )
        }
    }
    val deck = remember(activity.id, pairCount, replayKey) {
        val chosen = baseItems.take(pairCount)
        (chosen + chosen).shuffled()
    }

    val matchedIndices = remember(activity.id, pairCount, replayKey) { mutableStateListOf<Int>() }
    val flippedIndices = remember(activity.id, pairCount, replayKey) { mutableStateListOf<Int>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_memory_match"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Matched ${matchedIndices.size / 2} of $pairCount pairs! Tap two cards to flip them.",
            style = MaterialTheme.typography.titleMedium,
            color = DeepSlateInk
        )

        val cols = if (deck.size <= 6) 3 else 4
        val rows = (deck.size + cols - 1) / cols
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        val card = deck.getOrNull(idx)
                        if (card != null) {
                            val isOpen = idx in matchedIndices || idx in flippedIndices
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (!isOpen && flippedIndices.size < 2) {
                                            flippedIndices.add(idx)
                                            viewModel.audio.speakInstruction(card.label)
                                            if (flippedIndices.size == 2) {
                                                val firstIdx = flippedIndices[0]
                                                val secondIdx = flippedIndices[1]
                                                if (deck[firstIdx].id == deck[secondIdx].id) {
                                                    matchedIndices.add(firstIdx)
                                                    matchedIndices.add(secondIdx)
                                                    flippedIndices.clear()
                                                    if (matchedIndices.size == deck.size) {
                                                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                                                    }
                                                }
                                            }
                                        } else if (flippedIndices.size == 2) {
                                            flippedIndices.clear()
                                            flippedIndices.add(idx)
                                        }
                                    }
                                    .testTag("memory_card_$idx"),
                                color = if (isOpen) PlaySurfaceWhite else ActionContinueBlue,
                                border = BorderStroke(3.dp, if (idx in matchedIndices) ActionPlayGreen else ActionContinueBlue),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isOpen) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = card.emoji, fontSize = 36.sp)
                                            Text(text = card.label, fontWeight = FontWeight.Bold, color = DeepSlateInk)
                                        }
                                    } else {
                                        Text(text = "🌟", fontSize = 34.sp)
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

/**
 * ENGINE 9: Flagship Alphabet & Phonics Quiz Engine
 * - Shows real letters in large answer tiles
 * - Explicitly distinguishes Letter Names from Phonetic Sounds
 */
@Composable
fun PhonicsWordQuizEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    savedStepIndex: Int,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val pool = remember(activity.id) {
        activity.items.ifEmpty {
            listOf(
                GameItemOption("p1", "A", "🍎", "Apple", "Letter A... aah, Apple.", phoneticCue = "aah"),
                GameItemOption("p2", "B", "🐻", "Bear", "Letter B... b, Bear.", phoneticCue = "b"),
                GameItemOption("p3", "C", "🐱", "Cat", "Letter C... k, Cat.", phoneticCue = "k"),
                GameItemOption("p4", "D", "🐬", "Dolphin", "Letter D... d, Dolphin.", phoneticCue = "d")
            )
        }
    }
    var stepIdx by remember(activity.id, replayKey) { mutableIntStateOf(savedStepIndex) }
    var speakPureSound by remember { mutableStateOf(true) }
    var hadRetries by remember(stepIdx, replayKey) { mutableStateOf(false) }

    val choices = remember(pool, stepIdx, adaptive.effectiveChoiceCount) {
        val count = adaptive.effectiveChoiceCount.coerceIn(2, pool.size)
        val rotated = pool.drop(stepIdx % pool.size) + pool.take(stepIdx % pool.size)
        rotated.take(count)
    }
    val target = choices.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_phonics_quiz"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tap the tile for: ${target.label} (${target.categoryOrTarget}) ${target.emoji}",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepSlateInk
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        speakPureSound = false
                        val ch = target.label.firstOrNull() ?: 'A'
                        viewModel.audio.speakPhonicsLetter(ch, speakSoundNotName = false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🔤 Letter Name", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        speakPureSound = true
                        val ch = target.label.firstOrNull() ?: 'A'
                        viewModel.audio.speakPhonicsLetter(ch, speakSoundNotName = true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🗣️ Phonics Sound", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Large Real Letter Answer Tiles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            choices.forEachIndexed { idx, item ->
                val isTarget = item.id == target.id
                val glow = isTarget && adaptive.showVisualScaffoldGlow

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(26.dp))
                        .clickable {
                            if (isTarget) {
                                viewModel.onActivityMilestoneCompleted(
                                    activity = activity,
                                    hadRetriesInRound = hadRetries,
                                    nextStepIndex = stepIdx + 1
                                )
                                stepIdx += 1
                            } else {
                                hadRetries = true
                                viewModel.onGentleRetryNeeded(activity, "That is ${item.label}. Try finding ${target.label}!")
                            }
                        }
                        .testTag("phonics_tile_$idx"),
                    color = if (glow) ActionPlayGreen.copy(alpha = 0.18f) else SoftCreamCard,
                    border = BorderStroke(if (glow) 4.dp else 3.dp, Color(item.primaryColorHex)),
                    shape = RoundedCornerShape(26.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 62.sp,
                            color = DeepSlateInk
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(text = "${item.emoji} ${item.categoryOrTarget}", style = MaterialTheme.typography.titleMedium, color = MutedSlateText)
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 10: Numbers & Math Counter Engine (One-by-One Counting & Addition)
 */
@Composable
fun NumbersMathCounterEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    savedStepIndex: Int,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    var targetCount by remember(activity.id, replayKey) {
        mutableIntStateOf(((savedStepIndex % 6) + 2).coerceIn(2, 8))
    }
    val tappedIndices = remember(targetCount, replayKey) { mutableStateListOf<Int>() }
    val itemSpec = activity.items.firstOrNull() ?: GameItemOption("num_star", "Stars", "⭐", "3", "Count the stars!")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_numbers_math"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Tap each ${itemSpec.emoji} one by one! Counted: ${tappedIndices.size} of $targetCount",
            style = MaterialTheme.typography.headlineMedium,
            color = DeepSlateInk
        )

        // Interactive Counting Objects Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..targetCount) {
                val counted = i in tappedIndices
                Surface(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (!counted) {
                                tappedIndices.add(i)
                                viewModel.audio.playNoteHz(330f + tappedIndices.size * 45f, 180, isXylophone = true)
                                viewModel.audio.speakInstruction("${tappedIndices.size}!")
                            }
                        }
                        .testTag("math_object_$i"),
                    color = if (counted) ActionPlayGreen.copy(alpha = 0.24f) else SoftCreamCard,
                    border = BorderStroke(3.dp, if (counted) ActionPlayGreen else ActionContinueBlue),
                    shape = CircleShape
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = itemSpec.emoji, fontSize = 36.sp)
                        if (counted) {
                            Text(text = "${tappedIndices.indexOf(i) + 1}", fontWeight = FontWeight.Bold, color = DeepSlateInk)
                        }
                    }
                }
            }
        }

        // Answer Number Tiles
        val options = remember(targetCount, adaptive.effectiveChoiceCount) {
            listOf(targetCount, (targetCount % 8) + 1, ((targetCount + 2) % 9) + 1)
                .distinct()
                .take(adaptive.effectiveChoiceCount.coerceAtLeast(2))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEach { num ->
                Button(
                    onClick = {
                        if (num == targetCount) {
                            viewModel.onActivityMilestoneCompleted(
                                activity = activity,
                                hadRetriesInRound = false,
                                nextStepIndex = targetCount
                            )
                            targetCount = (targetCount % 7) + 2
                        } else {
                            viewModel.onGentleRetryNeeded(activity, "Let's count again together—there are $targetCount!")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("math_answer_$num")
                ) {
                    Text(text = "$num", style = MaterialTheme.typography.displayMedium, color = PlaySurfaceWhite)
                }
            }
        }
    }
}

/**
 * ENGINE 11: 3-Page Narrated Storybook & Safety Story Engine (including Knock Knock! Door Safety)
 */
@Composable
fun StorybookReaderEngine(
    activity: ActivityConfig,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val pages = remember(activity.id) {
        activity.storyPages.ifEmpty {
            listOf(
                StoryPageSpec(1, "Page 1", "📖 🌟", activity.instructionText, "Ready to turn the page?", listOf("Next Page!"))
            )
        }
    }
    var pageIdx by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    val currentPage = pages[pageIdx.coerceIn(0, pages.lastIndex)]

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_storybook_reader"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left: Illustrated Story Scene Card
        Surface(
            modifier = Modifier
                .weight(0.46f)
                .fillMaxHeight(),
            color = SoftCreamCard,
            border = BorderStroke(3.dp, activity.landId.accentColor),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Page ${currentPage.pageNumber} of ${pages.size}",
                    fontWeight = FontWeight.Bold,
                    color = MutedSlateText
                )
                Text(text = currentPage.emojiScene, fontSize = 68.sp, textAlign = TextAlign.Center)
                if (currentPage.parentTip.isNotEmpty()) {
                    Surface(
                        color = ActionListenAmber.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "💬 Grown-Up Prompt: ${currentPage.parentTip}",
                            fontSize = 11.sp,
                            color = DeepSlateInk,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Right: Narration Text, Interactive Choice & Page Turning
        Column(
            modifier = Modifier
                .weight(0.54f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = currentPage.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeepSlateInk
                )
                Text(
                    text = currentPage.narrationText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DeepSlateInk
                )
                Button(
                    onClick = {
                        viewModel.audio.speakStoryPage(
                            narrationText = currentPage.narrationText,
                            interactivePrompt = currentPage.interactivePrompt
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = DeepSlateInk)
                    Spacer(Modifier.width(6.dp))
                    Text("Read Page Aloud", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }
            }

            // Interactive Story Choice Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = currentPage.interactivePrompt,
                    fontWeight = FontWeight.Bold,
                    color = ActionContinueBlue
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentPage.choiceOptions.forEachIndexed { idx, choiceLabel ->
                        Button(
                            onClick = {
                                viewModel.audio.speakInstruction(choiceLabel)
                                if (pageIdx < pages.lastIndex) {
                                    pageIdx += 1
                                } else {
                                    viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                                    pageIdx = 0
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (idx == 0) ActionPlayGreen else ActionContinueBlue
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("story_choice_$idx")
                        ) {
                            Text(choiceLabel, color = PlaySurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 12: Early Coding Arrow Sequence Maze Engine
 */
@Composable
fun CodingArrowMazeEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    var botX by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var botY by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    val goalX = 2
    val goalY = 1
    val queuedArrows = remember(activity.id, replayKey) { mutableStateListOf<String>() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_coding_maze"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left: 3x2 Visual Grid Maze
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (r in 0..1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (c in 0..2) {
                        val isBot = botX == c && botY == r
                        val isGoal = goalX == c && goalY == r
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            color = when {
                                isBot -> ActionPlayGreen.copy(alpha = 0.25f)
                                isGoal -> ActionListenAmber.copy(alpha = 0.25f)
                                else -> SoftCreamCard
                            },
                            border = BorderStroke(2.dp, if (isBot) ActionPlayGreen else Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = when {
                                        isBot && isGoal -> "🤖🌟"
                                        isBot -> "🤖"
                                        isGoal -> "🌟 Goal"
                                        else -> "🟩"
                                    },
                                    fontSize = 32.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right: Arrow Pad & Run Code
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tap arrows to move Beep-Bot 🤖 to the Goal Star 🌟!",
                style = MaterialTheme.typography.titleMedium,
                color = DeepSlateInk
            )

            Text(
                text = "Steps: ${queuedArrows.joinToString(" ") { it }.ifEmpty { "(Tap arrows below)" }}",
                fontWeight = FontWeight.Bold,
                color = ActionContinueBlue
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("⬅️", -1, 0),
                    Triple("➡️", 1, 0),
                    Triple("⬆️", 0, -1),
                    Triple("⬇️", 0, 1)
                ).forEachIndexed { idx, (arrow, dx, dy) ->
                    Button(
                        onClick = {
                            queuedArrows.add(arrow)
                            botX = (botX + dx).coerceIn(0, 2)
                            botY = (botY + dy).coerceIn(0, 1)
                            viewModel.audio.playNoteHz(440f + idx * 50f, 140, isXylophone = true)
                            if (botX == goalX && botY == goalY) {
                                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("coding_arrow_$idx")
                    ) {
                        Text(arrow, fontSize = 22.sp)
                    }
                }
            }

            Button(
                onClick = {
                    botX = 0
                    botY = 0
                    queuedArrows.clear()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionRetryOrange),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Text("Reset Robot to Start", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * ENGINE 13: Flagship Pretend Play & Everyday Care Engine (Puppy Wash & Sparkle Toothbrushing)
 * - Uses the required 3-stage progression:
 *   Stage 0: Demonstrate -> Stage 1: Guided Practice -> Stage 2: Independent Try
 */
@Composable
fun PretendStepCareEngine(
    activity: ActivityConfig,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val isToothbrush = activity.title.contains("Tooth", ignoreCase = true)
    val steps = remember(isToothbrush) {
        if (isToothbrush) {
            listOf(
                Triple("Step 1: Demonstrate", "🪥💧", "Watch how we put pea-sized toothpaste on the brush and swirl gently!"),
                Triple("Step 2: Guided Practice", "🦷🫧", "Rub your finger across the teeth to make sparkling minty foam!"),
                Triple("Step 3: Independent Try", "😁✨", "Rinse with water and show your super-clean, healthy smile!")
            )
        } else {
            listOf(
                Triple("Step 1: Demonstrate", "🐶🛁", "First watch how we brush away leaves and add warm bubbly soap!"),
                Triple("Step 2: Guided Practice", "🐶🫧", "Scrub your finger across the puppy to wash with soapy bubbles!"),
                Triple("Step 3: Independent Try", "🐶🚿✨", "Rinse the bubbles with the warm shower and dry with a fluffy towel!")
            )
        }
    }

    var currentStage by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var scrubProgress by remember(currentStage, replayKey) { mutableIntStateOf(0) }
    val (stageTitle, stageEmoji, stagePrompt) = steps[currentStage.coerceIn(0, steps.lastIndex)]

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_pretend_care"),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left: Interactive Scrub / Rinse Stage
        Surface(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .clickable {
                    scrubProgress += 1
                    viewModel.audio.playBubblePopSound(1.0f + scrubProgress * 0.08f)
                    if (scrubProgress >= 3) {
                        if (currentStage < steps.lastIndex) {
                            currentStage += 1
                        } else {
                            viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                            currentStage = 0
                        }
                    }
                }
                .testTag("pretend_interactive_stage"),
            color = SoftCreamCard,
            border = BorderStroke(3.dp, ActionPlayGreen),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = stageTitle, style = MaterialTheme.typography.titleLarge, color = ActionContinueBlue)
                Text(text = stageEmoji, fontSize = 84.sp)
                Text(
                    text = "Tap or rub here (${scrubProgress}/3) to complete this step!",
                    fontWeight = FontWeight.Bold,
                    color = DeepSlateInk
                )
            }
        }

        // Right: Demonstrate -> Guided Practice -> Independent Try Progression Cards
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stagePrompt,
                style = MaterialTheme.typography.titleMedium,
                color = DeepSlateInk
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEachIndexed { idx, (label, emoji, _) ->
                    val active = idx == currentStage
                    val done = idx < currentStage
                    Surface(
                        color = when {
                            active -> ActionContinueBlue.copy(alpha = 0.16f)
                            done -> ActionPlayGreen.copy(alpha = 0.16f)
                            else -> SoftCreamCard
                        },
                        border = BorderStroke(
                            width = if (active) 2.5.dp else 1.dp,
                            color = if (active) ActionContinueBlue else if (done) ActionPlayGreen else Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { currentStage = idx }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (done) "✅" else emoji, fontSize = 22.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(text = label, fontWeight = FontWeight.Bold, color = DeepSlateInk)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (currentStage < steps.lastIndex) {
                        currentStage += 1
                    } else {
                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                        currentStage = 0
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("pretend_next_step_button")
            ) {
                Text(
                    text = if (currentStage < steps.lastIndex) "Next Care Step ▶" else "Finish & Celebrate! 🌟",
                    color = PlaySurfaceWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * ENGINE 14: Wonder Flashcards (16 Decks) & My Family Album (Meet My Family + Who's Who? Quiz)
 */
@Composable
fun FlashcardAndFamilyEngine(
    activity: ActivityConfig,
    familyMembers: List<FamilyMemberEntity>,
    adaptive: AdaptiveChallengeParameters,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val isFamilyWorld = activity.worldId == "w26_family"
    val isFamilyQuiz = activity.id == "w26_family_2"

    if (isFamilyWorld && familyMembers.size < 2 && isFamilyQuiz) {
        // Friendly empty state requiring at least 2 entries before starting the quiz
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("family_quiz_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("👨‍👩‍👧 📸", fontSize = 56.sp)
                Text(
                    text = "Add at least 2 Family Photos to play Who's Who!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeepSlateInk
                )
                Text(
                    text = "Ask a grown-up to open Parent Corner → Family Album to add up to 16 private family photo cards.",
                    color = MutedSlateText,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.navigateTo(WonderScreen.ParentCorner(unlocked = false)) },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Open Parent Corner to Add Photos")
                }
            }
        }
        return
    }

    val cards = remember(activity.id, familyMembers) {
        if (isFamilyWorld && familyMembers.isNotEmpty()) {
            familyMembers.map { m ->
                GameItemOption(
                    id = "fam_${m.id}",
                    label = m.name,
                    emoji = m.avatarEmoji,
                    categoryOrTarget = m.relationshipRole,
                    spokenText = "${m.name}, your ${m.relationshipRole}!",
                    explanation = m.localPhotoPath ?: ""
                )
            }
        } else {
            activity.items.ifEmpty {
                listOf(
                    GameItemOption("fam_def1", "Mama Bear", "🐻", "Mom", "This is loving Mama Bear!"),
                    GameItemOption("fam_def2", "Papa Bear", "🐻", "Dad", "This is caring Papa Bear!"),
                    GameItemOption("fam_def3", "Grandma Owl", "🦉", "Grandma", "This is wise Grandma Owl!")
                )
            }
        }
    }

    var cardIdx by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var quizMode by remember(activity.id) { mutableStateOf(isFamilyQuiz) }
    val currentCard = cards[cardIdx % cards.size]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_flashcard_family"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (quizMode) "Who's Who Quiz: Tap ${currentCard.label}!" else "Card ${(cardIdx % cards.size) + 1} of ${cards.size}: ${currentCard.label}",
                style = MaterialTheme.typography.headlineMedium,
                color = DeepSlateInk
            )
            if (cards.size >= 2) {
                Button(
                    onClick = { quizMode = !quizMode },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (quizMode) "📖 Browse Cards" else "🎯 Try Quiz", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (!quizMode) {
            // Large Browse Card with Hear Name, Previous, and Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { cardIdx = if (cardIdx > 0) cardIdx - 1 else cards.lastIndex },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCreamCard, contentColor = DeepSlateInk),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(26.dp))
                        .clickable {
                            viewModel.audio.speakInstruction(currentCard.spokenText)
                        },
                    color = SoftCreamCard,
                    border = BorderStroke(3.dp, Color(currentCard.primaryColorHex)),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (currentCard.explanation.isNotEmpty() && File(currentCard.explanation).exists()) {
                            AsyncImage(
                                model = File(currentCard.explanation),
                                contentDescription = currentCard.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(22.dp))
                            )
                        } else if (currentCard.drawableRes != null) {
                            Image(
                                painter = painterResource(id = currentCard.drawableRes),
                                contentDescription = currentCard.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(22.dp))
                            )
                        } else {
                            Text(text = currentCard.emoji, fontSize = 96.sp)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = currentCard.label,
                                style = MaterialTheme.typography.displayMedium,
                                color = DeepSlateInk
                            )
                            Text(
                                text = currentCard.categoryOrTarget,
                                style = MaterialTheme.typography.titleMedium,
                                color = ActionContinueBlue
                            )
                            Text(
                                text = currentCard.spokenText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MutedSlateText
                            )
                            Button(
                                onClick = { viewModel.audio.speakInstruction(currentCard.spokenText) },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = DeepSlateInk)
                                Spacer(Modifier.width(6.dp))
                                Text("Hear Name", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        cardIdx = (cardIdx + 1) % cards.size
                        if (cardIdx == 0) {
                            viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = PlaySurfaceWhite)
                }
            }
        } else {
            // 2 or 3 Face/Card Quiz
            val quizChoices = remember(cards, cardIdx, adaptive.effectiveChoiceCount) {
                val count = adaptive.effectiveChoiceCount.coerceIn(2, minOf(3, cards.size))
                val rotated = cards.drop(cardIdx % cards.size) + cards.take(cardIdx % cards.size)
                rotated.take(count)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                quizChoices.forEach { option ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .clickable {
                                if (option.id == currentCard.id) {
                                    viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                                    cardIdx = (cardIdx + 1) % cards.size
                                } else {
                                    viewModel.onGentleRetryNeeded(activity, "That's ${option.label}! Try finding ${currentCard.label}.")
                                }
                            },
                        color = SoftCreamCard,
                        border = BorderStroke(3.dp, ActionContinueBlue),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = option.emoji, fontSize = 68.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(text = option.label, style = MaterialTheme.typography.headlineMedium, color = DeepSlateInk)
                            Text(text = option.categoryOrTarget, fontSize = 13.sp, color = MutedSlateText)
                        }
                    }
                }
            }
        }
    }
}
