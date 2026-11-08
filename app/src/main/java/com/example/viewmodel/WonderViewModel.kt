package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.VoiceClipManifestEntry
import com.example.audio.WonderAudioController
import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveChallengeParameters
import com.example.data.AdaptiveLearningEngine
import com.example.data.CustomMediaOverrideEntity
import com.example.data.FamilyMemberEntity
import com.example.data.ProfileSettingsEntity
import com.example.data.RealWorldMediaRegistry
import com.example.data.SavedArtworkEntity
import com.example.data.WonderCatalogRegistry
import com.example.data.WonderDatabase
import com.example.data.WonderRepository
import com.example.model.ActivityConfig
import com.example.model.AgeBand
import com.example.model.LandId
import com.example.model.LearningBuddy
import com.example.model.RealMediaCategory
import com.example.model.WorldSpec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WonderScreen {
    data object Onboarding : WonderScreen
    data object Home : WonderScreen
    data object ToyBox : WonderScreen
    data class LandWorlds(val landId: LandId) : WonderScreen
    data class WorldActivities(val world: WorldSpec, val pageIndex: Int = 0) : WonderScreen
    data class ActivityPlay(val activity: ActivityConfig) : WonderScreen
    data class RealWorldHub(
        val category: RealMediaCategory = RealMediaCategory.ANIMAL,
        val isReelsMode: Boolean = false,
        val initialReelIndex: Int = 0
    ) : WonderScreen
    data class ParentCorner(val unlocked: Boolean = false) : WonderScreen
}

data class WorldProgressSummary(
    val world: WorldSpec,
    val totalActivities: Int,
    val activitiesTried: Int,
    val totalCompletions: Int,
    val starsEarned: Int,
    val maxStarsPossible: Int
)

data class LandProgressSummary(
    val land: LandId,
    val totalActivities: Int,
    val activitiesTried: Int,
    val totalCompletions: Int,
    val starsEarned: Int,
    val worldSummaries: List<WorldProgressSummary>
)

class WonderViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WonderDatabase.getInstance(application)
    private val repository = WonderRepository(application, database.wonderDao())
    val audio = WonderAudioController(application)

    val settingsState: StateFlow<ProfileSettingsEntity> = repository.settingsFlow
        .combine(MutableStateFlow(ProfileSettingsEntity())) { dbSettings, fallback ->
            val resolved = dbSettings ?: fallback
            audio.narrationEnabled = resolved.narrationEnabled
            audio.musicEnabled = resolved.musicEnabled
            audio.sfxEnabled = resolved.sfxEnabled
            audio.activeBuddy = LearningBuddy.fromId(resolved.buddyId)
            audio.setSelectedTeacherVoice(resolved.selectedVoiceId)
            resolved
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ProfileSettingsEntity())

    val progressListState: StateFlow<List<ActivityProgressEntity>> = repository.progressFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val savedArtworksState: StateFlow<List<SavedArtworkEntity>> = repository.artworksFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val familyMembersState: StateFlow<List<FamilyMemberEntity>> = repository.familyMembersFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val mediaOverridesState: StateFlow<List<CustomMediaOverrideEntity>> = repository.mediaOverridesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _navigationStack = MutableStateFlow<List<WonderScreen>>(listOf(WonderScreen.Home))
    val navigationStack: StateFlow<List<WonderScreen>> = _navigationStack.asStateFlow()

    private val _activeScreen = MutableStateFlow<WonderScreen>(WonderScreen.Home)
    val activeScreen: StateFlow<WonderScreen> = _activeScreen.asStateFlow()

    // Live per-activity session counters for real-time adaptive learning scaffolding
    private val _sessionRetries = MutableStateFlow(0)
    val sessionRetries: StateFlow<Int> = _sessionRetries.asStateFlow()

    private val _sessionSmoothWins = MutableStateFlow(0)
    val sessionSmoothWins: StateFlow<Int> = _sessionSmoothWins.asStateFlow()

    private val _showDemonstrationBanner = MutableStateFlow(false)
    val showDemonstrationBanner: StateFlow<Boolean> = _showDemonstrationBanner.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
            repository.settingsFlow.collect { settings ->
                if (settings != null && !settings.onboardingCompleted && _navigationStack.value.size == 1 && _activeScreen.value == WonderScreen.Home) {
                    _activeScreen.value = WonderScreen.Onboarding
                }
            }
        }
    }

    fun getCurrentAgeBand(): AgeBand = AgeBand.fromId(settingsState.value.ageBandId)
    fun getCurrentBuddy(): LearningBuddy = LearningBuddy.fromId(settingsState.value.buddyId)

    fun getAdaptiveParametersFor(activity: ActivityConfig): AdaptiveChallengeParameters {
        val progress = progressListState.value.find { it.activityId == activity.id }
        return AdaptiveLearningEngine.computeParameters(
            ageBand = getCurrentAgeBand(),
            autoAdaptEnabled = settingsState.value.autoAdaptiveDifficulty,
            progress = progress,
            recentSessionRetries = _sessionRetries.value,
            recentSessionSmoothWins = _sessionSmoothWins.value
        )
    }

    fun navigateTo(screen: WonderScreen) {
        audio.stopAllAudio()
        _showDemonstrationBanner.value = false
        if (screen is WonderScreen.ActivityPlay) {
            _sessionRetries.value = 0
            _sessionSmoothWins.value = 0
            viewModelScope.launch {
                repository.recordActivityOpened(screen.activity)
            }
            // Concise, one-action-at-a-time instruction when entering an activity
            audio.speakInstruction(screen.activity.instructionText)
        }
        val stack = _navigationStack.value.toMutableList()
        stack.add(screen)
        _navigationStack.value = stack
        _activeScreen.value = screen
    }

    fun navigateHome() {
        audio.stopAllAudio()
        _showDemonstrationBanner.value = false
        _navigationStack.value = listOf(WonderScreen.Home)
        _activeScreen.value = WonderScreen.Home
    }

    /**
     * Hierarchical Android Back behavior required by specification:
     * 1. If a "Show Me" demonstration overlay is open inside an activity, close it first.
     * 2. If inside RealWorldHub in full-screen Wonder Reels mode, return to Scroll Cards mode first.
     * 3. Otherwise pop the navigation stack back toward World -> Land -> Home.
     */
    fun navigateBack(): Boolean {
        audio.stopAllAudio()
        if (_showDemonstrationBanner.value) {
            _showDemonstrationBanner.value = false
            return true
        }
        val current = _activeScreen.value
        if (current is WonderScreen.RealWorldHub && current.isReelsMode) {
            val updated = current.copy(isReelsMode = false)
            val stack = _navigationStack.value.dropLast(1) + updated
            _navigationStack.value = stack
            _activeScreen.value = updated
            return true
        }
        val stack = _navigationStack.value
        if (stack.size > 1) {
            val newStack = stack.dropLast(1)
            _navigationStack.value = newStack
            _activeScreen.value = newStack.last()
            return true
        }
        return false
    }

    fun updateWorldPage(world: WorldSpec, newPage: Int) {
        val updated = WonderScreen.WorldActivities(world, newPage)
        val stack = _navigationStack.value.dropLast(1) + updated
        _navigationStack.value = stack
        _activeScreen.value = updated
    }

    fun updateRealWorldMode(
        category: RealMediaCategory,
        isReelsMode: Boolean,
        initialReelIndex: Int = 0
    ) {
        audio.stopAllAudio()
        val updated = WonderScreen.RealWorldHub(category, isReelsMode, initialReelIndex)
        val stack = _navigationStack.value.dropLast(1) + updated
        _navigationStack.value = stack
        _activeScreen.value = updated
    }

    fun launchSurprisePlay() {
        val randomActivity = WonderCatalogRegistry.allActivities.random()
        navigateTo(WonderScreen.ActivityPlay(randomActivity))
    }

    fun launchNextActivityInWorld(current: ActivityConfig) {
        val worldActivities = WonderCatalogRegistry.getActivitiesForWorld(current.worldId)
        val idx = worldActivities.indexOfFirst { it.id == current.id }
        val next = if (idx in 0 until worldActivities.lastIndex) {
            worldActivities[idx + 1]
        } else {
            worldActivities.firstOrNull() ?: current
        }
        val stack = _navigationStack.value.dropLast(1)
        _navigationStack.value = stack
        navigateTo(WonderScreen.ActivityPlay(next))
    }

    fun triggerShowMeDemo(activity: ActivityConfig) {
        _showDemonstrationBanner.value = !_showDemonstrationBanner.value
        if (_showDemonstrationBanner.value) {
            audio.speakInstruction(activity.showMeDemoHint)
        }
    }

    fun dismissShowMeDemo() {
        _showDemonstrationBanner.value = false
    }

    fun onActivityMilestoneCompleted(
        activity: ActivityConfig,
        hadRetriesInRound: Boolean = false,
        nextStepIndex: Int? = null,
        tracingAccuracyPercent: Int? = null,
        speakMilestonePraise: Boolean = true
    ) {
        if (!hadRetriesInRound) {
            _sessionSmoothWins.value += 1
            _sessionRetries.value = 0
        }
        audio.playSuccessChime()
        if (speakMilestonePraise) {
            audio.speakSincereSuccess()
        }
        viewModelScope.launch {
            repository.recordActivityMilestone(
                activity = activity,
                starsToAdd = 1,
                nextStepIndex = nextStepIndex,
                hadRetriesInRound = hadRetriesInRound,
                tracingAccuracyPercent = tracingAccuracyPercent
            )
        }
    }

    fun onGentleRetryNeeded(activity: ActivityConfig, gentlePrompt: String) {
        _sessionRetries.value += 1
        _sessionSmoothWins.value = 0
        audio.playGentleRetryTone()
        audio.speakPatientRetry(gentlePrompt)
        viewModelScope.launch {
            repository.recordGentleRetry(activity)
        }
    }

    fun toggleFavorite(activity: ActivityConfig) {
        viewModelScope.launch {
            repository.toggleFavorite(activity)
        }
    }

    fun completeOnboarding(ageBand: AgeBand, buddy: LearningBuddy) {
        viewModelScope.launch {
            repository.completeOnboarding(ageBand, buddy)
            audio.activeBuddy = buddy
            audio.speakInstruction(buddy.greeting)
            navigateHome()
        }
    }

    fun updateAgeAndBuddy(ageBand: AgeBand, buddy: LearningBuddy) {
        viewModelScope.launch {
            repository.updateAgeAndBuddy(ageBand, buddy)
            audio.activeBuddy = buddy
            audio.speakInstruction("Updated to ${ageBand.label} with companion ${buddy.displayName}.")
        }
    }

    fun selectAndPreviewTeacherVoice(voiceId: String) {
        viewModelScope.launch {
            repository.updateSelectedVoice(voiceId)
            audio.previewVoiceOption(voiceId)
        }
    }

    fun previewTeacherVoice(voiceId: String) {
        audio.previewVoiceOption(voiceId)
    }

    fun playTeacherAuditionClip(entry: VoiceClipManifestEntry) {
        audio.playAuditionClip(entry)
    }

    fun updateAudioMotionAndAdaptiveSettings(
        narrationEnabled: Boolean,
        musicEnabled: Boolean,
        sfxEnabled: Boolean,
        reducedMotion: Boolean,
        autoAdaptiveDifficulty: Boolean
    ) {
        viewModelScope.launch {
            if (!narrationEnabled) {
                audio.stopSpeechOnly()
            }
            repository.updateAudioAndMotionSettings(
                narrationEnabled = narrationEnabled,
                musicEnabled = musicEnabled,
                sfxEnabled = sfxEnabled,
                reducedMotion = reducedMotion,
                autoAdaptiveDifficulty = autoAdaptiveDifficulty
            )
        }
    }

    fun setWorldAdaptiveComfortDelta(worldId: String, delta: Int) {
        viewModelScope.launch {
            val activities = WonderCatalogRegistry.getActivitiesForWorld(worldId)
            activities.forEach { act ->
                repository.setActivityAdaptiveDelta(act, delta)
            }
        }
    }

    fun saveChildArtwork(title: String, templateName: String, strokesPayload: String, bgHex: Long) {
        viewModelScope.launch {
            repository.saveArtwork(title, templateName, strokesPayload, bgHex)
            audio.playSuccessChime()
            audio.speakInstruction("Saved your picture to My Art Gallery!")
        }
    }

    fun deleteArtwork(id: Long) {
        viewModelScope.launch {
            repository.deleteArtwork(id)
        }
    }

    fun addFamilyMember(name: String, role: String, photoUri: Uri?, emoji: String, colorHex: Long) {
        viewModelScope.launch {
            if (familyMembersState.value.size < 16) {
                repository.addFamilyMember(name, role, photoUri, emoji, colorHex)
            }
        }
    }

    fun deleteFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch {
            repository.deleteFamilyMember(member)
        }
    }

    fun importGoogleFlowMedia(mediaId: String, uri: Uri, isVideo: Boolean) {
        viewModelScope.launch {
            repository.importCustomMediaOverride(mediaId, uri, isVideo)
            audio.playSuccessChime()
        }
    }

    fun removeGoogleFlowMediaOverride(mediaId: String) {
        viewModelScope.launch {
            repository.removeCustomMediaOverride(mediaId)
        }
    }

    fun resetAllLocalDataConfirmed() {
        viewModelScope.launch {
            repository.resetAllChildData()
            audio.speakInstruction("Local progress, saved art, and family photos have been reset.")
        }
    }

    fun buildLandProgressSummaries(progressList: List<ActivityProgressEntity>): List<LandProgressSummary> {
        val progressById = progressList.associateBy { it.activityId }
        return LandId.entries.map { land ->
            val worldsInLand = WonderCatalogRegistry.getWorldsForLand(land)
            val worldSummaries = worldsInLand.map { world ->
                val acts = WonderCatalogRegistry.getActivitiesForWorld(world.id)
                var tried = 0
                var completions = 0
                var stars = 0
                acts.forEach { act ->
                    val p = progressById[act.id]
                    if (p != null && (p.attempts > 0 || p.completions > 0 || p.starsEarned > 0)) {
                        tried += 1
                        completions += p.completions
                        stars += p.starsEarned
                    }
                }
                WorldProgressSummary(
                    world = world,
                    totalActivities = acts.size,
                    activitiesTried = tried,
                    totalCompletions = completions,
                    starsEarned = stars,
                    maxStarsPossible = acts.size * 5
                )
            }
            LandProgressSummary(
                land = land,
                totalActivities = worldSummaries.sumOf { it.totalActivities },
                activitiesTried = worldSummaries.sumOf { it.activitiesTried },
                totalCompletions = worldSummaries.sumOf { it.totalCompletions },
                starsEarned = worldSummaries.sumOf { it.starsEarned },
                worldSummaries = worldSummaries
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        audio.shutdown()
    }
}
