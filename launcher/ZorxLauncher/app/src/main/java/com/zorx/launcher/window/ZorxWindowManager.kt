package com.zorx.launcher.window

/**
 * Central API for managing desktop windows in Zorx.
 *
 * For now this manages only the internal registry.
 * Android backend integration will be added later.
 */
object ZorxWindowManager {

    private val registry =
        WindowRegistry
        
    fun registerWindow(
        window: ZorxWindow
    ) {
        registry.addWindow(window)
    }

    fun unregisterWindow(
        taskId: Int
    ) {
        registry.removeWindow(taskId)
    }

    fun getWindow(
        taskId: Int
    ): ZorxWindow? {

        return registry.findWindow(taskId)
    }

    fun getWindows(): List<ZorxWindow> {

        return registry.getAllWindows()
    }

    fun getWindowForPackage(
        packageName: String
    ): ZorxWindow? {

        return registry.findWindowForPackage(
            packageName
        )
    }

    fun replaceTaskId(
        oldTaskId: Int,
        newTaskId: Int
    ): Boolean {

        return registry.replaceTaskId(
            oldTaskId,
            newTaskId
        )
    }

    fun clear() {

        registry.clear()
    }
}
