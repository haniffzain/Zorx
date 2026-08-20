package com.zorx.launcher.window

import android.graphics.Rect

/**
 * Wrapper around Android Rect.
 * This allows Zorx to extend window metadata in the future.
 */
data class WindowBounds(

    var rect: Rect
)
