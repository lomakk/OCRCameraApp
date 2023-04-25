package com.vision.scantexter.android.screen.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vision.scantexter.android.R
import com.vision.scantexter.android.theme.ScanTexterTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OCRHandleButtons(
    modifier: Modifier = Modifier,
    isTextEmpty: Boolean,
    onSelectAll: () -> Unit,
    onClear: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onCopy: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isTextEmpty) {
            Arrangement.spacedBy(4.dp, alignment = Alignment.End)
        } else {
            Arrangement.spacedBy(4.dp)
        }
    ) {
        AnimatedVisibility(
            modifier = Modifier.weight(1f),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = !isTextEmpty
        ) {
            OCRHandleButton(
                text = stringResource(R.string.camera_clear),
                onClick = onClear
            )
        }
        AnimatedVisibility(
            modifier = Modifier.weight(1f),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = !isTextEmpty
        ) {
            OCRHandleButton(
                text = stringResource(R.string.camera_edit),
                onClick = onEdit
            )
        }
        AnimatedContent(
            modifier = if (isTextEmpty) Modifier.width(100.dp) else Modifier.weight(1f),
            targetState = isTextEmpty
        ) { isEmpty ->
            OCRHandleButton(
                text = stringResource(R.string.camera_select_all),
                onClick = onSelectAll
            )
        }

        AnimatedContent(
            modifier =  if (isTextEmpty) Modifier.width(100.dp) else Modifier.weight(1f),
            targetState = isTextEmpty
        ) { isEmpty ->
            OCRHandleButton(
                text = stringResource(if (isEmpty) R.string.camera_cancel else R.string.camera_copy),
                icon = if (isEmpty) null else Icons.Outlined.ContentCopy,
                onClick = { if (isTextEmpty) onCancel() else onCopy() }
            )
        }
    }
}

@Composable
fun OCRHandleButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = modifier.defaultMinSize(
            minWidth = ButtonDefaults.MinWidth,
            minHeight = 34.dp
        ),
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = Color.Black
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary),
        elevation = ButtonDefaults.elevation(0.dp, pressedElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = MaterialTheme.colors.primary,
                fontFamily = FontFamily.Default,
                maxLines = 1
            )
            if (icon != null) {
                Icon(
                    modifier = Modifier.size(16.dp).padding(top = 1.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OCRHandleButtonPreview() {
    ScanTexterTheme {
        OCRHandleButton(
            text = stringResource(R.string.camera_copy),
            icon = Icons.Rounded.ChevronRight
        ) {

        }
    }
}