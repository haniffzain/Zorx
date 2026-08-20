package com.zorx.launcher.spatial

/**
 * Represents the position and size of a desktop object
 * within the Zorx Spatial Engine.
 *
 * This class is platform-independent and must not depend
 * on Android framework classes.
 */
data class SpatialBounds(

    val x: Int,

    val y: Int,

    val width: Int,

    val height: Int

)