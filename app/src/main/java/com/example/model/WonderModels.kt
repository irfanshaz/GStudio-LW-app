package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class AgeBand(
    val id: String,
    val label: String,
    val subtitle: String,
    val choiceCount: Int,
    val tracingToleranceDp: Float,
    val memoryPairs: Int
) {
    TODDLER_2_3(
        id = "2-3",
        label = "Ages 2–3",
        subtitle = "Gentle choices, large targets & guided play",
        choiceCount = 2,
        tracingToleranceDp = 56f,
        memoryPairs = 3
    ),
    PRESCHOOL_4_5(
        id = "4-5",
        label = "Ages 4–5",
        subtitle = "Phonics, counting, puzzles & discovery",
        choiceCount = 3,
        tracingToleranceDp = 42f,
        memoryPairs = 4
    ),
    KINDER_5_6(
        id = "5-6",
        label = "Ages 5–6",
        subtitle = "Word starts, addition, coding routes & tracing precision",
        choiceCount = 4,
        tracingToleranceDp = 30f,
        memoryPairs = 6
    );

    companion object {
        fun fromId(id: String): AgeBand = entries.find { it.id == id } ?: PRESCHOOL_4_5
    }
}

enum class LearningBuddy(
    val id: String,
    val displayName: String,
    val species: String,
    val emoji: String,
    val greeting: String,
    val encouragement: String,
    val voicePitch: Float,
    val voiceRate: Float,
    val accentColor: Color
) {
    SUNNY_CUB(
        id = "sunny_cub",
        displayName = "Sunny the Cub",
        species = "Golden Explorer Cub",
        emoji = "🦁",
        greeting = "Let's play together! Pick a world to explore.",
        encouragement = "You found it! Nice looking.",
        voicePitch = 1.00f,
        voiceRate = 0.90f,
        accentColor = Color(0xFFF59E0B)
    ),
    PIP_OWL(
        id = "pip_owl",
        displayName = "Pip the Owl",
        species = "Starlight Little Owl",
        emoji = "🦉",
        greeting = "Hello! Let's look at stories and puzzles together.",
        encouragement = "Right there! You figured it out.",
        voicePitch = 1.00f,
        voiceRate = 0.90f,
        accentColor = Color(0xFF6366F1)
    ),
    BRAMBLE_BUNNY(
        id = "bramble_bunny",
        displayName = "Bramble the Bunny",
        species = "Meadow Hop Bunny",
        emoji = "🐰",
        greeting = "Let's paint, make music, and sort colors together.",
        encouragement = "That's the one! Great looking.",
        voicePitch = 1.00f,
        voiceRate = 0.90f,
        accentColor = Color(0xFF10B981)
    ),
    CORAL_OTTER(
        id = "coral_otter",
        displayName = "Coral the Otter",
        species = "Splashy River Otter",
        emoji = "🦦",
        greeting = "Let's explore water science and animals together.",
        encouragement = "You found it! Wonderful exploring.",
        voicePitch = 1.00f,
        voiceRate = 0.90f,
        accentColor = Color(0xFF06B6D4)
    );

    companion object {
        fun fromId(id: String): LearningBuddy = entries.find { it.id == id } ?: SUNNY_CUB
    }
}

enum class LandId(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val accentColor: Color
) {
    LEARNING_LAND(
        id = "learning_land",
        title = "Learning Land",
        subtitle = "Letters, Numbers, Tracing & Phonics",
        emoji = "🔤",
        accentColor = Color(0xFFFF6B6B)
    ),
    THINKING_FOREST(
        id = "thinking_forest",
        title = "Thinking Forest",
        subtitle = "Sorting, Memory, Puzzles & Coding",
        emoji = "🌲",
        accentColor = Color(0xFF10B981)
    ),
    DISCOVERY_ISLAND(
        id = "discovery_island",
        title = "Discovery Island",
        subtitle = "Animals, Plants, Water Lab & Space",
        emoji = "🏝️",
        accentColor = Color(0xFF06B6D4)
    ),
    CREATIVE_GARDEN(
        id = "creative_garden",
        title = "Creative Garden",
        subtitle = "Coloring, Music, Building & Rhymes",
        emoji = "🎨",
        accentColor = Color(0xFF8B5CF6)
    ),
    LITTLE_LIFE_TOWN(
        id = "little_life_town",
        title = "Little Life Town",
        subtitle = "Helpers, Pretend Play, Safety & Chef",
        emoji = "🏘️",
        accentColor = Color(0xFFF97316)
    ),
    ADVENTURE_FUNFAIR(
        id = "adventure_funfair",
        title = "Adventure Funfair",
        subtitle = "Balloon Pop, Flashcards, Family & Arcade",
        emoji = "🎡",
        accentColor = Color(0xFFEAB308)
    )
}

