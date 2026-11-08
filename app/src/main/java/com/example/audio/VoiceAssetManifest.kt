package com.example.audio

import androidx.annotation.RawRes

/**
 * ============================================================================================
 * LITTLE WONDER WORLD — VOICE RECORDING SCRIPT, PERFORMANCE DIRECTIONS & ASSET MANIFEST
 * ============================================================================================
 * Persona: Warm Preschool Teacher ("Ms. Clara")
 * - Natural adult female voice sitting beside a child (ages 2–6).
 * - Sweet, playful, reassuring, with clear pronunciation, gentle smile in delivery, and natural pauses.
 * - Comfortable natural pitch (1.00f); NO squeaky cartoon pitch-shifting, NO baby talk, NO sing-song repetition.
 * - Phonics: Pure phoneme sounds without adding a trailing schwa "uh" to consonants (/m/ = "mmmm", /s/ = "ssss").
 */

enum class VoiceRecordingStatus(val label: String) {
    VERIFIED_HUMAN_STUDIO("Verified Human Recording"),
    BUNDLED_ACOUSTIC_CLIP("Bundled Offline Acoustic Clip"),
    UNVERIFIED_AWAITING_HUMAN("Unverified — Awaiting Human Studio Recording (Device Speech Fallback)")
}

enum class VoicePerformanceCategory(
    val displayName: String,
    val speechRate: Float,
    val pitch: Float,
    val prePauseMs: Long,
    val directionNote: String
) {
    INSTRUCTION(
        displayName = "Instruction",
        speechRate = 0.90f,
        pitch = 1.00f,
        prePauseMs = 60L,
        directionNote = "One short action at a time, with warm emphasis on the key word the child needs to hear."
    ),
    DISCOVERY(
        displayName = "Discovery",
        speechRate = 0.88f,
        pitch = 1.01f,
        prePauseMs = 80L,
        directionNote = "Gentle wonder and room to look; pause between naming the object and pointing out a detail."
    ),
    RETRY(
        displayName = "Patient Retry",
        speechRate = 0.87f,
        pitch = 0.99f,
        prePauseMs = 70L,
        directionNote = "Calm, reassuring, and unhurried; never disappointed or corrective."
    ),
    SUCCESS(
        displayName = "Sincere Success",
        speechRate = 0.92f,
        pitch = 1.01f,
        prePauseMs = 50L,
        directionNote = "Brief, warm, sincere smile in the voice; varied phrases rather than repetitive cheerleading."
    ),
    STORY(
        displayName = "Storytime",
        speechRate = 0.83f,
        pitch = 1.00f,
        prePauseMs = 120L,
        directionNote = "Slower, unhurried picture-book cadence with natural phrasing and a calm pause before turning the page."
    ),
    PHONICS_NAME(
        displayName = "Phonics — Letter Name",
        speechRate = 0.86f,
        pitch = 1.00f,
        prePauseMs = 60L,
        directionNote = "Crisp, clear letter name spoken alone first."
    ),
    PHONICS_SOUND(
        displayName = "Phonics — Pure Sound",
        speechRate = 0.84f,
        pitch = 1.00f,
        prePauseMs = 60L,
        directionNote = "Pure phoneme without trailing 'uh' schwa on continuants (/m/ = 'mmmm', /s/ = 'ssss', /f/ = 'ffff')."
    ),
    DYNAMIC_DEVICE_TTS(
        displayName = "Dynamic Custom Text (Device TTS)",
        speechRate = 0.90f,
        pitch = 1.00f,
        prePauseMs = 60L,
        directionNote = "Used only for user-authored dynamic content such as Family Vault custom names."
    )
}

data class VoiceClipManifestEntry(
    val clipId: String,
    val expectedRawFileName: String,
    val category: VoicePerformanceCategory,
    val displayCaption: String,
    val spokenScript: String,
    val performanceDirection: String,
    val status: VoiceRecordingStatus = VoiceRecordingStatus.UNVERIFIED_AWAITING_HUMAN,
    @RawRes val bundledRawResId: Int? = null
)

/**
 * Honest Teacher Voice Option model.
 * Only lists voices that are genuinely distinct speakers (never pitch/speed clones of the same voice).
 */
data class TeacherVoiceOption(
    val id: String,
    val displayName: String,
    val description: String,
    val isDefaultTeacher: Boolean,
    val systemVoiceName: String? = null,
    val qualityStatus: VoiceRecordingStatus = VoiceRecordingStatus.UNVERIFIED_AWAITING_HUMAN
)

object VoiceAssetManifest {

