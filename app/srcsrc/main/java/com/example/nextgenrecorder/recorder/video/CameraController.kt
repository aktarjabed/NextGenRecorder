package com.example.nextgenrecorder.recorder.video

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.FallbackStrategy
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraController(
private val context: Context,
private val cameraSelector: CameraSelector,
private val previewView: PreviewView // Injected, not lateinit
) {
private var cameraProvider: ProcessCameraProvider? = null
private var camera: Camera? = null
private var preview: Preview? = null

lateinit var videoCapture: VideoCapture<Recorder>

private val tag = "CameraController"

suspend fun initialize(lifecycleOwner: LifecycleOwner) {
Log.d(tag, "Initializing camera...")

val provider = suspendCancellableCoroutine<ProcessCameraProvider> { continuation ->
ProcessCameraProvider.getInstance(context).also { future ->
future.addListener({
try {
continuation.resume(future.get())
} catch (e: Exception) {
continuation.resumeWithException(e)
}
}, ContextCompat.getMainExecutor(context))
}
}

cameraProvider = provider

// Setup quality selector with proper fallback
val qualitySelector = QualitySelector.fromOrderedList(
listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD),
FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
)

val recorder = Recorder.Builder()
.setQualitySelector(qualitySelector)
.build()

videoCapture = VideoCapture.withOutput(recorder)

// Setup preview using the injected PreviewView
preview = Preview.Builder().build().apply {
setSurfaceProvider(previewView.surfaceProvider)
}

// Bind use cases to lifecycle
provider.unbindAll()
camera = provider.bindToLifecycle(
lifecycleOwner,
cameraSelector,
preview,
videoCapture
)

Log.d(tag, "Camera initialized successfully")
}

fun setZoom(zoomRatio: Float) {
val currentZoomState = camera?.cameraInfo?.zoomState?.value
currentZoomState?.let { state ->
val minZoom = state.minZoomRatio
val maxZoom = state.maxZoomRatio
val clampedZoom = zoomRatio.coerceIn(minZoom, maxZoom)
camera?.cameraControl?.setZoomRatio(clampedZoom)
Log.d(tag, "Zoom set to: $clampedZoom (min: $minZoom, max: $maxZoom)")
}
}

fun release() {
Log.d(tag, "Releasing camera resources")
cameraProvider?.unbindAll()
camera = null
preview = null
}
}
