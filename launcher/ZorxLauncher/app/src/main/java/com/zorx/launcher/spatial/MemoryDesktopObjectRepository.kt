package com.zorx.launcher.spatial

/**
 * Default in-memory repository used by
 * the Zorx Spatial Engine.
 */
class MemoryDesktopObjectRepository :
    DesktopObjectRepository {

    private val desktopObjects =
        mutableListOf<DesktopObject>()

    override fun add(
        desktopObject: DesktopObject
    ) {

        desktopObjects.add(
            desktopObject
        )
    }

    override fun remove(
        id: String
    ) {

        desktopObjects.removeAll {

            it.id == id

        }
    }

    override fun find(
        id: String
    ): DesktopObject? {

        return desktopObjects.firstOrNull {

            it.id == id

        }
    }

    override fun getAll(): List<DesktopObject> {

        return desktopObjects.toList()

    }

    override fun clear() {

        desktopObjects.clear()

    }
}