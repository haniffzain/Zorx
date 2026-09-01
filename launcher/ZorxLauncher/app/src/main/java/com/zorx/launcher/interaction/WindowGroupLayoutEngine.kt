package com.zorx.launcher.interaction

import com.zorx.launcher.spatial.SpatialBounds

enum class WindowGroupLayout(val label: String, val windowCount: Int) {
    TWO_COLUMNS("Two columns", 2),
    THREE_COLUMNS("Three columns", 3),
    MAIN_LEFT_STACKED_RIGHT("Main left + two right", 3)
}

object WindowGroupLayoutEngine {
    fun bounds(workArea: SpatialBounds, layout: WindowGroupLayout): List<SpatialBounds> {
        return when (layout) {
            WindowGroupLayout.TWO_COLUMNS -> listOf(
                SnapLayoutEngine.bounds(workArea, SnapSlot.LEFT_HALF),
                SnapLayoutEngine.bounds(workArea, SnapSlot.RIGHT_HALF)
            )

            WindowGroupLayout.THREE_COLUMNS -> columns(workArea, 3)

            WindowGroupLayout.MAIN_LEFT_STACKED_RIGHT -> listOf(
                SnapLayoutEngine.bounds(workArea, SnapSlot.LEFT_HALF),
                SnapLayoutEngine.bounds(workArea, SnapSlot.TOP_RIGHT),
                SnapLayoutEngine.bounds(workArea, SnapSlot.BOTTOM_RIGHT)
            )
        }
    }

    private fun columns(workArea: SpatialBounds, count: Int): List<SpatialBounds> {
        val baseWidth = workArea.width / count
        var x = workArea.x
        return List(count) { index ->
            val width = if (index == count - 1) {
                workArea.x + workArea.width - x
            } else baseWidth
            SpatialBounds(x, workArea.y, width, workArea.height).also { x += width }
        }
    }
}
