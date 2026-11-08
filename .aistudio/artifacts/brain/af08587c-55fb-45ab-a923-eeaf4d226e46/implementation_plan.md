# Multi-Level Balloon Pop Progression & Voice-Guided Random Challenge

Introduces structured multi-level progression across all 13 Balloon Pop learning modes, transitioning children from open discovery (popping every item in the deck once) into a voice-guided random challenge round where the Warm Preschool Teacher announces a target item and only that matching balloon pops, alongside four progressive number range tiers (`1–20`, `21–40`, `41–60`, `61–100`).

## User Review & Critical Decisions

> [!IMPORTANT]
> All core gameplay and progression choices have been confirmed from your answers:

- **Confirmed Decision 1 — 15 Random Items per Voice-Guided Challenge Round**: After completing the full Discovery deck (e.g., all 26 letters `A–Z`), the next level generates a freshly shuffled set of **15 random target items** (or the full shuffled deck when a category has fewer than 15 items, with fresh random ordering every round).
- **Confirmed Decision 2 — Gentle Wobble & Voice Clue Retry on Non-Matching Taps**: During the Voice-Guided Random Challenge level, tapping a distractor balloon triggers a playful side-to-side wobble animation without popping it, and the teacher voice gently repeats the target clue (*"Let's look for B! Find the B balloon!"*).
- **Confirmed Decision 3 — Four Progressive Number Tiers (`1–20`, `21–40`, `41–60`, `61–100`)**: Number Pop and Counting Pop expand from 10 numbers to four full tiers (`1–20`, `21–40`, `41–60`, and `61–100`), where each tier offers a Discovery stage followed by a 15-number Voice-Guided Random Challenge before advancing to the next range.

---

## 1. Overview & Core Concept

- **What It Does**:
  1. **Universal Multi-Level Progression Across All 13 Modes**:
     - **Stage A — Discovery Level (Learn & Explore)**: The child pops every item in the active deck once (e.g., all 26 letters `A–Z`, all 20 numbers in the active tier, or all shapes/colors/animals/fruits/vegetables/vehicles/words) with zero repeats within the session.
     - **Stage B — Voice-Guided Random Challenge Level (Listen & Find — 15 Random Targets)**: Once the Discovery deck is completed, tapping **Next Level** launches a freshly shuffled 15-item quiz round. At the start of each target, the Warm Preschool Teacher speaks the prompt (*"Can you pop the letter M?"* / *"Find number 24!"*). Floating on screen is the target balloon alongside non-matching distractor balloons. Only the matching balloon pops when tapped; tapping any other balloon gently wobbles it and replays the voice clue. Completing all 15 random targets unlocks a fresh random set or advances to the next tier.
  2. **Four Progressive Number & Counting Tiers (`1–20`, `21–40`, `41–60`, `61–100`)**:
     - **Tier 1 (`1–20`)**: Numbers `1` through `20` Discovery -> `1–20` Voice-Guided 15-Item Random Challenge.
     - **Tier 2 (`21–40`)**: Numbers `21` through `40` Discovery -> `21–40` Voice-Guided 15-Item Random Challenge.
     - **Tier 3 (`41–60`)**: Numbers `41` through `60` Discovery -> `41–60` Voice-Guided 15-Item Random Challenge.
     - **Tier 4 (`61–100`)**: Numbers `61` through `100` Discovery -> `61–100` Voice-Guided 15-Item Random Challenge.
