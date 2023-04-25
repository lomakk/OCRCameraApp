package com.vision.scantexter.android.screen.components.selection

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.Constraints

/**
 * Provides [Selection] information for a composable to SelectionContainer. Composables who can
 * be selected should subscribe to [SelectionRegistrar] using this interface.
 */

internal interface Selectable {
    val selectableId: Long
    fun updateSelection(isSelected: Boolean): Boolean

    /**
     * Return the [Offset] of a [SelectionHandle].
     *
     * @param selection [Selection] contains the [SelectionHandle]
     * @param isStartHandle true if it's the start handle, false if it's the end handle.
     *
     * @return [Offset] of this handle, based on which the [SelectionHandle] will be drawn.
     */
    fun getHandlePosition(selection: Selection, isStartHandle: Boolean): Offset

    /**
     * Return the [LayoutCoordinates] of the [Selectable].
     *
     * @return [LayoutCoordinates] of the [Selectable]. This could be null if called before
     * composing.
     */
    fun getLayoutCoordinates(): LayoutCoordinates?

    /**
     * Return the [AnnotatedString] of the [Selectable].
     *
     * @return text content as [AnnotatedString] of the [Selectable].
     */
    fun getText(): AnnotatedString

    /**
     * Return the bounding box of the character for given character offset. This is currently for
     * text.
     * In future when we implemented other selectable Composables, we can return the bounding box of
     * the wanted rectangle. For example, for an image selectable, this should return the
     * bounding box of the image.
     *
     * @param offset a character offset
     * @return the bounding box for the character in [Rect], or [Rect.Zero] if the selectable is
     * empty.
     */
    fun getBoundingBox(offset: Int): Rect
}