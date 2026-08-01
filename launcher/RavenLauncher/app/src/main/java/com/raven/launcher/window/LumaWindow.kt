package com.raven.launcher.window

import android.graphics.Rect

/**
 * Represents a single desktop window inside LumaOS.
 */
data class LumaWindow(

    val taskId: Int,

    val packageName: String,

    val title: String = "",

    var bounds: Rect,

    var state: WindowState = WindowState.NORMAL,

    var focused: Boolean = false
)
