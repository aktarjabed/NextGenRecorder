package com.aktarjabed.nextgenrecorder.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ConsentMeta(
val meetingId: String,
val title: String,
val participants: List<String>,
val consentTimestamp: Long,
val preset: MeetingPreset
)

enum class MeetingPreset {
SALES_DEMO, INTERVIEW, SUPPORT_CALL, BOARD_MEETING
}