    const val STANDARD_PREVIEW_SENTENCE =
        "Let's play together! Find the blue balloon, and take your time."

    /**
     * 6-Line Teacher Audition Suite (Section 2 of specification).
     * Allows parents/reviewers to audition all 6 performance styles through the device speaker
     * before mass-producing studio clips.
     */
    val sixAuditionLines: List<VoiceClipManifestEntry> = listOf(
        VoiceClipManifestEntry(
            clipId = "audition_01_instruction",
            expectedRawFileName = "voice_audition_01_instruction.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Find the BLUE balloon.",
            spokenScript = "Find the blue balloon.",
            performanceDirection = "Warm teacher sitting beside the child. Clear emphasis on 'blue', natural falling cadence."
        ),
        VoiceClipManifestEntry(
            clipId = "audition_02_discovery",
            expectedRawFileName = "voice_audition_02_discovery.wav",
            category = VoicePerformanceCategory.DISCOVERY,
            displayCaption = "A little turtle! Look at its shell.",
            spokenScript = "A little turtle... Look at its shell.",
            performanceDirection = "Quiet wonder on 'A little turtle', 350ms breath pause, warm observation on 'Look at its shell.'"
        ),
        VoiceClipManifestEntry(
            clipId = "audition_03_retry",
            expectedRawFileName = "voice_audition_03_retry.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Let’s try another one. Look for the round shape.",
            spokenScript = "Let's try another one... Look for the round shape.",
            performanceDirection = "Calm, patient, reassuring. Zero urgency or disappointment."
        ),
        VoiceClipManifestEntry(
            clipId = "audition_04_success",
            expectedRawFileName = "voice_audition_04_success.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "You found it! That’s the blue one! Nice looking!",
            spokenScript = "You found it... That's the blue one... Nice looking.",
            performanceDirection = "Three brief, sincere, varied affirmations with a gentle smile in the voice—not high-pitched cheerleading."
        ),
        VoiceClipManifestEntry(
            clipId = "audition_05_story",
            expectedRawFileName = "voice_audition_05_story.wav",
            category = VoicePerformanceCategory.STORY,
            displayCaption = "The little boat floated across the quiet pond... Ready for the next page?",
            spokenScript = "The little boat floated across the quiet pond... Ready for the next page?",
            performanceDirection = "Slower picture-book storytelling pace (115 WPM) with a 600ms pause before the page-turn prompt."
        ),
        VoiceClipManifestEntry(
            clipId = "audition_06_phonics",
            expectedRawFileName = "voice_audition_06_phonics.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "Letter M: /m/ (mmmm, moon). Letter S: /s/ (ssss, sun).",
            spokenScript = "Letter M... mmmm... moon. Letter S... ssss... sun.",
            performanceDirection = "Record letter name and pure continuant sound separately. Strictly no 'muh' or 'suh' schwa vowel."
        )
    )

    /**
     * Sincere, varied success lines (rotated so consecutive successes never repeat the same phrase).
     */
    val sincereSuccessClips: List<VoiceClipManifestEntry> = listOf(
        VoiceClipManifestEntry(
            clipId = "success_you_found_it",
            expectedRawFileName = "voice_success_you_found_it.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "You found it!",
            spokenScript = "You found it.",
            performanceDirection = "Sincere, warm nod of approval."
        ),
        VoiceClipManifestEntry(
            clipId = "success_nice_looking",
            expectedRawFileName = "voice_success_nice_looking.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "Nice looking!",
            spokenScript = "Nice looking.",
            performanceDirection = "Gentle, encouraging praise focused on the child's observation."
        ),
        VoiceClipManifestEntry(
            clipId = "success_right_there",
            expectedRawFileName = "voice_success_right_there.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "Right there! You got it.",
            spokenScript = "Right there... You got it.",
            performanceDirection = "Warm, conversational confirmation."
        ),
        VoiceClipManifestEntry(
            clipId = "success_thats_the_one",
            expectedRawFileName = "voice_success_thats_the_one.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "That's the one!",
            spokenScript = "That's the one.",
            performanceDirection = "Natural smile in delivery, comfortable adult pitch."
        )
    )

