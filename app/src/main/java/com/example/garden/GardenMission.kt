package com.example.garden

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.audio.VoicePerformanceCategory
import com.example.data.ProfileSettingsEntity
import com.example.model.AgeBand

/** Saved after each action. Levels are fixed for one adventure, and refreshed on replay. */
@Entity(tableName = "garden_progress")
data class GardenProgress(
    @PrimaryKey val id: Int = 1,
    val stage: Int = 0,
    val completedGardens: Int = 0,
    val readingIndex: Int = 0,
    val seedCount: Int = 0,
    val hintsMask: Int = 0,
    val retriesMask: Int = 0,
    val readingLevelId: String = "4-5",
    val mathLevelId: String = "4-5"
)

enum class LearningSubject { READING, MATH }

enum class GardenSceneStage(val id: String, val label: String) {
    BARE_SOIL("bare_soil", "Bare soil ready for planting"),
    SPROUT("sprout", "Green sprout growing in the pot"),
    BLOOM("bloom", "Golden sunflower in full bloom")
}

enum class SunnyPose(val id: String, val label: String, val durationMs: Int) {
    IDLE("idle", "Sunny standing ready", 0),
    SHORT_RUN("short_run", "Sunny running to the next garden stop", 700),
    WAVE("wave", "Sunny waving hello", 450),
    FLAG_PLANT("flag_plant", "Sunny planting the garden completion flag", 650)
}

enum class GardenStopState(
    val id: String,
    val badgeSymbol: String,
    val statusLabel: String
) {
    FINISHED("finished", "⚑ ✓", "Finished"),
    CURRENT("current", "▶ ★", "Current stop"),
    UP_NEXT("up_next", "○", "Up next")
}

data class GardenPromptSpec(
    val clipId: String,
    val visibleText: String,
    val spokenText: String,
    val languageCode: String,
    val category: VoicePerformanceCategory,
    val englishFallbackClipId: String,
    val englishFallbackText: String
)

fun ProfileSettingsEntity.subjectLevel(subject: LearningSubject): AgeBand {
    val override = if (subject == LearningSubject.READING) readingLevelId else mathLevelId
    return AgeBand.fromId(override.ifBlank { ageBandId })
}

sealed interface GardenAction {
    data class ReadingChoice(val value: String) : GardenAction
    data object AddSeed : GardenAction
    data object RemoveSeed : GardenAction
    data object PlantSeeds : GardenAction
    data class NeedChoice(val value: String) : GardenAction
    data class StoryChoice(val value: String) : GardenAction
    data object Hint : GardenAction
}

object GardenMission {
    val stageNames = listOf("Find a seed", "Plant seeds", "Find sunlight", "Water the garden")

    fun readingSequence(level: String): List<String> = when (level) {
        "2-3" -> listOf("S")
        "5-6" -> listOf("SUN", "SEED")
        else -> listOf("S", "U", "N")
    }

    fun seedTarget(level: String): Int = when (level) {
        "2-3" -> 3
        "5-6" -> 8
        else -> 5
    }

    fun sceneStageFor(stage: Int): GardenSceneStage = when {
        stage >= 4 -> GardenSceneStage.BLOOM
        stage >= 2 -> GardenSceneStage.SPROUT
        else -> GardenSceneStage.BARE_SOIL
    }

    fun stopStateFor(currentStage: Int, stopIndex: Int): GardenStopState = when {
        currentStage > stopIndex -> GardenStopState.FINISHED
        currentStage == stopIndex -> GardenStopState.CURRENT
        else -> GardenStopState.UP_NEXT
    }

    fun englishPromptFor(state: GardenProgress): String = when (state.stage) {
        0 -> when (state.readingLevelId) {
            "2-3" -> "Seed starts with S. Find S."
            "5-6" -> if (state.readingIndex == 0) "Plants need sunlight. Find SUN." else "We plant a seed. Find SEED."
            else -> "Build SUN. Find ${readingSequence(state.readingLevelId)[state.readingIndex.coerceIn(0, 2)]}."
        }
        1 -> if (state.mathLevelId == "5-6") {
            "Plant three seeds, then five more. How many altogether?"
        } else {
            "Put ${seedTarget(state.mathLevelId)} seeds in the pot."
        }
        2 -> "What gives the plant light?"
        3 -> "Sunny planted seeds. The soil is dry. What should Sunny give the seeds?"
        else -> "Your garden is growing! You helped Sunny read, count and care for a plant."
    }

    fun englishClipIdFor(state: GardenProgress): String = when (state.stage) {
        0 -> when (state.readingLevelId) {
            "2-3" -> "garden_s0_read_2_3_find_s_en"
            "5-6" -> if (state.readingIndex == 0) "garden_s0_read_5_6_word_sun_en" else "garden_s0_read_5_6_word_seed_en"
            else -> when (state.readingIndex) {
                0 -> "garden_s0_read_4_5_sun_s_en"
                1 -> "garden_s0_read_4_5_sun_u_en"
                else -> "garden_s0_read_4_5_sun_n_en"
            }
        }
        1 -> when (state.mathLevelId) {
            "2-3" -> "sunny_count_three"
            "5-6" -> "garden_s1_math_5_6_add_8_en"
            else -> "garden_s1_math_4_5_count_5_en"
        }
        2 -> "garden_s2_need_light_en"
        3 -> "garden_s3_story_water_en"
        else -> "garden_s4_complete_en"
    }

