package com.raven.launcher.window

/**
 * Central API for managing desktop windows in LumaOS.
 *
 * For now this manages only the internal registry.
 * Android backend integration will be added later.
 */
object LumaWindowManager {

    private val registry =
        WindowRegistry
        
    fun registerWindow(
        window: LumaWindow
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
    ): LumaWindow? {

        return registry.findWindow(taskId)
    }

    fun getWindows(): List<LumaWindow> {

        return registry.getAllWindows()
    }

    fun clear() {

        registry.clear()
    }
}
