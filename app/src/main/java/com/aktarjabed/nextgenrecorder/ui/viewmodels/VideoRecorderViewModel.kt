package com.aktarjabed.nextgenrecorder.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aktarjabed.nextgenrecorder.recorder.video.CameraController
import com.aktarjabed.nextgenrecorder.recorder.video.VideoRecorder
import com.aktarjabed.nextgenrecorder.util.AnalyticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoRecorderViewModel(
private val cameraController: CameraController,
private val videoRecorder: VideoRecorder,
private val analyticsManager: AnalyticsManager
) : ViewModel() {

private val _uiState = MutableStateFlow(VideoRecorderUiState())
val uiState: StateFlow<VideoRecorderUiState> = _uiState.asStateFlow()

private var recordingJob: kotlinx.coroutines.Job? = null

suspend fun initializeCamera(context: Context, lifecycleOwner: LifecycleOwner) {
try {
cameraController.initialize(lifecycleOwner)
_uiState.value = _uiState.value.copy(
cameraError = null,
isCameraInitialized = true
)
} catch (e: Exception) {
_uiState.value = _uiState.value.copy(
cameraError = e.message ?: "Unknown camera error"
)
}
}

fun startRecording() {
recordingJob = viewModelScope.launch {
try {
analyticsManager.logRecordingStarted("video")
videoRecorder.startRecording().collect { event ->
when (event) {
is androidx.camera.video.VideoRecordEvent.Start -> {
_uiState.value = _uiState.value.copy(
isRecording = true,
recordingStartTime = System.currentTimeMillis()
)
startRecordingTimer()
}
is androidx.camera.video.VideoRecordEvent.Finalize -> {
_uiState.value = _uiState.value.copy(
isRecording = false,
recordingDuration = 0L
)
if (event.hasError()) {
analyticsManager.logRecordingError("video", "Recording failed: ${event.error}")
_uiState.value = _uiState.value.copy(
recordingError = "Recording failed: ${event.error}"
)
} else {
_uiState.value = _uiState.value.copy(
recordingError = null
)
}
}
else -> {}
}
}
} catch (e: Exception) {
analyticsManager.logRecordingError("video", e.message ?: "Unknown error")
_uiState.value = _uiState.value.copy(
recordingError = "Failed to start recording: ${e.message}",
isRecording = false
)
}
}
}

fun stopRecording() {
recordingJob?.cancel()
videoRecorder.stopRecording()
analyticsManager.logRecordingStopped("video", uiState.value.recordingDuration, 0)
_uiState.value = _uiState.value.copy(
isRecording = false,
recordingDuration = 0L
)
}

private fun startRecordingTimer() {
viewModelScope.launch {
while (_uiState.value.isRecording) {
kotlinx.coroutines.ensureActive()
val currentTime = System.currentTimeMillis()
val duration = currentTime - (_uiState.value.recordingStartTime ?: currentTime)
_uiState.value = _uiState.value.copy(recordingDuration = duration)
kotlinx.coroutines.delay(1000)
}
}
}

override fun onCleared() {
super.onCleared()
recordingJob?.cancel()
cameraController.release()
}
}

data class VideoRecorderUiState(
val isCameraInitialized: Boolean = false,
val isRecording: Boolean = false,
val recordingStartTime: Long? = null,
val recordingDuration: Long = 0L,
val cameraError: String? = null,
val recordingError: String? = null
)
