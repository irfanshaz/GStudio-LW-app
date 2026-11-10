package com.example.garden

import com.example.audio.VoiceAssetManifest
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
        assertEquals(1, state.completedGardens)
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

    @Test fun sceneStagesAndStopStatesMatchProgress() {
        assertEquals(GardenSceneStage.BARE_SOIL, GardenMission.sceneStageFor(0))
        assertEquals(GardenSceneStage.BARE_SOIL, GardenMission.sceneStageFor(1))
        assertEquals(GardenSceneStage.SPROUT, GardenMission.sceneStageFor(2))
        assertEquals(GardenSceneStage.SPROUT, GardenMission.sceneStageFor(3))
        assertEquals(GardenSceneStage.BLOOM, GardenMission.sceneStageFor(4))

        // At stage 2: stops 0 and 1 are FINISHED, stop 2 is CURRENT, stop 3 is UP_NEXT
        assertEquals(GardenStopState.FINISHED, GardenMission.stopStateFor(2, 0))
        assertEquals(GardenStopState.FINISHED, GardenMission.stopStateFor(2, 1))
        assertEquals(GardenStopState.CURRENT, GardenMission.stopStateFor(2, 2))
        assertEquals(GardenStopState.UP_NEXT, GardenMission.stopStateFor(2, 3))
    }

    @Test fun promptClipIdsMatchExactReadingAndMathLevels() {
        // Sunny count three clip must ONLY be selected for math 2-3 (3 seeds), never for 5 or 8 seeds
        val math23 = GardenProgress(stage = 1, mathLevelId = "2-3")
        val math45 = GardenProgress(stage = 1, mathLevelId = "4-5")
        val math56 = GardenProgress(stage = 1, mathLevelId = "5-6")

        assertEquals("sunny_count_three", GardenMission.promptSpecFor(math23).clipId)
        assertEquals("garden_s1_math_4_5_count_5_en", GardenMission.promptSpecFor(math45).clipId)
        assertEquals("garden_s1_math_5_6_add_8_en", GardenMission.promptSpecFor(math56).clipId)
        assertNotEquals("sunny_count_three", GardenMission.promptSpecFor(math45).clipId)
        assertNotEquals("sunny_count_three", GardenMission.promptSpecFor(math56).clipId)

        // Reading prompts match 2-3, 4-5 (S, U, N), and 5-6 (SUN, SEED)
        assertEquals(
            "garden_s0_read_2_3_find_s_en",
            GardenMission.promptSpecFor(GardenProgress(stage = 0, readingLevelId = "2-3")).clipId
        )
        assertEquals(
            "garden_s0_read_4_5_sun_u_en",
            GardenMission.promptSpecFor(GardenProgress(stage = 0, readingLevelId = "4-5", readingIndex = 1)).clipId
        )
        assertEquals(
            "garden_s0_read_5_6_word_seed_en",
            GardenMission.promptSpecFor(GardenProgress(stage = 0, readingLevelId = "5-6", readingIndex = 1)).clipId
        )
    }

    @Test fun teluguPromptFallsBackToAccurateEnglishWhenUnavailableAndKeepsReadingInEnglish() {
        // Stage 0 (Reading) stays in English even when Telugu is requested and available
        val stage0 = GardenProgress(stage = 0, readingLevelId = "2-3")
        val spec0 = GardenMission.promptSpecFor(stage0, requestedLanguage = "te", teluguAvailable = true)
        assertEquals("en", spec0.languageCode)
        assertEquals("Seed starts with S. Find S.", spec0.visibleText)
        assertEquals("garden_s0_read_2_3_find_s_en", spec0.clipId)

        // Stage 1 (Math 5-6) uses Telugu when available
        val stage1 = GardenProgress(stage = 1, mathLevelId = "5-6")
        val specTeAvailable = GardenMission.promptSpecFor(stage1, requestedLanguage = "te", teluguAvailable = true)
        assertEquals("te", specTeAvailable.languageCode)
        assertEquals("garden_count_8_te", specTeAvailable.clipId)
        assertTrue(specTeAvailable.visibleText.contains("విత్తనాలు"))

        // When Telugu offline speech is unavailable, visible text and clipId fall back to matching English
        val specTeFallback = GardenMission.promptSpecFor(stage1, requestedLanguage = "te", teluguAvailable = false)
        assertEquals("en", specTeFallback.languageCode)
        assertEquals("garden_s1_math_5_6_add_8_en", specTeFallback.clipId)
        assertEquals("Plant three seeds, then five more. How many altogether?", specTeFallback.visibleText)
    }

    @Test fun voiceManifestContainsCalmPlayfulAuditionsAndDistinctPhonicsClips() {
        assertEquals(6, VoiceAssetManifest.gardenAuditionCalmClips.size)
        assertEquals(6, VoiceAssetManifest.gardenAuditionPlayfulClips.size)

        val phonicsIds = VoiceAssetManifest.distinctPhonicsClips.map { it.clipId }.toSet()
        assertTrue(phonicsIds.contains("phonics_s_isolated_sound"))
        assertTrue(phonicsIds.contains("phonics_m_isolated_sound"))
        assertTrue(phonicsIds.contains("phonics_blend_sun_segmented"))
        assertTrue(phonicsIds.contains("phonics_word_sun_whole"))
        assertTrue(phonicsIds.contains("phonics_word_seed_whole"))
    }
}
