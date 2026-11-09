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
    private val output get() = File(instrumentation.targetContext.filesDir, "kid-qa").apply { mkdirs() }
    private lateinit var vm: WonderViewModel
    private fun node(tag: String) = compose.onNodeWithTag(tag)
    private fun settle() = compose.mainClock.advanceTimeBy(96)
    private fun visible(tag: String) {
        settle()
        node(tag).assertIsDisplayed()
    }
    private fun click(tag: String) {
        visible(tag)
        node(tag).performClick()
        settle()
    }
    private fun open(screen: WonderScreen) {
        compose.runOnIdle { vm.navigateHome(); vm.navigateTo(screen) }
        settle()
    }
    private fun evidence(name: String) {
        settle()
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
        compose.mainClock.autoAdvance = false
        compose.activityRule.scenario.onActivity { vm = ViewModelProvider(it)[WonderViewModel::class.java] }
        compose.waitUntil(15000) {
            settle()
            compose.onAllNodesWithTag("onboarding_screen").fetchSemanticsNodes().isNotEmpty() ||
                vm.settingsState.value.onboardingCompleted
        }
        if (!vm.settingsState.value.onboardingCompleted) {
            evidence("onboarding")
            click("start_playing_button")
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
                    android.util.Log.i("KidQA", "Opening ${activity.id}")
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
    }

    @Test fun realWorldAndReelsNavigation() {
        click("home_real_world_button")
        visible("real_world_screen")
        evidence("real_world")
        click("mode_wonder_reels_button")
        evidence("reels")
        visible("wonder_reels_pager_container")
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        visible("real_world_scroll_grid")
        click("real_world_home_button")
        visible("home_screen")
    }

    @Test fun everyLandAndWorldRoute() {
        for (land in WonderCatalogRegistry.worlds.map { it.landId }.distinct()) {
            click("land_card_${land.id}")
            visible("land_worlds_screen_${land.id}")
            evidence("land_${land.id}")
            for (world in WonderCatalogRegistry.getWorldsForLand(land)) {
                click("world_card_${world.id}")
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
            compose.waitUntil(10000) { settle(); vm.getCurrentAgeBand() == age }
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

    @Test fun tracingLetterNumberShapeGuideGallery() {
        // Record every offered guide for the four core decks, including pen-independent outlines.
        for ((id, count) in listOf("w04_tracing_1" to 26, "w04_tracing_4" to 26,
            "w04_tracing_6" to 10, "w04_tracing_8" to 5)) {
            open(WonderScreen.ActivityPlay(WonderCatalogRegistry.getActivityById(id)!!))
            repeat(count) { index ->
                evidence("guide_${id}_${index.toString().padStart(2, '0')}")
                click("tracing_skip_button")
            }
        }
    }

    @Test fun tracingAdvertisedRangeStartsWithCorrectLetter() {
        // This is an acceptance assertion, deliberately not a snapshot of today's implementation.
        val expected = mapOf(2 to "I", 3 to "Q", 5 to "n", 7 to "6", 9 to "｜",
            10 to "⌒", 11 to "⚡", 12 to "C")
        val failures = expected.count { (index, glyph) ->
            val id = "w04_tracing_$index"
            !record("contract_$id") {
                open(WonderScreen.ActivityPlay(WonderCatalogRegistry.getActivityById(id)!!))
                evidence("contract_$id")
                compose.onNodeWithText(glyph, useUnmergedTree = true).assertExists()
            }
        }
        org.junit.Assert.assertEquals("Tracing decks with the wrong opening guide", 0, failures)
    }
}
