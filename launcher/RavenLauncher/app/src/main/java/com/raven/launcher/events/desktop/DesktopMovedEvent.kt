package com.raven.launcher.events.desktop

import com.raven.launcher.events.LumaEvent
import com.raven.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object
 * changes its position or size.
 */
data class DesktopMovedEvent(

    val desktopObject: DesktopObject

) : LumaEvent()