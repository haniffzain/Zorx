package com.zorx.launcher.window

import android.graphics.Rect

/**
 * Represents a single desktop window inside Zorx.
 */
data class ZorxWindow(

    val taskId: Int,

    val packageName: String,

    val title: String = "",

    var bounds: Rect,

    var state: WindowState = WindowState.NORMAL,

    var focused: Boolean = false
)
