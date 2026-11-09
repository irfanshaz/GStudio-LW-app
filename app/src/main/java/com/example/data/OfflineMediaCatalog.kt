package com.example.data

import android.content.Context
import com.example.model.RealMediaCategory
import com.example.model.RealWorldMediaItem
import org.json.JSONObject
import java.util.Locale

/** The generated index only lists media backed by bytes in the shipped packs. */
class OfflineMediaCatalog(context: Context) {
    val items: List<RealWorldMediaItem> = context.assets.open("real-world-index.json")
        .bufferedReader().use { reader ->
            val entries = JSONObject(reader.readText()).getJSONArray("items")
            List(entries.length()) { index ->
                val row = entries.getJSONObject(index)
                val category = when (row.getString("kind")) {
                    "animals" -> RealMediaCategory.ANIMAL
                    "objects" -> RealMediaCategory.OBJECT
                    else -> RealMediaCategory.FILM_LESSON
                }
                val title = row.getString("title")
                val template = RealWorldMediaRegistry.getItemsByCategory(category).first()
                val facts = row.getJSONArray("facts")
                val previous = RealWorldMediaRegistry.getItemsByCategory(category)
                    .firstOrNull { canonicalName(it.title) == canonicalName(title) }
                (previous ?: template).copy(
                    id = previous?.id ?: "pack_${row.getString("id")}",
                    title = title,
                    subtitle = category.label,
                    emoji = previous?.emoji ?: category.emoji,
                    habitatOrGroup = category.label,
                    funFacts = List(facts.length()) { facts.getString(it) },
                    soundDescription = "",
                    bundledDrawableRes = null,
                    bundledVideoAssetPath = row.optionalPath("video"),
                    bundledImageAssetPath = row.optionalPath("image"),
                    bundledAudioAssetPath = row.optionalPath("audio"),
                    subtitles = emptyList()
                )
            }.distinctBy { it.id }
        }

    fun find(label: String): RealWorldMediaItem? =
        items.firstOrNull { canonicalName(it.title) == canonicalName(label) }

    private fun JSONObject.optionalPath(key: String): String? =
        optString(key).takeIf { it.isNotBlank() }

    private fun canonicalName(value: String): String =
        when (val name = value.trim().lowercase(Locale.ROOT)) {
            "african savanna lion", "savanna lion" -> "lion"
            "bottlenose dolphin", "ocean dolphin" -> "dolphin"
            "gentle elephant" -> "elephant"
            "community fire engine" -> "fire engine"
            "classic steam locomotive" -> "steam train"
            else -> name
        }
}
