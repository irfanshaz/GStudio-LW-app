package com.example.ui.engines

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdaptiveChallengeParameters
import com.example.model.ActivityConfig
import com.example.model.AgeBand
import com.example.model.LearningBuddy
import com.example.ui.theme.ActionContinueBlue
import com.example.ui.theme.ActionListenAmber
import com.example.ui.theme.ActionPlayGreen
import com.example.ui.theme.DeepSlateInk
import com.example.ui.theme.PlaySurfaceWhite
import com.example.ui.theme.SoftCreamCard
import com.example.viewmodel.WonderViewModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * ============================================================================================
 * LITTLE WONDER WORLD — BALLOON POP LEARNING ADVENTURE MODULE
 * ============================================================================================
 * Picture-First, Toddler-Friendly Design (Ages 2–5) with:
 * - 9 Interactive Worlds (Farm, Jungle, Arctic, Underwater, Dinosaur, Space, Candy & Rainbow,
 *   Garden & Butterfly, City & Transport Road)
 * - 13 Selectable Learning Modes (Icon-first selector strip)
 * - 3 Distinctive LWW Enhancements:
 *   1. Animal Surprise Balloons: Special surprise balloons reveal an animated moving animal
 *      that hops across the scene, makes its signature sound, and introduces itself.
 *   2. Smart Learning Balloons: Tracks mastered vs. unmastered concepts and gradually
 *      introduces new letters, numbers, and shapes while revisiting unmastered items.
 *   3. Magical Balloon Adventures: Every few pops unlocks a magical animated scene surprise
 *      (Rainbow Arch, Parachuting Buddy, Butterfly Parade, or Collectible Sticker).
 * - 3 Languages (English, Telugu, Hindi) with authentic curriculum audio
 * - 60 FPS Frame-Time 2D Balloon Physics & Lifecycle Engine:
 *   SPAWNING -> FLOATING -> POPPING -> REVEALING -> COMPLETED
 */

enum class BalloonLifecycleState {
    SPAWNING,
    FLOATING,
    POPPING,
    REVEALING,
    COMPLETED
}

enum class BalloonNarrationLanguage(
    val code: String,
    val shortBadge: String,
    val displayName: String
) {
    ENGLISH("en", "EN", "English"),
    TELUGU("te", "తె", "తెలుగు"),
    HINDI("hi", "हि", "हिन्दी")
}

enum class MagicalAdventureSurprise(
    val title: String,
    val bannerEmoji: String,
    val spokenCelebration: String
) {
    RAINBOW_ARCH("Rainbow Surprise!", "🌈✨", "Look! A magical rainbow appeared in the sky!"),
    PARACHUTE_BUDDY("Buddy Parachute!", "🪂🦁", "Hooray! Our buddy is floating down on a starlight parachute!"),
    BUTTERFLY_PARADE("Butterfly Parade!", "🦋🌸", "Magical butterflies are dancing across the world!"),
    STICKER_UNLOCKED("New World Sticker!", "🏅🌟", "Wonderful popping! You unlocked a shiny sticker!")
}

enum class BalloonWorldEnvironment(
    val id: String,
    val title: String,
    val emoji: String,
    val subtitle: String,
    val skyTopHex: Long,
    val skyBottomHex: Long,
    val hillFarHex: Long,
    val hillNearHex: Long,
    val accentHex: Long,
    val usesBubbleStyle: Boolean = false,
    val lowGravityDrift: Boolean = false,
    val hasWindingRoad: Boolean = true,
    val badgeTitle: String
) {
    FARM_ADVENTURE(
        id = "world_farm",
        title = "Farm",
        emoji = "🚜",
        subtitle = "Green fields, red barn, wooden fences & friendly farm animals",
        skyTopHex = 0xFF7DD3FC,
        skyBottomHex = 0xFFE0F2FE,
        hillFarHex = 0xFF86EFAC,
        hillNearHex = 0xFF4ADE80,
        accentHex = 0xFFDC2626,
        hasWindingRoad = true,
        badgeTitle = "Farm Sticker"
    ),
    JUNGLE_ADVENTURE(
        id = "world_jungle",
        title = "Jungle",
        emoji = "🌴",
        subtitle = "Tropical trees, hanging vines, waterfalls & safari friends",
        skyTopHex = 0xFF6EE7B7,
        skyBottomHex = 0xFFD1FAE5,
        hillFarHex = 0xFF10B981,
        hillNearHex = 0xFF059669,
        accentHex = 0xFFF59E0B,
        hasWindingRoad = true,
        badgeTitle = "Jungle Sticker"
    ),
    ARCTIC_ADVENTURE(
        id = "world_arctic",
        title = "Arctic",
        emoji = "🐧",
        subtitle = "Snowy peaks, sparkling icebergs, igloos & polar pals",
        skyTopHex = 0xFF93C5FD,
        skyBottomHex = 0xFFEFF6FF,
        hillFarHex = 0xFFDBEAFE,
        hillNearHex = 0xFFBFDBFE,
        accentHex = 0xFF0284C7,
        hasWindingRoad = true,
        badgeTitle = "Arctic Sticker"
    ),
    UNDERWATER_ADVENTURE(
        id = "world_underwater",
        title = "Ocean",
        emoji = "🐬",
        subtitle = "Coral reefs, sea plants, sunlight rays & rising ocean bubbles",
        skyTopHex = 0xFF0284C7,
        skyBottomHex = 0xFF38BDF8,
        hillFarHex = 0xFF0369A1,
        hillNearHex = 0xFF0E7490,
        accentHex = 0xFF06B6D4,
        usesBubbleStyle = true,
        hasWindingRoad = false,
        badgeTitle = "Ocean Sticker"
    ),
    DINOSAUR_ADVENTURE(
        id = "world_dinosaur",
        title = "Dinos",
        emoji = "🦕",
        subtitle = "Prehistoric ferns, ancient mountains & gentle dinosaurs",
        skyTopHex = 0xFFFDE68A,
        skyBottomHex = 0xFFFEF3C7,
        hillFarHex = 0xFF65A30D,
        hillNearHex = 0xFF4D7C0F,
        accentHex = 0xFFD97706,
        hasWindingRoad = true,
        badgeTitle = "Dino Sticker"
    ),
    SPACE_ADVENTURE(
        id = "world_space",
        title = "Space",
        emoji = "🚀",
        subtitle = "Twinkling stars, colorful nebulae, planets & low-gravity float",
        skyTopHex = 0xFF1E1B4B,
        skyBottomHex = 0xFF312E81,
        hillFarHex = 0xFF4338CA,
        hillNearHex = 0xFF3730A3,
        accentHex = 0xFFFACC15,
        lowGravityDrift = true,
        hasWindingRoad = false,
        badgeTitle = "Space Sticker"
    ),
    CANDY_RAINBOW_LAND(
        id = "world_candy",
        title = "Rainbow",
        emoji = "🌈",
        subtitle = "Rainbow bridges, cotton-candy clouds & magical pastel hills",
        skyTopHex = 0xFFFBCFE8,
        skyBottomHex = 0xFFFDF2F8,
        hillFarHex = 0xFFF9A8D4,
        hillNearHex = 0xFFF472B6,
        accentHex = 0xFFEC4899,
        hasWindingRoad = true,
        badgeTitle = "Rainbow Sticker"
    ),
    GARDEN_BUTTERFLY_WORLD(
        id = "world_garden",
        title = "Garden",
        emoji = "🦋",
        subtitle = "Blooming flowers, fluttering butterflies, bees & ladybugs",
        skyTopHex = 0xFFBAE6FD,
        skyBottomHex = 0xFFF0FDF4,
        hillFarHex = 0xFF4ADE80,
        hillNearHex = 0xFF22C55E,
        accentHex = 0xFF8B5CF6,
        hasWindingRoad = true,
        badgeTitle = "Garden Sticker"
    ),
    CITY_TRANSPORT_WORLD(
        id = "world_city",
        title = "Vehicles",
        emoji = "🚒",
        subtitle = "Winding town roads, traffic lights, parks & zooming vehicles",
        skyTopHex = 0xFF93C5FD,
        skyBottomHex = 0xFFE0F2FE,
        hillFarHex = 0xFF64748B,
        hillNearHex = 0xFF34D399,
        accentHex = 0xFF2563EB,
        hasWindingRoad = true,
        badgeTitle = "City Sticker"
    )
}

enum class BalloonLearningMode(
    val id: String,
    val title: String,
    val shortLabel: String,
    val emoji: String,
    val objective: String
) {
    ALPHABET_POP("mode_alphabet", "Alphabet Pop", "ABC", "🔤", "A–Z letter recognition & example words"),
    PHONICS_POP("mode_phonics", "Phonics Pop", "Sounds", "🗣️", "Letter sounds and gentle phonics"),
    NUMBER_POP("mode_numbers", "Number Pop", "123", "🔢", "Numbers 1–20 with visual object counting"),
    SHAPE_POP("mode_shapes", "Shape Pop", "Shapes", "⭐", "Circle, Square, Triangle, Star, Heart & Diamond"),
    COLOR_POP("mode_colors", "Color Pop", "Colors", "🎨", "Recognize and name bright colors"),
    ANIMAL_POP("mode_animals", "Animal Pop", "Animals", "🦁", "Identify animals and their friendly sounds"),
    FRUIT_POP("mode_fruits", "Fruit Pop", "Fruits", "🍎", "Recognize sweet orchard fruits"),
    VEGETABLE_POP("mode_vegetables", "Vegetable Pop", "Veggies", "🥕", "Recognize healthy garden vegetables"),
    VEHICLE_POP("mode_vehicles", "Vehicle Pop", "Cars", "🚂", "Identify road, rail, sea & sky vehicles"),
    COUNTING_POP("mode_counting", "Counting Pop", "Count", "🧮", "Count objects one-by-one as balloons pop"),
    MATCHING_POP("mode_matching", "Matching Pop", "Match", "🧩", "Find the balloon that matches the target picture"),
    WORD_POP("mode_words", "Word Pop", "Words", "📖", "Associate simple sight words with pictures"),
    MIXED_ADVENTURE("mode_mixed", "Mixed Adventure", "Mix", "🎡", "Combine letters, numbers, animals & colors")
}

/**
 * Multi-Level Progression Stages across all 13 Balloon Pop modes:
 * - [DISCOVERY_EXPLORE]: Level 1 — Pop every item in the active deck once (e.g., all 26 letters A-Z
 *   or all 20 numbers in the active tier) with zero repeats.
 * - [VOICE_RANDOM_CHALLENGE]: Level 2+ — Voice-Guided Random Challenge where 15 randomly selected &
 *   freshly shuffled targets are announced one-by-one by the Warm Preschool Teacher. Only the
 *   matching target balloon pops; tapping a distractor gently wobbles it and repeats the voice clue.
 */
enum class BalloonStageType(
    val id: String,
    val shortBadge: String,
    val displayTitle: String
) {
    DISCOVERY_EXPLORE("stage_explore", "Lv1 Explore", "Level 1 • Explore All"),
    VOICE_RANDOM_CHALLENGE("stage_voice_challenge", "Lv2 🎯 Quiz", "Level 2 • Voice Challenge")
}

/**
 * Progressive Number & Counting Tiers (1–20, 21–40, 41–60, 61–100).
 * Once a child identifies all numbers in a tier and completes its random challenge,
 * they advance to the next number tier.
 */
enum class BalloonNumberTier(
    val id: String,
    val rangeLabel: String,
    val startNumber: Int,
    val endNumber: Int
) {
    TIER_1_20("tier_1_20", "1–20", 1, 20),
    TIER_21_40("tier_21_40", "21–40", 21, 40),
    TIER_41_60("tier_41_60", "41–60", 41, 60),
    TIER_61_100("tier_61_100", "61–100", 61, 100);

    fun nextTier(): BalloonNumberTier? = when (this) {
        TIER_1_20 -> TIER_21_40
        TIER_21_40 -> TIER_41_60
        TIER_41_60 -> TIER_61_100
        TIER_61_100 -> null
    }
}

data class BalloonLearningPayload(
    val id: String,
    val symbolOrBadge: String,
    val emoji: String,
    val revealIllustrationEmoji: String,
    val revealTitleEn: String,
    val spokenEn: String,
    val revealTitleTe: String,
    val spokenTe: String,
    val phoneticTeFallback: String,
    val revealTitleHi: String,
    val spokenHi: String,
    val phoneticHiFallback: String,
    val balloonColorHex: Long,
    val countQuantity: Int = 1,
    val categoryTag: String = "",
    val isAnimalSurprise: Boolean = false
) {
    fun getDisplayTitle(lang: BalloonNarrationLanguage): String = when (lang) {
        BalloonNarrationLanguage.ENGLISH -> revealTitleEn
        BalloonNarrationLanguage.TELUGU -> revealTitleTe
        BalloonNarrationLanguage.HINDI -> revealTitleHi
    }

    fun getSpokenScript(lang: BalloonNarrationLanguage): String = when (lang) {
        BalloonNarrationLanguage.ENGLISH -> spokenEn
        BalloonNarrationLanguage.TELUGU -> spokenTe
        BalloonNarrationLanguage.HINDI -> spokenHi
    }

    fun getPhoneticFallback(lang: BalloonNarrationLanguage): String = when (lang) {
        BalloonNarrationLanguage.ENGLISH -> spokenEn
        BalloonNarrationLanguage.TELUGU -> phoneticTeFallback
        BalloonNarrationLanguage.HINDI -> phoneticHiFallback
    }
}

data class ActiveBalloonEntity(
    val instanceId: Int,
    val slotIndex: Int,
    var baseXRatio: Float,
    var xRatio: Float,
    var yRatio: Float,
    var roadProgress: Float,
    var verticalSpeedRatio: Float,
    var driftAmplitudeRatio: Float,
    var driftFrequency: Float,
    var phaseOffset: Float,
    var rotationDeg: Float,
    var scale: Float,
    var state: BalloonLifecycleState,
    var stateTimerSec: Float,
    var payload: BalloonLearningPayload,
    val hasAnimalSurpriseBadge: Boolean = false,
    var wobbleTimerSec: Float = 0f
)

data class PopBurstParticle(
    val angleRad: Float,
    val speedRatio: Float,
    val colorHex: Long,
    val radiusDp: Float
)

data class ActivePopCelebration(
    val id: Int,
    val xRatio: Float,
    val yRatio: Float,
    val payload: BalloonLearningPayload,
    val particles: List<PopBurstParticle>,
    var elapsedSec: Float = 0f
)

/**
 * ============================================================================================
 * MODULE 1: BalloonPhysics
 * ============================================================================================
 */
object BalloonPhysics {

    fun evaluateRoadCurve(t: Float, widthPx: Float, heightPx: Float): Offset {
        val u = (1f - t).coerceIn(0f, 1f)
        val tt = t.coerceIn(0f, 1f)
        val p0x = widthPx * 0.06f
        val p0y = heightPx * 0.82f
        val p1x = widthPx * 0.34f
        val p1y = heightPx * 0.28f
        val p2x = widthPx * 0.66f
        val p2y = heightPx * 0.90f
        val p3x = widthPx * 0.93f
        val p3y = heightPx * 0.36f

        val x = (u * u * u * p0x) +
            (3f * u * u * tt * p1x) +
            (3f * u * tt * tt * p2x) +
            (tt * tt * tt * p3x)

        val y = (u * u * u * p0y) +
            (3f * u * u * tt * p1y) +
            (3f * u * tt * tt * p2y) +
            (tt * tt * tt * p3y)

        return Offset(x, y)
    }

    fun stepBalloon(
        balloon: ActiveBalloonEntity,
        deltaTimeSec: Float,
        totalElapsedSec: Float,
        followRoadStream: Boolean,
        lowGravity: Boolean,
        reducedMotion: Boolean
    ) {
        val dt = deltaTimeSec.coerceIn(0.001f, 0.05f)
        balloon.stateTimerSec += dt
        if (balloon.wobbleTimerSec > 0f) {
            balloon.wobbleTimerSec = (balloon.wobbleTimerSec - dt).coerceAtLeast(0f)
        }

        when (balloon.state) {
            BalloonLifecycleState.SPAWNING -> {
                balloon.scale = (0.35f + (balloon.stateTimerSec / 0.25f) * 0.65f).coerceAtMost(1f)
                if (balloon.stateTimerSec >= 0.25f) {
                    balloon.scale = 1f
                    balloon.state = BalloonLifecycleState.FLOATING
                    balloon.stateTimerSec = 0f
                }
                advancePosition(balloon, dt, totalElapsedSec, followRoadStream, lowGravity, reducedMotion)
            }
            BalloonLifecycleState.FLOATING -> {
                balloon.scale = 1f
                advancePosition(balloon, dt, totalElapsedSec, followRoadStream, lowGravity, reducedMotion)
            }
            BalloonLifecycleState.POPPING -> {
                balloon.scale = 1.0f + (balloon.stateTimerSec / 0.18f) * 0.38f
                if (balloon.stateTimerSec >= 0.18f) {
                    balloon.state = BalloonLifecycleState.REVEALING
                    balloon.stateTimerSec = 0f
                }
            }
            BalloonLifecycleState.REVEALING -> {
                if (balloon.stateTimerSec >= 0.45f) {
                    balloon.state = BalloonLifecycleState.COMPLETED
                    balloon.stateTimerSec = 0f
                }
            }
            BalloonLifecycleState.COMPLETED -> Unit
        }
    }

    private fun advancePosition(
        balloon: ActiveBalloonEntity,
        dt: Float,
        totalElapsedSec: Float,
        followRoadStream: Boolean,
        lowGravity: Boolean,
        reducedMotion: Boolean
    ) {
        val speedMult = if (reducedMotion) 0.55f else if (lowGravity) 0.75f else 1.0f
        if (followRoadStream) {
            balloon.roadProgress += balloon.verticalSpeedRatio * 0.62f * speedMult * dt
            if (balloon.roadProgress > 0.95f) {
                balloon.roadProgress = 0.05f
            }
            val pt = evaluateRoadCurve(balloon.roadProgress, 1f, 1f)
            val bobOffset = if (reducedMotion) 0f else sin(totalElapsedSec * balloon.driftFrequency + balloon.phaseOffset) * 0.045f
            balloon.xRatio = pt.x.coerceIn(0.07f, 0.93f)
            balloon.yRatio = (pt.y - 0.12f + bobOffset).coerceIn(0.14f, 0.82f)
            balloon.rotationDeg = if (reducedMotion) 0f else sin(totalElapsedSec * 1.6f + balloon.phaseOffset) * 5f
        } else {
            balloon.yRatio -= balloon.verticalSpeedRatio * speedMult * dt
            val wave = if (reducedMotion) 0f else sin(totalElapsedSec * balloon.driftFrequency + balloon.phaseOffset)
            balloon.xRatio = (balloon.baseXRatio + balloon.driftAmplitudeRatio * wave).coerceIn(0.08f, 0.92f)
            balloon.rotationDeg = if (reducedMotion) 0f else wave * 6.5f
            if (balloon.yRatio < 0.08f) {
                balloon.yRatio = 0.92f
            }
        }
    }
}

