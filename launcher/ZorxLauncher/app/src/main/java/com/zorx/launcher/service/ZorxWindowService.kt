package com.zorx.launcher.service

import com.zorx.launcher.events.ZorxEventBus
import com.zorx.launcher.events.WindowOpenedEvent
import com.zorx.launcher.window.ZorxWindow
import com.zorx.launcher.window.ZorxWindowManager
import android.util.Log

/**
 * Central service responsible for every window
 * operation inside Zorx.
 *
 * UI components never communicate directly with
 * WindowManager.
 */
class ZorxWindowService {

    companion object {
        private const val TAG = "ZorxWindowService"
    }

    private val windowManager =
        ZorxWindowManager

    fun registerWindow(
    window: ZorxWindow
) {

    windowManager.registerWindow(
        window
    )

    ZorxEventBus.post(

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

    fun getWindows(): List<ZorxWindow> {

        return windowManager.getWindows()
    }

    fun clear() {

        windowManager.clear()
    }
}