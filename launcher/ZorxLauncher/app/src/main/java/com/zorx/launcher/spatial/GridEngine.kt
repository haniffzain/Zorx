package com.zorx.launcher.spatial

import kotlin.math.abs
import kotlin.math.min

data class DesktopGridSpec(
    val workArea: SpatialBounds,
    val columns: Int = 12,
    val rows: Int = 8,
    val gap: Int = 16,
    val padding: Int = 16
) {
    init {
        require(columns > 0 && rows > 0)
        require(gap >= 0 && padding >= 0)
        require(workArea.width > padding * 2 + gap * (columns - 1))
        require(workArea.height > padding * 2 + gap * (rows - 1))
    }
}

data class DesktopGridPlacement(
    val column: Int,
    val row: Int,
    val columnSpan: Int = 1,
    val rowSpan: Int = 1
)

class GridEngine(private val spec: DesktopGridSpec) {
    fun bounds(placement: DesktopGridPlacement): SpatialBounds {
        require(fits(placement))
        val left = columnStart(placement.column)
        val top = rowStart(placement.row)
        val right = columnStart(placement.column + placement.columnSpan - 1) +
            columnWidth(placement.column + placement.columnSpan - 1)
        val bottom = rowStart(placement.row + placement.rowSpan - 1) +
            rowHeight(placement.row + placement.rowSpan - 1)
        return SpatialBounds(left, top, right - left, bottom - top)
    }

    fun nearestColumn(x: Int, columnSpan: Int = 1): Int {
        require(columnSpan in 1..spec.columns)
        return (0..spec.columns - columnSpan).minByOrNull { abs(columnStart(it) - x) } ?: 0
    }

    fun nearestRow(y: Int, rowSpan: Int = 1): Int {
        require(rowSpan in 1..spec.rows)
        return (0..spec.rows - rowSpan).minByOrNull { abs(rowStart(it) - y) } ?: 0
    }

    fun fits(placement: DesktopGridPlacement): Boolean =
        placement.column >= 0 && placement.row >= 0 &&
            placement.columnSpan > 0 && placement.rowSpan > 0 &&
            placement.column + placement.columnSpan <= spec.columns &&
            placement.row + placement.rowSpan <= spec.rows

    fun overlaps(first: DesktopGridPlacement, second: DesktopGridPlacement): Boolean =
        first.column < second.column + second.columnSpan &&
            first.column + first.columnSpan > second.column &&
            first.row < second.row + second.rowSpan &&
            first.row + first.rowSpan > second.row

    fun firstAvailable(
        columnSpan: Int,
        rowSpan: Int,
        occupied: List<DesktopGridPlacement>
    ): DesktopGridPlacement? {
        if (columnSpan !in 1..spec.columns || rowSpan !in 1..spec.rows) return null
        for (row in 0..spec.rows - rowSpan) {
            for (column in 0..spec.columns - columnSpan) {
                val candidate = DesktopGridPlacement(column, row, columnSpan, rowSpan)
                if (occupied.none { overlaps(candidate, it) }) return candidate
            }
        }
        return null
    }

    private fun columnStart(column: Int): Int =
        spec.workArea.x + spec.padding + column * baseColumnWidth() +
            min(column, columnRemainder()) + column * spec.gap

    private fun rowStart(row: Int): Int =
        spec.workArea.y + spec.padding + row * baseRowHeight() +
            min(row, rowRemainder()) + row * spec.gap

    private fun columnWidth(column: Int) = baseColumnWidth() + if (column < columnRemainder()) 1 else 0
    private fun rowHeight(row: Int) = baseRowHeight() + if (row < rowRemainder()) 1 else 0
    private fun usableWidth() = spec.workArea.width - spec.padding * 2 - spec.gap * (spec.columns - 1)
    private fun usableHeight() = spec.workArea.height - spec.padding * 2 - spec.gap * (spec.rows - 1)
    private fun baseColumnWidth() = usableWidth() / spec.columns
    private fun baseRowHeight() = usableHeight() / spec.rows
    private fun columnRemainder() = usableWidth() % spec.columns
    private fun rowRemainder() = usableHeight() % spec.rows
}
