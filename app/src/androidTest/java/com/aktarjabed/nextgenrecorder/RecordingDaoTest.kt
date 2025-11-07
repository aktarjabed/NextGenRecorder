package com.aktarjabed.nextgenrecorder

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aktarjabed.nextgenrecorder.data.database.AppDatabase
import com.aktarjabed.nextgenrecorder.data.database.dao.RecordingDao
import com.aktarjabed.nextgenrecorder.data.database.entity.RecordingEntity
import com.aktarjabed.nextgenrecorder.domain.model.Quality
import com.aktarjabed.nextgenrecorder.domain.model.RecordingType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class RecordingDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var recordingDao: RecordingDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        recordingDao = database.recordingDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun `insert and retrieve recording`() = runBlocking {
        // Given
        val recording = RecordingEntity(
            uri = "content://media/recording1",
            title = "Test Recording",
            duration = 60000L,
            fileSize = 1024L * 1024L,
            createdAt = LocalDateTime.now(),
            recordingType = RecordingType.VIDEO,
            quality = Quality.FHD
        )

        // When
        val id = recordingDao.insertRecording(recording)
        val allRecordings = recordingDao.getAllRecordings().first()

        // Then
        assertEquals(1, allRecordings.size)
        assertEquals("Test Recording", allRecordings[0].title)
    }

    @Test
    fun `update recording favorite status`() = runBlocking {
        // Given
        val recording = RecordingEntity(
            uri = "content://media/recording1",
            title = "Test Recording",
            duration = 60000L,
            fileSize = 1024L * 1024L,
            createdAt = LocalDateTime.now(),
            recordingType = RecordingType.VIDEO,
            quality = Quality.FHD
        )

        // When
        val id = recordingDao.insertRecording(recording)
        recordingDao.setFavorite(id, true)
        val favoriteRecordings = recordingDao.getFavoriteRecordings().first()

        // Then
        assertEquals(1, favoriteRecordings.size)
        assertTrue(favoriteRecordings[0].isFavorite)
    }
}