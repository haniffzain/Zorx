package com.zorx.launcher.events.desktop

import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object
 * changes its position or size.
 */
data class DesktopMovedEvent(

    val desktopObject: DesktopObject

) : ZorxEvent()