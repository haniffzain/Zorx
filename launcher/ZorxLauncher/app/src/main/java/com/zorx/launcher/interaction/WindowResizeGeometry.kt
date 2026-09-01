package com.zorx.launcher.interaction

import com.zorx.launcher.spatial.SpatialBounds

enum class ResizeDirection(
    val left: Boolean = false,
    val top: Boolean = false,
    val right: Boolean = false,
    val bottom: Boolean = false
) {
    LEFT(left = true), TOP(top = true), RIGHT(right = true), BOTTOM(bottom = true),
    TOP_LEFT(left = true, top = true), TOP_RIGHT(top = true, right = true),
    BOTTOM_LEFT(left = true, bottom = true), BOTTOM_RIGHT(right = true, bottom = true)
}

object WindowResizeGeometry {
    fun directionAt(bounds: SpatialBounds, x: Float, y: Float, handleSize: Int): ResizeDirection? {
        val nearLeft = x <= bounds.x + handleSize
        val nearRight = x >= bounds.x + bounds.width - handleSize
        val nearTop = y <= bounds.y + handleSize
        val nearBottom = y >= bounds.y + bounds.height - handleSize
        return when {
            nearLeft && nearTop -> ResizeDirection.TOP_LEFT
            nearRight && nearTop -> ResizeDirection.TOP_RIGHT
            nearLeft && nearBottom -> ResizeDirection.BOTTOM_LEFT
            nearRight && nearBottom -> ResizeDirection.BOTTOM_RIGHT
            nearLeft -> ResizeDirection.LEFT
            nearRight -> ResizeDirection.RIGHT
            nearTop -> ResizeDirection.TOP
            nearBottom -> ResizeDirection.BOTTOM
            else -> null
        }
    }

    fun resize(
        start: SpatialBounds,
        direction: ResizeDirection,
        deltaX: Int,
        deltaY: Int,
        workArea: SpatialBounds,
        minimumWidth: Int,
        minimumHeight: Int
    ): SpatialBounds {
        val workRight = workArea.x + workArea.width
        val workBottom = workArea.y + workArea.height
        val startRight = start.x + start.width
        val startBottom = start.y + start.height
        val left = if (direction.left) (start.x + deltaX).coerceIn(workArea.x, startRight - minimumWidth) else start.x
        val right = if (direction.right) (startRight + deltaX).coerceIn(start.x + minimumWidth, workRight) else startRight
        val top = if (direction.top) (start.y + deltaY).coerceIn(workArea.y, startBottom - minimumHeight) else start.y
        val bottom = if (direction.bottom) (startBottom + deltaY).coerceIn(start.y + minimumHeight, workBottom) else startBottom
        return SpatialBounds(left, top, right - left, bottom - top)
    }

    fun constrain(bounds: SpatialBounds, workArea: SpatialBounds): SpatialBounds {
        val width = bounds.width.coerceAtMost(workArea.width)
        val height = bounds.height.coerceAtMost(workArea.height)
        val x = bounds.x.coerceIn(workArea.x, workArea.x + workArea.width - width)
        val y = bounds.y.coerceIn(workArea.y, workArea.y + workArea.height - height)
        return SpatialBounds(x, y, width, height)
    }
}
