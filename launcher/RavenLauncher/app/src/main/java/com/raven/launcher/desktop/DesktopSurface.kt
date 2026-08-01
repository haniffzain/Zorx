package com.raven.launcher.desktop

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.raven.launcher.compositor.DesktopCompositor
import com.raven.launcher.runtime.DesktopRuntime

class DesktopSurface @JvmOverloads constructor(

    context: Context,
    attrs: AttributeSet? = null

) : View(context, attrs) {

    private val runtime =
        DesktopRuntime()

    private val compositor =
        DesktopCompositor(runtime)

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        compositor.compose(canvas)

    }
}