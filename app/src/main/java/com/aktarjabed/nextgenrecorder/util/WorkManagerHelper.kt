package com.aktarjabed.nextgenrecorder.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.aktarjabed.nextgenrecorder.worker.SilenceRemovalWorker
import com.aktarjabed.nextgenrecorder.worker.ThumbnailGenerationWorker
import java.util.concurrent.TimeUnit

class WorkManagerHelper(private val context: Context) {

    fun scheduleSilenceRemoval(recordingId: Long) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val inputData = Data.Builder()
            .putLong("recording_id", recordingId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SilenceRemovalWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setInitialDelay(5, TimeUnit.SECONDS) // Delay to ensure recording is complete
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "silence_removal_$recordingId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleThumbnailGeneration(recordingId: Long) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .build()

        val inputData = Data.Builder()
            .putLong("recording_id", recordingId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ThumbnailGenerationWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setInitialDelay(2, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "thumbnail_generation_$recordingId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun schedulePeriodicCleanup() {
        // Schedule periodic cleanup of temporary files
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build()

        // This would be a PeriodicWorkRequest in a real implementation
    }
}