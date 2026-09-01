package com.zorx.launcher.events.desktop

import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.spatial.DesktopObject

/**
 * Published whenever a desktop object
 * is removed from the desktop.
 */
data class DesktopRemovedEvent(

    val desktopObject: DesktopObject

) : ZorxEvent() {
    val objectId: String
        get() = desktopObject.id
}
