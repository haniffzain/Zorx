package com.raven.launcher.runtime

import com.raven.launcher.events.LumaEvent
import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.LumaEventListener
import com.raven.launcher.events.WindowOpenedEvent
import com.raven.launcher.spatial.DesktopObject
import com.raven.launcher.spatial.SpatialEngine
import com.raven.launcher.spatial.SpatialBounds
import android.util.Log

/**
 * Central runtime for the Luma desktop.
 *
 * Owns the spatial desktop runtime and converts
 * desktop events into spatial objects.
 */
class DesktopRuntime : LumaEventListener {

    companion object {
        private const val TAG = "DesktopRuntime"
    }

    val spatialEngine =
        SpatialEngine()

    init {

        LumaEventBus.register(this)

        Log.i(
            TAG,
            "DesktopRuntime registered with LumaEventBus"
        )

        spatialEngine.addObject(
            DesktopObject(
                id = "hello",
                title = "Hello LumaOS",
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
        event: LumaEvent
    ) {

        Log.i(
            TAG,
            "EVENT RECEIVED: ${event::class.simpleName}"
        )

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
        }
    }

    fun destroy() {

        LumaEventBus.unregister(this)

    }
}
