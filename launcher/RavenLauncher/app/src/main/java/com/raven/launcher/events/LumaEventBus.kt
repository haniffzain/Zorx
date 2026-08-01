package com.raven.launcher.events

/**
 * Global publish / subscribe event bus for LumaOS.
 */
object LumaEventBus {

    private val listeners =
        mutableListOf<LumaEventListener>()

    fun register(
        listener: LumaEventListener
    ) {

        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun unregister(
        listener: LumaEventListener
    ) {

        listeners.remove(listener)
    }

    fun post(
        event: LumaEvent
    ) {

        listeners.forEach {

            it.onEvent(event)

        }
    }

    fun clear() {

        listeners.clear()
    }

    fun listenerCount(): Int {

        return listeners.size
    }
}