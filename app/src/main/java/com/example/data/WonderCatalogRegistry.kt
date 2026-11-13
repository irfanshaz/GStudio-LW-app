package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.model.ActivityConfig
import com.example.model.EngineType
import com.example.model.GameItemOption
import com.example.model.LandId
import com.example.model.StoryPageSpec
import com.example.model.WorldSpec

/**
 * Source-of-truth registry for Little Wonder World:
 * - 6 Learning Lands
 * - 27 Themed Worlds
 * - Exactly 257 selectable, playable Activity Configurations (including 30 3-page Rhyme & Storytime books)
 */
object WonderCatalogRegistry {

    val worlds: List<WorldSpec> = listOf(
        // LEARNING LAND (Worlds 4, 7, 8, 9)
        WorldSpec("w04_tracing", 4, LandId.LEARNING_LAND, "Alphabet Tracing", "Upper/lowercase letters, numbers, shapes & lines", "✏️", EngineType.TRACING_STUDIO, Color(0xFFFF6B6B)),
        WorldSpec("w07_phonics", 7, LandId.LEARNING_LAND, "Alphabet & Phonics", "Letter Hunt, First Sounds, Missing ABC & 52-Step Journey", "🔤", EngineType.PHONICS_WORD_QUIZ, Color(0xFFEF4444)),
        WorldSpec("w08_math", 8, LandId.LEARNING_LAND, "Numbers & Math", "Count stars, apples in basket, fish, addition & 1–100", "🔢", EngineType.NUMBERS_MATH_COUNTER, Color(0xFFF97316)),
        WorldSpec("w09_stories", 9, LandId.LEARNING_LAND, "Read-Aloud Stories", "Illustrated short stories, page turning & word pictures", "📖", EngineType.STORYBOOK_READER, Color(0xFFEC4899)),

        // THINKING FOREST (Worlds 1, 3, 10, 13)
        WorldSpec("w01_sorting", 1, LandId.THINKING_FOREST, "Color & Shape Sorting", "Colors, shapes, sizes, habitats, foods, weather & tools", "🧺", EngineType.SORTING_BASKETS, Color(0xFF10B981)),
        WorldSpec("w03_memory", 3, LandId.THINKING_FOREST, "Memory Matching", "Animals, colors, fruit, letters, numbers & feelings", "🃏", EngineType.MEMORY_MATCH, Color(0xFF059669)),
        WorldSpec("w10_puzzles", 10, LandId.THINKING_FOREST, "Puzzles & Logic", "Peg puzzles, shadows, odd-one-out & shape sequences", "🧩", EngineType.SORTING_BASKETS, Color(0xFF14B8A6)),
        WorldSpec("w13_coding", 13, LandId.THINKING_FOREST, "Early Coding", "Arrow sequences, robot routes, mazes & ordered steps", "🤖", EngineType.CODING_ARROW_MAZE, Color(0xFF0D9488)),

        // DISCOVERY ISLAND (Worlds 2, 14, 15, 21)
        WorldSpec("w02_animals", 2, LandId.DISCOVERY_ISLAND, "Animal Sounds & Safari", "Listen & Find, Sound Safari, homes, babies & footprints", "🦁", EngineType.ANIMAL_SAFARI_SOUNDS, Color(0xFF06B6D4)),
        WorldSpec("w14_nature", 14, LandId.DISCOVERY_ISLAND, "Plants & Nature", "Flowers, seeds, insects, seasons & butterfly lifecycle", "🌻", EngineType.SORTING_BASKETS, Color(0xFF0EA5E9)),
        WorldSpec("w15_science", 15, LandId.DISCOVERY_ISLAND, "Science, Water Lab & Space", "Water Lab float/sink, planets, weather & dinosaurs", "🚀", EngineType.WATER_LAB_SCIENCE, Color(0xFF0284C7)),
        WorldSpec("w21_movement", 21, LandId.DISCOVERY_ISLAND, "Movement & Mindfulness", "Gentle dance, stretch, animal moves & star breaths", "🧘", EngineType.PRETEND_STEP_CARE, Color(0xFF38BDF8)),

        // CREATIVE GARDEN (Worlds 5, 11, 19, 27)
        WorldSpec("w05_coloring", 5, LandId.CREATIVE_GARDEN, "Coloring Studio", "My Sketch Pad, flower, ocean, bear, dinosaur & castle", "🎨", EngineType.COLORING_CANVAS, Color(0xFF8B5CF6)),
        WorldSpec("w11_music", 11, LandId.CREATIVE_GARDEN, "Music & Rhythm", "Piano, xylophone, drums, echo beats & bird orchestra", "🎹", EngineType.MUSIC_INSTRUMENTS, Color(0xFF7C3AED)),
        WorldSpec("w19_building", 19, LandId.CREATIVE_GARDEN, "Building & Design", "Towers, bridges, homes, castles, tracks & rockets", "🧱", EngineType.SORTING_BASKETS, Color(0xFFA855F7)),
        WorldSpec("w27_rhymes", 27, LandId.CREATIVE_GARDEN, "Rhyme & Storytime", "30 original three-page narrated nursery storybooks", "🎵", EngineType.STORYBOOK_READER, Color(0xFF9333EA)),

        // LITTLE LIFE TOWN (Worlds 12, 16, 17, 18, 20, 24)
        WorldSpec("w12_vehicles", 12, LandId.LITTLE_LIFE_TOWN, "Vehicles & Helpers", "Fire trucks, trains, traffic lights & community helpers", "🚒", EngineType.ANIMAL_SAFARI_SOUNDS, Color(0xFFF97316)),
        WorldSpec("w16_pretend", 16, LandId.LITTLE_LIFE_TOWN, "Pretend Play & Puppy Wash", "Puppy Wash, doctor, baker, firefighter, vet & astronaut", "🐶", EngineType.PRETEND_STEP_CARE, Color(0xFFEA580C)),
        WorldSpec("w17_feelings", 17, LandId.LITTLE_LIFE_TOWN, "Feelings & Friendship", "Recognize expressions, sharing, kindness & calm breaths", "💛", EngineType.PHONICS_WORD_QUIZ, Color(0xFFF59E0B)),
        WorldSpec("w18_skills", 18, LandId.LITTLE_LIFE_TOWN, "Everyday Skills", "Brushing teeth, handwashing, tidying, routines & manners", "🪥", EngineType.PRETEND_STEP_CARE, Color(0xFFD97706)),
        WorldSpec("w20_safety", 20, LandId.LITTLE_LIFE_TOWN, "Body & Safety", "Knock Knock! Door Safety, road crossing, senses & health", "🚪", EngineType.STORYBOOK_READER, Color(0xFFDC2626)),
        WorldSpec("w24_chef", 24, LandId.LITTLE_LIFE_TOWN, "Little Chef", "Fruit salad, pizza, soup, cake, breakfast & food groups", "🍕", EngineType.PRETEND_STEP_CARE, Color(0xFFE11D48)),

        // ADVENTURE FUNFAIR (Worlds 6, 22, 23, 25, 26)
        WorldSpec("w06_balloons", 6, LandId.ADVENTURE_FUNFAIR, "Balloon Pop Adventure", "Pop letters, numbers, colors, shapes, animals & fruit", "🎈", EngineType.BALLOON_POP, Color(0xFFEAB308)),
        WorldSpec("w22_arcade", 22, LandId.ADVENTURE_FUNFAIR, "Wonder Mini Arcade", "Untimed Bubble Pop, catch stars, butterflies & treasure", "🫧", EngineType.BALLOON_POP, Color(0xFFFACC15)),
        WorldSpec("w23_festivals", 23, LandId.ADVENTURE_FUNFAIR, "Festivals & Seasons", "Seasonal ordering, celebrations, gifts & kindness tales", "🎉", EngineType.SORTING_BASKETS, Color(0xFFF59E0B)),
        WorldSpec("w25_flashcards", 25, LandId.ADVENTURE_FUNFAIR, "Wonder Flashcards", "16 rich picture-word decks with spoken phonics & sounds", "🗂️", EngineType.FLASHCARD_EXPLORER, Color(0xFF3B82F6)),
        WorldSpec("w26_family", 26, LandId.ADVENTURE_FUNFAIR, "My Family Album", "Meet My Family photo cards & Who's Who? recognition quiz", "👨‍👩‍👧", EngineType.FLASHCARD_EXPLORER, Color(0xFFEC4899))
    )

    val allActivities: List<ActivityConfig> by lazy { buildFull257Catalog() }

    fun getWorldsForLand(landId: LandId): List<WorldSpec> =
        worlds.filter { it.landId == landId }

    fun getActivitiesForWorld(worldId: String): List<ActivityConfig> =
        allActivities.filter { it.worldId == worldId }

    fun getActivityById(activityId: String): ActivityConfig? =
        allActivities.find { it.id == activityId }

    fun getToyBoxShortcuts(): List<ActivityConfig> {
        val preferredIds = listOf(
            "w06_balloons_1",
            "w11_music_1",
            "w05_coloring_1",
            "w01_sorting_1",
            "w16_pretend_1",
            "w15_science_1",
            "w04_tracing_1",
            "w02_animals_1"
        )
        return preferredIds.mapNotNull { id -> allActivities.find { it.id == id } }
    }

