package com.zorx.launcher.spatial.commands

import com.zorx.launcher.spatial.SpatialBounds
import com.zorx.launcher.spatial.SpatialEngine

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