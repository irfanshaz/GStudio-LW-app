# Sunny Garden Art & Voice Production Handoff

## 1. Current Android Implementation & Fallback Status

The `feature/garden-art-voice-polish` branch (built on top of `feature/wonder-garden`, commit `49fcf56903639f51dfafa338aa5c6d583b8a1495`) implements:

- **Independent Reading and Math Levels**: Configured in Parent Corner (`2-3`, `4-5`, `5-6`). A garden in progress retains its starting levels until replay.
- **Four-Stop Garden Mission (`GardenMission`, `GardenScreen`)**:
  1. `Find a seed` (Stage 0 — English phonics & word building)
  2. `Plant seeds` (Stage 1 — Counting to 3, 5, or adding to 8)
  3. `Find sunlight` (Stage 2 — Plant care need)
  4. `Water the garden` (Stage 3 — Story comprehension & garden completion at Stage 4)
- **Three Matching Scene States (`GardenSceneStage`)**:
  - `BARE_SOIL` (`garden_scene_stage_bare_soil`): Stages 0–1 (horizon, terracotta pot, dark soil mound, and seed count dots during Stage 1).
  - `SPROUT` (`garden_scene_stage_sprout`): Stages 2–3 (green stem + cotyledon leaves emerging from the pot, with warm sunbeams on Stage 3).
  - `BLOOM` (`garden_scene_stage_bloom`): Stage 4 (full sunflower blossom with golden petals, brown center, and planted completion flag).
  - *Fallback Status*: The scene currently renders with deterministic Compose `Canvas` artwork + the approved Sunny character reference (`R.drawable.img_launcher_wonder_cub_1794160808743`). It is **not** final custom studio scene illustration.
- **Sunny Pose State Machine (`SunnyPose`)**:
  - `IDLE` (`0 ms`), `SHORT_RUN` (`700 ms` travel), `WAVE` (`450 ms`), and `FLAG_PLANT` (`650 ms`).
  - Bottom-center foot pivot `TransformOrigin(0.50f, 0.92f)` keeps Sunny's paws aligned with the ground horizon (`y = 0.78 * height`).
  - Celebration triggers **only** on an active mission stop completion (`practice == null && next.stage > active.stage`), never on initial composition, recomposition, practice/replay entry, or resuming an already-complete (`stage == 4`) garden.
  - Reduced Motion (`settings.reducedMotion == true`) uses still `IDLE` equivalents (`0 ms` tween). Leaving the screen or backgrounding (`ON_PAUSE`/`ON_STOP`) immediately stops motion and audio.
- **Four Visually Distinct & Reachable Stops (`GardenStopState`)**:
  - Arranged in a compact 2×2 grid inside the left column so all four stops (`garden_stop_0` through `garden_stop_3`) remain above the fold on small landscape screens (`731×411 dp` and `640×360 dp`) with `>= 48.dp` touch height.
  - Distinct shapes, symbols, labels, and TalkBack descriptions:
    - **Finished** (`GardenStopState.FINISHED`): `RoundedCornerShape(16.dp)`, `⚑ ✓`, `"Finished • Practice"`, enabled for practice without overwriting the active mission or incrementing `completedGardens`.
    - **Current** (`GardenStopState.CURRENT`): `CutCornerShape(10.dp)`, `▶ ★`, `"Current stop"`.
    - **Up next** (`GardenStopState.UP_NEXT`): `RoundedCornerShape(8.dp)`, `○`, `"Up next"`.

---

## 2. Visual Asset Specification & Destination Paths

Use `@drawable/img_launcher_wonder_cub_1794160808743` as the canonical Sunny character identity reference (golden fur, warm cheeks, rounded ears, friendly eyes, and proportions). Keep all labels and instructions in Compose (never bake text into raster or vector art).

### 2.1 Adaptive Launcher Icon Assets (Implemented Fallback + Studio Master Spec)

