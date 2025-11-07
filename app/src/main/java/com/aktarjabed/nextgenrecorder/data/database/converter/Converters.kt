package com.aktarjabed.nextgenrecorder.data.database.converter

import androidx.room.TypeConverter
import com.aktarjabed.nextgenrecorder.domain.model.ConsentMeta
import com.aktarjabed.nextgenrecorder.domain.model.MeetingPreset
import com.aktarjabed.nextgenrecorder.domain.model.Quality
import com.aktarjabed.nextgenrecorder.domain.model.RecordingType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }

    @TypeConverter
    fun fromRecordingType(value: RecordingType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRecordingType(value: String?): RecordingType? {
        return value?.let { RecordingType.valueOf(it) }
    }

    @TypeConverter
    fun fromQuality(value: Quality?): String? {
        return value?.name
    }

    @TypeConverter
    fun toQuality(value: String?): Quality? {
        return value?.let { Quality.valueOf(it) }
    }

    @TypeConverter
    fun fromMeetingPreset(value: MeetingPreset?): String? {
        return value?.name
    }

    @TypeConverter
    fun toMeetingPreset(value: String?): MeetingPreset? {
        return value?.let { MeetingPreset.valueOf(it) }
    }

    @TypeConverter
    fun fromConsentMeta(value: ConsentMeta?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toConsentMeta(value: String?): ConsentMeta? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return value?.let { Json.decodeFromString(it) } ?: emptyList()
    }
}