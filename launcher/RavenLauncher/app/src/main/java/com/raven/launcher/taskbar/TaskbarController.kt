package com.raven.launcher.taskbar

import com.raven.launcher.events.LumaEvent
import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.LumaEventListener
import com.raven.launcher.events.WindowOpenedEvent

/**
 * Receives desktop events and coordinates the Taskbar UI.
 */
class TaskbarController(
    private val taskbarView: TaskbarView
) : LumaEventListener {

    init {
        LumaEventBus.register(this)
    }

    override fun onEvent(
        event: LumaEvent
    ) {

        when (event) {

            is WindowOpenedEvent -> {

                taskbarView.refreshPinnedApps()

            }

        }
    }

    fun destroy() {

        LumaEventBus.unregister(this)

    }
}