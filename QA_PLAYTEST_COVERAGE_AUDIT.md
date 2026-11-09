# Little Wonder World — Full Play-Test Coverage & QA Audit (`1.6.0-wonder`)

- **Application ID**: `com.aistudio.littlewonderworld.wvkqzp`
- **Catalog Scope**: 6 Learning Lands • 27 Themed Worlds • 257 Selectable Activities + 157 Real World Media & Wonder Reels entries
- **Target Profiles**:
  - **Phone Landscape**: `1080 × 1920` @ `420 dpi` (`~731dp × 411dp`)
  - **Tablet Landscape**: `1200 × 1920` @ `240 dpi` (`1200dp × 800dp`)

## Summary of Stage 0–4 Repairs

1. **Stage 1 — Tracing Studio (`w04_tracing_1..12`, `w15_science_8`)**:
   - Replaced fragile title-substring routing with `TracingStudioDeckRegistry`, providing explicit multi-step decks for `w04_tracing_1` (`A–H`), `w04_tracing_2` (`I–P`), `w04_tracing_3` (`Q–Z`), `w04_tracing_4` (`a–m`), `w04_tracing_5` (`n–z`), `w04_tracing_6` (`1–5`), `w04_tracing_7` (`6–10` with `10` modeled as two separate strokes for `1` and `0`), `w04_tracing_8` (`◯, △, □, ☆, ♡`), `w04_tracing_9` (Straight Lines), `w04_tracing_10` (Curves & Waves), `w04_tracing_11` (Zig-Zag Mountain Paths), and `w04_tracing_12` (First Words `CAT`, `SUN`, `DOG`, `STAR`).
   - Modeled every glyph with explicit ordered `GlyphStrokeSpec` strokes, start/finish markers, direction cues, and pen-lift requirements.
   - Unified the coverage meter and feedback banner to read from one `TracingEvaluationResult` per gesture, rejecting tap clusters, reversed strokes, straight-line shortcuts across closed shapes/zigzags, and off-path scrubbing.
   - Kept `Assisted Tap-Along` clearly labeled as an assisted mode without fabricating a 92% accuracy score.
   - Added explicit `Retry`, `Skip`, and `Next Glyph` controls with guaranteed `>= 48dp` height on phone landscape and explicit deck completion rather than silent modulo wrap.

2. **Stage 2 — Shared Layouts & Phone Landscape Clipping Repairs**:
   - **`WorldActivitiesScreen` (All 37 Catalog Pages)**: Restructured the 3x3 catalog cards so every card displays a compact top badge row (`#index`, emoji, distinct `traceGlyph` range badge, favorite heart, stars) and a dedicated multi-line title region (`maxLines = 2`), eliminating the `0px` title clipping across all 37 pages on phone landscape.
   - **`ColoringCanvasEngine` (`w05_coloring_1..10`)**: Rebuilt the left tool column with a compact 4x2 color swatch grid and 48dp `Save Art` (`coloring_save_button`) button so all tools fit on phone landscape without clipping. Implemented normalized multi-color stroke serialization (`encodeStrokesPayload` / `decodeStrokesPayload`) and automatic restoration when reopening saved artwork.
   - **`SortingBasketsEngine` & `WaterLabScience` (`w15_science_1..5`)**: Added `BoxWithConstraints` adaptive item card widths (`68.dp..112.dp`) and 6-item science decks so `sort_item_water_anchor` and all sorting items have full visible width on phone landscape.
   - **`HomeScreen` & `RealWorldAndReelsScreen` Top Bars**: Made top navigation strips responsive so `home_parent_corner_button` (`Parents`) and `mode_wonder_reels_button` (`Wonder Reels`) have full visible width and `48.dp` height on phone landscape.

3. **Stage 3 — Title-to-Content Alignment Across All 257 Activities**:
   - Replaced modulo/shared item generators with explicit, title-accurate decks for `w01_sorting_1..9`, `w02_animals_1..11`, `w03_memory_1..9`, `w07_phonics_1..12`, `w08_math_1..10`, `w09_stories_1..8`, `w10_puzzles_1..8`, `w11_music_1..8`, `w12_vehicles_1..8`, `w13_coding_1..8`, `w14_nature_1..8`, `w15_science_1..8`, `w16_pretend_1..8`, `w17_feelings_1..8`, `w18_skills_1..8`, `w19_building_1..8`, `w20_safety_1..9`, `w21_movement_1..6`, `w23_festivals_1..6`, `w24_chef_1..8`, `w25_flashcards_1..16` (all 16 distinct 6-card decks), and `w27_rhymes_1..30`.

