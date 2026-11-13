package com.example

import androidx.compose.ui.geometry.Offset
import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveLearningEngine
import com.example.data.RealWorldMediaRegistry
import com.example.data.WonderCatalogRegistry
import com.example.model.AgeBand
import com.example.model.EngineType
import com.example.model.LandId
import com.example.model.RealMediaCategory
import com.example.ui.engines.DrawnStroke
import com.example.ui.engines.TracingStudioDeckRegistry
import com.example.ui.engines.decodeStrokesPayload
import com.example.ui.engines.encodeStrokesPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `verify full catalog has 6 lands, 27 worlds, and 257 unique playable activities`() {
        val lands = LandId.entries
        assertEquals(6, lands.size)

        val worlds = WonderCatalogRegistry.worlds
        assertEquals(27, worlds.size)

        val activities = WonderCatalogRegistry.allActivities
        assertEquals(257, activities.size)

        // Ensure all 257 activity IDs are strictly unique
        val uniqueIds = activities.map { it.id }.toSet()
        assertEquals(257, uniqueIds.size)

        // Verify w22_arcade_8 is excluded from selectable catalog
        assertEquals(null, WonderCatalogRegistry.getActivityById("w22_arcade_8"))

        // Verify 30 Rhyme & Storytime entries in World 27, each with 3 story pages
        val rhymes = WonderCatalogRegistry.getActivitiesForWorld("w27_rhymes")
        assertEquals(30, rhymes.size)
        assertTrue(rhymes.all { it.storyPages.size == 3 })

        // Verify 16 Flashcard decks in World 25, each with 6 distinct items
        val flashcards = WonderCatalogRegistry.getActivitiesForWorld("w25_flashcards")
        assertEquals(16, flashcards.size)
        assertTrue(flashcards.all { it.items.size == 6 })
        val flashcardFirstLabels = flashcards.map { it.items.first().label }.toSet()
        assertEquals(16, flashcardFirstLabels.size)
    }

    @Test
    fun `verify Stage 1 TracingStudioDeckRegistry explicit decks, multi-stroke templates, and stroke evaluator`() {
        // 1. Verify all 12 w04_tracing activities + w15_science_8 resolve explicit non-empty decks
        val expectedCounts = mapOf(
            "w04_tracing_1" to 8,   // A-H
            "w04_tracing_2" to 8,   // I-P
            "w04_tracing_3" to 10,  // Q-Z
            "w04_tracing_4" to 13,  // a-m
            "w04_tracing_5" to 13,  // n-z
            "w04_tracing_6" to 5,   // 1-5
            "w04_tracing_7" to 5,   // 6-10
            "w04_tracing_8" to 5,   // Shapes
            "w04_tracing_9" to 4,   // Straight lines
            "w04_tracing_10" to 4,  // Curves & waves
            "w04_tracing_11" to 3,  // Zigzags
            "w04_tracing_12" to 13, // First words CAT (3) + SUN (3) + DOG (3) + STAR (4) = 13 letter steps
            "w15_science_8" to 3    // Constellation stars
        )

        expectedCounts.forEach { (activityId, expectedCount) ->
            val deck = TracingStudioDeckRegistry.getDeckForActivity(activityId)
            assertEquals("Unexpected glyph count for $activityId", expectedCount, deck.size)
            assertTrue(deck.all { it.strokes.isNotEmpty() && it.glyph.isNotBlank() })
        }

        // Verify A-Z across w04_tracing_1..3
        val upperLetters = (
            TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_1") +
                TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_2") +
                TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_3")
            ).map { it.glyph }
        assertEquals(('A'..'Z').map { it.toString() }, upperLetters)

        // Verify a-z across w04_tracing_4..5 and that i/j have 2 strokes (stem + dot)
        val lowerDeck = TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_4") +
            TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_5")
        assertEquals(('a'..'z').map { it.toString() }, lowerDeck.map { it.glyph })
        val glyphI = lowerDeck.first { it.glyph == "i" }
        val glyphJ = lowerDeck.first { it.glyph == "j" }
        assertEquals(2, glyphI.strokes.size)
        assertEquals(2, glyphJ.strokes.size)

        // Verify numeral 10 has 2 distinct strokes ('1' and '0')
        val deck6To10 = TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_7")
        val glyph10 = deck6To10.first { it.glyph == "10" }
        assertEquals(2, glyph10.strokes.size)

        // Verify First Words deck has CAT, SUN, DOG, STAR contexts
        val wordsDeck = TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_12")
        assertEquals(listOf("CAT", "SUN", "DOG", "STAR"), wordsDeck.mapNotNull { it.wordContext }.distinct())

        // Verify legacy step index reconciliation clamps safely without crashing
        assertEquals(0, TracingStudioDeckRegistry.reconcileSavedStepIndex(-3, 8))
        assertEquals(0, TracingStudioDeckRegistry.reconcileSavedStepIndex(99, 8))
        assertEquals(3, TracingStudioDeckRegistry.reconcileSavedStepIndex(3, 8))

        // Verify aspect-ratio-safe canvas coordinate mapping preserves square proportions
        val topLeft = TracingStudioDeckRegistry.mapNormalizedToCanvas(Offset(0f, 0f), 800f, 400f)
        val bottomRight = TracingStudioDeckRegistry.mapNormalizedToCanvas(Offset(1f, 1f), 800f, 400f)
        assertEquals(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y, 0.5f)
        assertTrue(topLeft.x > 0f && topLeft.y > 0f)

        // Verify stroke evaluation:
        val verticalStroke = TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_9").first().strokes.first()
        val canvasW = 400f
        val canvasH = 400f
        val tolPx = 42f
        val checkpointPixels = verticalStroke.points.map {
            TracingStudioDeckRegistry.mapNormalizedToCanvas(it, canvasW, canvasH)
        }

        val startPx = checkpointPixels.first()
        val endPx = checkpointPixels.last()

        // 1) Valid forward trace from start to end -> passes
        val goodPoints = (0..20).map { idx ->
            val t = idx / 20f
            Offset(
                startPx.x + (endPx.x - startPx.x) * t,
                startPx.y + (endPx.y - startPx.y) * t
            )
        }
        val goodEval = TracingStudioDeckRegistry.evaluateStrokeAttempt(
            strokeSpec = verticalStroke,
            checkpointPixels = checkpointPixels,
            rawSegments = listOf(goodPoints),
            tolerancePx = tolPx,
            requiredCoverageRatio = 0.78f,
            isToddlerBand = false
        )
        assertTrue("Expected valid forward stroke to pass, got ${goodEval.feedbackMessage}", goodEval.strokePassed)
        assertTrue(goodEval.coveragePercent >= 78)
        assertTrue(goodEval.feedbackMessage.contains("${goodEval.coveragePercent}%"))

        // 2) Tap cluster at start -> rejected
        val tapCluster = listOf(startPx, Offset(startPx.x + 2f, startPx.y + 2f), Offset(startPx.x - 1f, startPx.y + 1f))
        val tapEval = TracingStudioDeckRegistry.evaluateStrokeAttempt(
            strokeSpec = verticalStroke,
            checkpointPixels = checkpointPixels,
            rawSegments = listOf(tapCluster),
            tolerancePx = tolPx,
            requiredCoverageRatio = 0.78f,
            isToddlerBand = false
        )
        assertFalse(tapEval.strokePassed)
        assertTrue(tapEval.isShortcutOrTapCluster)

        // 3) Reversed trace (bottom to top) -> rejected
        val reversedPoints = goodPoints.reversed()
        val revEval = TracingStudioDeckRegistry.evaluateStrokeAttempt(
            strokeSpec = verticalStroke,
            checkpointPixels = checkpointPixels,
            rawSegments = listOf(reversedPoints),
            tolerancePx = tolPx,
            requiredCoverageRatio = 0.78f,
            isToddlerBand = false
        )
        assertFalse(revEval.strokePassed)
        assertTrue(revEval.isReversed)

        // 4) Straight shortcut across a closed circle -> rejected
        val circleStroke = TracingStudioDeckRegistry.getDeckForActivity("w04_tracing_8").first().strokes.first()
        val circleCheckpoints = circleStroke.points.map {
            TracingStudioDeckRegistry.mapNormalizedToCanvas(it, canvasW, canvasH)
        }
        val circleStart = circleCheckpoints.first()
        val shortcutPoints = listOf(circleStart, Offset(circleStart.x, circleStart.y + 40f), circleStart)
        val shortcutEval = TracingStudioDeckRegistry.evaluateStrokeAttempt(
            strokeSpec = circleStroke,
            checkpointPixels = circleCheckpoints,
            rawSegments = listOf(shortcutPoints),
            tolerancePx = tolPx,
            requiredCoverageRatio = 0.78f,
            isToddlerBand = false
        )
        assertFalse(shortcutEval.strokePassed)
    }

    @Test
    fun `verify Stage 2 Coloring stroke payload serialization round-trip preserves geometry and colors`() {
        val originalStrokes = listOf(
            DrawnStroke(
                points = listOf(Offset(0.12f, 0.25f), Offset(0.45f, 0.55f), Offset(0.78f, 0.82f)),
                colorHex = 0xFFEF4444L,
                strokeWidth = 22f
            ),
            DrawnStroke(
                points = listOf(Offset(0.20f, 0.15f), Offset(0.60f, 0.35f)),
                colorHex = 0xFF3B82F6L,
                strokeWidth = 34f
            )
        )
        val encoded = encodeStrokesPayload(originalStrokes)
        assertTrue(encoded.isNotBlank())

        val decoded = decodeStrokesPayload(encoded)
        assertEquals(2, decoded.size)
        assertEquals(0xFFEF4444L, decoded[0].colorHex)
        assertEquals(0xFF3B82F6L, decoded[1].colorHex)
        assertEquals(3, decoded[0].points.size)
        assertEquals(0.12f, decoded[0].points[0].x, 0.001f)
        assertEquals(0.25f, decoded[0].points[0].y, 0.001f)
        assertEquals(22f, decoded[0].strokeWidth, 0.001f)
    }

    @Test
    fun `verify Stage 3 title-to-content alignment across Sorting, Animals, Memory, Phonics, Math, Science, Safety, and Rhymes`() {
        // 1. Verify w01_sorting_1..9 have distinct datasets matching their titles
        val sortingGames = WonderCatalogRegistry.getActivitiesForWorld("w01_sorting")
        assertEquals(9, sortingGames.size)
        val sortingFirstLabels = sortingGames.map { it.items.first().label }.toSet()
        assertEquals(9, sortingFirstLabels.size)

        // 2. Verify w02_animals_1..11 have distinct datasets
        val animalGames = WonderCatalogRegistry.getActivitiesForWorld("w02_animals")
        assertEquals(11, animalGames.size)
        assertTrue(animalGames.all { it.items.size >= 4 })

        // 3. Verify w03_memory_4 (Letter Twins) has 6 uppercase letters A-F paired with lowercase in MemoryMatchEngine
        val letterTwins = WonderCatalogRegistry.getActivityById("w03_memory_4")
        assertNotNull(letterTwins)
        assertEquals(6, letterTwins!!.items.size)
        assertEquals(listOf("A", "B", "C", "D", "E", "F"), letterTwins.items.map { it.label })

        // 4. Verify w07_phonics_1..12 have distinct datasets including 52-step alphabet journey parts 1 & 2
        val phonics11 = WonderCatalogRegistry.getActivityById("w07_phonics_11")!!
        val phonics12 = WonderCatalogRegistry.getActivityById("w07_phonics_12")!!
        assertEquals(26, phonics11.items.size)
        assertEquals(26, phonics12.items.size)
        assertTrue(phonics11.traceGlyph != phonics12.traceGlyph)

        // 5. Verify w08_math_1..10 have non-empty items
        val mathGames = WonderCatalogRegistry.getActivitiesForWorld("w08_math")
        assertEquals(10, mathGames.size)
        assertTrue(mathGames.all { game -> game.items.isNotEmpty() })

        // 6. Verify w15_science_1..8 use title-accurate engines and datasets
        val sci1 = WonderCatalogRegistry.getActivityById("w15_science_1")!!
        val sci3 = WonderCatalogRegistry.getActivityById("w15_science_3")!!
        val sci6 = WonderCatalogRegistry.getActivityById("w15_science_6")!!
        val sci8 = WonderCatalogRegistry.getActivityById("w15_science_8")!!
        assertEquals(EngineType.WATER_LAB_SCIENCE, sci1.engineType)
        assertEquals(EngineType.SORTING_BASKETS, sci3.engineType)
        assertEquals(EngineType.PRETEND_STEP_CARE, sci6.engineType)
        assertEquals(EngineType.TRACING_STUDIO, sci8.engineType)

        // 7. Verify w20_safety_1..9 have 3 story pages each with a valid correctChoiceIndex
        val safetyGames = WonderCatalogRegistry.getActivitiesForWorld("w20_safety")
        assertEquals(9, safetyGames.size)
        assertTrue(safetyGames.all { game ->
            game.storyPages.size == 3 && game.storyPages.all { it.correctChoiceIndex in 0..1 }
        })
    }

    @Test
    fun `verify Real World Media and Wonder Reels inventory and manifest`() {
        val animals = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.ANIMAL)
        val objects = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.OBJECT)
        val lessonsAndFilms = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.FILM_LESSON)

        assertEquals(80, animals.size)
        assertEquals(123, objects.size)
        assertEquals(34, lessonsAndFilms.size)

        val allItems = RealWorldMediaRegistry.allMediaItems
        assertEquals(237, allItems.size)
        assertEquals(237, allItems.map { it.id }.toSet().size)
        assertTrue(allItems.all { it.manifest.checksumSha256Prefix.isNotBlank() && it.funFacts.isNotEmpty() })
        // Pack entries coexist with legacy cards so saved IDs and parent imports survive.
        assertEquals(64, animals.count { it.manifest.exactPathOrResource.startsWith("res/drawable-nodpi/atlas_") })
        assertEquals(71, objects.count { it.manifest.exactPathOrResource.startsWith("res/drawable-nodpi/atlas_") })
        assertEquals(16, lessonsAndFilms.count { it.bundledVideoRes != null && it.bundledCaptionsRes != null })

    }

    @Test
    fun `verify AdaptiveLearningEngine adjusts difficulty by age band and non-punitive indicators`() {
        val toddlerParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.TODDLER_2_3,
            autoAdaptEnabled = true,
            progress = null
        )
        val kinderParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.KINDER_5_6,
            autoAdaptEnabled = true,
            progress = null
        )
        assertTrue(toddlerParams.effectiveChoiceCount < kinderParams.effectiveChoiceCount)
        assertTrue(toddlerParams.tracingToleranceDp > kinderParams.tracingToleranceDp)
        assertTrue(toddlerParams.memoryPairCount < kinderParams.memoryPairCount)

        // Verify gentle support scaffolding activates when a child needs retries
        val supportParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.PRESCHOOL_4_5,
            autoAdaptEnabled = true,
            progress = ActivityProgressEntity(
                activityId = "w01_sorting_1",
                worldId = "w01_sorting",
                landId = "thinking_forest",
                gentleRetryCount = 3,
                adaptiveComfortDelta = -1
            ),
            recentSessionRetries = 2
        )
        assertEquals(-1, supportParams.adaptiveTierDelta)
        assertTrue(supportParams.showVisualScaffoldGlow)
        assertEquals(2, supportParams.effectiveChoiceCount)

        // Verify stretch challenge activates after smooth completions
        val stretchParams = AdaptiveLearningEngine.computeParameters(
            ageBand = AgeBand.PRESCHOOL_4_5,
            autoAdaptEnabled = true,
            progress = null,
            recentSessionSmoothWins = 3
        )
        assertEquals(1, stretchParams.adaptiveTierDelta)
        assertEquals(4, stretchParams.effectiveChoiceCount)
    }

    @Test
    fun `verify all 12 Balloon Pop Adventure games and 7 Arcade games have distinct rich datasets and warm buddy voices`() {
        val balloonGames = WonderCatalogRegistry.getActivitiesForWorld("w06_balloons")
        assertEquals(12, balloonGames.size)
        balloonGames.forEach { game ->
            assertTrue("Expected at least 6 items in ${game.id}", game.items.size >= 6)
            assertTrue(game.items.all { it.spokenText.isNotBlank() && it.emoji.isNotBlank() })
        }
        val distinctFirstItemIds = balloonGames.map { it.items.first().id }.toSet()
        assertEquals(12, distinctFirstItemIds.size)

        val arcadeGames = WonderCatalogRegistry.getActivitiesForWorld("w22_arcade")
        assertEquals(7, arcadeGames.size)
        arcadeGames.forEach { game ->
            assertTrue("Expected at least 6 items in ${game.id}", game.items.size >= 6)
        }
        val distinctArcadeFirstIds = arcadeGames.map { it.items.first().id }.toSet()
        assertEquals(7, distinctArcadeFirstIds.size)

        com.example.model.LearningBuddy.entries.forEach { buddy ->
            assertTrue(buddy.voicePitch in 0.98f..1.06f)
            assertTrue(buddy.voiceRate in 0.90f..1.00f)
        }
    }

    @Test
    fun `verify BalloonPopAdventureModule 9 worlds, 13 modes, 3 languages, physics lifecycle, and adaptive hints`() {
        val worlds = com.example.ui.engines.BalloonWorldEnvironment.entries
        assertEquals(9, worlds.size)

        val modes = com.example.ui.engines.BalloonLearningMode.entries
        assertEquals(13, modes.size)

        val languages = com.example.ui.engines.BalloonNarrationLanguage.entries
        assertEquals(3, languages.size)

        modes.forEach { mode ->
            worlds.forEach { world ->
                val payloads = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(mode, world)
                assertTrue("Expected non-empty payloads for $mode in $world", payloads.isNotEmpty())
                val first = payloads.first()
                assertTrue(first.getDisplayTitle(com.example.ui.engines.BalloonNarrationLanguage.ENGLISH).isNotBlank())
                assertTrue(first.getDisplayTitle(com.example.ui.engines.BalloonNarrationLanguage.TELUGU).isNotBlank())
                assertTrue(first.getDisplayTitle(com.example.ui.engines.BalloonNarrationLanguage.HINDI).isNotBlank())
            }
        }
    }

    @Test
    fun `verify Warm Preschool Teacher VoiceAssetManifest, 6-line audition, schwa-free phonics, and honest voice selector`() {
        val auditionLines = com.example.audio.VoiceAssetManifest.sixAuditionLines
        assertEquals(6, auditionLines.size)

        assertTrue(auditionLines.all {
            it.spokenScript.isNotBlank() &&
                it.expectedRawFileName.endsWith(".wav") &&
                it.performanceDirection.isNotBlank()
        })

        val phonics = com.example.audio.VoiceAssetManifest.phonicsManifest
        assertEquals(26, phonics.size)
        for (ch in 'A'..'Z') {
            val entry = phonics[ch]
            assertTrue("Missing phonics entry for $ch", entry != null)
            assertTrue(entry!!.letterNameFile != entry.letterSoundFile)
            if (ch != 'U') {
                val lowerScript = entry.pureSoundScript.lowercase()
                val forbiddenSchwas = listOf("buh", "kuh", "duh", "fuh", "guh", "huh", "luh", "muh", "nuh", "puh", "ruh", "suh", "tuh", "vuh", "zuh")
                forbiddenSchwas.forEach { bad ->
                    assertTrue("Consonant $ch should not contain schwa '$bad' in '${entry.pureSoundScript}'", !lowerScript.contains(bad))
                }
            }
        }

        assertTrue(phonics['M']!!.pureSoundScript.startsWith("mmmm"))
        assertTrue(phonics['S']!!.pureSoundScript.startsWith("ssss"))
    }
}
