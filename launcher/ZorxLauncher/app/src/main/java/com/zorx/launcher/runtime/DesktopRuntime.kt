package com.zorx.launcher.runtime

import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.events.ZorxEventBus
import com.zorx.launcher.events.ZorxEventListener
import com.zorx.launcher.events.WindowOpenedEvent
import com.zorx.launcher.spatial.DesktopObject
import com.zorx.launcher.spatial.SpatialEngine
import com.zorx.launcher.spatial.SpatialBounds
import android.util.Log
import android.content.Context
import com.zorx.launcher.workspace.ZorxWorkspaceManager
import com.zorx.launcher.workspace.ZorxWorkspaceId
import com.zorx.launcher.display.ZorxDisplayId
import com.zorx.launcher.display.ZorxDisplayManager
import com.zorx.launcher.windowing.ZorxWindowLocationManager
import com.zorx.launcher.shell.ZorxShellSettingsStore

/**
 * Central runtime for the Zorx desktop.
 *
 * Owns the spatial desktop runtime and converts
 * desktop events into spatial objects.
 */
class DesktopRuntime(
    val context: Context
) : ZorxEventListener {

    companion object {
        private const val TAG = "DesktopRuntime"
    }

    val spatialEngine =
        SpatialEngine()

    private val nativeTaskSynchronizer =
        NativeTaskSynchronizer(
            context
        )

    init {

        ZorxEventBus.register(this)

        Log.i(
            TAG,
            "DesktopRuntime registered with ZorxEventBus"
        )

        spatialEngine.addObject(
            DesktopObject(
                id = "hello",
                title = "Hello Zorx",
                bounds = SpatialBounds(
                    250,
                    150,
                    600,
                    400
                )
            )
        )
    }

    override fun onEvent(
        event: ZorxEvent
    ) {

        when (event) {

            is WindowOpenedEvent -> {

                val window =
                    event.window

                val bounds =
                    window.bounds

                val objectId =
                    "window:${window.packageName}:${window.taskId}:${window.title}"

                if (!spatialEngine.containsObject(objectId)) {

                    ZorxWorkspaceManager.assignIfAbsent(context, objectId)

                    spatialEngine.addObject(

                        DesktopObject(

                            id = objectId,

                            title =
                                window.title,

                            bounds =
                                SpatialBounds(
                                    bounds.left,
                                    bounds.top,
                                    bounds.width(),
                                    bounds.height()
                                )
                        )
                    )
                    spatialEngine.findObject(objectId)?.let { created ->
                        ZorxWindowLocationManager.ensure(context, created, displayTopology())
                    }
                }
            }

            is com.zorx.launcher.events.desktop.DesktopMovedEvent -> {

                nativeTaskSynchronizer.syncBounds(
                    event.desktopObject
                )
            }
        }
    }

    fun moveWindowToWorkspace(windowId: String, workspaceId: ZorxWorkspaceId) {
        val window = spatialEngine.findObject(windowId) ?: return
        ZorxWindowLocationManager.moveToWorkspace(context, window, workspaceId, displayTopology())
    }

    fun moveWindowToDisplay(windowId: String, displayId: ZorxDisplayId) {
        val window = spatialEngine.findObject(windowId) ?: return
        val topology = displayTopology()
        val nativeBounds = ZorxWindowLocationManager.moveToDisplay(context, window, displayId, topology) ?: return
        // Waydroid commonly exposes one native display. The normal SpatialEngine path
        // keeps its task synchronization bridge authoritative when a move is possible.
        if (topology.displays.size > 1 && window.state != com.zorx.launcher.spatial.DesktopObjectState.MINIMIZED) {
            spatialEngine.moveObject(windowId, nativeBounds)
        }
    }

    private fun displayTopology() = ZorxDisplayManager(context).topology(
        ZorxShellSettingsStore.readDisplay(context).displayScale
    )

    fun destroy() {

        ZorxEventBus.unregister(this)

    }
}
