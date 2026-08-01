package com.raven.launcher.window

import android.graphics.Rect

/**
 * Wrapper around Android Rect.
 * This allows LumaOS to extend window metadata in the future.
 */
data class WindowBounds(

    var rect: Rect
)
