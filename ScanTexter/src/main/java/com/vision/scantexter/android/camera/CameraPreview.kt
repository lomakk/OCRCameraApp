package com.vision.scantexter.android.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.vision.scantexter.android.camera.extensions.clamped
import com.vision.scantexter.android.camera.extensions.onCameraTouchEvent
import com.vision.scantexter.android.camera.focus.FocusTap
import com.vision.scantexter.android.camera.focus.SquareCornerFocus
import com.vision.scantexter.android.camera.state.*
import kotlinx.coroutines.delay
import net.taghub.presentation.compose.camera.state.*
import androidx.camera.core.CameraSelector as CameraXSelector

/**
 * Creates a Camera Preview's composable.
 *
 * @param cameraState camera state hold some states and camera's controller, it can be useful to given action like [CameraState.takePicture]
 * @param camSelector camera selector to be added, default is back
 * @param captureMode camera capture mode, default is image
 * @param flashMode flash mode to be added, default is off
 * @param scaleType scale type to be added, default is fill center
 * @param enableTorch enable torch from camera, default is false.
 * @param zoomRatio zoom ratio to be added, default is 1.0
 * @param imageAnalyzer image analyzer from camera, see [ImageAnalyzer]
 * @param implementationMode implementation mode to be added, default is performance
 * @param isImageAnalysisEnabled enable or disable image analysis
 * @param isFocusOnTapEnabled turn on feature focus on tap if true
 * @param isPinchToZoomEnabled turn on feature pinch to zoom if true
 * @param onPreviewStreamChanged dispatch when preview is switching to front or back
 * @param onSwitchToFront composable preview when change camera to front and it's not been streaming yet
 * @param onSwitchToBack composable preview when change camera to back and it's not been streaming yet
 * @param onZoomRatioChanged dispatch when zoom is changed by pinch to zoom
 * @param focusTapContent content of focus tap, default is [SquareCornerFocus]
 * @param onFocus callback to use when on focus tap is triggered, call onComplete to [focusTapContent] gone.
 * @param content content composable within of camera preview.
 * @see ImageAnalyzer
 * @see CameraState
 * */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    latestBitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) },
    cameraState: CameraState = rememberCameraState(),
    camSelector: CamSelector = cameraState.camSelector,
    captureMode: CaptureMode = cameraState.captureMode,
    flashMode: FlashMode = cameraState.flashMode,
    scaleType: ScaleType = cameraState.scaleType,
    enableTorch: Boolean = cameraState.enableTorch,
    zoomRatio: Float = 1F,
    imageAnalyzer: ImageAnalyzer? = null,
    implementationMode: ImplementationMode = cameraState.implementationMode,
    isImageAnalysisEnabled: Boolean = cameraState.isImageAnalysisEnabled,
    isFocusOnTapEnabled: Boolean = cameraState.isFocusOnTapEnabled,
    isPinchToZoomEnabled: Boolean = cameraState.isZoomSupported,
    onPreviewStreamChanged: () -> Unit = {},
    onSwitchToFront: @Composable (Bitmap) -> Unit = {},
    onSwitchToBack: @Composable (Bitmap) -> Unit = {},
    onFocus: suspend (onComplete: () -> Unit) -> Unit = { onComplete ->
        delay(1000L)
        onComplete()
    },
    onZoomRatioChanged: (Float) -> Unit = {},
    focusTapContent: @Composable () -> Unit = { SquareCornerFocus() },
    content: @Composable () -> Unit = {},
) {
    CameraPreviewImpl(
        modifier = modifier,
        latestBitmap = latestBitmap,
        cameraState = cameraState,
        camSelector = camSelector,
        captureMode = captureMode,
        flashMode = flashMode,
        scaleType = scaleType,
        enableTorch = enableTorch,
        zoomRatio = zoomRatio,
        imageAnalyzer = imageAnalyzer,
        isImageAnalysisEnabled = isImageAnalysisEnabled,
        implementationMode = implementationMode,
        isFocusOnTapEnabled = isFocusOnTapEnabled,
        isPinchToZoomEnabled = isPinchToZoomEnabled,
        onZoomRatioChanged = onZoomRatioChanged,
        focusTapContent = focusTapContent,
        onFocus = onFocus,
        onPreviewStreamChanged = onPreviewStreamChanged,
        onSwipeToFront = onSwitchToFront,
        onSwipeToBack = onSwitchToBack,
        content = content
    )
}

