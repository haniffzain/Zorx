package com.zorx.launcher.desktop

import android.graphics.Canvas
import com.zorx.launcher.compositor.DesktopCompositor
import com.zorx.launcher.runtime.DesktopRuntime

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