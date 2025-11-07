package com.aktarjabed.nextgenrecorder.recorder.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class RecordingService : Service() {

override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
// Create and start foreground notification
val notification = createRecordingNotification()
startForeground(1, notification)

return START_STICKY
}

override fun onBind(intent: Intent?): IBinder? = null

private fun createRecordingNotification(): Notification {
return NotificationCompat.Builder(this, "recording_channel")
.setContentTitle("NextGenRecorder")
.setContentText("Recording in progress")
.setSmallIcon(android.R.drawable.ic_media_play)
.setOngoing(true)
.build()
}
}

class RecordingServiceManager(private val context: android.content.Context) {
fun startRecordingService() {
val intent = Intent(context, RecordingService::class.java)
context.startForegroundService(intent)
}

fun stopRecordingService() {
val intent = Intent(context, RecordingService::class.java)
context.stopService(intent)
}
}
