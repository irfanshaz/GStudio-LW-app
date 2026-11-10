package com.example.ui.engines

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

data class TracingStrokeSpec(
    val points: List<Offset>,
    val isClosed: Boolean = false,
    val requiresStrictIntermediateTurns: Boolean = false,
    val directionCue: String = "Start at the green star and follow the dots"
)

data class TracingGlyphSpec(
    val glyph: String,
    val wordHint: String,
    val catalogBadge: String,
    val strokes: List<TracingStrokeSpec>,
    val wordContext: String? = null,
    val letterIndexInWord: Int = 0,
    val totalLettersInWord: Int = 1
)

data class TracingEvaluationResult(
    val coverageRatio: Float,
    val orderedRatio: Float,
    val offPathRatio: Float,
    val startedNearStart: Boolean,
    val reachedNearFinish: Boolean,
    val isShortcutOrTapCluster: Boolean,
    val isReversed: Boolean,
    val strokePassed: Boolean,
    val accuracyPercent: Int,
    val feedbackMessage: String
) {
    val coveragePercent: Int get() = (coverageRatio.coerceIn(0f, 1f) * 100f).roundToInt()
}

object TracingStudioDeckRegistry {

    fun getCatalogPreviewBadge(activityId: String): String = when (activityId) {
        "w04_tracing_1" -> "A–H"
        "w04_tracing_2" -> "I–P"
        "w04_tracing_3" -> "Q–Z"
        "w04_tracing_4" -> "a–m"
        "w04_tracing_5" -> "n–z"
        "w04_tracing_6" -> "1–5"
        "w04_tracing_7" -> "6–10"
        "w04_tracing_8" -> "◯△☆"
        "w04_tracing_9" -> "｜―"
        "w04_tracing_10" -> "⌒〰"
        "w04_tracing_11" -> "⚡⛰"
        "w04_tracing_12" -> "CAT"
        else -> "ABC"
    }

    fun reconcileSavedStepIndex(savedStepIndex: Int, deckSize: Int): Int {
        if (deckSize <= 0) return 0
        if (savedStepIndex < 0) return 0
        if (savedStepIndex >= deckSize) return 0
        return savedStepIndex
    }

    fun getDeckForActivity(activityId: String): List<TracingGlyphSpec> {
        return when (activityId) {
            "w04_tracing_1" -> listOf(
                glyphSpec("A", "Apple"),
                glyphSpec("B", "Bear"),
                glyphSpec("C", "Cat"),
                glyphSpec("D", "Dolphin"),
                glyphSpec("E", "Elephant"),
                glyphSpec("F", "Fire Truck"),
                glyphSpec("G", "Giraffe"),
                glyphSpec("H", "Hat")
            )
            "w04_tracing_2" -> listOf(
                glyphSpec("I", "Igloo"),
                glyphSpec("J", "Jellyfish"),
                glyphSpec("K", "Kite"),
                glyphSpec("L", "Lion"),
                glyphSpec("M", "Moon"),
                glyphSpec("N", "Nest"),
                glyphSpec("O", "Owl"),
                glyphSpec("P", "Puppy")
            )
            "w04_tracing_3" -> listOf(
                glyphSpec("Q", "Queen"),
                glyphSpec("R", "Rainbow"),
                glyphSpec("S", "Sun"),
                glyphSpec("T", "Train"),
                glyphSpec("U", "Umbrella"),
                glyphSpec("V", "Violin"),
                glyphSpec("W", "Whale"),
                glyphSpec("X", "Xylophone"),
                glyphSpec("Y", "Yak"),
                glyphSpec("Z", "Zebra")
            )
            "w04_tracing_4" -> listOf(
                glyphSpec("a", "ant"),
                glyphSpec("b", "butterfly"),
                glyphSpec("c", "cup"),
                glyphSpec("d", "drum"),
                glyphSpec("e", "egg"),
                glyphSpec("f", "flower"),
                glyphSpec("g", "grapes"),
                glyphSpec("h", "honey"),
                glyphSpec("i", "insect"),
                glyphSpec("j", "jam"),
                glyphSpec("k", "kitten"),
                glyphSpec("l", "leaf"),
                glyphSpec("m", "muffin")
            )
            "w04_tracing_5" -> listOf(
                glyphSpec("n", "nest"),
                glyphSpec("o", "orange"),
                glyphSpec("p", "pear"),
                glyphSpec("q", "quilt"),
                glyphSpec("r", "rabbit"),
                glyphSpec("s", "star"),
                glyphSpec("t", "turtle"),
                glyphSpec("u", "umbrella"),
                glyphSpec("v", "van"),
                glyphSpec("w", "wagon"),
                glyphSpec("x", "xylophone"),
                glyphSpec("y", "yarn"),
                glyphSpec("z", "zebra")
            )
            "w04_tracing_6" -> listOf(
                glyphSpec("1", "One Sun"),
                glyphSpec("2", "Two Ducks"),
                glyphSpec("3", "Three Stars"),
                glyphSpec("4", "Four Wheels"),
                glyphSpec("5", "Five Petals")
            )
            "w04_tracing_7" -> listOf(
                glyphSpec("6", "Six Shells"),
                glyphSpec("7", "Seven Rainbow Colors"),
                glyphSpec("8", "Eight Octopus Arms"),
                glyphSpec("9", "Nine Balloons"),
                glyphSpec("10", "Ten Twinkling Stars")
            )
            "w04_tracing_8" -> listOf(
                glyphSpec("◯", "Circle"),
                glyphSpec("△", "Triangle"),
                glyphSpec("□", "Square"),
                glyphSpec("☆", "Star"),
                glyphSpec("♡", "Heart")
            )
            "w04_tracing_9" -> listOf(
                glyphSpec("｜", "Vertical Line"),
                glyphSpec("―", "Horizontal Line"),
                glyphSpec("┼", "Crossing Lines"),
                glyphSpec("∥", "Twin Tall Lines")
            )
            "w04_tracing_10" -> listOf(
                glyphSpec("⌒", "Rainbow Arch"),
                glyphSpec("◡", "Smiling Curve"),
                glyphSpec("〰", "Ocean Wave"),
                glyphSpec("〜", "Gentle Ripple")
            )
            "w04_tracing_11" -> listOf(
                glyphSpec("⚡", "Zig-Zag Path"),
                glyphSpec("⛰", "Mountain Peaks"),
                glyphSpec("〽", "Valley Zig-Zag")
            )
            "w04_tracing_12" -> {
                val words = listOf("CAT", "SUN", "DOG", "STAR")
                words.flatMap { word ->
                    word.mapIndexed { idx, ch ->
                        val base = glyphSpec(ch.toString(), "$word (${idx + 1}/${word.length})")
                        base.copy(
                            wordContext = word,
                            letterIndexInWord = idx,
                            totalLettersInWord = word.length
                        )
                    }
                }
            }
            "w15_science_8" -> listOf(
                glyphSpec("☆", "Star Constellation"),
                glyphSpec("△", "Summer Triangle Stars"),
                glyphSpec("⚡", "Cassiopeia W-Stars")
            )
            else -> listOf(
                glyphSpec("A", "Apple"),
                glyphSpec("B", "Bear"),
                glyphSpec("C", "Cat")
            )
        }
    }