- **Current Android Resources**:
  - Adaptive config: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`
  - Background: `app/src/main/res/drawable/ic_launcher_background.xml` (Teal `#00897B` with `#26A69A` safe-area halo)
  - Foreground: `app/src/main/res/drawable/ic_launcher_foreground.xml` (Running Sunny reference inside 66 dp safe zone + `@drawable/ic_launcher_star_trail`)
  - Monochrome silhouette: `app/src/main/res/drawable/ic_launcher_monochrome.xml` (Single-color vector silhouette of running Sunny + star trail inside 66 dp safe zone)
  - Legacy density PNGs: `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher.png` and circular `ic_launcher_round.png`
- **Required Studio Production Files**:

| Asset Purpose | Required File Name | Format & Color | Dimensions / Safe Zone | Alpha | Destination Path |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Master Icon Art | `ic_launcher_sunny_master_1024.png` | PNG-32, sRGB | `1024×1024 px` (face, paws & star trail inside central `624×624 px` / `66 dp` circle) | Full alpha (`0%` background) | `docs/gemini/masters/ic_launcher_sunny_master_1024.png` |
| Adaptive Foreground | `ic_launcher_foreground_sunny.webp` (or vector XML) | WebP Lossless / Vector XML | `432×432 px` (`108×108 dp` @ xxxhdpi; key art inside central `264×264 px` / `66×66 dp`) | Straight/unpremultiplied alpha | `app/src/main/res/drawable-nodpi/ic_launcher_foreground_sunny.webp` |
| Adaptive Background | `ic_launcher_background.xml` | Android Vector XML (`#00897B`) | `108×108 dp` | Opaque `#00897B` | `app/src/main/res/drawable/ic_launcher_background.xml` |
| Monochrome Silhouette | `ic_launcher_monochrome.xml` | Android Vector XML (`#FFFFFFFF` fill) | `108×108 dp` (`24×24` viewport, art within `4..20`) | Transparent background | `app/src/main/res/drawable/ic_launcher_monochrome.xml` |

### 2.2 Garden Scene State Masters (`BARE_SOIL`, `SPROUT`, `BLOOM`)

Keep the horizon line (`78%` from top), terracotta pot center (`72%` from left, `78%` from top), soil mound, and Sunny baseline (`84%` from top) identical across all three states so the child clearly perceives what changed after each step.

| Scene State (`GardenSceneStage`) | Required File Name | Format | Master Dimensions | Small-Screen Crop | Alpha | Destination Path |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `BARE_SOIL` (Stages 0–1) | `bg_garden_scene_bare_soil.webp` | WebP Lossless / PNG-32 | `1600×900 px` (16:9) | `960×540 px` safe crop | Opaque sky/ground or layered PNG | `app/src/main/res/drawable-nodpi/bg_garden_scene_bare_soil.webp` |
| `SPROUT` (Stages 2–3) | `bg_garden_scene_sprout.webp` | WebP Lossless / PNG-32 | `1600×900 px` (16:9) | `960×540 px` safe crop | Opaque sky/ground or layered PNG | `app/src/main/res/drawable-nodpi/bg_garden_scene_sprout.webp` |
| `BLOOM` (Stage 4) | `bg_garden_scene_bloom.webp` | WebP Lossless / PNG-32 | `1600×900 px` (16:9) | `960×540 px` safe crop | Opaque sky/ground or layered PNG | `app/src/main/res/drawable-nodpi/bg_garden_scene_bloom.webp` |

### 2.3 Sunny Mascot Poses & Frame Timing (`SunnyPose`)

All Sunny character frames share the exact same canvas size (`512×512 px`), transparent background, character scale, and **bottom-center foot pivot `(x = 256 px / 0.50, y = 471 px / 0.92)`** so feet never slide or jump between states.