enum class EngineType {
    SORTING_BASKETS,
    ANIMAL_SAFARI_SOUNDS,
    MEMORY_MATCH,
    TRACING_STUDIO,
    COLORING_CANVAS,
    BALLOON_POP,
    PHONICS_WORD_QUIZ,
    NUMBERS_MATH_COUNTER,
    STORYBOOK_READER,
    MUSIC_INSTRUMENTS,
    CODING_ARROW_MAZE,
    WATER_LAB_SCIENCE,
    PRETEND_STEP_CARE,
    FLASHCARD_EXPLORER
}

data class WorldSpec(
    val id: String,
    val number: Int,
    val landId: LandId,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val defaultEngine: EngineType,
    val accentColor: Color
)

data class GameItemOption(
    val id: String,
    val label: String,
    val emoji: String,
    val categoryOrTarget: String,
    val spokenText: String,
    val phoneticCue: String = "",
    val sizeScale: Float = 1.0f,
    val primaryColorHex: Long = 0xFF3B82F6,
    val sinksInWater: Boolean = false,
    val explanation: String = "",
    @DrawableRes val drawableRes: Int? = null
)

data class StoryPageSpec(
    val pageNumber: Int,
    val title: String,
    val emojiScene: String,
    val narrationText: String,
    val interactivePrompt: String,
    val choiceOptions: List<String> = emptyList(),
    val correctChoiceIndex: Int = 0,
    val parentTip: String = ""
)

data class ActivityConfig(
    val id: String,
    val worldId: String,
    val landId: LandId,
    val indexInWorld: Int,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val engineType: EngineType,
    val instructionText: String,
    val showMeDemoHint: String,
    val items: List<GameItemOption> = emptyList(),
    val traceGlyph: String = "A",
    val traceWordHint: String = "Apple",
    val storyPages: List<StoryPageSpec> = emptyList(),
    val isEndlessMilestone: Boolean = true,
    val featuredInToyBox: Boolean = false
)

enum class RealMediaCategory(val id: String, val label: String, val emoji: String) {
    ALL("all", "All Wonders", "🌟"),
    ANIMAL("animal", "Animals", "🦁"),
    OBJECT("object", "Objects", "🚂"),
    FILM_LESSON("film_lesson", "Films & Lessons", "🎬")
}

enum class ReelMotionStyle {
    SAVANNA_PROWL,
    OCEAN_SWIM,
    SKY_FLIGHT,
    MEADOW_BOUNCE,
    VEHICLE_DRIVE,
    SOUND_WAVE_PULSE,
    STORY_LESSON_SCENE
}

enum class AssetVerificationStatus(val label: String) {
    BUNDLED_VERIFIED("Bundled & Verified"),
    AI_GENERATED_REVIEWED("AI Art + Motion Verified"),
    PROCEDURAL_SYNTH_FALLBACK("Procedural Audio + Visual Verified"),
    USER_IMPORTED_FLOW("User Google Flow Media")
}

data class MediaManifestEntry(
    val assetId: String,
    val exactPathOrResource: String,
    val fileType: String,
    val durationSeconds: Int,
    val checksumSha256Prefix: String,
    val sourceAndTool: String,
    val licenseAttribution: String,
    val status: AssetVerificationStatus,
    val fallbackBehavior: String
)

data class RealWorldMediaItem(
    val id: String,
    val category: RealMediaCategory,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val habitatOrGroup: String,
    val funFacts: List<String>,
    val soundDescription: String,
    val soundFreqHz: Float,
    val motionStyle: ReelMotionStyle,
    val primaryColor: Color,
    val secondaryColor: Color,
    @DrawableRes val bundledDrawableRes: Int? = null,
    val bundledVideoAssetPath: String? = null,
    @androidx.annotation.RawRes val bundledVideoRes: Int? = null,
    @androidx.annotation.RawRes val bundledCaptionsRes: Int? = null,
    val subtitles: List<String> = emptyList(),
    val manifest: MediaManifestEntry
)
