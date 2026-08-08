package com.raven.launcher.desktop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.raven.launcher.events.LumaEvent
import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.LumaEventListener
import com.raven.launcher.runtime.DesktopRuntime

class DesktopSurface @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), LumaEventListener {

    private val runtime =
        DesktopRuntime()

    private val scene =
        DesktopScene(runtime)

    init {

        LumaEventBus.register(this)

    }

    override fun onEvent(
        event: LumaEvent
    ) {

        postInvalidateOnAnimation()

    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        canvas.drawColor(
            Color.rgb(28, 30, 38)
        )

        scene.render(canvas)

    }

    override fun onDetachedFromWindow() {

        LumaEventBus.unregister(this)

        runtime.destroy()

        super.onDetachedFromWindow()

    }
}
