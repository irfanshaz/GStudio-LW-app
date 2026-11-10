package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.model.ActivityConfig
import com.example.model.AgeBand
import com.example.model.LearningBuddy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class WonderRepository(
    private val context: Context,
    private val dao: WonderDao
) {
    val settingsFlow: Flow<ProfileSettingsEntity?> = dao.observeSettings()
    val progressFlow: Flow<List<ActivityProgressEntity>> = dao.observeAllProgress()
    val artworksFlow: Flow<List<SavedArtworkEntity>> = dao.observeSavedArtworks()
    val familyMembersFlow: Flow<List<FamilyMemberEntity>> = dao.observeFamilyMembers()
    val mediaOverridesFlow: Flow<List<CustomMediaOverrideEntity>> = dao.observeMediaOverrides()

    suspend fun ensureInitialized() {
        val current = dao.getSettingsOnce()
        if (current == null) {
            dao.upsertSettings(ProfileSettingsEntity())
        }
    }

    suspend fun completeOnboarding(ageBand: AgeBand, buddy: LearningBuddy) {
        ensureInitialized()
        dao.finishOnboarding(ageBand.id, buddy.id)
    }

    suspend fun updateAgeAndBuddy(ageBand: AgeBand, buddy: LearningBuddy) {
        dao.updateAgeBuddy(ageBand.id, buddy.id)
    }

    suspend fun updateSelectedVoice(voiceId: String) { dao.updateVoice(voiceId) }

    suspend fun updateAudioAndMotionSettings(
        narrationEnabled: Boolean, musicEnabled: Boolean, sfxEnabled: Boolean,
        reducedMotion: Boolean, autoAdaptiveDifficulty: Boolean = true
    ) {
        dao.updateAudio(narrationEnabled, musicEnabled, sfxEnabled, reducedMotion, autoAdaptiveDifficulty)
    }

    suspend fun recordActivityOpened(activity: ActivityConfig) {
        val existing = dao.getProgressForActivity(activity.id)
        val now = System.currentTimeMillis()
        if (existing == null) {
            dao.upsertActivityProgress(
                ActivityProgressEntity(
                    activityId = activity.id,
                    worldId = activity.worldId,
                    landId = activity.landId.id,
                    starsEarned = 1,
                    completions = 0,
                    attempts = 1,
                    isFavorite = false,
                    lastPlayedEpochMs = now,
                    savedStepIndex = 0
                )
            )
        } else {
            dao.upsertActivityProgress(
                existing.copy(
                    attempts = existing.attempts + 1,
                    lastPlayedEpochMs = now
                )
            )
        }
    }

    suspend fun recordActivityMilestone(
        activity: ActivityConfig,
        starsToAdd: Int = 1,
        nextStepIndex: Int? = null,
        hadRetriesInRound: Boolean = false,
        tracingAccuracyPercent: Int? = null
    ) {
        val existing = dao.getProgressForActivity(activity.id)
        val now = System.currentTimeMillis()
        if (existing == null) {
            val initialDelta = if (hadRetriesInRound) 0 else 0
            dao.upsertActivityProgress(
                ActivityProgressEntity(
                    activityId = activity.id,
                    worldId = activity.worldId,
                    landId = activity.landId.id,
                    starsEarned = starsToAdd.coerceIn(1, 5),
                    completions = 1,
                    attempts = 1,
                    smoothFirstTryCount = if (!hadRetriesInRound) 1 else 0,
                    gentleRetryCount = if (hadRetriesInRound) 1 else 0,
                    adaptiveComfortDelta = initialDelta,
                    lastTracingAccuracyPercent = tracingAccuracyPercent ?: 0,
                    isFavorite = false,
                    lastPlayedEpochMs = now,
                    savedStepIndex = nextStepIndex ?: 0
                )
            )
        } else {
            val newSmooth = existing.smoothFirstTryCount + if (!hadRetriesInRound) 1 else 0
            val newRetries = existing.gentleRetryCount + if (hadRetriesInRound) 1 else 0
            val updatedDelta = AdaptiveLearningEngine.computeUpdatedDelta(
                currentDelta = existing.adaptiveComfortDelta,
                smoothSuccessCount = newSmooth,
                gentleSupportCount = newRetries,
                hadRetriesInRound = hadRetriesInRound
            )
            dao.upsertActivityProgress(
                existing.copy(
                    starsEarned = (existing.starsEarned + starsToAdd).coerceAtMost(5),
                    completions = existing.completions + 1,
                    smoothFirstTryCount = newSmooth,
                    gentleRetryCount = newRetries,
                    adaptiveComfortDelta = updatedDelta,
                    lastTracingAccuracyPercent = tracingAccuracyPercent ?: existing.lastTracingAccuracyPercent,
                    lastPlayedEpochMs = now,
                    savedStepIndex = nextStepIndex ?: existing.savedStepIndex
                )
            )
        }
    }

    suspend fun saveActivityStepCheckpoint(activity: ActivityConfig, stepIndex: Int) {
        val existing = dao.getProgressForActivity(activity.id)
        val now = System.currentTimeMillis()
        if (existing == null) {
            dao.upsertActivityProgress(
                ActivityProgressEntity(
                    activityId = activity.id,
                    worldId = activity.worldId,
                    landId = activity.landId.id,
                    starsEarned = 0,
                    completions = 0,
                    attempts = 1,
                    lastPlayedEpochMs = now,
                    savedStepIndex = stepIndex.coerceAtLeast(0)
                )
            )
        } else {
            dao.upsertActivityProgress(
                existing.copy(
                    savedStepIndex = stepIndex.coerceAtLeast(0),
                    lastPlayedEpochMs = now
                )
            )
        }
    }

    suspend fun recordGentleRetry(activity: ActivityConfig) {
        val existing = dao.getProgressForActivity(activity.id) ?: return
        val newRetries = existing.gentleRetryCount + 1
        val newDelta = if (newRetries >= 2) -1 else existing.adaptiveComfortDelta
        dao.upsertActivityProgress(
            existing.copy(
                gentleRetryCount = newRetries,
                adaptiveComfortDelta = newDelta
            )
        )
    }

    suspend fun setActivityAdaptiveDelta(activity: ActivityConfig, delta: Int) {
        val existing = dao.getProgressForActivity(activity.id)
        val now = System.currentTimeMillis()
        if (existing == null) {
            dao.upsertActivityProgress(
                ActivityProgressEntity(
                    activityId = activity.id,
                    worldId = activity.worldId,
                    landId = activity.landId.id,
                    adaptiveComfortDelta = delta.coerceIn(-1, 1),
                    lastPlayedEpochMs = now
                )
            )
        } else {
            dao.upsertActivityProgress(
                existing.copy(adaptiveComfortDelta = delta.coerceIn(-1, 1))
            )
        }
    }

    suspend fun toggleFavorite(activity: ActivityConfig) {
        val existing = dao.getProgressForActivity(activity.id)
        val now = System.currentTimeMillis()
        if (existing == null) {
            dao.upsertActivityProgress(
                ActivityProgressEntity(
                    activityId = activity.id,
                    worldId = activity.worldId,
                    landId = activity.landId.id,
                    starsEarned = 0,
                    completions = 0,
                    attempts = 0,
                    isFavorite = true,
                    lastPlayedEpochMs = now
                )
            )
        } else {
            dao.upsertActivityProgress(
                existing.copy(isFavorite = !existing.isFavorite)
            )
        }
    }

    suspend fun saveArtwork(title: String, templateName: String, strokesPayload: String, bgHex: Long) {
        dao.insertArtwork(
            SavedArtworkEntity(
                title = title,
                templateName = templateName,
                strokesPayload = strokesPayload,
                backgroundColorHex = bgHex
            )
        )
    }

    suspend fun deleteArtwork(id: Long) {
        dao.deleteArtworkById(id)
    }

    suspend fun addFamilyMember(
        name: String,
        role: String,
        photoUri: Uri?,
        avatarEmoji: String,
        accentColorHex: Long
    ) = withContext(Dispatchers.IO) {
        var savedLocalPath: String? = null
        if (photoUri != null) {
            savedLocalPath = copyAndResizeImageToPrivateStorage(photoUri, "family_${System.currentTimeMillis()}.jpg")
        }
        dao.insertFamilyMember(
            FamilyMemberEntity(
                name = name.trim().ifEmpty { "Family Friend" },
                relationshipRole = role.trim().ifEmpty { "Loved One" },
                localPhotoPath = savedLocalPath,
                avatarEmoji = avatarEmoji,
                accentColorHex = accentColorHex
            )
        )
    }

    suspend fun deleteFamilyMember(member: FamilyMemberEntity) = withContext(Dispatchers.IO) {
        member.localPhotoPath?.let { path ->
            runCatching { File(path).delete() }
        }
        dao.deleteFamilyMemberById(member.id)
    }

    suspend fun importCustomMediaOverride(
        mediaId: String,
        mediaUri: Uri,
        isVideo: Boolean
    ) = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "wonder_reels_media").apply { mkdirs() }
        val ext = if (isVideo) "mp4" else "jpg"
        val destFile = File(dir, "${mediaId}_${System.currentTimeMillis()}.$ext")
        runCatching {
            context.contentResolver.openInputStream(mediaUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            dao.upsertMediaOverride(
                CustomMediaOverrideEntity(
                    mediaId = mediaId,
                    localVideoPath = if (isVideo) destFile.absolutePath else null,
                    localImagePath = if (!isVideo) destFile.absolutePath else null
                )
            )
        }
    }

    suspend fun removeCustomMediaOverride(mediaId: String) {
        dao.deleteMediaOverride(mediaId)
    }

    suspend fun resetAllChildData() = withContext(Dispatchers.IO) {
        dao.clearGarden()
        dao.clearAllProgress()
        dao.clearAllArtworks()
        dao.clearAllFamilyMembers()
        File(context.filesDir, "family_photos").deleteRecursively()
    }

    private fun copyAndResizeImageToPrivateStorage(uri: Uri, fileName: String): String? {
        return runCatching {
            val dir = File(context.filesDir, "family_photos").apply { mkdirs() }
            val destFile = File(dir, fileName)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val original = BitmapFactory.decodeStream(stream) ?: return null
                val maxDim = 640
                val ratio = minOf(
                    maxDim.toFloat() / original.width.coerceAtLeast(1),
                    maxDim.toFloat() / original.height.coerceAtLeast(1),
                    1.0f
                )
                val targetW = (original.width * ratio).toInt().coerceAtLeast(1)
                val targetH = (original.height * ratio).toInt().coerceAtLeast(1)
                val scaled = Bitmap.createScaledBitmap(original, targetW, targetH, true)
                FileOutputStream(destFile).use { out ->
                    scaled.compress(Bitmap.CompressFormat.JPEG, 86, out)
                }
            }
            destFile.absolutePath
        }.getOrNull()
    }
}
