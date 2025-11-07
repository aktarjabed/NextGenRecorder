package com.aktarjabed.nextgenrecorder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aktarjabed.nextgenrecorder.ui.theme.RecordingRed
import com.aktarjabed.nextgenrecorder.ui.viewmodels.AudioRecorderViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderScreen(onNavigateBack: () -> Unit) {
val viewModel: AudioRecorderViewModel = koinViewModel()
val uiState by viewModel.uiState.collectAsState()

Scaffold(
topBar = {
CenterAlignedTopAppBar(
title = {
Text(
text = "Audio Recorder"
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
if (uiState.isRecording) {
viewModel.stopRecording()
} else {
viewModel.startRecording()
}
}
) {
Icon(
imageVector = if (uiState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
contentDescription = if (uiState.isRecording) "Stop" else "Record",
modifier = Modifier.size(24.dp)
)
}
}
) { innerPadding ->
Box(
modifier = Modifier
.fillMaxSize()
.padding(innerPadding),
contentAlignment = Alignment.Center
) {
Column(
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.spacedBy(16.dp)
) {
if (uiState.isRecording) {
Box(
modifier = Modifier
.padding(16.dp)
.background(RecordingRed.copy(alpha = 0.8f))
.padding(horizontal = 24.dp, vertical = 8.dp)
) {
Text(
text = "● RECORDING AUDIO",
color = Color.White
)
}

Text(
text = formatTime(uiState.recordingDuration),
style = MaterialTheme.typography.headlineMedium
)

// Audio waveform visualization would go here
CircularProgressIndicator(
modifier = Modifier.size(100.dp),
strokeWidth = 8.dp
)

Text(
text = "Voice Activity: ${if (uiState.voiceDetected) "Active" else "Silent"}",
style = MaterialTheme.typography.bodyMedium
)
} else {
Icon(
imageVector = Icons.Default.Mic,
contentDescription = "Microphone",
modifier = Modifier.size(80.dp),
tint = MaterialTheme.colorScheme.primary
)

Text(
text = "Ready to Record",
style = MaterialTheme.typography.headlineSmall
)

Text(
text = "WAV 48kHz with Voice Activity Detection",
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
}

Spacer(modifier = Modifier.height(32.dp))
}

uiState.error?.let { error ->
Box(
modifier = Modifier
.align(Alignment.BottomCenter)
.padding(bottom = 100.dp)
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