- **Target Audience / Persona**: Toddlers and preschoolers (ages 2–6) learning letters, phonics, numbers up to 100, shapes, colors, animals, fruits, vegetables, vehicles, and sight words.
- **Key Value**: Transforms Balloon Pop from a single-pass popper into a self-paced learning ladder that reinforces active listening and symbol recognition without punitive failure states.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Discovery Level Flow**:
     - Child enters ABC Letter Balloon (or any Balloon Pop game) at **Level 1: Explore (`26` Letters)**.
     - Each popped balloon reveals its illustration, speaks its name/phonics, and is marked completed in the session counter (`🎈 1/26` ... `🎈 26/26`).
     - Upon popping the final balloon in the deck, a celebratory **Level Complete!** modal appears with a prominent **"🌟 Next Level: Voice Challenge"** button (and an option to replay the current level).
  2. **Voice-Guided Random Challenge Flow**:
     - Entering the Challenge Level selects a freshly shuffled queue of **15 random targets** from the current deck.
     - The Warm Preschool Teacher immediately announces the first target (*"Find the letter K!"*).
     - The spawner guarantees that the target balloon is actively floating on screen alongside 4–5 distinct distractor balloons from the same category.
     - **Correct Tap**: The balloon bursts with confetti particles, reveals the educational card, speaks praise + item narration, increments the challenge progress (`🎯 1/15`), and automatically announces the next random target from the 15-item queue.
     - **Distractor Tap**: The tapped balloon performs a damped horizontal wobble (`±12°` tilt / `±10dp` shake for `0.45s`) without popping, and the teacher calmly repeats the target prompt (*"Let's look for K! Tap the K balloon."*).
  3. **Number Tier Advancement Flow (`1–20` -> `21–40` -> `41–60` -> `61–100`)**:
     - In Number Pop and Counting Pop, completing **Tier 1 (`1–20`) Discovery** leads to **Tier 1 (`1–20`) Voice Challenge (15 random numbers)**, which then unlocks **Tier 2 (`21–40`) Discovery**, continuing all the way through **Tier 4 (`61–100`)**.
     - A compact **Level / Tier Pill** in the top control strip also lets parents or children jump directly between unlocked levels or number tiers (`1–20`, `21–40`, `41–60`, `61–100`, or `🎯 Quiz`) at any time.
- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Picture-first, sunlit storybook aesthetic with custom 3D Bézier teardrop balloons (tapered crown, wide upper dome, pinched neck, flared bottom knot, and swaying string).
  - *Color Palette & Mood*: High-contrast child-friendly palette (`ActionPlayGreen` `#16A34A` for level-up actions, `ActionListenAmber` `#F59E0B` for voice-challenge target clues, `ActionContinueBlue` `#2563EB` for level badges, `SoftCreamCard` `#FFFBEB` for clue banners, and `DeepSlateInk` `#0F172A` for ultra-legible numerals up to `100`).
  - *Typography & Hierarchy*: ExtraBold rounded numerals and letters inside the upper balloon dome (`32sp` for 1–2 digit symbols, `26sp` for `100`), paired with a compact icon-first top bar.
- **Interactive Feedback & Motion**:
  - **Wobble Animation on Distractor Tap**: Smooth sine-wave horizontal shake and tilt (`wobbleTimerSec` from `0.0s` to `0.45s`) so toddlers immediately understand "not that one" without harsh buzzers or red X marks.
  - **Automatic Voice Prompting**: Whenever a new target becomes active in the Challenge level, the audio controller automatically speaks the target instruction and ducks background music.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1 — Two-Phase Loop Per Tier (Discovery -> 15-Item Random Voice Challenge)**:
  - *Chosen Approach*: Every mode (and each of the 4 number tiers) pairs an ordered/complete Discovery pass with a 15-item randomized Voice Challenge pass, and re-entering the Challenge level always generates a new random seed.
  - *Why*: Ensures children first see and hear every concept in a stress-free exploration round before being asked to identify specific items by voice, while fresh random shuffling prevents memorizing a fixed sequence.
  - *Alternatives Considered*: Purely random mode from the start (rejected because children need initial exposure before testing, especially on higher numbers like `41–60` and `61–100`).
- **Decision 2 — Guaranteed Target Presence Among Distractors in Challenge Level**:
  - *Chosen Approach*: During the Voice Challenge level, exactly one floating slot is reserved for the active target balloon while the remaining 4–5 slots spawn non-repeating distractor balloons from the same tier.
  - *Why*: Prevents frustrating delays where a child hears *"Find M!"* but has to wait for `M` to spawn, while still requiring visual discrimination among multiple floating balloons.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:

