package com.example.garden

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AgeBand
import com.example.data.ProfileSettingsEntity

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
    fun seedTarget(level: String): Int = when (level) { "2-3" -> 3; "5-6" -> 8; else -> 5 }
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
