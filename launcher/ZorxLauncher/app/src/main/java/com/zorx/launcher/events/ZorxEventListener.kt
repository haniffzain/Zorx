package com.zorx.launcher.events

fun interface ZorxEventListener {

    fun onEvent(
        event: ZorxEvent
    )
}