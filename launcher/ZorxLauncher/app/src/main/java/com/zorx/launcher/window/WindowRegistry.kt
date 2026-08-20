package com.zorx.launcher.window

/**
 * Global registry containing every active window in Zorx.
 *
 * Every component shares this single instance.
 */
object WindowRegistry {

    private val windows =
        mutableListOf<ZorxWindow>()

    fun addWindow(
        window: ZorxWindow
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
    ): ZorxWindow? {

        return windows.firstOrNull {
            it.taskId == taskId
        }
    }

    fun findWindowForPackage(
        packageName: String
    ): ZorxWindow? {

        return windows.lastOrNull {
            it.packageName == packageName
        }
    }

    fun replaceTaskId(
        oldTaskId: Int,
        newTaskId: Int
    ): Boolean {

        val index =
            windows.indexOfFirst {
                it.taskId == oldTaskId
            }

        if (index < 0) {
            return false
        }

        val existing =
            windows.removeAt(index)

        windows.removeAll {
            it.taskId == newTaskId
        }

        windows.add(
            existing.copy(
                taskId = newTaskId
            )
        )

        return true
    }

    fun getAllWindows(): List<ZorxWindow> {

        return windows.toList()
    }

    fun clear() {

        windows.clear()
    }

    fun count(): Int {

        return windows.size
    }
}