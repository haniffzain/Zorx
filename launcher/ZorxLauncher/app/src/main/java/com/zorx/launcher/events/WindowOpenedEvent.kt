package com.zorx.launcher.events

import com.zorx.launcher.window.ZorxWindow

/**
 * Published whenever a new desktop window is created.
 */
class WindowOpenedEvent(

    val window: ZorxWindow

) : ZorxEvent()