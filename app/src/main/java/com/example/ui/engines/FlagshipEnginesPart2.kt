package com.example.ui.engines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AdaptiveChallengeParameters
import com.example.data.FamilyMemberEntity
import com.example.data.SavedArtworkEntity
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
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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

    val target = allItems[roundIdx % allItems.size]
    val choices = remember(allItems, roundIdx, adaptive.effectiveChoiceCount, replayKey) {
        val count = adaptive.effectiveChoiceCount.coerceIn(1, allItems.size)
        val rotated = allItems.drop(roundIdx % allItems.size) + allItems.take(roundIdx % allItems.size)
        rotated.take(count).shuffled()
    }
    var promptMessage by remember(roundIdx, replayKey, activity.id) {
        mutableStateOf("Listen and tap: ${target.label} (${target.categoryOrTarget})")
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
                style = MaterialTheme.typography.titleMedium,
                color = DeepSlateInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.audio.playAnimalOrObjectSignature(
                        baseFreqHz = 320f + (roundIdx % 5) * 45f,
                        label = target.label,
                        spokenFact = target.spokenText
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("animal_hear_call_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = DeepSlateInk)
                Spacer(Modifier.width(6.dp))
                Text("Hear ${target.label}", color = DeepSlateInk, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            choices.forEachIndexed { idx, option ->
                val isTarget = option.id == target.id
                val glow = isTarget && adaptive.showVisualScaffoldGlow

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            if (isTarget) {
                                promptMessage = "🎉 You found ${option.label}! ${option.spokenText}"
                                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = hadRetries)
                                roundIdx += 1
                            } else {
                                hadRetries = true
                                promptMessage = "That's ${option.label}! Let's look for ${target.label}."
                                viewModel.onGentleRetryNeeded(activity, "That's ${option.label}. Try tapping ${target.label}!")
                            }
                        }
                        .testTag("animal_choice_$idx"),
                    color = if (glow) Color(option.primaryColorHex).copy(alpha = 0.22f) else SoftCreamCard,
                    border = BorderStroke(
                        width = if (glow) 4.dp else 2.5.dp,
                        color = Color(option.primaryColorHex)
                    ),
                    shape = RoundedCornerShape(22.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
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
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = option.emoji, fontSize = 56.sp)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepSlateInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = option.categoryOrTarget,
                            fontSize = 12.sp,
                            color = MutedSlateText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
 * - Stores normalized [0..1] stroke coordinates + colorHex + strokeWidth so saved artworks preserve
 *   real multi-color geometry when leaving and reopening or viewing in the Toy Box gallery.
 * - Uses a compact 4x2 palette grid so `Save Art` (`coloring_save_button`) is guaranteed >= 48.dp visible
 *   height on both phone landscape (`1080x1920` @ 420dpi) and tablet landscape.
 */
data class DrawnStroke(
    val points: List<Offset>, // Normalized [0f..1f] coordinates
    val colorHex: Long,
    val strokeWidth: Float
)

fun encodeStrokesPayload(strokes: List<DrawnStroke>): String {
    if (strokes.isEmpty()) return ""
    return strokes.joinToString(";") { stroke ->
        val sampled = if (stroke.points.size <= 36) {
            stroke.points
        } else {
            val step = (stroke.points.size - 1).toFloat() / 35f
            (0..35).map { idx -> stroke.points[(idx * step).toInt().coerceIn(0, stroke.points.lastIndex)] }
        }
        val pts = sampled.joinToString(",") { pt ->
            String.format(Locale.US, "%.3f:%.3f", pt.x.coerceIn(0f, 1f), pt.y.coerceIn(0f, 1f))
        }
        "${stroke.colorHex}|${stroke.strokeWidth.toInt()}|$pts"
    }
}

fun decodeStrokesPayload(payload: String): List<DrawnStroke> {
    if (payload.isBlank() || payload == "sketch_star") return emptyList()
    return payload.split(";").mapNotNull { entry ->
        val pipeParts = entry.split("|")
        if (pipeParts.size == 3) {
            val colorHex = pipeParts[0].toLongOrNull() ?: 0xFFEF4444L
            val strokeWidth = pipeParts[1].toFloatOrNull() ?: 22f
            val points = pipeParts[2].split(",").mapNotNull { pair ->
                val xy = pair.split(":")
                if (xy.size == 2) {
                    val x = xy[0].toFloatOrNull()
                    val y = xy[1].toFloatOrNull()
                    if (x != null && y != null) Offset(x.coerceIn(0f, 1f), y.coerceIn(0f, 1f)) else null
                } else null
            }
            if (points.size >= 2) DrawnStroke(points, colorHex, strokeWidth) else null
        } else {
            // Legacy fallback format: colorHex:pointCount
            val legacyParts = entry.split(":")
            val colorHex = legacyParts.firstOrNull()?.toLongOrNull() ?: return@mapNotNull null
            DrawnStroke(
                points = listOf(Offset(0.25f, 0.35f), Offset(0.75f, 0.65f)),
                colorHex = colorHex,
                strokeWidth = 22f
            )
        }
    }
}

@Composable
fun ColoringCanvasEngine(
    activity: ActivityConfig,
    savedArtworks: List<SavedArtworkEntity> = emptyList(),
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val paletteColors = listOf(
        0xFFEF4444L, 0xFFF97316L, 0xFFEAB308L, 0xFF10B981L,
        0xFF06B6D4L, 0xFF3B82F6L, 0xFF8B5CF6L, 0xFFEC4899L
    )
    var activeColorHex by remember(activity.id) { mutableLongStateOf(paletteColors.first()) }
    var activeBrushWidth by remember(activity.id) { mutableFloatStateOf(22f) }
    var isEraser by remember(activity.id) { mutableStateOf(false) }

    val matchingSavedArt = remember(activity.title, savedArtworks) {
        savedArtworks.firstOrNull { it.title == activity.title }
    }
    var loadedSavedId by remember(activity.id, replayKey) { mutableLongStateOf(-1L) }
    val strokes = remember(activity.id, replayKey) {
        mutableStateListOf<DrawnStroke>().apply {
            if (matchingSavedArt != null) {
                addAll(decodeStrokesPayload(matchingSavedArt.strokesPayload))
            }
        }
    }
    LaunchedEffect(matchingSavedArt?.id, matchingSavedArt?.strokesPayload) {
        if (matchingSavedArt != null && matchingSavedArt.id != loadedSavedId && strokes.isEmpty()) {
            val decoded = decodeStrokesPayload(matchingSavedArt.strokesPayload)
            if (decoded.isNotEmpty()) {
                strokes.addAll(decoded)
                loadedSavedId = matchingSavedArt.id
            }
        }
    }

    val currentPoints = remember { mutableStateListOf<Offset>() }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_coloring_canvas"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left: Compact Color Palette & Tool Column (Fits phone landscape 100% with >= 48dp Save Art button)
        Column(
            modifier = Modifier
                .width(172.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 8 Colors in a compact 4x2 grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (r in 0..1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (c in 0..3) {
                            val swatchIdx = r * 4 + c
                            val hex = paletteColors[swatchIdx]
                            val selected = !isEraser && activeColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(
                                        width = if (selected) 3.dp else 1.dp,
                                        color = if (selected) DeepSlateInk else PlaySurfaceWhite,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        isEraser = false
                                        activeColorHex = hex
                                    }
                                    .testTag("coloring_swatch_$swatchIdx")
                            )
                        }
                    }
                }
            }

            // Brush Sizes (S, M, L) + Eraser in a single horizontal strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(12f to "S", 22f to "M", 36f to "L").forEach { (w, lbl) ->
                    val isSelected = !isEraser && activeBrushWidth == w
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable {
                                isEraser = false
                                activeBrushWidth = w
                            }
                            .testTag("coloring_brush_$lbl"),
                        color = if (isSelected) ActionContinueBlue else SoftCreamCard,
                        border = BorderStroke(1.5.dp, if (isSelected) DeepSlateInk else Color(0xFFCBD5E1))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = lbl,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PlaySurfaceWhite else DeepSlateInk
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isEraser = !isEraser }
                        .testTag("coloring_eraser_button"),
                    color = if (isEraser) ActionListenAmber else SoftCreamCard,
                    border = BorderStroke(1.5.dp, if (isEraser) DeepSlateInk else Color(0xFFCBD5E1))
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEraser) "🧽 ON" else "🧽",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateInk
                        )
                    }
                }
            }

            // Undo & Clear Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex) },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCreamCard, contentColor = DeepSlateInk),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("coloring_undo_button")
                ) {
                    Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Undo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { strokes.clear() },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCreamCard, contentColor = DeepSlateInk),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("coloring_clear_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Prominent Save Art Button (Guaranteed >= 48dp height on phone & tablet)
            Button(
                onClick = {
                    val effectiveStrokes = if (strokes.isNotEmpty()) {
                        strokes.toList()
                    } else {
                        listOf(
                            DrawnStroke(
                                points = listOf(Offset(0.28f, 0.42f), Offset(0.52f, 0.34f), Offset(0.72f, 0.56f)),
                                colorHex = activeColorHex,
                                strokeWidth = activeBrushWidth
                            )
                        )
                    }
                    if (strokes.isEmpty()) {
                        strokes.addAll(effectiveStrokes)
                    }
                    val payload = encodeStrokesPayload(effectiveStrokes)
                    viewModel.saveChildArtwork(
                        title = activity.title,
                        templateName = activity.traceWordHint,
                        strokesPayload = payload,
                        bgHex = 0xFFFFFBEBL
                    )
                    viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("coloring_save_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Art", modifier = Modifier.size(18.dp), tint = PlaySurfaceWhite)
                Spacer(Modifier.width(6.dp))
                Text("Save Art", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PlaySurfaceWhite)
            }
        }

        // Right: Full-Screen Drawing & Coloring Canvas with Subject Line-Art Guides
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFFFFBEB))
                .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
                .pointerInput(isEraser, activeColorHex, activeBrushWidth) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val w = size.width.coerceAtLeast(1).toFloat()
                            val h = size.height.coerceAtLeast(1).toFloat()
                            currentPoints.clear()
                            currentPoints.add(Offset((offset.x / w).coerceIn(0f, 1f), (offset.y / h).coerceIn(0f, 1f)))
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val w = size.width.coerceAtLeast(1).toFloat()
                            val h = size.height.coerceAtLeast(1).toFloat()
                            currentPoints.add(
                                Offset(
                                    (change.position.x / w).coerceIn(0f, 1f),
                                    (change.position.y / h).coerceIn(0f, 1f)
                                )
                            )
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                val pts = if (currentPoints.size == 1) {
                                    listOf(currentPoints[0], Offset((currentPoints[0].x + 0.01f).coerceAtMost(1f), (currentPoints[0].y + 0.01f).coerceAtMost(1f)))
                                } else {
                                    currentPoints.toList()
                                }
                                strokes.add(
                                    DrawnStroke(
                                        points = pts,
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
                        val w = size.width.coerceAtLeast(1).toFloat()
                        val h = size.height.coerceAtLeast(1).toFloat()
                        val nx = (tapOffset.x / w).coerceIn(0f, 1f)
                        val ny = (tapOffset.y / h).coerceIn(0f, 1f)
                        strokes.add(
                            DrawnStroke(
                                points = listOf(
                                    Offset(nx, ny),
                                    Offset((nx + 0.025f).coerceAtMost(1f), (ny + 0.025f).coerceAtMost(1f))
                                ),
                                colorHex = if (isEraser) 0xFFFFFBEBL else activeColorHex,
                                strokeWidth = if (isEraser) 44f else activeBrushWidth * 1.4f
                            )
                        )
                    }
                }
                .testTag("drawing_surface_canvas")
        ) {
            val distinctColorsCount = strokes.filter { it.colorHex != 0xFFFFFBEBL }.map { it.colorHex }.distinct().size
            Text(
                text = "${activity.emoji} ${activity.traceWordHint} • Strokes: ${strokes.size} • Colors: $distinctColorsCount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MutedSlateText,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .testTag("coloring_canvas_status")
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw subtle subject line-art guide for coloring pages (w05_coloring_2..10)
                if (activity.id != "w05_coloring_1") {
                    drawColoringTemplateOutline(activity.id)
                }

                val cw = size.width.coerceAtLeast(1f)
                val ch = size.height.coerceAtLeast(1f)

                strokes.forEach { stroke ->
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
                            style = Stroke(width = stroke.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
                if (currentPoints.size >= 2) {
                    val livePath = Path().apply {
                        moveTo(currentPoints.first().x * cw, currentPoints.first().y * ch)
                        for (i in 1 until currentPoints.size) {
                            lineTo(currentPoints[i].x * cw, currentPoints[i].y * ch)
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

private fun DrawScope.drawColoringTemplateOutline(activityId: String) {
    val guideColor = Color(0xFF94A3B8).copy(alpha = 0.42f)
    val guideStroke = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = size.width * 0.5f
    val cy = size.height * 0.54f
    val r = min(size.width, size.height) * 0.30f

    when (activityId) {
        "w05_coloring_2" -> {
            // Sunny Lion: Mane rays + face circle + ears
            drawCircle(color = guideColor, radius = r * 1.15f, center = Offset(cx, cy), style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.78f, center = Offset(cx, cy), style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.22f, center = Offset(cx - r * 0.65f, cy - r * 0.65f), style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.22f, center = Offset(cx + r * 0.65f, cy - r * 0.65f), style = guideStroke)
        }
        "w05_coloring_3" -> {
            // Rainbow Rocket: Fuselage + nose cone + fins + window
            val rocket = Path().apply {
                moveTo(cx, cy - r * 1.1f)
                lineTo(cx + r * 0.45f, cy - r * 0.35f)
                lineTo(cx + r * 0.45f, cy + r * 0.75f)
                lineTo(cx - r * 0.45f, cy + r * 0.75f)
                lineTo(cx - r * 0.45f, cy - r * 0.35f)
                close()
            }
            drawPath(rocket, color = guideColor, style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.22f, center = Offset(cx, cy - r * 0.05f), style = guideStroke)
        }
        "w05_coloring_8" -> {
            // Butterfly Garden: Body + 4 symmetrical wings
            drawCircle(color = guideColor, radius = r * 0.55f, center = Offset(cx - r * 0.55f, cy - r * 0.35f), style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.55f, center = Offset(cx + r * 0.55f, cy - r * 0.35f), style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.42f, center = Offset(cx - r * 0.45f, cy + r * 0.42f), style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.42f, center = Offset(cx + r * 0.45f, cy + r * 0.42f), style = guideStroke)
            drawLine(color = guideColor, start = Offset(cx, cy - r * 0.85f), end = Offset(cx, cy + r * 0.85f), strokeWidth = 8f, cap = StrokeCap.Round)
        }
        "w05_coloring_9", "w05_coloring_10" -> {
            // Cozy Castle / Happy Family Home: Roof + walls + door + windows
            drawRect(
                color = guideColor,
                topLeft = Offset(cx - r * 0.85f, cy - r * 0.25f),
                size = Size(r * 1.7f, r * 1.15f),
                style = guideStroke
            )
            val roof = Path().apply {
                moveTo(cx - r * 1.0f, cy - r * 0.25f)
                lineTo(cx, cy - r * 1.05f)
                lineTo(cx + r * 1.0f, cy - r * 0.25f)
                close()
            }
            drawPath(roof, color = guideColor, style = guideStroke)
            drawRect(
                color = guideColor,
                topLeft = Offset(cx - r * 0.22f, cy + r * 0.25f),
                size = Size(r * 0.44f, r * 0.65f),
                style = guideStroke
            )
        }
        else -> {
            // Friendly Animal / Train / Whale / Dino / Unicorn outline
            drawCircle(color = guideColor, radius = r * 0.85f, center = Offset(cx, cy), style = guideStroke)
            drawCircle(color = guideColor, radius = r * 0.45f, center = Offset(cx + r * 0.72f, cy - r * 0.35f), style = guideStroke)
        }
    }
}

/**
 * ENGINE 7: Flagship Music & Rhythm Studio (Piano, Xylophone, Drums, Echo Beats, Birds, Bells & Sound Detective)
 * - Supports all 8 w11_music_1..8 activities with activity-specific modes and real 3-note Echo Beats imitation
 */
@Composable
fun MusicInstrumentsEngine(
    activity: ActivityConfig,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val defaultMode = remember(activity.id) {
        when (activity.id) {
            "w11_music_2" -> 1 // Sparkling Xylophone
            "w11_music_3" -> 2 // Jungle Rhythm Drums
            "w11_music_4" -> 3 // Echo Beats 3-note imitation
            "w11_music_5" -> 4 // Singing Bird Orchestra
            "w11_music_6" -> 5 // Golden Bell Choir
            "w11_music_7" -> 6 // Instrument Sound Detective
            "w11_music_8" -> 7 // High Note vs Low Note
            else -> 0          // Rainbow Piano
        }
    }
    var instrumentMode by remember(activity.id, replayKey) { mutableIntStateOf(defaultMode) }
    var notesPlayed by remember(activity.id, replayKey) { mutableIntStateOf(0) }

    val musicScope = rememberCoroutineScope()
    var demoJob by remember { mutableStateOf<Job?>(null) }
    DisposableEffect(activity.id, replayKey, instrumentMode) {
        onDispose {
            demoJob?.cancel()
            viewModel.audio.stopAllAudio()
        }
    }
    fun beginPlaying() {
        demoJob?.cancel()
        viewModel.audio.stopSpeechOnly()
    }

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

    // Echo Beats 3-note melody state
    val echoMelodies = remember {
        listOf(
            listOf("C", "E", "G"),
            listOf("D", "F", "A"),
            listOf("E", "G", "C5"),
            listOf("G", "E", "C")
        )
    }
    var echoRound by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var echoStep by remember(activity.id, echoRound, replayKey) { mutableIntStateOf(0) }
    val currentEchoPattern = echoMelodies[echoRound % echoMelodies.size]
    var echoFeedback by remember(activity.id, echoRound, replayKey) {
        mutableStateOf("Echo Pattern: ${currentEchoPattern.joinToString(" → ")} (Tap ${currentEchoPattern.first()} first!)")
    }

    // Sound Detective & High/Low quiz state
    var quizIdx by remember(activity.id, replayKey) { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_music_instruments"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Instrument Switcher & Echo Tune Header Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    0 to "🎹 Piano",
                    1 to "🎼 Xylophone",
                    2 to "🥁 Drums",
                    3 to "🔁 Echo"
                ).forEach { (idx, label) ->
                    val selected = instrumentMode == idx || (idx == defaultMode && instrumentMode == defaultMode)
                    Button(
                        onClick = { instrumentMode = idx },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) ActionContinueBlue else SoftCreamCard,
                            contentColor = if (selected) PlaySurfaceWhite else DeepSlateInk
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (instrumentMode == 3) {
                Text(
                    text = echoFeedback,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepSlateInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }

            Button(
                onClick = {
                    demoJob?.cancel()
                    viewModel.audio.stopAllAudio()
                    val notesToPlay = currentEchoPattern.toList()
                    demoJob = musicScope.launch {
                        echoFeedback = "Listen to the notes"
                        notesToPlay.forEach { noteName ->
                            val freq = scaleNotes.find { it.first == noteName }?.second ?: 261.63f
                            viewModel.audio.playNoteHz(freq, 220, isXylophone = true)
                            delay(600)
                        }
                        echoFeedback = "Your turn!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .height(42.dp)
                    .testTag("music_play_echo_button")
            ) {
                Text("🎵 Play Echo Tune", color = DeepSlateInk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(6.dp))

        when (instrumentMode) {
            2 -> {
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
                                .size(136.dp)
                                .clip(CircleShape)
                                .clickable {
                                    beginPlaying()
                                    notesPlayed += 1
                                    viewModel.audio.playNoteHz(freq, 220, isDrum = idx < 3, isXylophone = idx == 3)
                                    if (notesPlayed % 6 == 0) {
                                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false, speakMilestonePraise = false, playMilestoneChime = false)
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
                                Text("🥁", fontSize = 36.sp)
                                Text(name, style = MaterialTheme.typography.titleMedium, color = DeepSlateInk)
                            }
                        }
                    }
                }
            }
            6, 7 -> {
                // w11_music_7 (Instrument Sound Detective) & w11_music_8 (High Note vs Low Note)
                val soundOptions = remember(instrumentMode) {
                    if (instrumentMode == 6) {
                        listOf(
                            GameItemOption("inst_piano", "Grand Piano", "🎹", "Warm Keys", "Grand Piano plays rich warm chords!", primaryColorHex = 0xFF3B82F6),
                            GameItemOption("inst_xylo", "Bright Xylophone", "🎼", "Wooden Bars", "Sparkling Xylophone rings clear and bright!", primaryColorHex = 0xFF10B981),
                            GameItemOption("inst_drum", "Jungle Drum", "🥁", "Deep Beat", "Jungle Drum booms with a steady beat!", primaryColorHex = 0xFFEF4444),
                            GameItemOption("inst_bell", "Golden Bell", "🔔", "High Chime", "Golden Bell chimes high in the tower!", primaryColorHex = 0xFFF59E0B)
                        )
                    } else {
                        listOf(
                            GameItemOption("pitch_high_bird", "High Singing Bird", "🐦", "High Pitch (784 Hz)", "Tweet-tweet! That is a high-pitched bird note!", primaryColorHex = 0xFF06B6D4),
                            GameItemOption("pitch_low_whale", "Low Ocean Whale", "🐋", "Low Pitch (131 Hz)", "Whooo-oom! That is a deep, low-pitched whale song!", primaryColorHex = 0xFF3B82F6),
                            GameItemOption("pitch_high_bell", "High Tiny Bell", "🔔", "High Pitch (880 Hz)", "Ting-ring! That is a high sparkling bell note!", primaryColorHex = 0xFFF59E0B),
                            GameItemOption("pitch_low_tuba", "Low Bass Drum", "🥁", "Low Pitch (110 Hz)", "Boom-boom! That is a low bass drum sound!", primaryColorHex = 0xFFEF4444)
                        )
                    }
                }
                val targetSound = soundOptions[quizIdx % soundOptions.size]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (instrumentMode == 6) {
                            "Sound Detective: Tap ${targetSound.label} (${targetSound.categoryOrTarget})!"
                        } else {
                            "High vs Low Pitch: Tap ${targetSound.label} (${targetSound.categoryOrTarget})!"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepSlateInk
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        soundOptions.forEachIndexed { idx, opt ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        val freq = if (opt.id.contains("low") || opt.id.contains("drum")) 130f else if (opt.id.contains("high") || opt.id.contains("bell")) 784f else 392f
                                        viewModel.audio.playNoteHz(freq, 300, isDrum = opt.id.contains("drum"), isXylophone = opt.id.contains("xylo") || opt.id.contains("bell"))
                                        if (opt.id == targetSound.id) {
                                            viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false, speakMilestonePraise = false, playMilestoneChime = false)
                                            quizIdx += 1
                                        } else {
                                            viewModel.onGentleRetryNeeded(activity, "That's ${opt.label}. Listen for ${targetSound.label}!")
                                        }
                                    }
                                    .testTag("music_detective_choice_$idx"),
                                color = SoftCreamCard,
                                border = BorderStroke(3.dp, Color(opt.primaryColorHex)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(opt.emoji, fontSize = 44.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Text(opt.label, fontWeight = FontWeight.Bold, color = DeepSlateInk, textAlign = TextAlign.Center)
                                    Text(opt.categoryOrTarget, fontSize = 11.sp, color = MutedSlateText, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                // 8 Large Responsive Bars for Piano (0), Xylophone (1), Echo Beats (3), Bird Orchestra (4), Bell Choir (5)
                val keyBadgeEmoji = when (instrumentMode) {
                    4 -> listOf("🐦", "🐤", "🦜", "🦢", "🕊️", "🦅", "🦉", "🦚")
                    5 -> List(8) { "🔔" }
                    else -> emptyList()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    scaleNotes.forEachIndexed { idx, (noteName, freq, color) ->
                        val heightFraction = if (instrumentMode == 1 || instrumentMode == 5) (1f - idx * 0.055f) else 0.96f
                        val isNextEchoTarget = instrumentMode == 3 && currentEchoPattern.getOrNull(echoStep) == noteName
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightFraction)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    beginPlaying()
                                    notesPlayed += 1
                                    val pitchMultiplier = if (instrumentMode == 4) 1.5f else 1.0f
                                    viewModel.audio.playNoteHz(
                                        freqHz = freq * pitchMultiplier,
                                        durationMs = 340,
                                        isXylophone = instrumentMode == 1 || instrumentMode == 5
                                    )
                                    if (instrumentMode == 3) {
                                        val expected = currentEchoPattern.getOrNull(echoStep)
                                        if (noteName == expected) {
                                            if (echoStep + 1 >= currentEchoPattern.size) {
                                                echoFeedback = "🎉 Echo Complete: ${currentEchoPattern.joinToString(" → ")}!"
                                                viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false, speakMilestonePraise = false, playMilestoneChime = false)
                                                echoRound += 1
                                                echoStep = 0
                                            } else {
                                                echoStep += 1
                                                echoFeedback = "Great! Next Echo note (${echoStep + 1}/3): ${currentEchoPattern[echoStep]}"
                                            }
                                        } else {
                                            echoStep = 0
                                            echoFeedback = "Listen closely! Start with ${currentEchoPattern.first()} (${currentEchoPattern.joinToString(" → ")})"
                                        }
                                    } else if (notesPlayed % 8 == 0) {
                                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = false, speakMilestonePraise = false, playMilestoneChime = false)
                                    }
                                }
                                .testTag("music_key_$noteName"),
                            color = color,
                            border = BorderStroke(if (isNextEchoTarget) 5.dp else 3.dp, if (isNextEchoTarget) DeepSlateInk else PlaySurfaceWhite),
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = 5.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (keyBadgeEmoji.isNotEmpty()) {
                                    Text(text = keyBadgeEmoji[idx], fontSize = 26.sp)
                                } else if (isNextEchoTarget) {
                                    Text(text = "👇", fontSize = 24.sp)
                                } else {
                                    Spacer(Modifier.height(8.dp))
                                }
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
}

/**
 * ENGINE 8: Memory Match Engine (Age-Adjusted Grid Complexity & Partner Matching)
 */
@Composable
fun MemoryMatchEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    var hadMismatch by remember(activity.id, replayKey) { mutableStateOf(false) }
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
        val partnerCards = chosen.map { item ->
            if (activity.id == "w03_memory_4" && item.label.length == 1 && item.label[0].isUpperCase()) {
                item.copy(label = item.label.lowercase())
            } else {
                item
            }
        }
        (chosen + partnerCards).shuffled()
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
            text = "${activity.title}: Matched ${matchedIndices.size / 2} of $pairCount pairs! Tap two cards to flip them.",
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
                                                if (deck[firstIdx].id != deck[secondIdx].id) hadMismatch = true
                                                if (deck[firstIdx].id == deck[secondIdx].id) {
                                                    matchedIndices.add(firstIdx)
                                                    matchedIndices.add(secondIdx)
                                                    flippedIndices.clear()
                                                    if (matchedIndices.size == deck.size) {
                                                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = hadMismatch)
                                                    }
                                                }
                                            }
                                        } else if (flippedIndices.size == 2 && !isOpen) {
                                            flippedIndices.clear()
                                            flippedIndices.add(idx)
                                            viewModel.audio.speakInstruction(card.label)
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
                                            Text(text = card.emoji, fontSize = 32.sp)
                                            Text(
                                                text = card.label,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepSlateInk,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        Text(text = "🌟", fontSize = 32.sp)
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
 * - Shows real letters/words in large answer tiles
 * - Explicitly distinguishes Letter Names from Phonetic Sounds
 * - Supports Three-Letter CVC Word Blending (w07_phonics_10) and 52-Step Journeys (w07_phonics_11, w07_phonics_12)
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
    var stepIdx by remember(activity.id, replayKey) { mutableIntStateOf(savedStepIndex.coerceAtLeast(0)) }
    var hadRetries by remember(stepIdx, replayKey) { mutableStateOf(false) }

    if (activity.id == "w07_phonics_10") {
        CvcWordBuilder(activity, pool, stepIdx, replayKey, viewModel) { next ->
            stepIdx = next
        }
        return
    }
    val target = pool[stepIdx % pool.size]
    val choices = remember(pool, stepIdx, adaptive.effectiveChoiceCount, replayKey) {
        val count = adaptive.effectiveChoiceCount.coerceIn(1, pool.size)
        val rotated = pool.drop(stepIdx % pool.size) + pool.take(stepIdx % pool.size)
        rotated.take(count).shuffled()
    }

    val headerPrompt = remember(activity.id, target, stepIdx) {
        when (activity.id) {
            "w07_phonics_4" -> "Find the lowercase partner for ${target.label.first()}"
            "w07_phonics_5" -> "What rhymes with ${target.label.substringAfter("•").trim()}?"
            "w07_phonics_6" -> "Which letter is missing? ${target.categoryOrTarget}"
            "w07_phonics_7" -> "Which letter belongs here? ${target.categoryOrTarget}"
            "w07_phonics_10" -> "CVC Word Builder: Blend ${target.label.toCharArray().joinToString("-")} for ${target.categoryOrTarget} ${target.emoji}"
            "w07_phonics_11", "w07_phonics_12" -> "Step ${(stepIdx % pool.size) + 1} of ${pool.size}: Tap ${target.label} (${target.categoryOrTarget}) ${target.emoji}"
            else -> "Tap the tile for: ${target.label} (${target.categoryOrTarget}) ${target.emoji}"
        }
    }

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
                text = headerPrompt,
                style = MaterialTheme.typography.titleLarge,
                color = DeepSlateInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val ch = target.label.firstOrNull { it.isLetter() } ?: 'A'
                        viewModel.audio.speakPhonicsLetter(ch, speakSoundNotName = false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(46.dp)
                        .testTag("phonics_letter_name_button")
                ) {
                    Text("🔤 Letter Name", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val ch = target.label.firstOrNull { it.isLetter() } ?: 'A'
                        viewModel.audio.speakPhonicsLetter(ch, speakSoundNotName = true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(46.dp)
                        .testTag("phonics_pure_sound_button")
                ) {
                    Text("🗣️ Phonics Sound", color = DeepSlateInk, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Large Real Letter / Word Answer Tiles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            choices.forEachIndexed { idx, item ->
                val isTarget = item.id == target.id
                val glow = isTarget && adaptive.showVisualScaffoldGlow

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            if (isTarget) {
                                val nextStep = stepIdx + 1
                                viewModel.onActivityMilestoneCompleted(
                                    activity = activity,
                                    hadRetriesInRound = hadRetries,
                                    nextStepIndex = nextStep
                                )
                                stepIdx = nextStep
                            } else {
                                hadRetries = true
                                viewModel.onGentleRetryNeeded(activity, "That is ${item.label}. Try finding ${target.label}!")
                            }
                        }
                        .testTag("phonics_tile_$idx"),
                    color = if (glow) ActionPlayGreen.copy(alpha = 0.18f) else SoftCreamCard,
                    border = BorderStroke(if (glow) 4.dp else 3.dp, Color(item.primaryColorHex)),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (activity.id) {
                                "w07_phonics_4" -> item.label.last().toString()
                                "w07_phonics_5" -> item.label.substringBefore("•").trim()
                                else -> item.label
                            },
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = if (item.label.length <= 2) 56.sp else 36.sp,
                            color = DeepSlateInk,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${item.emoji} ${item.categoryOrTarget}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedSlateText,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 10: Numbers & Math Counter Engine
 * - Supports One-by-One Counting (w08_math_1), Apples in Basket (w08_math_2), Group Match (w08_math_3),
 *   Number Hunt (w08_math_4), Picture Addition Two Groups (w08_math_5), Number Train Order (w08_math_6),
 *   More or Fewer Compare (w08_math_7), Shape Sides Counter (w08_math_8), and 1–50 / 51–100 Journeys (w08_math_9..10).
 */
@Composable
fun NumbersMathCounterEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    savedStepIndex: Int,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    var stepIdx by remember(activity.id, replayKey) { mutableIntStateOf(savedStepIndex.coerceAtLeast(0)) }
    val itemPool = remember(activity.id) {
        activity.items.ifEmpty {
            listOf(GameItemOption("num_star", "Stars", "⭐", "3", "Count the stars!"))
        }
    }
    val currentItem = itemPool[stepIdx % itemPool.size]
    val parsedTarget = currentItem.categoryOrTarget.filter { it.isDigit() }.toIntOrNull()
    val targetCount = remember(activity.id, stepIdx, parsedTarget) {
        when (activity.id) {
            "w08_math_8" -> (parsedTarget ?: 3).coerceIn(3, 8) // Shape sides: 3, 4, 5, 6, 8
            "w08_math_5" -> (parsedTarget ?: ((stepIdx % 6) + 3)).coerceIn(2, 8)
            else -> ((parsedTarget ?: ((stepIdx % 7) + 2)).coerceIn(2, 8))
        }
    }
    val tappedIndices = remember(activity.id, stepIdx, targetCount, replayKey) { mutableStateListOf<Int>() }

    // Addition split for w08_math_5
    val leftGroupCount = (targetCount / 2).coerceAtLeast(1)
    val rightGroupCount = (targetCount - leftGroupCount).coerceAtLeast(1)

    // Journey display number for w08_math_9 (1..50) and w08_math_10 (51..100)
    val journeyDisplayNumber = when (activity.id) {
        "w08_math_9" -> (stepIdx % 50) + 1
        "w08_math_10" -> 51 + (stepIdx % 50)
        else -> targetCount
    }

    val promptHeader = when (activity.id) {
        "w08_math_2" -> "🧺 Apple Basket (${tappedIndices.size}/$targetCount in basket): Tap all $targetCount 🍎, then tap $targetCount!"
        "w08_math_5" -> "➕ Picture Addition: $leftGroupCount ${currentItem.emoji} + $rightGroupCount ${currentItem.emoji} = $targetCount! Tap to count (${tappedIndices.size}/$targetCount)"
        "w08_math_6" -> "🚂 Number Train Order: Car #${(stepIdx % 10) + 1} needs $targetCount ${currentItem.emoji}! (${tappedIndices.size}/$targetCount)"
        "w08_math_7" -> "⚖️ More or Fewer: Big Basket has $targetCount ${currentItem.emoji} vs Small Basket ${(targetCount - 1).coerceAtLeast(1)}! Tap $targetCount"
        "w08_math_8" -> "📐 Shape Sides Counter: Count all $targetCount sides of ${currentItem.label} ${currentItem.emoji}! (${tappedIndices.size}/$targetCount)"
        "w08_math_9", "w08_math_10" -> "🔢 Journey Landmark #$journeyDisplayNumber: Tap ${currentItem.emoji} (${tappedIndices.size}/$targetCount), then tap $targetCount!"
        else -> "Tap each ${currentItem.emoji} (${currentItem.label}) one by one! Counted: ${tappedIndices.size} of $targetCount"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_numbers_math"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = promptHeader,
            style = MaterialTheme.typography.titleLarge,
            color = DeepSlateInk,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Interactive Counting Objects Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.56f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..targetCount) {
                val counted = i in tappedIndices
                val showPlusSignAfter = activity.id == "w08_math_5" && i == leftGroupCount
                Surface(
                    modifier = Modifier
                        .size(if (targetCount >= 7) 68.dp else 80.dp)
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
                        Text(text = currentItem.emoji, fontSize = if (targetCount >= 7) 28.sp else 34.sp)
                        if (counted) {
                            Text(
                                text = "${tappedIndices.indexOf(i) + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DeepSlateInk
                            )
                        }
                    }
                }
                if (showPlusSignAfter) {
                    Text(
                        text = "➕",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ActionContinueBlue
                    )
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
                .height(72.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            options.forEach { num ->
                Button(
                    onClick = {
                        if (num == targetCount) {
                            val nextStep = stepIdx + 1
                            viewModel.onActivityMilestoneCompleted(
                                activity = activity,
                                hadRetriesInRound = false,
                                nextStepIndex = nextStep
                            )
                            stepIdx = nextStep
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
 * ENGINE 11: 3-Page Narrated Storybook, Rhymes & Safety Story Engine
 * - Enforces `correctChoiceIndex` on multi-choice safety/story pages so unsafe/distractor choices
 *   trigger gentle corrective guidance instead of advancing as if correct.
 * - Includes an explicit `Read Again` action that resets to Page 1 and reads aloud without duplicate stars.
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
                StoryPageSpec(1, "${activity.title} - Page 1", "${activity.emoji} 🌟", activity.instructionText, "Ready to turn the page?", listOf("Next Page ▶")),
                StoryPageSpec(2, "${activity.title} - Page 2", "${activity.emoji} 🎶", activity.showMeDemoHint, "What happens next?", listOf("Turn to Page 3 ▶")),
                StoryPageSpec(3, "${activity.title} - Finale", "${activity.emoji} 🎉", "Hooray! We finished exploring ${activity.title} together!", "Would you like to read again?", listOf("Celebrate & Read Again! 🌟"))
            )
        }
    }
    var pageIdx by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var hadRetries by remember(activity.id, replayKey) { mutableStateOf(false) }
    var completedAtLeastOnce by remember(activity.id, replayKey) { mutableStateOf(false) }
    val currentPage = pages[pageIdx.coerceIn(0, pages.lastIndex)]
    LaunchedEffect(activity.id, pageIdx, replayKey) {
        viewModel.audio.stopSpeechOnly()
        viewModel.audio.speakStoryPage(currentPage.narrationText, currentPage.interactivePrompt)
    }
    DisposableEffect(activity.id) {
        onDispose { viewModel.audio.stopSpeechOnly() }
    }

    var feedbackBanner by remember(activity.id, pageIdx, replayKey) {
        mutableStateOf(currentPage.interactivePrompt)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_storybook_reader"),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Left: Illustrated Story Scene Card
        Surface(
            modifier = Modifier
                .weight(0.44f)
                .fillMaxHeight(),
            color = SoftCreamCard,
            border = BorderStroke(3.dp, activity.landId.accentColor),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Page ${currentPage.pageNumber} of ${pages.size}",
                        fontWeight = FontWeight.Bold,
                        color = MutedSlateText
                    )
                    if (pageIdx > 0) {
                        Button(
                            onClick = {
                                pageIdx = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftCreamCard, contentColor = DeepSlateInk),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("story_read_again_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Read Again", modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Read Again", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(text = currentPage.emojiScene, fontSize = 62.sp, textAlign = TextAlign.Center)

                if (currentPage.parentTip.isNotEmpty()) {
                    Surface(
                        color = ActionListenAmber.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "💬 Grown-Up Tip: ${currentPage.parentTip}",
                            fontSize = 11.sp,
                            color = DeepSlateInk,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        // Right: Narration Text, Interactive Choice & Page Turning
        Column(
            modifier = Modifier
                .weight(0.56f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = currentPage.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepSlateInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currentPage.narrationText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DeepSlateInk,
                    modifier = Modifier.height(100.dp).verticalScroll(rememberScrollState())
                )
                Button(
                    onClick = {
                        viewModel.audio.speakStoryPage(
                            narrationText = currentPage.narrationText,
                            interactivePrompt = currentPage.interactivePrompt
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("story_read_aloud_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = DeepSlateInk)
                    Spacer(Modifier.width(6.dp))
                    Text("Read Page Aloud", color = DeepSlateInk, fontWeight = FontWeight.Bold)
                }
            }

            // Interactive Story Choice Buttons
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = feedbackBanner,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ActionContinueBlue,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentPage.choiceOptions.forEachIndexed { idx, choiceLabel ->
                        val isReadAgainChoice = choiceLabel.contains("Read Again", ignoreCase = true)
                        val isCorrectChoice = currentPage.choiceOptions.size == 1 ||
                            idx == currentPage.correctChoiceIndex ||
                            isReadAgainChoice

                        Button(
                            onClick = {
                                if (isReadAgainChoice && pageIdx == pages.lastIndex) {
                                    if (!completedAtLeastOnce) {
                                        completedAtLeastOnce = true
                                        viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = hadRetries, speakMilestonePraise = false)
                                    }
                                    pageIdx = 0
                                } else if (isCorrectChoice) {
                                    if (pageIdx < pages.lastIndex) {
                                        pageIdx += 1
                                    } else {
                                        if (!completedAtLeastOnce) {
                                            completedAtLeastOnce = true
                                            viewModel.onActivityMilestoneCompleted(activity, hadRetriesInRound = hadRetries, speakMilestonePraise = false)
                                        }
                                        pageIdx = 0
                                    }
                                } else {
                                    hadRetries = true
                                    val safeOption = currentPage.choiceOptions.getOrElse(currentPage.correctChoiceIndex) {
                                        currentPage.choiceOptions.first()
                                    }
                                    feedbackBanner = "Let's choose the safe, kind answer: \"$safeOption\"!"
                                    viewModel.onGentleRetryNeeded(
                                        activity = activity,
                                        gentlePrompt = "Let's pause and think! Try choosing: $safeOption"
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ActionContinueBlue
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("story_choice_$idx")
                        ) {
                            Text(
                                text = choiceLabel,
                                color = PlaySurfaceWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * ENGINE 12: Early Coding Arrow Sequence Maze Engine
 * - Customizes protagonist, obstacle, and goal for each w13_coding_1..8 activity
 */
@Composable
fun CodingArrowMazeEngine(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val (botEmoji, botName, goalEmoji, goalName) = remember(activity.id) {
        when (activity.id) {
            "w13_coding_2" -> listOf("🐰", "Bunny", "🥕", "Carrot")
            "w13_coding_3" -> listOf("🚀", "Rocket", "🌙", "Moon")
            "w13_coding_4" -> listOf("🐶", "Puppy", "🦴", "Bone")
            "w13_coding_5" -> listOf("🐝", "Honeybee", "🌻", "Sunflower")
            "w13_coding_6" -> listOf("🚂", "Train", "🚉", "Station")
            "w13_coding_7" -> listOf("🚤", "Submarine", "👑", "Treasure")
            "w13_coding_8" -> listOf("🚒", "Rescue Bot", "🐱", "Kitty")
            else -> listOf("🤖", "Beep-Bot", "🌟", "Star")
        }
    }
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
                        val isDecorationObstacle = activity.id == "w13_coding_3" && c == 1 && r == 1 && !isBot
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
                                        isBot && isGoal -> "$botEmoji$goalEmoji"
                                        isBot -> botEmoji
                                        isGoal -> "$goalEmoji $goalName"
                                        isDecorationObstacle -> "☄️"
                                        else -> "🟩"
                                    },
                                    fontSize = 30.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right: Arrow Pad & Reset
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Tap arrows to guide $botName $botEmoji to the $goalName $goalEmoji!",
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("coding_reset_button")
            ) {
                Text("Reset $botName to Start", color = PlaySurfaceWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * ENGINE 13: Flagship Pretend Play, Life Skills, Movement, Nature, Science & Little Chef Engine
 * - Uses the required 3-stage progression:
 *   Stage 0: Demonstrate -> Stage 1: Guided Practice -> Stage 2: Independent Try
 * - Provides bespoke, title-accurate 3-step scripts and emojis for every activity so
 *   Handwashing, Pizzeria, Doctor Checkup, Cake Baker, Movement, etc. never fall back to Puppy Wash.
 */
@Composable
fun PretendStepCareEngine(
    activity: ActivityConfig,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val steps = remember(activity.id) {
        buildPretendCareSteps(activity)
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
        // Left: Interactive Scrub / Action Stage
        Surface(
            modifier = Modifier
                .weight(0.54f)
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
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "${activity.title} • $stageTitle",
                    style = MaterialTheme.typography.titleMedium,
                    color = ActionContinueBlue,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = stageEmoji, fontSize = 74.sp)
                Text(
                    text = "Tap or rub here ($scrubProgress/3) to complete this step!",
                    fontWeight = FontWeight.Bold,
                    color = DeepSlateInk,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Right: Demonstrate -> Guided Practice -> Independent Try Progression Cards
        Column(
            modifier = Modifier
                .weight(0.46f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stagePrompt,
                style = MaterialTheme.typography.titleMedium,
                color = DeepSlateInk,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { currentStage = idx }
                            .testTag("pretend_step_card_$idx")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = if (done) "✅" else emoji, fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepSlateInk)
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

private fun buildPretendCareSteps(activity: ActivityConfig): List<Triple<String, String, String>> {
    return when (activity.id) {
        // World 16: Pretend Play
        "w16_pretend_1" -> listOf(
            Triple("Step 1: Demonstrate", "🐶🛁", "First watch how we brush away leaves and add warm bubbly puppy soap!"),
            Triple("Step 2: Guided Practice", "🐶🫧", "Scrub your finger across the puppy to wash with gentle soapy bubbles!"),
            Triple("Step 3: Independent Try", "🐶🚿✨", "Rinse the bubbles with the warm shower and dry with a fluffy towel!")
        )
        "w16_pretend_2" -> listOf(
            Triple("Step 1: Demonstrate", "🩺🧸", "Place the stethoscope gently on Teddy's chest to hear a steady heartbeat: lub-dub!"),
            Triple("Step 2: Guided Practice", "🌡️🩹", "Check Teddy's temperature and place a colorful star bandage on the scraped knee!"),
            Triple("Step 3: Independent Try", "🧸💚✨", "Give Teddy a brave sticker and a gentle hug—all healthy and ready to play!")
        )
        "w16_pretend_3" -> listOf(
            Triple("Step 1: Demonstrate", "🫖🍵", "Set out the teacups and saucers, then pour warm herbal berry tea carefully!"),
            Triple("Step 2: Guided Practice", "🧁🍓", "Share fresh strawberry scones on each guest's plate and say 'Please' and 'Thank you'!"),
            Triple("Step 3: Independent Try", "☕🎶✨", "Clink teacups gently with your friends and host a joyful tea party!")
        )
        "w16_pretend_4" -> listOf(
            Triple("Step 1: Demonstrate", "🥣🥚", "Pour flour, milk, and eggs into the mixing bowl and stir the cake batter smooth!"),
            Triple("Step 2: Guided Practice", "🎂🔥", "Pour the batter into the baking pan and watch it rise golden and fluffy!"),
            Triple("Step 3: Independent Try", "🍰🍓✨", "Swirl vanilla frosting and top your celebration cake with sweet strawberries!")
        )
        "w16_pretend_5" -> listOf(
            Triple("Step 1: Demonstrate", "🛒🍎", "Roll each healthy fruit and veggie across the grocery scanner—beep, beep!"),
            Triple("Step 2: Guided Practice", "🛍️🥦", "Pack heavy items on the bottom and soft bread and berries gently on top!"),
            Triple("Step 3: Independent Try", "🧾😊✨", "Hand over the market receipt with a big friendly smile!")
        )
        "w16_pretend_6" -> listOf(
            Triple("Step 1: Demonstrate", "👶🍼", "Cradle Baby Doll gently in your arms and offer a warm milk bottle!"),
            Triple("Step 2: Guided Practice", "🧸🛏️", "Tuck a soft cozy blanket around Baby Doll and pat gently!"),
            Triple("Step 3: Independent Try", "🎶🌙✨", "Hum a quiet starlight lullaby as Baby Doll drifts off to sleep!")
        )
        "w16_pretend_7" -> listOf(
            Triple("Step 1: Demonstrate", "🪴🧤", "Put on garden gloves and loosen the soil around the thirsty flower sprouts!"),
            Triple("Step 2: Guided Practice", "🚿🌱", "Tip the watering can gently so cool water reaches every plant's roots!"),
            Triple("Step 3: Independent Try", "🌻🦋✨", "Watch the golden sunflowers bloom bright in the morning sunshine!")
        )
        "w16_pretend_8" -> listOf(
            Triple("Step 1: Demonstrate", "📏🪵", "Line up the wooden birdhouse boards and measure twice with your ruler!"),
            Triple("Step 2: Guided Practice", "🔨🔩", "Tap the wooden pegs in straight and turn the colorful bolts with your wrench!"),
            Triple("Step 3: Independent Try", "🏠🐦✨", "Stand back and admire the sturdy birdhouse you built at the workbench!")
        )

        // World 18: Everyday Life Skills
        "w18_skills_1" -> listOf(
            Triple("Step 1: Demonstrate", "🪥💧", "Put a pea-sized dot of minty toothpaste on your toothbrush and add water!"),
            Triple("Step 2: Guided Practice", "🦷🫧", "Brush in gentle circles across top, bottom, and back teeth for two minutes!"),
            Triple("Step 3: Independent Try", "😁✨", "Rinse with clean water and flash your sparkling healthy smile!")
        )
        "w18_skills_2" -> listOf(
            Triple("Step 1: Demonstrate", "🚰🧼", "Turn on warm water, wet both hands, and press one pump of bubbly soap!"),
            Triple("Step 2: Guided Practice", "👐🫧", "Scrub palms, backs of hands, and between fingers for 20 happy seconds!"),
            Triple("Step 3: Independent Try", "🚿🧻✨", "Rinse away every bubble with clean water and dry with a fresh towel!")
        )
        "w18_skills_3" -> listOf(
            Triple("Step 1: Demonstrate", "🧸🧺", "Look around the playroom and pick up building blocks from the rug first!"),
            Triple("Step 2: Guided Practice", "📚📦", "Place storybooks neatly on the shelf and cuddle toys into the toy basket!"),
            Triple("Step 3: Independent Try", "🧹🌟✨", "Give the floor a quick sweep—now our playroom is safe and tidy!")
        )
        "w18_skills_4" -> listOf(
            Triple("Step 1: Demonstrate", "👕👖", "Slip arms through your cozy shirt sleeves and pull up comfy pants!"),
            Triple("Step 2: Guided Practice", "🧦🧥", "Match left and right socks and pull your jacket zipper up to the top!"),
            Triple("Step 3: Independent Try", "🧢🌟✨", "Put on your sun hat—you got dressed all by yourself!")
        )
        "w18_skills_5" -> listOf(
            Triple("Step 1: Demonstrate", "🎒📘", "Open the big backpack pocket and slide in your folder and storybook!"),
            Triple("Step 2: Guided Practice", "🖍️💧", "Tuck your crayon box inside and place your water bottle in the side pocket!"),
            Triple("Step 3: Independent Try", "🎒😊✨", "Zip the backpack closed and wear both shoulder straps comfortably!")
        )
        "w18_skills_6" -> listOf(
            Triple("Step 1: Demonstrate", "🍽️🟩", "Lay a clean placemat at each seat around the family dinner table!"),
            Triple("Step 2: Guided Practice", "🥄🥛", "Set a plate in the middle with a napkin, fork, spoon, and water cup!"),
            Triple("Step 3: Independent Try", "🥗👨‍👩‍👧✨", "Call the family to the table for a warm meal together!")
        )
        "w18_skills_7" -> listOf(
            Triple("Step 1: Demonstrate", "👟🧦", "Check that left and right shoes match your feet and open the tongue wide!"),
            Triple("Step 2: Guided Practice", "👟➰", "Slide your heel all the way in and pull the straps or laces snug!"),
            Triple("Step 3: Independent Try", "🏃‍♂️🌟✨", "Press the velcro or tie the bow tight—ready to run and play!")
        )
        "w18_skills_8" -> listOf(
            Triple("Step 1: Demonstrate", "👚🪥", "Put on soft pajamas and brush your teeth until they sparkle!"),
            Triple("Step 2: Guided Practice", "📖🧸", "Cuddle with a grown-up to read a calm bedtime storybook!"),
            Triple("Step 3: Independent Try", "🛌🌙✨", "Tuck under the cozy blanket and wish the stars goodnight!")
        )

        // World 21: Movement & Mindfulness
        "w21_movement_1" -> listOf(
            Triple("Step 1: Demonstrate", "🐰🦵", "Bend your knees low like a meadow bunny getting ready to spring!"),
            Triple("Step 2: Guided Practice", "🐰⬆️", "Hold up two bunny ears with your hands and hop forward three times!"),
            Triple("Step 3: Independent Try", "🥕🌟✨", "Land softly on both feet and wiggle your bunny nose!")
        )
        "w21_movement_2" -> listOf(
            Triple("Step 1: Demonstrate", "🎶💃", "When the happy music plays, wiggle your arms and dance to the beat!"),
            Triple("Step 2: Guided Practice", "🛑🧊", "Listen for 'FREEZE!' and hold a funny statue pose without moving!"),
            Triple("Step 3: Independent Try", "🎉🌟✨", "Take a bow after three musical freeze rounds!")
        )
        "w21_movement_3" -> listOf(
            Triple("Step 1: Demonstrate", "🙆‍♂️☀️", "Stand tall like a mountain tree and breathe in deeply!"),
            Triple("Step 2: Guided Practice", "🌈✨", "Reach both fingertips high toward the clouds and stretch side to side!"),
            Triple("Step 3: Independent Try", "😌🌿✨", "Float your arms down slowly like autumn leaves!")
        )
        "w21_movement_4" -> listOf(
            Triple("Step 1: Demonstrate", "🦩👀", "Spread your wings wide for balance and look at one steady spot ahead!"),
            Triple("Step 2: Guided Practice", "🦩🦵", "Lift one foot slowly and balance like a pink lagoon flamingo for 1, 2, 3!"),
            Triple("Step 3: Independent Try", "🦩🌟✨", "Switch feet and balance tall on the other leg!")
        )
        "w21_movement_5" -> listOf(
            Triple("Step 1: Demonstrate", "🦋🌬️", "Sit comfortably and rest your hands gently on your belly!"),
            Triple("Step 2: Guided Practice", "🌈🫁", "Breathe in slowly through your nose as you open your butterfly wings wide!"),
            Triple("Step 3: Independent Try", "😌✨", "Breathe out softly through your mouth as your wings fold calm and still!")
        )
        "w21_movement_6" -> listOf(
            Triple("Step 1: Demonstrate", "👂👏", "Listen carefully: 'Simon says clap your hands three times!' Clap-clap-clap!"),
            Triple("Step 2: Guided Practice", "👂🦶", "'Simon says stomp your feet and touch your shoulders!'"),
            Triple("Step 3: Independent Try", "🏆🌟✨", "Wait for the magic words 'Simon says' before every move!")
        )

        // World 24: Little Chef Kitchen
        "w24_chef_1" -> listOf(
            Triple("Step 1: Demonstrate", "🍓🍌", "Wash red strawberries, yellow banana slices, and blue blueberries in cool water!"),
            Triple("Step 2: Guided Practice", "🥣🥝", "Scoop orange mango and green kiwi cubes into the big wooden salad bowl!"),
            Triple("Step 3: Independent Try", "🥗🌈✨", "Toss the rainbow fruit salad gently and serve in colorful bowls!")
        )
        "w24_chef_2" -> listOf(
            Triple("Step 1: Demonstrate", "🫓🍅", "Stretch the round pizza dough on the tray and swirl warm tomato sauce!"),
            Triple("Step 2: Guided Practice", "🧀🥦", "Sprinkle golden mozzarella cheese and arrange broccoli, peppers, and mushrooms!"),
            Triple("Step 3: Independent Try", "🍕🔥✨", "Bake until bubbly and slice your delicious veggie pizza!")
        )
        "w24_chef_3" -> listOf(
            Triple("Step 1: Demonstrate", "🍓🫐", "Drop sweet strawberries, blueberries, and banana chunks into the blender pitcher!"),
            Triple("Step 2: Guided Practice", "🥛🌀", "Pour in cold milk or yogurt, secure the lid tight, and whirl the blender!"),
            Triple("Step 3: Independent Try", "🥤💜✨", "Pour the creamy purple berry smoothie into a tall cup with a paper straw!")
        )
        "w24_chef_4" -> listOf(
            Triple("Step 1: Demonstrate", "🍞🥬", "Lay two slices of whole-grain bread on the cutting board and add crisp lettuce!"),
            Triple("Step 2: Guided Practice", "🧀🍅", "Layer cheddar cheese, juicy tomato slices, and crunchy cucumber circles!"),
            Triple("Step 3: Independent Try", "🥪🌟✨", "Place the top bread slice on and cut into two neat triangles!")
        )
        "w24_chef_5" -> listOf(
            Triple("Step 1: Demonstrate", "🥣🌾", "Scoop warm, cozy oatmeal into your favorite breakfast bowl!"),
            Triple("Step 2: Guided Practice", "🫐🍌", "Make a happy smiling face on top using blueberries for eyes and banana for a smile!"),
            Triple("Step 3: Independent Try", "🍯🌟✨", "Sprinkle cinnamon and enjoy a nutritious morning breakfast!")
        )
        "w24_chef_6" -> listOf(
            Triple("Step 1: Demonstrate", "🧺🏁", "Spread the red-and-white checkered blanket out in the sunny park!"),
            Triple("Step 2: Guided Practice", "🥪🍎", "Pack healthy sandwiches, crunchy apples, and cool water bottles into the basket!"),
            Triple("Step 3: Independent Try", "🧺☀️✨", "Close the picnic basket latches and head outside with your friends!")
        )

        // Nature & Science Step-by-Step Explorations
        "w14_nature_2" -> listOf(
            Triple("Step 1: Demonstrate", "🪴🌱", "Scoop rich dark garden soil into the pot and press a tiny seed inside!"),
            Triple("Step 2: Guided Practice", "💧☀️", "Water the soil gently and place the pot in warm golden sunlight!"),
            Triple("Step 3: Independent Try", "🌻🌿✨", "Watch green leaves uncurl and grow into a tall blooming plant!")
        )
        "w14_nature_6" -> listOf(
            Triple("Step 1: Demonstrate", "🍎🍂", "Collect apple cores and dry garden leaves in the green compost bucket!"),
            Triple("Step 2: Guided Practice", "♻️📦", "Sort clean paper, cardboard, and bottles into the blue recycling bin!"),
            Triple("Step 3: Independent Try", "🌍💚✨", "Turn the compost into rich garden soil to help Earth stay clean and green!")
        )
        "w14_nature_7" -> listOf(
            Triple("Step 1: Demonstrate", "🥚🐛", "A tiny egg on a green leaf hatches into a hungry munching caterpillar!"),
            Triple("Step 2: Guided Practice", "🐛🛡️", "The caterpillar wraps itself inside a cozy green chrysalis to rest and transform!"),
            Triple("Step 3: Independent Try", "🦋🌸✨", "Out emerges a colorful butterfly ready to flutter among the flowers!")
        )
        "w15_science_6" -> listOf(
            Triple("Step 1: Demonstrate", "🪨⛏️", "Tap the sandstone rock gently at the dig site to uncover hidden dinosaur fossils!"),
            Triple("Step 2: Guided Practice", "🖌️🦴", "Use your soft paleontologist brush to sweep sandy dust off the giant rib and leg bones!"),
            Triple("Step 3: Independent Try", "🦖🏛️✨", "Fit the fossil bones together to stand up a complete dinosaur skeleton!")
        )
        "w15_science_7" -> listOf(
            Triple("Step 1: Demonstrate", "☁️🌧️", "Warm sun lifts water vapor into fluffy clouds until cool raindrops fall to the pond!"),
            Triple("Step 2: Guided Practice", "🥚🐸", "In the pond, jelly frog eggs hatch into swimming tadpoles that grow strong back legs!"),
            Triple("Step 3: Independent Try", "🐸🪷✨", "The young frog hops onto a green lily pad as the water cycle begins again!")
        )
        else -> listOf(
            Triple("Step 1: Demonstrate", "${activity.emoji} 👀", activity.instructionText),
            Triple("Step 2: Guided Practice", "${activity.emoji} 🤲", activity.showMeDemoHint),
            Triple("Step 3: Independent Try", "${activity.emoji} 🌟", "Wonderful job completing ${activity.title} step by step!")
        )
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
                Text("👨‍👩‍👧 📸", fontSize = 52.sp)
                Text(
                    text = "Add at least 2 Family Photos to play Who's Who!",
                    style = MaterialTheme.typography.headlineSmall,
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
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.height(48.dp)
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
                text = if (quizMode) {
                    "Who's Who Quiz: Tap ${currentCard.label}!"
                } else {
                    "Card ${(cardIdx % cards.size) + 1} of ${cards.size}: ${currentCard.label}"
                },
                style = MaterialTheme.typography.headlineSmall,
                color = DeepSlateInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (cards.size >= 2) {
                Button(
                    onClick = { quizMode = !quizMode },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("flashcard_mode_toggle")
                ) {
                    Text(
                        text = if (quizMode) "📖 Browse Cards" else "🎯 Try Quiz",
                        color = PlaySurfaceWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (!quizMode) {
            // Large Browse Card with Hear Name, Previous, and Next
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { cardIdx = if (cardIdx > 0) cardIdx - 1 else cards.lastIndex },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCreamCard, contentColor = DeepSlateInk),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("flashcard_prev_button")
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
                        }
                        .testTag("flashcard_main_card"),
                    color = SoftCreamCard,
                    border = BorderStroke(3.dp, Color(currentCard.primaryColorHex)),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (currentCard.explanation.isNotEmpty() && File(currentCard.explanation).exists()) {
                            AsyncImage(
                                model = File(currentCard.explanation),
                                contentDescription = currentCard.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(22.dp))
                            )
                        } else if (currentCard.drawableRes != null) {
                            Image(
                                painter = painterResource(id = currentCard.drawableRes),
                                contentDescription = currentCard.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(22.dp))
                            )
                        } else {
                            Text(text = currentCard.emoji, fontSize = 84.sp)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = currentCard.label,
                                style = MaterialTheme.typography.displaySmall,
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
                                color = MutedSlateText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Button(
                                onClick = { viewModel.audio.speakInstruction(currentCard.spokenText) },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionListenAmber),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("flashcard_hear_name_button")
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
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("flashcard_next_button")
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
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                quizChoices.forEachIndexed { idx, option ->
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
                            }
                            .testTag("flashcard_quiz_choice_$idx"),
                        color = SoftCreamCard,
                        border = BorderStroke(3.dp, ActionContinueBlue),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = option.emoji, fontSize = 58.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.headlineSmall,
                                color = DeepSlateInk,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = option.categoryOrTarget,
                                fontSize = 12.sp,
                                color = MutedSlateText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/** A word is earned only after the child places every letter in order. */
@Composable
private fun CvcWordBuilder(
    activity: ActivityConfig,
    pool: List<GameItemOption>,
    step: Int,
    replayKey: Int,
    viewModel: WonderViewModel,
    onNext: (Int) -> Unit
) {
    val target = pool[step % pool.size]
    val letters = remember(target.id, replayKey) { target.label.toList().shuffled() }
    var assembled by remember(target.id, replayKey) { mutableStateOf("") }
    var retried by remember(target.id, replayKey) { mutableStateOf(false) }
    val complete = assembled == target.label
    Row(
        Modifier.fillMaxSize().testTag("engine_cvc_builder"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.weight(0.36f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Build ${target.label.lowercase()} ${target.emoji}", fontSize = 24.sp)
            Button(onClick = { viewModel.audio.speakInstruction(target.label.lowercase()) }) {
                Text("Hear the word")
            }
            if (complete) {
                Button(onClick = {
                    viewModel.saveActivityCheckpoint(activity, step + 1)
                    onNext(step + 1)
                }, modifier = Modifier.testTag("cvc_next")) { Text("Next word") }
            }
        }
        Column(
            Modifier.weight(0.64f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                target.label.indices.joinToString("  ") { assembled.getOrNull(it)?.toString() ?: "＿" },
                fontSize = 36.sp,
                modifier = Modifier.testTag("cvc_slots")
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                letters.forEachIndexed { index, letter ->
                    Button(
                        enabled = !complete,
                        modifier = Modifier.weight(1f).height(56.dp).testTag("cvc_letter_$index"),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            if (letter == target.label.getOrNull(assembled.length)) {
                                assembled += letter
                                if (assembled == target.label) {
                                    viewModel.onActivityMilestoneCompleted(
                                        activity, hadRetriesInRound = retried,
                                        speakMilestonePraise = false
                                    )
                                    viewModel.audio.speakInstruction(target.label.lowercase())
                                }
                            } else {
                                retried = true
                                viewModel.audio.speakInstruction("Try another letter for this space.")
                            }
                        }
                    ) { Text(letter.toString(), fontSize = 26.sp) }
                }
            }
        }
    }
}
