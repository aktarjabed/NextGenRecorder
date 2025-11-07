package com.aktarjabed.nextgenrecorder.data.model

import android.net.Uri
import com.aktarjabed.nextgenrecorder.domain.model.ConsentMeta
import java.time.LocalDateTime

data class Recording(
val id: Long = 0,
val uri: Uri,
val title: String,
val duration: Long, // milliseconds
val fileSize: Long, // bytes
val createdAt: LocalDateTime,
val recordingType: RecordingType,
val quality: Quality,
val consentMeta: ConsentMeta? = null
)

enum class RecordingType {
VIDEO, AUDIO, MEETING
}

enum class Quality {
UHD, FHD, HD, SD
}
