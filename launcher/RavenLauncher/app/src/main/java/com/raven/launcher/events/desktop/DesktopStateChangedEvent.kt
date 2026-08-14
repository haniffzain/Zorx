package com.raven.launcher.events.desktop

import com.raven.launcher.events.LumaEvent
import com.raven.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object's lifecycle state changes.
 */
data class DesktopStateChangedEvent(
    val desktopObject: DesktopObject
) : LumaEvent()
