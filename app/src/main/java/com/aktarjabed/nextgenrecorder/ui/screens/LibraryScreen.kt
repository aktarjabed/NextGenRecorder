package com.aktarjabed.nextgenrecorder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(onNavigateBack: () -> Unit) {
// Mock data - replace with actual repository call
val recordings = listOf(
Recording(
title = "Team Meeting",
duration = "15:30",
fileSize = "45.2 MB",
createdAt = LocalDateTime.now().minusHours(2),
type = RecordingType.VIDEO
),
Recording(
title = "Client Interview",
duration = "32:15",
fileSize = "128.7 MB",
createdAt = LocalDateTime.now().minusDays(1),
type = RecordingType.VIDEO
),
Recording(
title = "Audio Note",
duration = "05:45",
fileSize = "12.3 MB",
createdAt = LocalDateTime.now().minusHours(5),
type = RecordingType.AUDIO
)
)

Scaffold(
topBar = {
CenterAlignedTopAppBar(
title = {
Text(
text = "Library",
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
}
) { innerPadding ->
if (recordings.isEmpty()) {
Column(
modifier = Modifier
.fillMaxSize()
.padding(innerPadding)
.padding(32.dp),
horizontalAlignment = Alignment.CenterHorizontally,
verticalArrangement = Arrangement.Center
) {
Text(
text = "No Recordings Yet",
style = MaterialTheme.typography.headlineSmall
)
Spacer(modifier = Modifier.height(8.dp))
Text(
text = "Your recordings will appear here",
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
}
} else {
LazyColumn(
modifier = Modifier
.fillMaxSize()
.padding(innerPadding)
.padding(16.dp),
verticalArrangement = Arrangement.spacedBy(8.dp)
) {
items(recordings) { recording ->
RecordingItem(recording = recording)
}
}
}
}
}

@Composable
fun RecordingItem(recording: Recording) {
Card(
onClick = { /* Play recording */ }
) {
ListItem(
headlineContent = {
Text(
text = recording.title,
fontWeight = FontWeight.Medium
)
},
supportingContent = {
Column {
Text("Duration: ${recording.duration} • Size: ${recording.fileSize}")
Text(
recording.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
}
},
leadingContent = {
Icon(
imageVector = when (recording.type) {
RecordingType.VIDEO -> Icons.Default.VideoFile
RecordingType.AUDIO -> Icons.Default.AudioFile
else -> Icons.Default.PlayArrow
},
contentDescription = null,
tint = MaterialTheme.colorScheme.primary
)
}
)
}
}

data class Recording(
val title: String,
val duration: String,
val fileSize: String,
val createdAt: LocalDateTime,
val type: RecordingType
)

enum class RecordingType {
VIDEO, AUDIO, MEETING
}
