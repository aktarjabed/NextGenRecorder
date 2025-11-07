package com.aktarjabed.nextgenrecorder.recorder.audio

import android.content.ContentValues
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale

class AudioRecorder(
private val context: Context,
private val dspPipeline: DspPipeline, // Fixed: Now properly injected
private val voiceActivityDetector: VoiceActivityDetector // Fixed: Now properly injected
) {
private var audioRecord: AudioRecord? = null
private var recordingJob: Job? = null
private var outputStream: OutputStream? = null
private val tag = "AudioRecorder"

// Audio configuration
private val sampleRate = 48000
private val channelConfig = AudioFormat.CHANNEL_IN_MONO
private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

private var isRecording = false
private var totalSamples = 0
private var recordingUri: android.net.Uri? = null

fun startRecording(
filename: String = generateTimestampFilename(),
enableVad: Boolean = true
): Result<Unit> {
Log.d(tag, "Starting audio recording: $filename")

if (isRecording) {
stopRecording()
}

// Setup output file
val contentValues = ContentValues().apply {
put(MediaStore.Audio.Media.DISPLAY_NAME, "$filename.wav")
put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/NextGenRecorder")
}

val uri = context.contentResolver.insert(
MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
contentValues
) ?: throw IllegalStateException("Failed to create audio file")

recordingUri = uri
outputStream = context.contentResolver.openOutputStream(uri)
?: throw IllegalStateException("Failed to open output stream")

// Initialize AudioRecord
audioRecord = AudioRecord.Builder()
.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED)
.setAudioFormat(
android.media.AudioFormat.Builder()
.setEncoding(audioFormat)
.setSampleRate(sampleRate)
.setChannelMask(channelConfig)
.build()
)
.setBufferSizeInBytes(bufferSize * 2)
.build()

// Enable built-in effects
dspPipeline.enableBuiltInEffects(audioRecord!!.audioSessionId)

// Write WAV header (will be updated later)
writeWavHeader(outputStream!!, sampleRate, 1, 16, 0)
totalSamples = 0

// Start recording
audioRecord!!.startRecording()
isRecording = true

recordingJob = CoroutineScope(Dispatchers.IO).launch {
val buffer = ShortArray(bufferSize)

while (isActive && isRecording) {
val samplesRead = audioRecord!!.read(buffer, 0, buffer.size)

if (samplesRead > 0) {
// Process audio through DSP pipeline
val processedSamples = dspPipeline.process(buffer, samplesRead)

// Apply VAD if enabled
val shouldWrite = !enableVad || voiceActivityDetector.isSpeech(processedSamples, samplesRead)

if (shouldWrite) {
val bytes = shortsToBytes(processedSamples, samplesRead)
outputStream!!.write(bytes)
totalSamples += samplesRead
}
} else if (samplesRead == AudioRecord.ERROR_INVALID_OPERATION) {
Log.e(tag, "AudioRecord ERROR_INVALID_OPERATION")
break
} else if (samplesRead == AudioRecord.ERROR_BAD_VALUE) {
Log.e(tag, "AudioRecord ERROR_BAD_VALUE")
break
}
}
}

Log.d(tag, "Audio recording started successfully")
    return Result.success(Unit)
}

fun stopRecording(): Result<Unit> = runCatching {
    Log.d(tag, "Stopping audio recording")

    isRecording = false
    recordingJob?.cancel()
    recordingJob = null

    audioRecord?.let { recorder ->
        if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            recorder.stop()
        }
        recorder.release()
    }
    audioRecord = null

    outputStream?.let { stream ->
        stream.flush()
        stream.close()

        // Fix: Update WAV header with actual file size
        updateWavHeaderWithActualSize()
    }
    outputStream = null

    Log.d(tag, "Audio recording stopped. Total samples: $totalSamples")
}

private fun updateWavHeaderWithActualSize() {
    recordingUri?.let { uri ->
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "rw")
            parcelFileDescriptor?.use { pfd ->
                val file = RandomAccessFile(pfd.fileDescriptor, "rw")

                val dataSize = totalSamples * 2 // 16-bit = 2 bytes per sample
                val totalSize = 36 + dataSize

                // Update RIFF chunk size
                file.seek(4)
                file.write(intToBytes(totalSize))

                // Update data chunk size
                file.seek(40)
                file.write(intToBytes(dataSize))

                file.close()
                Log.d(tag, "WAV header updated: dataSize=$dataSize, totalSize=$totalSize")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to update WAV header", e)
        }
    }
}

private fun intToBytes(value: Int): ByteArray {
    return byteArrayOf(
        (value and 0xFF).toByte(),
        ((value shr 8) and 0xFF).toByte(),
        ((value shr 16) and 0xFF).toByte(),
        ((value shr 24) and 0xFF).toByte()
    )
}

private fun generateTimestampFilename(): String {
val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
return "AUD_${timestamp}"
}

private fun writeWavHeader(
out: OutputStream,
sampleRate: Int,
channels: Int,
bitsPerSample: Int,
dataSize: Int
) {
val byteRate = sampleRate * channels * bitsPerSample / 8
val blockAlign = (channels * bitsPerSample / 8).toShort()
val totalSize = 36 + dataSize

val buffer = ByteBuffer.allocate(44).order(java.nio.ByteOrder.LITTLE_ENDIAN)

// RIFF header
buffer.put("RIFF".toByteArray())
buffer.putInt(totalSize)
buffer.put("WAVE".toByteArray())

// fmt chunk
buffer.put("fmt ".toByteArray())
buffer.putInt(16) // chunk size
buffer.putShort(1) // PCM format
buffer.putShort(channels.toShort())
buffer.putInt(sampleRate)
buffer.putInt(byteRate)
buffer.putShort(blockAlign)
buffer.putShort(bitsPerSample.toShort())

// data chunk
buffer.put("data".toByteArray())
buffer.putInt(dataSize)

out.write(buffer.array())
}

private fun shortsToBytes(shorts: ShortArray, length: Int): ByteArray {
val bytes = ByteArray(length * 2)
for (i in 0 until length) {
bytes[i * 2] = (shorts[i] and 0xFF).toByte()
bytes[i * 2 + 1] = (shorts[i] shr 8).toByte()
}
return bytes
}
}
