package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.example.R
import com.example.model.LearningBuddy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * Redesigned Warm Preschool Teacher Voice & Acoustic Audio Engine (100% Offline).
 *
 * Key guarantees:
 * 1. Default Voice: Warm Preschool Teacher ("Ms. Clara") with comfortable pitch (1.00f),
 *    activity-directed delivery (Instructions, Discovery, Patient Retry, Sincere Varied Success,
 *    Storybook Pacing, and Schwa-Free Pure Phonics).
 * 2. Honest Voice Selector: Never fakes multiple voices by shifting pitch/speed on a single voice.
 *    Offers the default Teacher Voice with Preview and Mute controls, and adds a second choice
 *    ONLY when the device exposes a genuinely distinct offline speaker voice.
 * 3. Bundled Offline Clips First: Plays bundled pure phoneme WAV clips (e.g., /m/ "mmmm" and
 *    /s/ "ssss" in res/raw/) and any studio WAV matching VoiceAssetManifest in res/raw/, falling
 *    back to directed teacher speech when unrecorded or dynamic.
 * 4. One Voice at a Time + Audio Ducking + Stale-Prompt Cancellation on Rapid Balloon Taps:
 *    Every balloon tap gets immediate visual burst + soft pop, while stale voice prompts are
 *    discarded so the voice always matches the item currently revealed on screen.
 */