    private fun buildFull257Catalog(): List<ActivityConfig> {
        val list = mutableListOf<ActivityConfig>()

        // 1. World 01: Color & Shape Sorting (9 activities)
        val sortingThemes = listOf(
            Triple("Primary Color Baskets", "Sort red, blue, and yellow treasures into matching baskets", "🎨"),
            Triple("Shape Sorter Workshop", "Match circles, squares, stars, and hearts to their wooden slots", "⭐"),
            Triple("Big, Medium & Little Size Sort", "Place tiny, medium, and giant objects into the right size box", "📏"),
            Triple("Pattern Train Baskets", "Complete sunny color and shape patterns in the forest cart", "🚂"),
            Triple("Yummy Fruit vs. Veggie Baskets", "Sort sweet fruits and crunchy garden vegetables", "🍎"),
            Triple("Land, Sea & Sky Habitats", "Help forest, ocean, and sky friends find their home", "🌊"),
            Triple("Toy Box vs. Bookshelf Sort", "Tidy up plush toys and storybooks into their shelves", "🧸"),
            Triple("Sunny Day vs. Rainy Day Gear", "Sort sunglasses, umbrellas, rainboots, and sunhats", "☔"),
            Triple("Garden & Builder Tools", "Sort watering cans, hammers, paintbrushes, and gears", "🧰")
        )
        sortingThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w01_sorting_${idx + 1}",
                    worldId = "w01_sorting",
                    landId = LandId.THINKING_FOREST,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.SORTING_BASKETS,
                    instructionText = "Drag or tap each item into its matching basket!",
                    showMeDemoHint = "Watch how the first item slides right into the matching basket!",
                    items = createSortingItemsForIndex(idx),
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 2. World 02: Animal Sounds & Safari (11 activities)
        val animalThemes = listOf(
            Triple("Listen & Find Safari", "Hear the animal call and tap the matching friend", "🦁"),
            Triple("Sound Safari Savanna", "Explore lions, elephants, zebras, and giraffes", "🐘"),
            Triple("Friendly Farmyard Calls", "Cows, ducks, sheep, horses, and piglets on the farm", "🐄"),
            Triple("Who Is My Baby?", "Match mama and papa animals with their little babies", "🐣"),
            Triple("Cozy Animal Homes", "Match bees to hives, birds to nests, and bears to dens", "🪺"),
            Triple("Mystery Animal Shadows", "Spot which animal matches the forest silhouette", "🐾"),
            Triple("Muddy Paw & Footprint Trail", "Follow tracks in the sand to discover who walked by", "👣"),
            Triple("What Do Animals Eat?", "Match bamboo to pandas, acorns to squirrels, and hay to horses", "🥕"),
            Triple("Ocean & Reef Singers", "Dolphins, whales, sea turtles, and playful seals", "🐬"),
            Triple("Interactive Animal Encyclopedia", "Explore animal facts and authentic calls", "📘"),
            Triple("Object & Vehicle Sound Cards", "Hear bells, trains, clocks, and musical chimes", "🔔")
        )
        animalThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w02_animals_${idx + 1}",
                    worldId = "w02_animals",
                    landId = LandId.DISCOVERY_ISLAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.ANIMAL_SAFARI_SOUNDS,
                    instructionText = "Listen closely and tap the friend making that sound!",
                    showMeDemoHint = "Tap the golden speaker to hear the call, then tap the glowing card!",
                    items = createAnimalItemsForIndex(idx),
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 3. World 03: Memory Matching (9 activities)
        val memoryThemes = listOf(
            Triple("Forest Animal Pairs", "Flip cards to match friendly animal twins", "🦊"),
            Triple("Rainbow Color Splash Pairs", "Match bright color splashes and paint stars", "🌈"),
            Triple("Orchard Fruit Basket Pairs", "Match apples, bananas, berries, and oranges", "🍓"),
            Triple("ABC Letter Twins", "Match uppercase letter cards with their lowercase twin", "🔤"),
            Triple("1-2-3 Number Dot Pairs", "Match friendly numerals and counting stars", "🔢"),
            Triple("Zooming Vehicle Pairs", "Match trains, fire engines, boats, and planes", "🚂"),
            Triple("Happy Feelings Faces", "Match smiling, surprised, sleepy, and silly faces", "😊"),
            Triple("Picnic Treat Pairs", "Match sandwiches, muffins, juice, and fruit", "🧁"),
            Triple("Playroom Toy Pairs", "Match blocks, teddies, drums, and kites", "🪁")
        )
        memoryThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w03_memory_${idx + 1}",
                    worldId = "w03_memory",
                    landId = LandId.THINKING_FOREST,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.MEMORY_MATCH,
                    instructionText = "Tap two cards to find matching pairs!",
                    showMeDemoHint = "Peek at two cards and remember where each picture hides!",
                    items = createMemoryItemsForIndex(idx)
                )
            )
        }

        // 4. World 04: Alphabet Tracing (12 activities with distinct emojis & glyph preview badges)
        data class TraceThemeSpec(
            val title: String,
            val sub: String,
            val emoji: String,
            val glyphBadge: String,
            val word: String
        )
        val tracingThemes = listOf(
            TraceThemeSpec("Uppercase A to H Tracing", "Trace big sunny letters A through H", "🅰️", "A–H", "Apple"),
            TraceThemeSpec("Uppercase I to P Tracing", "Trace big sunny letters I through P", "🌙", "I–P", "Igloo"),
            TraceThemeSpec("Uppercase Q to Z Tracing", "Trace big sunny letters Q through Z", "☀️", "Q–Z", "Queen"),
            TraceThemeSpec("Lowercase a to m Tracing", "Gentle curved lowercase letters a through m", "🐜", "a–m", "ant"),
            TraceThemeSpec("Lowercase n to z Tracing", "Gentle curved lowercase letters n through z", "🦋", "n–z", "nest"),
            TraceThemeSpec("Numbers 1 to 5 Tracing", "Follow the stars to write numbers 1 to 5", "1️⃣", "1–5", "One Star"),
            TraceThemeSpec("Numbers 6 to 10 Tracing", "Follow the stars to write numbers 6 to 10", "🔟", "6–10", "Six Loops"),
            TraceThemeSpec("Sunny Shapes Tracing", "Trace circles, triangles, squares, stars, and hearts", "⭐", "◯△☆", "Circle"),
            TraceThemeSpec("Pre-Writing Straight Lines", "Top-to-bottom and side-to-side steady paths", "📏", "｜―", "Tall Line"),
            TraceThemeSpec("Rainbow Curves & Waves", "Smooth ocean waves and rainbow arches", "🌊", "⌒〰", "Rainbow Arch"),
            TraceThemeSpec("Zig-Zag Mountain Paths", "Follow the mountain peaks from start to finish", "⛰️", "⚡⛰", "Zig-Zag"),
            TraceThemeSpec("First Words Star Trace", "Trace CAT, SUN, DOG, and STAR letter by letter", "🐱", "CAT", "CAT")
        )
        tracingThemes.forEachIndexed { idx, spec ->
            list.add(
                ActivityConfig(
                    id = "w04_tracing_${idx + 1}",
                    worldId = "w04_tracing",
                    landId = LandId.LEARNING_LAND,
                    indexInWorld = idx + 1,
                    title = spec.title,
                    subtitle = spec.sub,
                    emoji = spec.emoji,
                    engineType = EngineType.TRACING_STUDIO,
                    instructionText = "Start at the green star and trace along the glowing path!",
                    showMeDemoHint = "Watch the magic finger trace from the green star to the finish!",
                    traceGlyph = spec.glyphBadge,
                    traceWordHint = spec.word,
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 5. World 05: Coloring Studio (10 activities, My Sketch Pad first)
        val coloringThemes = listOf(
            Triple("My Sketch Pad", "Free-draw canvas with rainbow brushes, stamps & gallery save", "🖌️"),
            Triple("Sunny Meadow Flower", "Color petals, leaves, and fluttering butterflies", "🌸"),
            Triple("Coral Reef Ocean", "Color friendly dolphins, fish, and sea stars", "🐬"),
            Triple("Cuddly Forest Bear", "Paint a honey bear under tall pine trees", "🐻"),
            Triple("Zooming Red Cruiser Car", "Color wheels, windows, and racing stripes", "🚗"),
            Triple("Tropical Fruit Bowl", "Color apples, bananas, grapes, and watermelon", "🍉"),
            Triple("Gentle Baby Dinosaur", "Color a smiling brontosaurus in a fern valley", "🦕"),
            Triple("Starry Rocket Space", "Paint rockets, ringed planets, and shooting stars", "🚀"),
            Triple("Magical Sky Rainbow", "Fill every arch of the rainbow with bright colors", "🌈"),
            Triple("Fairytale Storybook Castle", "Paint towers, flags, and a royal garden bridge", "🏰")
        )
        coloringThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w05_coloring_${idx + 1}",
                    worldId = "w05_coloring",
                    landId = LandId.CREATIVE_GARDEN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.COLORING_CANVAS,
                    instructionText = "Pick a bright color and brush size to paint your masterpiece!",
                    showMeDemoHint = "Tap any color circle on the palette, then glide your finger across the canvas!",
                    traceWordHint = title,
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 6. World 06: Balloon Pop Adventure (12 activities)
        val balloonThemes = listOf(
            Triple("ABC Letter Balloons", "Pop floating balloons and hear every letter name", "🎈"),
            Triple("Rainbow Color Balloons", "Find and pop the color Sunny calls out", "🌈"),
            Triple("1-to-20 Number Balloons", "Pop counting balloons as they float into the sky", "🔢"),
            Triple("Floating Shape Balloons", "Pop star, heart, circle, and diamond balloons", "⭐"),
            Triple("Animal Call Balloons", "Pop the balloon carrying the roaring or chirping friend", "🦁"),
            Triple("Wonder Buddy Balloons", "Pop balloons with Sunny, Pip, Bramble, and Coral", "🦉"),
            Triple("Sweet Orchard Fruit Balloons", "Pop floating strawberries, apples, and bananas", "🍓"),
            Triple("Picnic Treat Balloons", "Pop yummy snacks floating past the clouds", "🥨"),
            Triple("Zooming Vehicle Balloons", "Pop helicopters, trains, sailboats, and rockets", "🚁"),
            Triple("Sunny & Snowy Weather Balloons", "Pop suns, raindrops, snowflakes, and rainbows", "☀️"),
            Triple("Toy Box Treasure Balloons", "Pop kites, drums, blocks, and teddy bears", "🧸"),
            Triple("Phonics Clue Balloons", "Listen to the first sound and pop the matching letter balloon", "🔤")
        )
        balloonThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w06_balloons_${idx + 1}",
                    worldId = "w06_balloons",
                    landId = LandId.ADVENTURE_FUNFAIR,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.BALLOON_POP,
                    instructionText = "Tap the floating balloons to pop them and hear their sound!",
                    showMeDemoHint = "Tap a rising balloon with your finger—pop!",
                    items = createBalloonItemsForIndex(idx),
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 7. World 07: Alphabet & Phonics (12 activities including 52-step ABC journey)
        val phonicsThemes = listOf(
            Triple("Letter Hunt Safari", "Spot the big target letter among friendly tiles", "🔍"),
            Triple("First Sounds (/a/ to /m/)", "Hear the pure phonetic sound and pick its letter A–M", "🗣️"),
            Triple("First Sounds (/n/ to /z/)", "Match /n/, /p/, /r/, /s/, /t/ to real letter tiles N–Z", "🔊"),
            Triple("Little Letters Match", "Connect uppercase mama letters to lowercase baby letters", "🔡"),
            Triple("Rhyme Time Pairs", "Match CAT with HAT, STAR with CAR, and BEE with TREE", "🎶"),
            Triple("Missing ABC Bridge", "Fill in the missing letter to complete the alphabet bridge", "🌉"),
            Triple("Choo-Choo Letter Train", "Load the train cars in alphabetical order", "🚂"),
            Triple("Word Starts Picture Quiz", "Which letter starts Apple, Bear, Dolphin, and Sun?", "🍎"),
            Triple("Vowel Sound Explorer", "Explore short vowel sounds A, E, I, O, and U", "🌟"),
            Triple("Three-Letter Word Builder", "Blend C-A-T, D-O-G, S-U-N, and B-U-S", "🧱"),
            Triple("52-Step ABC Uppercase & Lowercase Journey (Part 1)", "Guided steps A–M with saved checkpoint", "🗺️"),
            Triple("52-Step ABC Uppercase & Lowercase Journey (Part 2)", "Guided steps N–Z with saved checkpoint", "🏆")
        )
        phonicsThemes.forEachIndexed { idx, (title, sub, emoji) ->
            val glyphBadge = when (idx) {
                1 -> "A–M"
                2 -> "N–Z"
                3 -> "Aa"
                8 -> "AEIOU"
                9 -> "CAT"
                10 -> "A–M"
                11 -> "N–Z"
                else -> "ABC"
            }
            list.add(
                ActivityConfig(
                    id = "w07_phonics_${idx + 1}",
                    worldId = "w07_phonics",
                    landId = LandId.LEARNING_LAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.PHONICS_WORD_QUIZ,
                    instructionText = "Listen to the letter or sound and tap the matching big tile!",
                    showMeDemoHint = "Tap Listen to hear the phonetic sound, then tap the matching letter tile!",
                    traceGlyph = glyphBadge,
                    items = createPhonicsItemsForIndex(idx)
                )
            )
        }

        // 8. World 08: Numbers & Math (10 activities)
        val mathThemes = listOf(
            Triple("Count the Twinkling Stars", "Tap each star one by one as we count aloud", "⭐"),
            Triple("Put Apples in the Basket", "Place the exact number of crisp red apples into the basket", "🍎"),
            Triple("Count Coral Reef Fish", "Tap swimming fish one by one up to 10", "🐠"),
            Triple("Number Hunt Meadow", "Find the hidden numeral in the garden", "🔢"),
            Triple("Two-Group Picture Addition", "Count two friendly groups together (e.g., 2 + 1 = 3)", "➕"),
            Triple("Number Train Order (1–10)", "Tap the numbers in order from smallest to biggest", "🚂"),
            Triple("More or Fewer Compare", "Which basket has more yummy berries?", "⚖️"),
            Triple("Shape Sides Counter", "Count the corners on triangles, squares, and stars", "📐"),
            Triple("1–50 Stepping Stones Journey", "Hop along numbers 1 to 50 with saved progress", "🐾"),
            Triple("51–100 Big Kid Counter Journey", "Explore big numbers up to 100 at your own pace", "💯")
        )
        mathThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w08_math_${idx + 1}",
                    worldId = "w08_math",
                    landId = LandId.LEARNING_LAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.NUMBERS_MATH_COUNTER,
                    instructionText = "Tap the objects to count them, then pick the matching number!",
                    showMeDemoHint = "Let's count each object together: one, two, three!",
                    items = createMathItemsForIndex(idx)
                )
            )
        }

        // 9. World 09: Read-Aloud Stories (8 activities)
        val storyThemes = listOf(
            Triple("Sunny's Lost Magnifying Glass", "Turn pages and help Sunny search the meadow", "🔍"),
            Triple("Pip Owl's Starlight Lantern", "A gentle night story about sharing light with friends", "🏮"),
            Triple("Bramble's Rainbow Carrot Garden", "Watering seeds and watching colors grow", "🥕"),
            Triple("Coral Otter's River Boat", "Building a floating raft with woodland friends", "🛶"),
            Triple("The Little Red Fire Engine's Bell", "Helping a kitten down from a tall oak tree", "🚒"),
            Triple("First Day at Wonder Preschool", "Making friends, hanging backpacks, and singing", "🎒"),
            Triple("Story Sequencing: Baking Berry Pie", "Pick what happens first, next, and last", "🥧"),
            Triple("Picture-Word Story Match", "Tap glowing words to hear them read in the scene", "📖")
        )
        storyThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w09_stories_${idx + 1}",
                    worldId = "w09_stories",
                    landId = LandId.LEARNING_LAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.STORYBOOK_READER,
                    instructionText = "Listen to the story page and tap the interactive choice!",
                    showMeDemoHint = "Tap Listen to hear the page read aloud, then tap Next Page!",
                    storyPages = createReadAloudStoryPages(idx, title, emoji),
                    isEndlessMilestone = false
                )
            )
        }

        // 10. World 10: Puzzles & Logic (8 activities)
        val puzzleThemes = listOf(
            Triple("Wooden Animal Peg Puzzle", "Snap lion, elephant, and owl pieces into carved slots", "🧩"),
            Triple("Four-Piece Picture Jigsaw", "Assemble cheerful animal and vehicle scenes", "🖼️"),
            Triple("Shadow Silhouette Match", "Match tools, fruits, and animals to dark outlines", "👤"),
            Triple("Find the Missing Piece", "Spot which puzzle tile completes the picture", "🔎"),
            Triple("Odd-One-Out Explorer", "Three things belong together—spot the silly extra one!", "🤔"),
            Triple("Smallest to Largest Lineup", "Order bears, trees, and stars by size", "📊"),
            Triple("What Comes Next? Sequences", "Complete Sun-Moon-Sun-Moon and Red-Blue-Red patterns", "🔁"),
            Triple("Half & Half Symmetry Match", "Join the left and right halves of butterflies and leaves", "🦋")
        )
        puzzleThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w10_puzzles_${idx + 1}",
                    worldId = "w10_puzzles",
                    landId = LandId.THINKING_FOREST,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.SORTING_BASKETS,
                    instructionText = "Match each puzzle piece to its target slot!",
                    showMeDemoHint = "Tap or drag the glowing puzzle piece into its matching outline!",
                    items = createPuzzleItemsForIndex(idx)
                )
            )
        }

        // 11. World 11: Music & Rhythm (8 activities)
        val musicThemes = listOf(
            Triple("Rainbow Grand Piano", "Large responsive keys with polyphonic chord support", "🎹"),
            Triple("Wooden Toy Xylophone", "Distinct resonant mallet chimes with slide-to-play", "🎼"),
            Triple("Jungle Rhythm Drums", "Bongo, snare, bass drum, and sparkling cymbal pads", "🥁"),
            Triple("Echo Beats Imitation", "Listen to Buddy's 3-note melody and tap it back", "👂"),
            Triple("Singing Bird Orchestra", "Tap perched songbirds to harmonize a meadow tune", "🐦"),
            Triple("Golden Bell Choir", "Ring colourful handbells from low to high pitch", "🔔"),
            Triple("Instrument Sound Detective", "Hear piano, drum, xylophone, or bell and spot the instrument", "🎺"),
            Triple("High Note vs. Low Note", "Discover high bird trills and deep whale songs", "🎵")
        )
        musicThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w11_music_${idx + 1}",
                    worldId = "w11_music",
                    landId = LandId.CREATIVE_GARDEN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.MUSIC_INSTRUMENTS,
                    instructionText = "Tap or slide across the keys and drums to make music!",
                    showMeDemoHint = "Watch the keys light up and play a happy melody!",
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 12. World 12: Vehicles & Helpers (8 activities)
        val vehicleThemes = listOf(
            Triple("Road, Rail, Sea & Sky", "Sort cars, trains, sailboats, and airplanes", "✈️"),
            Triple("Red, Yellow, Green Traffic Light", "Learn when to Stop, Wait, and Go safely", "🚦"),
            Triple("Community Helpers & Tools", "Match firefighters, doctors, chefs, and builders", "👩‍🚒"),
            Triple("Town Garage Tune-Up", "Match tires, fuel hoses, and car wash brushes", "🔧"),
            Triple("Mail Truck Delivery Route", "Deliver letters to the right numbered house", "📬"),
            Triple("Sirens, Horns & Bells", "Listen and identify emergency and travel vehicles", "🚒"),
            Triple("Build a Rescue Truck", "Assemble cab, ladder, wheels, and siren light", "🚑"),
            Triple("Harbor Boat & Train Bridge", "Help boats sail under and trains cross over", "🚢")
        )
        vehicleThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w12_vehicles_${idx + 1}",
                    worldId = "w12_vehicles",
                    landId = LandId.LITTLE_LIFE_TOWN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = if (idx % 2 == 0) EngineType.SORTING_BASKETS else EngineType.ANIMAL_SAFARI_SOUNDS,
                    instructionText = "Explore community helpers and zooming vehicles!",
                    showMeDemoHint = "Tap the helper or vehicle that matches the clue!",
                    items = createVehicleItemsForIndex(idx)
                )
            )
        }

        // 13. World 13: Early Coding (8 activities)
        val codingThemes = listOf(
            Triple("Robot Arrow Steps", "Tap Up, Down, Left, Right arrows to guide Beep-Bot", "🤖"),
            Triple("Sunny's Honey Jar Maze", "Plan 3 arrow steps to reach the golden honey pot", "🍯"),
            Triple("Coral's River Stone Path", "Guide the rocket around asteroids to the moon", "🚀"),
            Triple("Morning Routine Step Order", "Guide the puppy step by step to the bone", "🐶"),
            Triple("Looping Color Code", "Repeat a 2-step arrow pattern to help the bee reach the flower", "🐝"),
            Triple("Left & Right Treasure Map", "Follow directional clues so the train reaches the station", "🚂"),
            Triple("Fix the Buggy Path", "Guide the submarine along the reef to the treasure crown", "🚤"),
            Triple("Rocket Launch Countdown Code", "Guide the rescue bot to help the little kitty", "🚒")
        )
        codingThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w13_coding_${idx + 1}",
                    worldId = "w13_coding",
                    landId = LandId.THINKING_FOREST,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.CODING_ARROW_MAZE,
                    instructionText = "Tap the direction arrows to guide your buddy to the goal!",
                    showMeDemoHint = "Tap Right, Right, then Down to reach the goal!"
                )
            )
        }

        // 14. World 14: Plants & Nature (8 activities)
        val natureThemes = listOf(
            Triple("Parts of a Flower", "Roots, stem, green leaves, and sunny petals", "🌻"),
            Triple("Plant a Tiny Seed", "Soil, water drops, sunshine, and sprouting stem", "🌱"),
            Triple("Friendly Garden Insects", "Ladybugs, bumblebees, butterflies, and ants", "🐞"),
            Triple("Tall Trees & Leaves", "Oak acorns, maple leaves, and pine cones", "🌳"),
            Triple("Four Seasons Tree", "Spring blossoms, summer sun, autumn leaves, winter snow", "🍂"),
            Triple("Vegetable Patch Harvest", "Pull carrots, pick tomatoes, and water lettuce", "🥕"),
            Triple("Butterfly Lifecycle", "Egg on a leaf, hungry caterpillar, chrysalis, butterfly!", "🦋"),
            Triple("Compost & Garden Care", "Sort leaves and apple cores to nourish the soil", "♻️")
        )
        natureThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w14_nature_${idx + 1}",
                    worldId = "w14_nature",
                    landId = LandId.DISCOVERY_ISLAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = if (idx in listOf(1, 5, 6)) EngineType.PRETEND_STEP_CARE else EngineType.SORTING_BASKETS,
                    instructionText = "Explore how plants, seeds, and butterflies grow!",
                    showMeDemoHint = "Follow each gentle nature step in order!",
                    items = createNatureItemsForIndex(idx)
                )
            )
        }

        // 15. World 15: Science, Water Lab & Space (8 activities with activity-accurate engines & items)
        val scienceThemes = listOf(
            Triple("Water Lab: Float or Sink?", "Predict and drop objects into the water tank!", "💧"),
            Triple("Water Lab: Boats & Stones", "Test heavy coins, hollow boats, sponges, and apples", "⛵"),
            Triple("Day Sky vs. Night Sky", "Sort sunshine, clouds, rainbows, moon, and stars", "🌙"),
            Triple("Solar System Planet Parade", "Explore the Sun, Earth, Mars, and ringed Saturn", "🪐"),
            Triple("Weather Watcher Lab", "Thermometer, rain clouds, wind pinwheels, and snow", "🌦️"),
            Triple("Gentle Dinosaur Dig", "Brush sand to uncover T-Rex, Triceratops, and Stegosaurus", "🦖"),
            Triple("Frog & Raindrop Cycles", "See how puddles form clouds and tadpoles grow legs", "🐸"),
            Triple("Star Constellation Connect", "Connect glowing stars in the night sky", "✨")
        )
        scienceThemes.forEachIndexed { idx, (title, sub, emoji) ->
            val engine = when (idx) {
                0, 1 -> EngineType.WATER_LAB_SCIENCE
                5, 6 -> EngineType.PRETEND_STEP_CARE
                7 -> EngineType.TRACING_STUDIO
                else -> EngineType.SORTING_BASKETS
            }
            list.add(
                ActivityConfig(
                    id = "w15_science_${idx + 1}",
                    worldId = "w15_science",
                    landId = LandId.DISCOVERY_ISLAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = engine,
                    instructionText = when (engine) {
                        EngineType.WATER_LAB_SCIENCE -> "Will it float on top or sink to the bottom? Predict and drop it in!"
                        EngineType.TRACING_STUDIO -> "Connect the glowing stars to trace the night sky constellation!"
                        EngineType.PRETEND_STEP_CARE -> "Follow each hands-on science discovery step!"
                        else -> "Sort each science discovery into its matching sky, planet, or weather group!"
                    },
                    showMeDemoHint = "Watch how each science object matches its group!",
                    traceGlyph = if (idx == 7) "☆" else "A",
                    traceWordHint = if (idx == 7) "Star Constellation" else title,
                    items = createScienceItemsForIndex(idx),
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 16. World 16: Pretend Play & Puppy Wash (8 activities)
        val pretendThemes = listOf(
            Triple("Puppy Wash Salon", "Brush mud, scrub bubbly soap, rinse water & towel dry!", "🐶"),
            Triple("Kind Doctor Checkup", "Listen to heartbeats, check temperature & place a star bandage", "🩺"),
            Triple("Sunny Tea Party Host", "Pour warm berry tea, share scones & clink teacups", "🫖"),
            Triple("Happy Cake Baker", "Mix flour and eggs, bake golden layers & swirl frosting", "🎂"),
            Triple("Friendly Grocery Shopkeeper", "Scan fruits, pack reusable bags & hand over the receipt", "🛒"),
            Triple("Baby Doll Lullaby Care", "Offer a warm bottle, tuck the blanket & hum a lullaby", "👶"),
            Triple("Garden Watering Helper", "Loosen soil, tip the watering can & watch flowers bloom", "🪴"),
            Triple("Fix-It Tool Workbench", "Measure wooden boards, tap pegs & tighten bolts", "🧰")
        )
        pretendThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w16_pretend_${idx + 1}",
                    worldId = "w16_pretend",
                    landId = LandId.LITTLE_LIFE_TOWN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.PRETEND_STEP_CARE,
                    instructionText = "Follow the demonstration, guided practice, and independent play steps!",
                    showMeDemoHint = "Watch Step 1, then rub or tap across the play area to complete each care action!",
                    featuredInToyBox = idx == 0,
                    isEndlessMilestone = false
                )
            )
        }

        // 17. World 17: Feelings & Friendship (8 activities)
        val feelingsThemes = listOf(
            Triple("Spot the Feeling Face", "Recognize Happy, Sad, Surprised, Calm, and Proud", "😊"),
            Triple("Taking Turns & Sharing Toys", "Choose the kind way to share blocks and crayons", "🤝"),
            Triple("Helping a Friend Who Fell", "Offer a hand, a tissue, and a kind word", "💛"),
            Triple("Starfish Calming Breaths", "Trace the star slowly as we breathe in and out", "🌟"),
            Triple("What Makes Us Smile?", "Match situations with how our heart feels", "🌈"),
            Triple("Listening Ears & Kind Words", "Practise saying Please, Thank You, and Excuse Me", "👂"),
            Triple("Cozy Calm-Down Corner", "Pick a hug, deep breath, or quiet book when frustrated", "🧸"),
            Triple("Friendship Band Parade", "Celebrate team play with all four Learning Buddies", "🎈")
        )
        feelingsThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w17_feelings_${idx + 1}",
                    worldId = "w17_feelings",
                    landId = LandId.LITTLE_LIFE_TOWN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.PHONICS_WORD_QUIZ,
                    instructionText = "Look at the expression or story and tap the kindest choice!",
                    showMeDemoHint = "Tap the smiling or caring choice that helps our friend feel better!",
                    items = createFeelingsItemsForIndex(idx)
                )
            )
        }

        // 18. World 18: Everyday Skills (8 activities)
        val skillsThemes = listOf(
            Triple("Sparkle Toothbrushing", "Demonstrate, guided practice & independent brushing!", "🪥"),
            Triple("Bubbly Handwashing Steps", "Wet hands, scrub soap for 20 seconds, rinse & dry", "🧼"),
            Triple("Tidy Up the Playroom", "Put blocks in bins, books on shelves, and sweep the floor", "🧺"),
            Triple("Get Dressed for the Weather", "Put on shirt, pants, socks, and jacket in order", "🧥"),
            Triple("Pack My School Backpack", "Pack storybook, crayons, and water bottle neatly", "🎒"),
            Triple("Set the Dinner Table", "Place mat, plate, cup, napkin, fork, and spoon", "🍽️"),
            Triple("Tie & Buckle My Shoes", "Slip heel in, pull straps snug, and fasten tight", "👟"),
            Triple("Bedtime Routine Checklist", "Pajamas, toothbrush, storybook, and sweet dreams", "🛌")
        )
        skillsThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w18_skills_${idx + 1}",
                    worldId = "w18_skills",
                    landId = LandId.LITTLE_LIFE_TOWN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.PRETEND_STEP_CARE,
                    instructionText = "Practise each everyday life skill step by step!",
                    showMeDemoHint = "First watch the demonstration, then guide your finger to complete each step!",
                    isEndlessMilestone = false
                )
            )
        }

        // 19. World 19: Building & Design (8 activities)
        val buildingThemes = listOf(
            Triple("Rainbow Block Tower", "Stack wide blocks on the bottom and tall spires on top", "🧱"),
            Triple("River Bridge Builder", "Place stone arches so the little train can cross", "🌉"),
            Triple("Cozy Cottage Home", "Assemble walls, windows, front door, and chimney roof", "🏡"),
            Triple("Town Road & Roundabout", "Connect curved and straight road tiles", "🛣️"),
            Triple("Fairytale Sandcastle", "Scoop buckets, place turrets, and add seashells", "🏰"),
            Triple("Flower Garden Designer", "Place stone paths, benches, fountains, and tulip beds", "🌷"),
            Triple("Wooden Train Track Loop", "Join wooden rails into a figure-eight loop", "🛤️"),
            Triple("Starship Rocket Assembly", "Snap boosters, cabin porthole, and nose cone", "🚀")
        )
        buildingThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w19_building_${idx + 1}",
                    worldId = "w19_building",
                    landId = LandId.CREATIVE_GARDEN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.SORTING_BASKETS,
                    instructionText = "Tap or drag each building block into the blueprint!",
                    showMeDemoHint = "Start with the foundation piece and snap each part into place!",
                    items = createBuildingItemsForIndex(idx)
                )
            )
        }

        // 20. World 20: Body & Safety (9 activities including Knock Knock! Door Safety)
        val safetyThemes = listOf(
            Triple("Knock Knock! Door Safety", "Keep the door closed and call a trusted grown-up!", "🚪"),
            Triple("Stop, Look & Listen Road Safety", "Hold a grown-up's hand and look both ways at the crosswalk", "🚸"),
            Triple("Head, Shoulders, Knees & Toes", "Learn body parts with cheerful movement prompts", "🧒"),
            Triple("Our Five Amazing Senses", "Eyes see, ears hear, nose smells, tongue tastes, hands touch", "👀"),
            Triple("Happy Dentist & Strong Teeth", "Choose water and crunchy apples over sticky sweets", "🦷"),
            Triple("Morning, Noon & Night Clock", "Match daily activities to sun positions and clock hours", "🕒"),
            Triple("Trusted Helpers in Town", "Who can help if you feel lost? Parents, teachers & officers", "🛡️"),
            Triple("Safe vs. Hot & Sharp Choices", "Recognize what only grown-ups should touch in the kitchen", "⚠️"),
            Triple("Helmet & Seatbelt Click!", "Always buckle up in the car and wear a helmet on bikes", "⛑️")
        )
        safetyThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w20_safety_${idx + 1}",
                    worldId = "w20_safety",
                    landId = LandId.LITTLE_LIFE_TOWN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.STORYBOOK_READER,
                    instructionText = "Listen to the safety story and choose the safe action!",
                    showMeDemoHint = "Remember: keep the door closed and always get a trusted grown-up!",
                    storyPages = createSafetyStoryPages(title, emoji, idx),
                    isEndlessMilestone = false
                )
            )
        }

        // 21. World 21: Movement & Mindfulness (6 activities)
        val movementThemes = listOf(
            Triple("Hop Like a Meadow Bunny", "Bend knees low, hold up bunny ears, and hop softly!", "🐰"),
            Triple("Sunny Freeze Dance", "Wiggle and hop while music plays, then freeze like a statue!", "💃"),
            Triple("Reach for the Sky Stretch", "Stretch tall like a giraffe and float arms down like leaves", "🦒"),
            Triple("Flamingo One-Foot Balance", "Practice steady balance with gentle posture cues", "🦩"),
            Triple("Rainbow Butterfly Breaths", "Inhale slowly opening butterfly wings, exhale softly", "🦋"),
            Triple("Simon Says Action Play", "Listen closely for 'Simon says' before clapping or stomping", "👂")
        )
        movementThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w21_movement_${idx + 1}",
                    worldId = "w21_movement",
                    landId = LandId.DISCOVERY_ISLAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.PRETEND_STEP_CARE,
                    instructionText = "Follow each gentle movement or breathing step together!",
                    showMeDemoHint = "Breathe in slowly as the circle grows, and breathe out softly!",
                    isEndlessMilestone = false
                )
            )
        }

        // 22. World 22: Wonder Mini Arcade (8 activities, trimmed to 7 for 257 total)
        val arcadeThemes = listOf(
            Triple("Untimed Rainbow Bubble Pop", "Endless shimmering soap bubbles flowing along the meadow path", "🫧"),
            Triple("Catch the Falling Stars", "Tap glowing shooting stars along the twilight road", "🌟"),
            Triple("Meadow Butterfly Catcher", "Tap fluttering butterflies gliding along the garden trail", "🦋"),
            Triple("Coral Reef Fish Spotter", "Tap friendly clownfish and sea turtles swimming along the reef stream", "🐠"),
            Triple("Sunken Treasure Chest Tap", "Open clam shells and chests along the sandy ocean path", "💎"),
            Triple("Cloud Hopper Maze", "Pop fluffy sky clouds floating across the rainbow highway", "☁️"),
            Triple("Falling Fruit Basket Catch", "Tap ripe apples, pears, and berries rolling along the orchard lane", "🍏"),
            Triple("Hidden Forest Friends", "Tap rustling bushes to play peek-a-boo with buddies", "🦊")
        )
        arcadeThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w22_arcade_${idx + 1}",
                    worldId = "w22_arcade",
                    landId = LandId.ADVENTURE_FUNFAIR,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.BALLOON_POP,
                    instructionText = "Tap the flowing wonders along the road at your own pace—no timer!",
                    showMeDemoHint = "Watch the wonders flow along the winding road and tap them to pop!",
                    items = createArcadeItemsForIndex(idx)
                )
            )
        }

        // 23. World 23: Festivals & Seasons (6 activities)
        val festivalThemes = listOf(
            Triple("Spring, Summer, Autumn, Winter", "Order the four seasons and their weather treasures", "🌸"),
            Triple("Birthday Party Table Setup", "Match plates, party hats, balloons, and fruit cake", "🎂"),
            Triple("Wrap a Kindness Gift", "Choose paper, ribbon bow, and a handmade card", "🎁"),
            Triple("Lantern & Lights Festival", "Light warm paper lanterns and hear celebratory chimes", "🏮"),
            Triple("Harvest Sharing Basket", "Pack apples, corn, and pumpkins for neighbors", "🎃"),
            Triple("Winter Snowflake Celebration", "Match mittens, cocoa mugs, and paper snowflakes", "❄️")
        )
        festivalThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w23_festivals_${idx + 1}",
                    worldId = "w23_festivals",
                    landId = LandId.ADVENTURE_FUNFAIR,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.SORTING_BASKETS,
                    instructionText = "Sort and celebrate seasonal traditions and kindness!",
                    showMeDemoHint = "Drag or tap each festive item into its matching celebration basket!",
                    items = createFestivalItemsForIndex(idx)
                )
            )
        }

        // 24. World 24: Little Chef (8 activities)
        val chefThemes = listOf(
            Triple("Rainbow Fruit Salad Bowl", "Wash, slice, and toss strawberries, kiwi, and mango", "🍓"),
            Triple("Pizzeria Veggie Topper", "Spread tomato sauce, sprinkle cheese & add veggies", "🍕"),
            Triple("Berry Smoothie Blender", "Blend strawberries, blueberries, banana, and milk", "🥤"),
            Triple("Healthy Sandwich Stacker", "Layer whole-grain bread, lettuce, cheese, and tomato", "🥪"),
            Triple("Morning Oatmeal Decorator", "Top warm oatmeal with blueberry eyes and banana smile", "🥣"),
            Triple("Sunny Picnic Basket Packer", "Spread the blanket and pack sandwiches, apples, and water", "🧺"),
            Triple("Healthy Food Groups Sorter", "Sort fruits, vegetables, and grains into healthy plates", "🥗"),
            Triple("Little Chef Kitchen Organizer", "Put pots, wooden spoons, and bowls in their cupboards", "👩‍🍳")
        )
        chefThemes.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w24_chef_${idx + 1}",
                    worldId = "w24_chef",
                    landId = LandId.LITTLE_LIFE_TOWN,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = if (idx < 6) EngineType.PRETEND_STEP_CARE else EngineType.SORTING_BASKETS,
                    instructionText = "Follow the recipe steps to prepare a healthy treat!",
                    showMeDemoHint = "Tap each ingredient in order and stir the bowl!",
                    items = createChefSortingItemsForIndex(idx)
                )
            )
        }

        // 25. World 25: Wonder Flashcards (16 distinct 6-card decks)
        val flashcardDecks = listOf(
            Triple("Deck 1: Vehicles & Sirens", "Fire engine, train, ambulance, boat, plane & tractor", "🚒"),
            Triple("Deck 2: Orchard Fruits", "Apple, banana, strawberry, mango, orange & grapes", "🍎"),
            Triple("Deck 3: Garden Vegetables", "Carrot, broccoli, tomato, corn, peas & pumpkin", "🥕"),
            Triple("Deck 4: Everyday Foods", "Bread, cheese, milk, soup, oatmeal & rice", "🍞"),
            Triple("Deck 5: Community Helpers", "Doctor, firefighter, teacher, chef, farmer & pilot", "👩‍⚕️"),
            Triple("Deck 6: Family Words", "Mother, father, sister, brother, grandma & grandpa", "👨‍👩‍👧"),
            Triple("Deck 7: Around the Home", "Bed, table, lamp, window, door & bookshelf", "🏠"),
            Triple("Deck 8: My Body Parts", "Hands, feet, eyes, ears, nose & smile", "🖐️"),
            Triple("Deck 9: Weather & Sky", "Sun, rain, rainbow, cloud, snow & wind", "🌈"),
            Triple("Deck 10: Musical Instruments", "Piano, xylophone, drum, guitar, bell & flute", "🎹"),
            Triple("Deck 11: Feelings & Emotions", "Happy, calm, surprised, sleepy, proud & kind", "😊"),
            Triple("Deck 12: Colors & Shapes", "Red circle, blue square, yellow star & green heart", "🔷"),
            Triple("Deck 13: Places in Town", "Park, library, school, bakery, fire station & harbor", "🏞️"),
            Triple("Deck 14: Fun Opposites", "Big/little, fast/slow, up/down, hot/cold & day/night", "↔️"),
            Triple("Deck 15: Feathered Birds", "Owl, eagle, penguin, parrot, flamingo & robin", "🦉"),
            Triple("Deck 16: Friendly Farm Animals", "Cow, horse, sheep, duck, goat & rooster", "🐄")
        )
        flashcardDecks.forEachIndexed { idx, (title, sub, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w25_flashcards_${idx + 1}",
                    worldId = "w25_flashcards",
                    landId = LandId.ADVENTURE_FUNFAIR,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = EngineType.FLASHCARD_EXPLORER,
                    instructionText = "Flip through picture cards, tap Listen, or try the quick quiz!",
                    showMeDemoHint = "Tap the speaker button to hear the word and fun fact!",
                    items = createFlashcardDeckItems(idx)
                )
            )
        }

        // 26. World 26: My Family Album (2 activities)
        list.add(
            ActivityConfig(
                id = "w26_family_1",
                worldId = "w26_family",
                landId = LandId.ADVENTURE_FUNFAIR,
                indexInWorld = 1,
                title = "Meet My Family Cards",
                subtitle = "Browse family photos, hear names & celebrate loved ones",
                emoji = "📸",
                engineType = EngineType.FLASHCARD_EXPLORER,
                instructionText = "Tap Hear Name or flip through your private family photo cards!",
                showMeDemoHint = "Grown-ups can add up to 16 family photos in Parent Corner!"
            )
        )
        list.add(
            ActivityConfig(
                id = "w26_family_2",
                worldId = "w26_family",
                landId = LandId.ADVENTURE_FUNFAIR,
                indexInWorld = 2,
                title = "Who's Who? Family Quiz",
                subtitle = "Listen to the name and tap the matching family member!",
                emoji = "💝",
                engineType = EngineType.FLASHCARD_EXPLORER,
                instructionText = "Who is Sunny calling? Tap their smiling photo!",
                showMeDemoHint = "Requires at least 2 family cards—or try with our friendly sample family!"
            )
        )

        // 27. World 27: Rhyme & Storytime (30 original three-page narrated stories)
        val thirtyRhymes = listOf(
            "Twinkle Little Wonder Star" to "🌟",
            "Row Your Cozy River Boat" to "🛶",
            "Sunny Had a Little Lamb" to "🐑",
            "Wheels on the Wonder Bus" to "🚌",
            "Five Little Speckled Frogs" to "🐸",
            "Baa Baa Golden Wool" to "🧶",
            "Itsy Bitsy Garden Spider" to "🕷️",
            "Hickory Dickory Meadow Clock" to "🕰️",
            "Old MacSunny's Happy Farm" to "🚜",
            "Humpty Dumpty's Pillow Wall" to "🥚",
            "Pat-a-Cake Baker's Joy" to "🧁",
            "Hey Diddle Cat & Fiddle" to "🎻",
            "Little Bo-Peep's Fluffy Sheep" to "🐑",
            "Jack & Jill's Water Pail" to "🪣",
            "Five Little Ducks Went Swimming" to "🦆",
            "Rain Rain Water the Flowers" to "🌧️",
            "One Two Buckle My Shoe" to "👟",
            "Star Light Star Bright" to "✨",
            "Muffin Man of Wonder Lane" to "🧁",
            "Mulberry Bush Morning Dance" to "🌿",
            "Polly Put the Kettle On" to "🫖",
            "Little Boy Blue's Meadow Horn" to "📯",
            "Sing a Song of Sunbeams" to "☀️",
            "Hush Little Cub Don't You Cry" to "🦁",
            "Ten In the Cozy Bed" to "🛏️",
            "Once I Caught a Fish Alive" to "🐟",
            "Teddy Bear Turn Around" to "🧸",
            "Open Shut Them Clapping Hands" to "👏",
            "I'm a Little Teapot Sprout" to "🍵",
            "Goodnight Moonlit Wonder World" to "🌙"
        )
        thirtyRhymes.forEachIndexed { idx, (title, emoji) ->
            list.add(
                ActivityConfig(
                    id = "w27_rhymes_${idx + 1}",
                    worldId = "w27_rhymes",
                    landId = LandId.CREATIVE_GARDEN,
                    indexInWorld = idx + 1,
                    title = "${idx + 1}. $title",
                    subtitle = "3-page illustrated & narrated nursery storybook",
                    emoji = emoji,
                    engineType = EngineType.STORYBOOK_READER,
                    instructionText = "Listen to the 3-page nursery story and tap each page's picture clue!",
                    showMeDemoHint = "Tap Listen to hear the rhyme read aloud, then turn the page!",
                    storyPages = createNurseryRhymePages(idx, title, emoji),
                    isEndlessMilestone = false
                )
            )
        }

        // Strict 257 selectable activities across 27 worlds
        return list.filterNot { it.id == "w22_arcade_8" }
    }

    /**
     * Explicit, title-accurate sorting decks for all 9 activities in World 01 (`w01_sorting_1..9`).
     */
    private fun createSortingItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // w01_sorting_1: Primary Color Baskets
                GameItemOption("sort_red_apple", "Red Apple", "🍎", "Red Basket", "Red Apple goes in the Red Basket!", primaryColorHex = 0xFFEF4444, sizeScale = 1.0f),
                GameItemOption("sort_blue_berry", "Blueberry", "🫐", "Blue Basket", "Blueberry goes in the Blue Basket!", primaryColorHex = 0xFF3B82F6, sizeScale = 0.9f),
                GameItemOption("sort_yellow_sun", "Yellow Star", "⭐", "Yellow Basket", "Yellow Star goes in the Yellow Basket!", primaryColorHex = 0xFFEAB308, sizeScale = 1.1f),
                GameItemOption("sort_red_firetruck", "Fire Truck", "🚒", "Red Basket", "Red Fire Truck goes in the Red Basket!", primaryColorHex = 0xFFEF4444, sizeScale = 1.15f, drawableRes = R.drawable.img_object_fire_engine_1794160812725),
                GameItemOption("sort_blue_dolphin", "Dolphin", "🐬", "Blue Basket", "Blue Dolphin goes in the Blue Basket!", primaryColorHex = 0xFF3B82F6, sizeScale = 1.1f, drawableRes = R.drawable.img_animal_ocean_dolphin_1794160811539),
                GameItemOption("sort_yellow_lion", "Lion Cub", "🦁", "Yellow Basket", "Golden Lion goes in the Yellow Basket!", primaryColorHex = 0xFFEAB308, sizeScale = 1.0f, drawableRes = R.drawable.img_animal_savanna_lion_1794160810452)
            )
            1 -> listOf( // w01_sorting_2: Shape Sorter Workshop
                GameItemOption("sort_circle_ball", "Round Ball", "⚽", "Circle Slot", "Round Ball fits the Circle Slot!", primaryColorHex = 0xFF10B981, sizeScale = 0.95f),
                GameItemOption("sort_square_gift", "Square Box", "🎁", "Square Slot", "Square Box fits the Square Slot!", primaryColorHex = 0xFF8B5CF6, sizeScale = 1.05f),
                GameItemOption("sort_star_badge", "Glowing Star", "🌟", "Star Slot", "Glowing Star fits the Star Slot!", primaryColorHex = 0xFFF59E0B, sizeScale = 1.15f),
                GameItemOption("sort_circle_orange", "Sweet Orange", "🍊", "Circle Slot", "Round Orange fits the Circle Slot!", primaryColorHex = 0xFFF97316, sizeScale = 0.9f),
                GameItemOption("sort_square_frame", "Picture Frame", "🖼️", "Square Slot", "Square Frame fits the Square Slot!", primaryColorHex = 0xFF06B6D4, sizeScale = 1.05f),
                GameItemOption("sort_star_sea", "Sea Star", "⭐", "Star Slot", "Sea Star fits the Star Slot!", primaryColorHex = 0xFFEC4899, sizeScale = 1.0f)
            )
            2 -> listOf( // w01_sorting_3: Big, Medium & Little Size Sort
                GameItemOption("sort_tiny_bee", "Tiny Bee", "🐝", "Small Box", "Tiny Bee goes in the Small Box!", primaryColorHex = 0xFFEAB308, sizeScale = 0.78f),
                GameItemOption("sort_med_bunny", "Medium Bunny", "🐰", "Medium Box", "Bunny goes in the Medium Box!", primaryColorHex = 0xFF10B981, sizeScale = 1.0f),
                GameItemOption("sort_big_elephant", "Giant Elephant", "🐘", "Large Box", "Elephant goes in the Large Box!", primaryColorHex = 0xFF3B82F6, sizeScale = 1.28f),
                GameItemOption("sort_tiny_ladybug", "Little Ladybug", "🐞", "Small Box", "Little Ladybug goes in the Small Box!", primaryColorHex = 0xFFEF4444, sizeScale = 0.8f),
                GameItemOption("sort_med_dog", "Playful Puppy", "🐶", "Medium Box", "Puppy goes in the Medium Box!", primaryColorHex = 0xFFF97316, sizeScale = 1.0f),
                GameItemOption("sort_big_whale", "Blue Whale", "🐋", "Large Box", "Giant Whale goes in the Large Box!", primaryColorHex = 0xFF0284C7, sizeScale = 1.3f)
            )
            3 -> listOf( // w01_sorting_4: Pattern Train Baskets
                GameItemOption("pat_sun_1", "Sun Car A", "☀️", "Sun-Moon Pattern", "Sun Car completes the Sun-Moon train pattern!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("pat_moon_1", "Moon Car B", "🌙", "Sun-Moon Pattern", "Moon Car completes the Sun-Moon train pattern!", primaryColorHex = 0xFF6366F1),
                GameItemOption("pat_star_1", "Star Car A", "⭐", "Star-Heart Pattern", "Star Car completes the Star-Heart train pattern!", primaryColorHex = 0xFFEAB308),
                GameItemOption("pat_heart_1", "Heart Car B", "💖", "Star-Heart Pattern", "Heart Car completes the Star-Heart train pattern!", primaryColorHex = 0xFFEC4899),
                GameItemOption("pat_leaf_1", "Flower Car A", "🌸", "Garden Pattern", "Flower Car completes the Garden train pattern!", primaryColorHex = 0xFF10B981),
                GameItemOption("pat_tree_1", "Leaf Car B", "🍃", "Garden Pattern", "Green Leaf Car completes the Garden train pattern!", primaryColorHex = 0xFF059669)
            )
            4 -> listOf( // w01_sorting_5: Yummy Fruit vs. Veggie Baskets
                GameItemOption("fv_apple", "Crisp Apple", "🍎", "Orchard Fruit", "Crisp Red Apple is a sweet Orchard Fruit!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fv_banana", "Yellow Banana", "🍌", "Orchard Fruit", "Sweet Banana is a yummy Fruit!", primaryColorHex = 0xFFEAB308),
                GameItemOption("fv_berry", "Strawberry", "🍓", "Orchard Fruit", "Juicy Strawberry goes in the Fruit Basket!", primaryColorHex = 0xFFEC4899),
                GameItemOption("fv_carrot", "Crunchy Carrot", "🥕", "Garden Veggie", "Crunchy Carrot grows in the Vegetable patch!", primaryColorHex = 0xFFF97316),
                GameItemOption("fv_broccoli", "Green Broccoli", "🥦", "Garden Veggie", "Green Broccoli goes in the Garden Veggie Basket!", primaryColorHex = 0xFF10B981),
                GameItemOption("fv_corn", "Sweet Corn", "🌽", "Garden Veggie", "Golden Corn goes in the Garden Veggie Basket!", primaryColorHex = 0xFFF59E0B)
            )
            5 -> listOf( // w01_sorting_6: Land, Sea & Sky Habitats
                GameItemOption("hab_lion", "Savanna Lion", "🦁", "Land Habitat", "Savanna Lion runs across the Land Habitat!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("hab_bear", "Forest Bear", "🐻", "Land Habitat", "Forest Bear lives on the Land!", primaryColorHex = 0xFFD97706),
                GameItemOption("hab_dolphin", "Ocean Dolphin", "🐬", "Sea Habitat", "Ocean Dolphin swims in the Sea Habitat!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("hab_turtle", "Sea Turtle", "🐢", "Sea Habitat", "Sea Turtle glides through the Ocean Sea!", primaryColorHex = 0xFF10B981),
                GameItemOption("hab_eagle", "Soaring Eagle", "🦅", "Sky Habitat", "Soaring Eagle flies high in the Sky Habitat!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("hab_owl", "Starlight Owl", "🦉", "Sky Habitat", "Owl glides through the twilight Sky!", primaryColorHex = 0xFF8B5CF6)
            )
            6 -> listOf( // w01_sorting_7: Toy Box vs. Bookshelf Sort
                GameItemOption("tb_teddy", "Teddy Bear", "🧸", "Toy Box", "Cuddly Teddy Bear goes into the Toy Box!", primaryColorHex = 0xFFD97706),
                GameItemOption("tb_kite", "Diamond Kite", "🪁", "Toy Box", "Playful Kite goes into the Toy Box!", primaryColorHex = 0xFFEC4899),
                GameItemOption("tb_drum", "Rhythm Drum", "🥁", "Toy Box", "Toy Drum goes neatly in the Toy Box!", primaryColorHex = 0xFFEF4444),
                GameItemOption("tb_bluebook", "Bedtime Book", "📘", "Bookshelf", "Bedtime Storybook stands neatly on the Bookshelf!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("tb_greenbook", "Nature Atlas", "📗", "Bookshelf", "Nature Atlas belongs on the Bookshelf!", primaryColorHex = 0xFF10B981),
                GameItemOption("tb_redbook", "Fairy Tales", "📕", "Bookshelf", "Fairy Tale Book goes on the Bookshelf!", primaryColorHex = 0xFF8B5CF6)
            )
            7 -> listOf( // w01_sorting_8: Sunny Day vs. Rainy Day Gear
                GameItemOption("wea_sunglass", "Sunglasses", "🕶️", "Sunny Day Gear", "Cool Sunglasses are for a bright Sunny Day!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("wea_sunhat", "Sun Hat", "👒", "Sunny Day Gear", "Wide Sun Hat shades us on a Sunny Day!", primaryColorHex = 0xFFEAB308),
                GameItemOption("wea_sandals", "Beach Sandals", "🩴", "Sunny Day Gear", "Comfy Sandals are for warm Sunny Days!", primaryColorHex = 0xFFF97316),
                GameItemOption("wea_umbrella", "Umbrella", "☔", "Rainy Day Gear", "Purple Umbrella keeps us dry on a Rainy Day!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("wea_raincoat", "Rain Jacket", "🧥", "Rainy Day Gear", "Waterproof Coat is for a Rainy Day!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("wea_boots", "Splash Boots", "🥾", "Rainy Day Gear", "Boots keep feet dry in Rainy Day puddles!", primaryColorHex = 0xFF06B6D4)
            )
            else -> listOf( // w01_sorting_9: Garden & Builder Tools
                GameItemOption("tl_sprout", "Seedling Pot", "🪴", "Garden Shed", "Seedling Pot belongs in the Garden Shed!", primaryColorHex = 0xFF10B981),
                GameItemOption("tl_glove", "Garden Gloves", "🧤", "Garden Shed", "Soft Gloves protect hands in the Garden Shed!", primaryColorHex = 0xFF059669),
                GameItemOption("tl_bucket", "Water Pail", "🪣", "Garden Shed", "Watering Pail belongs in the Garden Shed!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("tl_hammer", "Toy Hammer", "🔨", "Builder Bench", "Wooden Hammer goes on the Builder Bench!", primaryColorHex = 0xFFF97316),
                GameItemOption("tl_wrench", "Bolt Wrench", "🔧", "Builder Bench", "Bolt Wrench belongs on the Builder Bench!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("tl_gear", "Steel Gear", "⚙️", "Builder Bench", "Turning Gear goes on the Builder Bench!", primaryColorHex = 0xFF6366F1)
            )
        }
    }

    /**
     * Explicit, title-accurate animal & sound items for all 11 activities in World 02 (`w02_animals_1..11`).
     */
    private fun createAnimalItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // w02_animals_1: Listen & Find Safari
                GameItemOption("ani_lion", "Savanna Lion", "🦁", "Savanna", "Roar! Lions live in family prides.", phoneticCue = "Roar!", primaryColorHex = 0xFFF59E0B, drawableRes = R.drawable.img_animal_savanna_lion_1794160810452),
                GameItemOption("ani_dolphin", "Ocean Dolphin", "🐬", "Ocean", "Click-whistle! Dolphins leap through waves.", phoneticCue = "Splash!", primaryColorHex = 0xFF06B6D4, drawableRes = R.drawable.img_animal_ocean_dolphin_1794160811539),
                GameItemOption("ani_elephant", "Gentle Elephant", "🐘", "Savanna", "Pawoo! Elephants drink with their trunks.", phoneticCue = "Pawoo!", primaryColorHex = 0xFF64748B),
                GameItemOption("ani_owl", "Woodland Owl", "🦉", "Forest", "Hoo-hoo! Owls see clearly at twilight.", phoneticCue = "Hoo-hoo!", primaryColorHex = 0xFF6366F1)
            )
            1 -> listOf( // w02_animals_2: Sound Safari Savanna
                GameItemOption("sav_giraffe", "Tall Giraffe", "🦒", "Acacia Trees", "Munch! Giraffes reach the highest green leaves.", primaryColorHex = 0xFFF59E0B),
                GameItemOption("sav_zebra", "Striped Zebra", "🦓", "Grasslands", "Whinny-bark! Every zebra has unique stripes.", primaryColorHex = 0xFF3B82F6),
                GameItemOption("sav_elephant", "African Elephant", "🐘", "Waterhole", "Pawoo! Elephants cool off with splashy mud baths.", primaryColorHex = 0xFF64748B),
                GameItemOption("sav_lion", "Golden Lion", "🦁", "Sunny Rock", "Roar! The golden lion rests in the warm sun.", primaryColorHex = 0xFFD97706, drawableRes = R.drawable.img_animal_savanna_lion_1794160810452)
            )
            2 -> listOf( // w02_animals_3: Friendly Farmyard Calls
                GameItemOption("frm_cow", "Meadow Cow", "🐄", "Red Barn", "Moooo! Friendly cows give fresh farm milk.", primaryColorHex = 0xFF10B981),
                GameItemOption("frm_duck", "Pond Duck", "🦆", "Farm Pond", "Quack-quack! Ducks paddle across the pond.", primaryColorHex = 0xFFEAB308),
                GameItemOption("frm_sheep", "Fluffy Sheep", "🐑", "Green Hill", "Baa-baa! Sheep wear cozy wool coats.", primaryColorHex = 0xFF3B82F6),
                GameItemOption("frm_horse", "Trotting Horse", "🐴", "Paddock", "Neigh! Horses love crunchy apples and hay.", primaryColorHex = 0xFFF97316)
            )
            3 -> listOf( // w02_animals_4: Who Is My Baby?
                GameItemOption("bab_chick", "Fluffy Chick", "🐣", "Baby of Mama Hen 🐔", "Peep-peep! Fluffy Chick is Mama Hen's baby!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bab_puppy", "Playful Puppy", "🐶", "Baby of Mama Dog 🐕", "Arf-arf! Puppy is Mama Dog's little baby!", primaryColorHex = 0xFFF97316),
                GameItemOption("bab_kitten", "Cozy Kitten", "🐱", "Baby of Mama Cat 🐈", "Mew-mew! Kitten is Mama Cat's sweet baby!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bab_tadpole", "Little Froglet", "🐸", "Baby of Pond Frog 🪷", "Ribbit! Tadpoles grow into hopping Pond Frogs!", primaryColorHex = 0xFF10B981)
            )
            4 -> listOf( // w02_animals_5: Cozy Animal Homes
                GameItemOption("hom_bee", "Honeybee • Hive", "🐝", "Lives in Golden Beehive 🍯", "Buzz! Honeybees build waxy hex combs in their hive!", primaryColorHex = 0xFFEAB308),
                GameItemOption("hom_bird", "Songbird • Nest", "🐦", "Lives in Twig Nest 🪺", "Tweet! Birds weave soft twig nests high in trees!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("hom_bear", "Brown Bear • Den", "🐻", "Lives in Cozy Cave Den 🪨", "Grrr-hum! Bears curl up in a warm cave den!", primaryColorHex = 0xFFD97706),
                GameItemOption("hom_fish", "Clownfish • Reef", "🐠", "Lives in Coral Reef 🪸", "Bloop! Clownfish Dart among colorful coral reefs!", primaryColorHex = 0xFFF97316)
            )
            5 -> listOf( // w02_animals_6: Mystery Animal Shadows
                GameItemOption("shd_giraffe", "Long Neck Shadow", "🦒", "Giraffe Silhouette", "That tall neck shadow belongs to the Giraffe!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("shd_elephant", "Curved Trunk Shadow", "🐘", "Elephant Silhouette", "That big ears and trunk shadow is the Elephant!", primaryColorHex = 0xFF64748B),
                GameItemOption("shd_kangaroo", "Hopping Tail Shadow", "🦘", "Kangaroo Silhouette", "That bouncy tail and pouch shadow is the Kangaroo!", primaryColorHex = 0xFFF97316),
                GameItemOption("shd_owl", "Wide Wings Shadow", "🦉", "Owl Silhouette", "Those silent feathered wings belong to the Owl!", primaryColorHex = 0xFF6366F1)
            )
            6 -> listOf( // w02_animals_7: Muddy Paw & Footprint Trail
                GameItemOption("trk_paw", "Four-Toe Paw Print", "🐾", "Puppy Trail 🐶", "Woof! Four round toe pads show where Puppy trotted!", primaryColorHex = 0xFFF97316),
                GameItemOption("trk_duck", "Webbed Triangle Track", "🦆", "Duckling Trail 💧", "Quack! Webbed footprints lead right to the pond!", primaryColorHex = 0xFFEAB308),
                GameItemOption("trk_hoof", "U-Shaped Hoof Print", "🐴", "Horse Trail 🌾", "Clip-clop! U-shaped horseshoes mark the meadow path!", primaryColorHex = 0xFFD97706),
                GameItemOption("trk_dino", "Giant Three-Toe Print", "🦖", "Dinosaur Trail 🌿", "Stomp! A giant three-toed dinosaur footprint!", primaryColorHex = 0xFF10B981)
            )
            7 -> listOf( // w02_animals_8: What Do Animals Eat?
                GameItemOption("eat_panda", "Panda • Bamboo", "🐼", "Munches Green Bamboo 🎋", "Crunch! Giant Pandas love fresh green bamboo stalks!", primaryColorHex = 0xFF10B981),
                GameItemOption("eat_squirrel", "Squirrel • Acorn", "🐿️", "Gathers Oak Acorns 🌰", "Nibble! Squirrels tuck crunchy acorns in tree hollows!", primaryColorHex = 0xFFD97706),
                GameItemOption("eat_bunny", "Bunny • Carrot", "🐰", "Loves Crunchy Carrots 🥕", "Munch! Bramble the Bunny loves leafy garden carrots!", primaryColorHex = 0xFFF97316),
                GameItemOption("eat_monkey", "Monkey • Banana", "🐵", "Peels Sweet Bananas 🍌", "Ooh-ooh! Monkeys peel sweet yellow bananas!", primaryColorHex = 0xFFEAB308)
            )
            8 -> listOf( // w02_animals_9: Ocean & Reef Singers
                GameItemOption("ocn_dolphin", "Ocean Dolphin", "🐬", "Whistles & Clicks", "Splash! Dolphins leap and sing with clicks!", primaryColorHex = 0xFF06B6D4, drawableRes = R.drawable.img_animal_ocean_dolphin_1794160811539),
                GameItemOption("ocn_whale", "Humpback Whale", "🐋", "Deep Ocean Song", "Whooo! Whales sing gentle songs across the sea!", primaryColorHex = 0xFF2563EB),
                GameItemOption("ocn_turtle", "Green Sea Turtle", "🐢", "Reef Glider", "Swish! Sea turtles use flippers like wings in water!", primaryColorHex = 0xFF10B981),
                GameItemOption("ocn_seal", "Harbor Seal", "🦭", "Clap & Bark", "Arf-clap! Playful seals bask on sunny coastal rocks!", primaryColorHex = 0xFF6366F1)
            )
            9 -> listOf( // w02_animals_10: Interactive Animal Encyclopedia
                GameItemOption("enc_penguin", "Emperor Penguin", "🐧", "Antarctic Ice", "Penguins can't fly in air, but they fly underwater!", primaryColorHex = 0xFF0284C7),
                GameItemOption("enc_koala", "Tree Koala", "🐨", "Australian Gum Tree", "Koalas sleep up to 18 hours in cozy eucalyptus branches!", primaryColorHex = 0xFF64748B),
                GameItemOption("enc_flamingo", "Pink Flamingo", "🦩", "Tropical Lagoon", "Flamingos turn pink from the tiny shrimp they eat!", primaryColorHex = 0xFFEC4899),
                GameItemOption("enc_otter", "River Otter", "🦦", "Freshwater Stream", "Otters hold paws while floating so they stay together!", primaryColorHex = 0xFF06B6D4)
            )
            else -> listOf( // w02_animals_11: Object & Vehicle Sound Cards
                GameItemOption("snd_bell", "Golden Handbell", "🔔", "Ring-a-Ling Chime", "Ding-dong! The brass bell rings a clear musical note!", primaryColorHex = 0xFFEAB308),
                GameItemOption("snd_train", "Steam Locomotive", "🚂", "Choo-Choo Whistle", "Choo-choo! Steam puffs as the train rolls down the track!", primaryColorHex = 0xFF3B82F6, drawableRes = R.drawable.img_object_steam_train_1794160812641),
                GameItemOption("snd_clock", "Grandfather Clock", "🕰️", "Tick-Tock Chime", "Tick-tock! The pendulum swings in steady rhythm!", primaryColorHex = 0xFFD97706),
                GameItemOption("snd_fire", "Fire Engine Bell", "🚒", "Rescue Siren", "Wee-oo! The bright red fire engine races to help!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.img_object_fire_engine_1794160812725)
            )
        }
    }

    /**
     * Explicit, title-accurate memory decks for all 9 activities in World 03 (`w03_memory_1..9`).
     */
    private fun createMemoryItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // Forest Animal Pairs
                GameItemOption("mem_fox", "Fox", "🦊", "Forest", "Clever Fox!"),
                GameItemOption("mem_bear", "Bear", "🐻", "Forest", "Cuddly Bear!"),
                GameItemOption("mem_owl", "Owl", "🦉", "Forest", "Wise Owl!"),
                GameItemOption("mem_deer", "Deer", "🦌", "Forest", "Gentle Deer!"),
                GameItemOption("mem_bunny", "Bunny", "🐰", "Forest", "Hopping Bunny!"),
                GameItemOption("mem_squirrel", "Squirrel", "🐿️", "Forest", "Busy Squirrel!")
            )
            1 -> listOf( // Rainbow Color Splash Pairs
                GameItemOption("mem_red", "Red", "❤️", "Color", "Bright Red!"),
                GameItemOption("mem_blue", "Blue", "💙", "Color", "Sky Blue!"),
                GameItemOption("mem_yellow", "Yellow", "💛", "Color", "Sunny Yellow!"),
                GameItemOption("mem_green", "Green", "💚", "Color", "Meadow Green!"),
                GameItemOption("mem_purple", "Purple", "💜", "Color", "Royal Purple!"),
                GameItemOption("mem_orange", "Orange", "🧡", "Color", "Warm Orange!")
            )
            2 -> listOf( // Orchard Fruit Basket Pairs
                GameItemOption("mem_apple", "Apple", "🍎", "Fruit", "Crisp Apple!"),
                GameItemOption("mem_banana", "Banana", "🍌", "Fruit", "Sweet Banana!"),
                GameItemOption("mem_straw", "Strawberry", "🍓", "Fruit", "Red Strawberry!"),
                GameItemOption("mem_orange_f", "Orange", "🍊", "Fruit", "Juicy Orange!"),
                GameItemOption("mem_grape", "Grapes", "🍇", "Fruit", "Purple Grapes!"),
                GameItemOption("mem_melon", "Watermelon", "🍉", "Fruit", "Sweet Watermelon!")
            )
            3 -> listOf( // ABC Letter Twins (Uppercase paired with lowercase in MemoryMatchEngine)
                GameItemOption("mem_let_a", "A", "🍎", "Letter", "Letter A!"),
                GameItemOption("mem_let_b", "B", "🐻", "Letter", "Letter B!"),
                GameItemOption("mem_let_c", "C", "🐱", "Letter", "Letter C!"),
                GameItemOption("mem_let_d", "D", "🐬", "Letter", "Letter D!"),
                GameItemOption("mem_let_e", "E", "🐘", "Letter", "Letter E!"),
                GameItemOption("mem_let_f", "F", "🐸", "Letter", "Letter F!")
            )
            4 -> listOf( // 1-2-3 Number Dot Pairs
                GameItemOption("mem_num_1", "1 • One", "1️⃣", "Number", "Number One!"),
                GameItemOption("mem_num_2", "2 • Two", "2️⃣", "Number", "Number Two!"),
                GameItemOption("mem_num_3", "3 • Three", "3️⃣", "Number", "Number Three!"),
                GameItemOption("mem_num_4", "4 • Four", "4️⃣", "Number", "Number Four!"),
                GameItemOption("mem_num_5", "5 • Five", "5️⃣", "Number", "Number Five!"),
                GameItemOption("mem_num_6", "6 • Six", "6️⃣", "Number", "Number Six!")
            )
            5 -> listOf( // Zooming Vehicle Pairs
                GameItemOption("mem_veh_train", "Train", "🚂", "Vehicle", "Steam Train!"),
                GameItemOption("mem_veh_fire", "Fire Truck", "🚒", "Vehicle", "Fire Engine!"),
                GameItemOption("mem_veh_boat", "Sailboat", "⛵", "Vehicle", "Ocean Sailboat!"),
                GameItemOption("mem_veh_plane", "Airplane", "✈️", "Vehicle", "Sky Airplane!"),
                GameItemOption("mem_veh_heli", "Helicopter", "🚁", "Vehicle", "Helicopter!"),
                GameItemOption("mem_veh_rocket", "Rocket", "🚀", "Vehicle", "Space Rocket!")
            )
            6 -> listOf( // Happy Feelings Faces
                GameItemOption("mem_emo_happy", "Happy", "😊", "Feeling", "Happy Smile!"),
                GameItemOption("mem_emo_calm", "Calm", "😌", "Feeling", "Peaceful Calm!"),
                GameItemOption("mem_emo_wow", "Surprised", "😮", "Feeling", "Surprised Wow!"),
                GameItemOption("mem_emo_sleep", "Sleepy", "😴", "Feeling", "Cozy Sleepy!"),
                GameItemOption("mem_emo_silly", "Silly", "😜", "Feeling", "Playful Silly!"),
                GameItemOption("mem_emo_love", "Kind", "🤗", "Feeling", "Warm Kind Hug!")
            )
            7 -> listOf( // Picnic Treat Pairs
                GameItemOption("mem_pic_sand", "Sandwich", "🥪", "Picnic", "Healthy Sandwich!"),
                GameItemOption("mem_pic_muf", "Muffin", "🧁", "Picnic", "Berry Muffin!"),
                GameItemOption("mem_pic_jui", "Juice Box", "🧃", "Picnic", "Apple Juice!"),
                GameItemOption("mem_pic_pret", "Pretzel", "🥨", "Picnic", "Crunchy Pretzel!"),
                GameItemOption("mem_pic_ches", "Cheese", "🧀", "Picnic", "Cheddar Cheese!"),
                GameItemOption("mem_pic_pie", "Fruit Pie", "🥧", "Picnic", "Warm Apple Pie!")
            )
            else -> listOf( // Playroom Toy Pairs
                GameItemOption("mem_toy_ted", "Teddy", "🧸", "Toy", "Teddy Bear!"),
                GameItemOption("mem_toy_kit", "Kite", "🪁", "Toy", "Flying Kite!"),
                GameItemOption("mem_toy_drm", "Drum", "🥁", "Toy", "Rhythm Drum!"),
                GameItemOption("mem_toy_blk", "Blocks", "🧱", "Toy", "Building Blocks!"),
                GameItemOption("mem_toy_bal", "Ball", "⚽", "Toy", "Bouncy Ball!"),
                GameItemOption("mem_toy_art", "Paint", "🎨", "Toy", "Artist Palette!")
            )
        }
    }

    private fun createBalloonItemsForIndex(seed: Int): List<GameItemOption> {
        return when (seed) {
            0 -> listOf(
                GameItemOption("bal_let_a", "A • Apple", "🍎", "Letter A", "Letter A, for Apple!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_let_b", "B • Bear", "🐻", "Letter B", "Letter B, for Bear!", primaryColorHex = 0xFFF97316),
                GameItemOption("bal_let_c", "C • Cat", "🐱", "Letter C", "Letter C, for Cat!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_let_d", "D • Dolphin", "🐬", "Letter D", "Letter D, for Dolphin!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("bal_let_e", "E • Elephant", "🐘", "Letter E", "Letter E, for Elephant!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bal_let_f", "F • Fire Truck", "🚒", "Letter F", "Letter F, for Fire Truck!", primaryColorHex = 0xFFDC2626),
                GameItemOption("bal_let_g", "G • Giraffe", "🦒", "Letter G", "Letter G, for Giraffe!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_let_h", "H • Helicopter", "🚁", "Letter H", "Letter H, for Helicopter!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_let_l", "L • Lion", "🦁", "Letter L", "Letter L, for Lion!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_let_m", "M • Moon", "🌙", "Letter M", "Letter M, for Moon!", primaryColorHex = 0xFF6366F1),
                GameItemOption("bal_let_r", "R • Rainbow", "🌈", "Letter R", "Letter R, for Rainbow!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_let_s", "S • Sun", "☀️", "Letter S", "Letter S, for Sun!", primaryColorHex = 0xFFD97706)
            )
            1 -> listOf(
                GameItemOption("bal_col_red", "Red", "❤️", "Red", "Bright Red balloon!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_col_blue", "Blue", "💙", "Blue", "Sky Blue balloon!", primaryColorHex = 0xFF2563EB),
                GameItemOption("bal_col_yellow", "Yellow", "💛", "Yellow", "Sunny Yellow balloon!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_col_green", "Green", "💚", "Green", "Emerald Green balloon!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_col_orange", "Orange", "🧡", "Orange", "Warm Orange balloon!", primaryColorHex = 0xFFF97316),
                GameItemOption("bal_col_purple", "Purple", "💜", "Purple", "Royal Purple balloon!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_col_pink", "Pink", "💖", "Pink", "Rosy Pink balloon!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_col_teal", "Teal", "🩵", "Teal", "Ocean Teal balloon!", primaryColorHex = 0xFF06B6D4)
            )
            2 -> listOf(
                GameItemOption("bal_num_1", "1 • One", "1️⃣", "1", "Number One!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_num_2", "2 • Two", "2️⃣", "2", "Number Two!", primaryColorHex = 0xFFF97316),
                GameItemOption("bal_num_3", "3 • Three", "3️⃣", "3", "Number Three!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_num_4", "4 • Four", "4️⃣", "4", "Number Four!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_num_5", "5 • Five", "5️⃣", "5", "Number Five!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("bal_num_6", "6 • Six", "6️⃣", "6", "Number Six!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bal_num_7", "7 • Seven", "7️⃣", "7", "Number Seven!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_num_8", "8 • Eight", "8️⃣", "8", "Number Eight!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_num_9", "9 • Nine", "9️⃣", "9", "Number Nine!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_num_10", "10 • Ten", "🔟", "10", "Number Ten!", primaryColorHex = 0xFF059669),
                GameItemOption("bal_num_15", "15 • Fifteen", "🌟", "15", "Number Fifteen!", primaryColorHex = 0xFF6366F1),
                GameItemOption("bal_num_20", "20 • Twenty", "🏆", "20", "Number Twenty!", primaryColorHex = 0xFFDC2626)
            )
            3 -> listOf(
                GameItemOption("bal_shp_circle", "Circle", "⭕", "Circle", "Round Circle!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_shp_star", "Star", "⭐", "Star", "Five-point Star!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_shp_heart", "Heart", "💖", "Heart", "Loving Heart shape!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_shp_square", "Square", "🟦", "Square", "Four-corner Square!", primaryColorHex = 0xFF2563EB),
                GameItemOption("bal_shp_triangle", "Triangle", "🔺", "Triangle", "Three-side Triangle!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_shp_diamond", "Diamond", "🔷", "Diamond", "Shining Diamond!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("bal_shp_moon", "Crescent", "🌙", "Crescent", "Curved Crescent Moon!", primaryColorHex = 0xFF8B5CF6)
            )
            4 -> listOf(
                GameItemOption("bal_ani_lion", "Lion • Roar", "🦁", "Lion", "Lion says Roar!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_ani_ele", "Elephant", "🐘", "Elephant", "Elephant trumpets Pawoo!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bal_ani_cow", "Cow • Moo", "🐄", "Cow", "Friendly Cow says Moo!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_ani_duck", "Duck • Quack", "🦆", "Duck", "Pond Duck says Quack, quack!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_ani_owl", "Owl • Hoo", "🦉", "Owl", "Night Owl says Hoo, hoo!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_ani_frog", "Frog • Ribbit", "🐸", "Frog", "Green Frog says Ribbit!", primaryColorHex = 0xFF059669),
                GameItemOption("bal_ani_dog", "Puppy • Woof", "🐶", "Puppy", "Happy Puppy says Woof!", primaryColorHex = 0xFFF97316),
                GameItemOption("bal_ani_cat", "Kitten • Meow", "🐱", "Kitten", "Cozy Kitten says Meow!", primaryColorHex = 0xFFEC4899)
            )
            5 -> listOf(
                GameItemOption("bal_bud_sunny", "Sunny Cub", "🦁", "Sunny", "Hi from Sunny the Golden Cub!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_bud_pip", "Pip Owl", "🦉", "Pip", "Hoo-ray from Pip the Owl!", primaryColorHex = 0xFF6366F1),
                GameItemOption("bal_bud_bramble", "Bramble Bunny", "🐰", "Bramble", "Hop, hop from Bramble the Bunny!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_bud_coral", "Coral Otter", "🦦", "Coral", "Splash hello from Coral the Otter!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("bal_bud_bot", "Beep-Bot", "🤖", "Beep-Bot", "Beep-boop from Robot Friend!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bal_bud_chef", "Little Chef", "👩‍🍳", "Chef", "Yummy treats from Little Chef!", primaryColorHex = 0xFFEF4444)
            )
            6 -> listOf(
                GameItemOption("bal_fru_apple", "Red Apple", "🍎", "Apple", "Crisp Red Apple!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_fru_banana", "Banana", "🍌", "Banana", "Sweet Yellow Banana!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_fru_straw", "Strawberry", "🍓", "Strawberry", "Juicy Garden Strawberry!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_fru_orange", "Orange", "🍊", "Orange", "Sunny Citrus Orange!", primaryColorHex = 0xFFF97316),
                GameItemOption("bal_fru_grape", "Grapes", "🍇", "Grapes", "Bunch of Purple Grapes!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_fru_melon", "Watermelon", "🍉", "Watermelon", "Refreshing Watermelon slice!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_fru_mango", "Mango", "🥭", "Mango", "Golden Tropical Mango!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_fru_cherry", "Cherries", "🍒", "Cherries", "Two sweet red Cherries!", primaryColorHex = 0xFFDC2626)
            )
            7 -> listOf(
                GameItemOption("bal_fod_carrot", "Carrot", "🥕", "Carrot", "Crunchy Orange Carrot!", primaryColorHex = 0xFFF97316),
                GameItemOption("bal_fod_broc", "Broccoli", "🥦", "Broccoli", "Green Tree Broccoli!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_fod_bread", "Warm Bread", "🍞", "Bread", "Fresh Bakery Bread!", primaryColorHex = 0xFFD97706),
                GameItemOption("bal_fod_cheese", "Cheese", "🧀", "Cheese", "Yummy Yellow Cheese!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_fod_muffin", "Berry Muffin", "🧁", "Muffin", "Sweet Berry Muffin!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_fod_pretzel", "Pretzel", "🥨", "Pretzel", "Twisty Picnic Pretzel!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_fod_corn", "Sweet Corn", "🌽", "Corn", "Golden Ear of Corn!", primaryColorHex = 0xFFF59E0B)
            )
            8 -> listOf(
                GameItemOption("bal_veh_fire", "Fire Engine", "🚒", "Fire Engine", "Rescue Red Fire Engine!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_veh_train", "Steam Train", "🚂", "Steam Train", "Choo-choo Steam Train!", primaryColorHex = 0xFF2563EB),
                GameItemOption("bal_veh_heli", "Helicopter", "🚁", "Helicopter", "Whirring Sky Helicopter!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_veh_boat", "Sailboat", "⛵", "Sailboat", "Breezy Ocean Sailboat!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("bal_veh_plane", "Airplane", "✈️", "Airplane", "High-flying Airplane!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_veh_bus", "School Bus", "🚌", "School Bus", "Yellow Town Bus!", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_veh_rocket", "Rocket", "🚀", "Rocket", "Starlight Space Rocket!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_veh_tractor", "Tractor", "🚜", "Tractor", "Green Farm Tractor!", primaryColorHex = 0xFF059669)
            )
            9 -> listOf(
                GameItemOption("bal_wea_sun", "Golden Sun", "☀️", "Sun", "Warm Golden Sunshine!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_wea_rain", "Rain Cloud", "🌧️", "Rain", "Gentle Raindrops falling!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bal_wea_bow", "Rainbow", "🌈", "Rainbow", "Seven-color Sky Rainbow!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_wea_snow", "Snowflake", "❄️", "Snowflake", "Sparkling Winter Snowflake!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("bal_wea_cloud", "Fluffy Cloud", "☁️", "Cloud", "Soft Fluffy White Cloud!", primaryColorHex = 0xFF6366F1),
                GameItemOption("bal_wea_star", "Night Star", "🌟", "Star", "Twinkling Evening Star!", primaryColorHex = 0xFFEAB308)
            )
            10 -> listOf(
                GameItemOption("bal_toy_teddy", "Teddy Bear", "🧸", "Teddy Bear", "Cuddly Brown Teddy Bear!", primaryColorHex = 0xFFD97706),
                GameItemOption("bal_toy_kite", "Sky Kite", "🪁", "Kite", "Soaring Diamond Kite!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bal_toy_drum", "Rhythm Drum", "🥁", "Drum", "Rat-a-tat Rhythm Drum!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_toy_block", "Blocks", "🧱", "Blocks", "Colorful Building Blocks!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bal_toy_ball", "Soccer Ball", "⚽", "Ball", "Bouncy Playfield Ball!", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_toy_paint", "Paint Set", "🎨", "Paint", "Rainbow Artist Palette!", primaryColorHex = 0xFF8B5CF6)
            )
            else -> listOf(
                GameItemOption("bal_pho_a", "/a/ • A", "🍎", "A", "Letter A... aah, Apple.", phoneticCue = "aah", primaryColorHex = 0xFFEF4444),
                GameItemOption("bal_pho_b", "/b/ • B", "🐻", "B", "Letter B... b, Bear.", phoneticCue = "b", primaryColorHex = 0xFF2563EB),
                GameItemOption("bal_pho_c", "/k/ • C", "🐱", "C", "Letter C... k, Cat.", phoneticCue = "k", primaryColorHex = 0xFF10B981),
                GameItemOption("bal_pho_d", "/d/ • D", "🐬", "D", "Letter D... d, Dolphin.", phoneticCue = "d", primaryColorHex = 0xFF06B6D4),
                GameItemOption("bal_pho_l", "/l/ • L", "🦁", "L", "Letter L... llll, Lion.", phoneticCue = "llll", primaryColorHex = 0xFFF59E0B),
                GameItemOption("bal_pho_m", "/m/ • M", "🌙", "M", "Letter M... mmmm, Moon.", phoneticCue = "mmmm", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("bal_pho_s", "/s/ • S", "☀️", "S", "Letter S... ssss, Sun.", phoneticCue = "ssss", primaryColorHex = 0xFFEAB308),
                GameItemOption("bal_pho_t", "/t/ • T", "🚂", "T", "Letter T... t, Train.", phoneticCue = "t", primaryColorHex = 0xFFEC4899)
            )
        }
    }

    private fun createArcadeItemsForIndex(seed: Int): List<GameItemOption> {
        return when (seed) {
            0 -> listOf(
                GameItemOption("arc_bub_1", "Pink Bubble", "🫧", "Bubble", "Pop! Shimmering Pink Bubble!", primaryColorHex = 0xFFEC4899),
                GameItemOption("arc_bub_2", "Blue Bubble", "🫧", "Bubble", "Pop! Floating Sky Bubble!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("arc_bub_3", "Gold Bubble", "🫧", "Bubble", "Pop! Golden Sun Bubble!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("arc_bub_4", "Mint Bubble", "🫧", "Bubble", "Pop! Meadow Mint Bubble!", primaryColorHex = 0xFF10B981),
                GameItemOption("arc_bub_5", "Violet Bubble", "🫧", "Bubble", "Pop! Sparkling Violet Bubble!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("arc_bub_6", "Coral Bubble", "🫧", "Bubble", "Pop! Warm Coral Bubble!", primaryColorHex = 0xFFF97316)
            )
            1 -> listOf(
                GameItemOption("arc_str_1", "Gold Star", "🌟", "Star", "Caught a glowing Gold Star!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("arc_str_2", "Comet Star", "🌠", "Comet", "Caught a bright Shooting Star!", primaryColorHex = 0xFF6366F1),
                GameItemOption("arc_str_3", "Sparkle Star", "✨", "Sparkle", "Caught a twinkling Sparkle Star!", primaryColorHex = 0xFFEC4899),
                GameItemOption("arc_str_4", "Moon Star", "🌙", "Moon", "Caught a Crescent Moonbeam!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("arc_str_5", "Sun Star", "☀️", "Sun", "Caught a warm Morning Sunbeam!", primaryColorHex = 0xFFEAB308),
                GameItemOption("arc_str_6", "Nova Star", "⭐", "Nova", "Caught a shining Super Star!", primaryColorHex = 0xFF10B981)
            )
            2 -> listOf(
                GameItemOption("arc_but_1", "Monarch", "🦋", "Butterfly", "Fluttering Monarch Butterfly!", primaryColorHex = 0xFFF97316),
                GameItemOption("arc_but_2", "Blue Morpho", "🦋", "Butterfly", "Sky-blue Morpho Butterfly!", primaryColorHex = 0xFF2563EB),
                GameItemOption("arc_but_3", "Ladybug", "🐞", "Ladybug", "Friendly Garden Ladybug!", primaryColorHex = 0xFFEF4444),
                GameItemOption("arc_but_4", "Honeybee", "🐝", "Bee", "Buzzing Meadow Honeybee!", primaryColorHex = 0xFFEAB308),
                GameItemOption("arc_but_5", "Dragonfly", "蜻", "Dragonfly", "Darting Pond Dragonfly!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("arc_but_6", "Firefly", "✨", "Firefly", "Glowing Twilight Firefly!", primaryColorHex = 0xFF8B5CF6)
            )
            3 -> listOf(
                GameItemOption("arc_fsh_1", "Clownfish", "🐠", "Fish", "Reef Clownfish swimming by!", primaryColorHex = 0xFFF97316),
                GameItemOption("arc_fsh_2", "Blue Tang", "🐟", "Fish", "Bright Blue Reef Fish!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("arc_fsh_3", "Sea Turtle", "🐢", "Turtle", "Gentle Green Sea Turtle!", primaryColorHex = 0xFF10B981),
                GameItemOption("arc_fsh_4", "Dolphin", "🐬", "Dolphin", "Playful Leaping Dolphin!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("arc_fsh_5", "Octopus", "🐙", "Octopus", "Eight-arm Coral Octopus!", primaryColorHex = 0xFFEC4899),
                GameItemOption("arc_fsh_6", "Sea Star", "⭐", "Sea Star", "Golden Tidepool Starfish!", primaryColorHex = 0xFFF59E0B)
            )
            4 -> listOf(
                GameItemOption("arc_trs_1", "Pearl Shell", "🐚", "Shell", "Found a glowing Ocean Shell!", primaryColorHex = 0xFFEC4899),
                GameItemOption("arc_trs_2", "Gem Jewel", "💎", "Gem", "Found a sparkling Blue Gem!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("arc_trs_3", "Gold Crown", "👑", "Crown", "Found the Royal Treasure Crown!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("arc_trs_4", "Gold Coin", "🪙", "Coin", "Found a shiny Treasure Coin!", primaryColorHex = 0xFFEAB308),
                GameItemOption("arc_trs_5", "Star Key", "🔑", "Key", "Found the Golden Wonder Key!", primaryColorHex = 0xFFF97316),
                GameItemOption("arc_trs_6", "Trophy Cup", "🏆", "Trophy", "Found the Champion Star Cup!", primaryColorHex = 0xFF10B981)
            )
            5 -> listOf(
                GameItemOption("arc_cld_1", "Sun Cloud", "🌤️", "Cloud", "Hopped onto the Sunbeam Cloud!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("arc_cld_2", "Rainbow Cloud", "🌈", "Rainbow", "Hopped onto the Rainbow Arch!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("arc_cld_3", "Fluffy Cloud", "☁️", "Cloud", "Hopped onto the Fluffy Sky Cloud!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("arc_cld_4", "Star Cloud", "🌟", "Star", "Hopped onto the Starry Cloud!", primaryColorHex = 0xFFEAB308),
                GameItemOption("arc_cld_5", "Breeze Cloud", "🍃", "Breeze", "Hopped onto the Gentle Breeze!", primaryColorHex = 0xFF10B981),
                GameItemOption("arc_cld_6", "Pink Dawn", "🩷", "Dawn", "Hopped onto the Rosy Dawn Cloud!", primaryColorHex = 0xFFEC4899)
            )
            else -> listOf(
                GameItemOption("arc_frt_1", "Green Apple", "🍏", "Apple", "Caught a crisp Orchard Apple!", primaryColorHex = 0xFF10B981),
                GameItemOption("arc_frt_2", "Sweet Pear", "🍐", "Pear", "Caught a ripe Meadow Pear!", primaryColorHex = 0xFFEAB308),
                GameItemOption("arc_frt_3", "Red Berry", "🍓", "Berry", "Caught a sweet Red Berry!", primaryColorHex = 0xFFEF4444),
                GameItemOption("arc_frt_4", "Ripe Peach", "🍑", "Peach", "Caught a fuzzy Golden Peach!", primaryColorHex = 0xFFF97316),
                GameItemOption("arc_frt_5", "Sweet Cherries", "🍒", "Cherries", "Caught twin Ruby Cherries!", primaryColorHex = 0xFFDC2626),
                GameItemOption("arc_frt_6", "Blueberry", "🫐", "Blueberry", "Caught a plump Blueberry!", primaryColorHex = 0xFF3B82F6)
            )
        }
    }

    /**
     * Explicit, title-accurate Phonics & Alphabet decks for all 12 activities in World 07 (`w07_phonics_1..12`).
     */
    private fun createPhonicsItemsForIndex(idx: Int): List<GameItemOption> {
        val aToM = listOf(
            GameItemOption("ph_a", "A", "🍎", "Apple", "Letter A... aah, Apple.", phoneticCue = "aah", primaryColorHex = 0xFFEF4444),
            GameItemOption("ph_b", "B", "🐻", "Bear", "Letter B... b, Bear.", phoneticCue = "b", primaryColorHex = 0xFF3B82F6),
            GameItemOption("ph_c", "C", "🐱", "Cat", "Letter C... k, Cat.", phoneticCue = "k", primaryColorHex = 0xFF10B981),
            GameItemOption("ph_d", "D", "🐬", "Dolphin", "Letter D... d, Dolphin.", phoneticCue = "d", primaryColorHex = 0xFF06B6D4),
            GameItemOption("ph_e", "E", "🐘", "Elephant", "Letter E... eh, Elephant.", phoneticCue = "eh", primaryColorHex = 0xFF8B5CF6),
            GameItemOption("ph_f", "F", "🚒", "Fire Truck", "Letter F... ffff, Fire Truck.", phoneticCue = "ffff", primaryColorHex = 0xFFEC4899),
            GameItemOption("ph_g", "G", "🦒", "Giraffe", "Letter G... g, Giraffe.", phoneticCue = "g", primaryColorHex = 0xFFF59E0B),
            GameItemOption("ph_h", "H", "🚁", "Helicopter", "Letter H... h, Helicopter.", phoneticCue = "h", primaryColorHex = 0xFF10B981),
            GameItemOption("ph_i", "I", "🧊", "Igloo", "Letter I... ih, Igloo.", phoneticCue = "ih", primaryColorHex = 0xFF06B6D4),
            GameItemOption("ph_j", "J", "🪼", "Jellyfish", "Letter J... j, Jellyfish.", phoneticCue = "j", primaryColorHex = 0xFF6366F1),
            GameItemOption("ph_k", "K", "🪁", "Kite", "Letter K... k, Kite.", phoneticCue = "k", primaryColorHex = 0xFFEC4899),
            GameItemOption("ph_l", "L", "🦁", "Lion", "Letter L... llll, Lion.", phoneticCue = "llll", primaryColorHex = 0xFFF59E0B),
            GameItemOption("ph_m", "M", "🌙", "Moon", "Letter M... mmmm, Moon.", phoneticCue = "mmmm", primaryColorHex = 0xFF3B82F6)
        )
        val nToZ = listOf(
            GameItemOption("ph_n", "N", "🪺", "Nest", "Letter N... nnnn, Nest.", phoneticCue = "nnnn", primaryColorHex = 0xFF10B981),
            GameItemOption("ph_o", "O", "🐙", "Octopus", "Letter O... aw, Octopus.", phoneticCue = "aw", primaryColorHex = 0xFFF97316),
            GameItemOption("ph_p", "P", "🐧", "Penguin", "Letter P... p, Penguin.", phoneticCue = "p", primaryColorHex = 0xFF3B82F6),
            GameItemOption("ph_q", "Q", "👑", "Queen", "Letter Q... kw, Queen.", phoneticCue = "kw", primaryColorHex = 0xFF8B5CF6),
            GameItemOption("ph_r", "R", "🌈", "Rainbow", "Letter R... rrrr, Rainbow.", phoneticCue = "rrrr", primaryColorHex = 0xFFEC4899),
            GameItemOption("ph_s", "S", "☀️", "Sun", "Letter S... ssss, Sun.", phoneticCue = "ssss", primaryColorHex = 0xFFEAB308),
            GameItemOption("ph_t", "T", "🚂", "Train", "Letter T... t, Train.", phoneticCue = "t", primaryColorHex = 0xFF06B6D4),
            GameItemOption("ph_u", "U", "☂️", "Umbrella", "Letter U... uh, Umbrella.", phoneticCue = "uh", primaryColorHex = 0xFF6366F1),
            GameItemOption("ph_v", "V", "🎻", "Violin", "Letter V... vvvv, Violin.", phoneticCue = "vvvv", primaryColorHex = 0xFFEF4444),
            GameItemOption("ph_w", "W", "🐋", "Whale", "Letter W... w, Whale.", phoneticCue = "w", primaryColorHex = 0xFF0284C7),
            GameItemOption("ph_x", "X", "🎼", "Xylophone", "Letter X... ks, Xylophone.", phoneticCue = "ks", primaryColorHex = 0xFF10B981),
            GameItemOption("ph_y", "Y", "🧶", "Yarn", "Letter Y... y, Yarn.", phoneticCue = "y", primaryColorHex = 0xFFF59E0B),
            GameItemOption("ph_z", "Z", "🦓", "Zebra", "Letter Z... zzzz, Zebra.", phoneticCue = "zzzz", primaryColorHex = 0xFF8B5CF6)
        )
        return when (idx) {
            0 -> aToM.take(8) // w07_phonics_1: Letter Hunt Safari
            1 -> aToM          // w07_phonics_2: First Sounds (/a/ to /m/)
            2 -> nToZ          // w07_phonics_3: First Sounds (/n/ to /z/)
            3 -> listOf(       // w07_phonics_4: Little Letters Match (Uppercase to Lowercase)
                GameItemOption("ul_a", "Aa", "🍎", "Uppercase A & lowercase a", "Big A and little a!", primaryColorHex = 0xFFEF4444),
                GameItemOption("ul_b", "Bb", "🐻", "Uppercase B & lowercase b", "Big B and little b!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("ul_c", "Cc", "🐱", "Uppercase C & lowercase c", "Big C and little c!", primaryColorHex = 0xFF10B981),
                GameItemOption("ul_d", "Dd", "🐬", "Uppercase D & lowercase d", "Big D and little d!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("ul_e", "Ee", "🐘", "Uppercase E & lowercase e", "Big E and little e!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("ul_f", "Ff", "🐸", "Uppercase F & lowercase f", "Big F and little f!", primaryColorHex = 0xFFEC4899)
            )
            4 -> listOf(       // w07_phonics_5: Rhyme Time Pairs
                GameItemOption("rhy_cat", "CAT • HAT", "🐱", "Rhymes with HAT 🎩", "Cat and Hat rhyme!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("rhy_star", "STAR • CAR", "⭐", "Rhymes with CAR 🚗", "Star and Car rhyme!", primaryColorHex = 0xFFEF4444),
                GameItemOption("rhy_bee", "BEE • TREE", "🐝", "Rhymes with TREE 🌳", "Bee and Tree rhyme!", primaryColorHex = 0xFF10B981),
                GameItemOption("rhy_dog", "DOG • FROG", "🐶", "Rhymes with FROG 🐸", "Dog and Frog rhyme!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("rhy_sun", "SUN • RUN", "☀️", "Rhymes with RUN 🏃", "Sun and Run rhyme!", primaryColorHex = 0xFFEAB308),
                GameItemOption("rhy_cake", "CAKE • LAKE", "🎂", "Rhymes with LAKE 🌊", "Cake and Lake rhyme!", primaryColorHex = 0xFF8B5CF6)
            )
            5 -> listOf(       // w07_phonics_6: Missing ABC Bridge
                GameItemOption("brg_b", "B", "🌉", "A [ ? ] C", "B bridges A and C!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("brg_e", "E", "🌉", "D [ ? ] F", "E bridges D and F!", primaryColorHex = 0xFF10B981),
                GameItemOption("brg_h", "H", "🌉", "G [ ? ] I", "H bridges G and I!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("brg_k", "K", "🌉", "J [ ? ] L", "K bridges J and L!", primaryColorHex = 0xFFEC4899),
                GameItemOption("brg_n", "N", "🌉", "M [ ? ] O", "N bridges M and O!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("brg_q", "Q", "🌉", "P [ ? ] R", "Q bridges P and R!", primaryColorHex = 0xFF06B6D4)
            )
            6 -> listOf(       // w07_phonics_7: Choo-Choo Letter Train
                GameItemOption("trn_a", "A", "🚂", "Car 1 before B-C", "Train Car A leads the way!", primaryColorHex = 0xFFEF4444),
                GameItemOption("trn_b", "B", "🚃", "Car 2 after A", "Train Car B follows A!", primaryColorHex = 0xFFF97316),
                GameItemOption("trn_c", "C", "🚃", "Car 3 after A-B", "Train Car C follows B!", primaryColorHex = 0xFFEAB308),
                GameItemOption("trn_d", "D", "🚃", "Car 4 after B-C", "Train Car D follows C!", primaryColorHex = 0xFF10B981),
                GameItemOption("trn_e", "E", "🚃", "Car 5 after C-D", "Train Car E follows D!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("trn_f", "F", "🚃", "Car 6 after D-E", "Train Car F follows E!", primaryColorHex = 0xFF3B82F6)
            )
            7 -> listOf(       // w07_phonics_8: Word Starts Picture Quiz
                GameItemOption("ws_a", "A", "🍎", "Starts Apple", "Apple starts with Letter A!", primaryColorHex = 0xFFEF4444),
                GameItemOption("ws_b", "B", "🐻", "Starts Bear", "Bear starts with Letter B!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("ws_d", "D", "🐬", "Starts Dolphin", "Dolphin starts with Letter D!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("ws_s", "S", "☀️", "Starts Sun", "Sun starts with Letter S!", primaryColorHex = 0xFFEAB308),
                GameItemOption("ws_l", "L", "🦁", "Starts Lion", "Lion starts with Letter L!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("ws_t", "T", "🚂", "Starts Train", "Train starts with Letter T!", primaryColorHex = 0xFF8B5CF6)
            )
            8 -> listOf(       // w07_phonics_9: Vowel Sound Explorer (Strictly A, E, I, O, U)
                GameItemOption("vow_a", "A", "🍎", "Short Vowel /a/ • Apple", "Vowel A says aah as in Apple!", phoneticCue = "aah", primaryColorHex = 0xFFEF4444),
                GameItemOption("vow_e", "E", "🐘", "Short Vowel /e/ • Elephant", "Vowel E says eh as in Elephant!", phoneticCue = "eh", primaryColorHex = 0xFF3B82F6),
                GameItemOption("vow_i", "I", "🧊", "Short Vowel /i/ • Igloo", "Vowel I says ih as in Igloo!", phoneticCue = "ih", primaryColorHex = 0xFF06B6D4),
                GameItemOption("vow_o", "O", "🐙", "Short Vowel /o/ • Octopus", "Vowel O says aw as in Octopus!", phoneticCue = "aw", primaryColorHex = 0xFFF97316),
                GameItemOption("vow_u", "U", "☂️", "Short Vowel /u/ • Umbrella", "Vowel U says uh as in Umbrella!", phoneticCue = "uh", primaryColorHex = 0xFF8B5CF6)
            )
            9 -> listOf(       // w07_phonics_10: Three-Letter Word Builder (CVC Blending)
                GameItemOption("cvc_cat", "CAT", "🐱", "C - A - T • Cozy Cat", "C... A... T blends CAT!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("cvc_dog", "DOG", "🐶", "D - O - G • Happy Dog", "D... O... G blends DOG!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("cvc_sun", "SUN", "☀️", "S - U - N • Golden Sun", "S... U... N blends SUN!", primaryColorHex = 0xFFEAB308),
                GameItemOption("cvc_bus", "BUS", "🚌", "B - U - S • Yellow Bus", "B... U... S blends BUS!", primaryColorHex = 0xFF10B981),
                GameItemOption("cvc_hat", "HAT", "👒", "H - A - T • Sun Hat", "H... A... T blends HAT!", primaryColorHex = 0xFFEC4899),
                GameItemOption("cvc_pig", "PIG", "🐷", "P - I - G • Pink Pig", "P... I... G blends PIG!", primaryColorHex = 0xFFEF4444)
            )
            10 -> aToM + aToM.map { it.copy(id = "${it.id}_low", label = it.label.lowercase(), categoryOrTarget = "lowercase ${it.label.lowercase()} • ${it.categoryOrTarget}") } // 26 steps A-M & a-m
            else -> nToZ + nToZ.map { it.copy(id = "${it.id}_low", label = it.label.lowercase(), categoryOrTarget = "lowercase ${it.label.lowercase()} • ${it.categoryOrTarget}") } // 26 steps N-Z & n-z
        }
    }

    /**
     * Explicit, title-accurate Math & Number items for all 10 activities in World 08 (`w08_math_1..10`).
     */
    private fun createMathItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // w08_math_1: Count the Twinkling Stars
                GameItemOption("m1_s3", "Twinkling Stars", "⭐", "3", "Count 3 twinkling stars!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("m1_s4", "Shooting Stars", "🌟", "4", "Count 4 glowing stars!", primaryColorHex = 0xFFEAB308),
                GameItemOption("m1_s5", "Night Stars", "✨", "5", "Count 5 sparkling stars!", primaryColorHex = 0xFF6366F1),
                GameItemOption("m1_s6", "Golden Stars", "⭐", "6", "Count 6 golden stars!", primaryColorHex = 0xFFF97316)
            )
            1 -> listOf( // w08_math_2: Put Apples in the Basket
                GameItemOption("m2_a3", "Red Apples", "🍎", "3", "Put 3 crisp red apples in the basket!", primaryColorHex = 0xFFEF4444),
                GameItemOption("m2_a4", "Orchard Apples", "🍎", "4", "Put 4 crisp apples in the basket!", primaryColorHex = 0xFFDC2626),
                GameItemOption("m2_a5", "Green Apples", "🍏", "5", "Put 5 sweet green apples in the basket!", primaryColorHex = 0xFF10B981),
                GameItemOption("m2_a6", "Harvest Apples", "🍎", "6", "Put 6 harvest apples in the basket!", primaryColorHex = 0xFFF97316)
            )
            2 -> listOf( // w08_math_3: Count Coral Reef Fish
                GameItemOption("m3_f3", "Clownfish", "🐠", "3", "Count 3 orange reef clownfish!", primaryColorHex = 0xFFF97316),
                GameItemOption("m3_f4", "Blue Tang Fish", "🐟", "4", "Count 4 swimming blue fish!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("m3_f5", "Pufferfish", "🐡", "5", "Count 5 bubbly reef fish!", primaryColorHex = 0xFFEAB308),
                GameItemOption("m3_f6", "Sea Turtles", "🐢", "6", "Count 6 gentle sea turtles!", primaryColorHex = 0xFF10B981)
            )
            3 -> listOf( // w08_math_4: Number Hunt Meadow
                GameItemOption("m4_n3", "Meadow Flowers", "🌻", "3", "Spot 3 golden meadow flowers!", primaryColorHex = 0xFFEAB308),
                GameItemOption("m4_n4", "Ladybugs", "🐞", "4", "Spot 4 red garden ladybugs!", primaryColorHex = 0xFFEF4444),
                GameItemOption("m4_n5", "Butterflies", "🦋", "5", "Spot 5 fluttering butterflies!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("m4_n6", "Honeybees", "🐝", "6", "Spot 6 buzzing honeybees!", primaryColorHex = 0xFFF59E0B)
            )
            4 -> listOf( // w08_math_5: Two-Group Picture Addition
                GameItemOption("m5_add3", "Apples (1 + 2)", "🍎", "3", "1 apple plus 2 apples equals 3!", primaryColorHex = 0xFFEF4444),
                GameItemOption("m5_add4", "Stars (2 + 2)", "⭐", "4", "2 stars plus 2 stars equals 4!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("m5_add5", "Berries (2 + 3)", "🍓", "5", "2 berries plus 3 berries equals 5!", primaryColorHex = 0xFFEC4899),
                GameItemOption("m5_add6", "Ducks (3 + 3)", "🦆", "6", "3 ducks plus 3 ducks equals 6!", primaryColorHex = 0xFFEAB308)
            )
            5 -> listOf( // w08_math_6: Number Train Order (1–10)
                GameItemOption("m6_t2", "Train Car 2", "🚂", "2", "Count 2 train wheels!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("m6_t3", "Train Car 3", "🚃", "3", "Count 3 train cars!", primaryColorHex = 0xFF10B981),
                GameItemOption("m6_t4", "Train Car 4", "🚃", "4", "Count 4 train cars!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("m6_t5", "Train Car 5", "🚃", "5", "Count 5 train cars!", primaryColorHex = 0xFFEC4899)
            )
            6 -> listOf( // w08_math_7: More or Fewer Compare
                GameItemOption("m7_c4", "More Strawberries", "🍓", "4", "The bigger basket has 4 strawberries!", primaryColorHex = 0xFFEF4444),
                GameItemOption("m7_c5", "More Blueberries", "🫐", "5", "The bigger basket has 5 blueberries!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("m7_c6", "More Cherries", "🍒", "6", "The bigger basket has 6 sweet cherries!", primaryColorHex = 0xFFEC4899),
                GameItemOption("m7_c3", "More Oranges", "🍊", "3", "The bigger basket has 3 oranges!", primaryColorHex = 0xFFF97316)
            )
            7 -> listOf( // w08_math_8: Shape Sides Counter
                GameItemOption("m8_tri", "Triangle (3 Sides)", "🔺", "3", "A triangle has 3 straight sides and 3 corners!", primaryColorHex = 0xFF10B981),
                GameItemOption("m8_sq", "Square (4 Sides)", "🟦", "4", "A square has 4 equal sides and 4 corners!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("m8_star", "Five-Point Star", "⭐", "5", "A star has 5 shining points!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("m8_hex", "Hexagon Honeycomb", "⬢", "6", "A hexagon has 6 sides like a honeycomb!", primaryColorHex = 0xFFEAB308)
            )
            8 -> listOf( // w08_math_9: 1–50 Stepping Stones Journey
                GameItemOption("m9_st3", "Stepping Stones", "🐾", "3", "Hop across 3 stepping stones!", primaryColorHex = 0xFF10B981),
                GameItemOption("m9_st4", "River Stones", "🪨", "4", "Hop across 4 river stones!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("m9_st5", "Golden Stones", "🌟", "5", "Hop across 5 golden stones!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("m9_st6", "Meadow Stones", "🍀", "6", "Hop across 6 meadow stones!", primaryColorHex = 0xFF059669)
            )
            else -> listOf( // w08_math_10: 51–100 Big Kid Counter Journey
                GameItemOption("m10_b4", "Ten-Star Bundles", "💯", "4", "Count 4 big number bundles!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("m10_b5", "Galaxy Stars", "🌟", "5", "Count 5 galaxy star clusters!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("m10_b6", "Golden Crowns", "👑", "6", "Count 6 golden crowns!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("m10_b7", "Diamond Gems", "💎", "7", "Count 7 sparkling gems!", primaryColorHex = 0xFF06B6D4)
            )
        }
    }

    /**
     * Explicit, title-accurate Puzzle & Logic items for World 10 (`w10_puzzles_1..8`).
     */
    private fun createPuzzleItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // Wooden Animal Peg Puzzle
                GameItemOption("puz_lion", "Lion Peg Piece", "🦁", "Savanna Peg Slot", "Snap the Lion into the Savanna Peg Slot!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("puz_ele", "Elephant Peg Piece", "🐘", "Savanna Peg Slot", "Snap the Elephant into the Savanna Peg Slot!", primaryColorHex = 0xFF64748B),
                GameItemOption("puz_owl", "Owl Peg Piece", "🦉", "Forest Peg Slot", "Snap the Owl into the Forest Peg Slot!", primaryColorHex = 0xFF6366F1),
                GameItemOption("puz_fox", "Fox Peg Piece", "🦊", "Forest Peg Slot", "Snap the Fox into the Forest Peg Slot!", primaryColorHex = 0xFFF97316),
                GameItemOption("puz_dol", "Dolphin Peg Piece", "🐬", "Ocean Peg Slot", "Snap the Dolphin into the Ocean Peg Slot!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("puz_tur", "Turtle Peg Piece", "🐢", "Ocean Peg Slot", "Snap the Sea Turtle into the Ocean Peg Slot!", primaryColorHex = 0xFF10B981)
            )
            1 -> listOf( // Four-Piece Picture Jigsaw
                GameItemOption("jig_tl", "Sun Sky Corner", "☀️", "Top Jigsaw Row", "Sun Sky piece fits the Top Jigsaw Row!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("jig_tr", "Rainbow Cloud Corner", "🌈", "Top Jigsaw Row", "Rainbow Cloud fits the Top Jigsaw Row!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("jig_bl", "Train Track Corner", "🚂", "Bottom Jigsaw Row", "Train Track fits the Bottom Jigsaw Row!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("jig_br", "Flower Meadow Corner", "🌻", "Bottom Jigsaw Row", "Flower Meadow fits the Bottom Jigsaw Row!", primaryColorHex = 0xFF10B981)
            )
            2 -> listOf( // Shadow Silhouette Match
                GameItemOption("sil_apple", "Round Stem Fruit", "🍎", "Fruit Shadow", "Apple matches the round fruit shadow!", primaryColorHex = 0xFFEF4444),
                GameItemOption("sil_banana", "Curved Fruit", "🍌", "Fruit Shadow", "Banana matches the curved crescent shadow!", primaryColorHex = 0xFFEAB308),
                GameItemOption("sil_hammer", "Hammer Silhouette", "🔨", "Tool Shadow", "Hammer matches the T-shaped tool shadow!", primaryColorHex = 0xFFF97316),
                GameItemOption("sil_key", "Key Silhouette", "🔑", "Tool Shadow", "Golden Key matches the notched key shadow!", primaryColorHex = 0xFFD97706),
                GameItemOption("sil_but", "Winged Butterfly", "🦋", "Animal Shadow", "Butterfly matches the four-wing shadow!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("sil_fish", "Finned Fish", "🐠", "Animal Shadow", "Reef Fish matches the tail-fin shadow!", primaryColorHex = 0xFF06B6D4)
            )
            3 -> listOf( // Find the Missing Piece
                GameItemOption("mis_wheel", "Round Tire Wheel", "🛞", "Car Missing Piece", "The round wheel completes the red car!", primaryColorHex = 0xFFEF4444),
                GameItemOption("mis_door", "Red Front Door", "🚪", "House Missing Piece", "The front door completes the cozy cottage!", primaryColorHex = 0xFFF97316),
                GameItemOption("mis_sail", "White Wind Sail", "⛵", "Boat Missing Piece", "The white sail completes the harbor boat!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("mis_wing", "Airplane Wing", "✈️", "Plane Missing Piece", "The wing completes the sky airplane!", primaryColorHex = 0xFF3B82F6)
            )
            4 -> listOf( // Odd-One-Out Explorer
                GameItemOption("odd_apple", "Red Apple", "🍎", "Belongs in Fruit Group", "Apple belongs with the fruits!", primaryColorHex = 0xFFEF4444),
                GameItemOption("odd_pear", "Sweet Pear", "🍐", "Belongs in Fruit Group", "Pear belongs with the fruits!", primaryColorHex = 0xFF10B981),
                GameItemOption("odd_berry", "Strawberry", "🍓", "Belongs in Fruit Group", "Strawberry belongs with the fruits!", primaryColorHex = 0xFFEC4899),
                GameItemOption("odd_boot", "Silly Rainboot", "🥾", "Odd One Out!", "Haha! A rainboot is not a fruit—it's the Odd One Out!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("odd_hat", "Silly Top Hat", "🎩", "Odd One Out!", "A top hat is the silly Odd One Out!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("odd_truck", "Silly Fire Truck", "🚒", "Odd One Out!", "A fire truck is the silly Odd One Out!", primaryColorHex = 0xFFF97316)
            )
            5 -> listOf( // Smallest to Largest Lineup
                GameItemOption("lin_acorn", "1. Tiny Acorn", "🌰", "Smallest (1st)", "Tiny Acorn goes first—Smallest!", primaryColorHex = 0xFFD97706, sizeScale = 0.78f),
                GameItemOption("lin_sprout", "1. Tiny Sprout", "🌱", "Smallest (1st)", "Tiny Sprout is Smallest!", primaryColorHex = 0xFF10B981, sizeScale = 0.8f),
                GameItemOption("lin_bush", "2. Garden Bush", "🪴", "Medium (2nd)", "Potted Plant is Medium size!", primaryColorHex = 0xFF059669, sizeScale = 1.0f),
                GameItemOption("lin_flower", "2. Tall Sunflower", "🌻", "Medium (2nd)", "Sunflower is Medium size!", primaryColorHex = 0xFFEAB308, sizeScale = 1.05f),
                GameItemOption("lin_oak", "3. Giant Oak Tree", "🌳", "Largest (3rd)", "Giant Oak Tree is Largest!", primaryColorHex = 0xFF15803D, sizeScale = 1.28f),
                GameItemOption("lin_pine", "3. Tall Pine Tree", "🌲", "Largest (3rd)", "Tall Pine Tree is Largest!", primaryColorHex = 0xFF0D9488, sizeScale = 1.28f)
            )
            6 -> listOf( // What Comes Next? Sequences
                GameItemOption("seq_sun", "Next: Golden Sun", "☀️", "Sun-Moon-Sun-[?]", "Sun comes next in Sun, Moon, Sun, Sun/Moon!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("seq_moon", "Next: Crescent Moon", "🌙", "Sun-Moon-Sun-[?]", "Moon completes the alternating sky sequence!", primaryColorHex = 0xFF6366F1),
                GameItemOption("seq_red", "Next: Red Heart", "❤️", "Red-Blue-Red-[?]", "Red Heart continues the color sequence!", primaryColorHex = 0xFFEF4444),
                GameItemOption("seq_blue", "Next: Blue Heart", "💙", "Red-Blue-Red-[?]", "Blue Heart completes Red, Blue, Red, Blue!", primaryColorHex = 0xFF3B82F6)
            )
            else -> listOf( // Half & Half Symmetry Match
                GameItemOption("sym_but_l", "Left Butterfly Wing", "🦋", "Butterfly Symmetry", "Join the left and right Monarch butterfly wings!", primaryColorHex = 0xFFF97316),
                GameItemOption("sym_but_r", "Right Butterfly Wing", "🦋", "Butterfly Symmetry", "Both butterfly wings match in symmetry!", primaryColorHex = 0xFFEC4899),
                GameItemOption("sym_leaf_l", "Left Maple Leaf", "🍁", "Leaf Symmetry", "Join both halves of the autumn maple leaf!", primaryColorHex = 0xFFEF4444),
                GameItemOption("sym_leaf_r", "Right Maple Leaf", "🍁", "Leaf Symmetry", "Both halves of the leaf match!", primaryColorHex = 0xFFD97706),
                GameItemOption("sym_star_l", "Left Star Half", "⭐", "Star Symmetry", "Join both halves of the golden star!", primaryColorHex = 0xFFEAB308),
                GameItemOption("sym_star_r", "Right Star Half", "🌟", "Star Symmetry", "The five-point star is symmetrical!", primaryColorHex = 0xFFF59E0B)
            )
        }
    }

    /**
     * Explicit, title-accurate Vehicles & Helpers items for World 12 (`w12_vehicles_1..8`).
     */
    private fun createVehicleItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // Road, Rail, Sea & Sky
                GameItemOption("veh_fire", "Fire Engine", "🚒", "Road & Rail", "Fire Engine rolls on town roads!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.img_object_fire_engine_1794160812725),
                GameItemOption("veh_train", "Steam Train", "🚂", "Road & Rail", "Steam Train chugs along steel rails!", primaryColorHex = 0xFF3B82F6, drawableRes = R.drawable.img_object_steam_train_1794160812641),
                GameItemOption("veh_boat", "Sailboat", "⛵", "Sea & Sky", "Sailboat glides across ocean waves!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("veh_plane", "Airplane", "✈️", "Sea & Sky", "Airplane soars through the clouds!", primaryColorHex = 0xFF8B5CF6)
            )
            1 -> listOf( // Red, Yellow, Green Traffic Light
                GameItemOption("trf_red", "Red Light • STOP", "🛑", "Wait Safely at Curb", "Red light means STOP and wait safely!", primaryColorHex = 0xFFEF4444),
                GameItemOption("trf_yellow", "Yellow Light • SLOW", "⚠️", "Get Ready to Stop", "Yellow light means slow down and be careful!", primaryColorHex = 0xFFEAB308),
                GameItemOption("trf_green", "Green Light • GO", "🟢", "Look Both Ways & Go", "Green light means look both ways, then go!", primaryColorHex = 0xFF10B981),
                GameItemOption("trf_walk", "Crosswalk Signal", "🚸", "Hold a Grown-Up's Hand", "Always cross at the crosswalk with a grown-up!", primaryColorHex = 0xFF3B82F6)
            )
            2 -> listOf( // Community Helpers & Tools
                GameItemOption("hlp_fire", "Firefighter • Hose", "👩‍🚒", "Rescue Station", "Firefighters use water hoses and ladders to help!", primaryColorHex = 0xFFEF4444),
                GameItemOption("hlp_doc", "Doctor • Stethoscope", "👩‍⚕️", "Health Clinic", "Doctors listen to heartbeats with a stethoscope!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("hlp_chef", "Chef • Mixing Whisk", "👩‍🍳", "Town Bakery", "Chefs bake healthy bread and soup for the town!", primaryColorHex = 0xFFF97316),
                GameItemOption("hlp_teach", "Teacher • Storybook", "👩‍🏫", "Health Clinic", "Teachers help children read and explore!", primaryColorHex = 0xFF10B981)
            )
            3 -> listOf( // Town Garage Tune-Up
                GameItemOption("gar_tire", "Rubber Wheel Tire", "🛞", "Wheel Alignment", "Pump up the round rubber tires with air!", primaryColorHex = 0xFF475569),
                GameItemOption("gar_wrench", "Spark Wrench", "🔧", "Engine Tune-Up", "Tighten the bolts gently with the wrench!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("gar_wash", "Soapy Car Sponge", "🧽", "Bubbly Car Wash", "Scrub road dust off the windows with bubbles!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("gar_fuel", "Green Electric Charger", "🔋", "Clean Energy Charge", "Charge up the town bus for a clean ride!", primaryColorHex = 0xFF10B981)
            )
            4 -> listOf( // Mail Truck Delivery Route
                GameItemOption("mal_1", "Blue Letter #1", "✉️", "House #1 Mailbox", "Deliver Blue Letter #1 to House #1!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("mal_2", "Birthday Card #1", "💌", "House #1 Mailbox", "Deliver the Birthday Card to House #1!", primaryColorHex = 0xFFEC4899),
                GameItemOption("mal_3", "Brown Parcel Box", "📦", "House #2 Porch", "Deliver the Parcel Box to House #2!", primaryColorHex = 0xFFD97706),
                GameItemOption("mal_4", "Postcard #2", "📮", "House #2 Porch", "Deliver the sunny Postcard to House #2!", primaryColorHex = 0xFFEF4444)
            )
            5 -> listOf( // Sirens, Horns & Bells
                GameItemOption("sir_fire", "Fire Engine Siren", "🚒", "Wee-Oo Rescue Siren", "Wee-oo! The red fire engine rings its bell!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.img_object_fire_engine_1794160812725),
                GameItemOption("sir_amb", "Ambulance Chime", "🚑", "Medical Helper", "Nee-naw! The ambulance brings paramedics to help!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("sir_train", "Train Whistle", "🚂", "Choo-Choo Track", "Toot-toot! The steam train blows its brass whistle!", primaryColorHex = 0xFF10B981, drawableRes = R.drawable.img_object_steam_train_1794160812641),
                GameItemOption("sir_ship", "Harbor Ferry Horn", "🚢", "Deep Bay Foghorn", "Booo-oom! The harbor ship sounds its deep horn!", primaryColorHex = 0xFF06B6D4)
            )
            6 -> listOf( // Build a Rescue Truck
                GameItemOption("res_cab", "Red Driver Cab", "🚒", "Front Chassis", "Snap the Red Driver Cab onto the Front Chassis!", primaryColorHex = 0xFFEF4444),
                GameItemOption("res_siren", "Flashing Light Bar", "🚨", "Front Chassis", "Mount the Flashing Light Bar on top of the cab!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("res_ladder", "Tall Rescue Ladder", "🪜", "Rear Rescue Deck", "Attach the Tall Ladder to the Rear Rescue Deck!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("res_hose", "Coiled Water Hose", "🧯", "Rear Rescue Deck", "Pack the Water Hose on the Rear Rescue Deck!", primaryColorHex = 0xFF06B6D4)
            )
            else -> listOf( // Harbor Boat & Train Bridge
                GameItemOption("brg_sail", "Tall Sailboat", "⛵", "Sails Under Bridge", "Tall Sailboat glides through the blue water under the bridge!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("brg_tug", "Harbor Tugboat", "🚤", "Sails Under Bridge", "Little Tugboat chugs across the harbor water!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("brg_train", "Express Train", "🚂", "Rolls Over Bridge", "Express Train rolls across the high bridge tracks!", primaryColorHex = 0xFFEF4444),
                GameItemOption("brg_bus", "Town Bus", "🚌", "Rolls Over Bridge", "Yellow Town Bus drives safely over the bridge deck!", primaryColorHex = 0xFFEAB308)
            )
        }
    }

    /**
     * Explicit, title-accurate Plants & Nature items for World 14 (`w14_nature_1..8`).
     */
    private fun createNatureItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // Parts of a Flower
                GameItemOption("flw_root", "Soil Roots", "🌱", "Underground Roots", "Roots drink water and hold the plant steady in soil!", primaryColorHex = 0xFFD97706),
                GameItemOption("flw_stem", "Green Stem", "🌿", "Tall Stem & Leaves", "The strong green stem carries water up to the leaves!", primaryColorHex = 0xFF10B981),
                GameItemOption("flw_leaf", "Sun Leaf", "🍃", "Tall Stem & Leaves", "Green leaves catch warm sunshine for the plant!", primaryColorHex = 0xFF059669),
                GameItemOption("flw_petal", "Golden Petals", "🌻", "Sunny Flower Bloom", "Bright golden petals open wide at the top!", primaryColorHex = 0xFFEAB308)
            )
            2 -> listOf( // Friendly Garden Insects
                GameItemOption("ins_bee", "Bumblebee", "🐝", "Pollinator Flyers", "Bumblebees buzz from flower to flower collecting nectar!", primaryColorHex = 0xFFEAB308),
                GameItemOption("ins_but", "Butterfly", "🦋", "Pollinator Flyers", "Butterflies flutter on colorful painted wings!", primaryColorHex = 0xFFF97316),
                GameItemOption("ins_lady", "Red Ladybug", "🐞", "Leaf & Soil Helpers", "Polka-dot ladybugs protect garden leaves!", primaryColorHex = 0xFFEF4444),
                GameItemOption("ins_ant", "Garden Ant", "🐜", "Leaf & Soil Helpers", "Busy ants aerate the soil and work as a team!", primaryColorHex = 0xFF64748B)
            )
            3 -> listOf( // Tall Trees & Leaves
                GameItemOption("tre_oak", "Oak Acorn", "🌰", "Oak & Maple Woods", "Tiny acorns grow into mighty oak trees!", primaryColorHex = 0xFFD97706),
                GameItemOption("tre_maple", "Red Maple Leaf", "🍁", "Oak & Maple Woods", "Maple leaves turn scarlet and gold in autumn!", primaryColorHex = 0xFFEF4444),
                GameItemOption("tre_pine", "Evergreen Pine", "🌲", "Evergreen Forest", "Pine trees stay green all winter long!", primaryColorHex = 0xFF10B981),
                GameItemOption("tre_palm", "Coconut Palm", "🌴", "Evergreen Forest", "Palm trees sway in warm coastal breezes!", primaryColorHex = 0xFF059669)
            )
            4 -> listOf( // Four Seasons Tree
                GameItemOption("sea_spr", "Pink Blossoms", "🌸", "Spring & Summer", "Pink blossoms bloom on branches in Spring!", primaryColorHex = 0xFFEC4899),
                GameItemOption("sea_sum", "Bright Sun & Apples", "☀️", "Spring & Summer", "Warm Summer sunshine ripens orchard apples!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("sea_aut", "Golden Falling Leaf", "🍂", "Autumn & Winter", "Crisp golden leaves swirl down in Autumn!", primaryColorHex = 0xFFF97316),
                GameItemOption("sea_win", "Sparkling Snowflake", "❄️", "Autumn & Winter", "Soft white snow blankets the branches in Winter!", primaryColorHex = 0xFF06B6D4)
            )
            else -> listOf( // Compost & Garden Care
                GameItemOption("cmp_core", "Apple Core", "🍎", "Green Compost Bin", "Fruit scraps turn into rich garden soil in compost!", primaryColorHex = 0xFF10B981),
                GameItemOption("cmp_leaf", "Dry Autumn Leaves", "🍂", "Green Compost Bin", "Dry leaves mix into healthy garden compost!", primaryColorHex = 0xFFD97706),
                GameItemOption("cmp_jar", "Clean Glass Jar", "🫙", "Blue Recycle Bin", "Clean jars go into the Blue Recycle Bin!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("cmp_box", "Cardboard Box", "📦", "Blue Recycle Bin", "Cardboard is recycled into brand new paper!", primaryColorHex = 0xFF06B6D4)
            )
        }
    }

    /**
     * Explicit, title-accurate Science, Water Lab & Space items for World 15 (`w15_science_1..8`).
     * Keeps each deck at 6 items so all items fit comfortably with generous width on phone landscape.
     */
    private fun createScienceItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // w15_science_1: Water Lab: Float or Sink?
                GameItemOption("water_duck", "Rubber Duck", "🦆", "Float", "The hollow rubber duck is full of air, so it floats on top!", sinksInWater = false, explanation = "Full of light air — Floats!", primaryColorHex = 0xFFEAB308),
                GameItemOption("water_stone", "River Stone", "🪨", "Sink", "The heavy river stone is dense, so it sinks to the sandy bottom!", sinksInWater = true, explanation = "Heavy & dense — Sinks!", primaryColorHex = 0xFF64748B),
                GameItemOption("water_leaf", "Green Leaf", "🍃", "Float", "A flat green leaf is very light and rests right on the water!", sinksInWater = false, explanation = "Light & flat — Floats!", primaryColorHex = 0xFF10B981),
                GameItemOption("water_key", "Metal Key", "🔑", "Sink", "Solid metal is heavier than water, so the key sinks down!", sinksInWater = true, explanation = "Solid metal — Sinks!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("water_apple", "Crisp Apple", "🍎", "Float", "Apples have tiny air pockets inside, helping them bob and float!", sinksInWater = false, explanation = "Tiny air pockets — Floats!", primaryColorHex = 0xFFEF4444),
                GameItemOption("water_coin", "Treasure Coin", "🪙", "Sink", "Plink! The heavy metal coin sinks straight to the bottom.", sinksInWater = true, explanation = "Heavy metal — Sinks!", primaryColorHex = 0xFFD97706)
            )
            1 -> listOf( // w15_science_2: Water Lab: Boats & Stones
                GameItemOption("water_boat", "Wooden Toy Boat", "⛵", "Float", "Its curved hull pushes water aside so the boat floats!", sinksInWater = false, explanation = "Boat hull shape — Floats!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("water_anchor", "Iron Anchor", "⚓", "Sink", "Heavy iron anchors sink deep to hold ships steady!", sinksInWater = true, explanation = "Heavy iron — Sinks!", primaryColorHex = 0xFF475569),
                GameItemOption("water_sponge", "Dry Sea Sponge", "🧽", "Float", "A light dry sponge is full of tiny air bubbles that float!", sinksInWater = false, explanation = "Air pockets — Floats!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("water_gem", "Crystal Stone", "💎", "Sink", "Dense crystal rock sinks right to the tank floor!", sinksInWater = true, explanation = "Dense crystal — Sinks!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("water_ball", "Beach Ball", "🏐", "Float", "An air-filled beach ball pops right back to the surface!", sinksInWater = false, explanation = "Filled with air — Floats!", primaryColorHex = 0xFFEC4899),
                GameItemOption("water_bolt", "Steel Bolt", "🔩", "Sink", "Solid steel is much denser than water, so it sinks!", sinksInWater = true, explanation = "Solid steel — Sinks!", primaryColorHex = 0xFF6366F1)
            )
            2 -> listOf( // w15_science_3: Day Sky vs. Night Sky
                GameItemOption("sky_sun", "Golden Sun", "☀️", "Daytime Sky", "The bright Golden Sun shines in the Daytime Sky!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("sky_rainbow", "Sky Rainbow", "🌈", "Daytime Sky", "Rainbows arch across the sunny Daytime Sky!", primaryColorHex = 0xFFEC4899),
                GameItemOption("sky_cloud", "Sunbeam Cloud", "🌤️", "Daytime Sky", "White clouds drift across the blue Daytime Sky!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("sky_moon", "Crescent Moon", "🌙", "Nighttime Sky", "The glowing Crescent Moon shines in the Nighttime Sky!", primaryColorHex = 0xFF6366F1),
                GameItemOption("sky_star", "Twinkling Star", "⭐", "Nighttime Sky", "Stars twinkle brightly in the Nighttime Sky!", primaryColorHex = 0xFFEAB308),
                GameItemOption("sky_comet", "Shooting Star", "🌠", "Nighttime Sky", "Shooting stars streak across the Nighttime Sky!", primaryColorHex = 0xFF8B5CF6)
            )
            3 -> listOf( // w15_science_4: Solar System Planet Parade
                GameItemOption("pln_earth", "Blue Earth", "🌍", "Rocky Inner Planets", "Our home planet Earth has blue oceans and rocky land!", primaryColorHex = 0xFF2563EB),
                GameItemOption("pln_mars", "Red Planet Mars", "🔴", "Rocky Inner Planets", "Rusty red Mars has tall volcanoes and rocky canyons!", primaryColorHex = 0xFFEF4444),
                GameItemOption("pln_sun", "Warm Star Sun", "☀️", "Rocky Inner Planets", "The rocky inner planets orbit close to the warm Sun!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("pln_saturn", "Ringed Saturn", "🪐", "Giant Outer Planets", "Saturn wears dazzling rings of ice and rock!", primaryColorHex = 0xFFEAB308),
                GameItemOption("pln_jupiter", "Giant Jupiter", "🟠", "Giant Outer Planets", "Jupiter is the largest gas giant in our solar system!", primaryColorHex = 0xFFF97316),
                GameItemOption("pln_neptune", "Windy Neptune", "🔵", "Giant Outer Planets", "Deep blue Neptune orbits far out in space!", primaryColorHex = 0xFF06B6D4)
            )
            4 -> listOf( // w15_science_5: Weather Watcher Lab
                GameItemOption("wth_sun", "Warm Thermometer", "🌡️", "Sunny & Warm", "A rising red thermometer shows warm sunny weather!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("wth_hat", "Sun Visor", "☀️", "Sunny & Warm", "Bright sunshine warms the meadow and garden!", primaryColorHex = 0xFFEAB308),
                GameItemOption("wth_rain", "Rain Gauge Cloud", "🌧️", "Rain & Wind", "Rain clouds bring fresh water drops for plants!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("wth_wind", "Wind Vane", "🍃", "Rain & Wind", "Breezy wind spins the weather vane!", primaryColorHex = 0xFF10B981),
                GameItemOption("wth_snow", "Ice Snowflake", "❄️", "Snow & Frost", "Six-pointed snowflakes form when clouds are freezing cold!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("wth_snowman", "Frosty Snowman", "⛄", "Snow & Frost", "We roll snowy spheres on a frosty winter day!", primaryColorHex = 0xFF6366F1)
            )
            else -> listOf( // Dinosaur & Cycle fallback items
                GameItemOption("sci_dino_1", "T-Rex Footprint", "🦖", "Fossil Discovery", "Uncover giant dinosaur fossils in the sand!", primaryColorHex = 0xFF10B981),
                GameItemOption("sci_dino_2", "Sauropod Leaf", "🦕", "Plant Eater", "Long-necked dinosaurs munched treetop ferns!", primaryColorHex = 0xFF059669),
                GameItemOption("sci_dino_3", "Fossil Bone", "🦴", "Fossil Discovery", "Paleontologists brush sand off ancient bones!", primaryColorHex = 0xFFD97706)
            )
        }
    }

    /**
     * Explicit, title-accurate Feelings & Friendship items for World 17 (`w17_feelings_1..8`).
     */
    private fun createFeelingsItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf(
                GameItemOption("feel_happy", "Happy", "😊", "Smiling & Joyful", "Sharing toys and playing together feels happy!", primaryColorHex = 0xFF10B981),
                GameItemOption("feel_calm", "Calm", "😌", "Peaceful Star Breath", "Taking a slow deep breath helps our body feel calm.", primaryColorHex = 0xFF06B6D4),
                GameItemOption("feel_surprised", "Surprised", "😮", "Wide Eyes Wow", "Seeing a rainbow pop out makes us feel surprised!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("feel_proud", "Proud", "🌟", "Finished the Puzzle", "Trying again when a puzzle is tricky makes us proud!", primaryColorHex = 0xFF8B5CF6)
            )
            1 -> listOf(
                GameItemOption("shr_turns", "Take Turns", "🤝", "Share the Blocks", "Let's take turns stacking the tall tower together!", primaryColorHex = 0xFF10B981),
                GameItemOption("shr_crayons", "Share Crayons", "🖍️", "Pass the Rainbow Box", "Here, you can use the red crayon while I use blue!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("shr_ball", "Roll Ball Back", "⚽", "Play Catch Together", "Rolling the ball back and forth is twice the fun!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("shr_swing", "Count to 10 Turn", "🎠", "Playground Fairness", "Counting turns on the swing helps everyone play!", primaryColorHex = 0xFFEC4899)
            )
            else -> listOf(
                GameItemOption("knd_help", "Help Up", "🤗", "Offer a Helping Hand", "Offering a gentle hand when a friend trips is super kind!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("knd_please", "Say Please", "💛", "Polite Magic Word", "Saying 'Please' and 'Thank you' warms everyone's heart!", primaryColorHex = 0xFF10B981),
                GameItemOption("knd_breath", "Star Breath", "🌟", "Inhale & Exhale", "Breathing in like smelling a flower calms big feelings!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("knd_listen", "Listening Ears", "👂", "Listen to Friends", "Listening when a friend speaks shows we care!", primaryColorHex = 0xFF8B5CF6)
            )
        }
    }

    /**
     * Explicit, title-accurate Building & Design items for World 19 (`w19_building_1..8`).
     */
    private fun createBuildingItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // Rainbow Block Tower
                GameItemOption("bld_base", "Wide Red Base", "🧱", "1. Bottom Foundation", "Wide heavy blocks build a strong bottom foundation!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bld_mid", "Blue Pillar", "🟦", "2. Middle Column", "Blue pillars stack tall in the middle!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bld_top", "Gold Star Spire", "🌟", "3. Top Tower Peak", "The golden star crowns the top of the tower!", primaryColorHex = 0xFFF59E0B)
            )
            1 -> listOf( // River Bridge Builder
                GameItemOption("br_pier", "Stone River Piers", "🪨", "1. Bridge Supports", "Strong stone piers stand in the riverbed!", primaryColorHex = 0xFF64748B),
                GameItemOption("br_arch", "Steel Bridge Arch", "🌉", "2. Main Span Arch", "The curved arch spans from bank to bank!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("br_rail", "Wooden Train Rails", "🛤️", "3. Top Crossing Deck", "Rails on top let the little train cross safely!", primaryColorHex = 0xFFD97706)
            )
            7 -> listOf( // Starship Rocket Assembly
                GameItemOption("rck_boost", "Thruster Boosters", "🔥", "1. Bottom Engines", "Boosters go at the bottom to lift the rocket!", primaryColorHex = 0xFFF97316),
                GameItemOption("rck_cabin", "Astronaut Cabin", "🛸", "2. Middle Crew Pod", "The round window cabin holds the astronauts!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("rck_nose", "Red Nose Cone", "🚀", "3. Top Nose Cone", "The pointed nose cone steers toward the stars!", primaryColorHex = 0xFFEF4444)
            )
            else -> listOf( // Cozy Cottage Home / Castle / Garden / Track
                GameItemOption("cot_base", "Brick Foundation", "🧱", "1. Bottom Base", "Strong brick walls go at the bottom!", primaryColorHex = 0xFFEF4444),
                GameItemOption("cot_win", "Sunny Window & Door", "🪟", "2. Middle Walls", "Bright windows let the morning sunshine in!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("cot_roof", "Chimney Gable Roof", "🏡", "3. Top Roof", "The angled roof keeps rain off the cozy home!", primaryColorHex = 0xFFF59E0B)
            )
        }
    }

    /**
     * Explicit, title-accurate Festivals & Seasons items for World 23 (`w23_festivals_1..6`).
     */
    private fun createFestivalItemsForIndex(idx: Int): List<GameItemOption> {
        return when (idx) {
            0 -> listOf( // Spring, Summer, Autumn, Winter
                GameItemOption("fes_spr", "Cherry Blossom", "🌸", "Spring & Summer", "Pink blossoms open in warm Spring!", primaryColorHex = 0xFFEC4899),
                GameItemOption("fes_sum", "Beach Sunshine", "☀️", "Spring & Summer", "Golden sunshine brightens Summer days!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("fes_aut", "Harvest Pumpkin", "🎃", "Autumn & Winter", "Orange pumpkins ripen in crisp Autumn!", primaryColorHex = 0xFFF97316),
                GameItemOption("fes_win", "Snowy Mittens", "🧤", "Autumn & Winter", "Cozy mittens keep hands warm in Winter!", primaryColorHex = 0xFF06B6D4)
            )
            1 -> listOf( // Birthday Party Table Setup
                GameItemOption("bdy_cake", "Berry Birthday Cake", "🎂", "Centerpiece Table", "The birthday cake sits right in the center!", primaryColorHex = 0xFFEC4899),
                GameItemOption("bdy_plate", "Polka-Dot Plates", "🍽️", "Centerpiece Table", "Set a colorful plate for every friend!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("bdy_bal", "Party Balloons", "🎈", "Festive Decorations", "Tie floating balloons above the party banner!", primaryColorHex = 0xFFEF4444),
                GameItemOption("bdy_hat", "Sparkle Party Hats", "🥳", "Festive Decorations", "Everyone wears a cheerful party cone hat!", primaryColorHex = 0xFFF59E0B)
            )
            else -> listOf( // Kindness Gift / Lantern / Harvest / Winter
                GameItemOption("gft_box", "Wrapped Gift Box", "🎁", "Kindness Gift Basket", "Wrap the gift box in recycled star paper!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("gft_card", "Handmade Heart Card", "💌", "Kindness Gift Basket", "Draw a smiling picture on the thank-you card!", primaryColorHex = 0xFFEC4899),
                GameItemOption("gft_lan", "Glowing Paper Lantern", "🏮", "Festival Lights", "Warm lanterns glow softly in the evening!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("gft_star", "Sparkling Star Light", "✨", "Festival Lights", "Twinkling lights celebrate kindness together!", primaryColorHex = 0xFFEAB308)
            )
        }
    }

    /**
     * Explicit, title-accurate Little Chef sorting items for `w24_chef_7` and `w24_chef_8`.
     */
    private fun createChefSortingItemsForIndex(idx: Int): List<GameItemOption> {
        return if (idx == 6) {
            // w24_chef_7: Healthy Food Groups Sorter
            listOf(
                GameItemOption("fdg_apple", "Red Apple", "🍎", "Fruits & Veggies", "Apples give us vitamins in Fruits & Veggies!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fdg_broc", "Green Broccoli", "🥦", "Fruits & Veggies", "Broccoli is a super green Vegetable!", primaryColorHex = 0xFF10B981),
                GameItemOption("fdg_bread", "Whole-Grain Bread", "🍞", "Grains & Energy", "Whole-grain bread gives us steady energy!", primaryColorHex = 0xFFD97706),
                GameItemOption("fdg_rice", "Steamed Rice Bowl", "🍚", "Grains & Energy", "Warm rice belongs in the Grains group!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("fdg_milk", "Fresh Milk Cup", "🥛", "Dairy & Protein", "Milk helps build strong bones and teeth!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fdg_egg", "Sunny Farm Egg", "🥚", "Dairy & Protein", "Eggs give us healthy protein to grow!", primaryColorHex = 0xFF8B5CF6)
            )
        } else {
            // w24_chef_8: Little Chef Kitchen Organizer
            listOf(
                GameItemOption("korg_pot", "Soup Cooking Pot", "🍲", "Lower Pot Cupboard", "Heavy cooking pots go in the Lower Pot Cupboard!", primaryColorHex = 0xFFF97316),
                GameItemOption("korg_pan", "Skillet Pan", "🍳", "Lower Pot Cupboard", "The skillet pan rests in the Lower Pot Cupboard!", primaryColorHex = 0xFF64748B),
                GameItemOption("korg_bowl", "Salad Mixing Bowl", "🥣", "Upper Dish Shelf", "Mixing bowls stack neatly on the Upper Dish Shelf!", primaryColorHex = 0xFF10B981),
                GameItemOption("korg_plate", "Dinner Plate", "🍽️", "Upper Dish Shelf", "Plates belong on the Upper Dish Shelf!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("korg_spoon", "Wooden Stir Spoon", "🥄", "Utensil Drawer", "Wooden spoons go in the Utensil Drawer!", primaryColorHex = 0xFFD97706),
                GameItemOption("korg_fork", "Silver Fork", "🍴", "Utensil Drawer", "Forks line up neatly in the Utensil Drawer!", primaryColorHex = 0xFF8B5CF6)
            )
        }
    }

    /**
     * All 16 distinct 6-card Flashcard Decks (`w25_flashcards_1..16`).
     * Eliminates `deckIndex % 4` so every single deck matches its title 100%.
     */
    private fun createFlashcardDeckItems(deckIndex: Int): List<GameItemOption> {
        return when (deckIndex) {
            0 -> listOf( // Deck 1: Vehicles & Sirens
                GameItemOption("fc1_fire", "Fire Engine", "🚒", "Vehicles & Sirens", "Firefighters ride the red fire engine to help town friends!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.atlas_vehicle_fire_engine),
                GameItemOption("fc1_train", "Steam Train", "🚂", "Vehicles & Sirens", "Choo-choo! The steam train crosses bridges and valleys.", primaryColorHex = 0xFF3B82F6, drawableRes = R.drawable.img_object_steam_train_1794160812641),
                GameItemOption("fc1_amb", "Ambulance", "🚑", "Vehicles & Sirens", "Paramedics ride the ambulance to care for people.", primaryColorHex = 0xFF10B981, drawableRes = R.drawable.atlas_vehicle_ambulance),
                GameItemOption("fc1_boat", "Sailboat", "⛵", "Vehicles & Sirens", "Gentle breezes push sailboats across the blue bay.", primaryColorHex = 0xFF06B6D4),
                GameItemOption("fc1_plane", "Airplane", "✈️", "Vehicles & Sirens", "Strong wings lift the airplane high above the clouds.", primaryColorHex = 0xFF8B5CF6, drawableRes = R.drawable.atlas_vehicle_aeroplane),
                GameItemOption("fc1_tractor", "Farm Tractor", "🚜", "Vehicles & Sirens", "Big tires help the green tractor plow the farm fields.", primaryColorHex = 0xFF059669, drawableRes = R.drawable.atlas_vehicle_tractor)
            )
            1 -> listOf( // Deck 2: Orchard Fruits
                GameItemOption("fc2_apple", "Crisp Red Apple", "🍎", "Orchard Fruits", "Apples grow on orchard trees and crunch when you bite!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.atlas_fruit_apple),
                GameItemOption("fc2_banana", "Yellow Banana", "🍌", "Orchard Fruits", "Bananas come in their own easy-to-peel yellow jacket!", primaryColorHex = 0xFFEAB308, drawableRes = R.drawable.atlas_fruit_banana),
                GameItemOption("fc2_straw", "Sweet Strawberry", "🍓", "Orchard Fruits", "Strawberries wear their tiny seeds on the outside!", primaryColorHex = 0xFFEC4899, drawableRes = R.drawable.atlas_fruit_strawberry),
                GameItemOption("fc2_mango", "Golden Mango", "🥭", "Orchard Fruits", "Mangoes are sweet, juicy tropical stone fruits!", primaryColorHex = 0xFFF59E0B, drawableRes = R.drawable.atlas_fruit_mango),
                GameItemOption("fc2_orange", "Citrus Orange", "🍊", "Orchard Fruits", "Oranges are packed with sunshine and Vitamin C!", primaryColorHex = 0xFFF97316, drawableRes = R.drawable.atlas_fruit_orange),
                GameItemOption("fc2_grapes", "Purple Grapes", "🍇", "Orchard Fruits", "Grapes grow in bunches on leafy sunny vines!", primaryColorHex = 0xFF8B5CF6, drawableRes = R.drawable.atlas_fruit_grapes)
            )
            2 -> listOf( // Deck 3: Garden Vegetables
                GameItemOption("fc3_carrot", "Crunchy Carrot", "🥕", "Garden Vegetables", "Carrots are orange roots that grow underground with green tops!", primaryColorHex = 0xFFF97316, drawableRes = R.drawable.atlas_veg_carrot),
                GameItemOption("fc3_broccoli", "Green Broccoli", "🥦", "Garden Vegetables", "Broccoli florets look just like tiny green forest trees!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc3_tomato", "Garden Tomato", "🍅", "Garden Vegetables", "Plump red tomatoes ripen on sunny garden vines!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.atlas_veg_tomato),
                GameItemOption("fc3_corn", "Sweet Corn", "🌽", "Garden Vegetables", "Golden corn kernels grow in rows inside green husks!", primaryColorHex = 0xFFEAB308),
                GameItemOption("fc3_peas", "Green Garden Peas", "🫛", "Garden Vegetables", "Round green peas cozy up together inside a pod!", primaryColorHex = 0xFF059669, drawableRes = R.drawable.atlas_veg_peas),
                GameItemOption("fc3_pumpkin", "Orange Pumpkin", "🎃", "Garden Vegetables", "Pumpkins grow on trailing vines in the autumn patch!", primaryColorHex = 0xFFD97706, drawableRes = R.drawable.atlas_veg_pumpkin)
            )
            3 -> listOf( // Deck 4: Everyday Foods
                GameItemOption("fc4_bread", "Whole-Grain Bread", "🍞", "Everyday Foods", "Warm baked bread is delicious for morning toast and sandwiches!", primaryColorHex = 0xFFD97706),
                GameItemOption("fc4_cheese", "Cheddar Cheese", "🧀", "Everyday Foods", "Cheese is made from farm milk and tastes great on crackers!", primaryColorHex = 0xFFEAB308),
                GameItemOption("fc4_milk", "Fresh Milk", "🥛", "Everyday Foods", "Cool milk helps build strong bones and healthy teeth!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fc4_soup", "Warm Veggie Soup", "🍲", "Everyday Foods", "A cozy bowl of vegetable soup warms us up on chilly days!", primaryColorHex = 0xFFF97316),
                GameItemOption("fc4_oats", "Morning Oatmeal", "🥣", "Everyday Foods", "Warm porridge oats with berries make a great breakfast!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc4_rice", "Fluffy Rice", "🍚", "Everyday Foods", "Steamed grains of rice nourish families all around the world!", primaryColorHex = 0xFF8B5CF6)
            )
            4 -> listOf( // Deck 5: Community Helpers
                GameItemOption("fc5_doc", "Caring Doctor", "👩‍⚕️", "Community Helpers", "Doctors help us stay healthy and strong at the clinic!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fc5_fire", "Brave Firefighter", "👩‍🚒", "Community Helpers", "Firefighters keep our neighborhood safe from fires!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fc5_teach", "Kind Teacher", "👩‍🏫", "Community Helpers", "Teachers share stories, numbers, and songs at school!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc5_chef", "Town Chef", "👩‍🍳", "Community Helpers", "Chefs cook nutritious meals in the kitchen!", primaryColorHex = 0xFFF97316),
                GameItemOption("fc5_farm", "Orchard Farmer", "👩‍🌾", "Community Helpers", "Farmers plant seeds and harvest fruits and vegetables!", primaryColorHex = 0xFF059669),
                GameItemOption("fc5_pilot", "Sky Pilot", "🧑‍✈️", "Community Helpers", "Pilots fly airplanes safely across the clouds!", primaryColorHex = 0xFF6366F1)
            )
            5 -> listOf( // Deck 6: Family Words
                GameItemOption("fc6_mom", "Loving Mother", "👩", "Family Words", "Mom gives warm hugs and reads bedtime stories!", primaryColorHex = 0xFFEC4899),
                GameItemOption("fc6_dad", "Caring Father", "👨", "Family Words", "Dad plays games and helps us build tall block towers!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fc6_sis", "Helpful Sister", "👧", "Family Words", "Sisters share giggles, drawings, and outdoor adventures!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("fc6_bro", "Playful Brother", "👦", "Family Words", "Brothers race bikes and share building blocks!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc6_gma", "Wise Grandma", "👵", "Family Words", "Grandma tells wonderful stories and bakes sweet treats!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("fc6_gpa", "Kind Grandpa", "👴", "Family Words", "Grandpa shows us garden birds and wooden puzzles!", primaryColorHex = 0xFF06B6D4)
            )
            6 -> listOf( // Deck 7: Around the Home
                GameItemOption("fc7_bed", "Cozy Bed", "🛏️", "Around the Home", "We rest our head on a soft pillow in our cozy bed!", primaryColorHex = 0xFF6366F1, drawableRes = R.drawable.atlas_everyday_bed),
                GameItemOption("fc7_chair", "Reading Chair", "🪑", "Around the Home", "We sit comfortably on a wooden chair at the table!", primaryColorHex = 0xFFD97706, drawableRes = R.drawable.atlas_everyday_chair),
                GameItemOption("fc7_lamp", "Warm Table Lamp", "💡", "Around the Home", "Switching on the lamp shines a warm reading light!", primaryColorHex = 0xFFEAB308),
                GameItemOption("fc7_win", "Sunny Window", "🪟", "Around the Home", "We look through the window to see trees and birds outside!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("fc7_door", "Front Door", "🚪", "Around the Home", "The front door welcomes family home!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fc7_books", "Bookshelf", "📚", "Around the Home", "Our favorite storybooks stand side by side on the shelf!", primaryColorHex = 0xFF10B981)
            )
            7 -> listOf( // Deck 8: My Body Parts
                GameItemOption("fc8_hands", "Clapping Hands", "🖐️", "My Body Parts", "Our two hands wave hello, paint pictures, and clap!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("fc8_feet", "Stepping Feet", "🦶", "My Body Parts", "Our two feet help us walk, tiptoe, jump, and run!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc8_eyes", "Looking Eyes", "👀", "My Body Parts", "Our eyes see colors, books, and smiling faces!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fc8_ears", "Listening Ears", "👂", "My Body Parts", "Our ears hear music, birdsong, and kind words!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("fc8_nose", "Smelling Nose", "👃", "My Body Parts", "Our nose smells fresh flowers and warm bakery bread!", primaryColorHex = 0xFFF97316),
                GameItemOption("fc8_smile", "Happy Smile", "😁", "My Body Parts", "We brush our teeth so our smile stays bright and strong!", primaryColorHex = 0xFFEC4899)
            )
            8 -> listOf( // Deck 9: Weather & Sky
                GameItemOption("fc9_sun", "Golden Sun", "☀️", "Weather & Sky", "The warm sun lights up the day and helps plants grow!", primaryColorHex = 0xFFF59E0B, drawableRes = R.drawable.atlas_nature_sun),
                GameItemOption("fc9_rain", "Rain Cloud", "🌧️", "Weather & Sky", "Pitter-patter raindrops water the thirsty meadow!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fc9_bow", "Sky Rainbow", "🌈", "Weather & Sky", "Sunlight shining through raindrops paints a rainbow!", primaryColorHex = 0xFFEC4899, drawableRes = R.drawable.atlas_nature_rainbow),
                GameItemOption("fc9_cloud", "Fluffy Cloud", "☁️", "Weather & Sky", "White clouds float like cotton across the blue sky!", primaryColorHex = 0xFF06B6D4, drawableRes = R.drawable.atlas_nature_cloud),
                GameItemOption("fc9_snow", "Winter Snowflake", "❄️", "Weather & Sky", "Every crystal snowflake has six delicate points!", primaryColorHex = 0xFF6366F1),
                GameItemOption("fc9_wind", "Gentle Breeze", "🍃", "Weather & Sky", "Moving air makes kites fly and leaves dance!", primaryColorHex = 0xFF10B981)
            )
            9 -> listOf( // Deck 10: Musical Instruments
                GameItemOption("fc10_piano", "Grand Piano", "🎹", "Musical Instruments", "Pressing piano keys makes little felt hammers tap strings!", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("fc10_xylo", "Bright Xylophone", "🎼", "Musical Instruments", "Tapping wooden bars with mallets rings out clear notes!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc10_drum", "Rhythm Drum", "🥁", "Musical Instruments", "Tapping the drumhead sends bouncy beats through the air!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fc10_guitar", "Acoustic Guitar", "🎸", "Musical Instruments", "Strumming six guitar strings plays warm songs!", primaryColorHex = 0xFFF97316),
                GameItemOption("fc10_bell", "Golden Handbell", "🔔", "Musical Instruments", "Ringing a brass bell makes a bright sparkling chime!", primaryColorHex = 0xFFEAB308),
                GameItemOption("fc10_trumpet", "Brass Trumpet", "🎺", "Musical Instruments", "Blowing into a trumpet plays a joyful fanfare!", primaryColorHex = 0xFF3B82F6)
            )
            10 -> listOf( // Deck 11: Feelings & Emotions
                GameItemOption("fc11_happy", "Happy & Joyful", "😊", "Feelings & Emotions", "We smile wide when we feel happy and playful!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc11_calm", "Peaceful & Calm", "😌", "Feelings & Emotions", "Slow deep breaths help our heart and body feel calm!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("fc11_wow", "Curious & Surprised", "😮", "Feelings & Emotions", "Discovering something new makes us feel amazed!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("fc11_sleepy", "Cozy & Sleepy", "😴", "Feelings & Emotions", "Yawning tells us our body is ready for a cozy rest!", primaryColorHex = 0xFF6366F1),
                GameItemOption("fc11_proud", "Confident & Proud", "🌟", "Feelings & Emotions", "Practising a skill step by step makes us feel proud!", primaryColorHex = 0xFFEAB308),
                GameItemOption("fc11_kind", "Caring & Kind", "🤗", "Feelings & Emotions", "Sharing a hug or kind word helps everyone feel loved!", primaryColorHex = 0xFFEC4899)
            )
            11 -> listOf( // Deck 12: Colors & Shapes
                GameItemOption("fc12_circle", "Red Circle", "🔴", "Colors & Shapes", "A circle is completely round with no corners at all!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.atlas_shape_circle),
                GameItemOption("fc12_square", "Blue Square", "🟦", "Colors & Shapes", "A square has four equal straight sides and four corners!", primaryColorHex = 0xFF2563EB, drawableRes = R.drawable.atlas_shape_square),
                GameItemOption("fc12_star", "Yellow Star", "⭐", "Colors & Shapes", "A golden star shines with five bright points!", primaryColorHex = 0xFFEAB308, drawableRes = R.drawable.atlas_shape_star),
                GameItemOption("fc12_heart", "Green Heart", "💚", "Colors & Shapes", "A heart has two smooth top curves and a point at the bottom!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc12_tri", "Orange Triangle", "🔺", "Colors & Shapes", "A triangle has three straight sides and three corners!", primaryColorHex = 0xFFF97316),
                GameItemOption("fc12_dia", "Purple Diamond", "🟣", "Colors & Shapes", "A diamond shape looks just like a soaring sky kite!", primaryColorHex = 0xFF8B5CF6)
            )
            12 -> listOf( // Deck 13: Places in Town
                GameItemOption("fc13_park", "Sunny Town Park", "🏞️", "Places in Town", "Friends play on swings and grass at the town park!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc13_school", "Wonder Preschool", "🏫", "Places in Town", "Children learn, sing, and paint together at school!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fc13_bakery", "Corner Bakery", "🥖", "Places in Town", "Warm loaves of bread and muffins bake at the bakery!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("fc13_fire", "Fire Station", "🚒", "Places in Town", "Red fire engines stay polished and ready at the station!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fc13_harbor", "Bay Harbor", "⚓", "Places in Town", "Sailboats and ferries dock calmly at the town harbor!", primaryColorHex = 0xFF06B6D4),
                GameItemOption("fc13_lib", "Town Library", "🏛️", "Places in Town", "Thousands of picture books wait to be borrowed at the library!", primaryColorHex = 0xFF8B5CF6)
            )
            13 -> listOf( // Deck 14: Fun Opposites
                GameItemOption("fc14_big", "Big Elephant / Little Mouse", "🐘", "Fun Opposites", "An elephant is BIG, and a meadow mouse is LITTLE!", primaryColorHex = 0xFF3B82F6),
                GameItemOption("fc14_fast", "Fast Rocket / Slow Turtle", "🚀", "Fun Opposites", "A rocket zooms FAST, and a garden turtle walks SLOW!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fc14_up", "Up in Sky / Down in Sea", "🎈", "Fun Opposites", "Balloons float UP high, and anchors sink DOWN low!", primaryColorHex = 0xFFF59E0B),
                GameItemOption("fc14_hot", "Warm Sun / Cold Snow", "☀️", "Fun Opposites", "Summer sunshine is WARM, and winter snow is COLD!", primaryColorHex = 0xFFF97316),
                GameItemOption("fc14_day", "Bright Day / Starry Night", "🌙", "Fun Opposites", "We play in bright DAYLIGHT and sleep in starry NIGHT!", primaryColorHex = 0xFF6366F1),
                GameItemOption("fc14_open", "Open Box / Closed Box", "🎁", "Fun Opposites", "We OPEN the lid to peek inside and CLOSE it to tidy up!", primaryColorHex = 0xFF10B981)
            )
            14 -> listOf( // Deck 15: Feathered Birds
                GameItemOption("fc15_owl", "Woodland Owl", "🦉", "Feathered Birds", "Owls fly softly on velvety feathers at dusk!", primaryColorHex = 0xFF8B5CF6, drawableRes = R.drawable.atlas_bird_owl),
                GameItemOption("fc15_eagle", "Golden Eagle", "🦅", "Feathered Birds", "Eagles soar high above mountain peaks on wide wings!", primaryColorHex = 0xFFD97706),
                GameItemOption("fc15_penguin", "Emperor Penguin", "🐧", "Feathered Birds", "Penguins waddle on snow and swim swiftly in the sea!", primaryColorHex = 0xFF0284C7),
                GameItemOption("fc15_parrot", "Rainbow Parrot", "🦜", "Feathered Birds", "Tropical parrots have bright feathers and curved beaks!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.atlas_bird_parrot),
                GameItemOption("fc15_flamingo", "Pink Flamingo", "🦩", "Feathered Birds", "Flamingos balance gracefully on one long leg in lagoons!", primaryColorHex = 0xFFEC4899, drawableRes = R.drawable.atlas_bird_flamingo),
                GameItemOption("fc15_swan", "White Pond Swan", "🦢", "Feathered Birds", "Swans glide smoothly across quiet mirror ponds!", primaryColorHex = 0xFF06B6D4)
            )
            else -> listOf( // Deck 16: Friendly Farm Animals
                GameItemOption("fc16_cow", "Meadow Cow", "🐄", "Friendly Farm Animals", "Moooo! Friendly cows graze on sweet green clover!", primaryColorHex = 0xFF10B981, drawableRes = R.drawable.atlas_domestic_cow),
                GameItemOption("fc16_horse", "Galloping Horse", "🐴", "Friendly Farm Animals", "Neigh! Horses trot happily around the wooden paddock!", primaryColorHex = 0xFFD97706, drawableRes = R.drawable.atlas_domestic_horse),
                GameItemOption("fc16_sheep", "Woolly Sheep", "🐑", "Friendly Farm Animals", "Baa-baa! Fluffy sheep wear soft warm wool coats!", primaryColorHex = 0xFF3B82F6, drawableRes = R.drawable.atlas_domestic_sheep),
                GameItemOption("fc16_duck", "Yellow Duckling", "🦆", "Friendly Farm Animals", "Quack-quack! Ducks paddle with orange webbed feet!", primaryColorHex = 0xFFEAB308),
                GameItemOption("fc16_goat", "Mountain Goat", "🐐", "Friendly Farm Animals", "Meh-eh! Curious goats love climbing rocky hills!", primaryColorHex = 0xFF64748B, drawableRes = R.drawable.atlas_domestic_goat),
                GameItemOption("fc16_rooster", "Morning Rooster", "🐓", "Friendly Farm Animals", "Cock-a-doodle-doo! The rooster greets the sunrise!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.atlas_bird_rooster)
            )
        }
    }

    /**
     * Bespoke 3-page Read-Aloud Storybooks for World 09 (`w09_stories_1..8`).
     */
    private fun createReadAloudStoryPages(idx: Int, title: String, emoji: String): List<StoryPageSpec> {
        return when (idx) {
            0 -> listOf(
                StoryPageSpec(1, "Sunny's Missing Magnifying Glass", "🦁 🔍 🌿", "Sunny the Cub wants to count polka-dots on a ladybug, but his magnifying glass is missing! He checks by the big oak tree.", "Where should Sunny look next?", listOf("Check by the sunflower patch!", "Ask Bramble Bunny kindly"), 0, "Ask your child what tools help us look closely at nature."),
                StoryPageSpec(2, "Sparkle by the Sunflowers", "🌻 ✨ 🐰", "Bramble Bunny points to a bright sparkle under the tallest sunflower. 'There it is, Sunny! The sunlight is shining right through the lens!'", "What should Sunny say to Bramble?", listOf("Thank you for helping, Bramble!", "Let's share the magnifying glass"), 0, "Practice saying thank you when a friend helps find something."),
                StoryPageSpec(3, "Counting Ladybug Dots Together", "🐞 🦁 🐰", "Together, Sunny and Bramble count one, two, three, four, five tiny black dots on the friendly red ladybug!", "Would you like to celebrate or read again?", listOf("Celebrate with 5 Stars!", "Read Again!"), 0, "Count five fingers together just like the five ladybug dots.")
            )
            1 -> listOf(
                StoryPageSpec(1, "Pip Owl's Glowing Lantern", "🦉 🏮 🌙", "As twilight falls over Thinking Forest, Pip the Owl lights a warm golden starlight lantern on the high branch.", "Who needs light on the forest path below?", listOf("Shine the light for Coral Otter!", "Fly down to guide friends"), 0, "Talk about how sharing makes dark evenings feel cozy."),
                StoryPageSpec(2, "Walking Home Together", "🦦 🏮 🦉", "Coral the Otter follows the warm golden beam along the river stones all the way back to the cozy burrow.", "How does Coral feel with Pip's help?", listOf("Safe, happy, and thankful!", "Ready to wave goodnight"), 0, "Ask how it feels when someone helps guide the way."),
                StoryPageSpec(3, "Starlight Goodnight", "🌟 🦉 🦦", "Pip hangs the lantern by the window as the twinkling night stars come out to watch over all the forest friends.", "Would you like to celebrate or read again?", listOf("Celebrate with 5 Stars!", "Read Again!"), 0, "Point to the stars and wish Pip Owl goodnight.")
            )
            else -> listOf(
                StoryPageSpec(
                    pageNumber = 1,
                    title = "$title • Beginning",
                    emojiScene = "$emoji 🌤️ 🌳",
                    narrationText = "One sunny morning in Little Wonder World, Sunny, Pip, Bramble, and Coral gathered for \"$title\". Everyone brought helping hands and curiosity!",
                    interactivePrompt = "How should our friends start \"$title\"?",
                    choiceOptions = listOf("Work together step by step!", "Wait without helping"),
                    correctChoiceIndex = 0,
                    parentTip = "Ask your child what they notice first in the story scene."
                ),
                StoryPageSpec(
                    pageNumber = 2,
                    title = "$title • Middle",
                    emojiScene = "✨ $emoji 🤝",
                    narrationText = "Halfway through \"$title\", the buddies took turns, shared their tools kindly, and cheered each other on!",
                    interactivePrompt = "What is the kindest thing to do when working with friends?",
                    choiceOptions = listOf("Share and help kindly!", "Keep everything to yourself"),
                    correctChoiceIndex = 0,
                    parentTip = "Talk about how taking turns makes every project more fun."
                ),
                StoryPageSpec(
                    pageNumber = 3,
                    title = "$title • Happy Finale",
                    emojiScene = "🌈 ⭐ $emoji",
                    narrationText = "Hooray! Because everyone helped with \"$title\", the adventure finished with big smiles and five golden stars!",
                    interactivePrompt = "Would you like to celebrate or read again?",
                    choiceOptions = listOf("Celebrate with 5 Stars!", "Read Again!"),
                    correctChoiceIndex = 0,
                    parentTip = "Ask your child what happened first, next, and last."
                )
            )
        }
    }

    /**
     * Explicit 3-page Safety Storybooks for World 20 (`w20_safety_1..9`).
     * Every safety choice presents the safe action at index 0 (`correctChoiceIndex = 0`) and a clear
     * unsafe distractor at index 1 so `StorybookReaderEngine` gently corrects unsafe choices.
     */
    private fun createSafetyStoryPages(title: String, emoji: String, idx: Int): List<StoryPageSpec> {
        return when (idx) {
            0 -> listOf( // w20_safety_1: Knock Knock! Door Safety
                StoryPageSpec(
                    pageNumber = 1,
                    title = "Knock Knock! Who's at the Door?",
                    emojiScene = "🚪 🔔 🏠",
                    narrationText = "Sunny the Cub is playing in the living room when someone knocks: Knock, knock, knock! Ring-a-ling goes the doorbell.",
                    interactivePrompt = "What is the safe choice when someone knocks at the door?",
                    choiceOptions = listOf("Keep the door closed!", "Open the door alone"),
                    correctChoiceIndex = 0,
                    parentTip = "Remind children that the front door stays closed until a parent or trusted adult checks."
                ),
                StoryPageSpec(
                    pageNumber = 2,
                    title = "Step Back & Call a Grown-Up",
                    emojiScene = "🦁 🗣️ 👨‍👩‍👧",
                    narrationText = "Sunny keeps the door safely shut and walks right to Mom and Dad: 'Grown-up! Someone is knocking at the door!'",
                    interactivePrompt = "Who should Sunny get right away?",
                    choiceOptions = listOf("Get a trusted grown-up!", "Unlock the door yourself"),
                    correctChoiceIndex = 0,
                    parentTip = "Practice who the trusted adults are in your home."
                ),
                StoryPageSpec(
                    pageNumber = 3,
                    title = "Safe & Smart Together!",
                    emojiScene = "🛡️ 🌟 👨‍👩‍👧",
                    narrationText = "Dad checks the window and opens the door safely together. 'Thank you, Sunny! Keeping the door closed and getting a grown-up is always the safe choice!'",
                    interactivePrompt = "What is our Door Safety rule?",
                    choiceOptions = listOf("Keep it closed & get a grown-up!", "Read Again!"),
                    correctChoiceIndex = 0,
                    parentTip = "Discussion prompt: What do we do when we hear the doorbell ring?"
                )
            )
            1 -> listOf( // w20_safety_2: Stop, Look & Listen Road Safety
                StoryPageSpec(1, "Stopping at the Curb", "🚸 🛑 👟", "Bramble Bunny and Mama Bunny walk up to the town crosswalk. Bramble stops both feet right behind the curb line.", "What do we do first at the street curb?", listOf("Stop and hold a grown-up's hand!", "Run into the street alone"), 0, "Always stop at the curb and hold an adult's hand."),
                StoryPageSpec(2, "Look Left, Right, and Left Again", "👀 🚗 🚦", "Bramble waits for the green Walk sign, then looks left, right, and left again while listening for cars.", "When is it safe to step onto the crosswalk?", listOf("When cars stop & Walk sign shines!", "Without looking both ways"), 0, "Practice looking left, right, and left together."),
                StoryPageSpec(3, "Walking Safely Across", "🐰 🤝 🌳", "Holding Mama's hand tightly, Bramble walks calmly across the white stripes to the sunny park!", "Would you like to celebrate or read again?", listOf("Safe Crosswalk Star!", "Read Again!"), 0, "Reinforce walking—never running—across the crosswalk.")
            )
            7 -> listOf( // w20_safety_8: Safe vs. Hot & Sharp Choices
                StoryPageSpec(1, "Warm Stove in the Kitchen", "🍲 🔥 ⚠️", "Little Chef sees a bubbling soup pot on the warm stove. Steam is rising from the lid!", "Should a child touch a hot pot on the stove?", listOf("Step back & ask a grown-up!", "Reach for the hot stove"), 0, "Explain that stoves and ovens stay hot and are for grown-ups."),
                StoryPageSpec(2, "Safe Tools for Little Hands", "🥣 🥄 👩‍🍳", "Dad gives Sunny a cool plastic mixing bowl and a smooth wooden spoon on the low table.", "Which kitchen tool is safe for Sunny to use?", listOf("Cool bowl & wooden spoon!", "Sharp kitchen knife"), 0, "Give children their own safe mixing bowls and wooden spoons."),
                StoryPageSpec(3, "Smart Kitchen Helpers", "🥗 🌟 👨‍👩‍👧", "By letting grown-ups handle hot pots and sharp knives, everyone cooks a delicious meal safely!", "Would you like to celebrate or read again?", listOf("Safe Kitchen Helper Star!", "Read Again!"), 0, "Review which kitchen helpers are for kids vs. adults.")
            )
            8 -> listOf( // w20_safety_9: Helmet & Seatbelt Click!
                StoryPageSpec(1, "Buckle Up in the Car Seat", "🚗 💺 🔔", "Before the family car starts rolling, Coral Otter climbs into the booster seat and pulls the straps snug.", "What sound tells us the seatbelt is fastened?", listOf("Buckle until it goes CLICK!", "Ride without a seatbelt"), 0, "Make a game of listening for the seatbelt 'Click!' every ride."),
                StoryPageSpec(2, "Helmet On for Bike Rides", "🚲 ⛑️ 🌟", "At the park bike path, Coral puts a sturdy bike helmet level on her head and snaps the chin strap.", "What protects our head when riding a bike or scooter?", listOf("Wear a buckled bike helmet!", "Ride without a helmet"), 0, "Check that two fingers fit between eyebrows and helmet rim."),
                StoryPageSpec(3, "Ready to Roll Safely!", "🦦 🚲 🏆", "With seatbelt clicked in the car and helmet buckled on the bike, Coral is ready for every adventure!", "Would you like to celebrate or read again?", listOf("Helmet & Seatbelt Champion!", "Read Again!"), 0, "Praise children every time they remember their helmet and seatbelt.")
            )
            else -> listOf(
                StoryPageSpec(1, "$title • Step 1", "$emoji 🛡️ 🏠", "In \"$title\", our Learning Buddies learn how to keep their bodies healthy, strong, and safe every day!", "What is the safest choice to make first?", listOf("Follow the safe rule with a grown-up!", "Skip the safety check"), 0, "Talk together about why this safety habit protects us."),
                StoryPageSpec(2, "$title • Step 2", "👀 $emoji 🤝", "Sunny and Pip pause to look, listen, and check with a trusted parent or teacher whenever they have a question.", "Who can always help us stay safe?", listOf("A trusted parent or teacher!", "Go off alone without asking"), 0, "Name the trusted adults in your child's daily life."),
                StoryPageSpec(3, "$title • Safe Star!", "🌟 $emoji 🏆", "Wonderful job! Practising \"$title\" keeps everyone healthy, confident, and smiling!", "Would you like to celebrate or read again?", listOf("Celebrate Safety Star!", "Read Again!"), 0, "Give a high-five for learning today's safety rule!")
            )
        }
    }

    /**
     * Bespoke 3-page Nursery Rhyme Storybooks for all 30 titles in World 27 (`w27_rhymes_1..30`).
     */
    private fun createNurseryRhymePages(idx: Int, title: String, emoji: String): List<StoryPageSpec> {
        return listOf(
            StoryPageSpec(
                pageNumber = 1,
                title = "$title • Verse 1",
                emojiScene = "$emoji 🎶 🌤️",
                narrationText = "Let's sing \"$title\" together! As the gentle music begins, our Little Wonder friends clap in rhythm and smile.",
                interactivePrompt = "Ready to sing and turn to Verse 2 of \"$title\"?",
                choiceOptions = listOf("Sing & Turn Page ▶", "Clap the Rhythm!"),
                correctChoiceIndex = 0,
                parentTip = "Clap the steady beat of \"$title\" with your child."
            ),
            StoryPageSpec(
                pageNumber = 2,
                title = "$title • Verse 2",
                emojiScene = "✨ $emoji 🎵",
                narrationText = "In the second verse of \"$title\", everyone joins the melody, pointing to the $emoji picture and humming along!",
                interactivePrompt = "Can you do the happy motion for \"$title\"?",
                choiceOptions = listOf("Do the Rhyme Motion ▶", "Hum the Melody!"),
                correctChoiceIndex = 0,
                parentTip = "Act out the rhyme gestures together as you read."
            ),
            StoryPageSpec(
                pageNumber = 3,
                title = "$title • Grand Finale",
                emojiScene = "🌈 ⭐ $emoji",
                narrationText = "Bravo! We sang all three pages of \"$title\"! Take a happy bow or tap Read Again to sing from the beginning!",
                interactivePrompt = "Would you like to celebrate or sing \"$title\" again?",
                choiceOptions = listOf("Celebrate with 5 Stars!", "Read Again!"),
                correctChoiceIndex = 0,
                parentTip = "Ask your child which rhyming words they heard in \"$title\"."
            )
        )
    }
}
