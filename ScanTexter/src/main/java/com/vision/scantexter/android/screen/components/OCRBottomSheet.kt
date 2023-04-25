package com.vision.scantexter.android.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vision.scantexter.android.R

@Composable
fun OCRBottomSheet(
    modifier: Modifier = Modifier,
    currentFraction: Float,
    isTextEmpty: Boolean,
    selectedText: TextFieldValue,
    onSelectAll: () -> Unit,
    onClear: () -> Unit,
    onCancel: () -> Unit,
    onCopy: () -> Unit,
    onUpdateSelectedText: (TextFieldValue) -> Unit,
    openSheet: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BlindView(
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = 12.dp),
            color = MaterialTheme.colors.primary,
            expandableFraction = currentFraction
        )
        OCRHandleButtons(
            isTextEmpty = isTextEmpty,
            onSelectAll = onSelectAll,
            onEdit = openSheet,
            onClear = onClear,
            onCancel = onCancel,
            onCopy = onCopy
        )
        Card(
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .navigationBarsPadding()
                .alpha(1 - currentFraction),
            elevation = 0.dp
        ) {
            TextField(
                value = selectedText,
                onValueChange = onUpdateSelectedText,
                modifier = Modifier
                    .fillMaxSize(),
                textStyle = TextStyle(
                    color = MaterialTheme.colors.primary,
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.primary,
                    cursorColor = MaterialTheme.colors.primary,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.camera_selected_text),
                        color = MaterialTheme.colors.secondary.copy(alpha = 0.4f)
                    )
                }
            )
        }
    }
}