package com.raven.launcher.desktop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import com.raven.launcher.events.LumaEvent
import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.LumaEventListener
import com.raven.launcher.runtime.DesktopRuntime
import com.raven.launcher.interaction.WindowInteractionController

class DesktopSurface @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), LumaEventListener {

    private val runtime =
        DesktopRuntime()

    val spatialEngine
        get() = runtime.spatialEngine

    private val scene =
        DesktopScene(runtime)
    private val interactionController =
        WindowInteractionController(
            runtime.spatialEngine
        ) {
            width to height
        }


    init {

        LumaEventBus.register(this)

    }

    override fun onEvent(
        event: LumaEvent
    ) {

        postInvalidateOnAnimation()

    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (interactionController.onTouchEvent(event)) {
            return true
        }

        return super.onTouchEvent(event)

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