    fun glyphSpec(glyph: String, wordHint: String): TracingGlyphSpec {
        val strokes = buildExplicitStrokesForGlyph(glyph)
        return TracingGlyphSpec(
            glyph = glyph,
            wordHint = wordHint,
            catalogBadge = glyph,
            strokes = strokes
        )
    }

    private fun linePts(x0: Float, y0: Float, x1: Float, y1: Float, steps: Int = 8): List<Offset> {
        return (0..steps).map { i ->
            val t = i.toFloat() / steps.toFloat()
            Offset(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t)
        }
    }

    private fun polyPts(
        p1: Offset,
        p2: Offset,
        p3: Offset? = null,
        p4: Offset? = null,
        p5: Offset? = null,
        p6: Offset? = null,
        p7: Offset? = null,
        p8: Offset? = null,
        p9: Offset? = null,
        p10: Offset? = null,
        p11: Offset? = null,
        p12: Offset? = null,
        stepsPerSegment: Int = 5
    ): List<Offset> {
        val waypoints = listOfNotNull(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
        if (waypoints.size < 2) return waypoints
        val out = mutableListOf<Offset>()
        for (i in 0 until waypoints.lastIndex) {
            val a = waypoints[i]
            val b = waypoints[i + 1]
            val seg = linePts(a.x, a.y, b.x, b.y, stepsPerSegment)
            if (out.isNotEmpty()) out.addAll(seg.drop(1)) else out.addAll(seg)
        }
        return out
    }

    private fun arcPts(
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        startDeg: Float,
        sweepDeg: Float,
        steps: Int = 12
    ): List<Offset> {
        return (0..steps).map { i ->
            val deg = startDeg + sweepDeg * (i.toFloat() / steps.toFloat())
            val rad = deg * (PI.toFloat() / 180f)
            Offset(cx + rx * cos(rad), cy + ry * sin(rad))
        }
    }

    /**
     * Explicit, non-fallback stroke templates for every uppercase letter (A-Z),
     * genuine lowercase letter (a-z), numeral (1-10), shape, line, wave, and zigzag.
     */
    fun buildExplicitStrokesForGlyph(glyph: String): List<TracingStrokeSpec> {
        return when (glyph) {
            // UPPERCASE A-Z
            "A" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.24f, 0.84f), directionCue = "Slide down the left side"),
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.76f, 0.84f), directionCue = "Slide down the right side"),
                TracingStrokeSpec(linePts(0.33f, 0.56f, 0.67f, 0.56f, 6), directionCue = "Cross the middle bridge")
            )
            "B" -> listOf(
                TracingStrokeSpec(linePts(0.30f, 0.16f, 0.30f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.30f, 0.16f), Offset(0.60f, 0.18f), Offset(0.68f, 0.33f), Offset(0.58f, 0.48f), Offset(0.30f, 0.50f)),
                    directionCue = "Curve around the top belly"
                ),
                TracingStrokeSpec(
                    polyPts(Offset(0.30f, 0.50f), Offset(0.62f, 0.52f), Offset(0.70f, 0.68f), Offset(0.60f, 0.82f), Offset(0.30f, 0.84f)),
                    directionCue = "Curve around the bottom belly"
                )
            )
            "C" -> listOf(
                TracingStrokeSpec(arcPts(0.54f, 0.50f, 0.28f, 0.34f, -45f, -270f, 14), directionCue = "Curve around like a crescent moon")
            )
            "D" -> listOf(
                TracingStrokeSpec(linePts(0.30f, 0.16f, 0.30f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(arcPts(0.30f, 0.50f, 0.40f, 0.34f, -90f, 180f, 12), directionCue = "Curve all the way from top to bottom")
            )
            "E" -> listOf(
                TracingStrokeSpec(linePts(0.30f, 0.16f, 0.30f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(linePts(0.30f, 0.16f, 0.72f, 0.16f, 6), directionCue = "Slide across the top"),
                TracingStrokeSpec(linePts(0.30f, 0.50f, 0.64f, 0.50f, 6), directionCue = "Slide across the middle"),
                TracingStrokeSpec(linePts(0.30f, 0.84f, 0.72f, 0.84f, 6), directionCue = "Slide across the bottom")
            )
            "F" -> listOf(
                TracingStrokeSpec(linePts(0.32f, 0.16f, 0.32f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(linePts(0.32f, 0.16f, 0.72f, 0.16f, 6), directionCue = "Slide across the top"),
                TracingStrokeSpec(linePts(0.32f, 0.50f, 0.64f, 0.50f, 6), directionCue = "Slide across the middle")
            )
            "G" -> listOf(
                TracingStrokeSpec(
                    arcPts(0.52f, 0.50f, 0.28f, 0.34f, -45f, -295f, 14) +
                        linePts(0.76f, 0.54f, 0.52f, 0.54f, 4).drop(1),
                    directionCue = "Curve around and tuck the shelf inside"
                )
            )
            "H" -> listOf(
                TracingStrokeSpec(linePts(0.28f, 0.16f, 0.28f, 0.84f), directionCue = "Draw the left pillar down"),
                TracingStrokeSpec(linePts(0.72f, 0.16f, 0.72f, 0.84f), directionCue = "Draw the right pillar down"),
                TracingStrokeSpec(linePts(0.28f, 0.50f, 0.72f, 0.50f, 6), directionCue = "Connect the middle bridge")
            )
            "I" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.50f, 0.84f), directionCue = "Draw the straight line down"),
                TracingStrokeSpec(linePts(0.34f, 0.16f, 0.66f, 0.16f, 5), directionCue = "Cross the top bar"),
                TracingStrokeSpec(linePts(0.34f, 0.84f, 0.66f, 0.84f, 5), directionCue = "Cross the bottom bar")
            )
            "J" -> listOf(
                TracingStrokeSpec(
                    linePts(0.58f, 0.16f, 0.58f, 0.66f, 7) +
                        arcPts(0.43f, 0.66f, 0.15f, 0.18f, 0f, 180f, 8).drop(1),
                    directionCue = "Slide down and curve the fishhook"
                ),
                TracingStrokeSpec(linePts(0.38f, 0.16f, 0.74f, 0.16f, 5), directionCue = "Cross the top hat")
            )
            "K" -> listOf(
                TracingStrokeSpec(linePts(0.30f, 0.16f, 0.30f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(linePts(0.70f, 0.18f, 0.30f, 0.52f, 6), directionCue = "Slide in to the middle"),
                TracingStrokeSpec(linePts(0.30f, 0.52f, 0.72f, 0.84f, 6), directionCue = "Kick down to the corner")
            )
            "L" -> listOf(
                TracingStrokeSpec(linePts(0.32f, 0.16f, 0.32f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(linePts(0.32f, 0.84f, 0.72f, 0.84f, 6), directionCue = "Slide across the bottom")
            )
            "M" -> listOf(
                TracingStrokeSpec(linePts(0.22f, 0.16f, 0.22f, 0.84f), directionCue = "Draw the left line down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.22f, 0.16f), Offset(0.50f, 0.58f), Offset(0.78f, 0.16f), Offset(0.78f, 0.84f)),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Slide down to the valley, up to the peak, and down"
                )
            )
            "N" -> listOf(
                TracingStrokeSpec(linePts(0.26f, 0.16f, 0.26f, 0.84f), directionCue = "Draw the left line down"),
                TracingStrokeSpec(linePts(0.26f, 0.16f, 0.74f, 0.84f), directionCue = "Slide diagonally down"),
                TracingStrokeSpec(linePts(0.74f, 0.16f, 0.74f, 0.84f), directionCue = "Draw the right line down")
            )
            "O" -> listOf(
                TracingStrokeSpec(
                    arcPts(0.50f, 0.50f, 0.28f, 0.34f, -90f, 360f, 16),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace all the way around the big oval"
                )
            )
            "P" -> listOf(
                TracingStrokeSpec(linePts(0.30f, 0.16f, 0.30f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.30f, 0.16f), Offset(0.62f, 0.18f), Offset(0.70f, 0.34f), Offset(0.62f, 0.50f), Offset(0.30f, 0.52f)),
                    directionCue = "Curve around the top loop"
                )
            )
            "Q" -> listOf(
                TracingStrokeSpec(
                    arcPts(0.50f, 0.48f, 0.27f, 0.32f, -90f, 360f, 16),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace all the way around the circle"
                ),
                TracingStrokeSpec(linePts(0.56f, 0.62f, 0.78f, 0.86f, 5), directionCue = "Add the little diagonal tail")
            )
            "R" -> listOf(
                TracingStrokeSpec(linePts(0.30f, 0.16f, 0.30f, 0.84f), directionCue = "Draw the tall line down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.30f, 0.16f), Offset(0.62f, 0.18f), Offset(0.68f, 0.34f), Offset(0.60f, 0.48f), Offset(0.30f, 0.50f)),
                    directionCue = "Curve around the top loop"
                ),
                TracingStrokeSpec(linePts(0.48f, 0.50f, 0.72f, 0.84f, 6), directionCue = "Kick the diagonal leg down")
            )
            "S" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.68f, 0.24f), Offset(0.50f, 0.16f), Offset(0.30f, 0.24f),
                        Offset(0.32f, 0.42f), Offset(0.50f, 0.50f), Offset(0.68f, 0.58f),
                        Offset(0.70f, 0.76f), Offset(0.50f, 0.84f), Offset(0.30f, 0.76f),
                        stepsPerSegment = 3
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Snake around the top curve and bottom curve"
                )
            )
            "T" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.50f, 0.84f), directionCue = "Draw the tall stem down"),
                TracingStrokeSpec(linePts(0.22f, 0.16f, 0.78f, 0.16f, 7), directionCue = "Slide across the top roof")
            )
            "U" -> listOf(
                TracingStrokeSpec(
                    linePts(0.28f, 0.16f, 0.28f, 0.62f, 5) +
                        arcPts(0.50f, 0.62f, 0.22f, 0.22f, 180f, -180f, 8).drop(1) +
                        linePts(0.72f, 0.62f, 0.72f, 0.16f, 5).drop(1),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Slide down, curve the cup, and go up"
                )
            )
            "V" -> listOf(
                TracingStrokeSpec(
                    polyPts(Offset(0.24f, 0.16f), Offset(0.50f, 0.84f), Offset(0.76f, 0.16f), stepsPerSegment = 7),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Slide down to the point and up"
                )
            )
            "W" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.18f, 0.16f), Offset(0.34f, 0.84f),
                        Offset(0.50f, 0.42f), Offset(0.66f, 0.84f),
                        Offset(0.82f, 0.16f),
                        stepsPerSegment = 5
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Down, up, down, up across the waves"
                )
            )
            "X" -> listOf(
                TracingStrokeSpec(linePts(0.26f, 0.16f, 0.74f, 0.84f), directionCue = "Slide diagonally down-right"),
                TracingStrokeSpec(linePts(0.74f, 0.16f, 0.26f, 0.84f), directionCue = "Criss-cross down-left")
            )
            "Y" -> listOf(
                TracingStrokeSpec(linePts(0.26f, 0.16f, 0.50f, 0.50f, 6), directionCue = "Slide down-right to the center"),
                TracingStrokeSpec(linePts(0.74f, 0.16f, 0.50f, 0.50f, 6), directionCue = "Slide down-left to the center"),
                TracingStrokeSpec(linePts(0.50f, 0.50f, 0.50f, 0.84f, 6), directionCue = "Draw the stem straight down")
            )
            "Z" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.26f, 0.18f), Offset(0.74f, 0.18f),
                        Offset(0.26f, 0.82f), Offset(0.74f, 0.82f),
                        stepsPerSegment = 6
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Across the top, zig-zag down, across the bottom"
                )
            )

            // GENUINE LOWERCASE a-z
            "a" -> listOf(
                TracingStrokeSpec(arcPts(0.46f, 0.62f, 0.18f, 0.18f, -30f, -310f, 12), directionCue = "Curve around the little round belly"),
                TracingStrokeSpec(linePts(0.64f, 0.42f, 0.64f, 0.82f, 6), directionCue = "Draw the short tail down")
            )
            "b" -> listOf(
                TracingStrokeSpec(linePts(0.34f, 0.16f, 0.34f, 0.82f, 8), directionCue = "Draw the tall stem down"),
                TracingStrokeSpec(arcPts(0.34f, 0.62f, 0.26f, 0.19f, -90f, 180f, 12), directionCue = "Bounce around the bottom belly")
            )
            "c" -> listOf(
                TracingStrokeSpec(arcPts(0.54f, 0.62f, 0.22f, 0.20f, -45f, -270f, 12), directionCue = "Curve the little c")
            )
            "d" -> listOf(
                TracingStrokeSpec(arcPts(0.46f, 0.62f, 0.20f, 0.19f, -35f, -290f, 12), directionCue = "Curve the round drum first"),
                TracingStrokeSpec(linePts(0.66f, 0.16f, 0.66f, 0.82f, 8), directionCue = "Draw the tall stick down")
            )
            "e" -> listOf(
                TracingStrokeSpec(
                    linePts(0.30f, 0.60f, 0.68f, 0.60f, 5) +
                        arcPts(0.49f, 0.60f, 0.19f, 0.20f, 0f, -315f, 12).drop(1),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Hit the ball across and curve up and around"
                )
            )
            "f" -> listOf(
                TracingStrokeSpec(
                    arcPts(0.58f, 0.30f, 0.12f, 0.14f, 0f, -180f, 6) +
                        linePts(0.46f, 0.30f, 0.46f, 0.84f, 7).drop(1),
                    directionCue = "Curve the candy cane hook and go down"
                ),
                TracingStrokeSpec(linePts(0.32f, 0.48f, 0.62f, 0.48f, 5), directionCue = "Cross the middle bar")
            )
            "g" -> listOf(
                TracingStrokeSpec(arcPts(0.46f, 0.56f, 0.18f, 0.16f, -35f, -300f, 12), directionCue = "Curve around the little circle"),
                TracingStrokeSpec(
                    linePts(0.64f, 0.40f, 0.64f, 0.78f, 6) +
                        arcPts(0.48f, 0.78f, 0.16f, 0.12f, 0f, 180f, 7).drop(1),
                    directionCue = "Drop down and curl the monkey tail"
                )
            )
            "h" -> listOf(
                TracingStrokeSpec(linePts(0.34f, 0.16f, 0.34f, 0.82f, 8), directionCue = "Draw the tall stem down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.34f, 0.54f), Offset(0.50f, 0.42f), Offset(0.66f, 0.52f), Offset(0.66f, 0.82f), stepsPerSegment = 4),
                    directionCue = "Bounce over the little hill"
                )
            )
            "i" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.42f, 0.50f, 0.82f, 6), directionCue = "Draw the short stem down"),
                TracingStrokeSpec(arcPts(0.50f, 0.24f, 0.04f, 0.04f, 0f, 360f, 6), directionCue = "Tap or trace the little dot on top")
            )
            "j" -> listOf(
                TracingStrokeSpec(
                    linePts(0.54f, 0.42f, 0.54f, 0.76f, 6) +
                        arcPts(0.42f, 0.76f, 0.12f, 0.12f, 0f, 180f, 6).drop(1),
                    directionCue = "Slide down and curl the hook"
                ),
                TracingStrokeSpec(arcPts(0.54f, 0.24f, 0.04f, 0.04f, 0f, 360f, 6), directionCue = "Add the dot on top")
            )
            "k" -> listOf(
                TracingStrokeSpec(linePts(0.34f, 0.16f, 0.34f, 0.82f, 8), directionCue = "Draw the tall stem down"),
                TracingStrokeSpec(linePts(0.64f, 0.44f, 0.34f, 0.62f, 5), directionCue = "Slide in to the stem"),
                TracingStrokeSpec(linePts(0.34f, 0.62f, 0.66f, 0.82f, 5), directionCue = "Kick out to the bottom")
            )
            "l" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.50f, 0.82f, 8), directionCue = "Draw the tall straight line down")
            )
            "m" -> listOf(
                TracingStrokeSpec(linePts(0.22f, 0.42f, 0.22f, 0.82f, 6), directionCue = "Draw the short stem down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.22f, 0.52f), Offset(0.36f, 0.42f), Offset(0.50f, 0.52f), Offset(0.50f, 0.82f), stepsPerSegment = 4),
                    directionCue = "Bounce over the first hill"
                ),
                TracingStrokeSpec(
                    polyPts(Offset(0.50f, 0.52f), Offset(0.64f, 0.42f), Offset(0.78f, 0.52f), Offset(0.78f, 0.82f), stepsPerSegment = 4),
                    directionCue = "Bounce over the second hill"
                )
            )
            "n" -> listOf(
                TracingStrokeSpec(linePts(0.32f, 0.42f, 0.32f, 0.82f, 6), directionCue = "Draw the short stem down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.32f, 0.52f), Offset(0.50f, 0.42f), Offset(0.68f, 0.52f), Offset(0.68f, 0.82f), stepsPerSegment = 4),
                    directionCue = "Bounce over the rainbow tunnel"
                )
            )
            "o" -> listOf(
                TracingStrokeSpec(
                    arcPts(0.50f, 0.62f, 0.20f, 0.20f, -90f, 360f, 14),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace around the little circle"
                )
            )
            "p" -> listOf(
                TracingStrokeSpec(linePts(0.34f, 0.42f, 0.34f, 0.90f, 7), directionCue = "Drop the stem down below the line"),
                TracingStrokeSpec(arcPts(0.34f, 0.58f, 0.26f, 0.17f, -90f, 180f, 10), directionCue = "Curve around the loop")
            )
            "q" -> listOf(
                TracingStrokeSpec(arcPts(0.46f, 0.58f, 0.18f, 0.17f, -35f, -300f, 10), directionCue = "Curve around the little loop"),
                TracingStrokeSpec(
                    polyPts(Offset(0.64f, 0.42f), Offset(0.64f, 0.88f), Offset(0.76f, 0.78f), stepsPerSegment = 4),
                    directionCue = "Drop down and flick the tail up"
                )
            )
            "r" -> listOf(
                TracingStrokeSpec(linePts(0.38f, 0.42f, 0.38f, 0.82f, 6), directionCue = "Draw the short stem down"),
                TracingStrokeSpec(
                    polyPts(Offset(0.38f, 0.54f), Offset(0.50f, 0.42f), Offset(0.66f, 0.46f), stepsPerSegment = 4),
                    directionCue = "Curve up and nod the little branch"
                )
            )
            "s" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.62f, 0.46f), Offset(0.48f, 0.40f), Offset(0.36f, 0.48f),
                        Offset(0.50f, 0.60f), Offset(0.64f, 0.70f), Offset(0.48f, 0.82f), Offset(0.36f, 0.76f),
                        stepsPerSegment = 3
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Wiggle around the little s curve"
                )
            )
            "t" -> listOf(
                TracingStrokeSpec(
                    polyPts(Offset(0.50f, 0.20f), Offset(0.50f, 0.76f), Offset(0.62f, 0.82f), stepsPerSegment = 5),
                    directionCue = "Slide down and curl the bottom tip"
                ),
                TracingStrokeSpec(linePts(0.32f, 0.44f, 0.68f, 0.44f, 5), directionCue = "Cross the middle bar")
            )
            "u" -> listOf(
                TracingStrokeSpec(
                    polyPts(Offset(0.32f, 0.42f), Offset(0.32f, 0.72f), Offset(0.50f, 0.82f), Offset(0.66f, 0.72f), Offset(0.66f, 0.42f), stepsPerSegment = 4),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Curve under the little cup"
                ),
                TracingStrokeSpec(linePts(0.66f, 0.42f, 0.66f, 0.82f, 5), directionCue = "Draw the short tail down")
            )
            "v" -> listOf(
                TracingStrokeSpec(
                    polyPts(Offset(0.30f, 0.42f), Offset(0.50f, 0.82f), Offset(0.70f, 0.42f), stepsPerSegment = 5),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Slant down and slant up"
                )
            )
            "w" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.22f, 0.42f), Offset(0.36f, 0.82f),
                        Offset(0.50f, 0.54f), Offset(0.64f, 0.82f),
                        Offset(0.78f, 0.42f),
                        stepsPerSegment = 4
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Down, up, down, up"
                )
            )
            "x" -> listOf(
                TracingStrokeSpec(linePts(0.32f, 0.42f, 0.68f, 0.82f, 6), directionCue = "Slide down-right"),
                TracingStrokeSpec(linePts(0.68f, 0.42f, 0.32f, 0.82f, 6), directionCue = "Cross down-left")
            )
            "y" -> listOf(
                TracingStrokeSpec(linePts(0.32f, 0.42f, 0.50f, 0.70f, 5), directionCue = "Slant down-right"),
                TracingStrokeSpec(linePts(0.68f, 0.42f, 0.36f, 0.90f, 7), directionCue = "Slant all the way down-left")
            )
            "z" -> listOf(
                TracingStrokeSpec(
                    polyPts(Offset(0.32f, 0.42f), Offset(0.68f, 0.42f), Offset(0.32f, 0.82f), Offset(0.68f, 0.82f), stepsPerSegment = 5),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Across, diagonal down, across"
                )
            )

            // NUMBERS 1 TO 10 (with 10 as separate 1 and 0 strokes)
            "1" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.50f, 0.84f, 8), directionCue = "Start at the top and go straight down")
            )
            "2" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.30f, 0.30f), Offset(0.48f, 0.16f), Offset(0.68f, 0.26f),
                        Offset(0.64f, 0.46f), Offset(0.28f, 0.82f), Offset(0.72f, 0.82f),
                        stepsPerSegment = 4
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Curve around the swan neck, slide down, and across"
                )
            )
            "3" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.30f, 0.22f), Offset(0.56f, 0.16f), Offset(0.68f, 0.34f), Offset(0.46f, 0.48f),
                        Offset(0.70f, 0.66f), Offset(0.54f, 0.84f), Offset(0.30f, 0.78f),
                        stepsPerSegment = 3
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Around the tree, around the tree, that's how we make a three"
                )
            )
            "4" -> listOf(
                TracingStrokeSpec(
                    polyPts(Offset(0.32f, 0.18f), Offset(0.28f, 0.54f), Offset(0.74f, 0.54f), stepsPerSegment = 5),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Down and across the L"
                ),
                TracingStrokeSpec(linePts(0.60f, 0.18f, 0.60f, 0.84f, 8), directionCue = "Draw the tall line straight down")
            )
            "5" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.32f, 0.18f), Offset(0.30f, 0.48f), Offset(0.58f, 0.46f),
                        Offset(0.70f, 0.64f), Offset(0.54f, 0.82f), Offset(0.30f, 0.78f),
                        stepsPerSegment = 4
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Short neck down and big round belly"
                ),
                TracingStrokeSpec(linePts(0.32f, 0.18f, 0.68f, 0.18f, 5), directionCue = "Put on the top hat")
            )
            "6" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.62f, 0.18f), Offset(0.34f, 0.46f), Offset(0.30f, 0.68f),
                        Offset(0.48f, 0.84f), Offset(0.68f, 0.70f), Offset(0.52f, 0.52f), Offset(0.32f, 0.60f),
                        stepsPerSegment = 3
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Slide down and curl into a loop"
                )
            )
            "7" -> listOf(
                TracingStrokeSpec(
                    polyPts(Offset(0.26f, 0.18f), Offset(0.74f, 0.18f), Offset(0.40f, 0.84f), stepsPerSegment = 6),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Slide across the sky and slant down"
                )
            )
            "8" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.50f, 0.16f), Offset(0.30f, 0.30f), Offset(0.50f, 0.48f),
                        Offset(0.70f, 0.66f), Offset(0.50f, 0.84f), Offset(0.30f, 0.66f),
                        Offset(0.50f, 0.48f), Offset(0.70f, 0.30f), Offset(0.50f, 0.16f),
                        stepsPerSegment = 3
                    ),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Make an S and loop back to the top"
                )
            )
            "9" -> listOf(
                TracingStrokeSpec(
                    arcPts(0.48f, 0.34f, 0.20f, 0.16f, 0f, -360f, 10) +
                        linePts(0.68f, 0.34f, 0.68f, 0.84f, 6).drop(1),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Make a loop on top, then a straight line down"
                )
            )
            "10" -> listOf(
                TracingStrokeSpec(linePts(0.28f, 0.18f, 0.28f, 0.82f, 8), directionCue = "First trace the number 1 down"),
                TracingStrokeSpec(
                    arcPts(0.64f, 0.50f, 0.20f, 0.32f, -90f, 360f, 14),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Lift your finger and trace the 0 oval"
                )
            )

            // SHAPES
            "◯" -> listOf(
                TracingStrokeSpec(
                    arcPts(0.50f, 0.50f, 0.32f, 0.32f, -90f, 360f, 16),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace all the way around the circle"
                )
            )
            "△" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.50f, 0.16f),
                        Offset(0.82f, 0.80f),
                        Offset(0.18f, 0.80f),
                        Offset(0.50f, 0.16f),
                        stepsPerSegment = 6
                    ),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace all three sides of the triangle"
                )
            )
            "□" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.22f, 0.22f),
                        Offset(0.78f, 0.22f),
                        Offset(0.78f, 0.78f),
                        Offset(0.22f, 0.78f),
                        Offset(0.22f, 0.22f),
                        stepsPerSegment = 5
                    ),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace all four sides of the square"
                )
            )
            "☆" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.50f, 0.14f),
                        Offset(0.61f, 0.38f),
                        Offset(0.86f, 0.38f),
                        Offset(0.66f, 0.55f),
                        Offset(0.74f, 0.82f),
                        Offset(0.50f, 0.66f),
                        Offset(0.26f, 0.82f),
                        Offset(0.34f, 0.55f),
                        Offset(0.14f, 0.38f),
                        Offset(0.39f, 0.38f),
                        Offset(0.50f, 0.14f),
                        stepsPerSegment = 2
                    ),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace around all five star points"
                )
            )
            "♡" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.50f, 0.30f),
                        Offset(0.35f, 0.16f),
                        Offset(0.18f, 0.28f),
                        Offset(0.22f, 0.50f),
                        Offset(0.50f, 0.82f),
                        Offset(0.78f, 0.50f),
                        Offset(0.82f, 0.28f),
                        Offset(0.65f, 0.16f),
                        Offset(0.50f, 0.30f),
                        stepsPerSegment = 3
                    ),
                    isClosed = true,
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Trace both heart curves down to the point and back"
                )
            )

            // PRE-WRITING STRAIGHT LINES
            "｜" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.14f, 0.50f, 0.86f, 10), directionCue = "Start at the top star and slide straight down")
            )
            "―" -> listOf(
                TracingStrokeSpec(linePts(0.14f, 0.50f, 0.86f, 0.50f, 10), directionCue = "Start on the left and slide straight across")
            )
            "┼" -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.50f, 0.84f, 8), directionCue = "First draw the vertical line down"),
                TracingStrokeSpec(linePts(0.18f, 0.50f, 0.82f, 0.50f, 8), directionCue = "Now draw the horizontal line across")
            )
            "∥" -> listOf(
                TracingStrokeSpec(linePts(0.34f, 0.16f, 0.34f, 0.84f, 8), directionCue = "Trace the left tall pillar"),
                TracingStrokeSpec(linePts(0.66f, 0.16f, 0.66f, 0.84f, 8), directionCue = "Trace the right tall pillar")
            )

            // RAINBOW CURVES & WAVES
            "⌒" -> listOf(
                TracingStrokeSpec(
                    (0..12).map { i ->
                        val t = i / 12f
                        Offset(0.16f + t * 0.68f, 0.72f - sin(t * PI.toFloat()) * 0.48f)
                    },
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Glide up and over the rainbow arch"
                )
            )
            "◡" -> listOf(
                TracingStrokeSpec(
                    (0..12).map { i ->
                        val t = i / 12f
                        Offset(0.16f + t * 0.68f, 0.28f + sin(t * PI.toFloat()) * 0.48f)
                    },
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Glide down and up the smiling curve"
                )
            )
            "〰" -> listOf(
                TracingStrokeSpec(
                    (0..16).map { i ->
                        val t = i / 16f
                        Offset(0.14f + t * 0.72f, 0.50f - sin(t * 2f * PI.toFloat()) * 0.26f)
                    },
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Ride the rolling ocean wave up and down"
                )
            )
            "〜" -> listOf(
                TracingStrokeSpec(
                    (0..16).map { i ->
                        val t = i / 16f
                        Offset(0.14f + t * 0.72f, 0.50f + sin(t * 2f * PI.toFloat()) * 0.22f)
                    },
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Follow the gentle river ripple"
                )
            )

            // ZIG-ZAG MOUNTAIN PATHS
            "⚡" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.16f, 0.76f),
                        Offset(0.38f, 0.22f),
                        Offset(0.62f, 0.76f),
                        Offset(0.84f, 0.22f),
                        stepsPerSegment = 5
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Climb up, slide down, and climb up every zig-zag peak"
                )
            )
            "⛰" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.12f, 0.78f),
                        Offset(0.30f, 0.24f),
                        Offset(0.50f, 0.78f),
                        Offset(0.70f, 0.24f),
                        Offset(0.88f, 0.78f),
                        stepsPerSegment = 4
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Follow both mountain peaks from start to finish"
                )
            )
            "〽" -> listOf(
                TracingStrokeSpec(
                    polyPts(
                        Offset(0.14f, 0.24f),
                        Offset(0.38f, 0.78f),
                        Offset(0.62f, 0.24f),
                        Offset(0.86f, 0.78f),
                        stepsPerSegment = 5
                    ),
                    requiresStrictIntermediateTurns = true,
                    directionCue = "Slide down into the valleys and up the hills"
                )
            )

            else -> listOf(
                TracingStrokeSpec(linePts(0.50f, 0.16f, 0.50f, 0.84f, 8))
            )
        }
    }

    /**
     * Maps normalized (0..1) glyph coordinates into a centered aspect-ratio-safe square
     * inside the measured canvas so circles/squares never stretch into wide ovals.
     */
    fun mapNormalizedToCanvas(normPt: Offset, canvasWidth: Float, canvasHeight: Float): Offset {
        val w = canvasWidth.coerceAtLeast(1f)
        val h = canvasHeight.coerceAtLeast(1f)
        val boxSide = min(w, h) * 0.88f
        val left = (w - boxSide) * 0.5f
        val top = (h - boxSide) * 0.5f
        return Offset(
            x = left + normPt.x * boxSide,
            y = top + normPt.y * boxSide
        )
    }

    /**
     * Resamples raw user strokes (each sublist is one finger-down to finger-up segment,
     * preserving pen lifts!) into dense points for accurate fast-drag evaluation.
     */
    fun resampleStrokeSegments(segments: List<List<Offset>>, stepPx: Float = 8f): List<Offset> {
        val out = mutableListOf<Offset>()
        segments.forEach { seg ->
            if (seg.size == 1) {
                out.add(seg.first())
            } else if (seg.size >= 2) {
                out.add(seg.first())
                for (i in 0 until seg.lastIndex) {
                    val a = seg[i]
                    val b = seg[i + 1]
                    val dist = hypot(b.x - a.x, b.y - a.y)
                    val steps = max(1, (dist / stepPx).toInt())
                    for (s in 1..steps) {
                        val t = s.toFloat() / steps.toFloat()
                        out.add(Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t))
                    }
                }
            }
        }
        return out
    }

    /**
     * Evaluates a user's stroke attempt against the active [TracingStrokeSpec] in pixel space.
     * Both the UI meter and the status feedback read from this single returned [TracingEvaluationResult].
     */
    internal fun distanceToGuide(point: Offset, guide: List<Offset>): Float {
        if (guide.size == 1) return (point - guide.first()).getDistance()
        return guide.zipWithNext().minOf { (a, b) ->
            val delta = b - a
            val lengthSquared = delta.x * delta.x + delta.y * delta.y
            val t = if (lengthSquared == 0f) 0f else
                (((point.x - a.x) * delta.x + (point.y - a.y) * delta.y) / lengthSquared).coerceIn(0f, 1f)
            (point - (a + delta * t)).getDistance()
        }
    }

    fun evaluateStrokeAttempt(
        strokeSpec: TracingStrokeSpec,
        checkpointPixels: List<Offset>,
        rawSegments: List<List<Offset>>,
        tolerancePx: Float,
        requiredCoverageRatio: Float,
        isToddlerBand: Boolean
    ): TracingEvaluationResult {
        if (checkpointPixels.isEmpty()) {
            return TracingEvaluationResult(0f, 0f, 0f, false, false, false, false, false, 0, "Start here ⭐")
        }
        val resampled = resampleStrokeSegments(rawSegments, stepPx = max(4f, tolerancePx * 0.18f))
        if (resampled.isEmpty()) {
            return TracingEvaluationResult(
                coverageRatio = 0f,
                orderedRatio = 0f,
                offPathRatio = 0f,
                startedNearStart = false,
                reachedNearFinish = false,
                isShortcutOrTapCluster = false,
                isReversed = false,
                strokePassed = false,
                accuracyPercent = 0,
                feedbackMessage = "Start here ⭐ and follow the dots!"
            )
        }

        val hitRadius = tolerancePx
        val offPathRadius = hitRadius

        // 1. Check for single tap or tiny cluster
        val minX = resampled.minOf { it.x }
        val maxX = resampled.maxOf { it.x }
        val minY = resampled.minOf { it.y }
        val maxY = resampled.maxOf { it.y }
        val userSpan = hypot(maxX - minX, maxY - minY)

        val cpMinX = checkpointPixels.minOf { it.x }
        val cpMaxX = checkpointPixels.maxOf { it.x }
        val cpMinY = checkpointPixels.minOf { it.y }
        val cpMaxY = checkpointPixels.maxOf { it.y }
        val templateSpan = hypot(cpMaxX - cpMinX, cpMaxY - cpMinY)

        val isTinyDotTemplate = templateSpan < hitRadius * 0.75f
        val isTapCluster = !isTinyDotTemplate && userSpan < templateSpan * 0.25f

        // 2. Compute unordered checkpoint coverage
        val coveredFlags = BooleanArray(checkpointPixels.size) { idx ->
            val cp = checkpointPixels[idx]
            resampled.any { up -> hypot(up.x - cp.x, up.y - cp.y) <= hitRadius }
        }
        val coveredCount = coveredFlags.count { it }
        val rawCoverageRatio = coveredCount.toFloat() / checkpointPixels.size.toFloat()

        // 3. Compute ordered checkpoint progress (monotonically advancing index along resampled path)
        var orderedIdx = 0
        for (up in resampled) {
            if (orderedIdx < checkpointPixels.size) {
                val cp = checkpointPixels[orderedIdx]
                if (hypot(up.x - cp.x, up.y - cp.y) <= hitRadius * 1.15f) {
                    orderedIdx += 1
                } else if (orderedIdx + 1 < checkpointPixels.size &&
                    hypot(up.x - checkpointPixels[orderedIdx + 1].x, up.y - checkpointPixels[orderedIdx + 1].y) <= hitRadius
                ) {
                    orderedIdx += 2
                }
            }
        }
        val orderedRatio = orderedIdx.toFloat() / checkpointPixels.size.toFloat()

        // 4. Check reverse direction (e.g., starting near finish and ending near start on open strokes)
        val firstUserPt = resampled.first()
        val lastUserPt = resampled.last()
        val startCp = checkpointPixels.first()
        val endCp = checkpointPixels.last()
        val startedNearStart = hypot(firstUserPt.x - startCp.x, firstUserPt.y - startCp.y) <= hitRadius * 1.45f
        val reachedNearFinish = hypot(lastUserPt.x - endCp.x, lastUserPt.y - endCp.y) <= hitRadius * 1.45f ||
            coveredFlags.last()

        val startedNearEnd = !strokeSpec.isClosed && !isTinyDotTemplate &&
            hypot(firstUserPt.x - endCp.x, firstUserPt.y - endCp.y) < hypot(firstUserPt.x - startCp.x, firstUserPt.y - startCp.y) * 0.65f
        val endedNearStart = !strokeSpec.isClosed && !isTinyDotTemplate &&
            hypot(lastUserPt.x - startCp.x, lastUserPt.y - startCp.y) < hypot(lastUserPt.x - endCp.x, lastUserPt.y - endCp.y) * 0.65f
        val isReversed = startedNearEnd && endedNearStart && !startedNearStart

        // 5. Off-path ratio (detect random scribbling or cutting straight across a closed shape/zigzag)
        val offPathCount = resampled.count { up ->
            distanceToGuide(up, checkpointPixels) > offPathRadius
        }
        val offPathRatio = offPathCount.toFloat() / resampled.size.toFloat()

        // 6. Check if a user cut straight across a closed shape or multi-turn path
        val middleCheckpoints = if (checkpointPixels.size >= 5) {
            checkpointPixels.subList(1, checkpointPixels.size - 1)
        } else checkpointPixels
        val middleCoveredRatio = middleCheckpoints.count { cp ->
            resampled.any { up -> hypot(up.x - cp.x, up.y - cp.y) <= hitRadius }
        }.toFloat() / middleCheckpoints.size.toFloat()

        val isShortcutAcrossShape = strokeSpec.requiresStrictIntermediateTurns &&
            (middleCoveredRatio < requiredCoverageRatio || orderedRatio < requiredCoverageRatio * 0.85f)

        // Effective coverage combines raw coverage with ordered progress and penalizes tap clusters/shortcuts
        val effectiveCoverage = when {
            isTapCluster -> min(0.18f, rawCoverageRatio * 0.25f)
            isReversed -> min(0.30f, orderedRatio)
            isShortcutAcrossShape -> min(middleCoveredRatio, orderedRatio)
            else -> min(rawCoverageRatio, max(orderedRatio, rawCoverageRatio * 0.92f))
        }.coerceIn(0f, 1f)

        val coveragePct = (effectiveCoverage * 100f).roundToInt()
        val scribbleRejected = offPathRatio > if (isToddlerBand) 0.24f else 0.12f

        val guideLength = checkpointPixels.zipWithNext().sumOf { (a, b) ->
            (b - a).getDistance().toDouble()
        }.toFloat()
        val drawnLength = rawSegments.sumOf { segment ->
            segment.zipWithNext().sumOf { (a, b) -> (b - a).getDistance().toDouble() }
        }.toFloat()
        val hasContinuousTravel = isTinyDotTemplate || drawnLength >= guideLength * if (isToddlerBand) 0.65f else 0.80f

        val passed = hasContinuousTravel && !isTapCluster &&
            !isReversed &&
            !scribbleRejected &&
            !isShortcutAcrossShape &&
            startedNearStart &&
            reachedNearFinish &&
            effectiveCoverage >= if (isToddlerBand) 0.68f else max(requiredCoverageRatio, 0.85f)

        val travelRatio = if (isTinyDotTemplate) 1f else (drawnLength / guideLength).coerceIn(0f, 1f)
        val measuredAccuracy = (effectiveCoverage * (1f - offPathRatio) * travelRatio * 100f).roundToInt()
        val accuracyPct = if (passed && offPathCount == 0 && coveredCount == checkpointPixels.size) {
            measuredAccuracy
        } else {
            measuredAccuracy.coerceAtMost(99)
        }

        val message = when {
            passed -> "🌟 Great tracing! ($coveragePct%)"
            scribbleRejected -> if (isToddlerBand) "Try inside the wide trail." else "Stay on the dots! ($coveragePct%)"
            isReversed -> "Start at the green star ⭐! ($coveragePct%)"
            !startedNearStart -> "Start here at the green star ⭐! ($coveragePct%)"
            !hasContinuousTravel || isTapCluster -> "Slide your finger along the dots! ($coveragePct%)"
            isShortcutAcrossShape -> "Follow all the turns along the dots! ($coveragePct%)"
            else -> "Keep going to the finish dot! ($coveragePct%)"
        }

        return TracingEvaluationResult(
            coverageRatio = effectiveCoverage,
            orderedRatio = orderedRatio,
            offPathRatio = offPathRatio,
            startedNearStart = startedNearStart,
            reachedNearFinish = reachedNearFinish,
            isShortcutOrTapCluster = isTapCluster || isShortcutAcrossShape,
            isReversed = isReversed,
            strokePassed = passed,
            accuracyPercent = accuracyPct,
            feedbackMessage = message
        )
    }
}
