package com.raven.launcher.compositor

import android.graphics.Canvas
import com.raven.launcher.runtime.DesktopRuntime

/**
 * Combines every desktop object into
 * a single rendered desktop frame.
 */
class DesktopCompositor(

    private val runtime: DesktopRuntime,

    private val windowPainter: WindowPainter =
        WindowPainter()

) {

    fun compose(
        canvas: Canvas
    ) {

        runtime
    .spatialEngine
    .getAllObjects()
    .forEach { desktopObject ->

        windowPainter.paint(
            canvas,
            desktopObject
        )

    }

    }

}