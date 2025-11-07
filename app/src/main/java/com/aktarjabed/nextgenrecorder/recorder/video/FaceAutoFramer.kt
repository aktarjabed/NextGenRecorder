package com.aktarjabed.nextgenrecorder.recorder.video

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class FaceAutoFramer(
    private val context: Context
) {
    private val tag = "FaceAutoFramer"

    // Face detection
    private val detector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.1f)
            .build()
        FaceDetection.getClient(options)
    }

    // Auto-framing state
    private var isEnabled = true
    private var framingJob: Job? = null
    private var lastFaceDetectionTime = 0L
    private var currentZoom = 1.0f

    // Configuration
    private val minZoom = 1.0f
    private val maxZoom = 5.0f
    private val targetFaceSize = 0.3f // 30% of screen width
    private val framingDelay = 300L // ms between frame adjustments
    private val smoothingFactor = 0.7f

    fun enableAutoFraming(enable: Boolean) {
        isEnabled = enable
        if (!enable) {
            framingJob?.cancel()
            resetZoom()
        }
    }

    fun processFrame(bitmap: Bitmap, cameraControl: CameraControl?, cameraInfo: CameraInfo?) {
        if (!isEnabled || cameraControl == null || cameraInfo == null) {
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFaceDetectionTime < framingDelay) {
            return // Throttle face detection
        }

        lastFaceDetectionTime = currentTime

        val image = InputImage.fromBitmap(bitmap, 0)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val primaryFace = findPrimaryFace(faces)
                    adjustFraming(primaryFace, bitmap.width, bitmap.height, cameraControl, cameraInfo)
                } else {
                    // No faces detected, slowly return to default zoom
                    smoothZoomTo(minZoom, cameraControl, cameraInfo)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Face detection failed", exception)
            }
    }

    private fun findPrimaryFace(faces: List<Face>): Face {
        // Find the largest face (closest to camera)
        return faces.maxByOrNull { face ->
            face.boundingBox.width() * face.boundingBox.height()
        } ?: faces.first()
    }

    private fun adjustFraming(face: Face, imageWidth: Int, imageHeight: Int,
                            cameraControl: CameraControl, cameraInfo: CameraInfo) {
        val faceBounds = face.boundingBox
        val faceCenterX = faceBounds.centerX().toFloat() / imageWidth
        val faceCenterY = faceBounds.centerY().toFloat() / imageHeight
        val faceWidth = faceBounds.width().toFloat() / imageWidth

        // Calculate desired zoom based on face size
        val desiredZoom = calculateDesiredZoom(faceWidth)

        // Apply smoothing to avoid jerky movements
        currentZoom = currentZoom * smoothingFactor + desiredZoom * (1 - smoothingFactor)

        // Clamp zoom to camera limits
        val zoomState = cameraInfo.zoomState.value
        val clampedZoom = currentZoom.coerceIn(
            zoomState?.minZoomRatio ?: minZoom,
            zoomState?.maxZoomRatio ?: maxZoom
        )

        // Apply zoom
        CoroutineScope(Dispatchers.Main).launch {
            try {
                cameraControl.setZoomRatio(clampedZoom)
                Log.d(tag, "Auto-framing adjusted zoom to: $clampedZoom, face size: $faceWidth")
            } catch (e: Exception) {
                Log.e(tag, "Failed to set zoom ratio", e)
            }
        }
    }

    private fun calculateDesiredZoom(faceWidth: Float): Float {
        // Calculate zoom needed to make face the target size
        val zoomFactor = targetFaceSize / faceWidth
        return (minZoom * zoomFactor).coerceIn(minZoom, maxZoom)
    }

    private fun smoothZoomTo(targetZoom: Float, cameraControl: CameraControl, cameraInfo: CameraInfo) {
        framingJob?.cancel()
        framingJob = CoroutineScope(Dispatchers.Default).launch {
            val steps = 10
            val stepSize = (targetZoom - currentZoom) / steps

            repeat(steps) {
                currentZoom += stepSize
                val zoomState = cameraInfo.zoomState.value
                val clampedZoom = currentZoom.coerceIn(
                    zoomState?.minZoomRatio ?: minZoom,
                    zoomState?.maxZoomRatio ?: maxZoom
                )

                try {
                    cameraControl.setZoomRatio(clampedZoom)
                } catch (e: Exception) {
                    Log.e(tag, "Failed to set zoom during smooth transition", e)
                }

                delay(50) // 50ms between steps for smooth transition
            }
        }
    }

    private fun resetZoom() {
        currentZoom = minZoom
    }

    fun release() {
        framingJob?.cancel()
        detector.close()
    }
}