package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.model.AssetVerificationStatus
import com.example.model.MediaManifestEntry
import com.example.model.RealMediaCategory
import com.example.model.RealWorldMediaItem
import com.example.model.ReelMotionStyle

/** Curated matches to Batch 01. Existing catalog and parent import IDs remain stable. */
internal object Batch01Media {
    private data class Picture(
        val title: String, val category: RealMediaCategory, val emoji: String, val group: String,
        val drawable: Int, val path: String, val checksum: String
    )
    private val pictures = listOf(
        Picture("African Savanna Lion", RealMediaCategory.ANIMAL, "🦁", "Savanna", R.drawable.pack01_lion, "res/drawable-nodpi/pack01_lion.webp", "c1a5f5eeba2f"),
        Picture("African Bush Elephant", RealMediaCategory.ANIMAL, "🐘", "Savanna", R.drawable.pack01_elephant, "res/drawable-nodpi/pack01_elephant.webp", "a410ca4460ac"),
        Picture("Bengal Striped Tiger", RealMediaCategory.ANIMAL, "🐯", "Jungle", R.drawable.pack01_tiger, "res/drawable-nodpi/pack01_tiger.webp", "67054138a616"),
        Picture("Golden Retriever Puppy", RealMediaCategory.ANIMAL, "🐶", "Family Home", R.drawable.pack01_dog, "res/drawable-nodpi/pack01_dog.webp", "7550e9d231e4"),
        Picture("Tabby Purring Kitten", RealMediaCategory.ANIMAL, "🐱", "Cozy Porch", R.drawable.pack01_cat, "res/drawable-nodpi/pack01_cat.webp", "ef85c3f9f5b2"),
        Picture("Crisp Orchard Apple", RealMediaCategory.OBJECT, "🍎", "Fruits", R.drawable.pack01_apple, "res/drawable-nodpi/pack01_apple.webp", "383738fbf75e"),
        Picture("Sweet Yellow Banana", RealMediaCategory.OBJECT, "🍌", "Fruits", R.drawable.pack01_banana, "res/drawable-nodpi/pack01_banana.webp", "f37057daba62"),
        Picture("Mango", RealMediaCategory.OBJECT, "🥭", "Fruits", R.drawable.pack01_mango, "res/drawable-nodpi/pack01_mango.webp", "fcbeb3778cdd"),
        Picture("Yellow School Bus", RealMediaCategory.OBJECT, "🚌", "Vehicles", R.drawable.pack01_bus, "res/drawable-nodpi/pack01_bus.webp", "b6490241fc09"),
        Picture("Car", RealMediaCategory.OBJECT, "🚗", "Vehicles", R.drawable.pack01_car, "res/drawable-nodpi/pack01_car.webp", "2970bd9c1c29"),
        Picture("Circle", RealMediaCategory.OBJECT, "●", "Shapes", R.drawable.pack01_circle, "res/drawable-nodpi/pack01_circle.xml", "43042e1f6ec1"),
        Picture("Square", RealMediaCategory.OBJECT, "■", "Shapes", R.drawable.pack01_square, "res/drawable-nodpi/pack01_square.xml", "8201f4ef0d47"),
        Picture("Triangle", RealMediaCategory.OBJECT, "▲", "Shapes", R.drawable.pack01_triangle, "res/drawable-nodpi/pack01_triangle.xml", "234ac8f8547f"),
        Picture("Star", RealMediaCategory.OBJECT, "★", "Shapes", R.drawable.pack01_star, "res/drawable-nodpi/pack01_star.xml", "38781e9d5f66"),
        Picture("Heart", RealMediaCategory.OBJECT, "♥", "Shapes", R.drawable.pack01_heart, "res/drawable-nodpi/pack01_heart.xml", "251e514bfd61"),
    )

    fun mergeWith(existing: List<RealWorldMediaItem>): List<RealWorldMediaItem> {
        val matched = existing.map { item ->
            val picture = pictures.firstOrNull { it.title == item.title && it.category == item.category }
            if (picture == null) item
            else item.copy(
                bundledDrawableRes = picture.drawable,
                manifest = manifest(item.id, picture.path, picture.checksum, 0)
            )
        }
        val extraPictures = pictures.filter { picture ->
            existing.none { it.title == picture.title && it.category == picture.category }
        }.map { picture ->
            val id = "pack01_" + picture.title.lowercase()
            RealWorldMediaItem(
                id, picture.category, picture.title, picture.group, picture.emoji, picture.group,
                listOf("Look at the ${picture.title.lowercase()}."), "Picture card", 392f,
                ReelMotionStyle.SOUND_WAVE_PULSE, Color(0xFF0E7490), Color(0xFF164E63),
                bundledDrawableRes = picture.drawable,
                manifest = manifest(id, picture.path, picture.checksum, 0)
            )
        }
        val lessonId = "lesson_wild_and_domestic_animals_pilot"
        val lesson = RealWorldMediaItem(
            lessonId, RealMediaCategory.FILM_LESSON, "Wild and Domestic Animals",
            "25-second offline lesson", "🎬", "Animal lessons",
            listOf("Meet a lion, elephant, dog and cat."), "Recorded English narration", 392f,
            ReelMotionStyle.STORY_LESSON_SCENE, Color(0xFF5B21B6), Color(0xFF312E81),
            bundledDrawableRes = R.drawable.pack01_animals_lesson,
            bundledVideoRes = R.raw.pack01_animals_lesson,
            bundledCaptionsRes = R.raw.pack01_animals_lesson_captions,
            manifest = manifest(lessonId, "res/raw/pack01_animals_lesson.mp4", "2cb97bda3ab5", 25)
        )
        return matched + extraPictures + lesson
    }

    private fun manifest(id: String, path: String, checksum: String, duration: Int) =
        MediaManifestEntry(
            id, path, if (duration > 0) "MP4 + English captions" else "Picture",
            duration, checksum, "LWW MediaPack Batch 01",
            "Little Wonder World Original Production", AssetVerificationStatus.BUNDLED_VERIFIED,
            if (duration > 0) "Use the bundled lesson picture" else "Use the card symbol"
        )
}
