package com.aktarjabed.nextgenrecorder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aktarjabed.nextgenrecorder.data.database.dao.RecordingDao
import com.aktarjabed.nextgenrecorder.data.database.entity.RecordingEntity
import com.aktarjabed.nextgenrecorder.data.database.converter.Converters

@Database(
    entities = [RecordingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordingDao(): RecordingDao
}
