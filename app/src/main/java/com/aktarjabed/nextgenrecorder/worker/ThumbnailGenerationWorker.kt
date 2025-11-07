package com.aktarjabed.nextgenrecorder.worker

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aktarjabed.nextgenrecorder.domain.repository.RecordingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.FileOutputStream

class ThumbnailGenerationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val recordingRepository: RecordingRepository by inject(RecordingRepository::class.java)
    private val tag = "ThumbnailGenerationWorker"

    override suspend fun doWork(): Result {
        return try {
            Log.d(tag, "Starting thumbnail generation worker")

            val recordingId = inputData.getLong("recording_id", -1)
            if (recordingId == -1L) {
                Log.e(tag, "No recording ID provided")
                return Result.failure()
            }

            val recording = recordingRepository.getRecordingById(recordingId)
            if (recording == null) {
                Log.e(tag, "Recording not found: $recordingId")
                return Result.failure()
            }

            generateThumbnail(recording)

            Log.d(tag, "Successfully generated thumbnail for: ${recording.title}")
            Result.success()
        } catch (e: Exception) {
            Log.e(tag, "Error in thumbnail generation worker", e)
            Result.failure()
        }
    }

    private suspend fun generateThumbnail(recording: com.aktarjabed.nextgenrecorder.data.model.Recording) {
        withContext(Dispatchers.IO) {
            if (recording.recordingType == com.aktarjabed.nextgenrecorder.data.model.RecordingType.VIDEO) {
                generateVideoThumbnail(recording)
            } else {
                generateAudioThumbnail(recording)
            }
        }
    }

    private fun generateVideoThumbnail(recording: com.aktarjabed.nextgenrecorder.data.model.Recording) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(applicationContext, recording.uri)
            val bitmap = retriever.frameAtTime // Get frame at 1 second

            bitmap?.let { saveThumbnail(it, recording.id) }
        } catch (e: Exception) {
            Log.e(tag, "Error generating video thumbnail", e)
        } finally {
            retriever.release()
        }
    }

    private fun generateAudioThumbnail(recording: com.aktarjabed.nextgenrecorder.data.model.Recording) {
        // Create a waveform visualization or use a default audio icon
        // For now, we'll just create a placeholder
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        saveThumbnail(bitmap, recording.id)
    }

    private fun saveThumbnail(bitmap: Bitmap, recordingId: Long) {
        val file = File(applicationContext.filesDir, "thumbnails")
        if (!file.exists()) {
            file.mkdirs()
        }

        val thumbnailFile = File(file, "thumbnail_$recordingId.jpg")
        FileOutputStream(thumbnailFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }

        // Update recording with thumbnail path
        // recordingRepository.updateThumbnailPath(recordingId, thumbnailFile.absolutePath)
    }
}