    /**
     * Patient, calm retry lines.
     */
    val patientRetryClips: List<VoiceClipManifestEntry> = listOf(
        VoiceClipManifestEntry(
            clipId = "retry_try_another",
            expectedRawFileName = "voice_retry_try_another.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Let's try another one.",
            spokenScript = "Let's try another one.",
            performanceDirection = "Calm, unhurried, reassuring."
        ),
        VoiceClipManifestEntry(
            clipId = "retry_look_closely",
            expectedRawFileName = "voice_retry_look_closely.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Take your time. Let's look together.",
            spokenScript = "Take your time... Let's look together.",
            performanceDirection = "Warm teacher sitting beside the child."
        ),
        VoiceClipManifestEntry(
            clipId = "retry_round_shape",
            expectedRawFileName = "voice_retry_round_shape.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Let’s try another one. Look for the matching shape.",
            spokenScript = "Let's try another one... Look for the matching shape.",
            performanceDirection = "Gentle guidance toward the visual clue."
        )
    )

    /**
     * Pure, schwa-free Phonics Manifest (Letter Names & Pure Phoneme Sounds separated).
     * Avoids adding "uh" to continuants (/m/, /s/, /f/, /l/, /n/, /r/, /v/, /z/) and clipped stops.
     */
    data class PhonicsEntry(
        val letter: Char,
        val letterNameFile: String,
        val letterSoundFile: String,
        val ipaSymbol: String,
        val pureSoundScript: String,
        val exampleWord: String,
        val teacherNote: String
    )

    val phonicsManifest: Map<Char, PhonicsEntry> = mapOf(
        'A' to PhonicsEntry('A', "voice_letter_a_name.wav", "voice_letter_a_sound.wav", "/æ/", "aah, apple", "Apple", "Short front vowel /æ/ as in apple"),
        'B' to PhonicsEntry('B', "voice_letter_b_name.wav", "voice_letter_b_sound.wav", "/b/", "b, ball", "Ball", "Voiced bilabial stop; clip cleanly before schwa 'uh'"),
        'C' to PhonicsEntry('C', "voice_letter_c_name.wav", "voice_letter_c_sound.wav", "/k/", "k, cat", "Cat", "Unvoiced velar stop; crisp /k/ without 'uh'"),
        'D' to PhonicsEntry('D', "voice_letter_d_name.wav", "voice_letter_d_sound.wav", "/d/", "d, dog", "Dog", "Clipped alveolar stop /d/, no 'duh'"),
        'E' to PhonicsEntry('E', "voice_letter_e_name.wav", "voice_letter_e_sound.wav", "/ɛ/", "eh, elephant", "Elephant", "Short vowel /ɛ/ as in elephant"),
        'F' to PhonicsEntry('F', "voice_letter_f_name.wav", "voice_letter_f_sound.wav", "/f/", "ffff, fish", "Fish", "Continuous unvoiced labiodental fricative 'ffff' — NEVER 'fuh'"),
        'G' to PhonicsEntry('G', "voice_letter_g_name.wav", "voice_letter_g_sound.wav", "/ɡ/", "g, goat", "Goat", "Clipped velar stop /ɡ/, minimal vowel release"),
        'H' to PhonicsEntry('H', "voice_letter_h_name.wav", "voice_letter_h_sound.wav", "/h/", "hhh, hat", "Hat", "Soft breathy glottal fricative 'hhh' — NEVER 'huh'"),
        'I' to PhonicsEntry('I', "voice_letter_i_name.wav", "voice_letter_i_sound.wav", "/ɪ/", "ih, igloo", "Igloo", "Short vowel /ɪ/ as in igloo"),
        'J' to PhonicsEntry('J', "voice_letter_j_name.wav", "voice_letter_j_sound.wav", "/dʒ/", "j, jam", "Jam", "Clean affricate /dʒ/ into example word"),
        'K' to PhonicsEntry('K', "voice_letter_k_name.wav", "voice_letter_k_sound.wav", "/k/", "k, kite", "Kite", "Unvoiced crisp /k/, no 'kuh'"),
        'L' to PhonicsEntry('L', "voice_letter_l_name.wav", "voice_letter_l_sound.wav", "/l/", "llll, lion", "Lion", "Continuous liquid 'llll' — NEVER 'luh'"),
        'M' to PhonicsEntry('M', "voice_letter_m_name.wav", "voice_letter_m_sound.wav", "/m/", "mmmm, moon", "Moon", "Continuous bilabial nasal 'mmmm' — NEVER 'muh'"),
        'N' to PhonicsEntry('N', "voice_letter_n_name.wav", "voice_letter_n_sound.wav", "/n/", "nnnn, nest", "Nest", "Continuous alveolar nasal 'nnnn' — NEVER 'nuh'"),
        'O' to PhonicsEntry('O', "voice_letter_o_name.wav", "voice_letter_o_sound.wav", "/ɒ/", "aw, octopus", "Octopus", "Short open vowel as in octopus"),
        'P' to PhonicsEntry('P', "voice_letter_p_name.wav", "voice_letter_p_sound.wav", "/p/", "p, puppy", "Puppy", "Unvoiced bilabial puff of air /p/ — NEVER 'puh'"),
        'Q' to PhonicsEntry('Q', "voice_letter_q_name.wav", "voice_letter_q_sound.wav", "/kw/", "kw, queen", "Queen", "Clean labialized velar /kw/"),
        'R' to PhonicsEntry('R', "voice_letter_r_name.wav", "voice_letter_r_sound.wav", "/ɹ/", "rrrr, rainbow", "Rainbow", "Continuous approximant 'rrrr' — NEVER 'ruh'"),
        'S' to PhonicsEntry('S', "voice_letter_s_name.wav", "voice_letter_s_sound.wav", "/s/", "ssss, sun", "Sun", "Continuous alveolar sibilant 'ssss' — NEVER 'suh'"),
        'T' to PhonicsEntry('T', "voice_letter_t_name.wav", "voice_letter_t_sound.wav", "/t/", "t, turtle", "Turtle", "Unvoiced alveolar tap /t/ — NEVER 'tuh'"),
        'U' to PhonicsEntry('U', "voice_letter_u_name.wav", "voice_letter_u_sound.wav", "/ʌ/", "uh, umbrella", "Umbrella", "Short central vowel /ʌ/ as in umbrella"),
        'V' to PhonicsEntry('V', "voice_letter_v_name.wav", "voice_letter_v_sound.wav", "/v/", "vvvv, violin", "Violin", "Continuous voiced fricative 'vvvv' — NEVER 'vuh'"),
        'W' to PhonicsEntry('W', "voice_letter_w_name.wav", "voice_letter_w_sound.wav", "/w/", "w, whale", "Whale", "Rounded glide /w/ into whale"),
        'X' to PhonicsEntry('X', "voice_letter_x_name.wav", "voice_letter_x_sound.wav", "/ks/", "ks, box", "Xylophone", "Crisp cluster /ks/"),
        'Y' to PhonicsEntry('Y', "voice_letter_y_name.wav", "voice_letter_y_sound.wav", "/j/", "y, yak", "Yak", "Palatal glide /j/"),
        'Z' to PhonicsEntry('Z', "voice_letter_z_name.wav", "voice_letter_z_sound.wav", "/z/", "zzzz, zebra", "Zebra", "Continuous voiced sibilant 'zzzz' — NEVER 'zuh'")
    )

