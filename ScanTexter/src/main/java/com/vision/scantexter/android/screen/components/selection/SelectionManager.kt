package com.vision.scantexter.android.screen.components.selection

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

/**
 * A bridge class between user interaction to the text composables for text selection.
 */
internal class SelectionManager() {

    private val _selection: MutableState<Selection?> = mutableStateOf(null)

    /**
     * The current selection.
     */
    var selection: Selection?
        get() = _selection.value
        set(value) {
            _selection.value = value
            if (value != null) {
                updateHandleOffsets()
            }
        }

    /**
     * Modifier for selection container.
     */
    val modifier
        get() = Modifier
            .onGloballyPositioned { containerLayoutCoordinates = it }

    /**
     * Layout Coordinates of the selection container.
     */
    var containerLayoutCoordinates: LayoutCoordinates? = null
        set(value) {
            field = value
            if (selection != null) {
                val positionInWindow = value?.positionInWindow()
            }
        }

    /**
     * The beginning position of the drag gesture. Every time a new drag gesture starts, it wil be
     * recalculated.
     */
    internal var dragBeginPosition by mutableStateOf(Offset.Zero)
        private set

    /**
     * The total distance being dragged of the drag gesture. Every time a new drag gesture starts,
     * it will be zeroed out.
     */
    internal var dragTotalDistance by mutableStateOf(Offset.Zero)
        private set

    /**
     * The calculated position of the start handle in the [SelectionContainer] coordinates. It
     * is null when handle shouldn't be displayed.
     * It is a [State] so reading it during the composition will cause recomposition every time
     * the position has been changed.
     */
    var startHandlePosition: Offset? by mutableStateOf(null)
        private set

    /**
     * The calculated position of the end handle in the [SelectionContainer] coordinates. It
     * is null when handle shouldn't be displayed.
     * It is a [State] so reading it during the composition will cause recomposition every time
     * the position has been changed.
     */
    var endHandlePosition: Offset? by mutableStateOf(null)
        private set

    /**
     * When a handle is being dragged (i.e. [draggingHandle] is non-null), this is the last position
     * of the actual drag event. It is not clamped to handle positions. Null when not being dragged.
     */
    var currentDragPosition: Offset? by mutableStateOf(null)
        private set

    private fun updateHandleOffsets() {

    }

    fun handleDragObserver(isStartHandle: Boolean): TextDragObserver = object : TextDragObserver {
        override fun onDown(point: Offset) {
            val selection = selection ?: return
            val anchor = if (isStartHandle) selection.start else selection.end

        }

        override fun onUp() {
            currentDragPosition = null
        }

        override fun onStart(startPoint: Offset) {
            val selection = selection!!
        }

        override fun onDrag(delta: Offset) {
        }

        override fun onStop() {

        }

        override fun onCancel() {

        }
    }
}