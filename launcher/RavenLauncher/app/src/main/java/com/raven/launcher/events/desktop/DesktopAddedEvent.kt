package com.raven.launcher.events.desktop

import com.raven.launcher.events.LumaEvent
import com.raven.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object
 * is added to the desktop.
 */
data class DesktopAddedEvent(

    val desktopObject: DesktopObject

) : LumaEvent()