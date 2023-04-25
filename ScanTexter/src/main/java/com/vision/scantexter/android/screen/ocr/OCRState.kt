package com.vision.scantexter.android.screen.ocr

import androidx.compose.ui.text.input.TextFieldValue
import com.vision.scantexter.android.model.OCRBlock

/**
 * A state object that can be hoisted to control text recognition state.
 * */
data class OCRState internal constructor(
    val blocks: List<OCRBlock> = listOf(),
    val selectedText: TextFieldValue = TextFieldValue()
)