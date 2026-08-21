package com.zorx.launcher.taskbar

import android.content.Context
import com.zorx.launcher.display.ZorxDisplayId
import com.zorx.launcher.display.ZorxDisplayManager
import com.zorx.launcher.shell.ZorxShellSettingsStore
import com.zorx.launcher.spatial.DesktopObject
import com.zorx.launcher.windowing.ZorxWindowLocationManager

/** Future-ready taskbar placement policy. Phase 6D renders one primary taskbar. */
enum class ZorxTaskbarDisplayPolicy { PRIMARY_ONLY, PER_DISPLAY, MIRRORED }

object ZorxTaskbarPolicyStore {
    private const val PREF = "zorx_taskbar_policy"
    fun read(context: Context): ZorxTaskbarDisplayPolicy = runCatching {
        ZorxTaskbarDisplayPolicy.valueOf(context.getSharedPreferences(PREF, 0).getString("display_policy", ZorxTaskbarDisplayPolicy.PRIMARY_ONLY.name)!!)
    }.getOrDefault(ZorxTaskbarDisplayPolicy.PRIMARY_ONLY)
    fun save(context: Context, policy: ZorxTaskbarDisplayPolicy) {
        context.getSharedPreferences(PREF, 0).edit().putString("display_policy", policy.name).apply()
    }
}

/** Active display follows focused-window location, with primary-display fallback. */
object ZorxActiveDisplayResolver {
    fun displayFor(context: Context, windows: List<DesktopObject>): ZorxDisplayId? {
        val topology = ZorxDisplayManager(context).topology(ZorxShellSettingsStore.readDisplay(context).displayScale)
        val primary = topology.primary() ?: return null
        val focused = windows.firstOrNull { it.state.name == "FOCUSED" }
        return focused?.let { ZorxWindowLocationManager.ensure(context, it, topology)?.displayId } ?: primary.id
    }
}