| Pose (`SunnyPose`) | Required Still / Frame Files | Format & Alpha | Dimensions & Pivot | Timing | Reduced Motion Still Equivalent | Destination Path |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `IDLE` | `char_sunny_pose_idle.webp` | WebP Lossless / PNG-32, transparent alpha | `512×512 px`, pivot `(0.50, 0.92)` | Static / default rest pose | `char_sunny_pose_idle.webp` | `app/src/main/res/drawable-nodpi/char_sunny_pose_idle.webp` |
| `SHORT_RUN` | `char_sunny_run_01.webp` .. `char_sunny_run_04.webp` | WebP Lossless / PNG-32, transparent alpha | `512×512 px`, pivot `(0.50, 0.92)` | `700 ms` total travel (`4 × 175 ms` frames) | `char_sunny_pose_idle.webp` (`0 ms` snap) | `app/src/main/res/drawable-nodpi/char_sunny_run_01..04.webp` |
| `WAVE` | `char_sunny_wave_01.webp` .. `char_sunny_wave_02.webp` | WebP Lossless / PNG-32, transparent alpha | `512×512 px`, pivot `(0.50, 0.92)` | `450 ms` total (`2 × 225 ms` frames, then `IDLE`) | `char_sunny_pose_idle.webp` | `app/src/main/res/drawable-nodpi/char_sunny_wave_01..02.webp` |
| `FLAG_PLANT` | `char_sunny_flag_plant_01.webp` .. `char_sunny_flag_plant_02.webp` | WebP Lossless / PNG-32, transparent alpha | `512×512 px`, pivot `(0.50, 0.92)` | `650 ms` total (`325 ms` plant + `325 ms` hold, then `WAVE` -> `IDLE`) | `char_sunny_flag_plant_02.webp` | `app/src/main/res/drawable-nodpi/char_sunny_flag_plant_01..02.webp` |

---

## 3. Voice Recording Manifest & Runtime Contract

### 3.1 Recording Format Requirements
- **Format**: Uncompressed PCM `.wav`, `48 kHz` (or `44.1 kHz`), `16-bit`, Mono, dry studio voice without music, reverb, clipping, or leading silence `> 60 ms`.
- **Destination Path**: `app/src/main/res/raw/<expected_raw_file_name>.wav`
- **Resolution Order in `WonderAudioController`**:
  1. Bundled raw studio recording in `app/src/main/res/raw/` matching `clipId`.
  2. Installed offline device TTS voice (`Locale.US` for English; `Locale("te", "IN")` for Telugu when stage `!= 0`).
  3. If Telugu is selected in Parent Corner (`settings.instructionLanguage == "te"`) for stages `1..4` and neither a bundled Telugu WAV nor an offline Telugu TTS voice is installed, `GardenMission.promptSpecFor` and `WonderAudioController.speakLocalized` fall back to the matching English prompt and English clip ID so the visible on-screen text and spoken prompt always match.

### 3.2 Six-Line Sunny Audition Scripts (`docs/gemini/garden-voice-scripts.csv`)

Both `Calm` (`garden_audition_calm`) and `Playful` (`garden_audition_playful`) audition sets are exposed in Parent Corner (`SubjectLearningControls`) with an immediate `Stop` button (`garden_audition_stop`). They are labeled in UI as fallback device speech until the studio WAV files below are added to `app/src/main/res/raw/`:

| Calm Clip ID | Playful Clip ID | Expected Raw Files (`res/raw/`) | Script (`en-US`) | Target Duration | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `sunny_greeting_calm` | `sunny_greeting_playful` | `voice_sunny_greeting_calm.wav`, `voice_sunny_greeting_playful.wav` | `"Hello! Let's help our garden grow."` | `2.6 s` / `2.4 s` | `UNVERIFIED_AWAITING_HUMAN` |
| `sunny_count_three_calm` | `sunny_count_three_playful` | `voice_sunny_count_three_calm.wav`, `voice_sunny_count_three_playful.wav` | `"Put three seeds in the pot."` | `2.5 s` / `2.3 s` | `UNVERIFIED_AWAITING_HUMAN` |
| `sunny_phonics_s_calm` | `sunny_phonics_s_playful` | `voice_sunny_phonics_s_calm.wav`, `voice_sunny_phonics_s_playful.wav` | `"Seed starts with the letter S. Listen to the first sound: sss, seed."` | `4.2 s` / `4.0 s` | `UNVERIFIED_AWAITING_HUMAN` |
| `sunny_hint_count_calm` | `sunny_hint_count_playful` | `voice_sunny_hint_count_calm.wav`, `voice_sunny_hint_count_playful.wav` | `"Let's try together. Touch one seed at a time as we count."` | `3.8 s` / `3.6 s` | `UNVERIFIED_AWAITING_HUMAN` |
| `sunny_story_sprout_calm` | `sunny_story_sprout_playful` | `voice_sunny_story_sprout_calm.wav`, `voice_sunny_story_sprout_playful.wav` | `"Sunny planted the seeds in soft brown soil. A little rain fell, pat-pat-pat. Soon, a tiny green shoot peeked out."` | `8.5 s` / `8.0 s` | `UNVERIFIED_AWAITING_HUMAN` |
| `sunny_praise_three_seeds_calm` | `sunny_praise_three_seeds_playful` | `voice_sunny_praise_three_seeds_calm.wav`, `voice_sunny_praise_three_seeds_playful.wav` | `"You planted three seeds. Now your garden is ready to grow."` | `3.6 s` / `3.4 s` | `UNVERIFIED_AWAITING_HUMAN` |

