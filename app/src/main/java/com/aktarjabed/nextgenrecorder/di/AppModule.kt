package com.aktarjabed.nextgenrecorder.di

import android.app.Application
import androidx.room.Room
import com.aktarjabed.nextgenrecorder.data.database.AppDatabase
import com.aktarjabed.nextgenrecorder.data.repository.RecordingRepositoryImpl
import com.aktarjabed.nextgenrecorder.domain.repository.RecordingRepository
import com.aktarjabed.nextgenrecorder.util.AnalyticsManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object AppModule {
    val appModule = module {
        single {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "nextgenrecorder.db"
            ).fallbackToDestructiveMigration().build()
        }

        single { get<AppDatabase>().recordingDao() }
        single<RecordingRepository> { RecordingRepositoryImpl(get()) }
        single { AnalyticsManager(androidContext()) }
    }
}