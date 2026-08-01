package com.raven.launcher.compositor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.raven.launcher.spatial.DesktopObject

/**
 * Paints a desktop object onto the screen.
 */
class WindowPainter {

    private val paint =
        Paint().apply {

            color =
                Color.DKGRAY

        }

    fun paint(

        canvas: Canvas,

        desktopObject: DesktopObject

    ) {

        val b =
            desktopObject.bounds

        canvas.drawRect(

            b.x.toFloat(),

            b.y.toFloat(),

            (b.x + b.width).toFloat(),

            (b.y + b.height).toFloat(),

            paint

        )

    }

}