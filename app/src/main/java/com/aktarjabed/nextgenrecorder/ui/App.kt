package com.aktarjabed.nextgenrecorder.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aktarjabed.nextgenrecorder.ui.screens.DashboardScreen
import com.aktarjabed.nextgenrecorder.ui.screens.VideoRecorderScreen
import com.aktarjabed.nextgenrecorder.ui.screens.AudioRecorderScreen
import com.aktarjabed.nextgenrecorder.ui.screens.ConsentScreen
import com.aktarjabed.nextgenrecorder.ui.screens.LibraryScreen
import com.aktarjabed.nextgenrecorder.ui.screens.SettingsScreen

@Composable
fun App() {
val navController = rememberNavController()
val lifecycleOwner = LocalLifecycleOwner.current
val coroutineScope = rememberCoroutineScope()

// REMOVED: NextGenRecorderTheme wrapper - now only in MainActivity
NavHost(
navController = navController,
startDestination = "dashboard"
) {
composable("dashboard") {
DashboardScreen(
onNavigateToVideo = { navController.navigate("video") },
onNavigateToAudio = { navController.navigate("audio") },
onNavigateToConsent = { navController.navigate("consent") },
onNavigateToLibrary = { navController.navigate("library") },
onNavigateToSettings = { navController.navigate("settings") }
)
}

composable("video") {
VideoRecorderScreen(
onNavigateBack = { navController.popBackStack() }
)
}

composable("audio") {
AudioRecorderScreen(
onNavigateBack = { navController.popBackStack() }
)
}

composable("consent") {
ConsentScreen(
onNavigateBack = { navController.popBackStack() },
onStartRecording = { consentMeta ->
// Handle consent-based recording start
navController.navigate("video")
}
)
}

composable("library") {
LibraryScreen(
onNavigateBack = { navController.popBackStack() }
)
}

composable("settings") {
SettingsScreen(
onNavigateBack = { navController.popBackStack() }
)
}
}
}
