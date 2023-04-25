package com.vision.scantexter.android.screen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vision.scantexter.android.R

@Composable
fun PictureActions(
    modifier: Modifier = Modifier,
    onTakePicture: () -> Unit,
    onSwitchCamera: () -> Unit,
    buttonImageContent: @Composable BoxScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SwitchButton(onClick = onSwitchCamera)
        PictureButton(
            onClick = { onTakePicture() },
            content = {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                        .background(Color.White)
                ) {
                    buttonImageContent()
                }
            }
        )
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
fun SwitchButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var clicked by remember { mutableStateOf(false) }
    val rotate by animateFloatAsState(
        targetValue = if (clicked) 360F else 1F,
        animationSpec = tween(durationMillis = 500)
    )
    Button(
        modifier = Modifier
            .rotate(rotate)
            .size(54.dp)
            .border(BorderStroke(2.dp, Color.White), CircleShape)
            .background(Color.Transparent)
            .clip(CircleShape)
            .then(modifier),
        onClick = {
            clicked = !clicked
            onClick()
        }
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.camera_refresh),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = stringResource(R.string.camera_refresh)
        )
    }
}

@Composable
fun PictureButton(
    modifier: Modifier = Modifier,
    buttonColor: Color = Color.Transparent,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val enableButton = remember { mutableStateOf(true) }

    val color by animateColorAsState(
        targetValue = buttonColor,
        animationSpec = tween(durationMillis = 250)
    )

    val innerPadding by animateDpAsState(targetValue = 8.dp)
    val percentShape by animateIntAsState(targetValue = 50)
    CameraButton(
        modifier = Modifier
            .size(90.dp)
            .border(BorderStroke(2.dp, Color.White), CircleShape)
            .padding(innerPadding)
            .background(color, RoundedCornerShape(percentShape))
            .clip(CircleShape)
            .then(modifier),
        onClick = {
            enableButton.value = false
            onClick()
        },
        content = content,
        enabled = enableButton.value
    )
}


@Composable
fun CameraButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPaddingValues: PaddingValues = PaddingValues(0.dp),
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9F else 1F)

    Box(
        modifier = Modifier
            .scale(scale)
            .then(modifier)
            .clickable(
                enabled = enabled,
                indication = rememberRipple(bounded = true),
                interactionSource = interactionSource,
                onClick = onClick,
            )
            .padding(contentPaddingValues),
        contentAlignment = Alignment.Center,
        content = content
    )
}
