package com.raven.launcher.taskbar

import android.util.Log
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

companion object {

    private const val TAG = "TaskbarController"

}

    init {
        LumaEventBus.register(this)
    }

    override fun onEvent(
        event: LumaEvent
    ) {

        when (event) {

         is WindowOpenedEvent -> {

    Log.i(
        TAG,
        "Window opened: ${event.window.packageName}"
    )

    taskbarView.refreshPinnedApps()

}

        }
    }

    fun destroy() {

        LumaEventBus.unregister(this)

    }
}