    fun teluguPromptFor(state: GardenProgress): String = when (state.stage) {
        1 -> if (state.mathLevelId == "5-6") {
            "మూడు విత్తనాలు, తరువాత మరో ఐదు విత్తనాలు నాటు. మొత్తం ఎన్ని?"
        } else {
            "కుండలో ${seedTarget(state.mathLevelId)} విత్తనాలు వేయి."
        }
        2 -> "మొక్కకు వెలుతురు ఏది ఇస్తుంది?"
        3 -> "సన్నీ విత్తనాలు నాటింది. మట్టి పొడిగా ఉంది. విత్తనాలకు ఏమి ఇవ్వాలి?"
        else -> "నీ తోట పెరుగుతోంది! మొక్కను పెంచడానికి సన్నీకి సహాయం చేశావు."
    }

    fun teluguClipIdFor(state: GardenProgress): String = when (state.stage) {
        1 -> when (state.mathLevelId) {
            "2-3" -> "garden_count_3_te"
            "5-6" -> "garden_count_8_te"
            else -> "garden_count_5_te"
        }
        2 -> "garden_light_te"
        3 -> "garden_story_te"
        else -> "garden_complete_te"
    }

    /**
     * Resolves the exact [GardenPromptSpec] for the current stage, level, and language setting.
     * - Stage 0 (English phonics & reading) always stays in English (`languageCode = "en"`).
     * - Stages 1..4 use Telugu when `requestedLanguage == "te"` AND `teluguAvailable == true`,
     *   falling back to the matching English prompt and clip ID when Telugu speech is unavailable
     *   so the visible prompt text always matches the spoken language.
     */
    fun promptSpecFor(
        state: GardenProgress,
        requestedLanguage: String = "en",
        teluguAvailable: Boolean = false
    ): GardenPromptSpec {
        val enText = englishPromptFor(state)
        val enClipId = englishClipIdFor(state)
        val category = when (state.stage) {
            3 -> VoicePerformanceCategory.STORY
            4 -> VoicePerformanceCategory.SUCCESS
            else -> VoicePerformanceCategory.INSTRUCTION
        }
        val useTelugu = requestedLanguage == "te" && state.stage != 0
        if (!useTelugu) {
            return GardenPromptSpec(
                clipId = enClipId,
                visibleText = enText,
                spokenText = enText,
                languageCode = "en",
                category = category,
                englishFallbackClipId = enClipId,
                englishFallbackText = enText
            )
        }
        val teText = teluguPromptFor(state)
        val teClipId = teluguClipIdFor(state)
        return GardenPromptSpec(
            clipId = if (teluguAvailable) teClipId else enClipId,
            visibleText = if (teluguAvailable) teText else enText,
            spokenText = teText,
            languageCode = if (teluguAvailable) "te" else "en",
            category = category,
            englishFallbackClipId = enClipId,
            englishFallbackText = enText
        )
    }

    fun hintSpecFor(state: GardenProgress): GardenPromptSpec {
        val text = when (state.stage) {
            0 -> "Look for ${readingSequence(state.readingLevelId)[state.readingIndex.coerceIn(0, readingSequence(state.readingLevelId).lastIndex)]}."
            1 -> "Touch one seed at a time. We need ${seedTarget(state.mathLevelId)}."
            2 -> "Look up in the daytime sky."
            else -> "Dry soil needs water."
        }
        val clipId = when (state.stage) {
            0 -> "garden_hint_s0_${state.readingLevelId.replace("-", "_")}_${state.readingIndex}_en"
            1 -> "garden_hint_s1_${state.mathLevelId.replace("-", "_")}_en"
            2 -> "garden_hint_s2_light_en"
            else -> "garden_hint_s3_water_en"
        }
        return GardenPromptSpec(
            clipId = clipId,
            visibleText = text,
            spokenText = text,
            languageCode = "en",
            category = VoicePerformanceCategory.INSTRUCTION,
            englishFallbackClipId = clipId,
            englishFallbackText = text
        )
    }

    fun fresh(settings: ProfileSettingsEntity) = GardenProgress(
        readingLevelId = settings.subjectLevel(LearningSubject.READING).id,
        mathLevelId = settings.subjectLevel(LearningSubject.MATH).id
    )

    fun reduce(state: GardenProgress, action: GardenAction): GardenProgress {
        if (state.stage >= 4) return state
        fun retry() = state.copy(retriesMask = state.retriesMask or (1 shl state.stage))
        return when (action) {
            GardenAction.Hint -> state.copy(hintsMask = state.hintsMask or (1 shl state.stage))
            is GardenAction.ReadingChoice -> {
                if (state.stage != 0) return state
                val sequence = readingSequence(state.readingLevelId)
                if (action.value != sequence.getOrNull(state.readingIndex)) retry()
                else if (state.readingIndex + 1 == sequence.size) state.copy(stage = 1)
                else state.copy(readingIndex = state.readingIndex + 1)
            }
            GardenAction.AddSeed -> if (state.stage == 1) state.copy(seedCount = (state.seedCount + 1).coerceAtMost(10)) else state
            GardenAction.RemoveSeed -> if (state.stage == 1) state.copy(seedCount = (state.seedCount - 1).coerceAtLeast(0)) else state
            GardenAction.PlantSeeds -> if (state.stage != 1) state else if (state.seedCount == seedTarget(state.mathLevelId)) state.copy(stage = 2) else retry()
            is GardenAction.NeedChoice -> if (state.stage != 2) state else if (action.value == "sun") state.copy(stage = 3) else retry()
            is GardenAction.StoryChoice -> if (state.stage != 3) state else if (action.value == "water") state.copy(stage = 4, completedGardens = state.completedGardens + 1) else retry()
        }
    }
}
