package com.zorx.launcher.spatial

data class DesktopLayoutMoveItem(
    val id: String,
    val placement: DesktopGridPlacement,
    val columnStep: Int = 1,
    val maximumRows: Int? = null
) {
    init {
        require(columnStep > 0)
        require(maximumRows == null || maximumRows > 0)
    }
}

object DesktopLayoutMobilityPlanner {
    fun plan(
        engine: GridEngine,
        incoming: List<DesktopLayoutMoveItem>,
        occupied: List<DesktopGridPlacement>,
        columns: Int,
        rows: Int
    ): Map<String, DesktopGridPlacement>? {
        if (columns <= 0 || rows <= 0 || incoming.map { it.id }.toSet().size != incoming.size) return null
        val accepted = occupied.toMutableList()
        val result = linkedMapOf<String, DesktopGridPlacement>()
        for (item in incoming) {
            if (item.placement.columnSpan <= 0 || item.placement.rowSpan <= 0) return null
            val rowLimit = minOf(rows, item.maximumRows ?: rows)
            val resolved = item.placement.takeIf {
                it.column % item.columnStep == 0 && it.column + it.columnSpan <= columns &&
                    it.row + it.rowSpan <= rowLimit && DesktopPlacementPolicy.canPlace(engine, it, accepted)
            } ?: firstAvailable(engine, item, accepted, columns, rowLimit)
            if (resolved == null) return null
            result[item.id] = resolved
            accepted += resolved
        }
        return result
    }

    private fun firstAvailable(
        engine: GridEngine,
        item: DesktopLayoutMoveItem,
        occupied: List<DesktopGridPlacement>,
        columns: Int,
        rows: Int
    ): DesktopGridPlacement? {
        if (item.placement.columnSpan > columns || item.placement.rowSpan > rows) return null
        for (row in 0..rows - item.placement.rowSpan) {
            for (column in 0..columns - item.placement.columnSpan step item.columnStep) {
                val candidate = DesktopGridPlacement(
                    column,
                    row,
                    item.placement.columnSpan,
                    item.placement.rowSpan
                )
                if (DesktopPlacementPolicy.canPlace(engine, candidate, occupied)) return candidate
            }
        }
        return null
    }
}
