package com.vision.scantexter.android.camera.state

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.CameraController.OutputSize
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.vision.scantexter.android.camera.extensions.compatMainExecutor
import com.vision.scantexter.android.model.mapToUi
import net.taghub.presentation.compose.camera.state.CamSelector
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.concurrent.Executor


/**
 * A state object that can be hoisted to control camera, take picture or record video.
 *
 * To be created use [rememberCameraState].
 * */
class CameraState internal constructor(private val context: Context) {

    /**
     * Main Executor to action as take picture or record.
     * */
    private val mainExecutor: Executor = context.compatMainExecutor

    /**
     * Content resolver to picture and video.
     * */
    private val contentResolver: ContentResolver = context.contentResolver

    /**
     * Check if focus metering is supported
     * */
    private val MeteringPoint.isFocusMeteringSupported: Boolean
        get() = controller.cameraInfo?.isFocusMeteringSupported(
            FocusMeteringAction.Builder(this).build()
        ) ?: false

    /**
     * Main controller from CameraX. useful in cases that haven't been release some feature yet.
     * */
    internal val controller: LifecycleCameraController = LifecycleCameraController(context)

    /**
     * Camera preview width.
     * */
    var cameraPreviewWidth: Int = 0
        set(value) {
            if (value != 0) {
                field = value
            }
        }

    /**
     * Get camera preview height.
     * */
    var cameraPreviewHeight: Int = 0
        set(value) {
            if (value != 0) {
                field = value
            }
        }


    /**
     * Get max zoom from camera.
     * */
    var maxZoom: Float by mutableStateOf(
        controller.zoomState.value?.maxZoomRatio ?: INITIAL_ZOOM_VALUE
    )
        internal set

    /**
     * Get min zoom from camera.
     * */
    var minZoom: Float by mutableStateOf(
        controller.zoomState.value?.minZoomRatio ?: INITIAL_ZOOM_VALUE
    )
        internal set

    /**
     * Check if camera is streaming or not.
     * */
    var isStreaming: Boolean by mutableStateOf(false)
        internal set

    /**
     * Check if zoom is supported.
     * */
    val isZoomSupported: Boolean by derivedStateOf { maxZoom != 1F }

    /**
     * Check if focus on tap supported
     * */
    var isFocusOnTapSupported: Boolean by mutableStateOf(true)

    /**
     * Check if camera state is initialized or not.
     * */
    var isInitialized: Boolean by mutableStateOf(false)
        internal set

    /**
     * Verify if camera has flash or not.
     * */
    var hasFlashUnit: Boolean by mutableStateOf(
        controller.cameraInfo?.hasFlashUnit() ?: true
    )

    /**
     * Capture mode to be added on camera.
     * */
    internal var captureMode: CaptureMode = CaptureMode.Image
        set(value) {
            if (field != value) {
                field = value
                updateUseCases()
            }
        }

    /**
     * Get scale type from the camera.
     * */
    internal var scaleType: ScaleType = ScaleType.FillCenter

    /**
     * Get implementation mode from the camera.
     * */
    internal var implementationMode: ImplementationMode = ImplementationMode.Performance

    /**
     * Camera mode, it can be front or back.
     * @see CamSelector
     * */
    internal var camSelector: CamSelector = CamSelector.Back
        set(value) {
            when {
                value == field -> Unit
                !isRecording && hasCamera(value) -> {
                    if (controller.cameraSelector != value.selector) {
                        controller.cameraSelector = value.selector
                        field = value
                        resetCamera()
                    }
                }

                isRecording -> Timber.e("Device is recording, switch camera is unavailable")
                else -> Timber.e("Device does not have ${value.selector} camera")
            }
        }

    /**
     * Get Image Analyzer from camera.
     * */
    internal var imageAnalyzer: ImageAnalysis.Analyzer? = null
        set(value) {
            field = value
            with(controller) {
                clearImageAnalysisAnalyzer()
                setImageAnalysisAnalyzer(mainExecutor, value ?: return)
            }
        }

    /**
     * CameraX's use cases captures.
     * */
    private val useCases: MutableSet<Int> = mutableSetOf(IMAGE_ANALYSIS)

    /**
     * Enable/Disable Image analysis from the camera.
     * */
    internal var isImageAnalysisEnabled: Boolean = true
        set(value) {
            if (value != field) {
                if (value) useCases += IMAGE_ANALYSIS else useCases -= IMAGE_ANALYSIS
                updateUseCases()
                field = value
            }
        }