### 3.3 Stage & Level-Matched Garden Prompt Clip IDs (`GardenMission` & `VoiceAssetManifest`)

Each stage and Reading/Math level maps to a distinct clip ID so `sunny_count_three` never plays for the 5-seed or 8-seed task:

| Stage | Level | Language | Clip ID | Expected Raw File (`res/raw/`) | Visible & Spoken Script | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| Stage 0 (Read) | `2-3` | `en-US` | `garden_s0_read_2_3_find_s_en` | `voice_garden_s0_read_2_3_find_s_en.wav` | `"Seed starts with S. Find S."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 0 (Read) | `4-5` (idx 0) | `en-US` | `garden_s0_read_4_5_sun_s_en` | `voice_garden_s0_read_4_5_sun_s_en.wav` | `"Build SUN. Find S."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 0 (Read) | `4-5` (idx 1) | `en-US` | `garden_s0_read_4_5_sun_u_en` | `voice_garden_s0_read_4_5_sun_u_en.wav` | `"Build SUN. Find U."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 0 (Read) | `4-5` (idx 2) | `en-US` | `garden_s0_read_4_5_sun_n_en` | `voice_garden_s0_read_4_5_sun_n_en.wav` | `"Build SUN. Find N."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 0 (Read) | `5-6` (idx 0) | `en-US` | `garden_s0_read_5_6_word_sun_en` | `voice_garden_s0_read_5_6_word_sun_en.wav` | `"Plants need sunlight. Find SUN."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 0 (Read) | `5-6` (idx 1) | `en-US` | `garden_s0_read_5_6_word_seed_en` | `voice_garden_s0_read_5_6_word_seed_en.wav` | `"We plant a seed. Find SEED."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 1 (Math) | `2-3` (3 seeds) | `en-US` | `sunny_count_three` | `voice_sunny_count_three.wav` | `"Put 3 seeds in the pot."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 1 (Math) | `4-5` (5 seeds) | `en-US` | `garden_s1_math_4_5_count_5_en` | `voice_garden_s1_math_4_5_count_5_en.wav` | `"Put 5 seeds in the pot."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 1 (Math) | `5-6` (8 seeds) | `en-US` | `garden_s1_math_5_6_add_8_en` | `voice_garden_s1_math_5_6_add_8_en.wav` | `"Plant three seeds, then five more. How many altogether?"` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 2 (Need) | All | `en-US` | `garden_s2_need_light_en` | `voice_garden_s2_need_light_en.wav` | `"What gives the plant light?"` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 3 (Story) | All | `en-US` | `garden_s3_story_water_en` | `voice_garden_s3_story_water_en.wav` | `"Sunny planted seeds. The soil is dry. What should Sunny give the seeds?"` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 4 (Done) | All | `en-US` | `garden_s4_complete_en` | `voice_garden_s4_complete_en.wav` | `"Your garden is growing! You helped Sunny read, count and care for a plant."` | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 1 (Math) | `2-3` (3 seeds) | `te-IN` | `garden_count_3_te` | `voice_garden_count_3_te.wav` | `"కుండలో 3 విత్తనాలు వేయి."` (Fallback: `sunny_count_three`) | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 1 (Math) | `4-5` (5 seeds) | `te-IN` | `garden_count_5_te` | `voice_garden_count_5_te.wav` | `"కుండలో 5 విత్తనాలు వేయి."` (Fallback: `garden_s1_math_4_5_count_5_en`) | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 1 (Math) | `5-6` (8 seeds) | `te-IN` | `garden_count_8_te` | `voice_garden_count_8_te.wav` | `"మూడు విత్తనాలు, తరువాత మరో ఐదు విత్తనాలు నాటు. మొత్తం ఎన్ని?"` (Fallback: `garden_s1_math_5_6_add_8_en`) | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 2 (Need) | All | `te-IN` | `garden_light_te` | `voice_garden_light_te.wav` | `"మొక్కకు వెలుతురు ఏది ఇస్తుంది?"` (Fallback: `garden_s2_need_light_en`) | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 3 (Story) | All | `te-IN` | `garden_story_te` | `voice_garden_story_te.wav` | `"సన్నీ విత్తనాలు నాటింది. మట్టి పొడిగా ఉంది. విత్తనాలకు ఏమి ఇవ్వాలి?"` (Fallback: `garden_s3_story_water_en`) | `UNVERIFIED_AWAITING_HUMAN` |
| Stage 4 (Done) | All | `te-IN` | `garden_complete_te` | `voice_garden_complete_te.wav` | `"నీ తోట పెరుగుతోంది! మొక్కను పెంచడానికి సన్నీకి సహాయం చేశావు."` (Fallback: `garden_s4_complete_en`) | `UNVERIFIED_AWAITING_HUMAN` |

