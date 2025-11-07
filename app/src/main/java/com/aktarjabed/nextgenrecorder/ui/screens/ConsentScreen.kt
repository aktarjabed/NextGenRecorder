package com.aktarjabed.nextgenrecorder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aktarjabed.nextgenrecorder.domain.model.ConsentMeta
import com.aktarjabed.nextgenrecorder.domain.model.MeetingPreset
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentScreen(
onNavigateBack: () -> Unit,
onStartRecording: (ConsentMeta) -> Unit
) {
var meetingTitle by remember { mutableStateOf("") }
var participants by remember { mutableStateOf("") }
var selectedPreset by remember { mutableStateOf(MeetingPreset.INTERVIEW) }
var consentGiven by remember { mutableStateOf(false) }
var isPresetExpanded by remember { mutableStateOf(false) }

Scaffold(
topBar = {
CenterAlignedTopAppBar(
title = {
Text(
text = "Meeting Consent",
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
Column(
modifier = Modifier
.fillMaxSize()
.padding(innerPadding)
.padding(16.dp)
.verticalScroll(rememberScrollState()),
verticalArrangement = Arrangement.spacedBy(16.dp)
) {
Text(
text = "Meeting Recording Consent",
style = MaterialTheme.typography.headlineSmall
)

Text(
text = "Please provide meeting details and obtain consent from all participants before recording.",
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurfaceVariant
)

OutlinedTextField(
value = meetingTitle,
onValueChange = { meetingTitle = it },
label = { Text("Meeting Title") },
modifier = Modifier.fillMaxWidth()
)

OutlinedTextField(
value = participants,
onValueChange = { participants = it },
label = { Text("Participants (comma separated)") },
modifier = Modifier.fillMaxWidth()
)

// Preset Selection
ExposedDropdownMenuBox(
expanded = isPresetExpanded,
onExpandedChange = { isPresetExpanded = !isPresetExpanded }
) {
OutlinedTextField(
value = selectedPreset.name,
onValueChange = {},
readOnly = true,
label = { Text("Meeting Preset") },
trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPresetExpanded) },
modifier = Modifier
.fillMaxWidth()
.menuAnchor()
)

ExposedDropdownMenu(
expanded = isPresetExpanded,
onDismissRequest = { isPresetExpanded = false }
) {
MeetingPreset.values().forEach { preset ->
DropdownMenuItem(
text = { Text(preset.name) },
onClick = {
selectedPreset = preset
isPresetExpanded = false
}
)
}
}
}

Spacer(modifier = Modifier.height(16.dp))

// Consent Agreement
Column(
modifier = Modifier.fillMaxWidth(),
verticalArrangement = Arrangement.spacedBy(8.dp)
) {
Text(
text = "Consent Agreement",
style = MaterialTheme.typography.titleMedium,
fontWeight = FontWeight.SemiBold
)

Text(
text = "By checking this box, I confirm that:\n" +
"• All meeting participants have been informed about this recording\n" +
"• All participants consent to being recorded\n" +
"• The recording will be used only for its intended purpose",
style = MaterialTheme.typography.bodyMedium
)

Column(
modifier = Modifier.fillMaxWidth(),
horizontalAlignment = Alignment.Start
) {
Checkbox(
checked = consentGiven,
onCheckedChange = { consentGiven = it }
)
Text(
text = "I confirm all participants consent to recording",
style = MaterialTheme.typography.bodyMedium
)
}
}

Spacer(modifier = Modifier.height(24.dp))

Button(
onClick = {
val consentMeta = ConsentMeta(
meetingId = UUID.randomUUID().toString(),
title = meetingTitle.ifBlank { "Untitled Meeting" },
participants = participants.split(",").map { it.trim() }.filter { it.isNotEmpty() },
consentTimestamp = System.currentTimeMillis(),
preset = selectedPreset
)
onStartRecording(consentMeta)
},
enabled = consentGiven && meetingTitle.isNotBlank(),
modifier = Modifier
.fillMaxWidth()
.padding(vertical = 8.dp)
) {
Text("Start Recording with Consent")
}
}
}
}
