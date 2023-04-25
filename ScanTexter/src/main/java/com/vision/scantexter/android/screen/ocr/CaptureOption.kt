package com.vision.scantexter.android.screen.ocr

import androidx.annotation.StringRes
import com.vision.scantexter.android.R
import com.vision.scantexter.android.camera.state.CaptureMode

enum class CameraOption(@StringRes val titleRes: Int) {
    Photo(titleRes = R.string.camera_take_photo),
    TextRecognition(titleRes = R.string.camera_text_recognition);

    fun toCaptureMode(): CaptureMode = when (this) {
        Photo, TextRecognition -> CaptureMode.Image
    }
}