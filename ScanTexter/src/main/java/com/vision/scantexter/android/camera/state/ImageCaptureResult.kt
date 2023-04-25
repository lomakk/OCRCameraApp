package com.vision.scantexter.android.camera.state

import androidx.compose.runtime.Immutable
import com.vision.scantexter.android.model.OCRText
import java.io.File

/**
 * Photo Result of taking picture.
 *
 * @see CameraState.takePicture
 * */
sealed interface ImageCaptureResult {
    @Immutable
    data class Success(val savedFile: File) : ImageCaptureResult

    @Immutable
    data class Error(val throwable: Throwable) : ImageCaptureResult
}

/**
 * Photo Result of extracting text with picture.
 *
 * @see CameraState.extractTextFromFile
 * */
sealed interface ImageExtractTextResult {
    @Immutable
    data class SuccessText(
        val text: OCRText
    ) : ImageExtractTextResult

    @Immutable
    data class SuccessFile(
        val savedFile: File
    ) : ImageExtractTextResult

    @Immutable
    data class Error(val throwable: Throwable) : ImageExtractTextResult
}
