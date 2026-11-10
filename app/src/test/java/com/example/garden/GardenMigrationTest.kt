package com.example.garden

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.WonderDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GardenMigrationTest {
    @Test fun upgradePreservesExistingSettingsProgressAndArt() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "migration-garden.db"
        context.deleteDatabase(name)
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context).name(name)
                .callback(object : SupportSQLiteOpenHelper.Callback(3) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE profile_settings (id INTEGER NOT NULL PRIMARY KEY, ageBandId TEXT NOT NULL, buddyId TEXT NOT NULL, selectedVoiceId TEXT NOT NULL, narrationEnabled INTEGER NOT NULL, musicEnabled INTEGER NOT NULL, sfxEnabled INTEGER NOT NULL, reducedMotion INTEGER NOT NULL, autoAdaptiveDifficulty INTEGER NOT NULL, onboardingCompleted INTEGER NOT NULL, alphabetJourneyStep INTEGER NOT NULL, numberJourneyStep INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE activity_progress (activityId TEXT NOT NULL PRIMARY KEY, worldId TEXT NOT NULL, landId TEXT NOT NULL, starsEarned INTEGER NOT NULL, completions INTEGER NOT NULL, attempts INTEGER NOT NULL, smoothFirstTryCount INTEGER NOT NULL, gentleRetryCount INTEGER NOT NULL, adaptiveComfortDelta INTEGER NOT NULL, lastTracingAccuracyPercent INTEGER NOT NULL, isFavorite INTEGER NOT NULL, lastPlayedEpochMs INTEGER NOT NULL, savedStepIndex INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE saved_artworks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, templateName TEXT NOT NULL, strokesPayload TEXT NOT NULL, backgroundColorHex INTEGER NOT NULL, createdAtEpochMs INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE family_members (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, relationshipRole TEXT NOT NULL, localPhotoPath TEXT, avatarEmoji TEXT NOT NULL, accentColorHex INTEGER NOT NULL, createdAtEpochMs INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE custom_media_overrides (mediaId TEXT NOT NULL PRIMARY KEY, localVideoPath TEXT, localImagePath TEXT, sourceLabel TEXT NOT NULL, updatedAtEpochMs INTEGER NOT NULL)")
                        db.execSQL("INSERT INTO profile_settings VALUES (1, '2-3', 'sunny_cub', 'teacher_warm_default', 1, 1, 1, 1, 1, 1, 7, 4)")
                        db.execSQL("INSERT INTO activity_progress VALUES ('existing', 'reading', 'land', 3, 2, 4, 2, 1, 0, 88, 1, 100, 7)")
                        db.execSQL("INSERT INTO saved_artworks VALUES (1, 'My drawing', 'canvas', '[]', 0, 100)")
                    }
                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                }).build()
        )
        helper.writableDatabase
        helper.close()
        val database = Room.databaseBuilder(context, WonderDatabase::class.java, name)
            .addMigrations(WonderDatabase.MIGRATION_3_4).build()
        try {
            val dao = database.wonderDao()
            val settings = requireNotNull(dao.getSettingsOnce())
            assertEquals("2-3", settings.subjectLevel(LearningSubject.READING).id)
            assertEquals(7, settings.alphabetJourneyStep)
            assertTrue(settings.reducedMotion)
            assertEquals(7, dao.getProgressForActivity("existing")?.savedStepIndex)
            database.openHelper.readableDatabase.query("SELECT title FROM saved_artworks WHERE id=1").use {
                assertTrue(it.moveToFirst()); assertEquals("My drawing", it.getString(0))
            }
            dao.updateReadingLevel("5-6")
            dao.updateMathLevel("2-3")
            assertEquals("5-6", dao.getSettingsOnce()?.readingLevelId)
            assertEquals("2-3", dao.getSettingsOnce()?.mathLevelId)
            val garden = GardenMission.fresh(requireNotNull(dao.getSettingsOnce()))
            dao.saveGarden(garden.copy(stage = 1, seedCount = 2))
            database.close()
            val reopened = Room.databaseBuilder(context, WonderDatabase::class.java, name).build()
            try {
                assertEquals(2, reopened.wonderDao().getGarden()?.seedCount)
                assertEquals("5-6", reopened.wonderDao().getGarden()?.readingLevelId)
            } finally { reopened.close() }
        } finally { database.close(); context.deleteDatabase(name) }
    }
}
