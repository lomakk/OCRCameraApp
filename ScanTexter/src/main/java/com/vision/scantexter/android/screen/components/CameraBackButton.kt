package com.vision.scantexter.android.screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier
            .size(54.dp)
            .border(BorderStroke(2.dp, Color.White), CircleShape)
            .background(Color.Transparent)
            .clip(CircleShape)
            .then(modifier),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.Rounded.Close,
            tint = Color.White,
            contentDescription = null
        )
    }
}