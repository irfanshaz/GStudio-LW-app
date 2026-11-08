package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "profile_settings")
data class ProfileSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val ageBandId: String = "4-5",
    val buddyId: String = "sunny_cub",
    val selectedVoiceId: String = "teacher_warm_default",
    val narrationEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
    val autoAdaptiveDifficulty: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val alphabetJourneyStep: Int = 0,
    val numberJourneyStep: Int = 0
)

@Entity(tableName = "activity_progress")
data class ActivityProgressEntity(
    @PrimaryKey val activityId: String,
    val worldId: String,
    val landId: String,
    val starsEarned: Int = 0,
    val completions: Int = 0,
    val attempts: Int = 0,
    val smoothFirstTryCount: Int = 0,
    val gentleRetryCount: Int = 0,
    val adaptiveComfortDelta: Int = 0, // -1 = Gentle Support, 0 = Age Baseline, +1 = Confident Explorer
    val lastTracingAccuracyPercent: Int = 0,
    val isFavorite: Boolean = false,
    val lastPlayedEpochMs: Long = 0L,
    val savedStepIndex: Int = 0
)

@Entity(tableName = "saved_artworks")
data class SavedArtworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val templateName: String,
    val strokesPayload: String,
    val backgroundColorHex: Long,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationshipRole: String,
    val localPhotoPath: String?,
    val avatarEmoji: String = "😊",
    val accentColorHex: Long = 0xFF3B82F6,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_media_overrides")
data class CustomMediaOverrideEntity(
    @PrimaryKey val mediaId: String,
    val localVideoPath: String? = null,
    val localImagePath: String? = null,
    val sourceLabel: String = "Google Flow / User Pack",
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

@Dao
interface WonderDao {
    @Query("SELECT * FROM profile_settings WHERE id = 1")
    fun observeSettings(): Flow<ProfileSettingsEntity?>

    @Query("SELECT * FROM profile_settings WHERE id = 1")
    suspend fun getSettingsOnce(): ProfileSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: ProfileSettingsEntity)

    @Query("SELECT * FROM activity_progress")
    fun observeAllProgress(): Flow<List<ActivityProgressEntity>>

    @Query("SELECT * FROM activity_progress WHERE activityId = :activityId")
    suspend fun getProgressForActivity(activityId: String): ActivityProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertActivityProgress(progress: ActivityProgressEntity)

    @Query("DELETE FROM activity_progress")
    suspend fun clearAllProgress()

    @Query("SELECT * FROM saved_artworks ORDER BY createdAtEpochMs DESC")
    fun observeSavedArtworks(): Flow<List<SavedArtworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtwork(artwork: SavedArtworkEntity): Long

    @Query("DELETE FROM saved_artworks WHERE id = :id")
    suspend fun deleteArtworkById(id: Long)

    @Query("DELETE FROM saved_artworks")
    suspend fun clearAllArtworks()

    @Query("SELECT * FROM family_members ORDER BY createdAtEpochMs ASC")
    fun observeFamilyMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMemberEntity): Long

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteFamilyMemberById(id: Long)

    @Query("DELETE FROM family_members")
    suspend fun clearAllFamilyMembers()

    @Query("SELECT * FROM custom_media_overrides")
    fun observeMediaOverrides(): Flow<List<CustomMediaOverrideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMediaOverride(overrideEntity: CustomMediaOverrideEntity)

    @Query("DELETE FROM custom_media_overrides WHERE mediaId = :mediaId")
    suspend fun deleteMediaOverride(mediaId: String)
}

@Database(
    entities = [
        ProfileSettingsEntity::class,
        ActivityProgressEntity::class,
        SavedArtworkEntity::class,
        FamilyMemberEntity::class,
        CustomMediaOverrideEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WonderDatabase : RoomDatabase() {
    abstract fun wonderDao(): WonderDao

    companion object {
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE profile_settings ADD COLUMN selectedVoiceId TEXT NOT NULL DEFAULT 'teacher_warm_default'"
                )
            }
        }

        @Volatile
        private var INSTANCE: WonderDatabase? = null

        fun getInstance(context: Context): WonderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WonderDatabase::class.java,
                    "little_wonder_world_v1.db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
