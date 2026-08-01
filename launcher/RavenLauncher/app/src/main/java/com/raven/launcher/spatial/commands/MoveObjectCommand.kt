package com.raven.launcher.spatial.commands

import com.raven.launcher.spatial.SpatialBounds
import com.raven.launcher.spatial.SpatialEngine

/**
 * Moves a desktop object to a new position.
 */
class MoveObjectCommand(

    private val objectId: String,

    private val bounds: SpatialBounds

) {

    fun execute(
        spatialEngine: SpatialEngine
    ) {

        spatialEngine.moveObject(
            objectId,
            bounds
        )

    }

}