@SuppressLint("RestrictedApi")
@Composable
internal fun CameraPreviewImpl(
    modifier: Modifier,
    latestBitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) },
    cameraState: CameraState,
    camSelector: CamSelector,
    captureMode: CaptureMode,
    flashMode: FlashMode,
    scaleType: ScaleType,
    enableTorch: Boolean,
    zoomRatio: Float,
    implementationMode: ImplementationMode,
    imageAnalyzer: ImageAnalyzer?,
    isImageAnalysisEnabled: Boolean,
    isFocusOnTapEnabled: Boolean,
    isPinchToZoomEnabled: Boolean,
    onZoomRatioChanged: (Float) -> Unit,
    onPreviewStreamChanged: () -> Unit,
    onFocus: suspend (() -> Unit) -> Unit,
    onSwipeToFront: @Composable (Bitmap) -> Unit,
    onSwipeToBack: @Composable (Bitmap) -> Unit,
    focusTapContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleEvent by lifecycleOwner.lifecycle.observeAsState()
    val cameraIsInitialized by rememberUpdatedState(cameraState.isInitialized)
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    val isCameraIdle by rememberUpdatedState(!cameraState.isStreaming)

    AndroidView(modifier = modifier, factory = { context ->
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            controller = cameraState.controller.apply {
                bindToLifecycle(lifecycleOwner)
            }
            previewStreamState.observe(lifecycleOwner) { state ->
                cameraState.isStreaming = state == PreviewView.StreamState.STREAMING
            }
        }
    }, update = { previewView ->
        if (cameraIsInitialized) {
            with(previewView) {
                this.scaleType = scaleType.type
                this.implementationMode = implementationMode.value
                onCameraTouchEvent(
                    onTap = { if (isFocusOnTapEnabled) tapOffset = it },
                    onScaleChanged = {
                        if (isPinchToZoomEnabled) {
                            val zoom = zoomRatio.clamped(it).coerceIn(
                                minimumValue = cameraState.minZoom,
                                maximumValue = cameraState.maxZoom
                            )
                            onZoomRatioChanged(zoom)
                        }
                    }
                )
                latestBitmap.value = when {
                    lifecycleEvent == Lifecycle.Event.ON_STOP -> null
                    !isCameraIdle && camSelector != cameraState.camSelector -> bitmap
                    !isCameraIdle -> bitmap
                    else -> latestBitmap.value
                }
                cameraState.update(
                    camSelector = camSelector,
                    captureMode = captureMode,
                    scaleType = scaleType,
                    isImageAnalysisEnabled = isImageAnalysisEnabled,
                    imageAnalyzer = imageAnalyzer,
                    implementationMode = implementationMode,
                    isFocusOnTapEnabled = isFocusOnTapEnabled,
                    flashMode = flashMode,
                    enableTorch = enableTorch,
                    zoomRatio = zoomRatio,
                    meteringPoint = meteringPointFactory.createPoint(x, y),
                    cameraPreviewHeight = previewView.height,
                    cameraPreviewWidth = previewView.width
                )
            }

        }
    })

    FocusTap(
        offset = tapOffset,
        onFocus = { onFocus { tapOffset = Offset.Zero } },
    ) { focusTapContent() }

    if (isCameraIdle) {
        latestBitmap.value?.let {
            when (camSelector.selector.lensFacing) {
                CameraXSelector.LENS_FACING_FRONT -> onSwipeToFront(it)
                CameraXSelector.LENS_FACING_BACK -> onSwipeToBack(it)
                else -> Unit
            }
            LaunchedEffect(latestBitmap) {
                onPreviewStreamChanged()
                if (latestBitmap.value != null) onZoomRatioChanged(cameraState.minZoom)
            }
        }
    }

    content()
}