### 3.4 Separate Phonics Clip IDs (Letter Name vs. Isolated Sound vs. Segmented Blend vs. Whole Word)

| Phonics Type | Clip ID | Expected Raw File (`res/raw/`) | Delivery Notes | Status |
| :--- | :--- | :--- | :--- | :--- |
| Letter Name (`S`) | `phonics_s_name` | `voice_letter_s_name.wav` | `"Letter S... Sun."` (Distinct from `/s/` sound) | `UNVERIFIED_AWAITING_HUMAN` |
| Isolated Sound (`/s/`) | `phonics_s_isolated_sound` | `voice_phonics_s_pure.wav` (bundled) | Continuous schwa-free `/s/` (`"ssss"`, no `"suh"`) | `BUNDLED_RAW_UNREVIEWED_STUDIO` |
| Isolated Sound (`/m/`) | `phonics_m_isolated_sound` | `voice_phonics_m_pure.wav` (bundled) | Continuous schwa-free `/m/` (`"mmmm"`, no `"muh"`) | `BUNDLED_RAW_UNREVIEWED_STUDIO` |
| Short Vowels (`/a/`, `/e/`, `/i/`, `/o/`, `/u/`) | `phonics_a..u_short_vowel` | `voice_phonics_{a,e,i,o,u}_short.wav` | Pure short vowel phoneme (`æ`, `ɛ`, `ɪ`, `ɒ`, `ʌ`) | `UNVERIFIED_AWAITING_HUMAN` |
| Segmented Blend (`S-U-N`) | `phonics_blend_sun_segmented` | `voice_phonics_blend_sun_segmented.wav` | `"sss... uh... nnn... sun."` | `UNVERIFIED_AWAITING_HUMAN` |
| Segmented Blend (`S-EE-D`) | `phonics_blend_seed_segmented` | `voice_phonics_blend_seed_segmented.wav` | `"sss... eee... d... seed."` | `UNVERIFIED_AWAITING_HUMAN` |
| Whole Word (`SUN`) | `phonics_word_sun_whole` | `voice_phonics_word_sun_whole.wav` | `"sun"` | `UNVERIFIED_AWAITING_HUMAN` |
| Whole Word (`SEED`) | `phonics_word_seed_whole` | `voice_phonics_word_seed_whole.wav` | `"seed"` | `UNVERIFIED_AWAITING_HUMAN` |