/**
 * ============================================================================================
 * MODULE 2: BalloonSpawner & Smart Learning Selector
 * ============================================================================================
 */
object BalloonSpawner {

    const val CHALLENGE_ROUND_TARGET_COUNT = 15

    fun verticalSpeedRatioForAge(ageBand: AgeBand, adaptiveAdjustment: Float): Float {
        val base = when (ageBand) {
            AgeBand.TODDLER_2_3 -> 0.062f
            AgeBand.PRESCHOOL_4_5 -> 0.082f
            AgeBand.KINDER_5_6 -> 0.102f
        }
        return (base * adaptiveAdjustment).coerceIn(0.045f, 0.14f)
    }

    /**
     * Smart Learning Balloon Selection:
     * Prioritizes concepts the child has not yet mastered while gradually introducing new ones.
     */
    fun selectSmartPayload(
        allPayloads: List<BalloonLearningPayload>,
        masteryCounts: Map<String, Int>,
        seedIndex: Int
    ): BalloonLearningPayload {
        if (allPayloads.isEmpty()) return LearningContentManager.defaultFallbackPayload
        val unmastered = allPayloads.filter { (masteryCounts[it.id] ?: 0) < 2 }
        return if (unmastered.isNotEmpty() && seedIndex % 3 != 0) {
            unmastered[seedIndex % unmastered.size]
        } else {
            allPayloads[seedIndex % allPayloads.size]
        }
    }

    /**
     * Strict Non-Repeating Session Selector:
     * Returns an unpopped payload that is NOT in [poppedIds] and NOT currently floating in
     * [currentlyFloatingIds]. Returns null when all unpopped items are already on screen or
     * when every item in the mode has been popped (until the user resets the game).
     */
    fun selectNextUnpoppedPayload(
        allPayloads: List<BalloonLearningPayload>,
        poppedIds: Set<String>,
        currentlyFloatingIds: Set<String>,
        seedIndex: Int
    ): BalloonLearningPayload? {
        if (allPayloads.isEmpty()) return null
        val available = allPayloads.filter {
            it.id !in poppedIds && it.id !in currentlyFloatingIds
        }
        if (available.isEmpty()) return null
        return available[seedIndex.coerceAtLeast(0) % available.size]
    }

    /**
     * Generates a freshly shuffled list of up to [targetCount] (default 15) unique random
     * target payloads for Level 2+ Voice-Guided Random Challenge rounds.
     * Passing a different [roundSeed] guarantees a brand-new random order each time.
     */
    fun generateRandomChallengeQueue(
        allPayloads: List<BalloonLearningPayload>,
        roundSeed: Long,
        targetCount: Int = CHALLENGE_ROUND_TARGET_COUNT
    ): List<BalloonLearningPayload> {
        if (allPayloads.isEmpty()) return emptyList()
        val rng = kotlin.random.Random(roundSeed)
        val count = minOf(targetCount.coerceAtLeast(1), allPayloads.size)
        return allPayloads.shuffled(rng).take(count)
    }

    /**
     * Selects a distractor balloon for Voice-Guided Random Challenge mode that is distinct
     * from [targetPayload] and not currently floating on screen.
     */
    fun selectChallengeDistractorPayload(
        allPayloads: List<BalloonLearningPayload>,
        targetPayload: BalloonLearningPayload,
        currentlyFloatingIds: Set<String>,
        seedIndex: Int
    ): BalloonLearningPayload {
        if (allPayloads.isEmpty()) return targetPayload
        val candidates = allPayloads.filter {
            it.id != targetPayload.id && it.id !in currentlyFloatingIds
        }
        if (candidates.isNotEmpty()) {
            val idx = ((seedIndex * 7 + 3).coerceAtLeast(0)) % candidates.size
            return candidates[idx]
        }
        val fallback = allPayloads.filter { it.id != targetPayload.id }
        return if (fallback.isNotEmpty()) {
            fallback[seedIndex.coerceAtLeast(0) % fallback.size]
        } else {
            targetPayload
        }
    }

    /**
     * Builds the Warm Preschool Teacher voice prompt announcing the target balloon to pop
     * in Level 2+ Voice-Guided Random Challenge mode.
     */
    fun buildVoiceChallengePrompt(
        target: BalloonLearningPayload,
        lang: BalloonNarrationLanguage
    ): String {
        val label = when (lang) {
            BalloonNarrationLanguage.ENGLISH -> when (target.categoryTag) {
                "Alphabet", "Phonics" -> "letter ${target.symbolOrBadge}"
                "Number" -> "number ${target.symbolOrBadge}"
                else -> target.revealTitleEn.substringBefore("•").trim()
            }
            BalloonNarrationLanguage.TELUGU -> target.revealTitleTe.substringBefore("•").trim()
            BalloonNarrationLanguage.HINDI -> target.revealTitleHi.substringBefore("•").trim()
        }
        return "Find $label! Can you pop the ${target.symbolOrBadge} balloon?"
    }

    /**
     * Builds the patient, encouraging teacher retry prompt when a child taps a non-matching
     * balloon during the Voice-Guided Random Challenge level.
     */
    fun buildGentleWobbleRetryPrompt(
        tapped: BalloonLearningPayload,
        target: BalloonLearningPayload,
        lang: BalloonNarrationLanguage
    ): String {
        val targetName = target.getDisplayTitle(lang).substringBefore("•").trim()
        return "That's ${tapped.symbolOrBadge}. Let's look for ${target.symbolOrBadge}! Find the $targetName balloon."
    }

    fun createBalloonForSlot(
        instanceId: Int,
        slotIndex: Int,
        totalSlots: Int,
        ageBand: AgeBand,
        speedMultiplier: Float,
        payload: BalloonLearningPayload,
        startAtBottom: Boolean
    ): ActiveBalloonEntity {
        val laneSpan = 0.82f / totalSlots.coerceAtLeast(1)
        val baseX = 0.09f + laneSpan * (slotIndex + 0.5f)
        val initialRoadT = (0.06f + (slotIndex.toFloat() / totalSlots.coerceAtLeast(1)) * 0.84f).coerceIn(0.06f, 0.92f)
        val initialY = if (startAtBottom) 0.88f else (0.78f - (slotIndex % 3) * 0.22f)
        val isAnimalSurprise = payload.isAnimalSurprise || (instanceId % 3 == 0)
        return ActiveBalloonEntity(
            instanceId = instanceId,
            slotIndex = slotIndex,
            baseXRatio = baseX,
            xRatio = baseX,
            yRatio = initialY,
            roadProgress = if (startAtBottom) 0.06f else initialRoadT,
            verticalSpeedRatio = verticalSpeedRatioForAge(ageBand, speedMultiplier) * (0.92f + (slotIndex % 3) * 0.08f),
            driftAmplitudeRatio = 0.028f + (slotIndex % 2) * 0.012f,
            driftFrequency = 1.6f + (slotIndex % 3) * 0.35f,
            phaseOffset = slotIndex * 1.25f,
            rotationDeg = 0f,
            scale = if (startAtBottom) 0.35f else 1f,
            state = if (startAtBottom) BalloonLifecycleState.SPAWNING else BalloonLifecycleState.FLOATING,
            stateTimerSec = 0f,
            payload = payload,
            hasAnimalSurpriseBadge = isAnimalSurprise
        )
    }
}

/**
 * ============================================================================================
 * MODULE 3: AdaptiveDifficultyManager
 * ============================================================================================
 */
class AdaptiveDifficultyManager {
    var successfulTaps: Int by mutableIntStateOf(0)
        private set
    var gentleMisses: Int by mutableIntStateOf(0)
        private set
    var inactivitySeconds: Float by mutableFloatStateOf(0f)
        private set
    var spokenHintTriggeredForCycle: Boolean by mutableStateOf(false)
        private set

    var balloonSizeMultiplier: Float by mutableFloatStateOf(1.0f)
        private set
    var balloonSpeedMultiplier: Float by mutableFloatStateOf(1.0f)
        private set

    val shouldPulseHintBalloon: Boolean
        get() = inactivitySeconds >= 5.0f

    val shouldShowPointingHand: Boolean
        get() = inactivitySeconds >= 8.0f

    val shouldSpeakInactivityHint: Boolean
        get() = inactivitySeconds >= 12.0f && !spokenHintTriggeredForCycle

    fun onFrameTick(deltaTimeSec: Float) {
        inactivitySeconds += deltaTimeSec.coerceIn(0f, 0.1f)
    }

    fun markSpokenHintDelivered() {
        spokenHintTriggeredForCycle = true
    }

    fun onBalloonPopped(matchedChallengeTarget: Boolean) {
        successfulTaps += 1
        inactivitySeconds = 0f
        spokenHintTriggeredForCycle = false

        if (matchedChallengeTarget && successfulTaps % 4 == 0) {
            balloonSpeedMultiplier = (balloonSpeedMultiplier + 0.04f).coerceAtMost(1.16f)
            balloonSizeMultiplier = (balloonSizeMultiplier - 0.02f).coerceAtLeast(0.96f)
        }
    }

    fun onGentleMiss() {
        gentleMisses += 1
        inactivitySeconds = 0f
        spokenHintTriggeredForCycle = false
    }

    fun resetInactivity() {
        inactivitySeconds = 0f
        spokenHintTriggeredForCycle = false
    }
}

/**
 * ============================================================================================
 * MODULE 4: LearningContentManager
 * ============================================================================================
 */
object LearningContentManager {

    val defaultFallbackPayload = BalloonLearningPayload(
        id = "let_a",
        symbolOrBadge = "A",
        emoji = "🍎",
        revealIllustrationEmoji = "🍎",
        revealTitleEn = "A • Apple",
        spokenEn = "A! A for Apple!",
        revealTitleTe = "అ • అరటి",
        spokenTe = "అ! అ — అరటి!",
        phoneticTeFallback = "A! A for Apple! Telugu: Aaa, Arati!",
        revealTitleHi = "अ • अनार",
        spokenHi = "अ! अ से अनार!",
        phoneticHiFallback = "A! A for Apple! Hindi: A se Anaar!",
        balloonColorHex = 0xFFEF4444,
        categoryTag = "Alphabet"
    )

    fun mapActivityToInitialWorldAndMode(activityId: String): Pair<BalloonWorldEnvironment, BalloonLearningMode> {
        return when (activityId) {
            "w06_balloons_1" -> BalloonWorldEnvironment.FARM_ADVENTURE to BalloonLearningMode.ALPHABET_POP
            "w06_balloons_2" -> BalloonWorldEnvironment.CANDY_RAINBOW_LAND to BalloonLearningMode.COLOR_POP
            "w06_balloons_3" -> BalloonWorldEnvironment.SPACE_ADVENTURE to BalloonLearningMode.NUMBER_POP
            "w06_balloons_4" -> BalloonWorldEnvironment.CANDY_RAINBOW_LAND to BalloonLearningMode.SHAPE_POP
            "w06_balloons_5" -> BalloonWorldEnvironment.JUNGLE_ADVENTURE to BalloonLearningMode.ANIMAL_POP
            "w06_balloons_6" -> BalloonWorldEnvironment.GARDEN_BUTTERFLY_WORLD to BalloonLearningMode.MATCHING_POP
            "w06_balloons_7" -> BalloonWorldEnvironment.FARM_ADVENTURE to BalloonLearningMode.FRUIT_POP
            "w06_balloons_8" -> BalloonWorldEnvironment.GARDEN_BUTTERFLY_WORLD to BalloonLearningMode.VEGETABLE_POP
            "w06_balloons_9" -> BalloonWorldEnvironment.CITY_TRANSPORT_WORLD to BalloonLearningMode.VEHICLE_POP
            "w06_balloons_10" -> BalloonWorldEnvironment.ARCTIC_ADVENTURE to BalloonLearningMode.COUNTING_POP
            "w06_balloons_11" -> BalloonWorldEnvironment.DINOSAUR_ADVENTURE to BalloonLearningMode.WORD_POP
            "w06_balloons_12" -> BalloonWorldEnvironment.UNDERWATER_ADVENTURE to BalloonLearningMode.PHONICS_POP
            "w22_arcade_1" -> BalloonWorldEnvironment.UNDERWATER_ADVENTURE to BalloonLearningMode.COLOR_POP
            "w22_arcade_2" -> BalloonWorldEnvironment.SPACE_ADVENTURE to BalloonLearningMode.COUNTING_POP
            "w22_arcade_3" -> BalloonWorldEnvironment.GARDEN_BUTTERFLY_WORLD to BalloonLearningMode.ANIMAL_POP
            "w22_arcade_4" -> BalloonWorldEnvironment.UNDERWATER_ADVENTURE to BalloonLearningMode.ANIMAL_POP
            "w22_arcade_5" -> BalloonWorldEnvironment.DINOSAUR_ADVENTURE to BalloonLearningMode.SHAPE_POP
            "w22_arcade_6" -> BalloonWorldEnvironment.CANDY_RAINBOW_LAND to BalloonLearningMode.NUMBER_POP
            "w22_arcade_7" -> BalloonWorldEnvironment.FARM_ADVENTURE to BalloonLearningMode.FRUIT_POP
            else -> BalloonWorldEnvironment.FARM_ADVENTURE to BalloonLearningMode.MIXED_ADVENTURE
        }
    }

    fun getPayloadsForModeAndWorld(
        mode: BalloonLearningMode,
        world: BalloonWorldEnvironment,
        numberTier: BalloonNumberTier = BalloonNumberTier.TIER_1_20
    ): List<BalloonLearningPayload> {
        return when (mode) {
            BalloonLearningMode.ALPHABET_POP -> alphabetPayloads
            BalloonLearningMode.PHONICS_POP -> phonicsPayloads
            BalloonLearningMode.NUMBER_POP -> getNumberPayloadsForTier(numberTier)
            BalloonLearningMode.COUNTING_POP -> getNumberPayloadsForTier(numberTier)
            BalloonLearningMode.SHAPE_POP -> shapePayloads
            BalloonLearningMode.COLOR_POP -> colorPayloads
            BalloonLearningMode.ANIMAL_POP -> getWorldSpecificAnimals(world)
            BalloonLearningMode.FRUIT_POP -> fruitPayloads
            BalloonLearningMode.VEGETABLE_POP -> vegetablePayloads
            BalloonLearningMode.VEHICLE_POP -> vehiclePayloads
            BalloonLearningMode.MATCHING_POP -> getWorldSpecificAnimals(world) + shapePayloads.take(4)
            BalloonLearningMode.WORD_POP -> wordPayloads
            BalloonLearningMode.MIXED_ADVENTURE ->
                alphabetPayloads.take(4) + numberPayloads.take(4) + getWorldSpecificAnimals(world).take(4) + colorPayloads.take(4)
        }
    }

