package com.raven.launcher.spatial

/**
 * Base model for every object that can exist
 * on the Luma desktop surface.
 *
 * Window
 * Widget
 * Desktop Icon
 * AI Panel
 * Sticky Note
 * Future desktop objects
 */
open class DesktopObject(

    val id: String,

    var title: String,

    var bounds: SpatialBounds,

    var state: DesktopObjectState = DesktopObjectState.NORMAL,

    var zIndex: Int = 0,

    var isLocked: Boolean = false

)