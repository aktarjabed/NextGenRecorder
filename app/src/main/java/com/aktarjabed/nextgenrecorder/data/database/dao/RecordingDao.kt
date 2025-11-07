package com.aktarjabed.nextgenrecorder.data.database.dao

import androidx.room.*
import com.aktarjabed.nextgenrecorder.data.database.entity.RecordingEntity
import com.aktarjabed.nextgenrecorder.domain.model.RecordingType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface RecordingDao {

    @Query("SELECT * FROM recordings ORDER BY createdAt DESC")
    fun getAllRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE id = :id")
    suspend fun getRecordingById(id: Long): RecordingEntity?

    @Query("SELECT * FROM recordings WHERE recordingType = :type ORDER BY createdAt DESC")
    fun getRecordingsByType(type: RecordingType): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteRecordings(): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE title LIKE '%' || :query || '%' OR EXISTS (SELECT 1 FROM json_each(tags) WHERE value LIKE '%' || :query || '%') ORDER BY createdAt DESC")
    fun searchRecordings(query: String): Flow<List<RecordingEntity>>

    @Query("SELECT * FROM recordings WHERE createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getRecordingsBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<RecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: RecordingEntity): Long

    @Update
    suspend fun updateRecording(recording: RecordingEntity)

    @Query("UPDATE recordings SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE recordings SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("UPDATE recordings SET tags = :tags WHERE id = :id")
    suspend fun updateTags(id: Long, tags: List<String>)

    @Delete
    suspend fun deleteRecording(recording: RecordingEntity)

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteRecordingById(id: Long)

    @Query("SELECT SUM(fileSize) FROM recordings")
    suspend fun getTotalStorageUsed(): Long

    @Query("SELECT COUNT(*) FROM recordings WHERE recordingType = :type")
    suspend fun getRecordingCountByType(type: RecordingType): Int
}