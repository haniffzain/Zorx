package com.zorx.launcher.interaction

import com.zorx.launcher.spatial.SpatialBounds

enum class SnapSlot(val label: String) {
    LEFT_HALF("Left half"),
    RIGHT_HALF("Right half"),
    TOP_LEFT("Top-left quarter"),
    TOP_RIGHT("Top-right quarter"),
    BOTTOM_LEFT("Bottom-left quarter"),
    BOTTOM_RIGHT("Bottom-right quarter")
}

object SnapLayoutEngine {
    fun bounds(workArea: SpatialBounds, slot: SnapSlot): SpatialBounds {
        val leftWidth = workArea.width / 2
        val rightWidth = workArea.width - leftWidth
        val topHeight = workArea.height / 2
        val bottomHeight = workArea.height - topHeight
        val rightX = workArea.x + leftWidth
        val bottomY = workArea.y + topHeight

        return when (slot) {
            SnapSlot.LEFT_HALF -> SpatialBounds(workArea.x, workArea.y, leftWidth, workArea.height)
            SnapSlot.RIGHT_HALF -> SpatialBounds(rightX, workArea.y, rightWidth, workArea.height)
            SnapSlot.TOP_LEFT -> SpatialBounds(workArea.x, workArea.y, leftWidth, topHeight)
            SnapSlot.TOP_RIGHT -> SpatialBounds(rightX, workArea.y, rightWidth, topHeight)
            SnapSlot.BOTTOM_LEFT -> SpatialBounds(workArea.x, bottomY, leftWidth, bottomHeight)
            SnapSlot.BOTTOM_RIGHT -> SpatialBounds(rightX, bottomY, rightWidth, bottomHeight)
        }
    }
}
