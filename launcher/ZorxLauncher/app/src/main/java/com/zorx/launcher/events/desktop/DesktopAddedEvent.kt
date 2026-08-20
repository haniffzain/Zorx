package com.zorx.launcher.events.desktop

import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object
 * is added to the desktop.
 */
data class DesktopAddedEvent(

    val desktopObject: DesktopObject

) : ZorxEvent()