package com.example.garden

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.R
import com.example.data.ProfileSettingsEntity
import com.example.data.WonderDatabase
import com.example.ui.theme.LittleWonderWorldTheme
import com.example.viewmodel.WonderViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w731dp-h411dp-land-xxhdpi")
class GardenScreenRobolectricTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun prepareGardenViewModel(
        readingLevel: String = "2-3",
        mathLevel: String = "5-6",
        language: String = "en",
        reducedMotion: Boolean = false,
        initialGarden: GardenProgress? = null
    ): WonderViewModel {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val dao = WonderDatabase.getInstance(app).wonderDao()
        val settings = ProfileSettingsEntity(
            id = 1,
            ageBandId = "4-5",
            readingLevelId = readingLevel,
            mathLevelId = mathLevel,
            instructionLanguage = language,
            reducedMotion = reducedMotion,
            onboardingCompleted = true
        )
        runBlocking {
            dao.upsertSettings(settings)
            val progress = initialGarden ?: GardenMission.fresh(settings)
            dao.saveGarden(progress)
        }
        val vm = WonderViewModel(app)
        waitUntilGardenLoaded(vm)
        return vm
    }

    private fun waitUntilGardenLoaded(vm: WonderViewModel) {
        val deadline = System.currentTimeMillis() + 5_000L
        while (vm.gardenState.value == null && System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            Thread.sleep(15)
        }
        ShadowLooper.idleMainLooper()
    }

    private fun waitForStage(vm: WonderViewModel, expectedStage: Int) {
        val deadline = System.currentTimeMillis() + 5_000L
        while (vm.gardenState.value?.stage != expectedStage && System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            composeRule.mainClock.advanceTimeBy(50)
            Thread.sleep(15)
        }
        ShadowLooper.idleMainLooper()
        composeRule.waitForIdle()
    }

    @Test
    fun allFourStopsAndControlsFitOnPhoneLandscape() {
        val vm = prepareGardenViewModel(readingLevel = "2-3", mathLevel = "5-6")
        composeRule.setContent {
            LittleWonderWorldTheme {
                GardenScreen(viewModel = vm)
            }
        }
        composeRule.waitForIdle()

        // Verify top controls and all 4 stops are displayed with positive bounds above the fold
        listOf(
            "garden_back",
            "garden_quiet",
            "garden_listen",
            "garden_stop_0",
            "garden_stop_1",
            "garden_stop_2",
            "garden_stop_3",
            "garden_prompt",
            "garden_hint"
        ).forEach { tag ->
            val node = composeRule.onNodeWithTag(tag).assertIsDisplayed().fetchSemanticsNode()
            assertTrue("$tag width should be > 0", node.boundsInRoot.width > 0f)
            assertTrue("$tag height should be > 0", node.boundsInRoot.height > 0f)
        }

        // At stage 0, stop 0 is current (not enabled for practice), stops 1..3 are up next (not enabled)
        composeRule.onNodeWithTag("garden_stop_0").assertIsNotEnabled()
        composeRule.onNodeWithTag("garden_stop_1").assertIsNotEnabled()
        composeRule.onNodeWithTag("garden_stop_2").assertIsNotEnabled()
        composeRule.onNodeWithTag("garden_stop_3").assertIsNotEnabled()
    }

    @Test
    @Config(sdk = [36], qualifiers = "w640dp-h360dp-land-xhdpi", fontScale = 1.3f)
    fun narrowLandscapeWithLargeFontKeepsFourthStopReachable() {
        val vm = prepareGardenViewModel()
        composeRule.setContent {
            LittleWonderWorldTheme {
                GardenScreen(viewModel = vm)
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("garden_stop_3").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("garden_hint").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun sceneStatesSunnyCelebrationPracticeAndResumedCompleteBehavior() {
        val vm = prepareGardenViewModel(readingLevel = "2-3", mathLevel = "2-3")
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            LittleWonderWorldTheme {
                GardenScreen(viewModel = vm)
            }
        }
        composeRule.mainClock.advanceTimeBy(100)
        ShadowLooper.idleMainLooper()

        // Initial entry at Stage 0: Bare soil, Sunny is IDLE (no premature celebration)
        composeRule.onNodeWithTag("garden_scene_stage_bare_soil").assertExists()
        composeRule.onNodeWithTag("garden_sunny_pose_idle").assertExists()
        assertEquals("garden_s0_read_2_3_find_s_en", vm.audio.lastPlayedClipId.value)

        // Complete Stop 0 (Reading S) -> advances to Stage 1
        composeRule.onNodeWithTag("garden_read_S").performClick()
        waitForStage(vm, 1)

        // Immediately after completion, Sunny enters SHORT_RUN celebration pose
        composeRule.onNodeWithTag("garden_sunny_pose_short_run").assertExists()
        // Advance past 700ms run + 450ms wave -> returns to IDLE
        composeRule.mainClock.advanceTimeBy(1300)
        ShadowLooper.idleMainLooper()
        composeRule.onNodeWithTag("garden_sunny_pose_idle").assertExists()

        // Stage 1 with math 2-3 selects sunny_count_three
        assertEquals("sunny_count_three", vm.audio.lastPlayedClipId.value)
        composeRule.onNodeWithTag("garden_stop_0").assertIsEnabled()

        // Practice completed Stop 0: must not overwrite active stage 1 or award completion
        composeRule.onNodeWithTag("garden_stop_0").performClick()
        composeRule.mainClock.advanceTimeBy(100)
        composeRule.onNodeWithTag("garden_prompt").assertTextContains("Seed starts with S", substring = true)
        composeRule.onNodeWithTag("garden_read_S").performClick()
        composeRule.mainClock.advanceTimeBy(200)
        ShadowLooper.idleMainLooper()
        // Returned to active Stage 1 without incrementing completedGardens or re-triggering celebration
        assertEquals(1, vm.gardenState.value?.stage)
        assertEquals(0, vm.gardenState.value?.completedGardens)
        composeRule.onNodeWithTag("garden_sunny_pose_idle").assertExists()

        // Complete Stage 1 (3 seeds) -> advances to Stage 2 (SPROUT scene state)
        repeat(3) {
            composeRule.onNodeWithTag("garden_add_seed").performClick()
            waitForSeedCount(vm, it + 1)
        }
        composeRule.onNodeWithTag("garden_plant").performClick()
        waitForStage(vm, 2)
        composeRule.onNodeWithTag("garden_scene_stage_sprout").assertExists()
        composeRule.mainClock.advanceTimeBy(1300)

        // Complete Stage 2 (Sun) -> Stage 3 (Story) -> Stage 4 (BLOOM scene state)
        composeRule.onNodeWithTag("garden_choice_sun").performClick()
        waitForStage(vm, 3)
        composeRule.mainClock.advanceTimeBy(1300)

        composeRule.onNodeWithTag("garden_choice_water").performClick()
        waitForStage(vm, 4)
        composeRule.onNodeWithTag("garden_scene_stage_bloom").assertExists()
        // Advance past run (700ms) -> flag plant (650ms) -> wave (450ms) -> IDLE
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onNodeWithTag("garden_sunny_pose_flag_plant").assertExists()
        composeRule.mainClock.advanceTimeBy(1200)
        composeRule.onNodeWithTag("garden_sunny_pose_idle").assertExists()
        assertEquals(1, vm.gardenState.value?.completedGardens)
    }

    @Test
    fun resumingCompletedGardenAndReducedMotionDoNotTriggerCelebrationAnimation() {
        val completedGarden = GardenProgress(
            stage = 4,
            completedGardens = 2,
            readingLevelId = "2-3",
            mathLevelId = "5-6"
        )
        val vm = prepareGardenViewModel(
            readingLevel = "2-3",
            mathLevel = "5-6",
            reducedMotion = true,
            initialGarden = completedGarden
        )
        composeRule.setContent {
            LittleWonderWorldTheme {
                GardenScreen(viewModel = vm)
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("garden_complete").assertIsDisplayed()
        composeRule.onNodeWithTag("garden_scene_stage_bloom").assertExists()
        composeRule.onNodeWithTag("garden_sunny_pose_idle").assertExists()
    }

    @Test
    fun phonicsClipsAuditionsTeluguFallbackAndPianoSpeechCancellation() {
        val vm = prepareGardenViewModel(readingLevel = "2-3", mathLevel = "5-6", language = "te")
        composeRule.setContent {
            LittleWonderWorldTheme {
                GardenScreen(viewModel = vm)
            }
        }
        composeRule.waitForIdle()

        // Stage 0 reading stays in English even when Telugu is selected
        composeRule.onNodeWithTag("garden_prompt").assertTextContains("Seed starts with S. Find S.")
        assertEquals("garden_s0_read_2_3_find_s_en", vm.audio.lastPlayedClipId.value)

        // Separate phonics clip IDs for isolated sound vs letter name vs blend vs whole word
        composeRule.onNodeWithTag("garden_hear_phonics_s").performClick()
        composeRule.waitForIdle()
        assertEquals("phonics_s_isolated_sound", vm.audio.lastPlayedClipId.value)

        composeRule.onNodeWithTag("garden_hear_letter_s_name").performClick()
        composeRule.waitForIdle()
        assertEquals("phonics_s_name", vm.audio.lastPlayedClipId.value)
        assertNotEquals("phonics_s_isolated_sound", vm.audio.lastPlayedClipId.value)

        vm.audio.speakPhonicsClip("phonics_blend_sun_segmented")
        assertEquals("phonics_blend_sun_segmented", vm.audio.lastPlayedClipId.value)

        vm.audio.speakPhonicsClip("phonics_word_sun_whole")
        assertEquals("phonics_word_sun_whole", vm.audio.lastPlayedClipId.value)

        // Advance to Stage 1 (Math 5-6) with Telugu requested:
        // Since offline Telugu TTS is not installed in Robolectric JVM, it must fall back to accurate English
        composeRule.onNodeWithTag("garden_read_S").performClick()
        waitForStage(vm, 1)
        composeRule.onNodeWithTag("garden_prompt")
            .assertTextContains("Plant three seeds, then five more. How many altogether?")
        assertEquals("garden_s1_math_5_6_add_8_en", vm.audio.lastPlayedClipId.value)

        // Playing a piano note cancels speech immediately
        vm.audio.playNoteHz(261.63f, 220)
        assertFalse(vm.audio.isTeacherSpeaking.value)

        // Parent Corner Calm and Playful 6-line auditions and Stop control
        vm.audio.playGardenAudition(playful = false)
        assertEquals("sunny_greeting_calm", vm.audio.lastAuditionClipId.value)
        vm.audio.playGardenAudition(playful = true)
        assertEquals("sunny_greeting_playful", vm.audio.lastAuditionClipId.value)
        vm.audio.stopSpeechOnly()
        assertFalse(vm.audio.isTeacherSpeaking.value)
    }

    @Test
    fun launcherIconResourcesExistAndLoadCleanly() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        assertNotNull(context.getDrawable(R.drawable.ic_launcher_background))
        assertNotNull(context.getDrawable(R.drawable.ic_launcher_foreground))
        assertNotNull(context.getDrawable(R.drawable.ic_launcher_monochrome))
        assertNotNull(context.getDrawable(R.drawable.ic_launcher_star_trail))
        assertNotNull(context.getDrawable(R.mipmap.ic_launcher))
        assertNotNull(context.getDrawable(R.mipmap.ic_launcher_round))
    }

    private fun waitForSeedCount(vm: WonderViewModel, expectedSeeds: Int) {
        val deadline = System.currentTimeMillis() + 5_000L
        while (vm.gardenState.value?.seedCount != expectedSeeds && System.currentTimeMillis() < deadline) {
            ShadowLooper.idleMainLooper()
            composeRule.mainClock.advanceTimeBy(50)
            Thread.sleep(15)
        }
        ShadowLooper.idleMainLooper()
    }
}
