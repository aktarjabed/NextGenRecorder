package com.aktarjabed.nextgenrecorder.util

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.ktx.performance
import com.google.firebase.perf.metrics.Trace

class AnalyticsManager(private val context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }
    private val crashlytics: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }
    private val performance: FirebasePerformance by lazy { Firebase.performance }

    companion object {
        private const val TAG = "AnalyticsManager"

        // Event names
        const val EVENT_RECORDING_STARTED = "recording_started"
        const val EVENT_RECORDING_STOPPED = "recording_stopped"
        const val EVENT_RECORDING_ERROR = "recording_error"
        const val EVENT_CONSENT_GIVEN = "consent_given"
        const val EVENT_APP_CRASH = "app_crash"

        // Parameter names
        const val PARAM_RECORDING_TYPE = "recording_type"
        const val PARAM_DURATION = "duration"
        const val PARAM_FILE_SIZE = "file_size"
        const val PARAM_ERROR_MESSAGE = "error_message"
        const val PARAM_MEETING_TYPE = "meeting_type"
    }

    fun logRecordingStarted(recordingType: String, meetingType: String? = null) {
        try {
            firebaseAnalytics.logEvent(EVENT_RECORDING_STARTED) {
                param(PARAM_RECORDING_TYPE, recordingType)
                meetingType?.let { param(PARAM_MEETING_TYPE, it) }
            }
            Log.d(TAG, "Logged recording started: $recordingType")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log recording started", e)
        }
    }

    fun logRecordingStopped(recordingType: String, duration: Long, fileSize: Long) {
        try {
            firebaseAnalytics.logEvent(EVENT_RECORDING_STOPPED) {
                param(PARAM_RECORDING_TYPE, recordingType)
                param(PARAM_DURATION, duration)
                param(PARAM_FILE_SIZE, fileSize)
            }
            Log.d(TAG, "Logged recording stopped: $recordingType, duration: $duration, size: $fileSize")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log recording stopped", e)
        }
    }

    fun logRecordingError(recordingType: String, error: String) {
        try {
            firebaseAnalytics.logEvent(EVENT_RECORDING_ERROR) {
                param(PARAM_RECORDING_TYPE, recordingType)
                param(PARAM_ERROR_MESSAGE, error)
            }

            // Also log to Crashlytics for better error tracking
            crashlytics.log("Recording error: $error")
            crashlytics.setCustomKey("recording_type", recordingType)

            Log.e(TAG, "Logged recording error: $recordingType - $error")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log recording error", e)
        }
    }

    fun logConsentGiven(meetingType: String, participants: Int) {
        try {
            firebaseAnalytics.logEvent(EVENT_CONSENT_GIVEN) {
                param(PARAM_MEETING_TYPE, meetingType)
                param("participants_count", participants.toLong())
            }
            Log.d(TAG, "Logged consent given: $meetingType with $participants participants")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log consent given", e)
        }
    }

    fun logNonFatalError(throwable: Throwable, context: String) {
        try {
            crashlytics.apply {
                log("Non-fatal error in context: $context")
                setCustomKey("error_context", context)
                recordException(throwable)
            }
            Log.e(TAG, "Logged non-fatal error: $context", throwable)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to log non-fatal error", e)
        }
    }

    fun setUserProperties(userId: String? = null, recordingQuality: String? = null) {
        try {
            userId?.let { crashlytics.setUserId(it) }
            recordingQuality?.let {
                firebaseAnalytics.setUserProperty("preferred_quality", it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set user properties", e)
        }
    }

    fun startTrace(traceName: String): Trace {
        return performance.newTrace(traceName).apply { start() }
    }
}