    private fun updateUseCases() {
        controller.setEnabledUseCases(0)
        val useCases = when (captureMode) {
            CaptureMode.Video -> captureMode.value
            CaptureMode.Image -> useCases.sumOr(captureMode.value)
        }
        controller.setEnabledUseCases(useCases)
    }

    /**
     * Image analysis backpressure strategy, use [rememberImageAnalyzer] to set value.
     * */
    internal var imageAnalysisBackpressureStrategy: Int
        get() = controller.imageAnalysisBackpressureStrategy
        set(value) {
            if (imageAnalysisBackpressureStrategy != value) {
                controller.imageAnalysisBackpressureStrategy = value
            }
        }

    /**
     * Image analysis target size, use [rememberImageAnalyzer] to set value.
     * @see rememberImageAnalyzer
     * */
    internal var imageAnalysisTargetSize: OutputSize?
        get() = controller.imageAnalysisTargetSize
        set(value) {
            if (imageAnalysisTargetSize != value) {
                controller.imageAnalysisTargetSize = value
            }
        }

    /**
     * Image analysis image queue depth, use [rememberImageAnalyzer] to set value.
     * @see rememberImageAnalyzer
     * */
    internal var imageAnalysisImageQueueDepth: Int
        get() = controller.imageAnalysisImageQueueDepth
        set(value) {
            if (imageAnalysisImageQueueDepth != value) {
                controller.imageAnalysisImageQueueDepth = value
            }
        }


    /**
     * Get if focus on tap is enabled from cameraX.
     * */
    internal var isFocusOnTapEnabled: Boolean
        get() = controller.isTapToFocusEnabled
        set(value) {
            controller.isTapToFocusEnabled = value
        }

    /**
     * Flash Mode from the camera.
     * @see FlashMode
     * */
    internal var flashMode: FlashMode
        get() = FlashMode.find(controller.imageCaptureFlashMode)
        set(value) {
            if (hasFlashUnit && flashMode != value) {
                controller.imageCaptureFlashMode = value.mode
            }
        }

    /**
     * Enabled/Disable torch from camera.
     * */
    internal var enableTorch: Boolean
        get() = controller.torchState.value == TorchState.ON
        set(value) {
            if (enableTorch != value) {
                controller.enableTorch(hasFlashUnit && value)
            }
        }

    /**
     * Return true if it's recording.
     * */
    var isRecording: Boolean by mutableStateOf(controller.isRecording)
        private set

    init {
        controller.initializationFuture.addListener({
            startZoom()
            isInitialized = true
        }, mainExecutor)
    }

    /**
     *  Take a picture with the camera.
     *
     *  @param saveCollection Uri collection where the photo will be saved.
     *  @param contentValues Content values of the photo.
     *  @param onResult Callback called when [ImageCaptureResult] is ready
     * */
    fun takePicture(
        contentValues: ContentValues,
        saveCollection: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        onResult: (ImageCaptureResult) -> Unit,
    ) {
        takePicture(
            outputFileOptions = ImageCapture.OutputFileOptions.Builder(
                contentResolver, saveCollection, contentValues
            ).build(), onResult = onResult
        )
    }

    /**
     * Take a picture with the camera.
     * @param file file where the photo will be saved
     * @param onResult Callback called when [ImageCaptureResult] is ready
     * */
    fun takePicture(
        file: File, onResult: (ImageCaptureResult) -> Unit
    ) {
        takePicture(ImageCapture.OutputFileOptions.Builder(file).build(), onResult)
    }

