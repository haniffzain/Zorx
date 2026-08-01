package com.raven.launcher.events

fun interface LumaEventListener {

    fun onEvent(
        event: LumaEvent
    )
}