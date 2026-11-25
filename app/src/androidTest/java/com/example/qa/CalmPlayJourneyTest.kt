package com.example.qa

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.MainActivity
import com.example.data.WonderCatalogRegistry
import com.example.model.*
import com.example.viewmodel.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class CalmPlayJourneyTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private lateinit var vm: WonderViewModel

    @Before fun prepare() {
        compose.mainClock.autoAdvance = false
        var onboardingJob: kotlinx.coroutines.Job? = null
        compose.activityRule.scenario.onActivity {
            vm = ViewModelProvider(it)[WonderViewModel::class.java]
            onboardingJob = vm.completeOnboarding(AgeBand.TODDLER_2_3, LearningBuddy.entries.first())
        }
        // Joining the real save/navigation job prevents setup from navigating Home during a test.
        kotlinx.coroutines.runBlocking { kotlinx.coroutines.withTimeout(15000) { onboardingJob!!.join() } }
        compose.waitUntil(15000) { compose.mainClock.advanceTimeBy(96); vm.settingsState.value.onboardingCompleted }
    }

    private fun open(screen: WonderScreen) {
        compose.runOnIdle { vm.navigateHome(); vm.navigateTo(screen) }
        compose.mainClock.advanceTimeBy(500)
    }

    @OptIn(ExperimentalTestApi::class)
    private fun capture(name: String) {
        val dir = File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "calm-evidence").apply { mkdirs() }
        val image = compose.onRoot().captureToImage().asAndroidBitmap()
        File(dir, "$name.png").outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun allFourteenEnginesStillOpen() {
        for (type in EngineType.entries) {
            val activity = WonderCatalogRegistry.allActivities.first { it.engineType == type }
            open(WonderScreen.ActivityPlay(activity))
            compose.onNodeWithTag("activity_play_screen_${activity.id}").assertIsDisplayed()
            capture("engine_${type.name}")
        }
    }

    @Test fun storyNeedsNoReadingAndDoorNarrationAdvances() {
        val story = requireNotNull(WonderCatalogRegistry.getActivityById("w20_safety_1"))
        open(WonderScreen.ActivityPlay(story))
        capture("door_closed")
        compose.onNodeWithTag("door_safety_scene", useUnmergedTree = true).assertIsDisplayed()
        assertTrue(vm.audio.lastSpokenCaption.value.contains("knock", true))
        compose.onNodeWithTag("story_scene_action").performClick()
        compose.mainClock.advanceTimeBy(1800)
        compose.waitUntil(5000) { compose.mainClock.advanceTimeBy(96); vm.audio.lastSpokenCaption.value.contains("Mom and Dad") }
        capture("call_grown_up")
    }

    @Test fun balloonTargetsDoNotOverlapAfterMotion() {
        open(WonderScreen.ActivityPlay(requireNotNull(WonderCatalogRegistry.getActivityById("w06_balloons_1"))))
        repeat(5) {
            compose.mainClock.advanceTimeBy(5000)
            val nodes = (0..2).mapNotNull { slot ->
                compose.onAllNodes(hasTestTag("balloon_slot_$slot")).fetchSemanticsNodes().firstOrNull()
            }
            assertTrue(nodes.size in 2..3)
            nodes.forEachIndexed { i, a -> nodes.drop(i + 1).forEach { b ->
                assertFalse(a.boundsInRoot.overlaps(b.boundsInRoot))
            } }
        }
        capture("separated_balloons")
    }

    @Test fun discoverUsesPicturesAndHomeRotates() {
        open(WonderScreen.RealWorldHub())
        compose.onNodeWithTag("discovery_find").performClick()
        compose.mainClock.advanceTimeBy(96)
        capture("picture_choices")
        compose.onRoot(useUnmergedTree = true).printToLog("CALM_DISCOVERY")
        compose.onNodeWithTag("picture_choice_0", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("picture_choice_1", useUnmergedTree = true).assertIsDisplayed()
        open(WonderScreen.ToyBox)
        compose.onNodeWithTag("catalog_item_0").assertIsDisplayed()
        capture("calm_toy_box")
        compose.runOnIdle { vm.navigateHome() }
        compose.mainClock.advanceTimeBy(96)
        capture("home_landscape")
        compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
        compose.waitUntil(5000) { compose.mainClock.advanceTimeBy(96); compose.activity.resources.configuration.orientation == 1 }
        compose.onNodeWithTag("calm_home_tile_0").assertIsDisplayed()
        capture("home_portrait")
    }
}
