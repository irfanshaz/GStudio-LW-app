package com.example.garden

import com.example.data.ProfileSettingsEntity
import org.junit.Assert.*
import org.junit.Test

class GardenMissionTest {
    @Test fun independentLevelsAndLegacyDefaults() {
        val legacy = ProfileSettingsEntity(ageBandId = "2-3")
        assertEquals("2-3", legacy.subjectLevel(LearningSubject.READING).id)
        val split = legacy.copy(mathLevelId = "5-6")
        val mission = GardenMission.fresh(split)
        assertEquals("2-3", mission.readingLevelId)
        assertEquals("5-6", mission.mathLevelId)
        assertEquals(8, GardenMission.seedTarget(mission.mathLevelId))
    }
    @Test fun wrongAnswersAndHintsDoNotCompleteStops() {
        var state = GardenProgress(readingLevelId = "2-3")
        state = GardenMission.reduce(state, GardenAction.ReadingChoice("M"))
        state = GardenMission.reduce(state, GardenAction.Hint)
        assertEquals(0, state.stage)
        assertEquals(1, state.hintsMask)
        assertEquals(1, state.retriesMask)
        assertEquals(state, GardenMission.reduce(state, GardenAction.StoryChoice("water")))
    }
    @Test fun fullMissionRequiresExactCountAndRetainsEvidence() {
        var state = GardenProgress(readingLevelId = "2-3", mathLevelId = "5-6")
        state = GardenMission.reduce(state, GardenAction.ReadingChoice("S"))
        state = GardenMission.reduce(state, GardenAction.PlantSeeds)
        assertEquals(1, state.stage)
        repeat(8) { state = GardenMission.reduce(state, GardenAction.AddSeed) }
        state = GardenMission.reduce(state, GardenAction.PlantSeeds)
        assertEquals(2, state.stage)
        state = GardenMission.reduce(state, GardenAction.NeedChoice("sun"))
        state = GardenMission.reduce(state, GardenAction.StoryChoice("water"))
        assertEquals(4, state.stage)
        assertEquals(2, state.retriesMask)
        assertEquals(state, GardenMission.reduce(state, GardenAction.AddSeed))
    }
    @Test fun buildingWordsRequiresEveryLetterInOrder() {
        var state = GardenProgress()
        state = GardenMission.reduce(state, GardenAction.ReadingChoice("N"))
        assertEquals(0, state.readingIndex)
        listOf("S", "U", "N").forEach { state = GardenMission.reduce(state, GardenAction.ReadingChoice(it)) }
        assertEquals(1, state.stage)
    }
    @Test fun seedBoundsAndUndo() {
        var state = GardenProgress(stage = 1)
        assertEquals(0, GardenMission.reduce(state, GardenAction.RemoveSeed).seedCount)
        repeat(30) { state = GardenMission.reduce(state, GardenAction.AddSeed) }
        assertEquals(10, state.seedCount)
        assertEquals(9, GardenMission.reduce(state, GardenAction.RemoveSeed).seedCount)
    }
}
