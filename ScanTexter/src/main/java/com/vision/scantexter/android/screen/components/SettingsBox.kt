package com.vision.scantexter.android.screen.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vision.scantexter.android.camera.state.Flash
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun SettingsBox(
    modifier: Modifier = Modifier,
    flashMode: Flash,
    hasFlashUnit: Boolean,
    onFlashModeChanged: (Flash) -> Unit,
    zoomRatio: Float,
    zoomHasChanged: Boolean,
    onZoomFinish: () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedVisibility(
                modifier = Modifier.padding(top = 16.dp),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                visible = zoomHasChanged
            ) {
                Text(
                    text = "${zoomRatio.roundTo(1)}x",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        FlashBox(
            modifier = Modifier.align(Alignment.TopEnd),
            hasFlashUnit = hasFlashUnit,
            flashMode = flashMode,
            onFlashModeChanged = onFlashModeChanged
        )
    }
    LaunchedEffect(zoomRatio, zoomHasChanged) {
        delay(1_000)
        onZoomFinish()
    }
}

internal fun Float.roundTo(n: Int): Float {
    return try {
        "%.${n}f".format(Locale.US, this).toFloat()
    } catch (e: NumberFormatException) {
        this
    }
}