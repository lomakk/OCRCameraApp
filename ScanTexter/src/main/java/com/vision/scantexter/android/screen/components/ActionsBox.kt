package com.vision.scantexter.android.screen.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vision.scantexter.android.screen.ocr.CameraOption

@Composable
fun ActionBox(
    modifier: Modifier = Modifier,
    cameraOption: CameraOption,
    onTakePicture: () -> Unit,
    onSwitchCamera: () -> Unit,
    takePictureButtonContent: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = modifier,
    ) {
        OptionSection(
            modifier = Modifier.fillMaxWidth(),
            currentCameraOption = cameraOption
        )
        PictureActions(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 40.dp),
            onTakePicture = onTakePicture,
            onSwitchCamera = onSwitchCamera,
            buttonImageContent = takePictureButtonContent
        )
    }
}