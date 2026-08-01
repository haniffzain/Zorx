package com.raven.launcher.events

import com.raven.launcher.window.LumaWindow

/**
 * Published whenever a new desktop window is created.
 */
class WindowOpenedEvent(

    val window: LumaWindow

) : LumaEvent()