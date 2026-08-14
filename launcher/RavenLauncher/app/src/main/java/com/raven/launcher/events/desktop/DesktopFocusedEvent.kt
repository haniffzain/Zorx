package com.raven.launcher.events.desktop

import com.raven.launcher.events.LumaEvent
import com.raven.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object becomes focused.
 */
data class DesktopFocusedEvent(
    val desktopObject: DesktopObject
) : LumaEvent()
