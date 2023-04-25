package com.vision.scantexter.android.screen.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BlindView(
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 6.dp,
    strokeWidth: Dp = 2.dp,
    color: Color = MaterialTheme.colors.primary,
    expandableFraction: Float = 0.0f // From 0.0 to 1.0
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
            .height(height)
            .width(width)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawLine(
            start = Offset(x = 0f, y = canvasHeight),
            end = Offset(
                x = canvasWidth / 2,
                y = canvasHeight - expandableFraction * canvasHeight
            ),
            color = color,
            strokeWidth = strokeWidth.toPx()
        )
        drawLine(
            start = Offset(
                x = canvasWidth / 2,
                y = canvasHeight - expandableFraction * canvasHeight
            ),
            end = Offset(x = canvasWidth, y = canvasHeight),
            color = color,
            strokeWidth = strokeWidth.toPx()
        )
    }
}