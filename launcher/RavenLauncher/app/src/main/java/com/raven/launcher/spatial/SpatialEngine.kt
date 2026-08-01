package com.raven.launcher.spatial

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

    fun addObject(
        desktopObject: DesktopObject
    ) {

        repository.add(
            desktopObject
        )

    }

    fun removeObject(
        id: String
    ) {

        repository.remove(
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

    fun getObjects(): List<DesktopObject> {

        return repository.getAll()

    }

    fun clear() {

        repository.clear()

    }
}