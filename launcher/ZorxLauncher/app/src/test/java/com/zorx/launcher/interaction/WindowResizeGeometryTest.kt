package com.zorx.launcher.interaction

import com.zorx.launcher.spatial.SpatialBounds
import org.junit.Assert.assertEquals
import org.junit.Test

class WindowResizeGeometryTest {
    private val start = SpatialBounds(100, 100, 500, 400)
    private val workArea = SpatialBounds(0, 0, 1200, 800)

    @Test
    fun `detects all edge and corner handles`() {
        val expected = mapOf(
            100f to 100f to ResizeDirection.TOP_LEFT,
            350f to 100f to ResizeDirection.TOP,
            600f to 100f to ResizeDirection.TOP_RIGHT,
            100f to 300f to ResizeDirection.LEFT,
            600f to 300f to ResizeDirection.RIGHT,
            100f to 500f to ResizeDirection.BOTTOM_LEFT,
            350f to 500f to ResizeDirection.BOTTOM,
            600f to 500f to ResizeDirection.BOTTOM_RIGHT
        )

        expected.forEach { (point, direction) ->
            assertEquals(direction, WindowResizeGeometry.directionAt(start, point.first, point.second, 20))
        }
    }

    @Test
    fun `resizes top left while keeping opposite edges fixed`() {
        assertEquals(
            SpatialBounds(50, 60, 550, 440),
            WindowResizeGeometry.resize(start, ResizeDirection.TOP_LEFT, -50, -40, workArea, 240, 180)
        )
    }

    @Test
    fun `resizes bottom right`() {
        assertEquals(
            SpatialBounds(100, 100, 650, 500),
            WindowResizeGeometry.resize(start, ResizeDirection.BOTTOM_RIGHT, 150, 100, workArea, 240, 180)
        )
    }

    @Test
    fun `enforces minimum size from left and top`() {
        assertEquals(
            SpatialBounds(360, 320, 240, 180),
            WindowResizeGeometry.resize(start, ResizeDirection.TOP_LEFT, 1000, 1000, workArea, 240, 180)
        )
    }

    @Test
    fun `clamps expansion to work area`() {
        assertEquals(
            SpatialBounds(100, 100, 1100, 700),
            WindowResizeGeometry.resize(start, ResizeDirection.BOTTOM_RIGHT, 2000, 2000, workArea, 240, 180)
        )
    }

    @Test
    fun `constrains moved windows above taskbar and inside desktop`() {
        assertEquals(
            SpatialBounds(700, 400, 500, 400),
            WindowResizeGeometry.constrain(SpatialBounds(1000, 700, 500, 400), workArea)
        )
    }
}
