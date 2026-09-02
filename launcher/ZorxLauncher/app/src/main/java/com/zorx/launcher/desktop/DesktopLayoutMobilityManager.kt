package com.zorx.launcher.desktop

import android.content.Context
import com.zorx.launcher.desktopicons.DesktopShortcutStore
import com.zorx.launcher.spatial.DesktopGridPlacement
import com.zorx.launcher.spatial.DesktopLayoutMobilityPlanner
import com.zorx.launcher.spatial.DesktopLayoutMoveItem
import com.zorx.launcher.spatial.DesktopLayoutScope
import com.zorx.launcher.spatial.DesktopPlacementPolicy
import com.zorx.launcher.spatial.GridEngine
import com.zorx.launcher.widgets.WidgetType
import com.zorx.launcher.widgets.ZorxWidgetConfigStore
import com.zorx.launcher.widgets.ZorxWidgetInstance
import com.zorx.launcher.widgets.ZorxWidgetLayoutStore
import java.util.UUID

data class DesktopLayoutMobilityResult(
    val success: Boolean,
    val widgets: Int = 0,
    val shortcuts: Int = 0,
    val reason: String? = null
)

class DesktopLayoutMobilityManager(private val context: Context) {
    fun transfer(
        source: DesktopLayoutScope,
        target: DesktopLayoutScope,
        move: Boolean,
        engine: GridEngine,
        columns: Int,
        rows: Int
    ): DesktopLayoutMobilityResult {
        if (source == target) return DesktopLayoutMobilityResult(false, reason = "Source and destination are the same")

        val widgetStoreItems = ZorxWidgetLayoutStore.read(context)
        val shortcutStore = DesktopShortcutStore(context)
        val shortcutStoreItems = shortcutStore.read()
        val sourceWidgets = widgetStoreItems.filter { source.matches(it.workspaceId, it.displayId) }
        val sourceShortcuts = shortcutStoreItems.filter { source.matches(it.workspaceId, it.displayId) }
        if (sourceWidgets.isEmpty() && sourceShortcuts.isEmpty()) {
            return DesktopLayoutMobilityResult(false, reason = "The current layout is empty")
        }

        val targetWidgets = widgetStoreItems.filter { target.matches(it.workspaceId, it.displayId) }
        val targetShortcuts = shortcutStoreItems.filter { target.matches(it.workspaceId, it.displayId) }
        if (sourceShortcuts.any { incoming -> targetShortcuts.any { it.id == incoming.id } }) {
            return DesktopLayoutMobilityResult(false, reason = "Destination already contains one of these shortcuts")
        }

        val occupied = targetWidgets.map(::widgetPlacement) + targetShortcuts.map { it.placement }
        val incoming = sourceWidgets.map {
            DesktopLayoutMoveItem("widget:${it.instanceId}", widgetPlacement(it), columnStep = 3, maximumRows = 8)
        } + sourceShortcuts.map {
            DesktopLayoutMoveItem("shortcut:${it.scopedId}", it.placement)
        }
        val plan = DesktopLayoutMobilityPlanner.plan(engine, incoming, occupied, columns, rows)
            ?: return DesktopLayoutMobilityResult(false, reason = "Destination does not have enough free grid space")

        val transferredWidgets = sourceWidgets.map { widget ->
            val placement = plan.getValue("widget:${widget.instanceId}")
            widget.copy(
                instanceId = if (move) widget.instanceId else "${widget.widgetType.name.lowercase()}-${UUID.randomUUID()}",
                gridX = placement.column / 3,
                gridY = placement.row,
                workspaceId = target.workspaceId,
                displayId = target.displayId
            )
        }
        val transferredShortcuts = sourceShortcuts.map { shortcut ->
            shortcut.copy(
                placement = plan.getValue("shortcut:${shortcut.scopedId}"),
                workspaceId = target.workspaceId,
                displayId = target.displayId
            )
        }

        val keptWidgets = if (move) widgetStoreItems.filterNot { source.matches(it.workspaceId, it.displayId) } else widgetStoreItems
        val keptShortcuts = if (move) shortcutStoreItems.filterNot { source.matches(it.workspaceId, it.displayId) } else shortcutStoreItems
        ZorxWidgetLayoutStore.save(context, keptWidgets + transferredWidgets)
        shortcutStore.save(keptShortcuts + transferredShortcuts)

        if (!move) copyWidgetConfiguration(sourceWidgets, transferredWidgets)
        return DesktopLayoutMobilityResult(true, transferredWidgets.size, transferredShortcuts.size)
    }

    private fun widgetPlacement(widget: ZorxWidgetInstance): DesktopGridPlacement =
        DesktopPlacementPolicy.legacyWidgetPlacement(
            widget.gridX,
            widget.gridY,
            widget.gridWidth,
            widget.gridHeight
        )

    private fun copyWidgetConfiguration(source: List<ZorxWidgetInstance>, copies: List<ZorxWidgetInstance>) {
        source.zip(copies).forEach { (from, to) ->
            if (from.widgetType == WidgetType.NOTES) {
                ZorxWidgetConfigStore.saveNote(context, to.instanceId, ZorxWidgetConfigStore.note(context, from.instanceId))
            }
        }
    }
}
