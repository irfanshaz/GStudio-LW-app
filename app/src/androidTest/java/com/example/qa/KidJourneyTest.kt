package com.example.qa

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.MainActivity
import com.example.data.WonderCatalogRegistry
import com.example.model.AgeBand
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Baseline exploration: opening a card does not certify the educational content. */
class KidJourneyTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val output get() = File(instrumentation.targetContext.getExternalFilesDir(null), "kid-qa").apply { mkdirs() }
    private lateinit var vm: WonderViewModel
    private fun node(tag: String) = compose.onNodeWithTag(tag)
    private fun visible(tag: String) { node(tag).assertIsDisplayed() }
    private fun click(tag: String) { node(tag).performClick(); compose.waitForIdle() }
    private fun open(screen: WonderScreen) {
        compose.runOnIdle { vm.navigateHome(); vm.navigateTo(screen) }
        compose.waitForIdle()
    }
    private fun evidence(name: String) {
        compose.waitForIdle()
        File(output, "$name.txt").writeText(compose.onRoot().printToString())
        instrumentation.uiAutomation.takeScreenshot()?.let { bitmap ->
            File(output, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
    }
    private fun record(id: String, action: () -> Unit): Boolean {
        return try {
            action()
            File(output, "results.tsv").appendText("$id\tPASS\n")
            true
        } catch (e: AssertionError) {
            File(output, "results.tsv").appendText("$id\tFAIL\t${e.message?.replace('\n', ' ')}\n")
            evidence("FAIL_$id")
            false
        }
    }
    @Before fun onboard() {
        compose.activityRule.scenario.onActivity { vm = ViewModelProvider(it)[WonderViewModel::class.java] }
        compose.waitUntil(15000) {
            compose.onAllNodesWithTag("onboarding_screen").fetchSemanticsNodes().isNotEmpty() || vm.settingsState.value.onboardingCompleted
        }
        if (!vm.settingsState.value.onboardingCompleted) {
            node("start_playing_button").performScrollTo().performClick()
        }
        compose.waitUntil(10000) { vm.settingsState.value.onboardingCompleted }
        compose.runOnIdle { vm.navigateHome() }
        visible("home_screen")
    }

    @Test fun all257CardsOpenAndReturnThroughUi() {
        var failures = 0
        var count = 0
        // World selection is seeded; every activity is entered using its visible card.
        for (world in WonderCatalogRegistry.worlds) {
            open(WonderScreen.WorldActivities(world))
            val activities = WonderCatalogRegistry.getActivitiesForWorld(world.id)
            activities.forEachIndexed { index, activity ->
                if (index % 9 == 0) {
                    if (index > 0) click("world_next_page_button")
                    evidence("cards_${world.id}_page_${index / 9 + 1}")
                }
                if (!record(activity.id) {
                    visible("activity_tile_${activity.id}")
                    click("activity_tile_${activity.id}")
                    visible("activity_play_screen_${activity.id}")
                    evidence("activity_${activity.id}")
                    click("activity_back_button")
                    visible("world_activities_screen_${world.id}")
                    visible("activity_tile_${activity.id}")
                }) {
                    failures++
                    open(WonderScreen.WorldActivities(world, index / 9))
                }
                count++
            }
            click("world_home_button")
            visible("home_screen")
        }
        org.junit.Assert.assertEquals(257, count)
        org.junit.Assert.assertEquals("See results.tsv and FAIL screenshots", 0, failures)
    }

    @Test fun navigationAndParentGate() {
        evidence("home")
        click("home_toy_box_button")
        visible("toy_box_screen")
        click("toy_box_back_button")
        visible("home_screen")
        click("home_parent_corner_button")
        visible("parent_gate_screen")
        evidence("parent_gate")
        click("parent_gate_back_button")
        visible("home_screen")
        click("home_real_world_button")
        visible("real_world_screen")
        evidence("real_world")
        click("mode_wonder_reels_button")
        visible("wonder_reels_pager_container")
        evidence("reels")
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        visible("real_world_scroll_grid")
        click("real_world_home_button")
        visible("home_screen")
        for (land in WonderCatalogRegistry.worlds.map { it.landId }.distinct()) {
            node("land_card_${land.id}").performScrollTo().performClick()
            visible("land_worlds_screen_${land.id}")
            evidence("land_${land.id}")
            for (world in WonderCatalogRegistry.getWorldsForLand(land)) {
                node("world_card_${world.id}").performScrollTo().performClick()
                visible("world_activities_screen_${world.id}")
                click("world_back_button")
                visible("land_worlds_screen_${land.id}")
            }
            click("land_home_button")
            visible("home_screen")
        }
    }

    @Test fun tracingGestureRetryAndSkipAcrossAges() {
        val activity = WonderCatalogRegistry.getActivityById("w04_tracing_1")!!
        for (age in AgeBand.entries) {
            compose.runOnIdle { vm.updateAgeAndBuddy(age, vm.getCurrentBuddy()) }
            compose.waitUntil(10000) { vm.getCurrentAgeBand() == age }
            open(WonderScreen.ActivityPlay(activity))
            evidence("trace_${age.id}_before")
            node("tracing_canvas").performTouchInput {
                swipe(Offset(width * .27f, height * .83f), Offset(width * .5f, height * .17f), 600)
            }
            evidence("trace_${age.id}_stroke")
            click("tracing_retry_button")
            evidence("trace_${age.id}_retry")
            click("tracing_skip_button")
            evidence("trace_${age.id}_skip")
            click("activity_replay_button")
            evidence("trace_${age.id}_replay")
            click("activity_home_button")
            visible("home_screen")
        }
    }

    @Test fun tracingAdvertisedRangeStartsWithCorrectLetter() {
        // This is an acceptance assertion, deliberately not a snapshot of today's implementation.
        val activity = WonderCatalogRegistry.getActivityById("w04_tracing_2")!!
        open(WonderScreen.ActivityPlay(activity))
        evidence("tracing_range_contract")
        compose.onNodeWithText("I", useUnmergedTree = true).assertExists()
    }
}