```
┌───────────────────────────────────────────────────────────────────────────┐
│                      BalloonPopAdventureComposable                        │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ Top Picture-First Bar: World | 🎯 Voice Clue | Modes | Level/Tier   │  │
│  └──────────────────────────────────┬──────────────────────────────────┘  │
│                                     ▼                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │                   BalloonProgressionController                      │  │
│  │  • Stage: DISCOVERY_ALL vs VOICE_CHALLENGE_15                       │  │
│  │  • NumberTier: TIER_1_20 | TIER_21_40 | TIER_41_60 | TIER_61_100    │  │
│  │  • Random Challenge Queue (15 freshly shuffled targets per round)   │  │
│  └───────────────┬─────────────────────────────────────┬───────────────┘  │
│                  ▼                                     ▼                  │
│  ┌───────────────────────────────┐     ┌───────────────────────────────┐  │
│  │   BalloonSpawner & Physics    │     │   WonderAudioController       │  │
│  │  • Discovery: Unpopped only   │     │  • Auto-speaks target prompt  │  │
│  │  • Challenge: 1 Target +      │     │    on each new challenge step │  │
│  │    4-5 Distractor Balloons    │     │  • Gentle retry prompt on     │  │
│  │  • WOBBLING state on miss tap │     │    distractor tap wobble      │  │
│  └───────────────┬───────────────┘     └───────────────────────────────┘  │
│                  ▼                                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │             BalloonRenderer (3D Path-Based Silhouette)              │  │
│  │  • Tapered top crown, upper dome, pinched neck, flared bottom knot  │  │
│  │  • Horizontal wobble transform when distractor is tapped            │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────┘
```

- **Data Model & State**:
  - `BalloonNumberTier`: Enum defining `TIER_1_20` (`1..20`), `TIER_21_40` (`21..40`), `TIER_41_60` (`41..60`), and `TIER_61_100` (`61..100`), with multilingual English, Telugu, and Hindi spoken scripts and visual counting badges generated for all numbers `1..100`.
  - `BalloonStageType`: Enum with `DISCOVERY_EXPLORE` (pop all items in the deck once; any balloon tapped is valid) and `VOICE_RANDOM_CHALLENGE` (15 freshly shuffled target prompts; only the announced target balloon pops).
  - `wobbleTimerSec` on `ActiveBalloonEntity`: Tracks a `0.45s` damped sine-wave horizontal shake (`sin(wobbleTimerSec * 36f)`) when a child taps a non-matching distractor balloon in `VOICE_RANDOM_CHALLENGE` mode.
- **Interactive Component & State Mapping**:
  - **Tapping a Balloon in `DISCOVERY_EXPLORE`**: Pops immediately, records `payload.id` in `poppedIdsInSession`, speaks item narration, and never respawns that item in the current round. When `poppedIdsInSession.size == payloads.size`, displays the **Level Complete** modal with **"🌟 Next Level: Voice Challenge (15)"**.
  - **Tapping a Balloon in `VOICE_RANDOM_CHALLENGE`**:
    - If `balloon.payload.id == currentChallengeTarget.id`: Pops the balloon, speaks praise + item name, advances `challengeStepIndex` (`0 until 15`), spawns the next target balloon if not already floating, and speaks the next target prompt (*"Now find ...!"*).
    - If `balloon.payload.id != currentChallengeTarget.id`: Sets `balloon.wobbleTimerSec = 0.45f`, keeps the balloon floating, and speaks a patient retry cue (*"That's ${balloon.payload.symbolOrBadge}. Let's find ${currentChallengeTarget.symbolOrBadge}!"*).
  - **Completing All 15 Challenge Targets**: Shows the **Challenge Complete!** celebration card with **"🚀 Next Level"** (advancing `TIER_1_20` -> `TIER_21_40` -> `TIER_41_60` -> `TIER_61_100` in Number/Counting modes, or generating a brand-new random 15-item challenge set in other modes).
