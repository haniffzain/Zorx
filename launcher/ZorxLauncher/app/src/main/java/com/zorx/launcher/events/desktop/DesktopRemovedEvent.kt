package com.zorx.launcher.events.desktop

import com.zorx.launcher.events.ZorxEvent

/**
 * Published whenever a desktop object
 * is removed from the desktop.
 */
data class DesktopRemovedEvent(

    val objectId: String

) : ZorxEvent()