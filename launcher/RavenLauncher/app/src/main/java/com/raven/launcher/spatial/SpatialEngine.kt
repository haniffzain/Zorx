package com.raven.launcher.spatial

import android.util.Log
import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.desktop.DesktopAddedEvent
import com.raven.launcher.events.desktop.DesktopFocusedEvent
import com.raven.launcher.events.desktop.DesktopMovedEvent
import com.raven.launcher.events.desktop.DesktopRemovedEvent
import com.raven.launcher.events.desktop.DesktopStateChangedEvent

/**
 * Central coordinator for the Luma Spatial Engine.
 */
class SpatialEngine(

    private val repository: DesktopObjectRepository =
        MemoryDesktopObjectRepository()

) {

    companion object {

        private const val TAG =
            "SpatialEngine"
    }

    private val restoreBounds =
        mutableMapOf<String, SpatialBounds>()

    fun moveObject(
        id: String,
        newBounds: SpatialBounds
    ) {

        val desktopObject =
            repository.find(id)
                ?: return

        desktopObject.bounds =
            newBounds

        LumaEventBus.post(
            DesktopMovedEvent(
                desktopObject
            )
        )
    }

    fun addObject(
        desktopObject: DesktopObject
    ) {

        repository.add(
            desktopObject
        )

        LumaEventBus.post(
            DesktopAddedEvent(
                desktopObject
            )
        )
    }

    fun focusObject(
        id: String
    ): Boolean {

        val target =
            repository.find(id)
                ?: return false

        val highestZ =
            repository
                .getAll()
                .maxOfOrNull {
                    it.zIndex
                } ?: 0

        repository
            .getAll()
            .forEach { objectItem ->

                if (objectItem.id == id) {

                    if (
                        objectItem.state !=
                            DesktopObjectState.MAXIMIZED
                    ) {
                        objectItem.state =
                            DesktopObjectState.FOCUSED
                    }

                    objectItem.zIndex =
                        highestZ + 1

                } else {

                    if (
                        objectItem.state ==
                            DesktopObjectState.FOCUSED
                    ) {

                        objectItem.state =
                            DesktopObjectState.NORMAL
                    }
                }
            }

        LumaEventBus.post(
            DesktopFocusedEvent(
                target
            )
        )

        return true
    }

    fun minimizeObject(
        id: String
    ): Boolean {

        val desktopObject =
            repository.find(id)
                ?: return false

        if (
            desktopObject.state ==
                DesktopObjectState.MINIMIZED
        ) {
            return true
        }

        desktopObject.state =
            DesktopObjectState.MINIMIZED

        LumaEventBus.post(
            DesktopStateChangedEvent(
                desktopObject
            )
        )

        return true
    }

    fun maximizeObject(
        id: String,
        viewportWidth: Int,
        viewportHeight: Int
    ): Boolean {

        val desktopObject =
            repository.find(id)
                ?: return false

        if (
            desktopObject.state ==
                DesktopObjectState.MAXIMIZED
        ) {

            return restoreObject(
                id
            )
        }

        val currentBounds =
            desktopObject.bounds

        restoreBounds[id] =
            SpatialBounds(
                x = currentBounds.x,
                y = currentBounds.y,
                width = currentBounds.width,
                height = currentBounds.height
            )

        Log.i(
            TAG,
            "MAXIMIZE saveBounds " +
                "id=$id " +
                "x=${currentBounds.x} " +
                "y=${currentBounds.y} " +
                "width=${currentBounds.width} " +
                "height=${currentBounds.height}"
        )

        desktopObject.bounds =
            SpatialBounds(
                x = 0,
                y = 0,
                width = viewportWidth,
                height = viewportHeight
            )

        desktopObject.state =
            DesktopObjectState.MAXIMIZED

        LumaEventBus.post(
            DesktopStateChangedEvent(
                desktopObject
            )
        )

        return true
    }

    fun restoreFromMinimized(
        id: String
    ): Boolean {

        val desktopObject =
            repository.find(id)
                ?: return false

        if (
            desktopObject.state !=
                DesktopObjectState.MINIMIZED
        ) {
            return false
        }

        desktopObject.state =
            DesktopObjectState.NORMAL

        LumaEventBus.post(
            DesktopStateChangedEvent(
                desktopObject
            )
        )

        return true
    }

    fun restoreObject(
        id: String
    ): Boolean {

        val desktopObject =
            repository.find(id)
                ?: return false

        val originalBounds =
            restoreBounds[id]
                ?: return false

        desktopObject.bounds =
            SpatialBounds(
                x = originalBounds.x,
                y = originalBounds.y,
                width = originalBounds.width,
                height = originalBounds.height
            )

        Log.i(
            TAG,
            "RESTORE bounds " +
                "id=$id " +
                "x=${originalBounds.x} " +
                "y=${originalBounds.y} " +
                "width=${originalBounds.width} " +
                "height=${originalBounds.height}"
        )

        desktopObject.state =
            DesktopObjectState.NORMAL

        restoreBounds.remove(
            id
        )

        LumaEventBus.post(
            DesktopStateChangedEvent(
                desktopObject
            )
        )

        return true
    }

    fun removeObject(
        id: String
    ): Boolean {

        val desktopObject =
            repository.find(id)
                ?: return false

        repository.remove(
            id
        )

        restoreBounds.remove(
            id
        )

        LumaEventBus.post(
            DesktopRemovedEvent(
                id
            )
        )

        return true
    }

    fun findByPackageName(
        packageName: String
    ): DesktopObject? {

        return repository
            .getAll()
            .firstOrNull { objectItem ->

                objectItem.id.startsWith(
                    "window:$packageName:"
                )
            }
    }

    fun findObject(
        id: String
    ): DesktopObject? {

        return repository.find(
            id
        )
    }

    fun getAllObjects(): List<DesktopObject> {

        return repository.getAll()
    }

    fun containsObject(
        id: String
    ): Boolean {

        return repository.find(
            id
        ) != null
    }

    fun clear() {

        repository.clear()

        restoreBounds.clear()
    }
}
