package com.raven.launcher.spatial

import com.raven.launcher.events.LumaEventBus
import com.raven.launcher.events.desktop.DesktopAddedEvent
import com.raven.launcher.events.desktop.DesktopMovedEvent
import com.raven.launcher.events.desktop.DesktopRemovedEvent

/**
 * Central coordinator for the Luma Spatial Engine.
 *
 * The engine manages desktop objects through a repository.
 * Grid, snapping and layout behaviour will be introduced
 * in future Aurora sprints.
 */
class SpatialEngine(

    private val repository: DesktopObjectRepository =
        MemoryDesktopObjectRepository()

) {

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

    fun removeObject(
        id: String
    ) {

        DesktopRemovedEvent(
            id
        )

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

    return repository.find(id) != null

}

    fun clear() {

        repository.clear()

    }
}
