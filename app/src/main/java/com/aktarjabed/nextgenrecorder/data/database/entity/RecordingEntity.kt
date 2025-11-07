package com.aktarjabed.nextgenrecorder.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.aktarjabed.nextgenrecorder.data.database.converter.Converters
import com.aktarjabed.nextgenrecorder.domain.model.ConsentMeta
import com.aktarjabed.nextgenrecorder.domain.model.Quality
import com.aktarjabed.nextgenrecorder.domain.model.RecordingType
import java.time.LocalDateTime

@Entity(tableName = "recordings")
@TypeConverters(Converters::class)
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val title: String,
    val duration: Long, // milliseconds
    val fileSize: Long, // bytes
    val createdAt: LocalDateTime,
    val recordingType: RecordingType,
    val quality: Quality,
    val consentMeta: ConsentMeta? = null,
    val thumbnailPath: String? = null,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
)
