package com.aktarjabed.nextgenrecorder.data.repository

import android.content.Context
import android.net.Uri

class MediaRepositoryImpl(private val context: Context) : MediaRepository {
override suspend fun getRecordings(): Result<List<com.aktarjabed.nextgenrecorder.data.model.Recording>> {
// TODO: Implement MediaStore query
return Result.success(emptyList())
}

override suspend fun deleteRecording(uri: Uri): Result<Unit> {
// TODO: Implement MediaStore delete
    return Result.success(Unit)
}
}

interface MediaRepository {
suspend fun getRecordings(): Result<List<com.aktarjabed.nextgenrecorder.data.model.Recording>>
suspend fun deleteRecording(uri: Uri): Result<Unit>
}
