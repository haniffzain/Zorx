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

/**
 * Central runtime for the Zorx desktop.
 *
 * Owns the spatial desktop runtime and converts
 * desktop events into spatial objects.
 */
class DesktopRuntime(
    context: Context
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
                }
            }

            is com.zorx.launcher.events.desktop.DesktopMovedEvent -> {

                nativeTaskSynchronizer.syncBounds(
                    event.desktopObject
                )
            }
        }
    }

    fun destroy() {

        ZorxEventBus.unregister(this)

    }
}
