package com.raven.launcher.desktop

import android.graphics.Canvas
import com.raven.launcher.compositor.DesktopCompositor
import com.raven.launcher.runtime.DesktopRuntime

/**
 * Represents the complete desktop scene.
 *
 * Owns every visual element that exists
 * on the desktop.
 */
class DesktopScene(

    private val runtime: DesktopRuntime

) {

    private val compositor =
        DesktopCompositor(runtime)

    fun render(
        canvas: Canvas
    ) {

        compositor.compose(canvas)

    }

}