package com.aktarjabed.nextgenrecorder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aktarjabed.nextgenrecorder.ui.theme.RecordingRed
import com.aktarjabed.nextgenrecorder.ui.viewmodels.VideoRecorderViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoRecorderScreen(onNavigateBack: () -> Unit) {
val context = LocalContext.current
val lifecycleOwner = LocalLifecycleOwner.current
val coroutineScope = rememberCoroutineScope()
val viewModel: VideoRecorderViewModel = koinViewModel() // Fixed: Using Koin instead of Hilt

val uiState by viewModel.uiState.collectAsState()
var cameraInitialized by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
viewModel.initializeCamera(context, lifecycleOwner)
cameraInitialized = true
}

Scaffold(
topBar = {
CenterAlignedTopAppBar(
title = {
Text(
text = "Video Recorder",
fontWeight = FontWeight.SemiBold
)
},
navigationIcon = {
IconButton(onClick = onNavigateBack) {
Icon(Icons.Default.ArrowBack, contentDescription = "Back")
}
},
colors = TopAppBarDefaults.topAppBarColors(
containerColor = Color.Transparent
)
)
},
floatingActionButton = {
FloatingActionButton(
onClick = {
coroutineScope.launch {
if (uiState.isRecording) {
viewModel.stopRecording()
} else {
viewModel.startRecording()
}
}
},
modifier = Modifier.padding(bottom = 16.dp)
) {
Icon(
imageVector = if (uiState.isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
contentDescription = if (uiState.isRecording) "Stop" else "Record",
modifier = Modifier.size(24.dp)
)
}
}
) { innerPadding ->
Box(
modifier = Modifier
.fillMaxSize()
.padding(innerPadding)
) {
if (cameraInitialized && uiState.isCameraInitialized) {
// Camera Preview
AndroidView(
factory = { context ->
    try {
        (viewModel as any).cameraController.previewView
    } catch (e: Exception) {
        android.widget.TextView(context).apply {
            text = "Error accessing previewView: ${e.message}"
        }
    }
},
modifier = Modifier.fillMaxSize()
)


// Recording Indicator
if (uiState.isRecording) {
Box(
modifier = Modifier
.fillMaxWidth()
.background(RecordingRed.copy(alpha = 0.8f))
.padding(8.dp)
.align(Alignment.TopCenter)
) {
Text(
text = "● RECORDING",
color = Color.White,
fontWeight = FontWeight.Bold,
modifier = Modifier.align(Alignment.Center)
)
}
}

// Recording Timer
if (uiState.isRecording) {
Box(
modifier = Modifier
.align(Alignment.BottomCenter)
.padding(bottom = 80.dp)
) {
Text(
text = formatTime(uiState.recordingDuration),
style = MaterialTheme.typography.headlineMedium,
color = Color.White,
fontWeight = FontWeight.Bold
)
}
}
} else {
// Loading State
Column(
modifier = Modifier.fillMaxSize(),
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
) {
CircularProgressIndicator()
Spacer(modifier = Modifier.height(16.dp))
Text("Initializing Camera...")

uiState.cameraError?.let { error ->
Spacer(modifier = Modifier.height(8.dp))
Text("Error: $error", color = Color.Red)
}
}
}

// Recording Error
uiState.recordingError?.let { error ->
Box(
modifier = Modifier
.align(Alignment.BottomCenter)
.padding(bottom = 120.dp)
) {
Text(
text = error,
color = Color.Red,
style = MaterialTheme.typography.bodyMedium
)
}
}
}
}
}

private fun formatTime(milliseconds: Long): String {
val seconds = (milliseconds / 1000) % 60
val minutes = (milliseconds / (1000 * 60)) % 60
val hours = (milliseconds / (1000 * 60 * 60))

return if (hours > 0) {
String.format("%02d:%02d:%02d", hours, minutes, seconds)
} else {
String.format("%02d:%02d", minutes, seconds)
}
}
