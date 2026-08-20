package com.zorx.launcher.events

/**
 * Global publish / subscribe event bus for Zorx.
 */
object ZorxEventBus {

    private val listeners =
        mutableListOf<ZorxEventListener>()

    fun register(
        listener: ZorxEventListener
    ) {

        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun unregister(
        listener: ZorxEventListener
    ) {

        listeners.remove(listener)
    }

    fun post(
        event: ZorxEvent
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