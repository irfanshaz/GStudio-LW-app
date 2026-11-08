package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AgeBand
import com.example.model.LearningBuddy
import com.example.ui.engines.ActivityPlayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LandWorldsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ParentCornerScreen
import com.example.ui.screens.RealWorldAndReelsScreen
import com.example.ui.screens.ToyBoxScreen
import com.example.ui.screens.WorldActivitiesScreen
import com.example.ui.theme.LittleWonderWorldTheme
import com.example.viewmodel.WonderScreen
import com.example.viewmodel.WonderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LittleWonderWorldTheme {
                LittleWonderWorldApp()
            }
        }
    }
}

@Composable
fun LittleWonderWorldApp(
    viewModel: WonderViewModel = viewModel()
) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val navStack by viewModel.navigationStack.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    val progressList by viewModel.progressListState.collectAsStateWithLifecycle()
    val savedArtworks by viewModel.savedArtworksState.collectAsStateWithLifecycle()
    val familyMembers by viewModel.familyMembersState.collectAsStateWithLifecycle()
    val mediaOverrides by viewModel.mediaOverridesState.collectAsStateWithLifecycle()
    val showDemoBanner by viewModel.showDemonstrationBanner.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    // Stop narration, music, and video immediately on app background or device lock
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel.audio.stopAllAudio()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Intercept Android Back when an overlay, Reel view, or sub-screen is active
    val canInterceptBack = showDemoBanner ||
        (activeScreen is WonderScreen.RealWorldHub && (activeScreen as WonderScreen.RealWorldHub).isReelsMode) ||
        navStack.size > 1
    BackHandler(enabled = canInterceptBack) {
        viewModel.navigateBack()
    }

    val ageBand = AgeBand.fromId(settings.ageBandId)
    val buddy = LearningBuddy.fromId(settings.buddyId)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        when (val screen = activeScreen) {
            is WonderScreen.Onboarding -> OnboardingScreen(
                initialAge = ageBand,
                initialBuddy = buddy,
                onListenPreview = { text -> viewModel.audio.speakInstruction(text) },
                onFinishOnboarding = { chosenAge, chosenBuddy ->
                    viewModel.completeOnboarding(chosenAge, chosenBuddy)
                }
            )

            is WonderScreen.Home -> HomeScreen(
                viewModel = viewModel,
                ageBand = ageBand,
                buddy = buddy,
                progressList = progressList,
                reducedMotion = settings.reducedMotion
            )

            is WonderScreen.ToyBox -> ToyBoxScreen(
                viewModel = viewModel,
                progressList = progressList,
                savedArtworks = savedArtworks
            )

            is WonderScreen.LandWorlds -> LandWorldsScreen(
                landId = screen.landId,
                viewModel = viewModel,
                progressList = progressList
            )

            is WonderScreen.WorldActivities -> WorldActivitiesScreen(
                world = screen.world,
                pageIndex = screen.pageIndex,
                viewModel = viewModel,
                progressList = progressList
            )

            is WonderScreen.ActivityPlay -> ActivityPlayScreen(
                activity = screen.activity,
                viewModel = viewModel,
                progressList = progressList,
                familyMembers = familyMembers,
                showDemoBanner = showDemoBanner,
                reducedMotion = settings.reducedMotion
            )

            is WonderScreen.RealWorldHub -> RealWorldAndReelsScreen(
                category = screen.category,
                isReelsMode = screen.isReelsMode,
                initialReelIndex = screen.initialReelIndex,
                viewModel = viewModel,
                mediaOverrides = mediaOverrides,
                reducedMotion = settings.reducedMotion
            )

            is WonderScreen.ParentCorner -> ParentCornerScreen(
                initiallyUnlocked = screen.unlocked,
                viewModel = viewModel,
                settings = settings,
                progressList = progressList,
                familyMembers = familyMembers,
                savedArtworks = savedArtworks,
                mediaOverrides = mediaOverrides
            )
        }
    }
}
