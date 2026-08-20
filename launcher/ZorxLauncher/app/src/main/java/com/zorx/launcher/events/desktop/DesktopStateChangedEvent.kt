package com.zorx.launcher.events.desktop

import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object's lifecycle state changes.
 */
data class DesktopStateChangedEvent(
    val desktopObject: DesktopObject
) : ZorxEvent()
