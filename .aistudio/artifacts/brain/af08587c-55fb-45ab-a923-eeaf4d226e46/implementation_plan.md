# Little Wonder World

An offline-first, landscape-oriented preschool discovery and learning game for children aged 2–6, blending a warm storybook aesthetic with tactile Toy Box shortcuts, 6 Learning Lands, 27 Worlds, 257 playable activity configurations, a **TikTok/Reels-style Real World Animal & Object Video Feed ("Wonder Reels")**, and a protected Parent Corner.

## User Review & Critical Decisions

> [!IMPORTANT]
> Updated to incorporate **Scrollable Animal vs. Object Filtering**, **TikTok/Reels-Style Swipeable Video Reels ("Wonder Reels")**, and **Build-Time AI Asset Generation + Google Flow Video Pack Support**.

- **Confirmed Decision 1 — Native Jetpack Compose & Custom Canvas Architecture**: Built 100% in native Kotlin and Jetpack Compose using custom multi-touch `Canvas` engines for tracing, drawing, sorting, water physics, balloon pop, and xylophone/piano synthesis, ensuring zero WebView compatibility bottlenecks and instant offline responsiveness.
- **Confirmed Decision 2 — Flagship Interactive Priority**: First-slice deep polish focuses on **Color & Shape Sorting**, **Animal Sounds & Safari**, **Balloon Pop Adventure**, **Alphabet & Shape Tracing**, and **Water Lab (Float/Sink)** alongside the complete 14 reusable game engines powering all 27 worlds and 257 selectable activities.
- **Confirmed Decision 3 — Offline Spoken Narration & Procedural Audio**: Uses Android's offline `TextToSpeech` engine with phonetic tuning (distinguishing letter names vs. phonetic sounds) paired with a custom low-latency PCM `AudioTrack` procedural synthesizer for xylophone notes, piano chords, drums, animal calls, and gentle celebratory chimes—with automatic caption/visual fallback when TTS voices are unavailable.
- **Confirmed Decision 4 — Scrollable Animals vs. Objects + TikTok/Reels-Style "Wonder Reels"**:
  - Children can explicitly choose to browse **Animals** (64 rich visual cards / 123-animal atlas), **Objects** (71 everyday object sound & action cards), or **Mini Lessons** (16 narrated lessons + 6 Real Films).
  - Children can toggle between **Card Scroll Mode** (scrollable visual gallery) and **Wonder Reels Mode** (a full-screen, child-safe TikTok/Reels-style vertical swipe `VerticalPager` feed where each swipe auto-plays the animal or object's motion reel, authentic sound effect, spoken fact, and synchronized captions).
- **Confirmed Decision 5 — Build-Time Image Generation & Google Flow Video Pack Pipeline**:
  - Uses build-time AI image generation (`generate_image`) to bundle high-craft starting-frame artwork for flagship animals and objects directly in `res/drawable/`.
  - Pairs bundled images with a **Living Motion Reel Renderer** (animated habitat parallax, kinematics, particle effects, and soundscapes) so every Reel plays like a lively short video out of the box, while also supporting native offline `.mp4` playback (`VideoView` / `MediaPlayer`) for Google Flow / Veo video clips bundled in `assets/reels/` or imported by parents in Parent Corner.

---

## 1. Overview & Core Concept

- **What It Does**: Provides a forgiving, curiosity-driven play environment where toddlers and young children explore 6 themed Learning Lands, a Toy Box with 1-tap favorites (`My Shelf`), a **Real World Hub with scrollable Animal/Object galleries and swipeable Wonder Reels**, and 30 narrated three-page stories—all without timers, punitive scoring, or network dependencies.
- **Target Audience / Persona**:
  - **Children (Ages 2–3, 4–5, 5–6)**: Need oversized touch targets, spoken instructions, swipeable video reels for animals and objects, tap-to-place alternatives for drag interactions, optional "Show Me" demonstrations, and gentle retry feedback.
  - **Parents & Caregivers**: Need an arithmetic-gated Parent Corner to adjust age bands, switch speaking buddies, inspect local progress across lands, import custom Google Flow `.mp4` reels or family photos, and inspect media verification status.
- **Key Value**: Adapts difficulty dynamically by age band (choice count, tracing tolerance, phonics complexity) while preserving every activity, favorite, drawing, custom video reel, and milestone locally across sessions.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **First-Launch Onboarding**: Child or grown-up selects an **Age Band** (`2–3 Toddler`, `4–5 Preschool`, `5–6 Pre-K/Kindergarten`) and picks one of **four original speaking Learning Buddies** (*Sunny the Cub*, *Pip the Owl*, *Bramble the Bunny*, *Coral the Otter*).
2. **Landscape Home Hub**: Fixed non-scrolling child layout featuring the **6 Learning Lands**, prominent highlight cards for **Creative Garden** and **Thinking Forest**, **Surprise Play** (instant age-matched activity launcher), **Toy Box** drawer (`Balloon Pop`, `Music`, `Paint`, `Puzzles`, `Puppy Wash`, `Water Lab`, plus `My Shelf` favorites/recents), and a distinct **Real World & Wonder Reels** button kept clear of land cards.
3. **Real World Explorer & "Wonder Reels" (TikTok/Reels-Style Feed)**:
   - **Category Switcher**: Large top picture-pills let kids pick **🦁 Animals**, **🚂 Objects**, or **🎬 Stories & Films**.
   - **View Mode Toggle**: Kids can freely scroll through the **Card Gallery** or tap **"Watch Reels"** to enter the full-screen swipeable **Wonder Reels** feed (`VerticalPager`).
   - **Wonder Reels Interaction**: Swiping up or down (or tapping big Up/Down arrows for accessibility) transitions smoothly to the next Animal or Object clip. Each Reel plays the `.mp4` video (if imported/bundled from Google Flow) or the **Living Motion Reel** animation (using build-time generated reference art + animated motion layers), triggers the animal/object sound and spoken fun fact, displays synchronized readable captions, and offers big side buttons for **🔊 Hear Sound**, **🗣️ Hear Fact**, **❤️ Save to My Shelf**, and **🔁 Replay**.
4. **Land → World → Activity Pagination**: Tapping a Land reveals its Worlds; tapping a World displays up to **9 large activity tiles per page** with clear page dots and next/previous arrows.
5. **Parent Corner, Media Importer & My Family Album**: Protected by a rotating arithmetic challenge (e.g., `7 + 5 = ?`). Grown-ups can view progress by Land/World, toggle audio/reduced motion, browse the **Asset & Media Manifest Inspector**, import custom Google Flow `.mp4` video clips or custom animal/object images from device storage to override any card ID, or add up to 16 local photos via the system Photo Picker (`PickVisualMedia`) for **My Family Album**.

### Visual Identity & Theme
- **Aesthetic Direction**: Warm Storybook Tactile Play—high-craft illustrations and photorealistic Real World cards, rounded physical-toy card borders, soft layered depth, and calm visual breathing room.
- **Color Palette & Mood**:
  - **Canvas & Surfaces**: Calm Cool-Blue background (`#EBF4FA`), Crisp White Play Surface (`#FFFFFF`), Theater Dark Reel Backdrop (`#0F172A`) for immersive Wonder Reels, Elevated Soft Cream Card (`#FDFBF7`), Deep Slate Ink text (`#1B2A38`).
  - **Semantic Action Colors**: Play Action Green (`#22A06B`), Listen/Audio Amber (`#F59E0B`), Continue/Next Sky Blue (`#2563EB`).
  - **Distinct Land Accents**:
    - *Learning Land*: Sunny Coral (`#FF6B6B`)
    - *Thinking Forest*: Emerald Canopy (`#10B981`)
    - *Discovery Island*: Ocean Teal (`#06B6D4`)
    - *Creative Garden*: Berry Violet (`#8B5CF6`)
    - *Little Life Town*: Warm Tangerine (`#F97316`)
    - *Adventure Funfair*: Golden Marigold (`#EAB308`)
- **Typography & Hierarchy**: Locally bundled Google Fonts (`Fredoka` for rounded, friendly child headers and letter tiles; `Nunito` for ultra-legible phonics, captions, and parent metrics).
- **Interactive Feedback & Motion**: Spring-physics bounce on tile taps (respecting the **Reduced Motion** toggle), sparkle star milestones, and both drag-and-drop and 1-tap select-and-place accessibility modes.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Dual-Mode Real World Media (Scrollable Gallery + TikTok/Reels-Style "Wonder Reels")**
  - *Chosen Approach*: Provide dedicated category filters (`Animals` vs. `Objects` vs. `Lessons`) and a seamless toggle between a scrollable card grid and a full-screen vertical swipe feed (`VerticalPager`). Each Reel item supports native `.mp4` playback (for Google Flow / Veo clips) and an automatic **Living Motion Reel** fallback powered by build-time AI-generated reference images (`generate_image`) + multi-layer Compose Canvas animation.
  - *Why*: Gives kids the engaging, effortless swipe-to-watch experience of Reels in a 100% offline, ad-free, child-safe sandbox, while letting the user attach or drop in their own Google Flow videos and images by stable ID at any time.
  - *Alternatives Considered*: Static-only flashcards (less engaging for toddlers who want to see movement) or requiring internet streaming (violates strict offline requirement).

- **Decision 2: Data-Driven 257-Activity Catalog with 14 Reusable Engines**
  - *Chosen Approach*: Define a structured, deterministic catalog covering all 6 Lands, 27 Worlds, and 257 selectable activity configurations (including the 52-step ABC journey, 1–100 math journey, 16 flashcard decks, and 30 3-page Rhyme & Storytime books) mapped to 14 specialized Jetpack Compose interactive engines.
  - *Why*: Guarantees every single tile is genuinely playable with distinct items, rules, and age adaptations while keeping the codebase modular and maintainable.

- **Decision 3: Hybrid Offline Audio (Phonetic TTS + Real-Time PCM Synthesizer)**
  - *Chosen Approach*: Combine Android's `TextToSpeech` (with explicit phonetic spelling maps so `/b/` says *"buh"* instead of *"bee"*) with a multi-voice PCM waveform synthesizer (`AudioTrack`) for polyphonic piano/xylophone keys, drum pads, water splashes, vehicle sirens, and animal sound signatures.
  - *Why*: Eliminates external network calls, supports simultaneous multi-touch notes without pitch-shift artifacts, and guarantees 100% offline reliability.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                     MainActivity (Landscape, Edge-to-Edge)                   │
│             BackHandler Hierarchy: Reel/Overlay → Activity → World           │
└──────────────────────────────────────┬───────────────────────────────────────┘
                                       │
┌──────────────────────────────────────▼───────────────────────────────────────┐
│                      WonderWorldViewModel (StateFlow MVVM)                   │
│  • AgeBand (2–3, 4–5, 5–6) & LearningBuddy State                             │
│  • Navigation Stack (Onboarding │ Home │ ToyBox │ Land │ World │ Activity    │
│                      │ RealWorldHub [Scroll / WonderReels] │ ParentCorner)   │
│  • AudioVideoCoordinator (Stops TTS/Synth/Video on Swipe, Back, Background)  │
└───────────┬──────────────────────────────────┬───────────────────────────────┘
            │                                  │
┌───────────▼──────────────────────┐ ┌─────────▼───────────────────────────────┐
│   Catalog & Media Registries     │ │   Room Database & Local Storage         │
│  • 6 Lands / 27 Worlds           │ │  • UserSettingsEntity (Age, Buddy, SFX) │
│  • 257 Playable Activity Configs │ │  • ActivityProgressEntity (Stars, Step) │
│  • 30 Three-Page Storybooks      │ │  • CustomMediaOverrideEntity (User Flow │
│  • Real World Atlas & Reels:     │ │    MP4s & Custom Animal/Object Images)  │
│    - 123 Animals (64 Rich Pack)  │ │  • SavedArtworkEntity (Canvas Strokes)  │
│    - 71 Objects (45 Sound Cards) │ │  • FamilyMemberEntity (Up to 16 Photos) │
│    - 16 Short Lessons & 6 Films  │ │  • ShelfItemEntity (Favorites/Recents)  │
└───────────┬──────────────────────┘ └─────────────────────────────────────────┘
            │
┌───────────▼──────────────────────────────────────────────────────────────────┐
│           Wonder Reels Player & 14 Reusable Jetpack Compose Engines          │
│  • WonderReelsFeed (VerticalPager: MP4 VideoPlayer + Living Motion Canvas)   │
│  1. SortingEngine (Drag/Tap Baskets)   8. MusicRhythmEngine (Multi-touch)    │
│  2. AnimalSafariEngine (Sounds/Atlas)  9. StorybookEngine (3-Page + Safety)  │
│  3. BalloonPopEngine (Floating Canvas)10. CodingMazeEngine (Arrow Routes)    │
│  4. TracingEngine (Coverage/Direction)11. PretendCareEngine (Puppy/Teeth)    │
│  5. WaterLabEngine (Buoyancy Physics) 12. FlashcardDeckEngine (16 Decks)     │
│  6. MemoryMatchEngine (Age-Grid)      13. BuildingBlockEngine (Towers/Roads) │
│  7. PhonicsMathQuizEngine (ABC/1-100) 14. DrawingStudioEngine (Sketch/Color) │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Data Model & State
- **Real World Media & Wonder Reels Schema**:
  - `RealWorldMediaItem`: `id` (e.g., `animal_lion`, `object_fire_truck`), `category` (`ANIMAL`, `OBJECT`, `FILM_LESSON`), `title`, `subtitle`, `habitatOrContext`, `funFacts: List<String>`, `phoneticCue`, `drawableResId` (bundled AI-generated image or rich illustration), `bundledVideoAssetPath` (optional `assets/reels/<id>.mp4`), `motionStyle` (`SAVANNA_PROWL`, `OCEAN_SWIM`, `SKY_FLIGHT`, `VEHICLE_DRIVE`, `SOUND_PULSE`), `soundSignatureId`, and `manifestMetadata` (checksum, source, license, generation status, review status).
  - `CustomMediaOverrideEntity` (Room table): Allows linking any `mediaId` to a user-imported Google Flow `.mp4` video or high-res image picked via `PickVisualMedia`, persisted in app-private storage so custom Google Flow clips appear immediately inside **Wonder Reels** and **Animal/Object Scroll**.
- **Catalog Hierarchy**:
  - `Land` (6 total) → `World` (27 total) → `ActivityConfig` (257 total, each with stable ID, title, worldId, engineType, difficulty parameters, demonstration script, and item dataset).
- **Persistent Entities (Room v1)**:
  - `ProfileSettings`, `ActivityProgress`, `SavedArtwork`, `FamilyPhoto`, `CustomMediaOverride`.

### Interactive Component & State Mapping
- **Wonder Reels & Scrollable Explorer Mechanics**:
  - **Filter Tabs**: Switching between `All`, `🦁 Animals (123)`, `🚂 Objects (71)`, and `🎬 Lessons & Films (22)` filters both the scrollable card grid and the `VerticalPager` Reels queue.
  - **Auto-Play & Lifecycle Hygiene**: As a child swipes to page `N` in `VerticalPager`, page `N-1` immediately stops its audio/video, and page `N` begins its motion reel, plays its distinct animal/object audio signature, and reads its kid-friendly caption aloud. Pressing Android `Back` from Wonder Reels returns to the Real World Scroll Gallery before exiting to Home.
  - **Google Flow Asset Integration**: At build time, we generate flagship high-resolution starting-frame images (`generate_image`) for featured animals and objects and wire up both `assets/reels/<id>.mp4` auto-detection and a Parent Corner **"Import Google Flow Video / Image"** button so you can test and attach your generated videos effortlessly.
