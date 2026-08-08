package com.raven.launcher.service

import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.WindowOpenedEvent
import com.raven.launcher.window.LumaWindow
import com.raven.launcher.window.LumaWindowManager
import android.util.Log

/**
 * Central service responsible for every window
 * operation inside LumaOS.
 *
 * UI components never communicate directly with
 * WindowManager.
 */
class LumaWindowService {

    companion object {
        private const val TAG = "LumaWindowService"
    }

    private val windowManager =
        LumaWindowManager

    fun registerWindow(
    window: LumaWindow
) {

    windowManager.registerWindow(
        window
    )

    LumaEventBus.post(

        WindowOpenedEvent(
            window
        )

    )
}

    fun unregisterWindow(
        taskId: Int
    ) {

        windowManager.unregisterWindow(
            taskId
        )
    }

    fun getWindows(): List<LumaWindow> {

        return windowManager.getWindows()
    }

    fun clear() {

        windowManager.clear()
    }
}