package com.raven.launcher.taskbar

import android.util.Log
import com.raven.launcher.events.LumaEvent
import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.LumaEventListener
import com.raven.launcher.events.WindowOpenedEvent
import com.raven.launcher.events.desktop.DesktopRemovedEvent
import com.raven.launcher.events.desktop.DesktopStateChangedEvent
import com.raven.launcher.spatial.DesktopObjectState
import com.raven.launcher.spatial.SpatialEngine

/**
 * Coordinates Taskbar state with the shared Luma SpatialEngine.
 */
class TaskbarController(
    private val taskbarView: TaskbarView,
    private val spatialEngine: SpatialEngine
) : LumaEventListener {

    companion object {

        private const val TAG =
            "TaskbarController"
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
                taskbarView.refreshRunningWindows()
            }

            is DesktopStateChangedEvent -> {

                Log.i(
                    TAG,
                    "Window state changed: " +
                        event.desktopObject.title +
                        " state=" +
                        event.desktopObject.state
                )

                taskbarView.refreshRunningWindows()
            }

            is DesktopRemovedEvent -> {

                Log.i(
                    TAG,
                    "Window removed: ${event.objectId}"
                )

                taskbarView.refreshRunningWindows()
            }
        }
    }

    fun onAppClicked(
        packageName: String
    ): Boolean {

        val desktopObject =
            spatialEngine.findByPackageName(
                packageName
            )
                ?: return false

        when (
            desktopObject.state
        ) {

            DesktopObjectState.MINIMIZED -> {

                spatialEngine.restoreFromMinimized(
                    desktopObject.id
                )

                spatialEngine.focusObject(
                    desktopObject.id
                )

                return true
            }

            else -> {

                spatialEngine.focusObject(
                    desktopObject.id
                )

                return true
            }
        }
    }

    fun onRunningWindowClicked(
        objectId: String
    ): Boolean {

        val desktopObject =
            spatialEngine.findObject(
                objectId
            )
                ?: return false

        if (
            desktopObject.state ==
                DesktopObjectState.MINIMIZED
        ) {

            spatialEngine.restoreFromMinimized(
                desktopObject.id
            )
        }

        spatialEngine.focusObject(
            desktopObject.id
        )

        return true
    }

    fun destroy() {

        LumaEventBus.unregister(this)

    }
}