class WonderAudioController(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechSequenceJob: Job? = null
    private var voiceStreamId: Int = 0
    private var duckRestoreJob: Job? = null

    // Monotonic token to discard stale speech when a child pops several balloons rapidly
    private val latestSpeechToken = AtomicInteger(0)
    private val successRotationIndex = AtomicInteger(0)
    private val retryRotationIndex = AtomicInteger(0)

    private val soundPool: SoundPool
    private var sfxBalloonPopId: Int = 0
    private var sfxSuccessChimeId: Int = 0
    private var sfxGentleTryId: Int = 0
    private var sfxWaterSplashId: Int = 0
    private var voicePhonicsMPureId: Int = 0
    private var voicePhonicsSPureId: Int = 0
    private val xyloSoundIds = IntArray(8)

    // Cache of resolved raw resource IDs for studio voice clips in res/raw/
    private val rawVoiceClipSoundPoolIds = mutableMapOf<String, Int>()

    // Distinct offline system Voice objects keyed by TeacherVoiceOption.id
    private val distinctSystemVoicesById = mutableMapOf<String, Voice>()

    private val _lastSpokenCaption = MutableStateFlow("")
    val lastSpokenCaption: StateFlow<String> = _lastSpokenCaption.asStateFlow()

    private val _ttsAvailable = MutableStateFlow(false)
    val ttsAvailable: StateFlow<Boolean> = _ttsAvailable.asStateFlow()

    private val _isTeacherSpeaking = MutableStateFlow(false)
    val isTeacherSpeaking: StateFlow<Boolean> = _isTeacherSpeaking.asStateFlow()

    /**
     * Background music / instrument ducking multiplier:
     * 1.0f when teacher is silent; lowered to 0.18f while the teacher speaks.
     */
    private val _backgroundMusicDuckVolume = MutableStateFlow(1.0f)
    val backgroundMusicDuckVolume: StateFlow<Float> = _backgroundMusicDuckVolume.asStateFlow()

    private val _availableTeacherVoices = MutableStateFlow(
        listOf(DEFAULT_TEACHER_VOICE_OPTION)
    )
    val availableTeacherVoices: StateFlow<List<TeacherVoiceOption>> = _availableTeacherVoices.asStateFlow()

    private val _selectedVoiceId = MutableStateFlow(DEFAULT_TEACHER_VOICE_ID)
    val selectedVoiceId: StateFlow<String> = _selectedVoiceId.asStateFlow()

    private val _lastAuditionClipId = MutableStateFlow<String?>(null)
    val lastAuditionClipId: StateFlow<String?> = _lastAuditionClipId.asStateFlow()

    var narrationEnabled: Boolean = true
    var sfxEnabled: Boolean = true
    var musicEnabled: Boolean = true
    var activeBuddy: LearningBuddy = LearningBuddy.SUNNY_CUB

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(attrs)
            .build()

        runCatching {
            sfxBalloonPopId = soundPool.load(appContext, R.raw.sfx_balloon_pop, 1)
            sfxSuccessChimeId = soundPool.load(appContext, R.raw.sfx_success_chime, 1)
            sfxGentleTryId = soundPool.load(appContext, R.raw.sfx_gentle_try, 1)
            sfxWaterSplashId = soundPool.load(appContext, R.raw.sfx_water_splash, 1)

            // Bundled schwa-free pure phoneme clips (/m/ "mmmm" and /s/ "ssss")
            voicePhonicsMPureId = soundPool.load(appContext, R.raw.voice_phonics_m_pure, 1)
            voicePhonicsSPureId = soundPool.load(appContext, R.raw.voice_phonics_s_pure, 1)

            xyloSoundIds[0] = soundPool.load(appContext, R.raw.sfx_xylo_c4, 1)
            xyloSoundIds[1] = soundPool.load(appContext, R.raw.sfx_xylo_d4, 1)
            xyloSoundIds[2] = soundPool.load(appContext, R.raw.sfx_xylo_e4, 1)
            xyloSoundIds[3] = soundPool.load(appContext, R.raw.sfx_xylo_f4, 1)
            xyloSoundIds[4] = soundPool.load(appContext, R.raw.sfx_xylo_g4, 1)
            xyloSoundIds[5] = soundPool.load(appContext, R.raw.sfx_xylo_a4, 1)
            xyloSoundIds[6] = soundPool.load(appContext, R.raw.sfx_xylo_b4, 1)
            xyloSoundIds[7] = soundPool.load(appContext, R.raw.sfx_xylo_c5, 1)
        }

        runCatching {
            tts = TextToSpeech(appContext, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val engine = tts ?: return
            val result = engine.setLanguage(Locale.US)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED

            runCatching {
                engine.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        if (utteranceId != "wonder_teacher_${latestSpeechToken.get()}") return
                        _isTeacherSpeaking.value = true
                        _backgroundMusicDuckVolume.value = 0.18f
                    }

                    override fun onDone(utteranceId: String?) {
                        if (utteranceId != "wonder_teacher_${latestSpeechToken.get()}") return
                        _isTeacherSpeaking.value = false
                        _backgroundMusicDuckVolume.value = 1.0f
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        if (utteranceId != "wonder_teacher_${latestSpeechToken.get()}") return
                        _isTeacherSpeaking.value = false
                        _backgroundMusicDuckVolume.value = 1.0f
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        if (utteranceId != "wonder_teacher_${latestSpeechToken.get()}") return
                        _isTeacherSpeaking.value = false
                        _backgroundMusicDuckVolume.value = 1.0f
                    }
                })

                discoverHonestOfflineTeacherVoices(engine)
            }
            _ttsAvailable.value = isTtsReady
        } else {
            isTtsReady = false
            _ttsAvailable.value = false
        }
    }

    /**
     * Inspects installed offline English voices and populates [availableTeacherVoices].
     * Follows Section 3 strictly:
     * - Uses the warm teacher voice as default.
     * - Adds a second choice ONLY if a genuinely distinct offline speaker voice exists on device
     *   (different underlying voice name/speaker, never pitch or speed changes of one voice).
     */
    private fun discoverHonestOfflineTeacherVoices(engine: TextToSpeech) {
        val offlineEnglishVoices = engine.voices
            ?.filter { v ->
                v.locale.language == Locale.ENGLISH.language &&
                    !v.isNetworkConnectionRequired
            }
            ?.sortedWith(
                compareByDescending<Voice> { it.quality }
                    .thenByDescending { if (it.locale == Locale.US) 1 else 0 }
                    .thenByDescending {
                        val n = it.name.lowercase()
                        if (n.contains("female") || n.contains("sfg") || n.contains("tpf") || n.contains("iob")) 1 else 0
                    }
            )
            .orEmpty()

        distinctSystemVoicesById.clear()
        val options = mutableListOf<TeacherVoiceOption>()

        val primaryVoice = offlineEnglishVoices.firstOrNull()
        if (primaryVoice != null) {
            distinctSystemVoicesById[DEFAULT_TEACHER_VOICE_ID] = primaryVoice
            engine.voice = primaryVoice
        }
        options.add(
            DEFAULT_TEACHER_VOICE_OPTION.copy(
                systemVoiceName = primaryVoice?.name
            )
        )

        // Add a second option ONLY if there is a genuinely distinct offline speaker on this device
        val distinctSecondVoice = offlineEnglishVoices.firstOrNull { candidate ->
            primaryVoice != null &&
                candidate.name != primaryVoice.name &&
                extractSpeakerBaseKey(candidate.name) != extractSpeakerBaseKey(primaryVoice.name)
        }
        if (distinctSecondVoice != null) {
            val secondId = "teacher_secondary_offline"
            distinctSystemVoicesById[secondId] = distinctSecondVoice
            options.add(
                TeacherVoiceOption(
                    id = secondId,
                    displayName = "Storyteller Companion (Distinct Offline Speaker)",
                    description = "Alternative offline speaker (${distinctSecondVoice.name}) installed on this device.",
                    isDefaultTeacher = false,
                    systemVoiceName = distinctSecondVoice.name,
                    qualityStatus = VoiceRecordingStatus.UNVERIFIED_AWAITING_HUMAN
                )
            )
        }

        _availableTeacherVoices.value = options
        applySelectedTeacherVoiceToEngine(_selectedVoiceId.value)
    }

    private fun extractSpeakerBaseKey(voiceName: String): String {
        return voiceName.lowercase()
            .replace("-local", "")
            .replace("-network", "")
            .trim()
    }

    fun setSelectedTeacherVoice(voiceId: String) {
        val validId = if (_availableTeacherVoices.value.any { it.id == voiceId }) {
            voiceId
        } else {
            DEFAULT_TEACHER_VOICE_ID
        }
        _selectedVoiceId.value = validId
        applySelectedTeacherVoiceToEngine(validId)
    }

    private fun applySelectedTeacherVoiceToEngine(voiceId: String) {
        runCatching {
            val engine = tts ?: return
            val targetVoice = distinctSystemVoicesById[voiceId]
                ?: distinctSystemVoicesById[DEFAULT_TEACHER_VOICE_ID]
            if (targetVoice != null) {
                engine.voice = targetVoice
            }
        }
    }

    /**
     * Previews any voice option in Parent Corner using the exact same sentence
     * ("Let's play together! Find the blue balloon, and take your time.")
     * so adults can compare voices honestly before choosing.
     */
    fun playGardenAudition(playful: Boolean) {
        val lines = listOf(
            "Hello! Let's help our garden grow.",
            "Put three seeds in the pot.",
            "Seed starts with the letter S.",
            "Touch one seed at a time as you count.",
            "Sunny planted the seeds. A little rain fell. Soon, a green shoot appeared.",
            "You planted three seeds. Your garden is growing!"
        )
        _lastSpokenCaption.value = lines.joinToString("\n")
        speakDirectedInternal(lines.joinToString("... "),
            if (playful) VoicePerformanceCategory.SUCCESS else VoicePerformanceCategory.STORY, true)
    }

    fun previewVoiceOption(voiceId: String) {
        setSelectedTeacherVoice(voiceId)
        _lastSpokenCaption.value = VoiceAssetManifest.STANDARD_PREVIEW_SENTENCE
        if (!narrationEnabled) {
            // Even if narration was muted, allow explicit preview button to play the sample
            speakDirectedInternal(
                text = VoiceAssetManifest.STANDARD_PREVIEW_SENTENCE,
                category = VoicePerformanceCategory.INSTRUCTION,
                ignoreMuteForPreview = true
            )
            return
        }
        speakDirected(
            text = VoiceAssetManifest.STANDARD_PREVIEW_SENTENCE,
            category = VoicePerformanceCategory.INSTRUCTION,
            interrupt = true
        )
    }

    /**
     * Plays one of the 6 Teacher Audition lines in Parent Corner (Section 2).
     */
    fun playAuditionClip(entry: VoiceClipManifestEntry) {
        _lastAuditionClipId.value = entry.clipId
        _lastSpokenCaption.value = entry.displayCaption

        if (entry.clipId == "audition_06_phonics") {
            // Play our bundled pure schwa-free /m/ ("mmmm") and /s/ ("ssss") phoneme clips + directed script
            val token = latestSpeechToken.incrementAndGet()
            speechSequenceJob?.cancel()
            runCatching { tts?.stop() }
            speechSequenceJob = scope.launch {
                activateMusicDucking(2400L)
                playSample(voicePhonicsMPureId, volume = 0.72f)
                delay(420L)
                playSample(voicePhonicsSPureId, volume = 0.68f)
                delay(420L)
                if (token == latestSpeechToken.get() && isTtsReady) {
                    speakDirectedInternal(
                        text = entry.spokenScript,
                        category = entry.category,
                        ignoreMuteForPreview = true
                    )
                }
            }
            return
        }

        speakDirectedInternal(
            text = entry.spokenScript,
            category = entry.category,
            ignoreMuteForPreview = true
        )
    }

    /**
     * Primary directed teacher speech entrypoint.
     * 1. Updates visible text caption immediately so text is always visible even if speech is muted/unavailable.
     * 2. Checks if a bundled studio WAV exists in res/raw/ for [clipId] first.
     * 3. Otherwise speaks one voice at a time with category-directed rate, comfortable pitch (1.0f), and audio ducking.
     */
    fun speakDirected(
        text: String,
        category: VoicePerformanceCategory = VoicePerformanceCategory.INSTRUCTION,
        clipId: String? = null,
        interrupt: Boolean = true
    ) {
        if (!interrupt && (_isTeacherSpeaking.value || speechSequenceJob?.isActive == true)) return
        _lastSpokenCaption.value = text
        stopSpeechOnly()
        if (!narrationEnabled) return

        if (clipId != null && tryPlayBundledRawVoiceClip(clipId)) {
            return
        }

        speakDirectedInternal(
            text = text,
            category = category,
            ignoreMuteForPreview = false
        )
    }

    fun speakInstruction(text: String, interrupt: Boolean = true) {
        speakDirected(
            text = text,
            category = VoicePerformanceCategory.INSTRUCTION,
            interrupt = interrupt
        )
    }

    fun speakDiscovery(text: String) {
        speakDirected(
            text = text,
            category = VoicePerformanceCategory.DISCOVERY,
            interrupt = true
        )
    }

    /**
     * Speaks a brief, sincere, and varied success line ("You found it!", "Nice looking!",
     * "Right there! You got it.", "That's the one!") instead of repetitive cartoon cheerleading.
     */
    fun speakSincereSuccess(specificDetail: String? = null) {
        val clips = VoiceAssetManifest.sincereSuccessClips
        val idx = successRotationIndex.getAndIncrement() % clips.size
        val chosen = clips[idx]
        val display = if (!specificDetail.isNullOrBlank()) {
            "${chosen.displayCaption} $specificDetail"
        } else {
            chosen.displayCaption
        }
        val spoken = if (!specificDetail.isNullOrBlank()) {
            "${chosen.spokenScript} $specificDetail"
        } else {
            chosen.spokenScript
        }
        speakDirected(
            text = display,
            category = VoicePerformanceCategory.SUCCESS,
            clipId = chosen.clipId,
            interrupt = true
        ).also {
            if (narrationEnabled && isTtsReady) {
                speakDirectedInternal(spoken, VoicePerformanceCategory.SUCCESS, false)
            }
        }
    }

    /**
     * Speaks a calm, patient retry prompt ("Let's try another one. Look for the round shape.").
     */
    fun speakPatientRetry(gentlePrompt: String? = null) {
        val clips = VoiceAssetManifest.patientRetryClips
        val idx = retryRotationIndex.getAndIncrement() % clips.size
        val chosen = clips[idx]
        val finalPrompt = if (!gentlePrompt.isNullOrBlank()) {
            gentlePrompt
        } else {
            chosen.displayCaption
        }
        speakDirected(
            text = finalPrompt,
            category = VoicePerformanceCategory.RETRY,
            clipId = if (gentlePrompt.isNullOrBlank()) chosen.clipId else null,
            interrupt = true
        )
    }

    /**
     * Speaks a storybook page at a slower, unhurried picture-book pace (0.83f)
     * with natural phrasing and a pause before the page-turn prompt.
     */
    fun speakStoryPage(narrationText: String, interactivePrompt: String = "") {
        val combinedDisplay = if (interactivePrompt.isNotBlank()) {
            "$narrationText  •  $interactivePrompt"
        } else {
            narrationText
        }
        _lastSpokenCaption.value = combinedDisplay
        if (!narrationEnabled) return

        val phrasedScript = if (interactivePrompt.isNotBlank()) {
            "${sanitizeForWarmSpeech(narrationText)}... ${sanitizeForWarmSpeech(interactivePrompt)}"
        } else {
            sanitizeForWarmSpeech(narrationText)
        }
        speakDirectedInternal(
            text = phrasedScript,
            category = VoicePerformanceCategory.STORY,
            ignoreMuteForPreview = false
        )
    }

    /**
     * Rapid-Tap Safe Balloon Pop & Narration:
     * - Gives every successful tap immediate soft acoustic pop feedback.
     * - Increments [latestSpeechToken] and cancels any stale voice prompt so if the child pops
     *   several balloons quickly, the teacher voice always matches the item currently revealed!
     */
    fun playPopAndSpeak(spokenText: String, pitchMultiplier: Float = 1.0f) {
        val token = latestSpeechToken.incrementAndGet()
        _lastSpokenCaption.value = spokenText
        speechSequenceJob?.cancel()
        runCatching { tts?.stop() }

        if (sfxEnabled) {
            playSample(sfxBalloonPopId, volume = 0.42f, rate = pitchMultiplier.coerceIn(0.92f, 1.08f))
        }
        if (narrationEnabled && isTtsReady) {
            speechSequenceJob = scope.launch {
                // 70ms window lets the soft pop transient finish cleanly AND debounces rapid multi-pops
                delay(70L)
                if (token == latestSpeechToken.get()) {
                    speakDirectedInternal(
                        text = spokenText,
                        category = VoicePerformanceCategory.DISCOVERY,
                        ignoreMuteForPreview = false
                    )
                }
            }
        }
    }

    /**
     * Rapid-Tap Safe Multilingual Balloon Pop Narration ("en", "te", "hi").
     * Discards stale voice prompts when multiple balloons are popped in quick succession.
     */
    fun playPopAndSpeakLocalized(
        nativeCaption: String,
        nativeSpeech: String,
        phoneticFallbackSpeech: String,
        languageCode: String,
        pitchMultiplier: Float = 1.0f
    ) {
        stopSpeechOnly()
        val token = latestSpeechToken.get()
        _lastSpokenCaption.value = nativeCaption

        if (sfxEnabled) {
            playSample(sfxBalloonPopId, volume = 0.42f, rate = pitchMultiplier.coerceIn(0.92f, 1.08f))
        }
        if (narrationEnabled && isTtsReady) {
            speechSequenceJob = scope.launch {
                delay(70L)
                if (token == latestSpeechToken.get()) {
                    speakLocalizedInternal(nativeSpeech, phoneticFallbackSpeech, languageCode)
                }
            }
        }
    }

    fun speakLocalized(
        nativeCaption: String,
        nativeSpeech: String,
        phoneticFallbackSpeech: String,
        languageCode: String
    ) {
        stopSpeechOnly()
        val token = latestSpeechToken.get()
        _lastSpokenCaption.value = nativeCaption

        if (narrationEnabled && isTtsReady) {
            speechSequenceJob = scope.launch {
                if (token == latestSpeechToken.get()) {
                    speakLocalizedInternal(nativeSpeech, phoneticFallbackSpeech, languageCode)
                }
            }
        } else if (sfxEnabled) {
            playSample(sfxGentleTryId, volume = 0.35f)
        }
    }

    private fun speakLocalizedInternal(
        nativeSpeech: String,
        phoneticFallbackSpeech: String,
        languageCode: String
    ) {
        runCatching {
            val engine = tts ?: return
            val targetLocale = when (languageCode.lowercase()) {
                "te" -> Locale("te", "IN")
                "hi" -> Locale("hi", "IN")
                else -> Locale.US
            }
            if (targetLocale != Locale.US) {
                val nativeVoice = engine.voices.orEmpty().filter {
                    it.locale.language == targetLocale.language && !it.isNetworkConnectionRequired
                }.sortedBy { it.name }.firstOrNull()
                if (nativeVoice != null) {
                    engine.voice = nativeVoice
                    speakNowWithCategory(
                        text = sanitizeForWarmSpeech(nativeSpeech),
                        category = VoicePerformanceCategory.DISCOVERY,
                        preserveCurrentVoice = true
                    )
                    return
                }
            }
            _lastSpokenCaption.value = phoneticFallbackSpeech
            engine.language = Locale.US
            applySelectedTeacherVoiceToEngine(_selectedVoiceId.value)
            speakNowWithCategory(
                text = sanitizeForWarmSpeech(phoneticFallbackSpeech),
                category = VoicePerformanceCategory.DISCOVERY
            )
        }
    }

    /**
     * Separate Letter Names and Schwa-Free Pure Letter Sounds (Section 1: Phonics).
     * Avoids adding an extra "uh" to consonants such as /m/ ("mmmm") and /s/ ("ssss").
     */
    fun speakPhonicsLetter(letter: Char, speakSoundNotName: Boolean) {
        val upper = letter.uppercaseChar()
        val entry = VoiceAssetManifest.phonicsManifest[upper]
        val displayCaption = if (entry != null) {
            if (speakSoundNotName) {
                "$upper says ${entry.ipaSymbol} • ${entry.exampleWord}"
            } else {
                "Letter $upper • ${entry.exampleWord}"
            }
        } else {
            "Letter $upper"
        }
        _lastSpokenCaption.value = displayCaption
        if (!narrationEnabled) return

        stopSpeechOnly()
        val token = latestSpeechToken.get()

        // Play bundled pure phoneme WAV clip for /m/ and /s/ when speaking pure letter sounds
        if (speakSoundNotName && (upper == 'M' || upper == 'S')) {
            val pureSampleId = if (upper == 'M') voicePhonicsMPureId else voicePhonicsSPureId
            speechSequenceJob = scope.launch {
                activateMusicDucking(1400L)
                playVoiceSample(pureSampleId, volume = 0.72f)
                delay(420L)
                if (token == latestSpeechToken.get() && isTtsReady && entry != null) {
                    speakNowWithCategory(
                        text = entry.exampleWord,
                        category = VoicePerformanceCategory.PHONICS_SOUND
                    )
                }
            }
            return
        }

        if (isTtsReady) {
            val spoken = if (entry != null) {
                if (speakSoundNotName) {
                    entry.pureSoundScript
                } else {
                    "Letter $upper... ${entry.exampleWord}."
                }
            } else {
                "Letter $upper."
            }
            val category = if (speakSoundNotName) {
                VoicePerformanceCategory.PHONICS_SOUND
            } else {
                VoicePerformanceCategory.PHONICS_NAME
            }
            speakNowWithCategory(spoken, category)
        } else {
            val idx = (upper.code - 'A'.code).coerceIn(0, 7)
            playSample(xyloSoundIds[idx], volume = 0.45f)
        }
    }

    fun playNoteHz(
        freqHz: Float,
        durationMs: Int = 340,
        isXylophone: Boolean = false,
        isDrum: Boolean = false
    ) {
        if (!sfxEnabled && !musicEnabled) return
        val duckFactor = _backgroundMusicDuckVolume.value
        if (!isDrum) {
            val xyloIndex = when {
                freqHz < 277f -> 0 // C4
                freqHz < 311f -> 1 // D4
                freqHz < 339f -> 2 // E4
                freqHz < 370f -> 3 // F4
                freqHz < 415f -> 4 // G4
                freqHz < 466f -> 5 // A4
                freqHz < 508f -> 6 // B4
                else -> 7          // C5
            }
            val sampleId = xyloSoundIds[xyloIndex]
            if (sampleId != 0) {
                val baseFreqs = floatArrayOf(261.63f, 293.66f, 329.63f, 349.23f, 392.00f, 440.00f, 493.88f, 523.25f)
                val rate = (freqHz / baseFreqs[xyloIndex]).coerceIn(0.85f, 1.20f)
                playSample(sampleId, volume = 0.62f * duckFactor, rate = rate)
                return
            }
        }
        scope.launch {
            synthesizeSoftDrumOrWarmTone(freqHz, durationMs, isDrum, duckFactor)
        }
    }

    fun playSuccessChime() {
        if (!sfxEnabled) return
        val duckFactor = _backgroundMusicDuckVolume.value
        playSample(sfxSuccessChimeId, volume = 0.48f * duckFactor)
    }

    fun playGentleRetryTone() {
        if (!sfxEnabled) return
        val duckFactor = _backgroundMusicDuckVolume.value
        playSample(sfxGentleTryId, volume = 0.40f * duckFactor)
    }

    fun playBubblePopSound(pitchMultiplier: Float = 1.0f) {
        if (!sfxEnabled) return
        playSample(sfxBalloonPopId, volume = 0.44f, rate = pitchMultiplier.coerceIn(0.90f, 1.12f))
    }

    fun playWaterSplash(sinks: Boolean) {
        if (!sfxEnabled) return
        val rate = if (sinks) 0.88f else 1.08f
        playSample(sfxWaterSplashId, volume = 0.50f, rate = rate)
    }

    /**
     * Plays a warm marimba chime and then speaks the animal/object discovery line
     * with gentle wonder and room to look.
     */
    fun playAnimalOrObjectSignature(baseFreqHz: Float, label: String, spokenFact: String) {
        val token = latestSpeechToken.incrementAndGet()
        _lastSpokenCaption.value = "$label — $spokenFact"
        speechSequenceJob?.cancel()
        runCatching { tts?.stop() }

        if (sfxEnabled) {
            val idx = ((baseFreqHz.toInt() / 45) % 8).coerceIn(0, 7)
            playSample(xyloSoundIds[idx], volume = 0.28f)
        }
        if (narrationEnabled && isTtsReady) {
            speechSequenceJob = scope.launch {
                delay(110L)
                if (token == latestSpeechToken.get()) {
                    speakNowWithCategory(
                        text = sanitizeForWarmSpeech("$label... $spokenFact"),
                        category = VoicePerformanceCategory.DISCOVERY
                    )
                }
            }
        }
    }

    /**
     * Immediately stops any active or queued speech when leaving an activity or navigating.
     */
    fun stopSpeechOnly() {
        runCatching {
            latestSpeechToken.incrementAndGet()
            speechSequenceJob?.cancel()
            duckRestoreJob?.cancel()
            _isTeacherSpeaking.value = false
            _backgroundMusicDuckVolume.value = 1.0f
            tts?.stop()
            soundPool.stop(voiceStreamId)
            voiceStreamId = 0
        }
    }

    /**
     * Immediately stops all speech and sound effects when leaving an activity or backgrounding the app.
     */
    fun stopAllAudio() {
        runCatching {
            stopSpeechOnly()
            soundPool.autoPause()
        }
    }

    fun shutdown() {
        stopAllAudio()
        runCatching {
            soundPool.release()
            tts?.shutdown()
        }
    }

    private fun tryPlayBundledRawVoiceClip(clipId: String): Boolean {
        val entry = VoiceAssetManifest.allManifestEntries.find { it.clipId == clipId } ?: return false
        val rawName = entry.expectedRawFileName.substringBeforeLast(".")
        val cachedSoundId = rawVoiceClipSoundPoolIds[rawName]
        if (cachedSoundId != null && cachedSoundId != 0) {
            activateMusicDucking(1400L)
            playVoiceSample(cachedSoundId, volume = 0.85f)
            return true
        }
        val resId = appContext.resources.getIdentifier(rawName, "raw", appContext.packageName)
        if (resId != 0) {
            val loadedId = soundPool.load(appContext, resId, 1)
            rawVoiceClipSoundPoolIds[rawName] = loadedId
            activateMusicDucking(1400L)
            val token = latestSpeechToken.get()
            speechSequenceJob = scope.launch {
                delay(45L)
                if (token == latestSpeechToken.get()) playVoiceSample(loadedId, volume = 0.85f)
            }
            return true
        }
        return false
    }

    private fun speakDirectedInternal(
        text: String,
        category: VoicePerformanceCategory,
        ignoreMuteForPreview: Boolean
    ) {
        if (!narrationEnabled && !ignoreMuteForPreview) return
        stopSpeechOnly()
        val token = latestSpeechToken.get()

        val cleanText = sanitizeForWarmSpeech(text)
        if (isTtsReady) {
            speechSequenceJob = scope.launch {
                if (category.prePauseMs > 0L) {
                    delay(category.prePauseMs)
                }
                if (token == latestSpeechToken.get()) {
                    speakNowWithCategory(cleanText, category)
                }
            }
        } else if (sfxEnabled) {
            playSample(sfxGentleTryId, volume = 0.32f)
        }
    }

    private fun speakNowWithCategory(text: String, category: VoicePerformanceCategory, preserveCurrentVoice: Boolean = false) {
        runCatching {
            val engine = tts ?: return
            if (!preserveCurrentVoice) applySelectedTeacherVoiceToEngine(_selectedVoiceId.value)
            engine.setPitch(category.pitch.coerceIn(0.98f, 1.02f))
            engine.setSpeechRate(category.speechRate.coerceIn(0.80f, 0.94f))
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            }
            val estimatedDurationMs = (text.length * 100L).coerceIn(2000L, 120000L)
            activateMusicDucking(estimatedDurationMs)
            // Always QUEUE_FLUSH so only one voice plays at a time and stale lines never pile up
            engine.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                params,
                "wonder_teacher_${latestSpeechToken.get()}"
            )
        }
    }

    private fun activateMusicDucking(durationMs: Long) {
        _isTeacherSpeaking.value = true
        _backgroundMusicDuckVolume.value = 0.18f
        duckRestoreJob?.cancel()
        duckRestoreJob = scope.launch {
            delay(durationMs)
            _isTeacherSpeaking.value = false
            _backgroundMusicDuckVolume.value = 1.0f
        }
    }

    private fun playVoiceSample(soundId: Int, volume: Float) {
        if (soundId == 0) return
        runCatching {
            soundPool.stop(voiceStreamId)
            voiceStreamId = soundPool.play(soundId, volume, volume, 1, 0, 1f)
        }
    }

    private fun playSample(soundId: Int, volume: Float = 0.55f, rate: Float = 1.0f) {
        if (soundId == 0) return
        runCatching {
            soundPool.play(soundId, volume.coerceIn(0f, 1f), volume.coerceIn(0f, 1f), 1, 0, rate.coerceIn(0.5f, 2.0f))
        }
    }

    private fun sanitizeForWarmSpeech(raw: String): String {
        return raw
            .replace("•", "... ")
            .replace("—", "... ")
            .replace("⭐", "")
            .replace("🌟", "")
            .replace("🎈", "")
            .replace("💥", "")
            .replace("🎉", "")
            .replace("✨", "")
            .replace("🔊", "")
            .replace("🎯", "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun synthesizeSoftDrumOrWarmTone(
        freqHz: Float,
        durationMs: Int,
        isDrum: Boolean,
        duckFactor: Float
    ) {
        runCatching {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(512)
            val pcm = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val attack = 0.5 * (1.0 - cos(PI * (t / 0.008).coerceAtMost(1.0)))
                val decay = if (isDrum) exp(-14.0 * t) else exp(-5.0 * t)
                val currentFreq: Double = if (isDrum) freqHz.toDouble() * (0.65 + 0.35 * exp(-18.0 * t)) else freqHz.toDouble()
                val wave = sin(2.0 * PI * currentFreq * t) + 0.15 * sin(2.0 * PI * currentFreq * 2.0 * t)
                val sample = (wave * attack * decay * 0.36 * duckFactor * Short.MAX_VALUE).toInt()
                pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            val byteCount = pcm.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(byteCount.coerceAtLeast(2048))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(pcm, 0, pcm.size)
            track.play()
            Thread.sleep((pcm.size * 1000L / sampleRate) + 30L)
            track.stop()
            track.release()
        }
    }

    companion object {
        const val DEFAULT_TEACHER_VOICE_ID = "teacher_warm_default"

        val DEFAULT_TEACHER_VOICE_OPTION = TeacherVoiceOption(
            id = DEFAULT_TEACHER_VOICE_ID,
            displayName = "Ms. Clara — Warm Preschool Teacher (Default)",
            description = "Natural adult female teacher voice with comfortable pitch, gentle pauses, and schwa-free phonics.",
            isDefaultTeacher = true,
            systemVoiceName = null,
            qualityStatus = VoiceRecordingStatus.UNVERIFIED_AWAITING_HUMAN
        )
    }
}
