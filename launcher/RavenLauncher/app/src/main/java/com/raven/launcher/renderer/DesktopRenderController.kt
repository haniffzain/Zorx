package com.raven.launcher.renderer

import com.raven.launcher.events.LumaEvent
import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.LumaEventListener
import com.raven.launcher.events.desktop.DesktopMovedEvent

/**
 * Keeps the desktop renderer synchronized
 * with desktop events.
 */
class DesktopRenderController(

    private val renderer: DesktopRenderer

) : LumaEventListener {

    init {

        LumaEventBus.register(this)

    }

    override fun onEvent(
        event: LumaEvent
    ) {

        when (event) {

            is DesktopMovedEvent -> {

                // DesktopSurface owns visual redraw scheduling.

            }

        }

    }

    fun destroy() {

        LumaEventBus.unregister(this)

    }

}