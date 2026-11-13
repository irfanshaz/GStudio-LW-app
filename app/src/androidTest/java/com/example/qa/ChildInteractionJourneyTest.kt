package com.example.qa

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.lifecycle.ViewModelProvider
import com.example.MainActivity
import com.example.data.RealWorldMediaRegistry
import com.example.data.WonderCatalogRegistry
import com.example.model.RealMediaCategory
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChildInteractionJourneyTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private lateinit var vm: WonderViewModel

    @Before fun start() {
        compose.mainClock.autoAdvance = false
        compose.activityRule.scenario.onActivity { vm = ViewModelProvider(it)[WonderViewModel::class.java] }
        compose.waitUntil(15_000) {
            compose.mainClock.advanceTimeBy(96)
            vm.settingsState.value.onboardingCompleted ||
                compose.onAllNodes(androidx.compose.ui.test.hasTestTag("onboarding_screen"))
                    .fetchSemanticsNodes().isNotEmpty()
        }
        if (!vm.settingsState.value.onboardingCompleted) click("start_playing_button")
        compose.waitUntil(10_000) { vm.settingsState.value.onboardingCompleted }
    }

    private fun open(screen: WonderScreen) {
        compose.runOnIdle { vm.navigateHome(); vm.navigateTo(screen) }
        compose.mainClock.advanceTimeBy(96)
    }

    private fun click(tag: String) {
        compose.onNodeWithTag(tag).assertIsDisplayed().performClick()
        compose.mainClock.advanceTimeBy(96)
    }

    @OptIn(ExperimentalTestApi::class)
    private fun captureGarden(name: String) {
        compose.mainClock.advanceTimeBy(96)
        val directory = File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "garden-evidence").apply { mkdirs() }
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun independentLevelsGardenResumeAndCompletion() {
        open(WonderScreen.ParentCorner(unlocked = true))
        compose.mainClock.autoAdvance = true
        try {
            click("parent_tab_1")
            compose.onNodeWithTag("subject_READING_2-3").performScrollTo().performClick()
            compose.onNodeWithTag("subject_MATH_5-6").performScrollTo().performClick()
            compose.waitUntil(10000) { vm.settingsState.value.readingLevelId == "2-3" && vm.settingsState.value.mathLevelId == "5-6" }
            android.util.Log.i("GardenJourney", "Subject settings saved")
        } finally {
            compose.mainClock.autoAdvance = false
        }
        compose.runOnIdle { vm.startGarden(true); vm.navigateHome() }
        compose.mainClock.advanceTimeBy(96)
        click("featured_creative_garden_card")
        compose.waitUntil(10000) { compose.mainClock.advanceTimeBy(96); vm.gardenState.value != null }
        android.util.Log.i("GardenJourney", "Garden opened")
        captureGarden("reading")
        click("garden_read_S")
        compose.waitUntil(10000) { compose.mainClock.advanceTimeBy(96); vm.gardenState.value?.stage == 1 }
        click("garden_add_seed")
        compose.waitUntil(10000) { vm.gardenState.value?.seedCount == 1 }
        click("garden_plant")
        compose.waitUntil(10000) { vm.gardenState.value?.retriesMask == 2 }
        assertEquals(1, vm.gardenState.value?.stage)
        click("garden_back")
        click("featured_creative_garden_card")
        compose.onNodeWithTag("garden_seed_count").assertIsDisplayed().assertTextContains("1 seed", substring = true)
        android.util.Log.i("GardenJourney", "Garden resumed at one seed")
        repeat(7) { expected ->
            click("garden_add_seed")
            compose.waitUntil(10000) { compose.mainClock.advanceTimeBy(96); vm.gardenState.value?.seedCount == expected + 2 }
        }
        captureGarden("counting")
        click("garden_plant")
        compose.waitUntil(10000) { compose.mainClock.advanceTimeBy(96); vm.gardenState.value?.stage == 2 }
        click("garden_choice_sun")
        compose.waitUntil(10000) { compose.mainClock.advanceTimeBy(96); vm.gardenState.value?.stage == 3 }
        click("garden_choice_water")
        compose.waitUntil(10000) { compose.mainClock.advanceTimeBy(96); vm.gardenState.value?.stage == 4 }
        captureGarden("complete")
        compose.onNodeWithTag("garden_complete").assertIsDisplayed()
        assertEquals("2-3", vm.gardenState.value?.readingLevelId)
        assertEquals("5-6", vm.gardenState.value?.mathLevelId)
    }

    @Test fun pianoPlaysThroughFourRewardsWithoutSpeech() {
        val piano = requireNotNull(WonderCatalogRegistry.getActivityById("w11_music_1"))
        open(WonderScreen.ActivityPlay(piano))
        click("music_key_C")
        val captionAfterFirstNote = vm.audio.lastSpokenCaption.value
        repeat(31) { click("music_key_C") }
        assertEquals(captionAfterFirstNote, vm.audio.lastSpokenCaption.value)
        assertFalse(vm.audio.isTeacherSpeaking.value)
    }

    @Test fun storyTurnRequestsNewPageNarration() {
        val story = requireNotNull(WonderCatalogRegistry.getActivityById("w20_safety_1"))
        open(WonderScreen.ActivityPlay(story))
        val first = story.storyPages.first()
        click("story_choice_${first.correctChoiceIndex}")
        val second = story.storyPages[1]
        compose.onNodeWithText(second.title).assertIsDisplayed()
        assertTrue(vm.audio.lastSpokenCaption.value.startsWith(second.narrationText))
    }

    @Test fun wordBuilderRequiresLettersInOrder() {
        val phonics = requireNotNull(WonderCatalogRegistry.getActivityById("w07_phonics_10"))
        open(WonderScreen.ActivityPlay(phonics))
        compose.onNodeWithTag("engine_cvc_builder").assertIsDisplayed()
        val word = phonics.items.first().label
        val wrongFirstLetter = word.first { it != word.first() }.toString()
        compose.onNodeWithText(wrongFirstLetter).assertIsDisplayed().performClick()
        compose.mainClock.advanceTimeBy(96)
        compose.onNodeWithTag("cvc_next").assertDoesNotExist()
        for (letter in word) {
            compose.onNodeWithText(letter.toString()).assertIsDisplayed().performClick()
            compose.mainClock.advanceTimeBy(96)
        }
        click("cvc_next")
        compose.onNodeWithTag("cvc_next").assertDoesNotExist()
    }

    @Test fun bundledAnimalLessonShowsTimedCaptionAfterPlaybackStarts() {
        assertBundledLessonPlays("lesson_wild_and_domestic_animals_pilot", "Look at the Elephant!")
    }

    @Test fun bundledBrushingLessonShowsTimedCaptionAfterPlaybackStarts() {
        assertBundledLessonPlays("lesson_10", "Healthy habits make us shine!")
    }

    private fun assertBundledLessonPlays(lessonId: String, expectedCaption: String) {
        val category = RealMediaCategory.FILM_LESSON
        val lessonIndex = RealWorldMediaRegistry.getItemsByCategory(category).indexOfFirst { it.id == lessonId }
        assertTrue(lessonIndex >= 0)
        open(WonderScreen.RealWorldHub(category = category))
        compose.onNodeWithTag("real_world_scroll_grid").performScrollToIndex(lessonIndex)
        compose.mainClock.advanceTimeBy(96)
        val watch = compose.onNodeWithTag("watch_reel_$lessonId")
        watch.performScrollTo().assertIsDisplayed().performClick()
        compose.mainClock.advanceTimeBy(96)
        compose.onNodeWithTag("reel_video_surface").assertIsDisplayed()
        compose.waitUntil(20_000) {
            compose.mainClock.advanceTimeBy(240)
            compose.onAllNodes(androidx.compose.ui.test.hasText(expectedCaption, substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test fun childCanSwitchBetweenMediaCardsAndReels() {
        open(WonderScreen.RealWorldHub())
        compose.onNodeWithTag("real_world_scroll_grid").assertIsDisplayed()
        click("mode_wonder_reels_button")
        compose.onNodeWithTag("wonder_reels_pager_container").assertIsDisplayed()
        click("reel_next_button")
        click("mode_scroll_cards_button")
        compose.onNodeWithTag("real_world_scroll_grid").assertIsDisplayed()
    }
}
