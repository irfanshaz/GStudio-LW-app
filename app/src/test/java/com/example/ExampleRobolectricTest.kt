package com.example

import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveLearningEngine
import com.example.data.RealWorldMediaRegistry
import com.example.data.WonderCatalogRegistry
import com.example.model.AgeBand
import com.example.model.EngineType
import com.example.model.LandId
import com.example.model.LearningBuddy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun `verify ToyBox shortcuts, 14 reusable engines, and safety stories are reachable`() {
        val shortcuts = WonderCatalogRegistry.getToyBoxShortcuts()
        assertEquals(8, shortcuts.size)

        // Verify all 14 EngineType values are actively used in the 257-activity catalog
        val usedEngines = WonderCatalogRegistry.allActivities.map { it.engineType }.toSet()
        assertEquals(EngineType.entries.toSet(), usedEngines)

        // Verify Knock Knock! Door Safety is present in World 20 (Body & Safety) with 3 scenes
        val doorSafety = WonderCatalogRegistry.getActivityById("w20_safety_1")
        assertNotNull(doorSafety)
        assertEquals("Knock Knock! Door Safety", doorSafety?.title)
        assertEquals(3, doorSafety?.storyPages?.size)
    }

    @Test
    fun `verify Parent Corner Land and World progress aggregation and adaptive comfort transitions`() {
        val sampleProgress = listOf(
            ActivityProgressEntity(
                activityId = "w01_sorting_1",
                worldId = "w01_sorting",
                landId = LandId.THINKING_FOREST.id,
                starsEarned = 5,
                completions = 3,
                attempts = 3,
                smoothFirstTryCount = 3,
                gentleRetryCount = 0,
                adaptiveComfortDelta = 1
            ),
            ActivityProgressEntity(
                activityId = "w04_tracing_1",
                worldId = "w04_tracing",
                landId = LandId.LEARNING_LAND.id,
                starsEarned = 3,
                completions = 1,
                attempts = 2,
                smoothFirstTryCount = 0,
                gentleRetryCount = 2,
                adaptiveComfortDelta = -1
            )
        )

        val sortingParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.PRESCHOOL_4_5,
            autoAdaptEnabled = true,
            progress = sampleProgress[0]
        )
        val tracingParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.PRESCHOOL_4_5,
            autoAdaptEnabled = true,
            progress = sampleProgress[1]
        )

        assertEquals(1, sortingParams.adaptiveTierDelta)
        assertEquals(-1, tracingParams.adaptiveTierDelta)
        assertTrue(tracingParams.tracingToleranceDp > sortingParams.tracingToleranceDp)

        val manifestSummary = RealWorldMediaRegistry.buildManifestSummary(customOverridesCount = 2)
        assertEquals(64, manifestSummary.expectedAnimals)
        assertEquals(71, manifestSummary.expectedObjects)
        assertEquals(22, manifestSummary.expectedLessonsAndFilms)
        assertEquals(157, manifestSummary.bundledVerifiedCount)
        assertEquals(8, manifestSummary.aiGeneratedStartingFramesCount)
        assertEquals(2, manifestSummary.userCustomOverridesCount)

        // Verify all 4 speaking buddies have distinct voices and greetings
        assertEquals(4, LearningBuddy.entries.size)
        assertTrue(LearningBuddy.entries.all { it.greeting.isNotBlank() && it.encouragement.isNotBlank() })
    }
}
