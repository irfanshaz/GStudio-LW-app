package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import com.example.model.LearningBuddy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * 100% offline audio & narration engine.
 * Combines:
 * 1. Android TextToSpeech with phonetic pronunciation tuning (distinguishing letter names vs. phonetic sounds).
 * 2. Polyphonic real-time PCM synthesis (AudioTrack) for xylophone, piano, drums, water splashes, and animal sound signatures.
 * 3. Strict lifecycle safety: stops all narration and audio immediately on screen exit, overlay close, or app background.
 */
class WonderAudioController(context: Context) : TextToSpeech.OnInitListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var activeSynthJob: Job? = null

    private val _lastSpokenCaption = MutableStateFlow("")
    val lastSpokenCaption: StateFlow<String> = _lastSpokenCaption.asStateFlow()

    private val _ttsAvailable = MutableStateFlow(false)
    val ttsAvailable: StateFlow<Boolean> = _ttsAvailable.asStateFlow()

    var narrationEnabled: Boolean = true
    var sfxEnabled: Boolean = true
    var musicEnabled: Boolean = true
    var activeBuddy: LearningBuddy = LearningBuddy.SUNNY_CUB

    init {
        runCatching {
            tts = TextToSpeech(context.applicationContext, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED
            _ttsAvailable.value = isTtsReady
        } else {
            isTtsReady = false
            _ttsAvailable.value = false
        }
    }

    fun speakInstruction(text: String, interrupt: Boolean = true) {
        _lastSpokenCaption.value = text
        if (!narrationEnabled) return
        if (isTtsReady) {
            tts?.setPitch(activeBuddy.voicePitch)
            tts?.setSpeechRate(activeBuddy.voiceRate)
            val queueMode = if (interrupt) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(text, queueMode, null, "wonder_utt_${System.currentTimeMillis()}")
        } else {
            // Fallback gentle chime if offline TTS voice pack is missing on device
            playSuccessChime()
        }
    }

    fun speakPhonicsLetter(letter: Char, speakSoundNotName: Boolean) {
        val upper = letter.uppercaseChar()
        val phoneticSound = PHONETIC_SOUND_MAP[upper] ?: upper.lowercase()
        val displayCaption = if (speakSoundNotName) {
            "$upper says /$phoneticSound/!"
        } else {
            "Letter $upper! ($upper says $phoneticSound)"
        }
        _lastSpokenCaption.value = displayCaption
        if (!narrationEnabled) return
        if (isTtsReady) {
            tts?.setPitch(activeBuddy.voicePitch)
            tts?.setSpeechRate(0.84f)
            val spoken = if (speakSoundNotName) {
                "$phoneticSound! $upper makes the $phoneticSound sound."
            } else {
                "Letter $upper! $phoneticSound."
            }
            tts?.speak(spoken, TextToSpeech.QUEUE_FLUSH, null, "phonics_$upper")
        } else {
            playNoteHz(330f + (upper.code - 'A'.code) * 14f, durationMs = 260, isXylophone = true)
        }
    }

    fun playNoteHz(
        freqHz: Float,
        durationMs: Int = 340,
        isXylophone: Boolean = false,
        isDrum: Boolean = false
    ) {
        if (!sfxEnabled && !musicEnabled) return
        scope.launch {
            synthesizeTone(freqHz, durationMs, isXylophone, isDrum)
        }
    }

    fun playSuccessChime() {
        if (!sfxEnabled) return
        activeSynthJob?.cancel()
        activeSynthJob = scope.launch {
            synthesizeChordArpeggio(floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f), 110)
        }
    }

    fun playGentleRetryTone() {
        if (!sfxEnabled) return
        scope.launch {
            synthesizeTone(293.66f, 180, isXylophone = true, isDrum = false)
        }
    }

    fun playBubblePopSound(pitchMultiplier: Float = 1.0f) {
        if (!sfxEnabled) return
        scope.launch {
            synthesizeFrequencySweep(420f * pitchMultiplier, 920f * pitchMultiplier, 110)
        }
    }

    fun playWaterSplash(sinks: Boolean) {
        if (!sfxEnabled) return
        scope.launch {
            if (sinks) {
                synthesizeFrequencySweep(480f, 160f, 280)
            } else {
                synthesizeFrequencySweep(320f, 640f, 240)
            }
        }
    }

    fun playAnimalOrObjectSignature(baseFreqHz: Float, label: String, spokenFact: String) {
        _lastSpokenCaption.value = "$label — $spokenFact"
        if (sfxEnabled) {
            scope.launch {
                synthesizeFrequencySweep(baseFreqHz * 0.85f, baseFreqHz * 1.25f, 260)
            }
        }
        if (narrationEnabled && isTtsReady) {
            tts?.setPitch(activeBuddy.voicePitch)
            tts?.setSpeechRate(0.92f)
            tts?.speak("$label! $spokenFact", TextToSpeech.QUEUE_FLUSH, null, "media_${System.currentTimeMillis()}")
        }
    }

    fun stopAllAudio() {
        runCatching {
            tts?.stop()
            activeSynthJob?.cancel()
        }
    }

    fun shutdown() {
        stopAllAudio()
        runCatching {
            tts?.shutdown()
        }
    }

    private fun synthesizeTone(
        freqHz: Float,
        durationMs: Int,
        isXylophone: Boolean,
        isDrum: Boolean
    ) {
        runCatching {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(256)
            val pcm = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val decay = if (isDrum) exp(-18.0 * t) else if (isXylophone) exp(-7.5 * t) else exp(-3.5 * t)
                val currentFreq: Double = if (isDrum) freqHz.toDouble() * exp(-10.0 * t) else freqHz.toDouble()
                val fundamental = sin(2.0 * PI * currentFreq * t)
                val overtone = if (isXylophone) 0.35 * sin(2.0 * PI * currentFreq * 3.0 * t) else 0.2 * sin(2.0 * PI * currentFreq * 2.0 * t)
                val sample = ((fundamental + overtone) * decay * 0.45 * Short.MAX_VALUE).toInt()
                pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(pcm, sampleRate)
        }
    }

    private fun synthesizeFrequencySweep(startHz: Float, endHz: Float, durationMs: Int) {
        runCatching {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(256)
            val pcm = ShortArray(numSamples)
            var phase = 0.0
            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val freq = startHz + (endHz - startHz) * progress
                phase += 2.0 * PI * freq / sampleRate
                val env = sin(PI * progress)
                val sample = (sin(phase) * env * 0.42 * Short.MAX_VALUE).toInt()
                pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(pcm, sampleRate)
        }
    }

    private fun synthesizeChordArpeggio(notesHz: FloatArray, noteDurationMs: Int) {
        runCatching {
            val sampleRate = 22050
            val samplesPerNote = (sampleRate * (noteDurationMs / 1000f)).toInt().coerceAtLeast(256)
            val totalSamples = samplesPerNote * notesHz.size
            val pcm = ShortArray(totalSamples)
            for (n in notesHz.indices) {
                val freq = notesHz[n]
                for (i in 0 until samplesPerNote) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-5.0 * t)
                    val wave = sin(2.0 * PI * freq * t) + 0.25 * sin(2.0 * PI * freq * 2.0 * t)
                    val idx = n * samplesPerNote + i
                    pcm[idx] = (wave * env * 0.38 * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
            playPcmBuffer(pcm, sampleRate)
        }
    }

    private fun playPcmBuffer(pcm: ShortArray, sampleRate: Int) {
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
        Thread.sleep((pcm.size * 1000L / sampleRate) + 40L)
        track.stop()
        track.release()
    }

    companion object {
        private val PHONETIC_SOUND_MAP = mapOf(
            'A' to "aah as in apple",
            'B' to "buh as in bear",
            'C' to "kuh as in cat",
            'D' to "duh as in dog",
            'E' to "eh as in elephant",
            'F' to "fff as in fish",
            'G' to "guh as in goat",
            'H' to "huh as in hat",
            'I' to "ih as in igloo",
            'J' to "juh as in jam",
            'K' to "kuh as in kite",
            'L' to "lll as in lion",
            'M' to "mmm as in moon",
            'N' to "nnn as in nest",
            'O' to "aw as in octopus",
            'P' to "puh as in puppy",
            'Q' to "kwuh as in queen",
            'R' to "rrr as in rainbow",
            'S' to "sss as in sun",
            'T' to "tuh as in turtle",
            'U' to "uh as in umbrella",
            'V' to "vvv as in violin",
            'W' to "wuh as in whale",
            'X' to "ks as in xylophone",
            'Y' to "yuh as in yak",
            'Z' to "zzz as in zebra"
        )
    }
}
