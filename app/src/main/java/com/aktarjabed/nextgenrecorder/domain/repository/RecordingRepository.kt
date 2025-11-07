package com.aktarjabed.nextgenrecorder.domain.repository

import com.aktarjabed.nextgenrecorder.data.model.Recording
import com.aktarjabed.nextgenrecorder.data.model.RecordingType
import kotlinx.coroutines.flow.Flow

interface RecordingRepository {
    fun getAllRecordings(): Flow<List<Recording>>
    fun getRecordingsByType(type: RecordingType): Flow<List<Recording>>
    fun getFavoriteRecordings(): Flow<List<Recording>>
    fun searchRecordings(query: String): Flow<List<Recording>>
    suspend fun getRecordingById(id: Long): Recording?
    suspend fun saveRecording(recording: Recording): Long
    suspend fun updateRecording(recording: Recording)
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
    suspend fun deleteRecording(id: Long)
    suspend fun getTotalStorageUsed(): Long
    suspend fun getRecordingCountByType(type: RecordingType): Int
}
