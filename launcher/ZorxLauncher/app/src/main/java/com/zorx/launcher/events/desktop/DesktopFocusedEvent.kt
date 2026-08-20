package com.zorx.launcher.events.desktop

import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object becomes focused.
 */
data class DesktopFocusedEvent(
    val desktopObject: DesktopObject
) : ZorxEvent()
