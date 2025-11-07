package com.aktarjabed.nextgenrecorder.recorder.video

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class VideoRecorder(private val context: Context, private val cameraController: CameraController) {
    fun startRecording(): Flow<androidx.camera.video.VideoRecordEvent> {
        return flowOf()
    }

    fun stopRecording() {
    }
}