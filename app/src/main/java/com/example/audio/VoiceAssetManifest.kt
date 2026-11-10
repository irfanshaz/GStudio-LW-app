package com.example.audio

import androidx.annotation.RawRes
import com.example.R

/**
 * ============================================================================================
 * LITTLE WONDER WORLD — VOICE RECORDING SCRIPT, PERFORMANCE DIRECTIONS & ASSET MANIFEST
 * ============================================================================================
 * Persona: Warm Preschool Teacher ("Ms. Clara")
 * - Natural adult female voice sitting beside a child (ages 2–6).
 * - Sweet, playful, reassuring, with clear pronunciation, gentle smile in delivery, and natural pauses.
 * - Comfortable natural pitch (1.00f); NO squeaky cartoon pitch-shifting, NO baby talk, NO sing-song repetition.
 * - Phonics: Pure phoneme sounds without adding a trailing schwa "uh" to consonants (/m/ = "mmmm", /s/ = "ssss").
 * - Distinct Phonics Clip IDs: Letter Name, Isolated Sound, Segmented Blend, and Whole Word have separate IDs.
 */

enum class VoiceRecordingStatus(val label: String) {
    VERIFIED_HUMAN_STUDIO("Verified Human Recording"),
    BUNDLED_ACOUSTIC_CLIP("Bundled Offline Acoustic Clip (Needs Human Pronunciation Review)"),
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
    AUDITION_CALM(
        displayName = "Sunny Audition — Calm",
        speechRate = 0.84f,
        pitch = 1.00f,
        prePauseMs = 90L,
        directionNote = "Warm adult character with gentle pacing and unhurried room to think."
    ),
    AUDITION_PLAYFUL(
        displayName = "Sunny Audition — Playful",
        speechRate = 0.92f,
        pitch = 1.01f,
        prePauseMs = 60L,
        directionNote = "Same warm adult character with brighter energy and smile while keeping natural clarity (never squeaky)."
    ),
    PHONICS_NAME(
        displayName = "Phonics — Letter Name",
        speechRate = 0.86f,
        pitch = 1.00f,
        prePauseMs = 60L,
        directionNote = "Crisp, clear letter name spoken alone first."
    ),
    PHONICS_SOUND(
        displayName = "Phonics — Isolated Pure Sound",
        speechRate = 0.84f,
        pitch = 1.00f,
        prePauseMs = 60L,
        directionNote = "Pure isolated phoneme without trailing 'uh' schwa on continuants (/m/ = 'mmmm', /s/ = 'ssss')."
    ),
    PHONICS_BLEND(
        displayName = "Phonics — Segmented Blend",
        speechRate = 0.82f,
        pitch = 1.00f,
        prePauseMs = 70L,
        directionNote = "Distinct segmented phonemes with 250ms spacing before blending (e.g., '/s/ ... /ʌ/ ... /n/')."
    ),
    PHONICS_WORD(
        displayName = "Phonics — Whole Word",
        speechRate = 0.88f,
        pitch = 1.00f,
        prePauseMs = 50L,
        directionNote = "Natural whole-word pronunciation spoken clearly as a complete word (e.g., 'sun', 'seed')."
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
    val language: String = "en",
    val purpose: String = "instruction",
    val targetDurationMs: Int = 2200,
    val status: VoiceRecordingStatus = VoiceRecordingStatus.UNVERIFIED_AWAITING_HUMAN,
    @param:RawRes val bundledRawResId: Int? = null
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
     * Exact 6 English lines from `docs/gemini/garden-voice-scripts.csv`.
     * Used for both the Calm and Playful Sunny Voice Auditions in Parent Corner.
     */
    data class GardenAuditionLineSpec(
        val baseId: String,
        val purpose: String,
        val text: String,
        val targetDurationMs: Int
    )

    val gardenAuditionScriptLines: List<GardenAuditionLineSpec> = listOf(
        GardenAuditionLineSpec(
            baseId = "sunny_greeting",
            purpose = "greeting",
            text = "Hello! Let's help our garden grow.",
            targetDurationMs = 2400
        ),
        GardenAuditionLineSpec(
            baseId = "sunny_count_three",
            purpose = "instruction",
            text = "Put three seeds in the pot.",
            targetDurationMs = 2100
        ),
        GardenAuditionLineSpec(
            baseId = "sunny_letter_s",
            purpose = "letter_name",
            text = "Seed starts with the letter S.",
            targetDurationMs = 2200
        ),
        GardenAuditionLineSpec(
            baseId = "sunny_count_hint",
            purpose = "hint",
            text = "Touch one seed at a time as you count.",
            targetDurationMs = 2600
        ),
        GardenAuditionLineSpec(
            baseId = "sunny_story",
            purpose = "story",
            text = "Sunny planted the seeds. A little rain fell. Soon, a green shoot appeared.",
            targetDurationMs = 4800
        ),
        GardenAuditionLineSpec(
            baseId = "sunny_complete",
            purpose = "completion",
            text = "You planted three seeds. Your garden is growing!",
            targetDurationMs = 3100
        )
    )

    val gardenAuditionCalmClips: List<VoiceClipManifestEntry> = gardenAuditionScriptLines.map { spec ->
        VoiceClipManifestEntry(
            clipId = "${spec.baseId}_calm",
            expectedRawFileName = "voice_${spec.baseId}_calm.wav",
            category = VoicePerformanceCategory.AUDITION_CALM,
            displayCaption = spec.text,
            spokenScript = spec.text,
            performanceDirection = "Warm adult character, calm gentle pacing with room to think (48kHz 16-bit dry mono WAV).",
            language = "en",
            purpose = spec.purpose,
            targetDurationMs = spec.targetDurationMs
        )
    }

    val gardenAuditionPlayfulClips: List<VoiceClipManifestEntry> = gardenAuditionScriptLines.map { spec ->
        VoiceClipManifestEntry(
            clipId = "${spec.baseId}_playful",
            expectedRawFileName = "voice_${spec.baseId}_playful.wav",
            category = VoicePerformanceCategory.AUDITION_PLAYFUL,
            displayCaption = spec.text,
            spokenScript = spec.text,
            performanceDirection = "Same warm adult character, brighter playful energy and clarity without squeaky pitch (48kHz 16-bit dry mono WAV).",
            language = "en",
            purpose = spec.purpose,
            targetDurationMs = (spec.targetDurationMs * 0.92).toInt()
        )
    }

    /**
     * All Garden Lesson Stage, Level Variant, Language, Hint, and Retry Clips.
     * Ensures that e.g. `sunny_count_three` is ONLY used for the 3-seed task (2-3) and never for 5- or 8-seed tasks.
     */
    val gardenLessonClips: List<VoiceClipManifestEntry> = listOf(
        // Canonical CSV script entries
        VoiceClipManifestEntry(
            clipId = "sunny_greeting",
            expectedRawFileName = "voice_sunny_greeting.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Hello! Let's help our garden grow.",
            spokenScript = "Hello! Let's help our garden grow.",
            performanceDirection = "Warm greeting when entering Sunny's Garden.",
            language = "en",
            purpose = "greeting",
            targetDurationMs = 2400
        ),
        VoiceClipManifestEntry(
            clipId = "sunny_letter_s",
            expectedRawFileName = "voice_sunny_letter_s.wav",
            category = VoicePerformanceCategory.PHONICS_NAME,
            displayCaption = "Seed starts with the letter S.",
            spokenScript = "Seed starts with the letter S.",
            performanceDirection = "Clear letter name S (distinct from isolated /s/ phoneme sound).",
            language = "en",
            purpose = "letter_name",
            targetDurationMs = 2200
        ),
        VoiceClipManifestEntry(
            clipId = "sunny_count_three",
            expectedRawFileName = "voice_sunny_count_three.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Put 3 seeds in the pot.",
            spokenScript = "Put three seeds in the pot.",
            performanceDirection = "Stage 1 instruction exclusively for Math level 2-3 (3 seeds).",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "sunny_count_hint",
            expectedRawFileName = "voice_sunny_count_hint.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Touch one seed at a time as you count.",
            spokenScript = "Touch one seed at a time as you count.",
            performanceDirection = "Patient counting hint.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 2600
        ),
        VoiceClipManifestEntry(
            clipId = "sunny_story",
            expectedRawFileName = "voice_sunny_story.wav",
            category = VoicePerformanceCategory.STORY,
            displayCaption = "Sunny planted the seeds. A little rain fell. Soon, a green shoot appeared.",
            spokenScript = "Sunny planted the seeds. A little rain fell. Soon, a green shoot appeared.",
            performanceDirection = "Unhurried story narration.",
            language = "en",
            purpose = "story",
            targetDurationMs = 4800
        ),
        VoiceClipManifestEntry(
            clipId = "sunny_complete",
            expectedRawFileName = "voice_sunny_complete.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "You planted three seeds. Your garden is growing!",
            spokenScript = "You planted three seeds. Your garden is growing!",
            performanceDirection = "Warm completion line for 3-seed audition/journey.",
            language = "en",
            purpose = "completion",
            targetDurationMs = 3100
        ),

        // Stage 0 Reading Exercise Variants (Always English)
        VoiceClipManifestEntry(
            clipId = "garden_s0_read_2_3_find_s_en",
            expectedRawFileName = "voice_garden_s0_read_2_3_find_s_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Seed starts with S. Find S.",
            spokenScript = "Seed starts with S. Find S.",
            performanceDirection = "Reading 2-3 stop 0 prompt: letter name S + find S.",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2300
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s0_read_4_5_sun_s_en",
            expectedRawFileName = "voice_garden_s0_read_4_5_sun_s_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Build SUN. Find S.",
            spokenScript = "Build SUN. Find S.",
            performanceDirection = "Reading 4-5 stop 0 step 1: find first letter S of SUN.",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s0_read_4_5_sun_u_en",
            expectedRawFileName = "voice_garden_s0_read_4_5_sun_u_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Build SUN. Find U.",
            spokenScript = "Build SUN. Find U.",
            performanceDirection = "Reading 4-5 stop 0 step 2: find middle letter U of SUN.",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s0_read_4_5_sun_n_en",
            expectedRawFileName = "voice_garden_s0_read_4_5_sun_n_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Build SUN. Find N.",
            spokenScript = "Build SUN. Find N.",
            performanceDirection = "Reading 4-5 stop 0 step 3: find final letter N of SUN.",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s0_read_5_6_word_sun_en",
            expectedRawFileName = "voice_garden_s0_read_5_6_word_sun_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Plants need sunlight. Find SUN.",
            spokenScript = "Plants need sunlight. Find SUN.",
            performanceDirection = "Reading 5-6 stop 0 step 1: find whole word SUN.",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2500
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s0_read_5_6_word_seed_en",
            expectedRawFileName = "voice_garden_s0_read_5_6_word_seed_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "We plant a seed. Find SEED.",
            spokenScript = "We plant a seed. Find SEED.",
            performanceDirection = "Reading 5-6 stop 0 step 2: find whole word SEED.",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2500
        ),

        // Stage 1 Math Exercise Variants (Matched strictly to 3, 5, or 8 seeds; English & Telugu)
        VoiceClipManifestEntry(
            clipId = "garden_s1_math_2_3_count_3_te",
            expectedRawFileName = "voice_garden_s1_math_2_3_count_3_te.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "కుండలో 3 విత్తనాలు వేయి.",
            spokenScript = "కుండలో 3 విత్తనాలు వేయి.",
            performanceDirection = "Telugu Stage 1 instruction for Math 2-3 (3 seeds).",
            language = "te",
            purpose = "instruction",
            targetDurationMs = 2400
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s1_math_4_5_count_5_en",
            expectedRawFileName = "voice_garden_s1_math_4_5_count_5_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Put 5 seeds in the pot.",
            spokenScript = "Put five seeds in the pot.",
            performanceDirection = "English Stage 1 instruction exclusively for Math 4-5 (5 seeds).",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s1_math_4_5_count_5_te",
            expectedRawFileName = "voice_garden_s1_math_4_5_count_5_te.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "కుండలో 5 విత్తనాలు వేయి.",
            spokenScript = "కుండలో 5 విత్తనాలు వేయి.",
            performanceDirection = "Telugu Stage 1 instruction for Math 4-5 (5 seeds).",
            language = "te",
            purpose = "instruction",
            targetDurationMs = 2400
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s1_math_5_6_add_8_en",
            expectedRawFileName = "voice_garden_s1_math_5_6_add_8_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "Plant three seeds, then five more. How many altogether?",
            spokenScript = "Plant three seeds, then five more. How many altogether?",
            performanceDirection = "English Stage 1 instruction exclusively for Math 5-6 (3 + 5 = 8 seeds).",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 3400
        ),
        VoiceClipManifestEntry(
            clipId = "garden_s1_math_5_6_add_8_te",
            expectedRawFileName = "voice_garden_s1_math_5_6_add_8_te.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "మూడు విత్తనాలు, తరువాత మరో ఐదు విత్తనాలు నాటు. మొత్తం ఎన్ని?",
            spokenScript = "మూడు విత్తనాలు, తరువాత మరో ఐదు విత్తనాలు నాటు. మొత్తం ఎన్ని?",
            performanceDirection = "Telugu Stage 1 instruction for Math 5-6 (3 + 5 = 8 seeds).",
            language = "te",
            purpose = "instruction",
            targetDurationMs = 3600
        ),

        // Stage 2 Find Sunlight (English & Telugu)
        VoiceClipManifestEntry(
            clipId = "garden_s2_need_light_en",
            expectedRawFileName = "voice_garden_s2_need_light_en.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "What gives the plant light?",
            spokenScript = "What gives the plant light?",
            performanceDirection = "English Stage 2 sunlight question.",
            language = "en",
            purpose = "instruction",
            targetDurationMs = 2000
        ),
        VoiceClipManifestEntry(
            clipId = "garden_light_te",
            expectedRawFileName = "voice_garden_light_te.wav",
            category = VoicePerformanceCategory.INSTRUCTION,
            displayCaption = "మొక్కకు వెలుతురు ఏది ఇస్తుంది?",
            spokenScript = "మొక్కకు వెలుతురు ఏది ఇస్తుంది?",
            performanceDirection = "Telugu Stage 2 sunlight question from garden-voice-scripts.csv.",
            language = "te",
            purpose = "instruction",
            targetDurationMs = 2300
        ),

        // Stage 3 Story & Water (English & Telugu)
        VoiceClipManifestEntry(
            clipId = "garden_s3_story_water_en",
            expectedRawFileName = "voice_garden_s3_story_water_en.wav",
            category = VoicePerformanceCategory.STORY,
            displayCaption = "Sunny planted seeds. The soil is dry. What should Sunny give the seeds?",
            spokenScript = "Sunny planted seeds. The soil is dry. What should Sunny give the seeds?",
            performanceDirection = "English Stage 3 story and water question.",
            language = "en",
            purpose = "story",
            targetDurationMs = 4200
        ),
        VoiceClipManifestEntry(
            clipId = "garden_story_te",
            expectedRawFileName = "voice_garden_story_te.wav",
            category = VoicePerformanceCategory.STORY,
            displayCaption = "సన్నీ విత్తనాలు నాటింది. మట్టి పొడిగా ఉంది. విత్తనాలకు ఏమి ఇవ్వాలి?",
            spokenScript = "సన్నీ విత్తనాలు నాటింది. మట్టి పొడిగా ఉంది. విత్తనాలకు ఏమి ఇవ్వాలి?",
            performanceDirection = "Telugu Stage 3 story and water question from garden-voice-scripts.csv.",
            language = "te",
            purpose = "story",
            targetDurationMs = 4500
        ),

        // Stage 4 Completion (English & Telugu)
        VoiceClipManifestEntry(
            clipId = "garden_s4_complete_en",
            expectedRawFileName = "voice_garden_s4_complete_en.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "Your garden is growing! You helped Sunny read, count and care for a plant.",
            spokenScript = "Your garden is growing! You helped Sunny read, count and care for a plant.",
            performanceDirection = "English Stage 4 completion celebration.",
            language = "en",
            purpose = "completion",
            targetDurationMs = 4000
        ),
        VoiceClipManifestEntry(
            clipId = "garden_complete_te",
            expectedRawFileName = "voice_garden_complete_te.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "నీ తోట పెరుగుతోంది! మొక్కను పెంచడానికి సన్నీకి సహాయం చేశావు.",
            spokenScript = "నీ తోట పెరుగుతోంది! మొక్కను పెంచడానికి సన్నీకి సహాయం చేశావు.",
            performanceDirection = "Telugu Stage 4 completion celebration from garden-voice-scripts.csv.",
            language = "te",
            purpose = "completion",
            targetDurationMs = 4200
        ),

        // Stage-Specific Hints & Gentle Retry Clips
        VoiceClipManifestEntry(
            clipId = "garden_hint_s0_find_s_en",
            expectedRawFileName = "voice_garden_hint_s0_find_s_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Look for S.",
            spokenScript = "Look for S.",
            performanceDirection = "Calm hint for letter S.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 1500
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s0_find_u_en",
            expectedRawFileName = "voice_garden_hint_s0_find_u_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Look for U.",
            spokenScript = "Look for U.",
            performanceDirection = "Calm hint for letter U.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 1500
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s0_find_n_en",
            expectedRawFileName = "voice_garden_hint_s0_find_n_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Look for N.",
            spokenScript = "Look for N.",
            performanceDirection = "Calm hint for letter N.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 1500
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s0_find_sun_en",
            expectedRawFileName = "voice_garden_hint_s0_find_sun_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Look for SUN.",
            spokenScript = "Look for SUN.",
            performanceDirection = "Calm hint for word SUN.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 1600
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s0_find_seed_en",
            expectedRawFileName = "voice_garden_hint_s0_find_seed_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Look for SEED.",
            spokenScript = "Look for SEED.",
            performanceDirection = "Calm hint for word SEED.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 1600
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s1_count_3_en",
            expectedRawFileName = "voice_garden_hint_s1_count_3_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Touch one seed at a time. We need 3.",
            spokenScript = "Touch one seed at a time. We need three.",
            performanceDirection = "Calm counting hint for 3 seeds.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 2700
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s1_count_5_en",
            expectedRawFileName = "voice_garden_hint_s1_count_5_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Touch one seed at a time. We need 5.",
            spokenScript = "Touch one seed at a time. We need five.",
            performanceDirection = "Calm counting hint for 5 seeds.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 2700
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s1_count_8_en",
            expectedRawFileName = "voice_garden_hint_s1_count_8_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Touch one seed at a time. We need 8.",
            spokenScript = "Touch one seed at a time. We need eight.",
            performanceDirection = "Calm counting hint for 8 seeds.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 2700
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s2_light_en",
            expectedRawFileName = "voice_garden_hint_s2_light_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Look up in the daytime sky.",
            spokenScript = "Look up in the daytime sky.",
            performanceDirection = "Calm hint for sunlight.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "garden_hint_s3_water_en",
            expectedRawFileName = "voice_garden_hint_s3_water_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Dry soil needs water.",
            spokenScript = "Dry soil needs water.",
            performanceDirection = "Calm hint for watering dry soil.",
            language = "en",
            purpose = "hint",
            targetDurationMs = 2000
        ),
        VoiceClipManifestEntry(
            clipId = "garden_retry_gentle_en",
            expectedRawFileName = "voice_garden_retry_gentle_en.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Try another choice. Take your time.",
            spokenScript = "Try another choice. Take your time.",
            performanceDirection = "Patient, unhurried retry line.",
            language = "en",
            purpose = "retry",
            targetDurationMs = 2300
        )
    )

    /**
     * Distinct Phonics Clips (Isolated Phonemes, Short Vowels, Segmented Blends, Whole Words).
     * Separates letter names (`phonics_s_name`), isolated sounds (`phonics_s_isolated_sound`),
     * segmented blends (`phonics_blend_sun_segmented`), and whole words (`phonics_word_sun_whole`).
     */
    val distinctPhonicsClips: List<VoiceClipManifestEntry> = listOf(
        VoiceClipManifestEntry(
            clipId = "phonics_s_isolated_sound",
            expectedRawFileName = "voice_phonics_s_pure.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/s/ (ssss)",
            spokenScript = "ssss",
            performanceDirection = "Pure continuous unvoiced alveolar sibilant /s/ without trailing vowel.",
            language = "en",
            purpose = "phoneme_isolated",
            targetDurationMs = 950,
            status = VoiceRecordingStatus.BUNDLED_ACOUSTIC_CLIP,
            bundledRawResId = R.raw.voice_phonics_s_pure
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_m_isolated_sound",
            expectedRawFileName = "voice_phonics_m_pure.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/m/ (mmmm)",
            spokenScript = "mmmm",
            performanceDirection = "Pure continuous bilabial nasal /m/ without trailing vowel.",
            language = "en",
            purpose = "phoneme_isolated",
            targetDurationMs = 950,
            status = VoiceRecordingStatus.BUNDLED_ACOUSTIC_CLIP,
            bundledRawResId = R.raw.voice_phonics_m_pure
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_a_short_sound",
            expectedRawFileName = "voice_phonics_a_short.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/æ/ (short a as in apple)",
            spokenScript = "aah",
            performanceDirection = "Pure short front vowel /æ/ as in apple.",
            language = "en",
            purpose = "phoneme_short_vowel",
            targetDurationMs = 900
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_e_short_sound",
            expectedRawFileName = "voice_phonics_e_short.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/ɛ/ (short e as in elephant)",
            spokenScript = "eh",
            performanceDirection = "Pure short vowel /ɛ/ as in elephant.",
            language = "en",
            purpose = "phoneme_short_vowel",
            targetDurationMs = 900
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_i_short_sound",
            expectedRawFileName = "voice_phonics_i_short.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/ɪ/ (short i as in igloo)",
            spokenScript = "ih",
            performanceDirection = "Pure short vowel /ɪ/ as in igloo.",
            language = "en",
            purpose = "phoneme_short_vowel",
            targetDurationMs = 900
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_o_short_sound",
            expectedRawFileName = "voice_phonics_o_short.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/ɒ/ (short o as in octopus)",
            spokenScript = "aw",
            performanceDirection = "Pure short vowel /ɒ/ as in octopus.",
            language = "en",
            purpose = "phoneme_short_vowel",
            targetDurationMs = 900
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_u_short_sound",
            expectedRawFileName = "voice_phonics_u_short.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/ʌ/ (short u as in sun)",
            spokenScript = "uh",
            performanceDirection = "Pure short central vowel /ʌ/ as in sun/umbrella.",
            language = "en",
            purpose = "phoneme_short_vowel",
            targetDurationMs = 900
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_n_isolated_sound",
            expectedRawFileName = "voice_phonics_n_pure.wav",
            category = VoicePerformanceCategory.PHONICS_SOUND,
            displayCaption = "/n/ (nnnn)",
            spokenScript = "nnnn",
            performanceDirection = "Pure continuous alveolar nasal /n/ without trailing schwa.",
            language = "en",
            purpose = "phoneme_isolated",
            targetDurationMs = 950
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_blend_sun_segmented",
            expectedRawFileName = "voice_phonics_blend_sun_segmented.wav",
            category = VoicePerformanceCategory.PHONICS_BLEND,
            displayCaption = "S · U · N (/s/ … /ʌ/ … /n/)",
            spokenScript = "ssss... uh... nnnn",
            performanceDirection = "Segmented phoneme blend for SUN (/s/ - /ʌ/ - /n/) with clear pauses.",
            language = "en",
            purpose = "phonics_segmented_blend",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_word_sun_whole",
            expectedRawFileName = "voice_phonics_word_sun_whole.wav",
            category = VoicePerformanceCategory.PHONICS_WORD,
            displayCaption = "sun",
            spokenScript = "sun",
            performanceDirection = "Whole-word pronunciation of 'sun'.",
            language = "en",
            purpose = "phonics_whole_word",
            targetDurationMs = 1100
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_blend_seed_segmented",
            expectedRawFileName = "voice_phonics_blend_seed_segmented.wav",
            category = VoicePerformanceCategory.PHONICS_BLEND,
            displayCaption = "S · EE · D (/s/ … /iː/ … /d/)",
            spokenScript = "ssss... eeee... d",
            performanceDirection = "Segmented phoneme blend for SEED (/s/ - /iː/ - /d/).",
            language = "en",
            purpose = "phonics_segmented_blend",
            targetDurationMs = 2100
        ),
        VoiceClipManifestEntry(
            clipId = "phonics_word_seed_whole",
            expectedRawFileName = "voice_phonics_word_seed_whole.wav",
            category = VoicePerformanceCategory.PHONICS_WORD,
            displayCaption = "seed",
            spokenScript = "seed",
            performanceDirection = "Whole-word pronunciation of 'seed'.",
            language = "en",
            purpose = "phonics_whole_word",
            targetDurationMs = 1100
        )
    )

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
            performanceDirection = "Sincere, warm nod of approval.",
            purpose = "completion"
        ),
        VoiceClipManifestEntry(
            clipId = "success_nice_looking",
            expectedRawFileName = "voice_success_nice_looking.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "Nice looking!",
            spokenScript = "Nice looking.",
            performanceDirection = "Gentle, encouraging praise focused on the child's observation.",
            purpose = "completion"
        ),
        VoiceClipManifestEntry(
            clipId = "success_right_there",
            expectedRawFileName = "voice_success_right_there.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "Right there! You got it.",
            spokenScript = "Right there... You got it.",
            performanceDirection = "Warm, conversational confirmation.",
            purpose = "completion"
        ),
        VoiceClipManifestEntry(
            clipId = "success_thats_the_one",
            expectedRawFileName = "voice_success_thats_the_one.wav",
            category = VoicePerformanceCategory.SUCCESS,
            displayCaption = "That's the one!",
            spokenScript = "That's the one.",
            performanceDirection = "Natural smile in delivery, comfortable adult pitch.",
            purpose = "completion"
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
            performanceDirection = "Calm, unhurried, reassuring.",
            purpose = "retry"
        ),
        VoiceClipManifestEntry(
            clipId = "retry_look_closely",
            expectedRawFileName = "voice_retry_look_closely.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Take your time. Let's look together.",
            spokenScript = "Take your time... Let's look together.",
            performanceDirection = "Warm teacher sitting beside the child.",
            purpose = "retry"
        ),
        VoiceClipManifestEntry(
            clipId = "retry_round_shape",
            expectedRawFileName = "voice_retry_round_shape.wav",
            category = VoicePerformanceCategory.RETRY,
            displayCaption = "Let’s try another one. Look for the matching shape.",
            spokenScript = "Let's try another one... Look for the matching shape.",
            performanceDirection = "Gentle guidance toward the visual clue.",
            purpose = "retry"
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
     * Full Line-to-File Manifest combining Garden Auditions, Garden Lesson Variants, Distinct Phonics,
     * Core Feedback, and Activity Prompts.
     */
    val allManifestEntries: List<VoiceClipManifestEntry> by lazy {
        val list = mutableListOf<VoiceClipManifestEntry>()
        list.addAll(gardenAuditionCalmClips)
        list.addAll(gardenAuditionPlayfulClips)
        list.addAll(gardenLessonClips)
        list.addAll(distinctPhonicsClips)
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
                    performanceDirection = "Warm, clear letter name.",
                    purpose = "letter_name",
                    targetDurationMs = 1100
                )
            )
            list.add(
                VoiceClipManifestEntry(
                    clipId = "phonics_${entry.letter.lowercaseChar()}_sound",
                    expectedRawFileName = entry.letterSoundFile,
                    category = VoicePerformanceCategory.PHONICS_SOUND,
                    displayCaption = "${entry.letter} says ${entry.ipaSymbol} • ${entry.exampleWord}",
                    spokenScript = "${entry.pureSoundScript}.",
                    performanceDirection = entry.teacherNote,
                    purpose = "phoneme_with_example",
                    targetDurationMs = 1500
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
        "Sunny's Garden & Unrecorded Catalog Lines (Fallback Mode)" to "Until human studio 48kHz/16-bit mono WAV files listed in VoiceAssetManifest are reviewed and placed into app/src/main/res/raw/, unrecorded lines use the device's offline teacher voice (or English fallback when offline Telugu is not installed) with accurate visible captions."
    )
}
