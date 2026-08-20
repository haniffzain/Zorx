package com.zorx.launcher.shell

enum class ShellPanelState {
    CLOSED,
    OPEN,
    MINIMIZED
}

object ZorxShellPanelManager {
    private val listeners = mutableSetOf<() -> Unit>()

    var displaySettingsState: ShellPanelState =
        ShellPanelState.CLOSED
        private set

    fun setDisplaySettingsState(
        state: ShellPanelState
    ) {
        displaySettingsState = state
        listeners.toList().forEach { it() }
    }

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }
}
