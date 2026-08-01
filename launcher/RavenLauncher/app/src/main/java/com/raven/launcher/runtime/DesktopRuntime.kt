package com.raven.launcher.runtime

import com.raven.launcher.spatial.SpatialEngine

/**
 * Central runtime for the Luma desktop.
 *
 * Owns every core desktop subsystem.
 */
class DesktopRuntime(

    val spatialEngine:
        SpatialEngine =
            SpatialEngine()

)