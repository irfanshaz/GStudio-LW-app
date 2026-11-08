package com.example

import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveLearningEngine
import com.example.data.RealWorldMediaRegistry
import com.example.data.WonderCatalogRegistry
import com.example.model.AgeBand
import com.example.model.LandId
import com.example.model.RealMediaCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `verify full catalog has 6 lands, 27 worlds, and 257 unique playable activities`() {
        val lands = LandId.entries
        assertEquals(6, lands.size)

        val worlds = WonderCatalogRegistry.worlds
        assertEquals(27, worlds.size)

        val activities = WonderCatalogRegistry.allActivities
        assertEquals(257, activities.size)

        // Ensure all 257 activity IDs are strictly unique
        val uniqueIds = activities.map { it.id }.toSet()
        assertEquals(257, uniqueIds.size)

        // Verify 30 Rhyme & Storytime entries in World 27, each with 3 story pages
        val rhymes = WonderCatalogRegistry.getActivitiesForWorld("w27_rhymes")
        assertEquals(30, rhymes.size)
        assertTrue(rhymes.all { it.storyPages.size == 3 })

        // Verify 16 Flashcard decks in World 25
        val flashcards = WonderCatalogRegistry.getActivitiesForWorld("w25_flashcards")
        assertEquals(16, flashcards.size)
    }

    @Test
    fun `verify Real World Media and Wonder Reels inventory and manifest`() {
        val animals = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.ANIMAL)
        val objects = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.OBJECT)
        val lessonsAndFilms = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.FILM_LESSON)

        assertEquals(64, animals.size)
        assertEquals(71, objects.size)
        assertEquals(22, lessonsAndFilms.size)

        val allItems = RealWorldMediaRegistry.allMediaItems
        assertEquals(157, allItems.size)
        assertEquals(157, allItems.map { it.id }.toSet().size)
        assertTrue(allItems.all { it.manifest.checksumSha256Prefix.isNotBlank() && it.funFacts.isNotEmpty() })
    }

    @Test
    fun `verify AdaptiveLearningEngine adjusts difficulty by age band and non-punitive indicators`() {
        val toddlerParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.TODDLER_2_3,
            autoAdaptEnabled = true,
            progress = null
        )
        val kinderParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.KINDER_5_6,
            autoAdaptEnabled = true,
            progress = null
        )
        assertTrue(toddlerParams.effectiveChoiceCount < kinderParams.effectiveChoiceCount)
        assertTrue(toddlerParams.tracingToleranceDp > kinderParams.tracingToleranceDp)
        assertTrue(toddlerParams.memoryPairCount < kinderParams.memoryPairCount)

        // Verify gentle support scaffolding activates when a child needs retries
        val supportParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.PRESCHOOL_4_5,
            autoAdaptEnabled = true,
            progress = ActivityProgressEntity(
                activityId = "w01_sorting_1",
                worldId = "w01_sorting",
                landId = "thinking_forest",
                gentleRetryCount = 3,
                adaptiveComfortDelta = -1
            ),
            recentSessionRetries = 2
        )
        assertEquals(-1, supportParams.adaptiveTierDelta)
        assertTrue(supportParams.showVisualScaffoldGlow)
        assertEquals(2, supportParams.effectiveChoiceCount)

        // Verify stretch challenge activates after smooth completions
        val stretchParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.PRESCHOOL_4_5,
            autoAdaptEnabled = true,
            progress = null,
            recentSessionSmoothWins = 3
        )
        assertEquals(1, stretchParams.adaptiveTierDelta)
        assertEquals(4, stretchParams.effectiveChoiceCount)
    }
}
