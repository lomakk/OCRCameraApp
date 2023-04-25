package com.vision.scantexter.android.camera.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.vision.scantexter.android.R

fun Flash.toFlashMode() = when (this) {
    Flash.Auto -> FlashMode.Auto
    Flash.On -> FlashMode.On
    Flash.Off, Flash.Always -> FlashMode.Off
}

fun FlashMode.toFlash(isTorchEnabled: Boolean) = when (this) {
    FlashMode.On -> Flash.On
    FlashMode.Auto -> Flash.Auto
    FlashMode.Off -> Flash.Off
}.takeIf { !isTorchEnabled } ?: Flash.Always


enum class Flash(
    @DrawableRes val drawableRes: Int,
    @StringRes val contentRes: Int
) {
    Off(R.drawable.camera_flash_off, R.string.camera_flash_off),
    On(R.drawable.camera_flash_on, R.string.camera_flash_on),
    Auto(R.drawable.camera_flash_auto, R.string.camera_flash_auto),
    Always(R.drawable.camera_flash_always, R.string.camera_flash_always);
}
