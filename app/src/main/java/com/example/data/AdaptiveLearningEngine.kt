package com.example.data

import com.example.model.AgeBand
import kotlin.math.roundToInt

/**
 * Non-punitive Adaptive Learning Path Engine for ages 2–6.
 * Combines the baseline selected AgeBand (2–3, 4–5, 5–6) with local, non-punitive
 * performance indicators (smooth first-try rounds vs. gentle retry/show-me requests)
 * to compute real-time activity scaffolding:
 * - Effective choice count (2 to 6 choices)
 * - Tracing path tolerance in dp & coverage threshold (52% to 78%)
 * - Memory & puzzle grid complexity (2x2 up to 3x4)
 * - Automatic visual glow hints ("Gentle Scaffolding") when a child taps a wrong option twice
 */
data class AdaptiveChallengeParameters(
    val baseAgeBand: AgeBand,
    val adaptiveTierDelta: Int, // -1 = Gentle Support, 0 = Age Baseline, +1 = Stretch Explorer
    val effectiveChoiceCount: Int,
    val tracingToleranceDp: Float,
    val tracingCoverageRequired: Float,
    val memoryPairCount: Int,
    val showVisualScaffoldGlow: Boolean,
    val autoSpeakHintsOnSpawn: Boolean,
    val adaptiveStatusLabel: String,
    val parentExplanation: String
)

object AdaptiveLearningEngine {

    fun computeParameters(
        ageBand: AgeBand,
        autoAdaptEnabled: Boolean,
        progress: ActivityProgressEntity?,
        recentSessionRetries: Int = 0,
        recentSessionSmoothWins: Int = 0
    ): AdaptiveChallengeParameters {
        if (!autoAdaptEnabled) {
            return baselineForAge(ageBand)
        }

        val savedDelta = progress?.adaptiveComfortDelta ?: 0
        val liveDelta = when {
            recentSessionRetries >= 2 -> -1
            recentSessionSmoothWins >= 3 -> 1
            else -> savedDelta
        }.coerceIn(-1, 1)

        val baseChoices = ageBand.choiceCount
        val effectiveChoices = (baseChoices + liveDelta).coerceIn(2, 5)

        val baseTolerance = ageBand.tracingToleranceDp
        // If liveDelta == -1 (needs support), increase tolerance by +12dp; if +1 (mastery), tighten by -6dp
        val effectiveTolerance = (baseTolerance - (liveDelta * 8f)).coerceIn(24f, 68f)

        val baseCoverage = when (ageBand) {
            AgeBand.TODDLER_2_3 -> 0.55f
            AgeBand.PRESCHOOL_4_5 -> 0.66f
            AgeBand.KINDER_5_6 -> 0.74f
        }
        val effectiveCoverage = (baseCoverage + (liveDelta * 0.05f)).coerceIn(0.50f, 0.80f)

        val effectiveMemoryPairs = (ageBand.memoryPairs + liveDelta).coerceIn(2, 6)
        val showVisualGlow = liveDelta < 0 || recentSessionRetries >= 1 || ageBand == AgeBand.TODDLER_2_3

        val statusLabel = when (liveDelta) {
            -1 -> "${ageBand.label} • Gentle Support"
            1 -> "${ageBand.label} • Confident Explorer"
            else -> "${ageBand.label} • Balanced Pace"
        }

        val explanation = when (liveDelta) {
            -1 -> "Offering fewer distractors ($effectiveChoices choices), wider tracing guides (${effectiveTolerance.roundToInt()}dp), and warm visual hints."
            1 -> "Expanding challenge gently ($effectiveChoices choices & $effectiveMemoryPairs pairs) after smooth completions."
            else -> "Matched to ${ageBand.label} baseline ($effectiveChoices choices, ${effectiveTolerance.roundToInt()}dp tracing tolerance)."
        }

        return AdaptiveChallengeParameters(
            baseAgeBand = ageBand,
            adaptiveTierDelta = liveDelta,
            effectiveChoiceCount = effectiveChoices,
            tracingToleranceDp = effectiveTolerance,
            tracingCoverageRequired = effectiveCoverage,
            memoryPairCount = effectiveMemoryPairs,
            showVisualScaffoldGlow = showVisualGlow,
            autoSpeakHintsOnSpawn = ageBand == AgeBand.TODDLER_2_3 || liveDelta < 0,
            adaptiveStatusLabel = statusLabel,
            parentExplanation = explanation
        )
    }

    fun computeUpdatedDelta(
        currentDelta: Int,
        smoothSuccessCount: Int,
        gentleSupportCount: Int,
        hadRetriesInRound: Boolean
    ): Int {
        return when {
            hadRetriesInRound && gentleSupportCount >= 2 -> (currentDelta - 1).coerceAtLeast(-1)
            !hadRetriesInRound && smoothSuccessCount >= 2 -> (currentDelta + 1).coerceAtMost(1)
            !hadRetriesInRound && currentDelta < 0 -> 0
            else -> currentDelta.coerceIn(-1, 1)
        }
    }

    private fun baselineForAge(ageBand: AgeBand): AdaptiveChallengeParameters {
        val coverage = when (ageBand) {
            AgeBand.TODDLER_2_3 -> 0.55f
            AgeBand.PRESCHOOL_4_5 -> 0.66f
            AgeBand.KINDER_5_6 -> 0.74f
        }
        return AdaptiveChallengeParameters(
            baseAgeBand = ageBand,
            adaptiveTierDelta = 0,
            effectiveChoiceCount = ageBand.choiceCount,
            tracingToleranceDp = ageBand.tracingToleranceDp,
            tracingCoverageRequired = coverage,
            memoryPairCount = ageBand.memoryPairs,
            showVisualScaffoldGlow = ageBand == AgeBand.TODDLER_2_3,
            autoSpeakHintsOnSpawn = ageBand == AgeBand.TODDLER_2_3,
            adaptiveStatusLabel = "${ageBand.label} • Fixed Age Baseline",
            parentExplanation = "Using fixed ${ageBand.label} settings (${ageBand.choiceCount} choices, ${ageBand.tracingToleranceDp.roundToInt()}dp tracing)."
        )
    }
}
