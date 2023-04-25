package com.vision.scantexter.android.screen.components

import android.os.Build
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

class CustomTextToolbar(private val view: View) : TextToolbar {
    private var actionMode: ActionMode? = null
    private val textActionModeCallback: TextActionModeCallback = TextActionModeCallback()
    override var status: TextToolbarStatus = TextToolbarStatus.Hidden
        private set

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        textActionModeCallback.rect = rect
        textActionModeCallback.onCopyRequested = {  }
        textActionModeCallback.onCutRequested = { }
        textActionModeCallback.onPasteRequested = { }
        textActionModeCallback.onSelectAllRequested = { }
        if (actionMode == null) {
            status = TextToolbarStatus.Shown
            actionMode = if (Build.VERSION.SDK_INT >= 23) {
                TextToolbarHelperMethods.startActionMode(
                    view,
                    textActionModeCallback,
                    ActionMode.TYPE_FLOATING
                )
            } else {
                view.startActionMode(
                    textActionModeCallback
                )
            }
        } else {
            actionMode?.invalidate()
        }
    }

    override fun hide() {
        status = TextToolbarStatus.Hidden
        actionMode?.finish()
        actionMode = null
    }
}

internal const val MENU_ITEM_COPY = 0
internal const val MENU_ITEM_PASTE = 1
internal const val MENU_ITEM_CUT = 2
internal const val MENU_ITEM_SELECT_ALL = 3

internal class TextActionModeCallback(
    var rect: Rect = Rect.Zero,
    var onCopyRequested: (() -> Unit)? = null,
    var onPasteRequested: (() -> Unit)? = null,
    var onCutRequested: (() -> Unit)? = null,
    var onSelectAllRequested: (() -> Unit)? = null
) : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        requireNotNull(menu)
        requireNotNull(mode)

        onCopyRequested?.let {
            menu.add(0, MENU_ITEM_COPY, 0, android.R.string.copy)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        onPasteRequested?.let {
            menu.add(0, MENU_ITEM_PASTE, 1, android.R.string.paste)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        onCutRequested?.let {
            menu.add(0, MENU_ITEM_CUT, 2, android.R.string.cut)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        onSelectAllRequested?.let {
            menu.add(0, MENU_ITEM_SELECT_ALL, 3, android.R.string.selectAll)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item!!.itemId) {
            MENU_ITEM_COPY -> onCopyRequested?.invoke()
            MENU_ITEM_PASTE -> onPasteRequested?.invoke()
            MENU_ITEM_CUT -> onCutRequested?.invoke()
            MENU_ITEM_SELECT_ALL -> onSelectAllRequested?.invoke()
            else -> return false
        }
        mode?.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {}
}

/**
 * This class is here to ensure that the classes that use this API will get verified and can be
 * AOT compiled. It is expected that this class will soft-fail verification, but the classes
 * which use this method will pass.
 */
@RequiresApi(23)
internal object TextToolbarHelperMethods {
    @RequiresApi(23)
    @DoNotInline
    fun startActionMode(
        view: View,
        actionModeCallback: ActionMode.Callback,
        type: Int
    ): ActionMode {
        return view.startActionMode(
            actionModeCallback,
            type
        )
    }

    @RequiresApi(23)
    fun invalidateContentRect(actionMode: ActionMode) {
        actionMode.invalidateContentRect()
    }
}