    /**
     * Full Line-to-File Manifest combining Audition, Core Feedback, Phonics, and Activity Prompts.
     */
    val allManifestEntries: List<VoiceClipManifestEntry> by lazy {
        val list = mutableListOf<VoiceClipManifestEntry>()
        list.addAll(sixAuditionLines)
        list.addAll(sincereSuccessClips)
        list.addAll(patientRetryClips)
        phonicsManifest.values.forEach { entry ->
            list.add(
                VoiceClipManifestEntry(
                    clipId = "phonics_${entry.letter.lowercaseChar()}_name",
                    expectedRawFileName = entry.letterNameFile,
                    category = VoicePerformanceCategory.PHONICS_NAME,
                    displayCaption = "Letter ${entry.letter}",
                    spokenScript = "Letter ${entry.letter}.",
                    performanceDirection = "Warm, clear letter name."
                )
            )
            list.add(
                VoiceClipManifestEntry(
                    clipId = "phonics_${entry.letter.lowercaseChar()}_sound",
                    expectedRawFileName = entry.letterSoundFile,
                    category = VoicePerformanceCategory.PHONICS_SOUND,
                    displayCaption = "${entry.letter} says ${entry.ipaSymbol} • ${entry.exampleWord}",
                    spokenScript = "${entry.pureSoundScript}.",
                    performanceDirection = entry.teacherNote
                )
            )
        }
        list
    }

    /**
     * Explicit inventory of screens/features that still require runtime on-device TTS
     * for dynamic user-authored content (with visible text captions when speech is unavailable).
     */
    val screensUsingDynamicDeviceSpeech: List<Pair<String, String>> = listOf(
        "World 26: Family Tree & Voice Vault" to "Speaks custom user-entered family member names (e.g., 'Grandma Lakshmi', 'Uncle Ravi') when no custom recording is attached; always displays visible name cards.",
        "Unrecorded Catalog Lines (Fallback Mode)" to "Until human studio WAV files listed in VoiceAssetManifest are dropped into app/src/main/res/raw/, unrecorded lines use the single default Warm Preschool Teacher device voice with activity-directed pacing and visible text captions."
    )
}
