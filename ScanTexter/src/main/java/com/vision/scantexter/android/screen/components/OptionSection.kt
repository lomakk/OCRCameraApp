package com.vision.scantexter.android.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vision.scantexter.android.screen.ocr.CameraOption

@Composable
fun OptionSection(
    modifier: Modifier = Modifier,
    currentCameraOption: CameraOption
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp),
            text = stringResource(id = currentCameraOption.titleRes).replaceFirstChar { it.uppercase() },
            fontSize = 16.sp,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Yellow
        )
    }
}
