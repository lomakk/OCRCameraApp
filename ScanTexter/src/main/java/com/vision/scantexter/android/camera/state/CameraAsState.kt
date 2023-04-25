package com.vision.scantexter.android.camera.state

import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import net.taghub.presentation.compose.camera.state.*

/**
 * Camera State from [CameraPreview] Composable.
 * */
@Composable
fun rememberCameraState(): CameraState {
    val context = LocalContext.current
    return remember { CameraState(context) }
}

/**
 * Camera selector's State to [CameraPreview] Composable.
 * */
@Composable
fun rememberCamSelector(
    selector: CamSelector = CamSelector.Back
): MutableState<CamSelector> = rememberSaveable(saver = CamSelector.Saver) {
    mutableStateOf(selector)
}

/**
 * Flash mode's State to [CameraPreview] Composable.
 * */
@Composable
fun CameraState.rememberFlashMode(
    initialFlashMode: FlashMode = FlashMode.Off,
    useSaver: Boolean = true
): MutableState<FlashMode> = rememberConditionalState(
    initialValue = initialFlashMode,
    defaultValue = FlashMode.Off,
    useSaver = useSaver,
    predicate = hasFlashUnit
)

/**
 * Torch's State to [CameraPreview] Composable.
 * */
@Composable
fun CameraState.rememberTorch(
    initialTorch: Boolean = false,
    useSaver: Boolean = true
): MutableState<Boolean> = rememberConditionalState(
    initialValue = initialTorch,
    defaultValue = false,
    useSaver = useSaver,
    predicate = hasFlashUnit
)

/**
 * Create instance remember of ImageAnalyzer.
 *
 * @see ImageAnalyzer
 * */
@Composable
fun CameraState.rememberImageAnalyzer(
    imageAnalysisBackpressureStrategy: ImageAnalysisBackpressureStrategy = ImageAnalysisBackpressureStrategy.KeepOnlyLatest,
    imageAnalysisTargetSize: ImageAnalysisTargetSize? = ImageAnalysisTargetSize(this.imageAnalysisTargetSize),
    imageAnalysisImageQueueDepth: Int = this.imageAnalysisImageQueueDepth,
    analyze: (ImageProxy) -> Unit,
): ImageAnalyzer = remember(this) {
    ImageAnalyzer(
        this,
        imageAnalysisBackpressureStrategy,
        imageAnalysisTargetSize,
        imageAnalysisImageQueueDepth,
        analyze
    )
}
