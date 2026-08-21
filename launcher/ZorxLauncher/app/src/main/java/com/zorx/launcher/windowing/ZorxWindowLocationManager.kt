package com.zorx.launcher.windowing

import android.content.Context
import com.zorx.launcher.display.ZorxDisplayId
import com.zorx.launcher.display.ZorxDisplayTopology
import com.zorx.launcher.display.ZorxTopologyDisplay
import com.zorx.launcher.spatial.DesktopObject
import com.zorx.launcher.spatial.DesktopObjectState
import com.zorx.launcher.spatial.SpatialBounds
import com.zorx.launcher.workspace.ZorxWorkspaceId
import com.zorx.launcher.workspace.ZorxWorkspaceManager

/** A display-local logical rectangle. Android task bounds remain physical. */
data class ZorxLogicalBounds(val x: Int, val y: Int, val width: Int, val height: Int)

data class ZorxWindowLocation(
    val windowId: String,
    val workspaceId: ZorxWorkspaceId,
    val displayId: ZorxDisplayId,
    val logicalBounds: ZorxLogicalBounds,
    val windowState: DesktopObjectState,
    val zOrder: Int
)

/** Explicit conversion boundary between Zorx logical space and native task pixels. */
object ZorxDisplayCoordinates {
    fun physicalToLogical(bounds: SpatialBounds, display: ZorxTopologyDisplay): ZorxLogicalBounds =
        ZorxLogicalBounds(
            (bounds.x / display.scale).toInt(),
            (bounds.y / display.scale).toInt(),
            (bounds.width / display.scale).toInt().coerceAtLeast(1),
            (bounds.height / display.scale).toInt().coerceAtLeast(1)
        )

    fun logicalToNative(bounds: ZorxLogicalBounds, display: ZorxTopologyDisplay): SpatialBounds =
        SpatialBounds(
            (bounds.x * display.scale).toInt(),
            (bounds.y * display.scale).toInt(),
            (bounds.width * display.scale).toInt().coerceAtLeast(1),
            (bounds.height * display.scale).toInt().coerceAtLeast(1)
        )

    fun clampToWorkArea(bounds: ZorxLogicalBounds, display: ZorxTopologyDisplay): ZorxLogicalBounds {
        val maxWidth = display.workArea.width.coerceAtLeast(1)
        val maxHeight = display.workArea.height.coerceAtLeast(1)
        val width = bounds.width.coerceIn(1, maxWidth)
        val height = bounds.height.coerceIn(1, maxHeight)
        return ZorxLogicalBounds(
            bounds.x.coerceIn(0, (maxWidth - width).coerceAtLeast(0)),
            bounds.y.coerceIn(0, (maxHeight - height).coerceAtLeast(0)),
            width,
            height
        )
    }
}

/**
 * Persistent window -> workspace/display mapping. The SpatialEngine owns the one
 * in-memory window object; this store never creates a second representation.
 */
object ZorxWindowLocationManager {
    private const val PREF = "zorx_window_locations"

    private fun key(id: String) = id.hashCode().toString(16)
    private fun prefs(context: Context) = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun ensure(context: Context, window: DesktopObject, topology: ZorxDisplayTopology): ZorxWindowLocation? {
        val primary = topology.primary() ?: return null
        val p = prefs(context)
        val prefix = key(window.id)
        if (!p.contains("$prefix.display")) {
            ZorxWorkspaceManager.assignIfAbsent(context, window.id)
            save(context, window, primary.id, ZorxDisplayCoordinates.physicalToLogical(window.bounds, primary))
        }
        return location(context, window.id, topology)
    }

    fun location(context: Context, windowId: String, topology: ZorxDisplayTopology): ZorxWindowLocation? {
        val primary = topology.primary() ?: return null
        val p = prefs(context)
        val prefix = key(windowId)
        val requestedDisplay = ZorxDisplayId(p.getString("$prefix.display", primary.id.value) ?: primary.id.value)
        val display = topology.displays.firstOrNull { it.id == requestedDisplay } ?: primary
        if (display.id != requestedDisplay) p.edit().putString("$prefix.display", display.id.value).apply()
        return ZorxWindowLocation(
            windowId,
            ZorxWorkspaceManager.workspaceFor(context, windowId),
            display.id,
            ZorxLogicalBounds(
                p.getInt("$prefix.x", 0), p.getInt("$prefix.y", 0),
                p.getInt("$prefix.width", 1).coerceAtLeast(1), p.getInt("$prefix.height", 1).coerceAtLeast(1)
            ),
            runCatching { DesktopObjectState.valueOf(p.getString("$prefix.state", DesktopObjectState.NORMAL.name) ?: DesktopObjectState.NORMAL.name) }
                .getOrDefault(DesktopObjectState.NORMAL),
            p.getInt("$prefix.z", 0)
        )
    }

    fun save(context: Context, window: DesktopObject, displayId: ZorxDisplayId, logicalBounds: ZorxLogicalBounds) {
        val prefix = key(window.id)
        prefs(context).edit()
            .putString("$prefix.display", displayId.value)
            .putInt("$prefix.x", logicalBounds.x).putInt("$prefix.y", logicalBounds.y)
            .putInt("$prefix.width", logicalBounds.width).putInt("$prefix.height", logicalBounds.height)
            .putString("$prefix.state", window.state.name).putInt("$prefix.z", window.zIndex)
            .apply()
    }

    fun moveToWorkspace(context: Context, window: DesktopObject, workspaceId: ZorxWorkspaceId, topology: ZorxDisplayTopology) {
        val current = ensure(context, window, topology) ?: return
        ZorxWorkspaceManager.moveWindowToWorkspace(context, window.id, workspaceId)
        save(context, window, current.displayId, current.logicalBounds)
    }

    fun moveToDisplay(context: Context, window: DesktopObject, displayId: ZorxDisplayId, topology: ZorxDisplayTopology): SpatialBounds? {
        val current = ensure(context, window, topology) ?: return null
        val target = topology.displays.firstOrNull { it.id == displayId } ?: return null
        val targetLogical = if (window.state == DesktopObjectState.MAXIMIZED) {
            ZorxLogicalBounds(0, 0, target.workArea.width, target.workArea.height)
        } else {
            ZorxDisplayCoordinates.clampToWorkArea(current.logicalBounds, target)
        }
        save(context, window, target.id, targetLogical)
        return ZorxDisplayCoordinates.logicalToNative(targetLogical, target)
    }
}
