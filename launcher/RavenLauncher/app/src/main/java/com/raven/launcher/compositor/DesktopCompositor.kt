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

        val clip =
            canvas.clipBounds

        runtime
            .spatialEngine
            .getAllObjects()
            .forEach { desktopObject ->

                if (
                    desktopObject.state ==
                        com.raven.launcher.spatial.DesktopObjectState.MINIMIZED
                ) {
                    return@forEach
                }

                val bounds =
                    desktopObject.bounds

                val windowLeft =
                    bounds.x

                val windowTop =
                    bounds.y

                val windowRight =
                    bounds.x + bounds.width

                val windowBottom =
                    bounds.y + bounds.height

                if (
                    windowRight <= clip.left ||
                    windowLeft >= clip.right ||
                    windowBottom <= clip.top ||
                    windowTop >= clip.bottom
                ) {
                    return@forEach
                }

                windowPainter.paint(
                    canvas,
                    desktopObject
                )
            }
    }

}