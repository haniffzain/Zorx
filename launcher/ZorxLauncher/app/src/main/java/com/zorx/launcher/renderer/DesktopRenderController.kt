package com.zorx.launcher.renderer

import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.events.ZorxEventBus
import com.zorx.launcher.events.ZorxEventListener
import com.zorx.launcher.events.desktop.DesktopMovedEvent

/**
 * Keeps the desktop renderer synchronized
 * with desktop events.
 */
class DesktopRenderController(

    private val renderer: DesktopRenderer

) : ZorxEventListener {

    init {

        ZorxEventBus.register(this)

    }

    override fun onEvent(
        event: ZorxEvent
    ) {

        when (event) {

            is DesktopMovedEvent -> {

                // DesktopSurface owns visual redraw scheduling.

            }

        }

    }

    fun destroy() {

        ZorxEventBus.unregister(this)

    }

}