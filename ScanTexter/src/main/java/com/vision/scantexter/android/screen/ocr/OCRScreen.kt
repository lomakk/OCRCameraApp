package com.vision.scantexter.android.screen.ocr

import android.Manifest
import android.app.Activity
import android.graphics.RectF
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.rotationMatrix
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import com.vision.scantexter.android.R
import com.vision.scantexter.android.camera.CameraPreview
import com.vision.scantexter.android.camera.state.*
import com.vision.scantexter.android.permission.RequestPermission
import com.vision.scantexter.android.screen.components.*
import com.vision.scantexter.android.theme.toAndroidGraphicsColor
import com.vision.scantexter.android.tools.createTempPhotoFile
import kotlinx.coroutines.launch
import net.taghub.presentation.compose.camera.state.CamSelector
import org.koin.androidx.compose.get
import java.io.File

@Composable
fun OCRScreen(
    modifier: Modifier = Modifier,
    viewModel: OCRViewModel = get(),
    showOCRState: MutableState<Boolean>,
    onCancel: () -> Unit = {},
    onCopyText: (String) -> Unit
) {
    OCRScreenView(
        modifier = modifier,
        viewModel = viewModel,
        showCameraState = showOCRState,
        onCancel = onCancel,
        onCopy = { text ->
            showOCRState.value = false
            onCopyText(text)
        }
    )
}

