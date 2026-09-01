package com.zorx.launcher.spatial

object DesktopPlacementPolicy {
    fun overlaps(first: DesktopGridPlacement, second: DesktopGridPlacement): Boolean =
        first.column < second.column + second.columnSpan &&
            first.column + first.columnSpan > second.column &&
            first.row < second.row + second.rowSpan &&
            first.row + first.rowSpan > second.row

    fun legacyWidgetPlacement(
        gridX: Int,
        gridY: Int,
        gridWidth: Int,
        gridHeight: Int
    ) = DesktopGridPlacement(
        column = gridX * 3,
        row = gridY,
        columnSpan = gridWidth * 3,
        rowSpan = gridHeight
    )

    fun canPlace(
        engine: GridEngine,
        candidate: DesktopGridPlacement,
        occupied: List<DesktopGridPlacement>
    ): Boolean = engine.fits(candidate) && occupied.none { overlaps(candidate, it) }

    fun reconcile(
        engine: GridEngine,
        requested: List<DesktopGridPlacement>,
        reserved: List<DesktopGridPlacement>
    ): List<DesktopGridPlacement?> {
        val accepted = reserved.toMutableList()
        return requested.map { placement ->
            val resolved = if (canPlace(engine, placement, accepted)) {
                placement
            } else {
                engine.firstAvailable(placement.columnSpan, placement.rowSpan, accepted)
            }
            resolved?.also { accepted += it }
        }
    }
}