    /**
     * Take a picture with the camera.
     *
     * @param outputFileOptions Output file options of the photo.
     * @param onResult Callback called when [ImageCaptureResult] is ready
     * */
    fun takePicture(
        outputFileOptions: ImageCapture.OutputFileOptions,
        onResult: (ImageCaptureResult) -> Unit,
    ) {
        try {
            controller.takePicture(outputFileOptions,
                mainExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val uri = outputFileResults.savedUri
                        if (uri != null && uri.path != null) {
                            uri.path?.let { path ->
                                onResult(ImageCaptureResult.Success(File(path)))
                            }
                        } else {
                            onResult(ImageCaptureResult.Error(IllegalStateException("File does not exist")))
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        onResult(ImageCaptureResult.Error(exception))
                    }
                })
        } catch (exception: Exception) {
            onResult(ImageCaptureResult.Error(exception))
        }
    }

    /**
     * Take a picture with the camera.
     * @param file file where the photo will be saved
     * @param onResult Callback called when [ImageExtractTextResult] is ready
     * */
    fun extractTextFromFile(
        file: File, onResult: (ImageExtractTextResult) -> Unit
    ) {
        extractTextFromFile(ImageCapture.OutputFileOptions.Builder(file).build(), onResult)
    }

    /**
     * Take a picture with the camera and extract texts.
     *
     * @param outputFileOptions Output file options of the photo.
     * @param onResult Callback called when [ImageExtractTextResult] is ready
     * */
    fun extractTextFromFile(
        outputFileOptions: ImageCapture.OutputFileOptions,
        onResult: (ImageExtractTextResult) -> Unit
    ) {
        try {
            controller.takePicture(outputFileOptions,
                mainExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val uri = outputFileResults.savedUri
                        val path = uri?.path
                        if (uri != null && path != null) {
                            val file = File(path)
                            onResult(ImageExtractTextResult.SuccessFile(savedFile = file))
                            recognizeText(uri = uri, onResult = onResult)
                        } else {
                            onResult(ImageExtractTextResult.Error(IllegalStateException("File does not exist")))
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        onResult(ImageExtractTextResult.Error(exception))
                    }
                })
        } catch (exception: Exception) {
            onResult(ImageExtractTextResult.Error(exception))
        }
    }

    /**
     * Recognize text from the given file uri
     *
     * @param uri captured file uri
     * @param onResult Callback called when [ImageExtractTextResult] is ready
     * @receiver
     */
    private fun recognizeText(
        uri: Uri,
        onResult: (ImageExtractTextResult) -> Unit
    ) {
        uri.path?.let { path ->
            // Set image rotation to 90 degree always
            val newExif = ExifInterface(path)
            newExif.setAttribute(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_ROTATE_90.toString()
            )
            newExif.saveAttributes()

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            try {
                val image = InputImage.fromFilePath(context, uri)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        onResult(
                            ImageExtractTextResult.SuccessText(
                                text = visionText.mapToUi(
                                    scaleFactorX = if (cameraPreviewWidth == 0) {
                                        1f
                                    } else {
                                        image.width / cameraPreviewWidth.toFloat()
                                    },
                                    scaleFactorY = if (cameraPreviewHeight == 0) {
                                        1f
                                    } else {
                                        image.height / cameraPreviewHeight.toFloat()
                                    }
                                )
                            )
                        )
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Set zoom ratio to camera.
     * @param zoomRatio zoomRatio to be added
     * */
    private fun setZoomRatio(zoomRatio: Float) {
        controller.setZoomRatio(zoomRatio.coerceIn(minZoom, maxZoom))
    }

    /**
     * Return if has camera selector or not, camera must be initialized, otherwise result is false.
     * */
    fun hasCamera(cameraSelector: CamSelector): Boolean =
        isInitialized && controller.hasCamera(cameraSelector.selector)

    private fun startZoom() {
        // Turn off is pinch to zoom and use manually
        controller.isPinchToZoomEnabled = false

        val zoom = controller.zoomState.value
        minZoom = zoom?.minZoomRatio ?: INITIAL_ZOOM_VALUE
        maxZoom = zoom?.maxZoomRatio ?: INITIAL_ZOOM_VALUE
    }

    private fun resetCamera() {
        hasFlashUnit = controller.cameraInfo?.hasFlashUnit() ?: false
        startZoom()
    }

    private fun Set<Int>.sumOr(initial: Int = 0): Int = fold(initial) { acc, current ->
        acc or current
    }

    /**
     * Update all values from camera state.
     * */
    internal fun update(
        camSelector: CamSelector,
        captureMode: CaptureMode,
        scaleType: ScaleType,
        isImageAnalysisEnabled: Boolean,
        imageAnalyzer: ImageAnalyzer?,
        implementationMode: ImplementationMode,
        isFocusOnTapEnabled: Boolean,
        flashMode: FlashMode,
        zoomRatio: Float,
        enableTorch: Boolean,
        meteringPoint: MeteringPoint,
        cameraPreviewWidth: Int,
        cameraPreviewHeight: Int
    ) {
        this.camSelector = camSelector
        this.captureMode = captureMode
        this.scaleType = scaleType
        this.isImageAnalysisEnabled = isImageAnalysisEnabled
        this.imageAnalyzer = imageAnalyzer?.analyzer
        this.implementationMode = implementationMode
        this.isFocusOnTapEnabled = isFocusOnTapEnabled
        this.flashMode = flashMode
        this.enableTorch = enableTorch
        this.isFocusOnTapSupported = meteringPoint.isFocusMeteringSupported
        this.cameraPreviewHeight = cameraPreviewHeight
        this.cameraPreviewWidth = cameraPreviewWidth
        setZoomRatio(zoomRatio)
    }

    private companion object {
        private const val INITIAL_ZOOM_VALUE = 1F
    }
}
