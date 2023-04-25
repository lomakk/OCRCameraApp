package com.vision.scantexter.android.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

val ColorTextRecognition = Color(0xFFFFFFFF)
val ColorTextSelection = Color(0xFF4286F4)

val ColorPrimaryDark = Color

fun Color.toAndroidGraphicsColor(): Int {
    return android.graphics.Color.argb(
        toArgb().alpha,
        toArgb().red,
        toArgb().green,
        toArgb().blue
    )
}
