package com.example.qa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
import com.example.MainActivity
import com.example.data.WonderCatalogRegistry
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
