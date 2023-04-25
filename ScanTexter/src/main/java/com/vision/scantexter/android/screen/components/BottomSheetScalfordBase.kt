package com.vision.scantexter.android.screen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetScalfordBase(
    modifier: Modifier = Modifier,
    startPeekHeight: Dp = 0.dp,
    closePeekHeight: Dp = 0.dp,
    peekHeightState: MutableState<Dp> = remember { mutableStateOf(startPeekHeight) },
    isInitialCollapsed: Boolean = true,
    sheetCorners: Dp = 8.dp,
    sheetGesturesEnabled: Boolean = true,
    sheetExpandedState: MutableState<Boolean> = remember { mutableStateOf(false) },
    sheetContent: @Composable (openSheet: () -> Unit, closeSheet: () -> Unit, currentFraction: Float) -> Unit,
    content: @Composable (openSheet: () -> Unit, closeSheet: () -> Unit, currentFraction: Float) -> Unit
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = if (isInitialCollapsed) {
                BottomSheetValue.Collapsed
            } else {
                BottomSheetValue.Expanded
            },
            confirmStateChange = {
                sheetExpandedState.value = it == BottomSheetValue.Expanded
                true
            }
        )
    )
    val coroutineScope = rememberCoroutineScope()

    val closeSheet: () -> Unit = {
        coroutineScope.launch {
            peekHeightState.value = closePeekHeight
            delay(150)
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
    }

    val openSheet: () -> Unit = {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.expand()
            bottomSheetScaffoldState.drawerState
        }
    }

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            sheetContent(openSheet, closeSheet, bottomSheetScaffoldState.currentFraction)
        },
        sheetPeekHeight = peekHeightState.value,
        sheetShape = RoundedCornerShape(topEnd = sheetCorners, topStart = sheetCorners),
        sheetElevation = if (bottomSheetScaffoldState.bottomSheetState.currentValue == BottomSheetValue.Expanded) 0.dp else 16.dp,
        sheetGesturesEnabled = sheetGesturesEnabled
    ) {
        content(openSheet, closeSheet, bottomSheetScaffoldState.currentFraction)
    }
}

@OptIn(ExperimentalMaterialApi::class)
val BottomSheetScaffoldState.currentFraction: Float
    get() {
        try {
            val fraction = bottomSheetState.progress
            val currentValue = bottomSheetState.currentValue

            return when {
                currentValue == BottomSheetValue.Collapsed -> 1f
                currentValue == BottomSheetValue.Expanded -> 0f
                currentValue == BottomSheetValue.Collapsed -> {
                    1f - fraction
                }
                else -> {
                    fraction
                }
            }
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            return 0f
        }

    }