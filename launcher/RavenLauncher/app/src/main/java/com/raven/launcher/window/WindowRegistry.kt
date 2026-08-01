package com.raven.launcher.window

/**
 * Global registry containing every active window in LumaOS.
 *
 * Every component shares this single instance.
 */
object WindowRegistry {

    private val windows =
        mutableListOf<LumaWindow>()

    fun addWindow(
        window: LumaWindow
    ) {

        windows.removeAll {
            it.taskId == window.taskId
        }

        windows.add(window)
    }

    fun removeWindow(
        taskId: Int
    ) {

        windows.removeAll {
            it.taskId == taskId
        }
    }

    fun findWindow(
        taskId: Int
    ): LumaWindow? {

        return windows.firstOrNull {
            it.taskId == taskId
        }
    }

    fun getAllWindows(): List<LumaWindow> {

        return windows.toList()
    }

    fun clear() {

        windows.clear()
    }

    fun count(): Int {

        return windows.size
    }
}