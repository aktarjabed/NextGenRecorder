package com.aktarjabed.nextgenrecorder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aktarjabed.nextgenrecorder.data.repository.SettingsRepository
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
val settingsRepository: SettingsRepository = koinViewModel()
val analyticsEnabled by settingsRepository.getAnalyticsEnabled().collectAsState(initial = false)

var videoQuality by remember { mutableStateOf("UHD") }
var audioFormat by remember { mutableStateOf("WAV") }
var enableVad by remember { mutableStateOf(true) }

Scaffold(
topBar = {
CenterAlignedTopAppBar(
title = {
Text(
text = "Settings",
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
text = "Recording Settings",
style = MaterialTheme.typography.titleLarge,
fontWeight = FontWeight.Bold
)

// Video Settings
Text(
text = "Video",
style = MaterialTheme.typography.titleMedium,
fontWeight = FontWeight.SemiBold
)

SettingsOption(
title = "Video Quality",
description = "Select recording resolution",
value = videoQuality,
onValueChange = { videoQuality = it }
)

Spacer(modifier = Modifier.height(8.dp))

// Audio Settings
Text(
text = "Audio",
style = MaterialTheme.typography.titleMedium,
fontWeight = FontWeight.SemiBold
)

SettingsOption(
title = "Audio Format",
description = "WAV for quality, AAC for size",
value = audioFormat,
onValueChange = { audioFormat = it }
)

SettingsToggle(
title = "Voice Activity Detection",
description = "Only record when speech is detected",
checked = enableVad,
onCheckedChange = { enableVad = it }
)

Spacer(modifier = Modifier.height(8.dp))

// Privacy Settings
Text(
text = "Privacy",
style = MaterialTheme.typography.titleMedium,
fontWeight = FontWeight.SemiBold
)

SettingsToggle(
title = "Analytics",
description = "Help improve the app with anonymous usage data",
checked = analyticsEnabled,
onCheckedChange = { enabled ->
// settingsRepository.setAnalyticsEnabled(enabled)
}
)

Spacer(modifier = Modifier.height(24.dp))

// App Info
Text(
text = "App Information",
style = MaterialTheme.typography.titleMedium,
fontWeight = FontWeight.SemiBold
)

Text(
text = "NextGenRecorder v1.0.0",
style = MaterialTheme.typography.bodyMedium
)

Text(
text = "Professional recording with on-device AI",
style = MaterialTheme.typography.bodySmall,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
}
}
}

@Composable
fun SettingsOption(
title: String,
description: String,
value: String,
onValueChange: (String) -> Unit
) {
// Implementation for dropdown settings option
// For now, just display the values
Column {
Text(text = title, style = MaterialTheme.typography.bodyLarge)
Text(
text = "$description: $value",
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
}
}

@Composable
fun SettingsToggle(
title: String,
description: String,
checked: Boolean,
onCheckedChange: (Boolean) -> Unit
) {
androidx.compose.material3.ListItem(
headlineContent = {
Text(text = title, style = MaterialTheme.typography.bodyLarge)
},
supportingContent = {
Text(
text = description,
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurfaceVariant
)
},
trailingContent = {
Switch(
checked = checked,
onCheckedChange = onCheckedChange
)
}
)
}
