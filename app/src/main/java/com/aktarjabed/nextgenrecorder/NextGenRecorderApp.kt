package com.aktarjabed.nextgenrecorder

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.WorkManager
import com.aktarjabed.nextgenrecorder.di.AppModule
import com.aktarjabed.nextgenrecorder.util.AnalyticsManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NextGenRecorderApp : Application() {

private lateinit var analyticsManager: AnalyticsManager

override fun onCreate() {
    super.onCreate()

    // Initialize Firebase
    FirebaseApp.initializeApp(this)

    // Initialize Koin DI
    startKoin {
        androidLogger(Level.ERROR)
        androidContext(this@NextGenRecorderApp)
        modules(AppModule.appModule)
    }

    // Enable Crashlytics in release builds
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

    // Initialize Analytics Manager
    analyticsManager = AnalyticsManager(this)

    // Create notification channels
    createNotificationChannels()

    // Initialize WorkManager
    WorkManager.initialize(this, androidx.work.Configuration.Builder().build())

    // Set up global exception handler
    setupExceptionHandler()
}

private fun createNotificationChannels() {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
val recordingChannel = NotificationChannel(
"recording_channel",
"Recording Notifications",
NotificationManager.IMPORTANCE_HIGH
).apply {
description = "Notifications for active recordings"
setShowBadge(false)
}

val serviceChannel = NotificationChannel(
"service_channel",
"Background Service",
NotificationManager.IMPORTANCE_LOW
).apply {
description = "Background service notifications"
setShowBadge(false)
}

val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
notificationManager.createNotificationChannels(listOf(recordingChannel, serviceChannel))
}
}

private fun setupExceptionHandler() {
    val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        // Log the crash to Crashlytics
        Firebase.crashlytics.recordException(throwable)

        // Log custom analytics event
        analyticsManager.logRecordingError("app_crash", throwable.message ?: "Unknown error")

        // Call original handler
        defaultHandler?.uncaughtException(thread, throwable)
    }
}
}