    private fun getWorldSpecificAnimals(world: BalloonWorldEnvironment): List<BalloonLearningPayload> {
        return when (world) {
            BalloonWorldEnvironment.FARM_ADVENTURE -> listOf(
                BalloonLearningPayload("ani_cow", "🐄", "🐄", "🐄", "Cow • Moo!", "Cow! The friendly cow says Moo!", "ఆవు", "ఆవు! ఆవు అంబా అంటుంది!", "Aavu! Cow says Moo!", "गाय", "गाय! गाय मू करती है!", "Gaay! Cow says Moo!", 0xFF10B981, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_horse", "🐴", "🐴", "🐴", "Horse • Neigh!", "Horse! The fast horse says Neigh!", "గుర్రం", "గుర్రం! గుర్రం వేగంగా పరిగెడుతుంది!", "Gurram! Horse says Neigh!", "घोड़ा", "घोड़ा! घोड़ा हिनहिनाता है!", "Ghoda! Horse says Neigh!", 0xFFD97706, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_sheep", "🐑", "🐑", "🐑", "Sheep • Baa!", "Sheep! Fluffy sheep says Baa!", "గొర్రె", "గొర్రె! మెత్తని గొర్రె!", "Gorre! Fluffy Sheep says Baa!", "भेड़", "भेड़! प्यारी भेड़ बा करती है!", "Bhed! Sheep says Baa!", 0xFF3B82F6, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_duck", "🦆", "🦆", "🦆", "Duck • Quack!", "Duck! Yellow duck says Quack, quack!", "బాతు", "బాతు! బాతు క్వాక్ క్వాక్ అంటుంది!", "Baathu! Duck says Quack!", "बत्तख", "बत्तख! बत्तख क्वैक करती है!", "Battakh! Duck says Quack!", 0xFFEAB308, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_hen", "🐔", "🐔", "🐔", "Hen • Cluck!", "Hen! Farm hen says Cluck, cluck!", "కోడి", "కోడి! కోడి కొక్కొరొకో అంటుంది!", "Kodi! Hen says Cluck!", "मुर्गी", "मुर्गी! मुर्गी कुकड़ू-कू करती है!", "Murgi! Hen says Cluck!", 0xFFEF4444, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_goat", "🐐", "🐐", "🐐", "Goat • Meh!", "Goat! Playful goat leaps in the meadow!", "మేక", "మేక! మేక గడ్డి తింటుంది!", "Meka! Meadow Goat!", "बकरी", "बकरी! बकरी मैं-मैं करती है!", "Bakri! Meadow Goat!", 0xFF8B5CF6, categoryTag = "Animal", isAnimalSurprise = true)
            )
            BalloonWorldEnvironment.JUNGLE_ADVENTURE -> listOf(
                BalloonLearningPayload("ani_lion", "🦁", "🦁", "🦁", "Lion • Roar!", "Lion! The golden lion says Roar!", "సింహం", "సింహం! అడవికి రాజు సింహం!", "Simham! Lion says Roar!", "शेर", "शेर! जंगल का राजा शेर दहाड़ता है!", "Sher! Lion says Roar!", 0xFFF59E0B, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_tiger", "🐯", "🐯", "🐯", "Tiger • Roar!", "Tiger! Striped jungle tiger!", "పులి", "పులి! చారల పులి!", "Puli! Striped Tiger!", "बाघ", "बाघ! धारियों वाला बाघ!", "Baagh! Striped Tiger!", 0xFFF97316, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_monkey", "🐒", "🐒", "🍌", "Monkey • Ooh-ooh!", "Monkey! Playful monkey swings on vines!", "కోతి", "కోతి! కోతి చెట్టు మీద దుముకుతుంది!", "Kothi! Playful Monkey!", "बंदर", "बंदर! नटखट बंदर पेड़ पर कूदता है!", "Bandar! Playful Monkey!", 0xFFD97706, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_elephant", "🐘", "🐘", "🐘", "Elephant • Pawoo!", "Elephant! Gentle giant with a long trunk!", "ఏనుగు", "ఏనుగు! పెద్ద ఏనుగు తొండంతో నీళ్ళు చల్లుతుంది!", "Eenugu! Gentle Elephant!", "हाथी", "हाथी! प्यारा हाथी अपनी सूंड हिलाता है!", "Haathi! Gentle Elephant!", 0xFF3B82F6, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_zebra", "🦓", "🦓", "🦓", "Zebra • Stripes!", "Zebra! Black and white stripes!", "జీబ్రా", "జీబ్రా! నలుపు తెలుపు చారల జీబ్రా!", "Zebra! Black and white stripes!", "ज़ेबरा", "ज़ेबरा! काली और सफेद धारियों वाला ज़ेबरा!", "Zebra! Black and white stripes!", 0xFF6366F1, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_giraffe", "🦒", "🦒", "🦒", "Giraffe • Tall!", "Giraffe! Tallest animal reaching treetops!", "జిరాఫీ", "జిరాఫీ! పొడవైన మెడ ఉన్న జిరాఫీ!", "Giraffe! Tall Giraffe!", "जिराफ़", "जिराफ़! लंबी गर्दन वाला जिराफ़!", "Giraffe! Tall Giraffe!", 0xFFEAB308, categoryTag = "Animal", isAnimalSurprise = true)
            )
            BalloonWorldEnvironment.ARCTIC_ADVENTURE -> listOf(
                BalloonLearningPayload("ani_penguin", "🐧", "🐧", "🐧", "Penguin", "Penguin! Waddling on the snowy ice!", "పెంగ్విన్", "పెంగ్విన్! మంచు మీద నడిచే పెంగ్విన్!", "Penguin! Waddling on ice!", "पेंगुइन", "पेंगुइन! बर्फ पर चलने वाला पेंगुइन!", "Penguin! Waddling on ice!", 0xFF0284C7, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_polar", "🐻‍❄️", "🐻‍❄️", "❄️", "Polar Bear", "Polar Bear! Fluffy white snow bear!", "ధ్రువపు ఎలుగుబంటి", "ధ్రువపు ఎలుగుబంటి!", "Polar Bear! White snow bear!", "ध्रुवीय भालू", "ध्रुवीय भालू!", "Bhaalu! Polar Bear!", 0xFF3B82F6, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_seal", "🦭", "🦭", "🦭", "Seal", "Seal! Clapping flippers by the iceberg!", "సీల్", "సీల్! మంచు నీటిలో ఈదే సీల్!", "Seal! Clapping flippers!", "सील", "सील! पानी में तैरने वाली सील!", "Seal! Swimming in icy water!", 0xFF06B6D4, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_owl_snow", "🦉", "🦉", "🦉", "Snowy Owl", "Snowy Owl! Gliding across the winter sky!", "గుడ్లగూబ", "గుడ్లగూబ! హూ హూ అని పిలుస్తుంది!", "Gudlaguba! Snowy Owl says Hoo!", "उल्लू", "उल्लू! बर्फीला उल्लू हू-हू करता है!", "Ullu! Snowy Owl says Hoo!", 0xFF8B5CF6, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_whale_arc", "🐋", "🐋", "🐋", "Whale", "Whale! Singing beneath the arctic waves!", "తిమింగలం", "తిమింగలం! సముద్రంలో పెద్ద తిమింగలం!", "Thimingalam! Ocean Whale!", "व्हेल", "व्हेल! समुद्र की बड़ी व्हेल मछली!", "Whale! Singing in the ocean!", 0xFF2563EB, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_fox_arc", "🦊", "🦊", "❄️", "Arctic Fox", "Arctic Fox! Warm fluffy tail in the snow!", "నక్క", "నక్క! చురుకైన మంచు నక్క!", "Nakka! Clever Arctic Fox!", "लोमड़ी", "लोमड़ी! बर्फीली प्यारी लोमड़ी!", "Lomdi! Clever Arctic Fox!", 0xFFEC4899, categoryTag = "Animal", isAnimalSurprise = true)
            )
            BalloonWorldEnvironment.UNDERWATER_ADVENTURE -> listOf(
                BalloonLearningPayload("ani_fish", "🐠", "🐠", "🐠", "Clownfish", "Fish! Colorful reef fish swimming by!", "చేప", "చేప! నీటిలో హాయిగా ఈదే చేప!", "Chepa! Reef Fish swimming!", "मछली", "मछली! जल की रानी रंग-बिरंगी मछली!", "Machhli! Colorful Fish!", 0xFFF97316, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_dolphin", "🐬", "🐬", "🐬", "Dolphin", "Dolphin! Leaping happily through ocean waves!", "డాల్ఫిన్", "డాల్ఫిన్! అలలలో ఎగిరే డాల్ఫిన్!", "Dolphin! Leaping in ocean waves!", "डॉल्फ़िन", "डॉल्फ़िन! लहरों में कूदती प्यारी डॉल्फ़िन!", "Dolphin! Leaping in waves!", 0xFF06B6D4, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_whale", "🐋", "🐋", "🐋", "Blue Whale", "Whale! Gentle giant of the deep blue sea!", "తిమింగలం", "తిమింగలం! నీలి సముద్రపు తిమింగలం!", "Thimingalam! Blue Whale!", "व्हेल", "व्हेल! गहरे सागर की नीली व्हेल!", "Blue Whale! Gentle ocean giant!", 0xFF2563EB, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_octopus", "🐙", "🐙", "🐙", "Octopus", "Octopus! Waving eight friendly arms!", "ఆక్టోపస్", "ఆక్టోపస్! ఎనిమిది చేతుల ఆక్టోపస్!", "Octopus! Eight friendly arms!", "ऑक्टोपस", "ऑक्टोपस! आठ भुजाओं वाला ऑक्टोपस!", "Octopus! Eight friendly arms!", 0xFFEC4899, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_turtle", "🐢", "🐢", "🐢", "Sea Turtle", "Turtle! Gliding calmly over coral reefs!", "తాబేలు", "తాబేలు! సముద్రపు తాబేలు మెల్లగా ఈదుతుంది!", "Thaabelu! Gentle Sea Turtle!", "कछुआ", "कछुआ! शांत तैरने वाला समुद्री कछुआ!", "Kachhua! Gentle Sea Turtle!", 0xFF10B981, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_crab", "🦀", "🦀", "🦀", "Reef Crab", "Crab! Walking sideways on sandy shells!", "పీత", "పీత! ఇసుక మీద నడిచే ఎర్ర పీత!", "Peetha! Sandy Reef Crab!", "केकड़ा", "केकड़ा! रेत पर चलने वाला लाल केकड़ा!", "Kekda! Sandy Reef Crab!", 0xFFEF4444, categoryTag = "Animal", isAnimalSurprise = true)
            )
            BalloonWorldEnvironment.DINOSAUR_ADVENTURE -> listOf(
                BalloonLearningPayload("ani_brachio", "🦕", "🦕", "🦕", "Brachiosaurus", "Brachiosaurus! Friendly leaf-eating giant dinosaur!", "బ్రాకియోసారస్", "డినో! ఆకులు తినే పొడవైన డైనోసార్!", "Brachiosaurus! Tall leaf-eating dinosaur!", "ब्रैकियोसॉरस", "डायनासोर! लंबी गर्दन वाला डायनासोर!", "Brachiosaurus! Friendly tall dinosaur!", 0xFF10B981, categoryTag = "Dinosaur", isAnimalSurprise = true),
                BalloonLearningPayload("ani_trex", "🦖", "🦖", "🦖", "T-Rex", "T-Rex! Big dinosaur with mighty stomp!", "టి-రెక్స్", "టి-రెక్స్! పెద్ద డైనోసార్!", "T-Rex! Mighty stomping dinosaur!", "टी-रेक्स", "टी-रेक्स! बड़े कदमों वाला डायनासोर!", "T-Rex! Stomping dinosaur!", 0xFFF59E0B, categoryTag = "Dinosaur", isAnimalSurprise = true),
                BalloonLearningPayload("ani_tricera", "🛡️", "🦕", "🦕", "Triceratops", "Triceratops! Dinosaur with three friendly horns!", "ట్రైసెరాటాప్స్", "మూడు కొమ్ముల డైనోసార్!", "Triceratops! Three-horned dinosaur!", "ट्राइसेराटॉप्स", "तीन सींगों वाला प्यारा डायनासोर!", "Triceratops! Three-horned dinosaur!", 0xFF3B82F6, categoryTag = "Dinosaur", isAnimalSurprise = true),
                BalloonLearningPayload("ani_stego", "🌿", "🦖", "🦖", "Stegosaurus", "Stegosaurus! Dinosaur with back plates!", "స్టెగోసారస్", "వీపు మీద పలకలు ఉన్న డైనోసార్!", "Stegosaurus! Plated back dinosaur!", "स्टेगोसॉरस", "पीठ पर प्लेटों वाला डायनासोर!", "Stegosaurus! Plated dinosaur!", 0xFF8B5CF6, categoryTag = "Dinosaur", isAnimalSurprise = true),
                BalloonLearningPayload("ani_egg", "🥚", "🥚", "🦖", "Baby Dino", "Crack, crack! A cute baby dinosaur hatched!", "డైనో గుడ్డు", "చిన్న బేబీ డైనోసార్ బయటకు వచ్చింది!", "Baby Dino hatching from egg!", "नन्हा डायनासोर", "अंडे से निकला प्यारा नन्हा डायनासोर!", "Baby Dino hatching!", 0xFFEC4899, categoryTag = "Dinosaur", isAnimalSurprise = true),
                BalloonLearningPayload("ani_volcano", "🌋", "🌋", "🌋", "Volcano", "Ancient mountain steaming in Dinosaur Valley!", "అగ్నిపర్వతం", "డైనోసార్ లోయలో పెద్ద పర్వతం!", "Dinosaur Valley Volcano!", "ज्वालामुखी", "डायनासोर घाटी का ऊँचा पर्वत!", "Dinosaur Valley Mountain!", 0xFFEF4444, categoryTag = "Dinosaur", isAnimalSurprise = true)
            )
            BalloonWorldEnvironment.GARDEN_BUTTERFLY_WORLD -> listOf(
                BalloonLearningPayload("ani_butterfly", "🦋", "🦋", "🦋", "Butterfly", "Butterfly! Colorful wings fluttering over flowers!", "సీతాకోకచిలుక", "సీతాకోకచిలుక!", "Seethakokachiluka! Colorful Butterfly!", "तितली", "तितली! रंग-बिरंगे पंखों वाली तितली!", "Titli! Colorful Butterfly!", 0xFF8B5CF6, categoryTag = "Nature", isAnimalSurprise = true),
                BalloonLearningPayload("ani_bee", "🐝", "🐝", "🌻", "Honeybee", "Bee! Busy honeybee says Buzz, buzz!", "తేనెటీగ", "తేనెటీగ!", "Theneteega! Busy Honeybee says Buzz!", "मधुमक्खी", "मधुमक्खी! फूलों पर गुनगुनाती मधुमक्खी!", "Madhumakkhi! Honeybee says Buzz!", 0xFFEAB308, categoryTag = "Nature", isAnimalSurprise = true),
                BalloonLearningPayload("ani_ladybug", "🐞", "🐞", "🍀", "Ladybug", "Ladybug! Little red beetle with black spots!", "లేడీబగ్", "చిన్న ఎర్ర చుక్కల పురుగు!", "Little red spotted Ladybug!", "लेडीबग", "नन्ही लाल बिंदियों वाली लेडीबग!", "Little red spotted Ladybug!", 0xFFEF4444, categoryTag = "Nature", isAnimalSurprise = true),
                BalloonLearningPayload("ani_bunny", "🐰", "🐰", "🥕", "Bunny", "Bunny! Long-eared bunny hopping in the clover!", "కుందేలు", "కుందేలు! మెత్తని కుందేలు గెంతుతోంది!", "Kundelu! Hopping Garden Bunny!", "खरगोश", "खरगोश! बगीचे में फुदकता प्यारा खरगोश!", "Khargosh! Hopping Garden Bunny!", 0xFFEC4899, categoryTag = "Nature", isAnimalSurprise = true),
                BalloonLearningPayload("ani_bird", "🐦", "🐦", "🎵", "Songbird", "Songbird! Singing a sweet morning melody!", "పక్షి", "పక్షి! కిచకిచలాడే చిట్టి పక్షి!", "Pakshi! Singing Songbird!", "चिड़िया", "चिड़िया! मीठे गीत गाती नन्ही चिड़िया!", "Chidiya! Singing Songbird!", 0xFF06B6D4, categoryTag = "Nature", isAnimalSurprise = true),
                BalloonLearningPayload("ani_frog", "🐸", "🐸", "🪷", "Pond Frog", "Frog! Green lily-pad frog says Ribbit!", "కప్ప", "కప్ప! బెకబెకలాడే ఆకుపచ్చ కప్ప!", "Kappa! Green Frog says Ribbit!", "मेंढक", "मेंढक! कमल के पत्ते पर बैठा हरा मेंढक!", "Mendhak! Green Frog says Ribbit!", 0xFF10B981, categoryTag = "Nature", isAnimalSurprise = true)
            )
            else -> listOf(
                BalloonLearningPayload("ani_lion", "🦁", "🦁", "🦁", "Lion • Roar!", "Lion! The friendly lion says Roar!", "సింహం", "సింహం! అడవికి రాజు సింహం!", "Simham! Lion says Roar!", "शेर", "शेर! जंगल का राजा शेर!", "Sher! Lion says Roar!", 0xFFF59E0B, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_elephant", "🐘", "🐘", "🐘", "Elephant", "Elephant! Gentle giant with a trunk!", "ఏనుగు", "ఏనుగు! పెద్ద ఏనుగు!", "Eenugu! Gentle Elephant!", "हाथी", "हाथी! प्यारा बड़ा हाथी!", "Haathi! Gentle Elephant!", 0xFF3B82F6, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_dolphin", "🐬", "🐬", "🐬", "Dolphin", "Dolphin! Leaping through ocean waves!", "డాల్ఫిన్", "డాల్ఫిన్! అలలలో ఎగిరే డాల్ఫిన్!", "Dolphin! Ocean Dolphin!", "डॉल्फ़िन", "डॉल्फ़िन! लहरों में कूदती डॉल्फ़िन!", "Dolphin! Ocean Dolphin!", 0xFF06B6D4, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_cow", "🐄", "🐄", "🐄", "Cow • Moo!", "Cow! Friendly farm cow says Moo!", "ఆవు", "ఆవు! ఆవు అంబా అంటుంది!", "Aavu! Farm Cow says Moo!", "गाय", "गाय! प्यारी गाय!", "Gaay! Farm Cow says Moo!", 0xFF10B981, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_duck", "🦆", "🦆", "🦆", "Duck • Quack!", "Duck! Yellow duckling says Quack!", "బాతు", "బాతు! బాతు క్వాక్ అంటుంది!", "Baathu! Duck says Quack!", "बत्तख", "बत्तख! पीली बत्तख!", "Battakh! Duck says Quack!", 0xFFEAB308, categoryTag = "Animal", isAnimalSurprise = true),
                BalloonLearningPayload("ani_butterfly", "🦋", "🦋", "🦋", "Butterfly", "Butterfly! Fluttering rainbow wings!", "సీతాకోకచిలుక", "సీతాకోకచిలుక!", "Seethakokachiluka! Butterfly!", "तितली", "तितली! रंग-बिरंगी तितली!", "Titli! Colorful Butterfly!", 0xFF8B5CF6, categoryTag = "Animal", isAnimalSurprise = true)
            )
        }
    }

    private val alphabetPayloads = listOf(
        BalloonLearningPayload("let_a", "A", "🍎", "🍎", "A • Apple", "A! A for Apple!", "అ • అరటి", "అ! అ — అరటి! A for Apple!", "A! A for Apple! Telugu: Aaa, Arati!", "अ • अनार", "अ! अ से अनार! A for Apple!", "A! A for Apple! Hindi: A se Anaar!", 0xFFEF4444, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_b", "B", "⚽", "⚽", "B • Ball", "B! B for Ball!", "ఆ • ఆవు", "ఆ! ఆ — ఆవు! B for Ball!", "B! B for Ball! Telugu: Aaa, Aavu!", "आ • आम", "आ! आ से आम! B for Ball!", "B! B for Ball! Hindi: Aa se Aam!", 0xFF3B82F6, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_c", "C", "🐱", "🐱", "C • Cat", "C! C for Cat!", "ఇ • ఇల్లు", "ఇ! ఇ — ఇల్లు! C for Cat!", "C! C for Cat! Telugu: Eee, Illu!", "इ • इमली", "इ! इ से इमली! C for Cat!", "C! C for Cat! Hindi: E se Imli!", 0xFF10B981, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_d", "D", "🐶", "🐶", "D • Dog", "D! D for Dog!", "ఈ • ఈక", "ఈ! ఈ — ఈక! D for Dog!", "D! D for Dog! Telugu: Eee, Eeka!", "ई • ईख", "ई! ई से ईख! D for Dog!", "D! D for Dog! Hindi: Ee se Eekh!", 0xFFF59E0B, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_e", "E", "🐘", "🐘", "E • Elephant", "E! E for Elephant!", "ఉ • ఉడుత", "ఉ! ఉ — ఉడుత! E for Elephant!", "E! E for Elephant! Telugu: Uuu, Udutha!", "उ • उल्लू", "उ! उ से उल्लू! E for Elephant!", "E! E for Elephant! Hindi: U se Ullu!", 0xFF8B5CF6, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_f", "F", "🐠", "🐠", "F • Fish", "F! F for Fish!", "ఊ • ఊయల", "ఊ! ఊ — ఊయల! F for Fish!", "F! F for Fish! Telugu: Uuu, Ooyala!", "ऊ • ऊन", "ऊ! ऊ से ऊन! F for Fish!", "F! F for Fish! Hindi: Oo se Oon!", 0xFF06B6D4, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_g", "G", "🦒", "🦒", "G • Giraffe", "G! G for Giraffe!", "ఎ • ఎలుక", "ఎ! ఎ — ఎలుక! G for Giraffe!", "G! G for Giraffe! Telugu: Eh, Eluka!", "ए • एड़ी", "ए! ए से एड़ी! G for Giraffe!", "G! G for Giraffe!", 0xFFEAB308, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_h", "H", "🚁", "🚁", "H • Helicopter", "H! H for Helicopter!", "ఏ • ఏనుగు", "ఏ! ఏ — ఏనుగు! H for Helicopter!", "H! H for Helicopter! Telugu: Aay, Eenugu!", "ऐ • ऐनक", "ऐ! ऐ से ऐनक! H for Helicopter!", "H! H for Helicopter!", 0xFFEC4899, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_i", "I", "🍦", "🍦", "I • Ice Cream", "I! I for Ice Cream!", "ఐ • ఐస్ క్రీమ్", "I! I for Ice Cream!", "I! I for Ice Cream!", "आइसक्रीम", "I! I for Ice Cream!", "I! I for Ice Cream!", 0xFF0284C7, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_j", "J", "🪼", "🪼", "J • Jellyfish", "J! J for Jellyfish!", "జ • జెల్లీఫిష్", "J! J for Jellyfish!", "J! J for Jellyfish!", "ज • जेलीफ़िश", "J! J for Jellyfish!", "J! J for Jellyfish!", 0xFF8B5CF6, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_k", "K", "🪁", "🪁", "K • Kite", "K! K for Kite!", "క • గాలిపటం", "K! K for Kite!", "K! K for Kite!", "प • पतंग", "K! K for Kite!", "K! K for Kite!", 0xFFEF4444, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_l", "L", "🦁", "🦁", "L • Lion", "L! L for Lion!", "ఒ • ఒంటె", "ఒ! ఒ — ఒంటె! L for Lion!", "L! L for Lion!", "ओ • ओखली", "ओ! ओ से ओखली! L for Lion!", "L! L for Lion!", 0xFFF97316, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_m", "M", "🌙", "🌙", "M • Moon", "M! M for Moon!", "ఓ • ఓడ", "ఓ! ఓ — ఓడ! M for Moon!", "M! M for Moon!", "औ • औरत", "औ! M for Moon!", "M! M for Moon!", 0xFF6366F1, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_n", "N", "🪺", "🪺", "N • Nest", "N! N for Nest!", "గూడు • Nest", "N! N for Nest!", "N! N for Nest!", "घोंसला • Nest", "N! N for Nest!", "N! N for Nest!", 0xFF10B981, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_o", "O", "🍊", "🍊", "O • Orange", "O! O for Orange!", "నారింజ • Orange", "O! O for Orange!", "O! O for Orange!", "संतरा • Orange", "O! O for Orange!", "O! O for Orange!", 0xFFF97316, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_p", "P", "🐧", "🐧", "P • Penguin", "P! P for Penguin!", "పెంగ్విన్", "P! P for Penguin!", "P! P for Penguin!", "पेंगुइन", "P! P for Penguin!", "P! P for Penguin!", 0xFF2563EB, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_q", "Q", "👑", "👑", "Q • Queen", "Q! Q for Queen!", "రాణి • Queen", "Q! Q for Queen!", "Q! Q for Queen!", "रानी • Queen", "Q! Q for Queen!", "Q! Q for Queen!", 0xFFEC4899, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_r", "R", "🌈", "🌈", "R • Rainbow", "R! R for Rainbow!", "క • కమలం", "క! క — కమలం! R for Rainbow!", "R! R for Rainbow!", "क • कमल", "क! क से कमल! R for Rainbow!", "R! R for Rainbow! Hindi: Ka se Kamal!", 0xFFDC2626, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_s", "S", "☀️", "☀️", "S • Sun", "S! S for Sun!", "చ • చందమామ", "చ! చ — చందమామ! S for Sun!", "S! S for Sun!", "स • सूरज", "स! स से सूरज! S for Sun!", "S! S for Sun! Hindi: Sa se Sooraj!", 0xFF059669, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_t", "T", "🐢", "🐢", "T • Turtle", "T! T for Turtle!", "తాబేలు • Turtle", "T! T for Turtle!", "T! T for Turtle!", "कछुआ • Turtle", "T! T for Turtle!", "T! T for Turtle!", 0xFF10B981, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_u", "U", "☂️", "☂️", "U • Umbrella", "U! U for Umbrella!", "గొడుగు • Umbrella", "U! U for Umbrella!", "U! U for Umbrella!", "छाता • Umbrella", "U! U for Umbrella!", "U! U for Umbrella!", 0xFF8B5CF6, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_v", "V", "🎻", "🎻", "V • Violin", "V! V for Violin!", "వయోలిన్ • Violin", "V! V for Violin!", "V! V for Violin!", "वायलिन • Violin", "V! V for Violin!", "V! V for Violin!", 0xFFD97706, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_w", "W", "🐋", "🐋", "W • Whale", "W! W for Whale!", "తిమింగలం • Whale", "W! W for Whale!", "W! W for Whale!", "व्हेल • Whale", "W! W for Whale!", "W! W for Whale!", 0xFF0284C7, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_x", "X", "🎼", "🎼", "X • Xylophone", "X! X for Xylophone!", "జైలోఫోన్", "X! X for Xylophone!", "X! X for Xylophone!", "ज़ाइलोफ़ोन", "X! X for Xylophone!", "X! X for Xylophone!", 0xFFEAB308, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_y", "Y", "🧶", "🧶", "Y • Yarn", "Y! Y for Yarn!", "దారం • Yarn", "Y! Y for Yarn!", "Y! Y for Yarn!", "ऊन • Yarn", "Y! Y for Yarn!", "Y! Y for Yarn!", 0xFFEC4899, categoryTag = "Alphabet"),
        BalloonLearningPayload("let_z", "Z", "🦓", "🦓", "Z • Zebra", "Z! Z for Zebra!", "జీబ్రా • Zebra", "Z! Z for Zebra!", "Z! Z for Zebra!", "ज़ेबरा • Zebra", "Z! Z for Zebra!", "Z! Z for Zebra!", 0xFF6366F1, categoryTag = "Alphabet")
    )

    private val phonicsPayloads = listOf(
        BalloonLearningPayload("pho_a", "A", "🍎", "🍎", "A • Apple", "Letter A... aah, Apple!", "అ • అరటి", "అ! అరటి!", "Letter A... aah, Apple!", "अ • अनार", "अ! अ से अनार!", "Letter A... aah, Apple!", 0xFFEF4444, categoryTag = "Phonics"),
        BalloonLearningPayload("pho_b", "B", "🐻", "🐻", "B • Bear", "Letter B... b, Bear!", "బ • బంతి", "బ! బంతి!", "Letter B... b, Bear!", "ब • बॉल", "ब! ब से बॉल!", "Letter B... b, Bear!", 0xFF3B82F6, categoryTag = "Phonics"),
        BalloonLearningPayload("pho_c", "C", "🐱", "🐱", "C • Cat", "Letter C... k, Cat!", "క • కమలం", "క! కమలం!", "Letter C... k, Cat!", "क • कमल", "क! क से कमल!", "Letter C... k, Cat!", 0xFF10B981, categoryTag = "Phonics"),
        BalloonLearningPayload("pho_d", "D", "🐬", "🐬", "D • Dolphin", "Letter D... d, Dolphin!", "డ • డమరుకం", "డ! డాల్ఫిన్!", "Letter D... d, Dolphin!", "ड • डॉल्फ़िन", "ड! डॉल्फ़िन!", "Letter D... d, Dolphin!", 0xFF06B6D4, categoryTag = "Phonics"),
        BalloonLearningPayload("pho_l", "L", "🦁", "🦁", "L • Lion", "Letter L... llll, Lion!", "ల • లడ్డు", "ల! సింహం!", "Letter L... llll, Lion!", "ल • लट्टू", "ल! ल से लट्टू!", "Letter L... llll, Lion!", 0xFFF59E0B, categoryTag = "Phonics"),
        BalloonLearningPayload("pho_m", "M", "🌙", "🌙", "M • Moon", "Letter M... mmmm, Moon!", "మ • మబ్బు", "మ! మబ్బు!", "Letter M... mmmm, Moon!", "म • मछली", "म! म से मछली!", "Letter M... mmmm, Moon!", 0xFF8B5CF6, categoryTag = "Phonics"),
        BalloonLearningPayload("pho_s", "S", "☀️", "☀️", "S • Sun", "Letter S... ssss, Sun!", "స • సూర్యుడు", "స! సూర్యుడు!", "Letter S... ssss, Sun!", "स • सूरज", "स! स से सूरज!", "Letter S... ssss, Sun!", 0xFFEAB308, categoryTag = "Phonics"),
        BalloonLearningPayload("pho_t", "T", "🚂", "🚂", "T • Train", "Letter T... t, Train!", "ట • టమాటా", "ట! రైలు బండి!", "Letter T... t, Train!", "ट • टमाटर", "ट! ट से टमाटर!", "Letter T... t, Train!", 0xFFEC4899, categoryTag = "Phonics")
    )

    private val numberPayloads = listOf(
        BalloonLearningPayload("num_1", "1", "⭐", "⭐", "1 • One", "One! One shining star!", "1 • ఒకటి", "ఒకటి! ఒక మెరిసే నక్షత్రం!", "One! Telugu: Okati! One star!", "1 • एक", "एक! एक चमकता तारा!", "One! Hindi: Ek! One star!", 0xFFEF4444, countQuantity = 1, categoryTag = "Number"),
        BalloonLearningPayload("num_2", "2", "🍎", "🍎", "2 • Two", "Two! One, two! Two crisp apples!", "2 • రెండు", "రెండు! ఒకటి, రెండు!", "Two! Telugu: Rendu! One, two!", "2 • दो", "दो! एक, दो!", "Two! Hindi: Do! One, two!", 0xFFF97316, countQuantity = 2, categoryTag = "Number"),
        BalloonLearningPayload("num_3", "3", "🌟", "🌟", "3 • Three", "Three! One, two, three!", "3 • మూడు", "మూడు! ఒకటి, రెండు, మూడు!", "Three! Telugu: Moodu! One, two, three!", "3 • तीन", "तीन! एक, दो, तीन!", "Three! Hindi: Teen! One, two, three!", 0xFFEAB308, countQuantity = 3, categoryTag = "Number"),
        BalloonLearningPayload("num_4", "4", "🦋", "🦋", "4 • Four", "Four! One, two, three, four!", "4 • నాలుగు", "నాలుగు! ఒకటి, రెండు, మూడు, నాలుగు!", "Four! Telugu: Naalugu! One, two, three, four!", "4 • चार", "चार! एक, दो, तीन, चार!", "Four! Hindi: Chaar! One, two, three, four!", 0xFF10B981, countQuantity = 4, categoryTag = "Number"),
        BalloonLearningPayload("num_5", "5", "🎈", "🎈", "5 • Five", "Five! One, two, three, four, five!", "5 • ఐదు", "ఐదు! ఒకటి, రెండు, మూడు, నాలుగు, ఐదు!", "Five! Telugu: Aidu! One, two, three, four, five!", "5 • पाँच", "पाँच! एक, दो, तीन, चार, पाँच!", "Five! Hindi: Paanch! One, two, three, four, five!", 0xFF06B6D4, countQuantity = 5, categoryTag = "Number"),
        BalloonLearningPayload("num_6", "6", "🐠", "🐠", "6 • Six", "Six! Six colorful reef fish!", "6 • ఆరు", "ఆరు! ఆరు రంగుల చేపలు!", "Six! Telugu: Aaru! Six fish!", "6 • छह", "छह! छह रंग-बिरंगी मछलियाँ!", "Six! Hindi: Chhah! Six fish!", 0xFF3B82F6, countQuantity = 6, categoryTag = "Number"),
        BalloonLearningPayload("num_7", "7", "🌈", "🌈", "7 • Seven", "Seven! Seven colors in the rainbow!", "7 • ఏడు", "ఏడు! హరివిల్లులో ఏడు రంగులు!", "Seven! Telugu: Eedu! Seven rainbow colors!", "7 • सात", "सात! इंद्रधनुष के सात रंग!", "Seven! Hindi: Saat! Seven rainbow colors!", 0xFF8B5CF6, countQuantity = 7, categoryTag = "Number"),
        BalloonLearningPayload("num_8", "8", "🐙", "🐙", "8 • Eight", "Eight! Eight friendly octopus arms!", "8 • ఎనిమిది", "ఎనిమిది! ఆక్టోపస్ కు ఎనిమిది చేతులు!", "Eight! Telugu: Enimidi! Eight!", "8 • आठ", "आठ! ऑक्टोपस की आठ भुजाएँ!", "Eight! Hindi: Aath! Eight!", 0xFFEC4899, countQuantity = 8, categoryTag = "Number"),
        BalloonLearningPayload("num_9", "9", "🍓", "🍓", "9 • Nine", "Nine! Nine sweet red berries!", "9 • తొమ్మిది", "తొమ్మిది! తొమ్మిది తీపి పండ్లు!", "Nine! Telugu: Thommidi! Nine!", "9 • नौ", "नौ! नौ मीठी स्ट्रॉबेरी!", "Nine! Hindi: Nau! Nine!", 0xFFF59E0B, countQuantity = 9, categoryTag = "Number"),
        BalloonLearningPayload("num_10", "10", "🏆", "🌟", "10 • Ten", "Ten! Wonderful counting to ten!", "10 • పది", "పది! పది బంగారు నక్షత్రాలు!", "Ten! Telugu: Padi! Ten stars!", "10 • दस", "दस! दस सुनहरे तारे!", "Ten! Hindi: Das! Ten stars!", 0xFF059669, countQuantity = 10, categoryTag = "Number")
    )

    private val numberColorPalette = listOf(
        0xFFEF4444L, 0xFFF97316L, 0xFFEAB308L, 0xFF10B981L, 0xFF06B6D4L,
        0xFF3B82F6L, 0xFF8B5CF6L, 0xFFEC4899L, 0xFFF59E0BL, 0xFF059669L
    )

    private val numberEmojiPalette = listOf(
        "⭐", "🍎", "🌟", "🦋", "🎈", "🐠", "🌈", "🐙", "🍓", "🏆"
    )

    private fun englishWordForNumber(n: Int): String {
        val ones = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
        )
        if (n in 1..19) return ones[n]
        if (n == 100) return "One Hundred"
        val tens = arrayOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")
        val t = n / 10
        val u = n % 10
        return if (u == 0) tens[t] else "${tens[t]}-${ones[u]}"
    }

    /**
     * Generates the full set of number payloads for any of the 4 progressive number tiers:
     * - TIER_1_20 (1..20)
     * - TIER_21_40 (21..40)
     * - TIER_41_60 (41..60)
     * - TIER_61_100 (61..100)
     */
    fun getNumberPayloadsForTier(tier: BalloonNumberTier): List<BalloonLearningPayload> {
        return (tier.startNumber..tier.endNumber).map { n ->
            if (n in 1..10) {
                numberPayloads[n - 1]
            } else {
                val wordEn = englishWordForNumber(n)
                val emoji = numberEmojiPalette[(n - 1) % numberEmojiPalette.size]
                val colorHex = numberColorPalette[(n - 1) % numberColorPalette.size]
                BalloonLearningPayload(
                    id = "num_$n",
                    symbolOrBadge = n.toString(),
                    emoji = emoji,
                    revealIllustrationEmoji = emoji,
                    revealTitleEn = "$n • $wordEn",
                    spokenEn = "$wordEn! Number $n!",
                    revealTitleTe = "$n • సంఖ్య $n",
                    spokenTe = "$n! సంఖ్య $n! $wordEn!",
                    phoneticTeFallback = "$wordEn! Number $n!",
                    revealTitleHi = "$n • संख्या $n",
                    spokenHi = "$n! संख्या $n! $wordEn!",
                    phoneticHiFallback = "$wordEn! Number $n!",
                    balloonColorHex = colorHex,
                    countQuantity = (n % 10).let { if (it == 0) 10 else it },
                    categoryTag = "Number"
                )
            }
        }
    }

    private val shapePayloads = listOf(
        BalloonLearningPayload("shp_circle", "⭕", "⭕", "⚽", "Circle", "Circle! Round like a bouncy ball!", "వృత్తం", "వృత్తం! బంతి లాగా గుండ్రని వృత్తం!", "Circle! Telugu: Vruttham! Round like a ball!", "गोला", "गोला! गेंद की तरह गोल!", "Circle! Hindi: Gola! Round like a ball!", 0xFFEF4444, categoryTag = "Shape"),
        BalloonLearningPayload("shp_square", "🟦", "🟦", "🎁", "Square", "Square! Four equal sides like a gift box!", "చతురస్రం", "చతురస్రం! నాలుగు సమాన భుజాల చతురస్రం!", "Square! Telugu: Chathurasram! Four equal sides!", "चौकोर", "चौकोर! चार बराबर भुजाओं वाला वर्ग!", "Square! Hindi: Chaukor! Four equal sides!", 0xFF2563EB, categoryTag = "Shape"),
        BalloonLearningPayload("shp_triangle", "🔺", "🔺", "⛺", "Triangle", "Triangle! Three corners like a mountain peak!", "త్రిభుజం", "త్రిభుజం! మూడు మూలల త్రిభుజం!", "Triangle! Telugu: Tribhujam! Three corners!", "तिकोना", "तिकोना! तीन कोनों वाला त्रिभुज!", "Triangle! Hindi: Tikona! Three corners!", 0xFF10B981, categoryTag = "Shape"),
        BalloonLearningPayload("shp_rectangle", "▬", "▬", "🚪", "Rectangle", "Rectangle! Two long sides and two short sides!", "దీర్ఘచతురస్రం", "దీర్ఘచతురస్రం! తలుపు ఆకారం!", "Rectangle! Like a cozy front door!", "आयत", "आयत! दरवाजे जैसा आकार!", "Rectangle! Hindi: Aayat!", 0xFF06B6D4, categoryTag = "Shape"),
        BalloonLearningPayload("shp_oval", "🥚", "🥚", "🥚", "Oval", "Oval! Smooth curved egg shape!", "అండాకారం", "అండాకారం! కోడిగుడ్డు ఆకారం!", "Oval! Telugu: Andaakaaram! Egg shape!", "अंडाकार", "अंडाकार! अंडे जैसा गोल-लंबा आकार!", "Oval! Hindi: Andaakaar!", 0xFFF97316, categoryTag = "Shape"),
        BalloonLearningPayload("shp_star", "⭐", "⭐", "🌟", "Star", "Star! Twinkling five-pointed star!", "నక్షత్రం", "నక్షత్రం! ఆకాశంలో మెరిసే నక్షత్రం!", "Star! Telugu: Nakshathram! Twinkling star!", "तारा", "तारा! आसमान में चमकता तारा!", "Star! Hindi: Taara! Twinkling star!", 0xFFF59E0B, categoryTag = "Shape"),
        BalloonLearningPayload("shp_heart", "💖", "💖", "💖", "Heart", "Heart! A warm loving heart shape!", "హృదయం", "హృదయం! ప్రేమ నిండిన హృదయం ఆకారం!", "Heart! Warm loving heart shape!", "दिल", "दिल! प्यारा सा दिल का आकार!", "Heart! Hindi: Dil!", 0xFFEC4899, categoryTag = "Shape"),
        BalloonLearningPayload("shp_diamond", "🔷", "🔷", "🪁", "Diamond", "Diamond! Four-pointed like a flying kite!", "వజ్రం", "వజ్రం! గాలిపటం లాంటి డైమండ్ ఆకారం!", "Diamond! Like a soaring sky kite!", "हीरा", "पतंग जैसा डायमंड आकार!", "Diamond! Like a flying kite!", 0xFF8B5CF6, categoryTag = "Shape")
    )

    private val colorPayloads = listOf(
        BalloonLearningPayload("col_red", "❤️", "❤️", "🍎", "Red", "Red! This is bright red, like an apple!", "ఎరుపు", "ఎరుపు! ఆపిల్ లాంటి ఎరుపు రంగు!", "Red! Telugu: Erupu! Bright red apple!", "लाल", "लाल! सेब जैसा चमकीला लाल रंग!", "Red! Hindi: Laal! Bright red apple!", 0xFFEF4444, categoryTag = "Color"),
        BalloonLearningPayload("col_blue", "💙", "💙", "🐬", "Blue", "Blue! This is blue, like the sky and ocean!", "నీలం", "నీలం! ఆకాశం లాంటి నీలం రంగు!", "Blue! Telugu: Neelam! Sky blue!", "नीला", "नीला! आसमान और सागर जैसा नीला रंग!", "Blue! Hindi: Neela! Sky blue!", 0xFF2563EB, categoryTag = "Color"),
        BalloonLearningPayload("col_yellow", "💛", "💛", "☀️", "Yellow", "Yellow! This is sunny yellow, like the sun!", "పసుపు", "పసుపు! సూర్యుని లాంటి పసుపు రంగు!", "Yellow! Telugu: Pasupu! Sunny yellow!", "पीला", "पीला! सूरज और सूरजमुखी जैसा पीला रंग!", "Yellow! Hindi: Peela! Sunny yellow!", 0xFFEAB308, categoryTag = "Color"),
        BalloonLearningPayload("col_green", "💚", "💚", "🐸", "Green", "Green! This is green, like leaves and frogs!", "ఆకుపచ్చ", "ఆకుపచ్చ! చెట్ల ఆకుల లాంటి ఆకుపచ్చ!", "Green! Telugu: Aakupaccha! Leaf green!", "हरा", "हरा! पत्तियों और घास जैसा हरा रंग!", "Green! Hindi: Hara! Meadow green!", 0xFF10B981, categoryTag = "Color"),
        BalloonLearningPayload("col_orange", "🧡", "🧡", "🍊", "Orange", "Orange! This is warm orange!", "కాషాయం", "నారింజ రంగు! తియ్యని నారింజ పండు రంగు!", "Orange! Telugu: Naarinja rangu!", "नारंगी", "नारंगी! मीठे संतरे जैसा नारंगी रंग!", "Orange! Hindi: Naarangi!", 0xFFF97316, categoryTag = "Color"),
        BalloonLearningPayload("col_purple", "💜", "💜", "🍇", "Purple", "Purple! This is royal purple, like grapes!", "ఊదా", "ఊదా రంగు! ద్రాక్ష పండ్ల ఊదా రంగు!", "Purple! Telugu: Ooda rangu! Grapes!", "बैंगनी", "बैंगनी! अंगूरों जैसा सुंदर बैंगनी रंग!", "Purple! Hindi: Baingani!", 0xFF8B5CF6, categoryTag = "Color"),
        BalloonLearningPayload("col_pink", "🩷", "🩷", "🦩", "Pink", "Pink! This is soft rosy pink!", "గులాబీ", "గులాబీ రంగు! పువ్వు లాంటి గులాబీ రంగు!", "Pink! Telugu: Gulaabi rangu! Rose pink!", "गुलाबी", "गुलाबी! गुलाब के फूल जैसा गुलाबी रंग!", "Pink! Hindi: Gulaabi!", 0xFFEC4899, categoryTag = "Color")
    )

    private val fruitPayloads = listOf(
        BalloonLearningPayload("fru_apple", "🍎", "🍎", "🍎", "Apple", "Apple! Sweet, crunchy red apple!", "ఆపిల్", "ఆపిల్! తియ్యని ఎర్ర ఆపిల్ పండు!", "Apple! Sweet red apple!", "सेब", "सेब! मीठा और ताज़ा लाल सेब!", "Apple! Hindi: Seb! Sweet red apple!", 0xFFEF4444, categoryTag = "Fruit"),
        BalloonLearningPayload("fru_banana", "🍌", "🍌", "🍌", "Banana", "Banana! Soft, sunny yellow banana!", "అరటిపండు", "అరటిపండు! తియ్యని పసుపు అరటిపండు!", "Banana! Telugu: Aratipandu!", "केला", "केला! मीठा पीला केला!", "Banana! Hindi: Kela!", 0xFFEAB308, categoryTag = "Fruit"),
        BalloonLearningPayload("fru_mango", "🥭", "🥭", "🥭", "Mango", "Mango! Juicy golden mango!", "మామిడి", "మామిడి పండు! తియ్యని మామిడి పండు!", "Mango! Telugu: Maamidi pandu!", "आम", "आम! फलों का राजा मीठा आम!", "Mango! Hindi: Aam!", 0xFFF59E0B, categoryTag = "Fruit"),
        BalloonLearningPayload("fru_orange", "🍊", "🍊", "🍊", "Orange", "Orange! Bright citrus orange!", "నారింజ", "నారింజ పండు! రసాల నారింజ పండు!", "Orange! Telugu: Naarinja pandu!", "संतरा", "संतरा! रसीला नारंगी संतरा!", "Orange! Hindi: Santra!", 0xFFF97316, categoryTag = "Fruit"),
        BalloonLearningPayload("fru_grapes", "🍇", "🍇", "🍇", "Grapes", "Grapes! A bunch of sweet purple grapes!", "ద్రాక్ష", "ద్రాక్ష! తియ్యని ద్రాక్ష గుత్తి!", "Grapes! Telugu: Draaksha!", "अंगूर", "अंगूर! मीठे रसीले अंगूरों का गुच्छा!", "Grapes! Hindi: Angoor!", 0xFF8B5CF6, categoryTag = "Fruit"),
        BalloonLearningPayload("fru_melon", "🍉", "🍉", "🍉", "Watermelon", "Watermelon! Cool, refreshing watermelon slice!", "పుచ్చకాయ", "పుచ్చకాయ! చల్లని తియ్యని పుచ్చకాయ!", "Watermelon! Telugu: Pucchakaaya!", "तरबूज़", "तरबूज़! लाल और मीठा रसीला तरबूज़!", "Watermelon! Hindi: Tarbooz!", 0xFF10B981, categoryTag = "Fruit"),
        BalloonLearningPayload("fru_straw", "🍓", "🍓", "🍓", "Strawberry", "Strawberry! Sweet garden berry!", "స్ట్రాబెర్రీ", "స్ట్రాబెర్రీ! ఎర్రని తీపి స్ట్రాబెర్రీ!", "Strawberry! Sweet red berry!", "स्ट्रॉबेरी", "स्ट्रॉबेरी! मीठी लाल स्ट्रॉबेरी!", "Strawberry! Sweet red berry!", 0xFFEC4899, categoryTag = "Fruit")
    )

    private val vegetablePayloads = listOf(
        BalloonLearningPayload("veg_carrot", "🥕", "🥕", "🥕", "Carrot", "Carrot! Crunchy orange carrot that bunnies love!", "క్యారెట్", "క్యారెట్! కరకరలాడే నారింజ క్యారెట్!", "Carrot! Crunchy orange carrot!", "गाजर", "गाजर! खरगोश की पसंदीदा मीठी गाजर!", "Carrot! Hindi: Gaajar!", 0xFFF97316, categoryTag = "Vegetable"),
        BalloonLearningPayload("veg_tomato", "🍅", "🍅", "🍅", "Tomato", "Tomato! Plump, juicy red tomato!", "టమాటా", "టమాటా! ఎర్రని నిగనిగలాడే టమాటా!", "Tomato! Telugu: Tamaata!", "टमाटर", "टमाटर! गोल और लाल टमाटर!", "Tomato! Hindi: Tamaatar!", 0xFFEF4444, categoryTag = "Vegetable"),
        BalloonLearningPayload("veg_broccoli", "🥦", "🥦", "🥦", "Broccoli", "Broccoli! Little green tree vegetable!", "బ్రోకలీ", "బ్రోకలీ! చిన్న చెట్టు లాంటి ఆకుపచ్చ కూరగాయ!", "Broccoli! Healthy green broccoli!", "ब्रोकली", "ब्रोकली! छोटे हरे पेड़ जैसी ब्रोकली!", "Broccoli! Healthy green vegetable!", 0xFF10B981, categoryTag = "Vegetable"),
        BalloonLearningPayload("veg_corn", "🌽", "🌽", "🌽", "Sweet Corn", "Corn! Golden ear of sweet corn!", "మొక్కజొన్న", "మొక్కజొన్న! బంగారు రంగు మొక్కజొన్న పొత్తు!", "Sweet Corn! Telugu: Mokkajonna!", "भुट्टा", "भुट्टा! सुनहरे दानों वाला मीठा मक्का!", "Corn! Hindi: Bhutta!", 0xFFEAB308, categoryTag = "Vegetable"),
        BalloonLearningPayload("veg_potato", "🥔", "🥔", "🥔", "Potato", "Potato! Earthy garden potato!", "బంగాళదుంప", "బంగాళదుంప! అందరికీ ఇష్టమైన ఆలూ!", "Potato! Telugu: Bangaladumpa!", "आलू", "आलू! सब्जियों का राजा प्यारा आलू!", "Potato! Hindi: Aaloo!", 0xFFD97706, categoryTag = "Vegetable"),
        BalloonLearningPayload("veg_pea", "🫛", "🫛", "🫛", "Green Peas", "Green Peas! Little round peas cozy in their pod!", "బఠానీ", "పచ్చి బఠానీ! ఆకుపచ్చ బఠానీ గింజలు!", "Green Peas! Telugu: Bathaani!", "मटर", "मटर! हरी फली में गोल-गोल मटर के दाने!", "Green Peas! Hindi: Matar!", 0xFF059669, categoryTag = "Vegetable")
    )

    private val vehiclePayloads = listOf(
        BalloonLearningPayload("veh_bus", "🚌", "🚌", "🚌", "School Bus", "Bus! The yellow town bus goes Beep, beep!", "బస్సు", "బస్సు! పసుపు రంగు స్కూల్ బస్సు!", "Bus! Yellow School Bus goes Beep, beep!", "बस", "बस! पीली स्कूल बस पों-पों करती चलती है!", "Bus! Yellow School Bus!", 0xFFEAB308, categoryTag = "Vehicle"),
        BalloonLearningPayload("veh_car", "🚗", "🚗", "🚗", "Car", "Car! Driving smoothly along the road!", "కారు", "కారు! రోడ్డు మీద వెళ్ళే ఎర్ర కారు!", "Car! Driving along the road!", "कार", "कार! सड़क पर चलती प्यारी कार!", "Car! Driving along the road!", 0xFFEF4444, categoryTag = "Vehicle"),
        BalloonLearningPayload("veh_train", "🚂", "🚂", "🚂", "Train", "Train! The locomotive chugs Choo-choo on the rails!", "రైలు", "రైలు బండి! చుక్ చుక్ మంటూ వెళ్ళే రైలు!", "Train! Telugu: Railu bandi! Choo-choo!", "रेलगाड़ी", "रेलगाड़ी! छुक-छुक करती चलती रेलगाड़ी!", "Train! Hindi: Railgaadi! Choo-choo!", 0xFF2563EB, categoryTag = "Vehicle"),
        BalloonLearningPayload("veh_bike", "🚲", "🚲", "🚲", "Bicycle", "Bicycle! Pedal along the park path, ring-ring!", "సైకిల్", "సైకిల్! బెల్ కొడుతూ వెళ్ళే సైకిల్!", "Bicycle! Ring, ring!", "साइकिल", "साइकिल! ट्रिन-ट्रिन घंटी बजाती साइकिल!", "Bicycle! Ring, ring!", 0xFF10B981, categoryTag = "Vehicle"),
        BalloonLearningPayload("veh_plane", "✈️", "✈️", "✈️", "Airplane", "Airplane! Soaring high above the fluffy clouds!", "విమానం", "విమానం! మేఘాల పైన ఎగిరే విమానం!", "Airplane! Telugu: Vimaanam! Soaring high!", "हवाई जहाज़", "हवाई जहाज़! बादलों के ऊपर उड़ता विमान!", "Airplane! Hindi: Hawaai Jahaaz!", 0xFF8B5CF6, categoryTag = "Vehicle"),
        BalloonLearningPayload("veh_fire", "🚒", "🚒", "🚒", "Fire Truck", "Fire Truck! Brave red rescue truck!", "ఫైర్ ఇంజన్", "ఫైర్ ఇంజన్! సహాయం చేసే ఎర్ర ఫైర్ ట్రక్!", "Fire Truck! Brave red rescue truck!", "दमकल गाड़ी", "दमकल गाड़ी! मदद के लिए आती लाल फायर ब्रिगेड!", "Fire Truck! Red rescue truck!", 0xFFDC2626, categoryTag = "Vehicle"),
        BalloonLearningPayload("veh_rocket", "🚀", "🚀", "🚀", "Rocket", "Rocket! Blasting off to the stars!", "రాకెట్", "రాకెట్! నక్షత్రాల వైపు దూసుకెళ్ళే రాకెట్!", "Rocket! Blasting off to the stars!", "रॉकेट", "रॉकेट! तारों की सैर कराता अंतरिक्ष रॉकेट!", "Rocket! Blasting off to the stars!", 0xFF06B6D4, categoryTag = "Vehicle")
    )

    private val wordPayloads = listOf(
        BalloonLearningPayload("wrd_sun", "SUN", "☀️", "☀️", "SUN", "Sun! S, U, N spells Sun!", "సూర్యుడు", "సూర్యుడు! S-U-N Sun!", "Sun! S, U, N spells Sun!", "सूरज", "सूरज! S-U-N Sun!", "Sun! S, U, N spells Sun!", 0xFFF59E0B, categoryTag = "Word"),
        BalloonLearningPayload("wrd_cat", "CAT", "🐱", "🐱", "CAT", "Cat! C, A, T spells Cat!", "పిల్లి", "పిల్లి! C-A-T Cat!", "Cat! C, A, T spells Cat!", "बिल्ली", "बिल्ली! C-A-T Cat!", "Cat! C, A, T spells Cat!", 0xFFEC4899, categoryTag = "Word"),
        BalloonLearningPayload("wrd_dog", "DOG", "🐶", "🐶", "DOG", "Dog! D, O, G spells Dog!", "కుక్క", "కుక్క! D-O-G Dog!", "Dog! D, O, G spells Dog!", "कुत्ता", "कुत्ता! D-O-G Dog!", "Dog! D, O, G spells Dog!", 0xFFF97316, categoryTag = "Word"),
        BalloonLearningPayload("wrd_bus", "BUS", "🚌", "🚌", "BUS", "Bus! B, U, S spells Bus!", "బస్సు", "బస్సు! B-U-S Bus!", "Bus! B, U, S spells Bus!", "बस", "बस! B-U-S Bus!", "Bus! B, U, S spells Bus!", 0xFFEAB308, categoryTag = "Word"),
        BalloonLearningPayload("wrd_star", "STAR", "⭐", "🌟", "STAR", "Star! S, T, A, R spells Star!", "నక్షత్రం", "నక్షత్రం! S-T-A-R Star!", "Star! S, T, A, R spells Star!", "तारा", "तारा! S-T-A-R Star!", "Star! S, T, A, R spells Star!", 0xFF8B5CF6, categoryTag = "Word"),
        BalloonLearningPayload("wrd_tree", "TREE", "🌳", "🌳", "TREE", "Tree! T, R, E, E spells Tree!", "చెట్టు", "చెట్టు! T-R-E-E Tree!", "Tree! T, R, E, E spells Tree!", "पेड़", "पेड़! T-R-E-E Tree!", "Tree! T, R, E, E spells Tree!", 0xFF10B981, categoryTag = "Word")
    )
}

/**
 * ============================================================================================
 * MODULE 5: BalloonWorldCanvasRenderer
 * ============================================================================================
 */
object BalloonWorldCanvasRenderer {

    fun DrawScope.renderWorldBackground(
        world: BalloonWorldEnvironment,
        followRoad: Boolean,
        elapsedSec: Float,
        roadStep: Int,
        activeSurprise: MagicalAdventureSurprise?,
        activeCelebrations: List<ActivePopCelebration>
    ) {
        val w = size.width
        val h = size.height

        // 1. Sky / Water / Space Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(world.skyTopHex), Color(world.skyBottomHex))
            ),
            size = size
        )

        // 2. World-Specific Ambient Sky Elements
        when (world) {
            BalloonWorldEnvironment.SPACE_ADVENTURE -> {
                for (i in 0 until 18) {
                    val sx = ((i * 19 + 7) % 100) / 100f * w
                    val sy = ((i * 31 + 11) % 70) / 100f * h
                    val twinkle = 0.4f + 0.6f * abs(sin(elapsedSec * 2.2f + i))
                    drawCircle(
                        color = Color(0xFFFEF08A).copy(alpha = twinkle),
                        radius = (3f + (i % 3) * 2f),
                        center = Offset(sx, sy)
                    )
                }
                val planetCenter = Offset(w * 0.16f, h * 0.22f)
                drawCircle(color = Color(0xFFF59E0B), radius = h * 0.11f, center = planetCenter)
                drawOval(
                    color = Color(0xFFFDE68A).copy(alpha = 0.65f),
                    topLeft = Offset(planetCenter.x - h * 0.19f, planetCenter.y - h * 0.035f),
                    size = Size(h * 0.38f, h * 0.07f),
                    style = Stroke(width = 6f)
                )
            }
            BalloonWorldEnvironment.UNDERWATER_ADVENTURE -> {
                for (ray in 0 until 5) {
                    val rx = w * (0.15f + ray * 0.18f)
                    val sway = sin(elapsedSec * 0.8f + ray) * 18f
                    val rayPath = Path().apply {
                        moveTo(rx - 22f, 0f)
                        lineTo(rx + 28f, 0f)
                        lineTo(rx + 75f + sway, h * 0.85f)
                        lineTo(rx - 45f + sway, h * 0.85f)
                        close()
                    }
                    drawPath(rayPath, color = Color.White.copy(alpha = 0.10f))
                }
            }
            BalloonWorldEnvironment.ARCTIC_ADVENTURE -> {
                for (flake in 0 until 16) {
                    val fx = (((flake * 23) % 96) / 100f * w + sin(elapsedSec + flake) * 14f).coerceIn(0f, w)
                    val fy = (((flake * 0.07f + elapsedSec * 0.08f) % 1.0f) * h)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = 4.5f + (flake % 3) * 1.5f,
                        center = Offset(fx, fy)
                    )
                }
            }
            else -> {
                drawCircle(
                    color = Color(0xFFFDE047).copy(alpha = 0.35f),
                    radius = h * 0.20f,
                    center = Offset(w * 0.11f, h * 0.16f)
                )
                drawCircle(
                    color = Color(0xFFFACC15),
                    radius = h * 0.10f,
                    center = Offset(w * 0.11f, h * 0.16f)
                )
                for (c in 0 until 3) {
                    val cx = ((0.22f + c * 0.32f + elapsedSec * 0.012f) % 1.05f) * w
                    val cy = h * (0.14f + (c % 2) * 0.07f)
                    drawCircle(Color.White.copy(alpha = 0.82f), radius = h * 0.065f, center = Offset(cx, cy))
                    drawCircle(Color.White.copy(alpha = 0.82f), radius = h * 0.05f, center = Offset(cx - h * 0.045f, cy + 4f))
                    drawCircle(Color.White.copy(alpha = 0.82f), radius = h * 0.05f, center = Offset(cx + h * 0.045f, cy + 4f))
                }
            }
        }

        // 3. Magical Rainbow Arch (Always shown in Candy Rainbow Land OR when Magical Rainbow Surprise unlocks!)
        if (world == BalloonWorldEnvironment.CANDY_RAINBOW_LAND ||
            activeSurprise == MagicalAdventureSurprise.RAINBOW_ARCH
        ) {
            val rainbowColors = listOf(
                Color(0xFFEF4444),
                Color(0xFFF97316),
                Color(0xFFEAB308),
                Color(0xFF10B981),
                Color(0xFF3B82F6),
                Color(0xFF8B5CF6)
            )
            rainbowColors.forEachIndexed { idx, col ->
                drawArc(
                    color = col.copy(alpha = 0.55f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.08f + idx * 10f, h * 0.10f + idx * 10f),
                    size = Size(w * 0.84f - idx * 20f, h * 1.1f - idx * 20f),
                    style = Stroke(width = 11f, cap = StrokeCap.Round)
                )
            }
        }

        val farHills = Path().apply {
            moveTo(0f, h * 0.56f)
            cubicTo(w * 0.26f, h * 0.34f, w * 0.56f, h * 0.66f, w, h * 0.44f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(farHills, color = Color(world.hillFarHex))

        val nearHills = Path().apply {
            moveTo(0f, h * 0.70f)
            cubicTo(w * 0.32f, h * 0.54f, w * 0.68f, h * 0.84f, w, h * 0.60f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(nearHills, color = Color(world.hillNearHex))

        // 4. Winding S-Curve Adventure Road
        if (followRoad) {
            val roadPath = Path().apply {
                moveTo(w * 0.06f, h * 0.82f)
                cubicTo(
                    w * 0.34f, h * 0.28f,
                    w * 0.66f, h * 0.90f,
                    w * 0.93f, h * 0.36f
                )
            }
            drawPath(
                path = roadPath,
                color = Color(0xFFFDE68A),
                style = Stroke(width = h * 0.21f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            val roadSurfaceColor = when (world) {
                BalloonWorldEnvironment.CANDY_RAINBOW_LAND -> Color(0xFFDB2777)
                BalloonWorldEnvironment.ARCTIC_ADVENTURE -> Color(0xFF38BDF8)
                else -> Color(0xFF64748B)
            }
            drawPath(
                path = roadPath,
                color = roadSurfaceColor,
                style = Stroke(width = h * 0.16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = roadPath,
                color = Color(0xFFF8FAFC),
                style = Stroke(
                    width = h * 0.020f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(26f, 22f),
                        phase = elapsedSec * 42f
                    )
                )
            )

            for (cp in 0..5) {
                val cpT = 0.08f + (cp / 5f) * 0.82f
                val pt = BalloonPhysics.evaluateRoadCurve(cpT, w, h)
                val reached = (roadStep % 6) >= cp
                drawCircle(
                    color = if (reached) Color(0xFFFACC15) else Color(0xFFE2E8F0),
                    radius = h * 0.030f,
                    center = pt
                )
            }
        }

        // 5. Stage 2 & 4 Particle Bursts
        activeCelebrations.forEach { cel ->
            val progress = (cel.elapsedSec / 0.9f).coerceIn(0f, 1f)
            val center = Offset(cel.xRatio * w, cel.yRatio * h)
            val alpha = (1f - progress).coerceIn(0f, 1f)
            cel.particles.forEach { p ->
                val dist = progress * h * p.speedRatio
                val px = center.x + cos(p.angleRad) * dist
                val py = center.y + sin(p.angleRad) * dist
                drawCircle(
                    color = Color(p.colorHex).copy(alpha = alpha),
                    radius = p.radiusDp * (1f - progress * 0.4f),
                    center = Offset(px, py)
                )
            }
        }
    }
}

/**
 * ============================================================================================
 * MODULE 6: BalloonPopAdventureComposable
 * Picture-First, Minimal-Text Toddler UI with:
 * - Animal Surprise Balloons (moving animal reveal)
 * - Smart Learning Balloons (adaptive mastery tracking)
 * - Magical Balloon Adventures (every 5 pops unlocks a visual scene surprise)
 * ============================================================================================
 */
@Composable
fun BalloonPopAdventureComposable(
    activity: ActivityConfig,
    adaptive: AdaptiveChallengeParameters,
    reducedMotion: Boolean,
    replayKey: Int,
    viewModel: WonderViewModel
) {
    val settings by viewModel.settingsState.collectAsState()
    val buddy = LearningBuddy.fromId(settings.buddyId)
    val ageBand = AgeBand.fromId(settings.ageBandId)

    val initialPair = remember(activity.id) {
        LearningContentManager.mapActivityToInitialWorldAndMode(activity.id)
    }

    var currentWorld by remember(activity.id, replayKey) { mutableStateOf(initialPair.first) }
    var currentMode by remember(activity.id, replayKey) { mutableStateOf(initialPair.second) }
    var currentLanguage by remember(activity.id) { mutableStateOf(BalloonNarrationLanguage.ENGLISH) }
    var followRoadStream by remember(activity.id, replayKey) { mutableStateOf(initialPair.first.hasWindingRoad) }
    var showWorldSelectorOverlay by remember(activity.id, replayKey) { mutableStateOf(false) }

    // Multi-Level Progression State:
    // Stage 1: DISCOVERY_EXPLORE (pop all items in deck once with zero repeats)
    // Stage 2+: VOICE_RANDOM_CHALLENGE (15 random items per round; only spoken target pops; wrong tap wobbles)
    var currentStage by remember(activity.id, replayKey, currentMode) {
        mutableStateOf(BalloonStageType.DISCOVERY_EXPLORE)
    }
    var currentNumberTier by remember(activity.id, replayKey) {
        mutableStateOf(BalloonNumberTier.TIER_1_20)
    }
    var challengeSeed by remember(activity.id, replayKey) { mutableIntStateOf(1) }
    var challengeRoundNumber by remember(activity.id, replayKey, currentMode) { mutableIntStateOf(1) }

    val isNumberMode = currentMode == BalloonLearningMode.NUMBER_POP ||
        currentMode == BalloonLearningMode.COUNTING_POP

    val adaptiveManager = remember(activity.id, replayKey) { AdaptiveDifficultyManager() }

    // Smart Learning Balloons: track mastery per item ID
    val masteryCounts = remember(activity.id) { mutableStateMapOf<String, Int>() }

    val payloads = remember(currentMode, currentWorld, currentNumberTier) {
        LearningContentManager.getPayloadsForModeAndWorld(
            mode = currentMode,
            world = currentWorld,
            numberTier = if (isNumberMode) currentNumberTier else BalloonNumberTier.TIER_1_20
        )
    }

    // Session Reset Key: popped items never repeat in the same game unless the user resets or advances level
    var sessionResetKey by remember(activity.id, replayKey, currentMode, currentWorld, currentNumberTier) {
        mutableIntStateOf(0)
    }
    val poppedIdsInSession = remember(
        activity.id,
        replayKey,
        currentMode,
        currentWorld,
        currentNumberTier,
        currentStage,
        sessionResetKey
    ) {
        mutableStateListOf<String>()
    }

    // 15-item Randomly Generated Voice Challenge Queue (freshly shuffled per challengeSeed)
    val challengeQueue = remember(payloads, challengeSeed, currentStage, sessionResetKey) {
        BalloonSpawner.generateRandomChallengeQueue(
            allPayloads = payloads,
            roundSeed = challengeSeed.toLong() + sessionResetKey * 31L + currentMode.ordinal * 97L,
            targetCount = BalloonSpawner.CHALLENGE_ROUND_TARGET_COUNT
        )
    }
    var challengeStepIndex by remember(
        activity.id,
        replayKey,
        currentMode,
        currentWorld,
        currentNumberTier,
        currentStage,
        challengeSeed,
        sessionResetKey
    ) {
        mutableIntStateOf(0)
    }

    val visibleCount = minOf((adaptive.effectiveChoiceCount + 2).coerceIn(5, 6), payloads.size.coerceAtLeast(1))
    var poppedCount by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var roadStep by remember(activity.id, replayKey) { mutableIntStateOf(0) }
    var nextInstanceId by remember(activity.id, replayKey, sessionResetKey, currentStage) { mutableIntStateOf(100) }
    var targetIndex by remember(activity.id, replayKey, currentMode, currentWorld, currentNumberTier, sessionResetKey) {
        mutableIntStateOf(0)
    }

    val unpoppedPayloads = payloads.filter { it.id !in poppedIdsInSession }
    val isDiscoveryComplete = currentStage == BalloonStageType.DISCOVERY_EXPLORE &&
        poppedIdsInSession.size >= payloads.size && payloads.isNotEmpty()
    val isChallengeComplete = currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE &&
        challengeStepIndex >= challengeQueue.size && challengeQueue.isNotEmpty()
    val allItemsPoppedInSession = isDiscoveryComplete || isChallengeComplete

    val discoveredWorldIds = remember(activity.id) {
        mutableStateListOf(initialPair.first.id)
    }

    // Active Educational Reveal Overlay (Stage 3 & 4 of Pop, with moving Animal Surprise)
    var activeRevealPayload by remember(activity.id, replayKey) { mutableStateOf<BalloonLearningPayload?>(null) }
    var activeRevealIsAnimalSurprise by remember(activity.id, replayKey) { mutableStateOf(false) }
    var activeRevealTimerSec by remember(activity.id, replayKey) { mutableFloatStateOf(0f) }

    // Magical Balloon Adventures Surprise (every 5 pops)
    var activeMagicalSurprise by remember(activity.id, replayKey) { mutableStateOf<MagicalAdventureSurprise?>(null) }
    var activeSurpriseTimerSec by remember(activity.id, replayKey) { mutableFloatStateOf(0f) }

    val activeCelebrations = remember(activity.id, replayKey) { mutableStateListOf<ActivePopCelebration>() }

    val activeBalloons = remember(
        activity.id,
        replayKey,
        currentMode,
        currentWorld,
        currentNumberTier,
        currentStage,
        challengeSeed,
        visibleCount,
        sessionResetKey
    ) {
        mutableStateListOf<ActiveBalloonEntity>().apply {
            val usedIds = mutableSetOf<String>()
            val initialChallengeTarget = challengeQueue.firstOrNull()
            for (slot in 0 until visibleCount) {
                val item = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE && slot == 0 && initialChallengeTarget != null) {
                    initialChallengeTarget
                } else if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE && initialChallengeTarget != null) {
                    BalloonSpawner.selectChallengeDistractorPayload(
                        allPayloads = payloads,
                        targetPayload = initialChallengeTarget,
                        currentlyFloatingIds = usedIds,
                        seedIndex = slot + challengeSeed
                    )
                } else {
                    BalloonSpawner.selectNextUnpoppedPayload(
                        allPayloads = payloads,
                        poppedIds = emptySet(),
                        currentlyFloatingIds = usedIds,
                        seedIndex = slot
                    ) ?: payloads[slot % payloads.size]
                }
                usedIds.add(item.id)
                add(
                    BalloonSpawner.createBalloonForSlot(
                        instanceId = slot + 1,
                        slotIndex = slot,
                        totalSlots = visibleCount,
                        ageBand = ageBand,
                        speedMultiplier = adaptiveManager.balloonSpeedMultiplier,
                        payload = item,
                        startAtBottom = false
                    )
                )
            }
        }
    }

    // Active Target Payload:
    // - In VOICE_RANDOM_CHALLENGE: strictly the current item in the 15-item random challengeQueue
    // - In DISCOVERY_EXPLORE: an unpopped item (preferring one currently floating on screen)
    val indexedCandidate = payloads[targetIndex % payloads.size]
    val targetPayload = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
        challengeQueue.getOrElse(challengeStepIndex.coerceIn(0, (challengeQueue.size - 1).coerceAtLeast(0))) {
            payloads.first()
        }
    } else if (indexedCandidate.id !in poppedIdsInSession) {
        indexedCandidate
    } else {
        activeBalloons.firstOrNull {
            it.state != BalloonLifecycleState.COMPLETED && it.payload.id !in poppedIdsInSession
        }?.payload ?: unpoppedPayloads.firstOrNull() ?: payloads.first()
    }

    // Voice-Guided Random Challenge Step Controller:
    // Ensures the spoken target balloon is guaranteed to be on screen and announces the voice prompt
    LaunchedEffect(
        currentStage,
        challengeStepIndex,
        challengeSeed,
        currentNumberTier,
        currentMode,
        currentWorld,
        sessionResetKey
    ) {
        if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE && !isChallengeComplete) {
            val currentTarget = challengeQueue.getOrNull(challengeStepIndex) ?: return@LaunchedEffect
            val alreadyOnScreen = activeBalloons.any {
                it.state != BalloonLifecycleState.COMPLETED &&
                    it.state != BalloonLifecycleState.POPPING &&
                    it.state != BalloonLifecycleState.REVEALING &&
                    it.payload.id == currentTarget.id
            }
            if (!alreadyOnScreen && activeBalloons.isNotEmpty()) {
                val replaceIdx = (challengeStepIndex + challengeSeed.toInt()).mod(activeBalloons.size)
                val existing = activeBalloons[replaceIdx]
                nextInstanceId += 1
                activeBalloons[replaceIdx] = BalloonSpawner.createBalloonForSlot(
                    instanceId = nextInstanceId,
                    slotIndex = existing.slotIndex,
                    totalSlots = visibleCount,
                    ageBand = ageBand,
                    speedMultiplier = adaptiveManager.balloonSpeedMultiplier,
                    payload = currentTarget,
                    startAtBottom = false
                )
            }
            if (challengeStepIndex > 0) {
                kotlinx.coroutines.delay(950L)
            }
            viewModel.audio.speakInstruction(
                BalloonSpawner.buildVoiceChallengePrompt(currentTarget, currentLanguage),
                interrupt = challengeStepIndex == 0
            )
        }
    }

    var totalElapsedSec by remember(activity.id, replayKey) { mutableFloatStateOf(0f) }

    // 60 FPS Frame-Time Physics, Non-Repeating Spawner & Inactivity Hint Loop
    LaunchedEffect(
        activity.id,
        replayKey,
        currentMode,
        currentWorld,
        currentNumberTier,
        currentStage,
        challengeSeed,
        followRoadStream,
        reducedMotion,
        sessionResetKey
    ) {
        var lastFrameNanos = 0L
        while (true) {
            withFrameNanos { frameNanos ->
                val dt = if (lastFrameNanos == 0L) {
                    0.016f
                } else {
                    ((frameNanos - lastFrameNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                }
                lastFrameNanos = frameNanos
                totalElapsedSec += dt

                adaptiveManager.onFrameTick(dt)
                if (adaptiveManager.shouldSpeakInactivityHint && !allItemsPoppedInSession) {
                    adaptiveManager.markSpokenHintDelivered()
                    val hintPrompt = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                        BalloonSpawner.buildVoiceChallengePrompt(targetPayload, currentLanguage)
                    } else {
                        "Can you pop ${targetPayload.getDisplayTitle(currentLanguage)}?"
                    }
                    viewModel.audio.speakInstruction(hintPrompt, interrupt = false)
                }

                for (i in activeBalloons.indices) {
                    val b = activeBalloons[i]
                    BalloonPhysics.stepBalloon(
                        balloon = b,
                        deltaTimeSec = dt,
                        totalElapsedSec = totalElapsedSec,
                        followRoadStream = followRoadStream,
                        lowGravity = currentWorld.lowGravityDrift,
                        reducedMotion = reducedMotion
                    )
                    if (b.state == BalloonLifecycleState.COMPLETED && !allItemsPoppedInSession) {
                        val currentlyFloatingIds = activeBalloons
                            .filter { it.slotIndex != b.slotIndex && it.state != BalloonLifecycleState.COMPLETED }
                            .map { it.payload.id }
                            .toSet()

                        val nextPayload = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                            if (targetPayload.id !in currentlyFloatingIds) {
                                targetPayload
                            } else {
                                BalloonSpawner.selectChallengeDistractorPayload(
                                    allPayloads = payloads,
                                    targetPayload = targetPayload,
                                    currentlyFloatingIds = currentlyFloatingIds,
                                    seedIndex = nextInstanceId + b.slotIndex
                                )
                            }
                        } else {
                            val poppedSet = poppedIdsInSession.toSet()
                            if (targetPayload.id !in poppedSet && targetPayload.id !in currentlyFloatingIds) {
                                targetPayload
                            } else {
                                BalloonSpawner.selectNextUnpoppedPayload(
                                    allPayloads = payloads,
                                    poppedIds = poppedSet,
                                    currentlyFloatingIds = currentlyFloatingIds,
                                    seedIndex = nextInstanceId + b.slotIndex
                                )
                            }
                        }

                        if (nextPayload != null) {
                            nextInstanceId += 1
                            activeBalloons[i] = BalloonSpawner.createBalloonForSlot(
                                instanceId = nextInstanceId,
                                slotIndex = b.slotIndex,
                                totalSlots = visibleCount,
                                ageBand = ageBand,
                                speedMultiplier = adaptiveManager.balloonSpeedMultiplier,
                                payload = nextPayload,
                                startAtBottom = true
                            )
                        }
                    }
                }

                val iterator = activeCelebrations.iterator()
                while (iterator.hasNext()) {
                    val cel = iterator.next()
                    cel.elapsedSec += dt
                    if (cel.elapsedSec >= 0.9f) {
                        iterator.remove()
                    }
                }

                if (activeRevealPayload != null) {
                    activeRevealTimerSec += dt
                    if (activeRevealTimerSec >= 2.2f) {
                        activeRevealPayload = null
                        activeRevealTimerSec = 0f
                    }
                }

                if (activeMagicalSurprise != null) {
                    activeSurpriseTimerSec += dt
                    if (activeSurpriseTimerSec >= 3.2f) {
                        activeMagicalSurprise = null
                        activeSurpriseTimerSec = 0f
                    }
                }
            }
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "hint_pulse")
    val hintPulseScale by pulseTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("engine_balloon_pop"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Picture-First Compact Control Strip (Giant Icons, Minimal Words)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 9-World Picker Button (Icon-first)
            Surface(
                color = Color(currentWorld.accentHex).copy(alpha = 0.18f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(currentWorld.accentHex)),
                modifier = Modifier
                    .clickable {
                        showWorldSelectorOverlay = !showWorldSelectorOverlay
                        adaptiveManager.resetInactivity()
                    }
                    .testTag("balloon_world_selector_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🌍 ${currentWorld.emoji}", fontSize = 18.sp)
                    Text(
                        text = currentWorld.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = DeepSlateInk
                    )
                }
            }

            // Picture-First Target Clue Pill + Speaker
            Surface(
                color = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) Color(0xFFFEF08A) else SoftCreamCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) ActionPlayGreen else ActionListenAmber
                ),
                modifier = Modifier
                    .clickable {
                        adaptiveManager.resetInactivity()
                        if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                            viewModel.audio.speakInstruction(
                                BalloonSpawner.buildVoiceChallengePrompt(targetPayload, currentLanguage),
                                interrupt = true
                            )
                        } else {
                            viewModel.audio.speakLocalized(
                                nativeCaption = targetPayload.getDisplayTitle(currentLanguage),
                                nativeSpeech = targetPayload.getSpokenScript(currentLanguage),
                                phoneticFallbackSpeech = targetPayload.getPhoneticFallback(currentLanguage),
                                languageCode = currentLanguage.code
                            )
                        }
                    }
                    .testTag("balloon_clue_speaker_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) "🗣️🎯" else "🎯",
                        fontSize = 16.sp
                    )
                    Text(text = targetPayload.emoji, fontSize = 20.sp)
                    Text(
                        text = targetPayload.symbolOrBadge,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = DeepSlateInk
                    )
                    Text(text = "🔊", fontSize = 15.sp)
                }
            }

            // Level Stage & Number Tier Pill (Lv1 Explore / Lv2 Voice Challenge + 1-20, 21-40, 41-60, 61-100)
            Surface(
                color = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                    ActionPlayGreen.copy(alpha = 0.22f)
                } else {
                    ActionContinueBlue.copy(alpha = 0.16f)
                },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) ActionPlayGreen else ActionContinueBlue
                ),
                modifier = Modifier
                    .clickable {
                        adaptiveManager.resetInactivity()
                        if (isNumberMode) {
                            if (currentStage == BalloonStageType.DISCOVERY_EXPLORE) {
                                currentStage = BalloonStageType.VOICE_RANDOM_CHALLENGE
                                challengeSeed += 1
                                challengeStepIndex = 0
                                sessionResetKey += 1
                            } else {
                                val nextTier = currentNumberTier.nextTier() ?: BalloonNumberTier.TIER_1_20
                                currentNumberTier = nextTier
                                currentStage = BalloonStageType.DISCOVERY_EXPLORE
                                targetIndex = 0
                                sessionResetKey += 1
                                viewModel.audio.speakInstruction("Numbers ${nextTier.rangeLabel}")
                            }
                        } else {
                            if (currentStage == BalloonStageType.DISCOVERY_EXPLORE) {
                                currentStage = BalloonStageType.VOICE_RANDOM_CHALLENGE
                                challengeSeed += 1
                                challengeStepIndex = 0
                                sessionResetKey += 1
                            } else {
                                currentStage = BalloonStageType.DISCOVERY_EXPLORE
                                targetIndex = 0
                                sessionResetKey += 1
                                viewModel.audio.speakInstruction("Level 1 Explore")
                            }
                        }
                    }
                    .testTag("balloon_level_stage_pill")
            ) {
                val stageText = if (isNumberMode) {
                    val lvBadge = if (currentStage == BalloonStageType.DISCOVERY_EXPLORE) "Lv1" else "🎯Lv2"
                    "🔢${currentNumberTier.rangeLabel} $lvBadge"
                } else {
                    if (currentStage == BalloonStageType.DISCOVERY_EXPLORE) "🌟Lv1" else "🎯Lv2"
                }
                Text(
                    text = stageText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = DeepSlateInk,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                )
            }

            // Scrollable Picture-First Mode Bubbles
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BalloonLearningMode.entries.forEach { mode ->
                    val selected = mode == currentMode
                    Surface(
                        color = if (selected) ActionContinueBlue else PlaySurfaceWhite,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, if (selected) ActionContinueBlue else Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .clickable {
                                currentMode = mode
                                currentStage = BalloonStageType.DISCOVERY_EXPLORE
                                targetIndex = 0
                                adaptiveManager.resetInactivity()
                                viewModel.audio.speakInstruction(mode.title)
                            }
                            .testTag("balloon_learning_mode_${mode.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = mode.emoji, fontSize = 16.sp)
                            if (selected) {
                                Text(
                                    text = mode.shortLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PlaySurfaceWhite
                                )
                            }
                        }
                    }
                }
            }

            // Language Badge (EN | తె | हि)
            Surface(
                color = PlaySurfaceWhite,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, ActionContinueBlue),
                modifier = Modifier
                    .clickable {
                        val allLangs = BalloonNarrationLanguage.entries
                        currentLanguage = allLangs[(currentLanguage.ordinal + 1) % allLangs.size]
                        adaptiveManager.resetInactivity()
                        if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                            viewModel.audio.speakInstruction(
                                BalloonSpawner.buildVoiceChallengePrompt(targetPayload, currentLanguage),
                                interrupt = true
                            )
                        } else {
                            viewModel.audio.speakLocalized(
                                nativeCaption = targetPayload.getDisplayTitle(currentLanguage),
                                nativeSpeech = targetPayload.getSpokenScript(currentLanguage),
                                phoneticFallbackSpeech = targetPayload.getPhoneticFallback(currentLanguage),
                                languageCode = currentLanguage.code
                            )
                        }
                    }
                    .testTag("balloon_language_button")
            ) {
                Text(
                    text = currentLanguage.shortBadge,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = DeepSlateInk,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                )
            }

            // Road vs Sky Icon Toggle
            Surface(
                color = if (followRoadStream) ActionPlayGreen.copy(alpha = 0.18f) else ActionContinueBlue.copy(alpha = 0.18f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, if (followRoadStream) ActionPlayGreen else ActionContinueBlue),
                modifier = Modifier
                    .clickable {
                        followRoadStream = !followRoadStream
                        adaptiveManager.resetInactivity()
                    }
                    .testTag("balloon_mode_toggle")
            ) {
                Text(
                    text = if (followRoadStream) "🛣️" else "☁️",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }

            // Visual Pop & Remaining Deck Progress Counter (Click to Reset Session)
            Surface(
                color = ActionListenAmber.copy(alpha = 0.24f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, ActionListenAmber),
                modifier = Modifier
                    .clickable {
                        if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                            challengeSeed += 1
                            challengeStepIndex = 0
                        }
                        sessionResetKey += 1
                        targetIndex = 0
                        adaptiveManager.resetInactivity()
                    }
                    .testTag("balloon_session_progress_pill")
            ) {
                val progressLabel = if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                    "🎯${challengeStepIndex}/${challengeQueue.size}"
                } else {
                    "🎈${poppedIdsInSession.size}/${payloads.size}"
                }
                Text(
                    text = "$progressLabel 🏅${discoveredWorldIds.size}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = DeepSlateInk,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                )
            }
        }

        // 2. Giant Scenic Interactive World & Balloon Play Surface (92% of screen)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(currentWorld.skyTopHex))
        ) {
            val canvasWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
            val canvasHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)
            val density = LocalDensity.current

            // A. 60 FPS Scenic World Background, Winding Adventure Road & Burst Particles
            Canvas(modifier = Modifier.fillMaxSize()) {
                with(BalloonWorldCanvasRenderer) {
                    renderWorldBackground(
                        world = currentWorld,
                        followRoad = followRoadStream,
                        elapsedSec = totalElapsedSec,
                        roadStep = roadStep,
                        activeSurprise = activeMagicalSurprise,
                        activeCelebrations = activeCelebrations
                    )
                }
            }

            // B. Road Start Flag, Star Archway & Learning Buddy Traveler
            if (followRoadStream) {
                val startPt = BalloonPhysics.evaluateRoadCurve(0.06f, canvasWidthPx, canvasHeightPx)
                val archPt = BalloonPhysics.evaluateRoadCurve(0.93f, canvasWidthPx, canvasHeightPx)
                val buddyT = (0.08f + ((roadStep % 6) / 5f) * 0.82f).coerceIn(0.06f, 0.92f)
                val buddyPt = BalloonPhysics.evaluateRoadCurve(buddyT, canvasWidthPx, canvasHeightPx)
                val buddyHopPx = if (reducedMotion) 0f else abs(sin(totalElapsedSec * 3.5f)) * 9f

                Text(
                    text = "🚩",
                    fontSize = 26.sp,
                    modifier = Modifier.offset {
                        IntOffset(
                            x = (startPt.x - 16f).roundToInt().coerceAtLeast(6),
                            y = (startPt.y + 6f).roundToInt().coerceAtMost((canvasHeightPx - 40f).roundToInt())
                        )
                    }
                )

                Text(
                    text = "🌈🏛️",
                    fontSize = 28.sp,
                    modifier = Modifier.offset {
                        IntOffset(
                            x = (archPt.x - 28f).roundToInt().coerceAtLeast(6),
                            y = (archPt.y - 44f).roundToInt().coerceAtLeast(6)
                        )
                    }
                )

                // Learning Buddy advancing along the road checkpoints
                Surface(
                    color = PlaySurfaceWhite,
                    shape = CircleShape,
                    border = BorderStroke(3.dp, buddy.accentColor),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .size(50.dp)
                        .offset {
                            val halfPx = with(density) { 25.dp.toPx() }
                            IntOffset(
                                x = (buddyPt.x - halfPx).roundToInt(),
                                y = (buddyPt.y - halfPx - buddyHopPx).roundToInt()
                            )
                        }
                        .clickable {
                            viewModel.audio.speakInstruction(buddy.encouragement)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = buddy.emoji, fontSize = 26.sp)
                    }
                }
            }

            // C. Active Floating / Road-Streaming Balloons (True 3D Teardrop Balloon Shape, Non-Repeating Session)
            activeBalloons.forEachIndexed { slot, balloon ->
                if (balloon.state != BalloonLifecycleState.COMPLETED &&
                    balloon.state != BalloonLifecycleState.REVEALING
                ) {
                    val payload = balloon.payload
                    val isTargetMatch = payload.id == targetPayload.id
                    val isHintBalloon = isTargetMatch

                    val pulseFactor = if (isHintBalloon && adaptiveManager.shouldPulseHintBalloon) {
                        hintPulseScale
                    } else {
                        1.0f
                    }
                    val effectiveScale = (balloon.scale * adaptiveManager.balloonSizeMultiplier * pulseFactor)
                        .coerceIn(0.35f, 1.38f)

                    val boxWidthDp = if (isTargetMatch) 104.dp else 98.dp
                    val boxHeightDp = if (isTargetMatch) 142.dp else 134.dp
                    val boxWidthPx = with(density) { boxWidthDp.toPx() }
                    val boxHeightPx = with(density) { boxHeightDp.toPx() }

                    // Gentle horizontal wobble when a non-matching balloon is tapped in Voice Challenge mode
                    val wobbleOffsetPx = if (balloon.wobbleTimerSec > 0f && !reducedMotion) {
                        sin(balloon.wobbleTimerSec * 42f) * with(density) { 12.dp.toPx() }
                    } else {
                        0f
                    }

                    val centerX = balloon.xRatio * canvasWidthPx + wobbleOffsetPx
                    val centerY = balloon.yRatio * canvasHeightPx

                    Box(
                        modifier = Modifier
                            .offset {
                                val xPx = (centerX - boxWidthPx * 0.5f)
                                    .coerceIn(4f, (canvasWidthPx - boxWidthPx - 4f).coerceAtLeast(4f))
                                val yPx = (centerY - boxHeightPx * 0.5f)
                                    .coerceIn(4f, (canvasHeightPx - boxHeightPx - 4f).coerceAtLeast(4f))
                                IntOffset(xPx.roundToInt(), yPx.roundToInt())
                            }
                            .size(width = boxWidthDp, height = boxHeightDp)
                            .scale(effectiveScale)
                            .clickable(
                                enabled = !allItemsPoppedInSession && (
                                    balloon.state == BalloonLifecycleState.FLOATING ||
                                        balloon.state == BalloonLifecycleState.SPAWNING
                                    )
                            ) {
                                // In Level 2+ Voice Random Challenge, only the announced target balloon pops!
                                // Tapping a distractor triggers a gentle wobble and repeats the voice clue.
                                if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE && payload.id != targetPayload.id) {
                                    balloon.wobbleTimerSec = 0.48f
                                    adaptiveManager.onGentleMiss()
                                    viewModel.audio.speakInstruction(
                                        BalloonSpawner.buildGentleWobbleRetryPrompt(
                                            tapped = payload,
                                            target = targetPayload,
                                            lang = currentLanguage
                                        ),
                                        interrupt = true
                                    )
                                    return@clickable
                                }

                                balloon.state = BalloonLifecycleState.POPPING
                                balloon.stateTimerSec = 0f

                                poppedCount += 1
                                roadStep += 1

                                // Record popped item so it never repeats in this game session until reset
                                if (!poppedIdsInSession.contains(payload.id)) {
                                    poppedIdsInSession.add(payload.id)
                                }

                                if (currentStage == BalloonStageType.VOICE_RANDOM_CHALLENGE) {
                                    challengeStepIndex += 1
                                }

                                // Update Smart Learning Mastery map
                                masteryCounts[payload.id] = (masteryCounts[payload.id] ?: 0) + 1

                                val burstParticles = List(10) { idx ->
                                    PopBurstParticle(
                                        angleRad = (idx * 2.0 * PI / 10.0).toFloat(),
                                        speedRatio = 0.14f + (idx % 3) * 0.05f,
                                        colorHex = if (idx % 2 == 0) payload.balloonColorHex else 0xFFFACC15,
                                        radiusDp = 6f + (idx % 2) * 3f
                                    )
                                }
                                activeCelebrations.add(
                                    ActivePopCelebration(
                                        id = poppedCount,
                                        xRatio = balloon.xRatio,
                                        yRatio = balloon.yRatio,
                                        payload = payload,
                                        particles = burstParticles
                                    )
                                )

                                // Trigger Stage 3 Reveal (with Animal Surprise hop animation when applicable)
                                activeRevealPayload = payload
                                activeRevealIsAnimalSurprise = balloon.hasAnimalSurpriseBadge
                                activeRevealTimerSec = 0f

                                val pitchMult = 0.94f + (slot * 0.04f)
                                viewModel.audio.playPopAndSpeakLocalized(
                                    nativeCaption = payload.getDisplayTitle(currentLanguage),
                                    nativeSpeech = payload.getSpokenScript(currentLanguage),
                                    phoneticFallbackSpeech = payload.getPhoneticFallback(currentLanguage),
                                    languageCode = currentLanguage.code,
                                    pitchMultiplier = pitchMult
                                )

                                // Advance target to the next unpopped item in Level 1 Discovery
                                if (currentStage == BalloonStageType.DISCOVERY_EXPLORE) {
                                    val nextUnpoppedIdx = payloads.indices.firstOrNull { idx ->
                                        val candidate = payloads[(targetIndex + 1 + idx) % payloads.size]
                                        candidate.id !in poppedIdsInSession
                                    }
                                    if (nextUnpoppedIdx != null) {
                                        targetIndex = (targetIndex + 1 + nextUnpoppedIdx) % payloads.size
                                    }
                                }

                                adaptiveManager.onBalloonPopped(matchedChallengeTarget = isTargetMatch)

                                // Feature 3: Magical Balloon Adventures Surprise every 5 pops!
                                // Keep narration focused on the item currently being revealed (one voice at a time)
                                if (poppedCount % 5 == 0) {
                                    val surprises = MagicalAdventureSurprise.entries
                                    val unlocked = surprises[(poppedCount / 5 - 1) % surprises.size]
                                    activeMagicalSurprise = unlocked
                                    activeSurpriseTimerSec = 0f
                                    viewModel.onActivityMilestoneCompleted(
                                        activity = activity,
                                        hadRetriesInRound = false,
                                        speakMilestonePraise = false
                                    )
                                }
                            }
                            .testTag("balloon_slot_$slot"),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val showGiantSymbol = payload.categoryTag in listOf("Alphabet", "Phonics", "Number", "Word")
                        BalloonShapeGraphic(
                            balloonColorHex = payload.balloonColorHex,
                            isTargetMatch = isTargetMatch,
                            isTranslucentBubble = currentWorld.usesBubbleStyle,
                            swayPhase = if (reducedMotion) 0f else (totalElapsedSec * 2.4f + balloon.phaseOffset),
                            symbolOrBadge = payload.symbolOrBadge,
                            emoji = payload.emoji,
                            showGiantSymbol = showGiantSymbol,
                            hasAnimalSurpriseBadge = balloon.hasAnimalSurpriseBadge,
                            showPointingHand = isHintBalloon && adaptiveManager.shouldShowPointingHand
                        )
                    }
                }
            }

            // C2. Level Complete Celebration, Next Level Progression & Replay Modal
            if (allItemsPoppedInSession) {
                val nextTier = if (isNumberMode) currentNumberTier.nextTier() else null
                val headlineText = when {
                    isDiscoveryComplete && isNumberMode ->
                        "Level 1 Complete! Numbers ${currentNumberTier.rangeLabel} (${payloads.size}/${payloads.size})"
                    isDiscoveryComplete ->
                        "Level 1 Complete! You Popped All ${payloads.size} Balloons!"
                    isChallengeComplete && isNumberMode ->
                        "Voice Challenge Complete! (${challengeQueue.size}/${challengeQueue.size} in ${currentNumberTier.rangeLabel})"
                    else ->
                        "Voice Challenge Complete! (${challengeQueue.size}/${challengeQueue.size} Random Targets)"
                }
                val subText = when {
                    isDiscoveryComplete ->
                        "Ready for Level 2? Listen to the teacher and pop ${challengeQueue.size} random balloons!"
                    isChallengeComplete && nextTier != null ->
                        "Awesome listening! Unlock the next number tier (${nextTier.rangeLabel}) or play another random 15!"
                    else ->
                        "Super star ears! Play a new random set of ${challengeQueue.size} balloons!"
                }

                Surface(
                    color = PlaySurfaceWhite.copy(alpha = 0.97f),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(3.5.dp, ActionPlayGreen),
                    shadowElevation = 14.dp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .testTag("balloon_all_popped_card")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isDiscoveryComplete) "🎈🎉🏆" else "🗣️🎯🏆",
                            fontSize = 44.sp
                        )
                        Text(
                            text = headlineText,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepSlateInk.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Primary Next Level Action Button
                            Button(
                                onClick = {
                                    adaptiveManager.resetInactivity()
                                    if (isDiscoveryComplete) {
                                        // Transition from Level 1 (Explore All) -> Level 2 (15-item Random Voice Challenge)
                                        currentStage = BalloonStageType.VOICE_RANDOM_CHALLENGE
                                        challengeSeed += 1
                                        challengeStepIndex = 0
                                        sessionResetKey += 1
                                    } else if (isNumberMode && nextTier != null) {
                                        // Advance to next Number Tier (21-40, 41-60, 61-100)
                                        currentNumberTier = nextTier
                                        currentStage = BalloonStageType.DISCOVERY_EXPLORE
                                        targetIndex = 0
                                        sessionResetKey += 1
                                        viewModel.audio.speakInstruction(
                                            "Welcome to numbers ${nextTier.rangeLabel}! Let's explore!"
                                        )
                                    } else {
                                        // Generate a fresh random set of 15 items for the next Voice Challenge round
                                        challengeRoundNumber += 1
                                        challengeSeed += 1
                                        challengeStepIndex = 0
                                        sessionResetKey += 1
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .height(50.dp)
                                    .testTag("balloon_next_level_button")
                            ) {
                                val nextBtnLabel = when {
                                    isDiscoveryComplete ->
                                        "🎯 Next Level: Voice Challenge (${challengeQueue.size})"
                                    isNumberMode && nextTier != null ->
                                        "🚀 Next Level: Numbers ${nextTier.rangeLabel}"
                                    else ->
                                        "🎲 Next Random ${challengeQueue.size} Challenge"
                                }
                                Text(
                                    text = nextBtnLabel,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = PlaySurfaceWhite
                                )
                            }

                            // Secondary Replay / Reset Button
                            Button(
                                onClick = {
                                    adaptiveManager.resetInactivity()
                                    if (isDiscoveryComplete) {
                                        sessionResetKey += 1
                                        targetIndex = 0
                                        viewModel.audio.speakInstruction("Let's play Level 1 again!")
                                    } else if (isNumberMode && nextTier != null) {
                                        // Play another random 15 in the same number tier
                                        challengeRoundNumber += 1
                                        challengeSeed += 1
                                        challengeStepIndex = 0
                                        sessionResetKey += 1
                                    } else {
                                        // Return to Level 1 Discovery (or Tier 1-20 if at Tier 61-100)
                                        if (isNumberMode && currentNumberTier == BalloonNumberTier.TIER_61_100) {
                                            currentNumberTier = BalloonNumberTier.TIER_1_20
                                        }
                                        currentStage = BalloonStageType.DISCOVERY_EXPLORE
                                        sessionResetKey += 1
                                        targetIndex = 0
                                        viewModel.audio.speakInstruction("Let's explore from Level 1!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionContinueBlue),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .height(50.dp)
                                    .testTag("balloon_reset_session_button")
                            ) {
                                val replayLabel = when {
                                    isDiscoveryComplete -> "🔄 Replay Level 1"
                                    isNumberMode && nextTier != null -> "🎲 New Random 15"
                                    else -> "🔄 Level 1 Explore"
                                }
                                Text(
                                    text = replayLabel,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = PlaySurfaceWhite
                                )
                            }
                        }
                    }
                }
            }

            // D. Feature 3: Magical Balloon Adventures Surprise Overlay (Parachute Buddy / Butterfly Parade / Sticker)
            val surprise = activeMagicalSurprise
            if (surprise != null) {
                val floatOffset = if (reducedMotion) 0f else sin(totalElapsedSec * 5f) * 12f
                Surface(
                    color = Color(0xFFFEF08A),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(3.dp, ActionListenAmber),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .offset(y = floatOffset.dp)
                        .clickable { activeMagicalSurprise = null }
                        .testTag("balloon_magical_surprise_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = surprise.bannerEmoji, fontSize = 30.sp)
                        Text(
                            text = surprise.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepSlateInk
                        )
                    }
                }
            }

            // E. Stage 3 & 4 Picture-First Educational Reveal & Animated Animal Surprise Card
            val revealed = activeRevealPayload
            if (revealed != null) {
                val hopBounce = if (reducedMotion || !activeRevealIsAnimalSurprise) {
                    0f
                } else {
                    abs(sin(totalElapsedSec * 8f)) * 14f
                }
                Surface(
                    color = PlaySurfaceWhite.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(3.5.dp, Color(revealed.balloonColorHex)),
                    shadowElevation = 14.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                        .offset(y = (-hopBounce).dp)
                        .clickable { activeRevealPayload = null }
                        .testTag("balloon_educational_reveal_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(text = revealed.revealIllustrationEmoji, fontSize = 48.sp)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = revealed.getDisplayTitle(currentLanguage),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepSlateInk
                            )
                            if (revealed.countQuantity > 1) {
                                Text(
                                    text = List(revealed.countQuantity.coerceAtMost(10)) { revealed.emoji }
                                        .joinToString(" "),
                                    fontSize = 18.sp
                                )
                            }
                        }
                        if (activeRevealIsAnimalSurprise) {
                            Text(text = "🎉🐾", fontSize = 28.sp)
                        } else {
                            Text(text = "🌟", fontSize = 28.sp)
                        }
                    }
                }
            }

            // F. 9-World Picture-First Interactive Picker Overlay
            if (showWorldSelectorOverlay) {
                Surface(
                    color = DeepSlateInk.copy(alpha = 0.88f),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("balloon_world_picker_overlay")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌍 Pick a World",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = PlaySurfaceWhite
                            )
                            Button(
                                onClick = { showWorldSelectorOverlay = false },
                                colors = ButtonDefaults.buttonColors(containerColor = ActionPlayGreen),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text("▶ Play", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }

                        val worlds = BalloonWorldEnvironment.entries
                        for (rowIdx in 0 until 3) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (colIdx in 0 until 3) {
                                    val worldItem = worlds[rowIdx * 3 + colIdx]
                                    val isSelected = worldItem == currentWorld
                                    Surface(
                                        color = if (isSelected) Color(0xFFFEF08A) else PlaySurfaceWhite,
                                        shape = RoundedCornerShape(18.dp),
                                        border = BorderStroke(
                                            width = if (isSelected) 3.5.dp else 1.5.dp,
                                            color = Color(worldItem.accentHex)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .clickable {
                                                currentWorld = worldItem
                                                followRoadStream = worldItem.hasWindingRoad
                                                if (!discoveredWorldIds.contains(worldItem.id)) {
                                                    discoveredWorldIds.add(worldItem.id)
                                                }
                                                showWorldSelectorOverlay = false
                                                viewModel.audio.speakInstruction(worldItem.title)
                                            }
                                            .testTag("balloon_world_card_${worldItem.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = worldItem.emoji, fontSize = 34.sp)
                                            Spacer(Modifier.width(10.dp))
                                            Text(
                                                text = worldItem.title,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 16.sp,
                                                color = DeepSlateInk,
                                                maxLines = 1
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
}

/**
 * ============================================================================================
 * MODULE 7: BalloonRenderer
 * Custom path-based realistic balloon renderer used across all 13 learning modes & 9 worlds.
 * Replaces circular drawing with a custom Bézier path silhouette featuring:
 * - Tapered crown / top curve expanding into a full rounded upper dome
 * - Smoothly tapered lower body pinching into a narrow neck
 * - Flared tied bottom 'knot' and collar band
 * - Swaying S-curve ribbon string
 * - 3D volumetric radial shading and pure path-based specular highlights (no circular primitives)
 * ============================================================================================
 */
data class BalloonSilhouetteMetrics(
    val widthPx: Float,
    val heightPx: Float,
    val centerX: Float,
    val taperedTopY: Float,
    val upperDomeWidestY: Float,
    val lowerTaperNeckY: Float,
    val knotBottomY: Float,
    val stringTipY: Float,
    val halfTaperedTopWidth: Float,
    val halfUpperDomeWidth: Float,
    val halfNeckWidth: Float,
    val halfKnotWidth: Float
) {
    val bodyHeightPx: Float get() = lowerTaperNeckY - taperedTopY
    val knotHeightPx: Float get() = knotBottomY - lowerTaperNeckY
}

object BalloonRenderer {

    /**
     * Computes normalized path control metrics for a realistic balloon silhouette
     * with a tapered top crown, wide upper dome, tapered lower neck, and bottom knot.
     */
    fun computeMetrics(widthPx: Float, heightPx: Float): BalloonSilhouetteMetrics {
        val w = widthPx.coerceAtLeast(1f)
        val h = heightPx.coerceAtLeast(1f)
        return BalloonSilhouetteMetrics(
            widthPx = w,
            heightPx = h,
            centerX = w * 0.50f,
            taperedTopY = h * 0.03f,
            upperDomeWidestY = h * 0.33f,
            lowerTaperNeckY = h * 0.72f,
            knotBottomY = h * 0.80f,
            stringTipY = h * 0.98f,
            halfTaperedTopWidth = w * 0.28f,
            halfUpperDomeWidth = w * 0.44f,
            halfNeckWidth = w * 0.085f,
            halfKnotWidth = w * 0.135f
        )
    }

    /**
     * Builds the custom path-based balloon body with a gently tapered top crown,
     * full upper-middle dome, and smoothly tapered lower neck.
     */
    fun buildBalloonBodyPath(metrics: BalloonSilhouetteMetrics): Path {
        val cx = metrics.centerX
        val topY = metrics.taperedTopY
        val domeY = metrics.upperDomeWidestY
        val neckY = metrics.lowerTaperNeckY
        val halfTopW = metrics.halfTaperedTopWidth
        val halfDomeW = metrics.halfUpperDomeWidth
        val halfNeckW = metrics.halfNeckWidth

        return Path().apply {
            // Start at the tapered top crown apex
            moveTo(cx, topY)
            // Right tapered top crown curving out to widest upper dome shoulder
            cubicTo(
                cx + halfTopW, topY,
                cx + halfDomeW * 1.04f, topY + (domeY - topY) * 0.52f,
                cx + halfDomeW, domeY
            )
            // Right lower body tapering smoothly inward to the narrow pinched neck
            cubicTo(
                cx + halfDomeW * 0.95f, domeY + (neckY - domeY) * 0.50f,
                cx + halfNeckW * 1.90f, neckY - (neckY - domeY) * 0.15f,
                cx + halfNeckW, neckY
            )
            // Curved base across the pinched neck
            quadraticTo(
                cx, neckY + metrics.heightPx * 0.012f,
                cx - halfNeckW, neckY
            )
            // Left lower body tapering upward from the narrow neck to widest dome shoulder
            cubicTo(
                cx - halfNeckW * 1.90f, neckY - (neckY - domeY) * 0.15f,
                cx - halfDomeW * 0.95f, domeY + (neckY - domeY) * 0.50f,
                cx - halfDomeW, domeY
            )
            // Left tapered top crown curving back to the top apex
            cubicTo(
                cx - halfDomeW * 1.04f, topY + (domeY - topY) * 0.52f,
                cx - halfTopW, topY,
                cx, topY
            )
            close()
        }
    }

    /**
     * Builds the flared bottom 'knot' path attached beneath the pinched balloon neck.
     */
    fun buildBalloonKnotPath(metrics: BalloonSilhouetteMetrics): Path {
        val cx = metrics.centerX
        val neckY = metrics.lowerTaperNeckY
        val knotBottomY = metrics.knotBottomY
        val halfNeckW = metrics.halfNeckWidth
        val halfKnotW = metrics.halfKnotWidth

        return Path().apply {
            moveTo(cx - halfNeckW * 0.88f, neckY)
            lineTo(cx + halfNeckW * 0.88f, neckY)
            // Right flared lobe of the tied rubber knot
            cubicTo(
                cx + halfKnotW * 1.12f, neckY + (knotBottomY - neckY) * 0.45f,
                cx + halfKnotW, knotBottomY - (knotBottomY - neckY) * 0.12f,
                cx + halfKnotW * 0.82f, knotBottomY
            )
            // Rounded bottom lip of the knot
            quadraticTo(
                cx, knotBottomY + metrics.heightPx * 0.020f,
                cx - halfKnotW * 0.82f, knotBottomY
            )
            // Left flared lobe of the tied rubber knot
            cubicTo(
                cx - halfKnotW, knotBottomY - (knotBottomY - neckY) * 0.12f,
                cx - halfKnotW * 1.12f, neckY + (knotBottomY - neckY) * 0.45f,
                cx - halfNeckW * 0.88f, neckY
            )
            close()
        }
    }

    /**
     * Builds the snug rubber collar tie band path at the balloon's neck.
     */
    fun buildNeckCollarPath(metrics: BalloonSilhouetteMetrics): Path {
        val cx = metrics.centerX
        val neckY = metrics.lowerTaperNeckY
        val collarHalfW = metrics.halfNeckWidth * 1.08f
        val collarHalfH = metrics.heightPx * 0.012f
        return Path().apply {
            moveTo(cx - collarHalfW, neckY - collarHalfH)
            quadraticTo(cx, neckY - collarHalfH * 1.3f, cx + collarHalfW, neckY - collarHalfH)
            lineTo(cx + collarHalfW, neckY + collarHalfH)
            quadraticTo(cx, neckY + collarHalfH * 1.3f, cx - collarHalfW, neckY + collarHalfH)
            close()
        }
    }

    /**
     * Builds the swaying S-curve ribbon string path hanging from the bottom knot.
     */
    fun buildBalloonStringPath(metrics: BalloonSilhouetteMetrics, swayPhase: Float): Path {
        val cx = metrics.centerX
        val knotBottomY = metrics.knotBottomY
        val tipY = metrics.stringTipY
        val swayPx = sin(swayPhase) * (metrics.widthPx * 0.07f)

        return Path().apply {
            moveTo(cx, knotBottomY)
            cubicTo(
                cx + swayPx, knotBottomY + (tipY - knotBottomY) * 0.36f,
                cx - swayPx * 0.85f, knotBottomY + (tipY - knotBottomY) * 0.72f,
                cx + swayPx * 0.45f, tipY
            )
        }
    }

    /**
     * Builds the curved crescent specular highlight path along the upper-left tapered dome.
     */
    fun buildSpecularHighlightPath(metrics: BalloonSilhouetteMetrics): Path {
        val cx = metrics.centerX
        val topY = metrics.taperedTopY
        val domeY = metrics.upperDomeWidestY
        val halfDomeW = metrics.halfUpperDomeWidth

        return Path().apply {
            moveTo(cx - halfDomeW * 0.64f, domeY * 0.88f)
            quadraticTo(
                cx - halfDomeW * 0.60f, topY + (domeY - topY) * 0.22f,
                cx - halfDomeW * 0.16f, topY + (domeY - topY) * 0.14f
            )
        }
    }

    /**
     * Builds the secondary path-based teardrop specular reflection below the main highlight arc.
     */
    fun buildSecondaryShinePath(metrics: BalloonSilhouetteMetrics): Path {
        val cx = metrics.centerX - metrics.halfUpperDomeWidth * 0.60f
        val cy = metrics.upperDomeWidestY * 1.06f
        val rx = metrics.widthPx * 0.032f
        val ry = metrics.heightPx * 0.024f

        return Path().apply {
            moveTo(cx, cy - ry)
            cubicTo(cx + rx, cy - ry, cx + rx, cy + ry, cx, cy + ry)
            cubicTo(cx - rx, cy + ry, cx - rx, cy - ry, cx, cy - ry)
            close()
        }
    }

    /**
     * Renders the complete path-based 3D balloon (body, tapered crown, bottom knot, collar,
     * specular shine, and swaying string) onto any [DrawScope].
     */
    fun DrawScope.renderBalloon(
        balloonColorHex: Long,
        isTargetMatch: Boolean,
        isTranslucentBubble: Boolean,
        swayPhase: Float
    ) {
        val metrics = computeMetrics(size.width, size.height)
        val baseColor = Color(balloonColorHex)
        val bodyAlpha = if (isTranslucentBubble) 0.88f else 1.0f
        val lighterTint = Color(
            red = (baseColor.red + (1f - baseColor.red) * 0.46f).coerceIn(0f, 1f),
            green = (baseColor.green + (1f - baseColor.green) * 0.46f).coerceIn(0f, 1f),
            blue = (baseColor.blue + (1f - baseColor.blue) * 0.46f).coerceIn(0f, 1f),
            alpha = bodyAlpha
        )
        val deeperShade = Color(
            red = (baseColor.red * 0.70f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.70f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.70f).coerceIn(0f, 1f),
            alpha = bodyAlpha
        )

        val bodyPath = buildBalloonBodyPath(metrics)
        val knotPath = buildBalloonKnotPath(metrics)
        val collarPath = buildNeckCollarPath(metrics)
        val stringPath = buildBalloonStringPath(metrics, swayPhase)
        val primaryShinePath = buildSpecularHighlightPath(metrics)
        val secondaryShinePath = buildSecondaryShinePath(metrics)

        // 1. Swaying S-curve string (drawn behind the bottom knot)
        drawPath(
            path = stringPath,
            color = PlaySurfaceWhite.copy(alpha = 0.90f),
            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        )

        // 2. Flared bottom 'knot' beneath the tapered neck
        drawPath(
            path = knotPath,
            color = deeperShade
        )
        drawPath(
            path = knotPath,
            color = if (isTargetMatch) Color(0xFFFDE047) else PlaySurfaceWhite.copy(alpha = 0.86f),
            style = Stroke(width = 1.6.dp.toPx(), join = StrokeJoin.Round)
        )

        // 3. Custom path-based balloon body with 3D volumetric radial gradient
        drawPath(
            path = bodyPath,
            brush = Brush.radialGradient(
                colors = listOf(lighterTint, baseColor.copy(alpha = bodyAlpha), deeperShade),
                center = Offset(
                    metrics.centerX - metrics.halfUpperDomeWidth * 0.32f,
                    metrics.taperedTopY + metrics.bodyHeightPx * 0.28f
                ),
                radius = metrics.halfUpperDomeWidth * 1.68f
            )
        )

        // 4. Crisp outer contour stroke (golden glow when target match)
        drawPath(
            path = bodyPath,
            color = if (isTargetMatch) Color(0xFFFDE047) else PlaySurfaceWhite.copy(alpha = 0.92f),
            style = Stroke(
                width = if (isTargetMatch) 3.8.dp.toPx() else 2.2.dp.toPx(),
                join = StrokeJoin.Round
            )
        )

        // 5. Snug rubber neck collar tie band
        drawPath(
            path = collarPath,
            color = PlaySurfaceWhite.copy(alpha = 0.78f)
        )

        // 6. Path-based glossy specular reflection arc & secondary shine droplet
        drawPath(
            path = primaryShinePath,
            color = PlaySurfaceWhite.copy(alpha = 0.54f),
            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = secondaryShinePath,
            color = PlaySurfaceWhite.copy(alpha = 0.46f)
        )
    }
}

/**
 * Composable wrapper that renders a realistic balloon via [BalloonRenderer] and centers
 * the learning payload (Letter, Number, Word, or Picture) inside the upper balloon dome.
 */
@Composable
fun BalloonShapeGraphic(
    balloonColorHex: Long,
    isTargetMatch: Boolean,
    isTranslucentBubble: Boolean,
    swayPhase: Float,
    symbolOrBadge: String,
    emoji: String,
    showGiantSymbol: Boolean,
    hasAnimalSurpriseBadge: Boolean,
    showPointingHand: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            with(BalloonRenderer) {
                renderBalloon(
                    balloonColorHex = balloonColorHex,
                    isTargetMatch = isTargetMatch,
                    isTranslucentBubble = isTranslucentBubble,
                    swayPhase = swayPhase
                )
            }
        }

        // Center the Letter / Number / Illustration cleanly inside the upper balloon dome
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTargetMatch) 98.dp else 92.dp)
                .padding(top = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (showGiantSymbol) {
                    Text(
                        text = symbolOrBadge,
                        color = PlaySurfaceWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (symbolOrBadge.length <= 2) 32.sp else 19.sp
                    )
                    Text(text = emoji, fontSize = 19.sp)
                } else {
                    Text(text = emoji, fontSize = 40.sp)
                }
            }

            if (hasAnimalSurpriseBadge) {
                Text(
                    text = "✨",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 0.dp)
                )
            }

            if (showPointingHand) {
                Text(
                    text = "👆",
                    fontSize = 28.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-2).dp, y = 6.dp)
                )
            }
        }
    }
}