---

## Complete 257-Activity Coverage Matrix

| # | Activity ID | World ID | Title | Engine | Deck / Content Verification | Status |
|---|---|---|---|---|---|---|
| 1 | `w01_sorting_1` | `w01_sorting` | Primary Color Baskets | `SORTING_BASKETS` | Red, Blue, Yellow baskets (6 items) | PASS |
| 2 | `w01_sorting_2` | `w01_sorting` | Shape Sorter Workshop | `SORTING_BASKETS` | Circle, Square, Star slots (6 items) | PASS |
| 3 | `w01_sorting_3` | `w01_sorting` | Big, Medium & Little Size Sort | `SORTING_BASKETS` | Small, Medium, Large boxes (6 items) | PASS |
| 4 | `w01_sorting_4` | `w01_sorting` | Pattern Train Baskets | `SORTING_BASKETS` | Sun-Moon, Star-Heart, Garden pattern cars | PASS |
| 5 | `w01_sorting_5` | `w01_sorting` | Yummy Fruit vs. Veggie Baskets | `SORTING_BASKETS` | Orchard Fruit vs. Garden Veggie (6 items) | PASS |
| 6 | `w01_sorting_6` | `w01_sorting` | Land, Sea & Sky Habitats | `SORTING_BASKETS` | Land, Sea, Sky habitats (6 items) | PASS |
| 7 | `w01_sorting_7` | `w01_sorting` | Toy Box vs. Bookshelf Sort | `SORTING_BASKETS` | Toy Box vs. Bookshelf (6 items) | PASS |
| 8 | `w01_sorting_8` | `w01_sorting` | Sunny Day vs. Rainy Day Gear | `SORTING_BASKETS` | Sunny Day Gear vs. Rainy Day Gear (6 items) | PASS |
| 9 | `w01_sorting_9` | `w01_sorting` | Garden & Builder Tools | `SORTING_BASKETS` | Garden Shed vs. Builder Bench (6 items) | PASS |
| 10 | `w02_animals_1` | `w02_animals` | Listen & Find Safari | `ANIMAL_SAFARI_SOUNDS` | Lion, Dolphin, Elephant, Owl calls | PASS |
| 11 | `w02_animals_2` | `w02_animals` | Sound Safari Savanna | `ANIMAL_SAFARI_SOUNDS` | Giraffe, Zebra, Elephant, Lion | PASS |
| 12 | `w02_animals_3` | `w02_animals` | Friendly Farmyard Calls | `ANIMAL_SAFARI_SOUNDS` | Cow, Duck, Sheep, Horse | PASS |
| 13 | `w02_animals_4` | `w02_animals` | Who Is My Baby? | `ANIMAL_SAFARI_SOUNDS` | Chick, Puppy, Kitten, Froglet baby matches | PASS |
| 14 | `w02_animals_5` | `w02_animals` | Cozy Animal Homes | `ANIMAL_SAFARI_SOUNDS` | Hive, Nest, Cave Den, Coral Reef | PASS |
| 15 | `w02_animals_6` | `w02_animals` | Mystery Animal Shadows | `ANIMAL_SAFARI_SOUNDS` | Giraffe, Elephant, Kangaroo, Owl silhouettes | PASS |
| 16 | `w02_animals_7` | `w02_animals` | Muddy Paw & Footprint Trail | `ANIMAL_SAFARI_SOUNDS` | Paw, Webbed, Hoof, Dinosaur tracks | PASS |
| 17 | `w02_animals_8` | `w02_animals` | What Do Animals Eat? | `ANIMAL_SAFARI_SOUNDS` | Panda/Bamboo, Squirrel/Acorn, Bunny/Carrot, Monkey/Banana | PASS |
| 18 | `w02_animals_9` | `w02_animals` | Ocean & Reef Singers | `ANIMAL_SAFARI_SOUNDS` | Dolphin, Whale, Sea Turtle, Harbor Seal | PASS |
| 19 | `w02_animals_10` | `w02_animals` | Interactive Animal Encyclopedia | `ANIMAL_SAFARI_SOUNDS` | Penguin, Koala, Flamingo, River Otter facts | PASS |
| 20 | `w02_animals_11` | `w02_animals` | Object & Vehicle Sound Cards | `ANIMAL_SAFARI_SOUNDS` | Handbell, Locomotive, Clock, Fire Engine | PASS |
| 21 | `w03_memory_1` | `w03_memory` | Forest Animal Pairs | `MEMORY_MATCH` | Fox, Bear, Owl, Deer, Bunny, Squirrel | PASS |
| 22 | `w03_memory_2` | `w03_memory` | Rainbow Color Splash Pairs | `MEMORY_MATCH` | Red, Blue, Yellow, Green, Purple, Orange | PASS |
| 23 | `w03_memory_3` | `w03_memory` | Orchard Fruit Basket Pairs | `MEMORY_MATCH` | Apple, Banana, Strawberry, Orange, Grapes, Watermelon | PASS |
| 24 | `w03_memory_4` | `w03_memory` | ABC Letter Twins | `MEMORY_MATCH` | Uppercase A–F paired with lowercase a–f | PASS |
| 25 | `w03_memory_5` | `w03_memory` | 1-2-3 Number Dot Pairs | `MEMORY_MATCH` | Numerals 1–6 | PASS |
| 26 | `w03_memory_6` | `w03_memory` | Zooming Vehicle Pairs | `MEMORY_MATCH` | Train, Fire Truck, Sailboat, Airplane, Helicopter, Rocket | PASS |
| 27 | `w03_memory_7` | `w03_memory` | Happy Feelings Faces | `MEMORY_MATCH` | Happy, Calm, Surprised, Sleepy, Silly, Kind | PASS |
| 28 | `w03_memory_8` | `w03_memory` | Picnic Treat Pairs | `MEMORY_MATCH` | Sandwich, Muffin, Juice, Pretzel, Cheese, Pie | PASS |
| 29 | `w03_memory_9` | `w03_memory` | Playroom Toy Pairs | `MEMORY_MATCH` | Teddy, Kite, Drum, Blocks, Ball, Paint | PASS |
| 30–41 | `w04_tracing_1..12` | `w04_tracing` | Alphabet, Numbers, Shapes, Lines & Words Tracing | `TRACING_STUDIO` | 12 explicit multi-stroke decks (`A–H`, `I–P`, `Q–Z`, `a–m`, `n–z`, `1–5`, `6–10`, `◯△☆`, `｜―`, `⌒〰`, `⚡⛰`, `CAT/SUN/DOG/STAR`) | PASS |
| 42–51 | `w05_coloring_1..10` | `w05_coloring` | My Sketch Pad & 9 Themed Coloring Pages | `COLORING_CANVAS` | Non-clipping 48dp Save Art + normalized multi-color stroke persistence + subject outlines | PASS |
| 52–63 | `w06_balloons_1..12` | `w06_balloons` | Balloon Pop Adventure (12 themes) | `BALLOON_POP` | 12 distinct themed balloon pools | PASS |
| 64–75 | `w07_phonics_1..12` | `w07_phonics` | Alphabet & Phonics (including A–M, N–Z, Vowels, CVC, 52-Step Part 1 & 2) | `PHONICS_WORD_QUIZ` | 12 explicit phonics/letter/word decks with saved step checkpoints | PASS |
| 76–85 | `w08_math_1..10` | `w08_math` | Numbers & Math Counter (1–10, Addition, Compare, Shapes, 1–50, 51–100) | `NUMBERS_MATH_COUNTER` | 10 explicit math modes & item decks | PASS |
| 86–93 | `w09_stories_1..8` | `w09_stories` | Read-Aloud Stories (8 3-page books) | `STORYBOOK_READER` | Title-specific 3-page stories + Read Again | PASS |
| 94–101 | `w10_puzzles_1..8` | `w10_puzzles` | Puzzles & Logic (8 activities) | `SORTING_BASKETS` | 8 distinct peg/jigsaw/shadow/odd-one-out/symmetry decks | PASS |
| 102–109 | `w11_music_1..8` | `w11_music` | Music & Rhythm Studio (Piano, Xylophone, Drums, Echo, Birds, Bells, Detective, High/Low) | `MUSIC_INSTRUMENTS` | All 8 modes + 3-note Echo Beats imitation | PASS |
| 110–117 | `w12_vehicles_1..8` | `w12_vehicles` | Vehicles & Helpers (8 activities) | `SORTING_BASKETS` / `ANIMAL_SAFARI_SOUNDS` | 8 distinct vehicle/traffic/helper/rescue decks | PASS |
| 118–125 | `w13_coding_1..8` | `w13_coding` | Early Coding Arrow Mazes (8 activities) | `CODING_ARROW_MAZE` | 8 distinct protagonists, obstacles, and goals | PASS |
| 126–133 | `w14_nature_1..8` | `w14_nature` | Plants & Nature (8 activities) | `SORTING_BASKETS` / `PRETEND_STEP_CARE` | Flower parts, seed planting, insects, trees, seasons, harvest, butterfly cycle, compost | PASS |
| 134–141 | `w15_science_1..8` | `w15_science` | Water Lab, Day/Night Sky, Planets, Weather, Dino Dig, Cycles, Constellation | `WATER_LAB_SCIENCE` / `SORTING_BASKETS` / `PRETEND_STEP_CARE` / `TRACING_STUDIO` | 8 distinct science lessons; all 6 items per deck visible on phone landscape | PASS |
| 142–149 | `w16_pretend_1..8` | `w16_pretend` | Pretend Play & Puppy Wash (8 activities) | `PRETEND_STEP_CARE` | 8 bespoke 3-step Demonstrate/Practice/Try scripts | PASS |
| 150–157 | `w17_feelings_1..8` | `w17_feelings` | Feelings & Friendship (8 activities) | `PHONICS_WORD_QUIZ` | Expressions, sharing, kindness, calm breaths | PASS |
| 158–165 | `w18_skills_1..8` | `w18_skills` | Everyday Skills (Toothbrushing, Handwashing, Tidying, Dressing, Backpack, Table, Shoes, Bedtime) | `PRETEND_STEP_CARE` | 8 bespoke 3-step life skill scripts | PASS |
| 166–173 | `w19_building_1..8` | `w19_building` | Building & Design (8 activities) | `SORTING_BASKETS` | Tower, bridge, cottage, road, sandcastle, garden, track, rocket | PASS |
| 174–182 | `w20_safety_1..9` | `w20_safety` | Body & Safety (Knock Knock Door Safety, Crosswalk, Senses, Dentist, Kitchen, Helmet/Seatbelt) | `STORYBOOK_READER` | Safe choice validated (`correctChoiceIndex = 0`); unsafe distractor triggers gentle retry | PASS |
| 183–188 | `w21_movement_1..6` | `w21_movement` | Movement & Mindfulness (6 activities) | `PRETEND_STEP_CARE` | Bunny hop, freeze dance, sky stretch, flamingo balance, butterfly breath, Simon says | PASS |
| 189–195 | `w22_arcade_1..7` | `w22_arcade` | Wonder Mini Arcade (7 untimed activities) | `BALLOON_POP` | Bubbles, stars, butterflies, reef fish, treasure, clouds, falling fruit | PASS |
| 196–201 | `w23_festivals_1..6` | `w23_festivals` | Festivals & Seasons (6 activities) | `SORTING_BASKETS` | Four seasons, birthday table, kindness gift, lanterns, harvest, winter | PASS |
| 202–209 | `w24_chef_1..8` | `w24_chef` | Little Chef (Fruit Salad, Pizzeria, Smoothie, Sandwich, Oatmeal, Picnic, Food Groups, Organizer) | `PRETEND_STEP_CARE` / `SORTING_BASKETS` | 6 bespoke 3-step recipes + 2 kitchen/food sorting decks | PASS |
| 210–225 | `w25_flashcards_1..16` | `w25_flashcards` | Wonder Flashcards (16 distinct 6-card decks) | `FLASHCARD_EXPLORER` | 96 total cards across 16 distinct decks (no modulo recycling) | PASS |
| 226–227 | `w26_family_1..2` | `w26_family` | Meet My Family Cards & Who's Who? Quiz | `FLASHCARD_EXPLORER` | Family cards + empty state gate when `< 2` photos | PASS |
| 228–257 | `w27_rhymes_1..30` | `w27_rhymes` | 30 Three-Page Nursery Storybooks | `STORYBOOK_READER` | 30 bespoke 3-page rhyming books with Read Again | PASS |
