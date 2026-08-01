package com.raven.launcher.events.desktop

import com.raven.launcher.events.LumaEvent

/**
 * Published whenever a desktop object
 * is removed from the desktop.
 */
data class DesktopRemovedEvent(

    val objectId: String

) : LumaEvent()