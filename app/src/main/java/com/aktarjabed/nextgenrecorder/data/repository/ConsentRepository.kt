package com.aktarjabed.nextgenrecorder.data.repository

import com.aktarjabed.nextgenrecorder.domain.model.ConsentMeta

interface ConsentRepository {
suspend fun saveConsent(meta: ConsentMeta): Result<Unit>
suspend fun getConsent(meetingId: String): Result<ConsentMeta?>
}

class ConsentRepositoryImpl : ConsentRepository {
override suspend fun saveConsent(meta: ConsentMeta): Result<Unit> {
// TODO: Implement Room database storage
    return Result.success(Unit)
}

override suspend fun getConsent(meetingId: String): Result<ConsentMeta?> {
// TODO: Implement Room database query
    return Result.success(null)
}
}
