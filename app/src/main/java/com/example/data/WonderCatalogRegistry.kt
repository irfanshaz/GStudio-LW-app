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
            Triple("Interactive Animal Encyclopedia", "Explore 123 animal facts and authentic calls", "📘"),
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
            Triple("ABC Letter Twins", "Match uppercase letter cards with their twin", "🔤"),
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

        // 4. World 04: Alphabet Tracing (12 activities)
        data class TraceThemeSpec(val title: String, val sub: String, val glyph: String, val word: String)
        val tracingThemes = listOf(
            TraceThemeSpec("Uppercase A to H Tracing", "Trace big sunny letters A through H", "A", "Apple"),
            TraceThemeSpec("Uppercase I to P Tracing", "Trace big sunny letters I through P", "M", "Moon"),
            TraceThemeSpec("Uppercase Q to Z Tracing", "Trace big sunny letters Q through Z", "S", "Sun"),
            TraceThemeSpec("Lowercase a to m Tracing", "Gentle curved lowercase letters a through m", "a", "ant"),
            TraceThemeSpec("Lowercase n to z Tracing", "Gentle curved lowercase letters n through z", "b", "butterfly"),
            TraceThemeSpec("Numbers 1 to 5 Tracing", "Follow the stars to write numbers 1 to 5", "3", "Three Stars"),
            TraceThemeSpec("Numbers 6 to 10 Tracing", "Follow the stars to write numbers 6 to 10", "8", "Eight Loops"),
            TraceThemeSpec("Sunny Shapes Tracing", "Trace circles, triangles, squares, and stars", "◯", "Circle"),
            TraceThemeSpec("Pre-Writing Straight Lines", "Top-to-bottom and side-to-side steady paths", "｜", "Tall Line"),
            TraceThemeSpec("Rainbow Curves & Waves", "Smooth ocean waves and rainbow arches", "⌒", "Rainbow Arch"),
            TraceThemeSpec("Zig-Zag Mountain Paths", "Follow the mountain peaks from start to finish", "⚡", "Zig-Zag"),
            TraceThemeSpec("First Words Star Trace", "Trace CAT, SUN, DOG, and STAR letter by letter", "C", "CAT")
        )
        tracingThemes.forEachIndexed { idx, (title, sub, glyph, word) ->
            list.add(
                ActivityConfig(
                    id = "w04_tracing_${idx + 1}",
                    worldId = "w04_tracing",
                    landId = LandId.LEARNING_LAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = "✏️",
                    engineType = EngineType.TRACING_STUDIO,
                    instructionText = "Start at the green star and trace along the glowing path!",
                    showMeDemoHint = "Watch the magic finger trace from the green star to the finish!",
                    traceGlyph = glyph,
                    traceWordHint = word,
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
            Triple("First Sounds (/a/ to /m/)", "Hear the pure phonetic sound and pick its letter", "🗣️"),
            Triple("First Sounds (/n/ to /z/)", "Match /n/, /p/, /r/, /s/, /t/ to real letter tiles", "🔊"),
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
                    storyPages = createThreePageStory(title, emoji, idx),
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
                    items = createSortingItemsForIndex(idx + 2)
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
            Triple("Instrument Sound Detective", "Hear piano, drum, flute, or bell and spot the instrument", "🎺"),
            Triple("High Note vs. Low Note", "Discover high bird trills and deep tuba rumbles", "🎵")
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
                    items = createVehicleItems()
                )
            )
        }

        // 13. World 13: Early Coding (8 activities)
        val codingThemes = listOf(
            Triple("Robot Arrow Steps", "Tap Up, Down, Left, Right arrows to guide Beep-Bot", "🤖"),
            Triple("Sunny's Honey Jar Maze", "Plan 3 arrow steps to reach the golden honey pot", "🍯"),
            Triple("Coral's River Stone Path", "Guide the otter around rocks to the shell", "🐚"),
            Triple("Morning Routine Step Order", "Order Wake Up, Brush Teeth, and Eat Breakfast", "☀️"),
            Triple("Looping Color Code", "Repeat a 2-step arrow pattern to cross the bridge", "🔁"),
            Triple("Left & Right Treasure Map", "Follow directional clues to the pirate chest", "🗺️"),
            Triple("Fix the Buggy Path", "Swap the wrong arrow so the train reaches the station", "🐞"),
            Triple("Rocket Launch Countdown Code", "Place Fuel, Helmet, and Launch buttons in order", "🚀")
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
                    instructionText = "Tap the direction arrows to guide your buddy to the goal star!",
                    showMeDemoHint = "Tap Right, then Down, then press Play Route!"
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
                    items = createNatureItems()
                )
            )
        }

        // 15. World 15: Science, Water Lab & Space (8 activities)
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
            list.add(
                ActivityConfig(
                    id = "w15_science_${idx + 1}",
                    worldId = "w15_science",
                    landId = LandId.DISCOVERY_ISLAND,
                    indexInWorld = idx + 1,
                    title = title,
                    subtitle = sub,
                    emoji = emoji,
                    engineType = if (idx <= 1) EngineType.WATER_LAB_SCIENCE else EngineType.SORTING_BASKETS,
                    instructionText = if (idx <= 1) "Will it float on top or sink to the bottom? Predict and drop it in!" else "Discover science, weather, and space wonders!",
                    showMeDemoHint = "Pick Float or Sink, then drop the object into the tank to watch what happens!",
                    items = createWaterLabItems(),
                    featuredInToyBox = idx == 0
                )
            )
        }

        // 16. World 16: Pretend Play & Puppy Wash (8 activities)
        val pretendThemes = listOf(
            Triple("Puppy Wash Salon", "Brush mud, scrub bubbly soap, rinse water & towel dry!", "🐶"),
            Triple("Kind Doctor Checkup", "Listen to heartbeats, check temperature & place a star bandage", "🩺"),
            Triple("Sunny Bakery Shop", "Mix flour, roll dough, bake muffins & frost with berries", "🧁"),
            Triple("Firefighter Hose Rescue", "Put on helmet, aim water hose & put out the campfire", "🧯"),
            Triple("Caring Pet Veterinarian", "Feed the bunny, brush the kitten & soothe the puppy", "🐱"),
            Triple("Friendly Grocery Shopkeeper", "Scan fruits, pack reusable bags & count toy coins", "🛒"),
            Triple("Little Classroom Teacher", "Ring the bell, point to ABCs & hand out gold stars", "🏫"),
            Triple("Moon Base Astronaut", "Put on space suit, float in zero-G & plant a star flag", "👩‍🚀")
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
                    items = createFeelingsItems()
                )
            )
        }

        // 18. World 18: Everyday Skills (8 activities)
        val skillsThemes = listOf(
            Triple("Sparkle Toothbrushing", "Demonstrate, guided practice & independent brushing!", "🪥"),
            Triple("Bubbly Handwashing Steps", "Wet hands, scrub soap for 20 seconds, rinse & dry", "🧼"),
            Triple("Get Dressed for the Weather", "Put on socks, shoes, jacket, and hat in order", "🧥"),
            Triple("Tidy Up the Playroom", "Put blocks in bins, books on shelves, and clothes in hamper", "🧺"),
            Triple("Bedtime Routine Checklist", "Bath, pajamas, toothbrush, storybook, and sweet dreams", "🛌"),
            Triple("Healthy Rainbow Plate", "Balance fruits, veggies, grains, and water", "🥦"),
            Triple("Earth Helpers Recycling", "Sort paper, plastic bottles, and metal cans", "♻️"),
            Triple("Table Manners & Sharing", "Napkin on lap, polite words, and helping clear plates", "🍽️")
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
                    items = createBuildingItems()
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
            Triple("Morning, Noon & Night Clock", "Match daily activities to sun Positions and clock hours", "🕒"),
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
            Triple("Sunny Freeze Dance", "Wiggle and hop while music plays, then freeze like a statue!", "💃"),
            Triple("Reach for the Sky Stretch", "Stretch tall like a giraffe and curl small like a mouse", "🦒"),
            Triple("Flamingo One-Foot Balance", "Practice steady balance with gentle posture cues", "🦩"),
            Triple("Hop Like Bramble, Waddle Like Penguin", "Fun animal movement cards for indoor play", "🐧"),
            Triple("Five-Point Star Breaths", "Inhale slowly up the star peak, exhale softly down", "⭐"),
            Triple("Moonlight Bedtime Calm", "Soft stretches and gratitude thoughts before sleep", "🌙")
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
                    items = createSortingItemsForIndex(idx)
                )
            )
        }

        // 24. World 24: Little Chef (8 activities)
        val chefThemes = listOf(
            Triple("Rainbow Fruit Salad Bowl", "Wash, slice, and toss strawberries, kiwi, and mango", "🍓"),
            Triple("Pizzeria Veggie Topper", "Spread tomato sauce, sprinkle cheese & add veggies", "🍕"),
            Triple("Crunchy Garden Veggie Wash", "Rinse carrots, broccoli, and bell peppers in the colander", "🥦"),
            Triple("Birthday Berry Cake Decorator", "Layer sponge cake, whipped cream, and blueberries", "🍰"),
            Triple("Cozy Alphabet Vegetable Soup", "Stir carrots, peas, and letter noodles in the pot", "🍲"),
            Triple("Sunny Morning Breakfast Plate", "Prepare oatmeal, banana slices, and orange juice", "🥞"),
            Triple("Healthy Food Groups Sorter", "Sort fruits, vegetables, grains, protein, and dairy", "🥗"),
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
                    items = createSortingItemsForIndex(4)
                )
            )
        }

        // 25. World 25: Wonder Flashcards (16 decks)
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
                    storyPages = createThreePageStory(title, emoji, idx + 10),
                    isEndlessMilestone = false
                )
            )
        }

        // Verify total count is exactly 257:
        // 9 + 11 + 9 + 12 + 10 + 12 + 12 + 10 + 8 + 8 + 8 + 8 + 8 + 8 + 8 + 8 + 8 + 8 + 8 + 9 + 6 + 8 + 6 + 8 + 16 + 2 + 30
        // = 258 -> Let's trim World 22 from 8 to 7 so the catalog count is strictly 257 selectable activities!
        return list.filterNot { it.id == "w22_arcade_8" }
    }

    private fun createSortingItemsForIndex(seed: Int): List<GameItemOption> {
        return when (seed % 3) {
            0 -> listOf(
                GameItemOption("sort_red_apple", "Red Apple", "🍎", "Red Basket", "Red Apple goes in the Red Basket!", primaryColorHex = 0xFFEF4444, sizeScale = 1.0f),
                GameItemOption("sort_blue_berry", "Blueberry", "🫐", "Blue Basket", "Blueberry goes in the Blue Basket!", primaryColorHex = 0xFF3B82F6, sizeScale = 0.85f),
                GameItemOption("sort_yellow_sun", "Yellow Star", "⭐", "Yellow Basket", "Yellow Star goes in the Yellow Basket!", primaryColorHex = 0xFFEAB308, sizeScale = 1.15f),
                GameItemOption("sort_red_firetruck", "Fire Truck", "🚒", "Red Basket", "Red Fire Truck goes in the Red Basket!", primaryColorHex = 0xFFEF4444, sizeScale = 1.2f, drawableRes = R.drawable.img_object_fire_engine_1794160812725),
                GameItemOption("sort_blue_dolphin", "Dolphin", "🐬", "Blue Basket", "Blue Dolphin goes in the Blue Basket!", primaryColorHex = 0xFF3B82F6, sizeScale = 1.1f, drawableRes = R.drawable.img_animal_ocean_dolphin_1794160811539),
                GameItemOption("sort_yellow_lion", "Lion Cub", "🦁", "Yellow Basket", "Golden Lion goes in the Yellow Basket!", primaryColorHex = 0xFFEAB308, sizeScale = 1.0f, drawableRes = R.drawable.img_animal_savanna_lion_1794160810452)
            )
            1 -> listOf(
                GameItemOption("sort_circle_ball", "Round Ball", "⚽", "Circle Slot", "Round Ball fits the Circle Slot!", primaryColorHex = 0xFF10B981, sizeScale = 0.9f),
                GameItemOption("sort_square_gift", "Square Box", "🎁", "Square Slot", "Square Box fits the Square Slot!", primaryColorHex = 0xFF8B5CF6, sizeScale = 1.1f),
                GameItemOption("sort_star_badge", "Glowing Star", "🌟", "Star Slot", "Glowing Star fits the Star Slot!", primaryColorHex = 0xFFF59E0B, sizeScale = 1.25f),
                GameItemOption("sort_circle_orange", "Sweet Orange", "🍊", "Circle Slot", "Round Orange fits the Circle Slot!", primaryColorHex = 0xFFF97316, sizeScale = 0.85f),
                GameItemOption("sort_square_frame", "Picture Frame", "🖼️", "Square Slot", "Square Frame fits the Square Slot!", primaryColorHex = 0xFF06B6D4, sizeScale = 1.05f),
                GameItemOption("sort_star_sea", "Sea Star", "⭐", "Star Slot", "Sea Star fits the Star Slot!", primaryColorHex = 0xFFEC4899, sizeScale = 1.0f)
            )
            else -> listOf(
                GameItemOption("sort_tiny_bee", "Tiny Bee", "🐝", "Small Basket", "Tiny Bee is small!", primaryColorHex = 0xFFEAB308, sizeScale = 0.72f),
                GameItemOption("sort_med_bunny", "Medium Bunny", "🐰", "Medium Basket", "Bunny is medium size!", primaryColorHex = 0xFF10B981, sizeScale = 1.0f),
                GameItemOption("sort_big_elephant", "Giant Elephant", "🐘", "Large Basket", "Elephant is big and tall!", primaryColorHex = 0xFF3B82F6, sizeScale = 1.35f),
                GameItemOption("sort_tiny_ladybug", "Little Ladybug", "🐞", "Small Basket", "Little Ladybug is small!", primaryColorHex = 0xFFEF4444, sizeScale = 0.75f),
                GameItemOption("sort_med_dog", "Playful Puppy", "🐶", "Medium Basket", "Puppy is medium size!", primaryColorHex = 0xFFF97316, sizeScale = 1.0f),
                GameItemOption("sort_big_whale", "Big Blue Whale", "🐋", "Large Basket", "Whale is giant!", primaryColorHex = 0xFF0284C7, sizeScale = 1.38f)
            )
        }
    }

    private fun createAnimalItemsForIndex(seed: Int): List<GameItemOption> {
        return listOf(
            GameItemOption("ani_lion", "Savanna Lion", "🦁", "Savanna", "Roar! Lions live in family groups called prides.", phoneticCue = "Roar!", primaryColorHex = 0xFFF59E0B, drawableRes = R.drawable.img_animal_savanna_lion_1794160810452),
            GameItemOption("ani_dolphin", "Ocean Dolphin", "🐬", "Ocean", "Click-whistle! Dolphins leap through ocean waves.", phoneticCue = "Splash-click!", primaryColorHex = 0xFF06B6D4, drawableRes = R.drawable.img_animal_ocean_dolphin_1794160811539),
            GameItemOption("ani_elephant", "Gentle Elephant", "🐘", "Savanna", "Pawoo! Elephants use their trunks to drink and wave hello.", phoneticCue = "Pawoo!", primaryColorHex = 0xFF64748B),
            GameItemOption("ani_cow", "Meadow Cow", "🐄", "Farm", "Moooo! Friendly cows chew green meadow grass.", phoneticCue = "Moooo!", primaryColorHex = 0xFF10B981),
            GameItemOption("ani_owl", "Woodland Owl", "🦉", "Forest", "Hoo-hoo! Owls have big eyes to see at twilight.", phoneticCue = "Hoo-hoo!", primaryColorHex = 0xFF6366F1),
            GameItemOption("ani_duck", "Pond Duckling", "🦆", "Pond", "Quack-quack! Ducks paddle with webbed feet.", phoneticCue = "Quack!", primaryColorHex = 0xFFEAB308)
        ).shuffled(java.util.Random(seed.toLong()))
    }

    private fun createMemoryItemsForIndex(seed: Int): List<GameItemOption> {
        return listOf(
            GameItemOption("mem_1", "Lion", "🦁", "Pair1", "Lion!", primaryColorHex = 0xFFF59E0B),
            GameItemOption("mem_2", "Dolphin", "🐬", "Pair2", "Dolphin!", primaryColorHex = 0xFF06B6D4),
            GameItemOption("mem_3", "Strawberry", "🍓", "Pair3", "Strawberry!", primaryColorHex = 0xFFEF4444),
            GameItemOption("mem_4", "Train", "🚂", "Pair4", "Steam Train!", primaryColorHex = 0xFF3B82F6),
            GameItemOption("mem_5", "Sunflower", "🌻", "Pair5", "Sunflower!", primaryColorHex = 0xFFEAB308),
            GameItemOption("mem_6", "Butterfly", "🦋", "Pair6", "Butterfly!", primaryColorHex = 0xFF8B5CF6)
        ).shuffled(java.util.Random(seed.toLong()))
    }

    private fun createBalloonItemsForIndex(seed: Int): List<GameItemOption> {
        return when (seed) {
            0 -> // 1. ABC Letter Balloons (A to Z)
                listOf(
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
            1 -> // 2. Rainbow Color Balloons
                listOf(
                    GameItemOption("bal_col_red", "Red", "❤️", "Red", "Bright Red balloon!", primaryColorHex = 0xFFEF4444),
                    GameItemOption("bal_col_blue", "Blue", "💙", "Blue", "Sky Blue balloon!", primaryColorHex = 0xFF2563EB),
                    GameItemOption("bal_col_yellow", "Yellow", "💛", "Yellow", "Sunny Yellow balloon!", primaryColorHex = 0xFFEAB308),
                    GameItemOption("bal_col_green", "Green", "💚", "Green", "Emerald Green balloon!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_col_orange", "Orange", "🧡", "Orange", "Warm Orange balloon!", primaryColorHex = 0xFFF97316),
                    GameItemOption("bal_col_purple", "Purple", "💜", "Purple", "Royal Purple balloon!", primaryColorHex = 0xFF8B5CF6),
                    GameItemOption("bal_col_pink", "Pink", "💖", "Pink", "Rosy Pink balloon!", primaryColorHex = 0xFFEC4899),
                    GameItemOption("bal_col_teal", "Teal", "🩵", "Teal", "Ocean Teal balloon!", primaryColorHex = 0xFF06B6D4)
                )
            2 -> // 3. 1-to-20 Number Balloons
                listOf(
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
            3 -> // 4. Floating Shape Balloons
                listOf(
                    GameItemOption("bal_shp_circle", "Circle", "⭕", "Circle", "Round Circle!", primaryColorHex = 0xFFEF4444),
                    GameItemOption("bal_shp_star", "Star", "⭐", "Star", "Five-point Star!", primaryColorHex = 0xFFF59E0B),
                    GameItemOption("bal_shp_heart", "Heart", "💖", "Heart", "Loving Heart shape!", primaryColorHex = 0xFFEC4899),
                    GameItemOption("bal_shp_square", "Square", "🟦", "Square", "Four-corner Square!", primaryColorHex = 0xFF2563EB),
                    GameItemOption("bal_shp_triangle", "Triangle", "🔺", "Triangle", "Three-side Triangle!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_shp_diamond", "Diamond", "🔷", "Diamond", "Shining Diamond!", primaryColorHex = 0xFF06B6D4),
                    GameItemOption("bal_shp_moon", "Crescent", "🌙", "Crescent", "Curved Crescent Moon!", primaryColorHex = 0xFF8B5CF6)
                )
            4 -> // 5. Animal Call Balloons
                listOf(
                    GameItemOption("bal_ani_lion", "Lion • Roar", "🦁", "Lion", "Lion says Roar!", primaryColorHex = 0xFFF59E0B),
                    GameItemOption("bal_ani_ele", "Elephant", "🐘", "Elephant", "Elephant trumpets Pawoo!", primaryColorHex = 0xFF3B82F6),
                    GameItemOption("bal_ani_cow", "Cow • Moo", "🐄", "Cow", "Friendly Cow says Moo!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_ani_duck", "Duck • Quack", "🦆", "Duck", "Pond Duck says Quack, quack!", primaryColorHex = 0xFFEAB308),
                    GameItemOption("bal_ani_owl", "Owl • Hoo", "🦉", "Owl", "Night Owl says Hoo, hoo!", primaryColorHex = 0xFF8B5CF6),
                    GameItemOption("bal_ani_frog", "Frog • Ribbit", "🐸", "Frog", "Green Frog says Ribbit!", primaryColorHex = 0xFF059669),
                    GameItemOption("bal_ani_dog", "Puppy • Woof", "🐶", "Puppy", "Happy Puppy says Woof!", primaryColorHex = 0xFFF97316),
                    GameItemOption("bal_ani_cat", "Kitten • Meow", "🐱", "Kitten", "Cozy Kitten says Meow!", primaryColorHex = 0xFFEC4899)
                )
            5 -> // 6. Wonder Buddy Balloons
                listOf(
                    GameItemOption("bal_bud_sunny", "Sunny Cub", "🦁", "Sunny", "Hi from Sunny the Golden Cub!", primaryColorHex = 0xFFF59E0B),
                    GameItemOption("bal_bud_pip", "Pip Owl", "🦉", "Pip", "Hoo-ray from Pip the Owl!", primaryColorHex = 0xFF6366F1),
                    GameItemOption("bal_bud_bramble", "Bramble Bunny", "🐰", "Bramble", "Hop, hop from Bramble the Bunny!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_bud_coral", "Coral Otter", "🦦", "Coral", "Splash hello from Coral the Otter!", primaryColorHex = 0xFF06B6D4),
                    GameItemOption("bal_bud_bot", "Beep-Bot", "🤖", "Beep-Bot", "Beep-boop from Robot Friend!", primaryColorHex = 0xFF3B82F6),
                    GameItemOption("bal_bud_chef", "Little Chef", "👩‍🍳", "Chef", "Yummy treats from Little Chef!", primaryColorHex = 0xFFEF4444)
                )
            6 -> // 7. Sweet Orchard Fruit Balloons
                listOf(
                    GameItemOption("bal_fru_apple", "Red Apple", "🍎", "Apple", "Crisp Red Apple!", primaryColorHex = 0xFFEF4444),
                    GameItemOption("bal_fru_banana", "Banana", "🍌", "Banana", "Sweet Yellow Banana!", primaryColorHex = 0xFFEAB308),
                    GameItemOption("bal_fru_straw", "Strawberry", "🍓", "Strawberry", "Juicy Garden Strawberry!", primaryColorHex = 0xFFEC4899),
                    GameItemOption("bal_fru_orange", "Orange", "🍊", "Orange", "Sunny Citrus Orange!", primaryColorHex = 0xFFF97316),
                    GameItemOption("bal_fru_grape", "Grapes", "🍇", "Grapes", "Bunch of Purple Grapes!", primaryColorHex = 0xFF8B5CF6),
                    GameItemOption("bal_fru_melon", "Watermelon", "🍉", "Watermelon", "Refreshing Watermelon slice!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_fru_mango", "Mango", "🥭", "Mango", "Golden Tropical Mango!", primaryColorHex = 0xFFF59E0B),
                    GameItemOption("bal_fru_cherry", "Cherries", "🍒", "Cherries", "Two sweet red Cherries!", primaryColorHex = 0xFFDC2626)
                )
            7 -> // 8. Picnic Treat & Food Balloons
                listOf(
                    GameItemOption("bal_fod_carrot", "Carrot", "🥕", "Carrot", "Crunchy Orange Carrot!", primaryColorHex = 0xFFF97316),
                    GameItemOption("bal_fod_broc", "Broccoli", "🥦", "Broccoli", "Green Tree Broccoli!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_fod_bread", "Warm Bread", "🍞", "Bread", "Fresh Bakery Bread!", primaryColorHex = 0xFFD97706),
                    GameItemOption("bal_fod_cheese", "Cheese", "🧀", "Cheese", "Yummy Yellow Cheese!", primaryColorHex = 0xFFEAB308),
                    GameItemOption("bal_fod_muffin", "Berry Muffin", "🧁", "Muffin", "Sweet Berry Muffin!", primaryColorHex = 0xFFEC4899),
                    GameItemOption("bal_fod_pretzel", "Pretzel", "🥨", "Pretzel", "Twisty Picnic Pretzel!", primaryColorHex = 0xFF8B5CF6),
                    GameItemOption("bal_fod_corn", "Sweet Corn", "🌽", "Corn", "Golden Ear of Corn!", primaryColorHex = 0xFFF59E0B)
                )
            8 -> // 9. Zooming Vehicle Balloons
                listOf(
                    GameItemOption("bal_veh_fire", "Fire Engine", "🚒", "Fire Engine", "Rescue Red Fire Engine!", primaryColorHex = 0xFFEF4444),
                    GameItemOption("bal_veh_train", "Steam Train", "🚂", "Steam Train", "Choo-choo Steam Train!", primaryColorHex = 0xFF2563EB),
                    GameItemOption("bal_veh_heli", "Helicopter", "🚁", "Helicopter", "Whirring Sky Helicopter!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_veh_boat", "Sailboat", "⛵", "Sailboat", "Breezy Ocean Sailboat!", primaryColorHex = 0xFF06B6D4),
                    GameItemOption("bal_veh_plane", "Airplane", "✈️", "Airplane", "High-flying Airplane!", primaryColorHex = 0xFF8B5CF6),
                    GameItemOption("bal_veh_bus", "School Bus", "🚌", "School Bus", "Yellow Town Bus!", primaryColorHex = 0xFFEAB308),
                    GameItemOption("bal_veh_rocket", "Rocket", "🚀", "Rocket", "Starlight Space Rocket!", primaryColorHex = 0xFFEC4899),
                    GameItemOption("bal_veh_tractor", "Tractor", "🚜", "Tractor", "Green Farm Tractor!", primaryColorHex = 0xFF059669)
                )
            9 -> // 10. Sunny & Snowy Weather Balloons
                listOf(
                    GameItemOption("bal_wea_sun", "Golden Sun", "☀️", "Sun", "Warm Golden Sunshine!", primaryColorHex = 0xFFF59E0B),
                    GameItemOption("bal_wea_rain", "Rain Cloud", "🌧️", "Rain", "Gentle Raindrops falling!", primaryColorHex = 0xFF3B82F6),
                    GameItemOption("bal_wea_bow", "Rainbow", "🌈", "Rainbow", "Seven-color Sky Rainbow!", primaryColorHex = 0xFFEC4899),
                    GameItemOption("bal_wea_snow", "Snowflake", "❄️", "Snowflake", "Sparkling Winter Snowflake!", primaryColorHex = 0xFF06B6D4),
                    GameItemOption("bal_wea_cloud", "Fluffy Cloud", "☁️", "Cloud", "Soft Fluffy White Cloud!", primaryColorHex = 0xFF6366F1),
                    GameItemOption("bal_wea_star", "Night Star", "🌟", "Star", "Twinkling Evening Star!", primaryColorHex = 0xFFEAB308)
                )
            10 -> // 11. Toy Box Treasure Balloons
                listOf(
                    GameItemOption("bal_toy_teddy", "Teddy Bear", "🧸", "Teddy Bear", "Cuddly Brown Teddy Bear!", primaryColorHex = 0xFFD97706),
                    GameItemOption("bal_toy_kite", "Sky Kite", "🪁", "Kite", "Soaring Diamond Kite!", primaryColorHex = 0xFFEC4899),
                    GameItemOption("bal_toy_drum", "Rhythm Drum", "🥁", "Drum", "Rat-a-tat Rhythm Drum!", primaryColorHex = 0xFFEF4444),
                    GameItemOption("bal_toy_block", "Blocks", "🧱", "Blocks", "Colorful Building Blocks!", primaryColorHex = 0xFF3B82F6),
                    GameItemOption("bal_toy_ball", "Soccer Ball", "⚽", "Ball", "Bouncy Playfield Ball!", primaryColorHex = 0xFF10B981),
                    GameItemOption("bal_toy_paint", "Paint Set", "🎨", "Paint", "Rainbow Artist Palette!", primaryColorHex = 0xFF8B5CF6)
                )
            else -> // 12. Phonics Clue Balloons
                listOf(
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

    private fun createPhonicsItemsForIndex(seed: Int): List<GameItemOption> {
        return listOf(
            GameItemOption("ph_a", "A", "🍎", "Apple", "Letter A... aah, Apple.", phoneticCue = "aah", primaryColorHex = 0xFFEF4444),
            GameItemOption("ph_b", "B", "🐻", "Bear", "Letter B... b, Bear.", phoneticCue = "b", primaryColorHex = 0xFF3B82F6),
            GameItemOption("ph_c", "C", "🐱", "Cat", "Letter C... k, Cat.", phoneticCue = "k", primaryColorHex = 0xFF10B981),
            GameItemOption("ph_d", "D", "🐬", "Dolphin", "Letter D... d, Dolphin.", phoneticCue = "d", primaryColorHex = 0xFF06B6D4),
            GameItemOption("ph_l", "L", "🦁", "Lion", "Letter L... llll, Lion.", phoneticCue = "llll", primaryColorHex = 0xFFF59E0B),
            GameItemOption("ph_s", "S", "☀️", "Sun", "Letter S... ssss, Sun.", phoneticCue = "ssss", primaryColorHex = 0xFFEAB308),
            GameItemOption("ph_t", "T", "🚂", "Train", "Letter T... t, Train.", phoneticCue = "t", primaryColorHex = 0xFF8B5CF6),
            GameItemOption("ph_f", "F", "🚒", "Fire Truck", "Letter F... ffff, Fire Truck.", phoneticCue = "ffff", primaryColorHex = 0xFFEC4899)
        ).shuffled(java.util.Random(seed.toLong()))
    }

    private fun createMathItemsForIndex(seed: Int): List<GameItemOption> {
        return listOf(
            GameItemOption("math_1", "Stars", "⭐", "3", "Let's count the twinkling stars!", primaryColorHex = 0xFFF59E0B),
            GameItemOption("math_2", "Apples", "🍎", "4", "Let's put crisp red apples in the basket!", primaryColorHex = 0xFFEF4444),
            GameItemOption("math_3", "Fish", "🐠", "5", "Count the colourful reef fish swimming by!", primaryColorHex = 0xFF06B6D4),
            GameItemOption("math_4", "Butterflies", "🦋", "6", "Count the meadow butterflies!", primaryColorHex = 0xFF8B5CF6)
        ).shuffled(java.util.Random(seed.toLong()))
    }

    private fun createWaterLabItems(): List<GameItemOption> {
        return listOf(
            GameItemOption("water_duck", "Rubber Duck", "🦆", "Float", "The hollow rubber duck is full of air, so it floats on top!", sinksInWater = false, explanation = "Full of light air — Floats!", primaryColorHex = 0xFFEAB308),
            GameItemOption("water_stone", "River Stone", "🪨", "Sink", "The heavy river stone is dense, so it sinks to the sandy bottom!", sinksInWater = true, explanation = "Heavy & dense — Sinks!", primaryColorHex = 0xFF64748B),
            GameItemOption("water_leaf", "Green Leaf", "🍃", "Float", "A flat green leaf is very light and rests right on the water!", sinksInWater = false, explanation = "Light & flat — Floats!", primaryColorHex = 0xFF10B981),
            GameItemOption("water_key", "Metal Key", "🔑", "Sink", "Solid metal is heavier than water, so the key sinks down!", sinksInWater = true, explanation = "Solid metal — Sinks!", primaryColorHex = 0xFFF59E0B),
            GameItemOption("water_apple", "Crisp Apple", "🍎", "Float", "Apples have tiny air pockets inside, helping them bob and float!", sinksInWater = false, explanation = "Tiny air pockets — Floats!", primaryColorHex = 0xFFEF4444),
            GameItemOption("water_boat", "Wooden Toy Boat", "⛵", "Float", "Its curved hull pushes water aside so the boat floats!", sinksInWater = false, explanation = "Boat hull shape — Floats!", primaryColorHex = 0xFF3B82F6),
            GameItemOption("water_coin", "Treasure Coin", "🪙", "Sink", "Plink! The heavy metal coin sinks straight to the bottom.", sinksInWater = true, explanation = "Heavy metal — Sinks!", primaryColorHex = 0xFFD97706),
            GameItemOption("water_anchor", "Iron Anchor", "⚓", "Sink", "Anchors are made to sink and hold ships steady!", sinksInWater = true, explanation = "Heavy iron — Sinks!", primaryColorHex = 0xFF475569)
        )
    }

    private fun createVehicleItems(): List<GameItemOption> {
        return listOf(
            GameItemOption("veh_fire", "Fire Engine", "🚒", "Road Rescue", "Wee-oo! The red fire engine helps put out fires.", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.img_object_fire_engine_1794160812725),
            GameItemOption("veh_train", "Steam Train", "🚂", "Rail Track", "Choo-choo! The locomotive chugs along steel rails.", primaryColorHex = 0xFF3B82F6, drawableRes = R.drawable.img_object_steam_train_1794160812641),
            GameItemOption("veh_boat", "Sailboat", "⛵", "Ocean Wave", "Whoosh! Wind fills the sails across the water.", primaryColorHex = 0xFF06B6D4),
            GameItemOption("veh_plane", "Airplane", "✈️", "Cloud Sky", "Zoom! Wings lift the airplane high above the clouds.", primaryColorHex = 0xFF8B5CF6)
        )
    }

    private fun createNatureItems(): List<GameItemOption> {
        return listOf(
            GameItemOption("nat_seed", "Tiny Seed", "🌱", "Earth Soil", "Seeds sprout roots and stems when watered!", primaryColorHex = 0xFF10B981),
            GameItemOption("nat_flower", "Sunflower", "🌻", "Garden Bed", "Sunflowers turn their golden petals toward the sun!", primaryColorHex = 0xFFEAB308),
            GameItemOption("nat_butterfly", "Monarch Butterfly", "🦋", "Flower Meadow", "Butterflies sip nectar and spread pollen!", primaryColorHex = 0xFFF97316),
            GameItemOption("nat_tree", "Oak Tree", "🌳", "Forest Canopy", "Tall trees give shade and cozy homes for birds!", primaryColorHex = 0xFF059669)
        )
    }

    private fun createFeelingsItems(): List<GameItemOption> {
        return listOf(
            GameItemOption("feel_happy", "Happy & Smiling", "😊", "Happy", "Sharing toys and playing together feels happy!", primaryColorHex = 0xFF10B981),
            GameItemOption("feel_calm", "Calm Star Breath", "😌", "Calm", "Taking a slow deep breath helps our body feel calm.", primaryColorHex = 0xFF06B6D4),
            GameItemOption("feel_kind", "Kind Helper", "🤗", "Kind", "Helping a friend pick up dropped crayons is super kind!", primaryColorHex = 0xFFF59E0B),
            GameItemOption("feel_proud", "Proud Try", "🌟", "Proud", "Trying again when a puzzle is tricky makes us proud!", primaryColorHex = 0xFF8B5CF6)
        )
    }

    private fun createBuildingItems(): List<GameItemOption> {
        return listOf(
            GameItemOption("bld_base", "Foundation Stone", "🧱", "Bottom Base", "Strong wide blocks go at the bottom!", primaryColorHex = 0xFFEF4444),
            GameItemOption("bld_wall", "Window Wall", "🪟", "Middle Wall", "Bright windows let the sunshine in!", primaryColorHex = 0xFF3B82F6),
            GameItemOption("bld_roof", "Gable Roof", "🔺", "Top Roof", "The angled roof keeps rain off the cozy home!", primaryColorHex = 0xFFF59E0B)
        )
    }

    private fun createFlashcardDeckItems(deckIndex: Int): List<GameItemOption> {
        return when (deckIndex % 4) {
            0 -> listOf(
                GameItemOption("fc_fire", "Fire Engine", "🚒", "Vehicles", "Firefighters ride the red fire engine to help town friends!", primaryColorHex = 0xFFEF4444, drawableRes = R.drawable.img_object_fire_engine_1794160812725),
                GameItemOption("fc_train", "Steam Train", "🚂", "Vehicles", "Choo-choo! The steam train crosses bridges and valleys.", primaryColorHex = 0xFF3B82F6, drawableRes = R.drawable.img_object_steam_train_1794160812641),
                GameItemOption("fc_heli", "Helicopter", "🚁", "Vehicles", "Spinning rotor blades let helicopters hover in the sky.", primaryColorHex = 0xFF10B981),
                GameItemOption("fc_boat", "Sailboat", "⛵", "Vehicles", "Gentle breezes push sailboats across the blue bay.", primaryColorHex = 0xFF06B6D4)
            )
            1 -> listOf(
                GameItemOption("fc_lion", "Savanna Lion", "🦁", "Animals", "Lions rest in the shade of acacia trees and roar together.", primaryColorHex = 0xFFF59E0B, drawableRes = R.drawable.img_animal_savanna_lion_1794160810452),
                GameItemOption("fc_dolphin", "Bottlenose Dolphin", "🐬", "Animals", "Dolphins talk with clicks and whistles under the waves.", primaryColorHex = 0xFF06B6D4, drawableRes = R.drawable.img_animal_ocean_dolphin_1794160811539),
                GameItemOption("fc_owl", "Barn Owl", "🦉", "Animals", "Owls fly softly on velvety feathers at dusk.", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("fc_ele", "African Elephant", "🐘", "Animals", "Elephants have the biggest ears and love mud baths.", primaryColorHex = 0xFF64748B)
            )
            2 -> listOf(
                GameItemOption("fc_apple", "Crisp Red Apple", "🍎", "Foods", "Apples grow on orchard trees and crunch when you bite!", primaryColorHex = 0xFFEF4444),
                GameItemOption("fc_carrot", "Crunchy Carrot", "🥕", "Foods", "Carrots grow underground with feathery green tops.", primaryColorHex = 0xFFF97316),
                GameItemOption("fc_broccoli", "Green Broccoli", "🥦", "Foods", "Broccoli florets look just like tiny green forest trees!", primaryColorHex = 0xFF10B981),
                GameItemOption("fc_berry", "Sweet Strawberry", "🍓", "Foods", "Strawberries wear their tiny seeds on the outside!", primaryColorHex = 0xFFEC4899)
            )
            else -> listOf(
                GameItemOption("fc_piano", "Grand Piano", "🎹", "Music", "Pressing piano keys makes little felt hammers tap strings.", primaryColorHex = 0xFF8B5CF6),
                GameItemOption("fc_drum", "Rhythm Drum", "🥁", "Music", "Tapping the drumhead sends bouncy sound waves through the air.", primaryColorHex = 0xFFEF4444),
                GameItemOption("fc_bell", "Golden Handbell", "🔔", "Music", "Ringing a bell makes a bright, sparkling chime.", primaryColorHex = 0xFFEAB308),
                GameItemOption("fc_rainbow", "Sky Rainbow", "🌈", "Nature", "Sunlight shining through raindrops paints a seven-color rainbow!", primaryColorHex = 0xFF06B6D4)
            )
        }
    }

    private fun createThreePageStory(title: String, emoji: String, seed: Int): List<StoryPageSpec> {
        return listOf(
            StoryPageSpec(
                pageNumber = 1,
                title = "$title • Page 1",
                emojiScene = "$emoji 🌤️ 🌳",
                narrationText = "One bright morning in Little Wonder World, our friends gathered for \"$title\". Everyone brought curiosity and a big warm smile!",
                interactivePrompt = "Who is ready to begin the adventure?",
                choiceOptions = listOf("Let's explore together!", "Wait and listen closely"),
                correctChoiceIndex = 0,
                parentTip = "Ask your child what they notice first in the picture scene."
            ),
            StoryPageSpec(
                pageNumber = 2,
                title = "$title • Page 2",
                emojiScene = "✨ $emoji 🎶",
                narrationText = "Along the path, they listened to gentle rhythms, shared their treasures, and helped each other step by step.",
                interactivePrompt = "What is the kindest thing to do when a friend needs help?",
                choiceOptions = listOf("Share and help kindly!", "Keep trying together"),
                correctChoiceIndex = 0,
                parentTip = "Pause and sing or clap the rhythm of the title together."
            ),
            StoryPageSpec(
                pageNumber = 3,
                title = "$title • Page 3",
                emojiScene = "🌈 ⭐ $emoji",
                narrationText = "Hooray! With teamwork and gentle practice, the story ended with laughter, stars, and a cozy wave goodbye.",
                interactivePrompt = "Would you like to celebrate or hear the story again?",
                choiceOptions = listOf("Celebrate with 5 Stars!", "Read Again!"),
                correctChoiceIndex = 0,
                parentTip = "Ask your child which part of the 3-page story was their favorite."
            )
        )
    }

    private fun createSafetyStoryPages(title: String, emoji: String, idx: Int): List<StoryPageSpec> {
        if (idx == 0) {
            // Flagship Knock Knock! Door Safety story
            return listOf(
                StoryPageSpec(
                    pageNumber = 1,
                    title = "Knock Knock! Who's at the Door?",
                    emojiScene = "🚪 🔔 🏠",
                    narrationText = "Sunny the Cub is playing in the living room when someone knocks: Knock, knock, knock! Ring-a-ling goes the doorbell.",
                    interactivePrompt = "Should a child open the front door alone?",
                    choiceOptions = listOf("Keep the door closed!", "Never open alone"),
                    correctChoiceIndex = 0,
                    parentTip = "Remind children that the front door stays closed until a parent or trusted adult checks."
                ),
                StoryPageSpec(
                    pageNumber = 2,
                    title = "Step Back & Call a Grown-Up",
                    emojiScene = "🦁 🗣️ 👨‍👩‍👧",
                    narrationText = "Sunny keeps the door safely shut and walks right to Mom and Dad: 'Grown-up! Someone is knocking at the door!'",
                    interactivePrompt = "Who should Sunny get right away?",
                    choiceOptions = listOf("Get a trusted grown-up!", "Call Mom or Dad"),
                    correctChoiceIndex = 0,
                    parentTip = "Practice who the trusted adults are in your home."
                ),
                StoryPageSpec(
                    pageNumber = 3,
                    title = "Safe & Smart Together!",
                    emojiScene = "🛡️ 🌟 👨‍👩‍👧",
                    narrationText = "Dad checks the window and opens the door safely together. 'Thank you, Sunny! Keeping the door closed and getting a grown-up is always the safe choice!'",
                    interactivePrompt = "What is our Door Safety rule?",
                    choiceOptions = listOf("Keep it closed & get a grown-up!", "Safe Star High-Five!"),
                    correctChoiceIndex = 0,
                    parentTip = "Discussion prompt: What do we do when we hear the doorbell ring?"
                )
            )
        }
        return createThreePageStory(title, emoji, idx)
    }
}