@Composable
fun OCRScreenView(
    modifier: Modifier = Modifier,
    viewModel: OCRViewModel,
    showCameraState: MutableState<Boolean>,
    onCopy: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    BackHandler(enabled = showCameraState.value && viewModel.uiState.collectAsState().value !is CameraOCRUiState.Captured) {
        onCancel()
        (context as Activity).finish()
    }

    BackHandler(enabled = viewModel.uiState.collectAsState().value is CameraOCRUiState.Captured) {
        viewModel.initCamera()
    }

    AnimatedVisibility(
        modifier = modifier,
        enter = fadeIn() + slideInHorizontally(),
        exit = fadeOut(),
        visible = showCameraState.value
    ) {
        RequestPermission(
            permissions = listOf(Manifest.permission.CAMERA),
            messageResId = R.string.camera_permission_text,
            permissionDenied = {
                showCameraState.value = false
            }
        ) {
            val uiState by viewModel.uiState.collectAsState()
            when (val result: CameraOCRUiState = uiState) {
                is CameraOCRUiState.Ready -> {
                    val cameraState = rememberCameraState()
                    val coroutineScope = rememberCoroutineScope()
                    val isDisableUi = remember { mutableStateOf(false) }
                    CameraOCRSection(
                        cameraState = cameraState,
                        useFrontCamera = false,
                        usePinchToZoom = true,
                        useTapToFocus = true,
                        isDisableUi = isDisableUi.value,
                        onTakePicture = {
                            isDisableUi.value = true
                            coroutineScope.launch {
                                viewModel.extractText(
                                    cameraState = cameraState,
                                    tempFile = createTempPhotoFile(
                                        context = context
                                    )
                                )
                            }
                        }
                    )
                    LaunchedEffect(result.throwable) {
                        isDisableUi.value = false
                        if (result.throwable != null) {
                            Toast.makeText(context, result.throwable.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                is CameraOCRUiState.Captured -> {
                    CameraOCRScreen(
                        viewModel = viewModel,
                        file = result.imageFile,
                        onCancel = { viewModel.initCamera() },
                        onCopy = onCopy
                    )
                }
                else -> Unit
            }
        }
    }
}

val BottomSheetOcrPeekHeight = 130.dp

@Composable
fun CameraOCRScreen(
    modifier: Modifier = Modifier,
    viewModel: OCRViewModel,
    file: File,
    onCancel: () -> Unit,
    onCopy: (String) -> Unit
) {
    val painter = rememberAsyncImagePainter(
        model = file,
        imageLoader = LocalContext.current.imageLoader
            .newBuilder()
            .crossfade(true)
            .build()
    )

    val sheetState = remember { mutableStateOf(true) }
    val selectedText = viewModel.ocrState.collectAsState().value.selectedText

    BottomSheetScalfordBase(
        modifier = modifier,
        startPeekHeight = BottomSheetOcrPeekHeight,
        sheetExpandedState = sheetState,
        sheetContent = { openSheet, closeSheet, currentFraction ->
            OCRBottomSheet(
                currentFraction = currentFraction,
                isTextEmpty = selectedText.text.isEmpty(),
                selectedText = selectedText,
                onSelectAll = { viewModel.handleAll(isClear = false) },
                onClear = { viewModel.handleAll(isClear = true) },
                onCopy = { onCopy(selectedText.text) },
                onCancel = onCancel,
                onUpdateSelectedText = { viewModel.updateSelectedText(value = it) },
                openSheet = openSheet
            )
        }, content = { openSheet, closeSheet, currentFraction ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painter,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null
                )

                val haptic = LocalHapticFeedback.current
                val selectorPosition: MutableState<Offset?> = remember { mutableStateOf(null) }
                SelectionContainer {  }

                viewModel.ocrState.collectAsState().value.blocks.forEach { textBlock ->
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { tapOffset ->
                                        viewModel.handleTap(tapOffset = tapOffset) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            selectorPosition.value = Offset(
                                                it.bottomRightCoordinate.x.toFloat(),
                                                it.bottomRightCoordinate.y.toFloat()
                                            )
                                        }
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = {
                                    },
                                    onDragEnd = {
                                    }
                                ) { change, dragAmount ->
                                    viewModel.handleDrag(
                                        dragOffset = change.position,
                                        dragAmount = dragAmount,
                                        onLineAdded = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    )
                                }
                            }
                    ) {
                        textBlock.lines.forEach { textLine ->
                            drawPath(textLine.path, textLine.getLineColor())
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CameraOCRSection(
    modifier: Modifier = Modifier,
    cameraState: CameraState,
    useFrontCamera: Boolean,
    usePinchToZoom: Boolean,
    useTapToFocus: Boolean,
    onTakePicture: () -> Unit,
    isDisableUi: Boolean
) {
    var flashMode by cameraState.rememberFlashMode()
    var camSelector by rememberCamSelector(if (useFrontCamera) CamSelector.Front else CamSelector.Back)
    var zoomRatio by rememberSaveable { mutableStateOf(cameraState.minZoom) }
    var zoomHasChanged by rememberSaveable { mutableStateOf(false) }
    val hasFlashUnit by rememberUpdatedState(cameraState.hasFlashUnit)
    var cameraOption by rememberSaveable { mutableStateOf(CameraOption.TextRecognition) }
    var enableTorch by cameraState.rememberTorch(initialTorch = false)

    CameraPreview(
        modifier = modifier,
        cameraState = cameraState,
        camSelector = camSelector,
        captureMode = cameraOption.toCaptureMode(),
        enableTorch = enableTorch,
        flashMode = flashMode,
        zoomRatio = zoomRatio,
        isPinchToZoomEnabled = usePinchToZoom,
        isFocusOnTapEnabled = useTapToFocus,
        onZoomRatioChanged = {
            zoomHasChanged = true
            zoomRatio = it
        },
        onSwitchToFront = { bitmap ->
            Image(bitmap.asImageBitmap(), contentDescription = null)
        },
        onSwitchToBack = { bitmap ->
            Image(bitmap.asImageBitmap(), contentDescription = null)
        }
    ) {
        AnimatedVisibility(
            visible = !isDisableUi,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CameraOCRInnerContent(
                modifier = Modifier.fillMaxSize().clickable(enabled = isDisableUi) {},
                zoomHasChanged = zoomHasChanged,
                zoomRatio = zoomRatio,
                flashMode = flashMode.toFlash(enableTorch),
                cameraOption = cameraOption,
                hasFlashUnit = hasFlashUnit,
                onFlashModeChanged = { flash ->
                    enableTorch = flash == Flash.Always
                    flashMode = flash.toFlashMode()
                },
                onZoomFinish = { zoomHasChanged = false },
                onTakePicture = onTakePicture,
                onSwitchCamera = {
                    if (cameraState.isStreaming) {
                        camSelector = camSelector.inverse
                    }
                }
            )
        }
    }
}

@Composable
fun CameraOCRInnerContent(
    modifier: Modifier = Modifier,
    zoomHasChanged: Boolean,
    zoomRatio: Float,
    flashMode: Flash,
    cameraOption: CameraOption,
    hasFlashUnit: Boolean,
    onFlashModeChanged: (Flash) -> Unit,
    onZoomFinish: () -> Unit,
    onTakePicture: () -> Unit,
    onSwitchCamera: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SettingsBox(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 8.dp, start = 24.dp, end = 24.dp),
            flashMode = flashMode,
            hasFlashUnit = hasFlashUnit,
            onFlashModeChanged = onFlashModeChanged,
            zoomRatio = zoomRatio,
            zoomHasChanged = zoomHasChanged,
            onZoomFinish = onZoomFinish,
        )
        Column(
            modifier = Modifier.fillMaxWidth().noClickable()
                .padding(bottom = 32.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OCRTopText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.camera_take_photo_ocr_message)
            )
            ActionBox(
                modifier = Modifier.fillMaxWidth().noClickable(),
                cameraOption = cameraOption,
                onTakePicture = onTakePicture,
                onSwitchCamera = onSwitchCamera,
                takePictureButtonContent = {
                    Icon(
                        modifier = Modifier.align(Alignment.Center).size(32.dp),
                        imageVector = Icons.Filled.Article,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

fun Modifier.noClickable() = then(Modifier.clickable(enabled = false) {})


