package com.raven.launcher.spatial

/**
 * Defines how desktop objects are stored and retrieved.
 *
 * Different implementations may store objects in memory,
 * files, databases or cloud services.
 */
interface DesktopObjectRepository {

    fun add(
        desktopObject: DesktopObject
    )

    fun remove(
        id: String
    )

    fun find(
        id: String
    ): DesktopObject?

    fun getAll(): List<DesktopObject>

    fun clear()

}