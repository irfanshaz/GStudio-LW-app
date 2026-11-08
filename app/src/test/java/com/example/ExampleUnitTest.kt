package com.example

import com.example.data.ActivityProgressEntity
import com.example.data.AdaptiveLearningEngine
import com.example.data.RealWorldMediaRegistry
import com.example.data.WonderCatalogRegistry
import com.example.model.AgeBand
import com.example.model.LandId
import com.example.model.RealMediaCategory
import org.junit.Assert.assertEquals
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

        // Verify 30 Rhyme & Storytime entries in World 27, each with 3 story pages
        val rhymes = WonderCatalogRegistry.getActivitiesForWorld("w27_rhymes")
        assertEquals(30, rhymes.size)
        assertTrue(rhymes.all { it.storyPages.size == 3 })

        // Verify 16 Flashcard decks in World 25
        val flashcards = WonderCatalogRegistry.getActivitiesForWorld("w25_flashcards")
        assertEquals(16, flashcards.size)
    }

    @Test
    fun `verify Real World Media and Wonder Reels inventory and manifest`() {
        val animals = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.ANIMAL)
        val objects = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.OBJECT)
        val lessonsAndFilms = RealWorldMediaRegistry.getItemsByCategory(RealMediaCategory.FILM_LESSON)

        assertEquals(64, animals.size)
        assertEquals(71, objects.size)
        assertEquals(22, lessonsAndFilms.size)

        val allItems = RealWorldMediaRegistry.allMediaItems
        assertEquals(157, allItems.size)
        assertEquals(157, allItems.map { it.id }.toSet().size)
        assertTrue(allItems.all { it.manifest.checksumSha256Prefix.isNotBlank() && it.funFacts.isNotEmpty() })
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
        // Verify each of the 12 Balloon Pop games has its own distinct item dataset
        val distinctFirstItemIds = balloonGames.map { it.items.first().id }.toSet()
        assertEquals(12, distinctFirstItemIds.size)

        val arcadeGames = WonderCatalogRegistry.getActivitiesForWorld("w22_arcade")
        assertEquals(7, arcadeGames.size)
        arcadeGames.forEach { game ->
            assertTrue("Expected at least 6 items in ${game.id}", game.items.size >= 6)
        }
        val distinctArcadeFirstIds = arcadeGames.map { it.items.first().id }.toSet()
        assertEquals(7, distinctArcadeFirstIds.size)

        // Verify Learning Buddy TTS pitches are tuned to warm, natural human range (1.00f..1.05f)
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

        // Verify every mode and world combination returns valid multilingual payloads
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

        // Verify BalloonLifecycleState transitions: SPAWNING -> FLOATING -> POPPING -> REVEALING -> COMPLETED
        val samplePayload = com.example.ui.engines.LearningContentManager.defaultFallbackPayload
        val balloon = com.example.ui.engines.BalloonSpawner.createBalloonForSlot(
            instanceId = 1,
            slotIndex = 0,
            totalSlots = 5,
            ageBand = AgeBand.TODDLER_2_3,
            speedMultiplier = 1.0f,
            payload = samplePayload,
            startAtBottom = true
        )
        assertEquals(com.example.ui.engines.BalloonLifecycleState.SPAWNING, balloon.state)

        // Step 0.30s -> should transition to FLOATING
        repeat(10) {
            com.example.ui.engines.BalloonPhysics.stepBalloon(
                balloon = balloon,
                deltaTimeSec = 0.035f,
                totalElapsedSec = it * 0.035f,
                followRoadStream = true,
                lowGravity = false,
                reducedMotion = false
            )
        }
        assertEquals(com.example.ui.engines.BalloonLifecycleState.FLOATING, balloon.state)

        // Trigger pop -> POPPING -> REVEALING -> COMPLETED
        balloon.state = com.example.ui.engines.BalloonLifecycleState.POPPING
        balloon.stateTimerSec = 0f
        repeat(6) {
            com.example.ui.engines.BalloonPhysics.stepBalloon(
                balloon = balloon,
                deltaTimeSec = 0.04f,
                totalElapsedSec = 1.0f,
                followRoadStream = true,
                lowGravity = false,
                reducedMotion = false
            )
        }
        assertEquals(com.example.ui.engines.BalloonLifecycleState.REVEALING, balloon.state)

        repeat(12) {
            com.example.ui.engines.BalloonPhysics.stepBalloon(
                balloon = balloon,
                deltaTimeSec = 0.045f,
                totalElapsedSec = 1.5f,
                followRoadStream = true,
                lowGravity = false,
                reducedMotion = false
            )
        }
        assertEquals(com.example.ui.engines.BalloonLifecycleState.COMPLETED, balloon.state)

        // Verify AdaptiveDifficultyManager inactivity hints (5s pulse, 8s pointing hand, 12s spoken hint)
        val adaptiveMgr = com.example.ui.engines.AdaptiveDifficultyManager()
        repeat(55) { adaptiveMgr.onFrameTick(0.1f) } // 5.5s
        assertTrue(adaptiveMgr.shouldPulseHintBalloon)
        repeat(30) { adaptiveMgr.onFrameTick(0.1f) } // 8.5s
        assertTrue(adaptiveMgr.shouldShowPointingHand)
        repeat(40) { adaptiveMgr.onFrameTick(0.1f) } // 12.5s
        assertTrue(adaptiveMgr.shouldSpeakInactivityHint)

        adaptiveMgr.onBalloonPopped(matchedChallengeTarget = true)
        assertEquals(0f, adaptiveMgr.inactivitySeconds, 0.001f)
    }

    @Test
    fun `verify Warm Preschool Teacher VoiceAssetManifest, 6-line audition, schwa-free phonics, and honest voice selector`() {
        val auditionLines = com.example.audio.VoiceAssetManifest.sixAuditionLines
        assertEquals(6, auditionLines.size)

        // Verify all 6 audition lines have non-empty scripts, expected raw filenames, and performance directions
        assertTrue(auditionLines.all {
            it.spokenScript.isNotBlank() &&
                it.expectedRawFileName.endsWith(".wav") &&
                it.performanceDirection.isNotBlank()
        })

        // Verify all 26 letters A-Z are present in the phonics manifest with separate name & sound files
        val phonics = com.example.audio.VoiceAssetManifest.phonicsManifest
        assertEquals(26, phonics.size)
        for (ch in 'A'..'Z') {
            val entry = phonics[ch]
            assertTrue("Missing phonics entry for $ch", entry != null)
            assertTrue(entry!!.letterNameFile != entry.letterSoundFile)
            // Consonants must NOT add a trailing schwa "uh" (e.g., no buh, kuh, duh, fuh, luh, muh, suh, tuh, puh)
            if (ch != 'U') {
                val lowerScript = entry.pureSoundScript.lowercase()
                val forbiddenSchwas = listOf("buh", "kuh", "duh", "fuh", "guh", "huh", "luh", "muh", "nuh", "puh", "ruh", "suh", "tuh", "vuh", "zuh")
                forbiddenSchwas.forEach { bad ->
                    assertTrue("Consonant $ch should not contain schwa '$bad' in '${entry.pureSoundScript}'", !lowerScript.contains(bad))
                }
            }
        }

        // Verify /m/ and /s/ use continuous pure phonemes ("mmmm", "ssss")
        assertTrue(phonics['M']!!.pureSoundScript.startsWith("mmmm"))
        assertTrue(phonics['S']!!.pureSoundScript.startsWith("ssss"))

        // Verify all 4 visual companions share the exact same natural teacher pitch (1.00f) and rate (0.90f)
        // rather than pretending to be 4 different voices via pitch/speed shifts
        val pitches = com.example.model.LearningBuddy.entries.map { it.voicePitch }.toSet()
        val rates = com.example.model.LearningBuddy.entries.map { it.voiceRate }.toSet()
        assertEquals(setOf(1.00f), pitches)
        assertEquals(setOf(0.90f), rates)

        // Verify default persisted voice is the Warm Preschool Teacher
        val defaultSettings = com.example.data.ProfileSettingsEntity()
        assertEquals(
            com.example.audio.WonderAudioController.DEFAULT_TEACHER_VOICE_ID,
            defaultSettings.selectedVoiceId
        )
    }

    @Test
    fun `verify ABC Letter Balloon has all 26 letters A-Z and popped balloons never repeat in the same session across all modes`() {
        val abcPayloads = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(
            com.example.ui.engines.BalloonLearningMode.ALPHABET_POP,
            com.example.ui.engines.BalloonWorldEnvironment.FARM_ADVENTURE
        )
        assertEquals(26, abcPayloads.size)
        val symbols = abcPayloads.map { it.symbolOrBadge }
        assertEquals(('A'..'Z').map { it.toString() }, symbols)

        // Verify across all 13 learning modes that once an item is popped in a session,
        // selectNextUnpoppedPayload NEVER returns it again until the session is reset
        com.example.ui.engines.BalloonLearningMode.entries.forEach { mode ->
            val payloads = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(
                mode,
                com.example.ui.engines.BalloonWorldEnvironment.FARM_ADVENTURE
            )
            val poppedIds = mutableSetOf<String>()
            val floatingIds = mutableSetOf<String>()

            // Fill initial floating slots (up to 5) with unique items
            val initialSlots = minOf(5, payloads.size)
            for (slot in 0 until initialSlots) {
                val spawned = com.example.ui.engines.BalloonSpawner.selectNextUnpoppedPayload(
                    allPayloads = payloads,
                    poppedIds = poppedIds,
                    currentlyFloatingIds = floatingIds,
                    seedIndex = slot
                )
                assertTrue("Expected initial unpopped payload for $mode slot $slot", spawned != null)
                assertTrue("Duplicate floating item spawned in $mode", floatingIds.add(spawned!!.id))
            }

            // Simulate popping every balloon in the mode one by one without resetting
            var step = 0
            while (floatingIds.isNotEmpty()) {
                val poppedId = floatingIds.first()
                floatingIds.remove(poppedId)
                val newlyPopped = poppedIds.add(poppedId)
                assertTrue("Item $poppedId was popped twice in the same session for $mode!", newlyPopped)

                val replacement = com.example.ui.engines.BalloonSpawner.selectNextUnpoppedPayload(
                    allPayloads = payloads,
                    poppedIds = poppedIds,
                    currentlyFloatingIds = floatingIds,
                    seedIndex = step++
                )
                if (replacement != null) {
                    assertTrue("Replacement ${replacement.id} was already popped in $mode!", replacement.id !in poppedIds)
                    assertTrue("Replacement ${replacement.id} was already floating in $mode!", replacement.id !in floatingIds)
                    floatingIds.add(replacement.id)
                }
            }

            // Confirm every unique item in the mode was popped exactly once, and no more spawn until reset
            assertEquals(payloads.map { it.id }.toSet(), poppedIds)
            val afterAllPopped = com.example.ui.engines.BalloonSpawner.selectNextUnpoppedPayload(
                allPayloads = payloads,
                poppedIds = poppedIds,
                currentlyFloatingIds = floatingIds,
                seedIndex = 99
            )
            assertEquals(null, afterAllPopped)
        }

        // Verify BalloonRenderer custom path silhouette geometry (tapered top, wide dome, pinched neck, flared bottom knot)
        val metrics = com.example.ui.engines.BalloonRenderer.computeMetrics(100f, 140f)
        assertTrue(metrics.taperedTopY < metrics.upperDomeWidestY)
        assertTrue(metrics.upperDomeWidestY < metrics.lowerTaperNeckY)
        assertTrue(metrics.lowerTaperNeckY < metrics.knotBottomY)
        assertTrue(metrics.knotBottomY < metrics.stringTipY)
        assertTrue(metrics.halfTaperedTopWidth < metrics.halfUpperDomeWidth)
        assertTrue(metrics.halfNeckWidth < metrics.halfTaperedTopWidth)
        assertTrue(metrics.halfKnotWidth > metrics.halfNeckWidth)
    }

    @Test
    fun `verify multi-level 15-item voice random challenge, distractor wobble, and 4 progressive number tiers 1 to 100`() {
        // 1. Verify all 4 progressive number tiers (1-20, 21-40, 41-60, 61-100)
        val tier1 = com.example.ui.engines.BalloonNumberTier.TIER_1_20
        val tier2 = tier1.nextTier()!!
        val tier3 = tier2.nextTier()!!
        val tier4 = tier3.nextTier()!!
        assertEquals(com.example.ui.engines.BalloonNumberTier.TIER_21_40, tier2)
        assertEquals(com.example.ui.engines.BalloonNumberTier.TIER_41_60, tier3)
        assertEquals(com.example.ui.engines.BalloonNumberTier.TIER_61_100, tier4)
        assertEquals(null, tier4.nextTier())

        val numbers1To20 = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(
            com.example.ui.engines.BalloonLearningMode.NUMBER_POP,
            com.example.ui.engines.BalloonWorldEnvironment.FARM_ADVENTURE,
            tier1
        )
        assertEquals((1..20).map { it.toString() }, numbers1To20.map { it.symbolOrBadge })

        val numbers21To40 = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(
            com.example.ui.engines.BalloonLearningMode.NUMBER_POP,
            com.example.ui.engines.BalloonWorldEnvironment.FARM_ADVENTURE,
            tier2
        )
        assertEquals((21..40).map { it.toString() }, numbers21To40.map { it.symbolOrBadge })

        val numbers41To60 = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(
            com.example.ui.engines.BalloonLearningMode.COUNTING_POP,
            com.example.ui.engines.BalloonWorldEnvironment.FARM_ADVENTURE,
            tier3
        )
        assertEquals((41..60).map { it.toString() }, numbers41To60.map { it.symbolOrBadge })

        val numbers61To100 = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(
            com.example.ui.engines.BalloonLearningMode.NUMBER_POP,
            com.example.ui.engines.BalloonWorldEnvironment.FARM_ADVENTURE,
            tier4
        )
        assertEquals((61..100).map { it.toString() }, numbers61To100.map { it.symbolOrBadge })

        // 2. Verify 15-item random challenge queue generation and fresh randomization across seeds
        val abcPayloads = com.example.ui.engines.LearningContentManager.getPayloadsForModeAndWorld(
            com.example.ui.engines.BalloonLearningMode.ALPHABET_POP,
            com.example.ui.engines.BalloonWorldEnvironment.FARM_ADVENTURE
        )
        val round1Queue = com.example.ui.engines.BalloonSpawner.generateRandomChallengeQueue(
            allPayloads = abcPayloads,
            roundSeed = 101L,
            targetCount = com.example.ui.engines.BalloonSpawner.CHALLENGE_ROUND_TARGET_COUNT
        )
        val round2Queue = com.example.ui.engines.BalloonSpawner.generateRandomChallengeQueue(
            allPayloads = abcPayloads,
            roundSeed = 202L,
            targetCount = com.example.ui.engines.BalloonSpawner.CHALLENGE_ROUND_TARGET_COUNT
        )
        assertEquals(15, round1Queue.size)
        assertEquals(15, round1Queue.map { it.id }.toSet().size)
        assertEquals(15, round2Queue.size)
        assertEquals(15, round2Queue.map { it.id }.toSet().size)
        assertTrue("Expected different random 15-item sequences across seeds", round1Queue.map { it.id } != round2Queue.map { it.id })

        // 3. Verify Voice Challenge Prompt and Gentle Wobble Retry Prompt
        val targetA = abcPayloads.first()
        val distractorB = abcPayloads[1]
        val voiceClue = com.example.ui.engines.BalloonSpawner.buildVoiceChallengePrompt(
            targetA,
            com.example.ui.engines.BalloonNarrationLanguage.ENGLISH
        )
        val retryClue = com.example.ui.engines.BalloonSpawner.buildGentleWobbleRetryPrompt(
            distractorB,
            targetA,
            com.example.ui.engines.BalloonNarrationLanguage.ENGLISH
        )
        assertTrue(voiceClue.contains("A"))
        assertTrue(retryClue.contains("B") && retryClue.contains("A"))

        // 4. Verify distractor wobble timer decays smoothly without popping the balloon
        val balloon = com.example.ui.engines.BalloonSpawner.createBalloonForSlot(
            instanceId = 10,
            slotIndex = 1,
            totalSlots = 5,
            ageBand = AgeBand.PRESCHOOL_4_5,
            speedMultiplier = 1.0f,
            payload = distractorB,
            startAtBottom = false
        )
        balloon.wobbleTimerSec = 0.48f
        repeat(4) {
            com.example.ui.engines.BalloonPhysics.stepBalloon(
                balloon = balloon,
                deltaTimeSec = 0.05f,
                totalElapsedSec = 1.0f,
                followRoadStream = true,
                lowGravity = false,
                reducedMotion = false
            )
        }
        assertTrue(balloon.wobbleTimerSec in 0.25f..0.30f)
        assertEquals(com.example.ui.engines.BalloonLifecycleState.FLOATING, balloon.state)
    }
}



