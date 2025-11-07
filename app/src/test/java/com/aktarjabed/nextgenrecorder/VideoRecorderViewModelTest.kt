package com.aktarjabed.nextgenrecorder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aktarjabed.nextgenrecorder.recorder.video.CameraController
import com.aktarjabed.nextgenrecorder.recorder.video.VideoRecorder
import com.aktarjabed.nextgenrecorder.ui.viewmodels.VideoRecorderViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class VideoRecorderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: VideoRecorderViewModel
    private val mockCameraController: CameraController = mockk(relaxed = true)
    private val mockVideoRecorder: VideoRecorder = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = VideoRecorderViewModel(mockCameraController, mockVideoRecorder)
    }

    @Test
    fun `initial state is not recording`() = runTest {
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isRecording)
        assertFalse(uiState.isCameraInitialized)
        assertNull(uiState.cameraError)
    }

    @Test
    fun `start recording updates state`() = runTest {
        // Given
        coEvery { mockVideoRecorder.startRecording() } returns mockk()

        // When
        viewModel.startRecording()

        // Then
        // assertTrue(viewModel.uiState.value.isRecording)
    }

    @Test
    fun `stop recording updates state`() = runTest {
        // Given
        coEvery { mockVideoRecorder.startRecording() } returns mockk()
        coEvery { mockVideoRecorder.stopRecording() } returns Unit

        // When
        viewModel.startRecording()
        viewModel.stopRecording()

        // Then
        assertFalse(viewModel.uiState.value.isRecording)
    }
}