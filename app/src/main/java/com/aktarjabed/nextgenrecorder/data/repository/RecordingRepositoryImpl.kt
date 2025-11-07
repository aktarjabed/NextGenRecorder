package com.aktarjabed.nextgenrecorder.data.repository

import com.aktarjabed.nextgenrecorder.data.database.dao.RecordingDao
import com.aktarjabed.nextgenrecorder.data.database.entity.RecordingEntity
import com.aktarjabed.nextgenrecorder.data.model.Recording
import com.aktarjabed.nextgenrecorder.data.model.RecordingType
import com.aktarjabed.nextgenrecorder.domain.repository.RecordingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class RecordingRepositoryImpl(
    private val recordingDao: RecordingDao
) : RecordingRepository {

    override fun getAllRecordings(): Flow<List<Recording>> {
        return recordingDao.getAllRecordings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecordingsByType(type: RecordingType): Flow<List<Recording>> {
        return recordingDao.getRecordingsByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteRecordings(): Flow<List<Recording>> {
        return recordingDao.getFavoriteRecordings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchRecordings(query: String): Flow<List<Recording>> {
        return recordingDao.searchRecordings(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getRecordingById(id: Long): Recording? {
        return recordingDao.getRecordingById(id)?.toDomain()
    }

    override suspend fun saveRecording(recording: Recording): Long {
        return recordingDao.insertRecording(recording.toEntity())
    }

    override suspend fun updateRecording(recording: Recording) {
        recordingDao.updateRecording(recording.toEntity())
    }

    override suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        recordingDao.setFavorite(id, isFavorite)
    }

    override suspend fun deleteRecording(id: Long) {
        recordingDao.deleteRecordingById(id)
    }

    override suspend fun getTotalStorageUsed(): Long {
        return recordingDao.getTotalStorageUsed()
    }

    override suspend fun getRecordingCountByType(type: RecordingType): Int {
        return recordingDao.getRecordingCountByType(type)
    }
}

// Extension functions for conversion
private fun RecordingEntity.toDomain(): Recording {
    return Recording(
        id = id,
        uri = android.net.Uri.parse(uri),
        title = title,
        duration = duration,
        fileSize = fileSize,
        createdAt = createdAt,
        recordingType = recordingType,
        quality = quality,
        consentMeta = consentMeta
    )
}

private fun Recording.toEntity(): RecordingEntity {
    return RecordingEntity(
        id = id,
        uri = uri.toString(),
        title = title,
        duration = duration,
        fileSize = fileSize,
        createdAt = createdAt,
        recordingType = recordingType,
        quality = quality,
        consentMeta = consentMeta,
        thumbnailPath = null,
        isFavorite = false,
        tags = emptyList()
    )
}
