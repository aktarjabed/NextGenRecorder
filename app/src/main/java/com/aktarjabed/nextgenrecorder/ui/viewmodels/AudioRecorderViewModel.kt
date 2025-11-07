package com.aktarjabed.nextgenrecorder.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aktarjabed.nextgenrecorder.recorder.audio.AudioRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioRecorderViewModel(
private val audioRecorder: AudioRecorder
) : ViewModel() {

private val _uiState = MutableStateFlow(AudioRecorderUiState())
val uiState: StateFlow<AudioRecorderUiState> = _uiState.asStateFlow()

private var recordingJob: kotlinx.coroutines.Job? = null

fun startRecording() {
_uiState.value = _uiState.value.copy(
isRecording = true,
recordingStartTime = System.currentTimeMillis(),
error = null
)

recordingJob = viewModelScope.launch {
startRecordingTimer()

audioRecorder.startRecording().onSuccess {
// Recording started successfully
}.onFailure { error ->
_uiState.value = _uiState.value.copy(
error = "Failed to start recording: ${error.message}",
isRecording = false
)
}
}
}

fun stopRecording() {
recordingJob?.cancel()

viewModelScope.launch {
audioRecorder.stopRecording().onSuccess {
_uiState.value = _uiState.value.copy(
isRecording = false,
recordingDuration = 0L,
voiceDetected = false
)
}.onFailure { error ->
_uiState.value = _uiState.value.copy(
error = "Failed to stop recording: ${error.message}"
)
}
}
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
if (_uiState.value.isRecording) {
stopRecording()
}
}
}

data class AudioRecorderUiState(
val isRecording: Boolean = false,
val recordingStartTime: Long? = null,
val recordingDuration: Long = 0L,
val voiceDetected: Boolean = false,
val error: String? = null
)
