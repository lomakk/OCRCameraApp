package com.vision.scantexter.android.screen.ocr

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vision.scantexter.android.camera.state.CameraState
import com.vision.scantexter.android.camera.state.ImageExtractTextResult
import com.vision.scantexter.android.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class OCRViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<CameraOCRUiState> = MutableStateFlow(CameraOCRUiState.Initial)
    val uiState: StateFlow<CameraOCRUiState> get() = _uiState

    private val _ocrState: MutableStateFlow<OCRState> = MutableStateFlow(OCRState())
    val ocrState: StateFlow<OCRState> get() = _ocrState

    init {
        initCamera()
    }

    fun handleTap(tapOffset: Offset, onLineFound: (line: OCRLine) -> Unit) {
        viewModelScope.launch {
            _ocrState.update {
                val updatedBlocks = ocrState.value.blocks.updateBlocksByTap(tapOffset, onLineFound)
                it.copy(
                    blocks = updatedBlocks,
                    selectedText = TextFieldValue(updatedBlocks.getSelectedText())
                )
            }
        }
    }

    fun handleAll(isClear: Boolean) {
        viewModelScope.launch {
            _ocrState.update {
                val updatedBlocks = ocrState.value.blocks.handleAll(isClear = isClear)
                it.copy(
                    blocks = updatedBlocks,
                    selectedText = TextFieldValue(updatedBlocks.getSelectedText())
                )
            }
        }
    }

    fun updateSelectedText(value: TextFieldValue) {
        viewModelScope.launch {
            _ocrState.update {
                it.copy(selectedText = value)
            }
        }
    }

    fun handleDrag(dragOffset: Offset, dragAmount: Offset, onLineAdded: (line: OCRLine) -> Unit) {
        viewModelScope.launch {
            _ocrState.update {
                val updatedBlocks = ocrState.value.blocks.updateBlocksByDrag(
                    currentOffsetChange = dragOffset,
                    dragAmount = dragAmount,
                    onLineAdded = onLineAdded
                )
                it.copy(
                    blocks = updatedBlocks,
                    selectedText = TextFieldValue(updatedBlocks.getSelectedText())
                )
            }
        }
    }

    fun initCamera() {
        viewModelScope.launch {
            flowOf(true)
                .onStart { CameraOCRUiState.Initial }
                .collect {
                    _uiState.value = CameraOCRUiState.Ready()
                }
        }
    }

    fun extractText(cameraState: CameraState, tempFile: File) = with(cameraState) {
        viewModelScope.launch {
            extractTextFromFile(
                tempFile,
                onResult = ::onImageResult
            )
        }
    }

    private fun captureSuccess(file: File) {
        viewModelScope.launch {
            _uiState.update {
                CameraOCRUiState.Captured(imageFile = file)
            }
        }
    }

    private fun textRecognized(text: OCRText) {
        _ocrState.update {
            OCRState(
                blocks = text.blocks,
                selectedText = TextFieldValue(text.blocks.getSelectedText())
            )
        }
    }

    private fun onImageResult(imageResult: ImageExtractTextResult) {
        when (imageResult) {
            is ImageExtractTextResult.Error -> onError(imageResult.throwable)
            is ImageExtractTextResult.SuccessText -> {
                textRecognized(
                    text = imageResult.text
                )
            }
            is ImageExtractTextResult.SuccessFile -> {
                captureSuccess(
                    file = imageResult.savedFile
                )
            }
        }
    }

    private fun onError(throwable: Throwable?) {
        _uiState.update {
            CameraOCRUiState.Ready(
                throwable = throwable
            )
        }
    }
}

sealed interface CameraOCRUiState {
    object Initial : CameraOCRUiState
    data class Ready(
        val throwable: Throwable? = null,
        val capturing: Boolean = false
    ) : CameraOCRUiState

    data class Captured(
        val imageFile: File
    ) : CameraOCRUiState
}