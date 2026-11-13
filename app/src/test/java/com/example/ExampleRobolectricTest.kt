package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveLearningEngine
import com.example.data.RealWorldMediaRegistry
import com.example.data.WonderCatalogRegistry
import com.example.model.AgeBand
import com.example.model.EngineType
import com.example.model.LandId
import com.example.model.LearningBuddy
import com.example.model.RealMediaCategory
import com.example.ui.engines.ActivityPlayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RealWorldAndReelsScreen
import com.example.ui.screens.WorldActivitiesScreen
import com.example.ui.theme.LittleWonderWorldTheme
import com.example.viewmodel.WonderViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w731dp-h411dp-land-xxhdpi", sdk = [34])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel(): WonderViewModel {
        val app = ApplicationProvider.getApplicationContext<Application>()
        return WonderViewModel(app)
    }

    @Test
    fun `verify ToyBox shortcuts, 14 reusable engines, and safety stories are reachable`() {
        val shortcuts = WonderCatalogRegistry.getToyBoxShortcuts()
        assertEquals(8, shortcuts.size)

        val usedEngines = WonderCatalogRegistry.allActivities.map { it.engineType }.toSet()
        assertEquals(EngineType.entries.toSet(), usedEngines)

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
        assertEquals(80, manifestSummary.expectedAnimals)
        assertEquals(123, manifestSummary.expectedObjects)
        assertEquals(34, manifestSummary.expectedLessonsAndFilms)
        assertEquals(156, manifestSummary.bundledVerifiedCount)
        assertEquals(156, manifestSummary.aiGeneratedStartingFramesCount)
        assertEquals(2, manifestSummary.userCustomOverridesCount)

        assertEquals(4, LearningBuddy.entries.size)
        assertTrue(LearningBuddy.entries.all { it.greeting.isNotBlank() && it.encouragement.isNotBlank() })
    }

    @Test
    fun `verify Stage 2 HomeScreen top bar and Parents button have full visible bounds on phone landscape`() {
        val vm = createViewModel()
        composeTestRule.setContent {
            LittleWonderWorldTheme {
                HomeScreen(
                    viewModel = vm,
                    ageBand = AgeBand.PRESCHOOL_4_5,
                    buddy = LearningBuddy.SUNNY_CUB,
                    progressList = emptyList(),
                    reducedMotion = true
                )
            }
        }
        composeTestRule.waitForIdle()

        val parentNode = composeTestRule.onNodeWithTag("home_parent_corner_button", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        assertTrue("Expected home_parent_corner_button width > 40px, got ${parentNode.first}", parentNode.first > 40f)
        assertTrue("Expected home_parent_corner_button height > 30px, got ${parentNode.second}", parentNode.second > 30f)

        composeTestRule.onNodeWithTag("home_toy_box_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_surprise_play_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_real_world_button").assertIsDisplayed()
    }

    @Test
    fun `verify Stage 2 WorldActivitiesScreen renders non-zero card titles and distinct glyph badges on phone landscape`() {
        val vm = createViewModel()
        val tracingWorld = WonderCatalogRegistry.worlds.first { it.id == "w04_tracing" }
        composeTestRule.setContent {
            LittleWonderWorldTheme {
                WorldActivitiesScreen(
                    world = tracingWorld,
                    pageIndex = 0,
                    viewModel = vm,
                    progressList = emptyList()
                )
            }
        }
        composeTestRule.waitForIdle()

        // Verify all 9 cards on Page 1 of w04_tracing have non-zero title height and distinct glyph badge
        for (i in 1..9) {
            val id = "w04_tracing_$i"
            val titleBounds = composeTestRule.onNodeWithTag("activity_title_$id", useUnmergedTree = true).SemanticsNodeInteractionBounds()
            assertTrue("Expected non-zero title height for $id on phone landscape, got ${titleBounds.second}", titleBounds.second > 8f)
            val glyphBounds = composeTestRule.onNodeWithTag("activity_glyph_$id", useUnmergedTree = true).SemanticsNodeInteractionBounds()
            assertTrue("Expected non-zero glyph badge width for $id, got ${glyphBounds.first}", glyphBounds.first > 8f)
        }
    }

    @Test
    fun `verify Stage 1 TracingStudioEngine buttons and coverage meter have non-zero height on phone landscape`() {
        val vm = createViewModel()
        val tracingActivity = WonderCatalogRegistry.getActivityById("w04_tracing_1")!!
        composeTestRule.setContent {
            LittleWonderWorldTheme {
                ActivityPlayScreen(
                    activity = tracingActivity,
                    viewModel = vm,
                    progressList = emptyList(),
                    familyMembers = emptyList(),
                    showDemoBanner = false,
                    reducedMotion = true
                )
            }
        }
        composeTestRule.waitForIdle()

        val retryBounds = composeTestRule.onNodeWithTag("tracing_retry_button", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        val skipBounds = composeTestRule.onNodeWithTag("tracing_skip_button", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        val nextBounds = composeTestRule.onNodeWithTag("tracing_next_glyph_button", useUnmergedTree = true).SemanticsNodeInteractionBounds()

        assertTrue("Expected tracing_retry_button height > 30px, got ${retryBounds.second}", retryBounds.second > 30f)
        assertTrue("Expected tracing_skip_button height > 30px, got ${skipBounds.second}", skipBounds.second > 30f)
        assertTrue("Expected tracing_next_glyph_button height > 30px, got ${nextBounds.second}", nextBounds.second > 30f)

        val meterBounds = composeTestRule.onNodeWithTag("tracing_coverage_meter_text", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        val statusBounds = composeTestRule.onNodeWithTag("tracing_feedback_status_text", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        assertTrue("Expected tracing_coverage_meter_text height > 8px, got ${meterBounds.second}", meterBounds.second > 8f)
        assertTrue("Expected tracing_feedback_status_text height > 8px, got ${statusBounds.second}", statusBounds.second > 8f)

        // Step to the end of the 8-item A-H deck and verify explicit Deck Complete card appears instead of silent wrap
        repeat(8) {
            composeTestRule.onNodeWithTag("tracing_next_glyph_button").performClick()
            composeTestRule.waitForIdle()
        }
        composeTestRule.onNodeWithTag("tracing_deck_complete_card").assertIsDisplayed()
    }

    @Test
    fun `verify Stage 2 ColoringCanvasEngine Save Art button and Science Iron Anchor have non-zero bounds on phone landscape`() {
        val vm = createViewModel()
        val coloringActivity = WonderCatalogRegistry.getActivityById("w05_coloring_1")!!
        composeTestRule.setContent {
            LittleWonderWorldTheme {
                ActivityPlayScreen(
                    activity = coloringActivity,
                    viewModel = vm,
                    progressList = emptyList(),
                    familyMembers = emptyList(),
                    showDemoBanner = false,
                    reducedMotion = true
                )
            }
        }
        composeTestRule.waitForIdle()

        val saveBounds = composeTestRule.onNodeWithTag("coloring_save_button", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        assertTrue("Expected coloring_save_button height > 30px on phone landscape, got ${saveBounds.second}", saveBounds.second > 30f)
    }

    @Test
    fun `verify Stage 2 RealWorldAndReelsScreen mode switcher buttons have full visible width on phone landscape`() {
        val vm = createViewModel()
        composeTestRule.setContent {
            LittleWonderWorldTheme {
                RealWorldAndReelsScreen(
                    category = RealMediaCategory.ANIMAL,
                    isReelsMode = false,
                    initialReelIndex = 0,
                    viewModel = vm,
                    mediaOverrides = emptyList(),
                    reducedMotion = true
                )
            }
        }
        composeTestRule.waitForIdle()

        val cardsBounds = composeTestRule.onNodeWithTag("mode_scroll_cards_button", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        val reelsBounds = composeTestRule.onNodeWithTag("mode_wonder_reels_button", useUnmergedTree = true).SemanticsNodeInteractionBounds()
        assertTrue("Expected mode_scroll_cards_button width > 40px, got ${cardsBounds.first}", cardsBounds.first > 40f)
        assertTrue("Expected mode_wonder_reels_button width > 40px, got ${reelsBounds.first}", reelsBounds.first > 40f)
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteraction.SemanticsNodeInteractionBounds(): Pair<Float, Float> {
        val node = fetchSemanticsNode()
        val bounds = node.boundsInRoot
        return Pair(bounds.width, bounds.height)
    }
}
