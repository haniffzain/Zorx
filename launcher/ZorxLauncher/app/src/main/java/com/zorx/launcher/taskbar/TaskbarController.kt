package com.zorx.launcher.taskbar

import android.util.Log
import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.events.ZorxEventBus
import com.zorx.launcher.events.ZorxEventListener
import com.zorx.launcher.events.WindowOpenedEvent
import com.zorx.launcher.events.desktop.DesktopRemovedEvent
import com.zorx.launcher.events.desktop.DesktopStateChangedEvent
import com.zorx.launcher.spatial.DesktopObjectState
import com.zorx.launcher.spatial.SpatialEngine

/**
 * Coordinates Taskbar state with the shared Zorx SpatialEngine.
 */
class TaskbarController(
    private val taskbarView: TaskbarView,
    private val spatialEngine: SpatialEngine
) : ZorxEventListener {

    companion object {

        private const val TAG =
            "TaskbarController"
    }

    init {

        ZorxEventBus.register(this)
    }

    override fun onEvent(
        event: ZorxEvent
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

        ZorxEventBus.unregister(this)

    }
}
