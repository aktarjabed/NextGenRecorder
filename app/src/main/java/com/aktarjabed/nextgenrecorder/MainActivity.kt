package com.aktarjabed.nextgenrecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.aktarjabed.nextgenrecorder.ui.App
import com.aktarjabed.nextgenrecorder.ui.theme.NextGenRecorderTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
enableEdgeToEdge()

setContent {
NextGenRecorderApp()
}
}
}

@Composable
fun NextGenRecorderApp() {
val systemUiController = rememberSystemUiController()
val darkTheme = isSystemInDarkTheme()
val context = LocalContext.current

DisposableEffect(systemUiController, darkTheme) {
systemUiController.setSystemBarsColor(
color = Color.Transparent,
darkIcons = !darkTheme
)
onDispose {}
}

NextGenRecorderTheme(darkTheme = darkTheme) {
App()
}
}
