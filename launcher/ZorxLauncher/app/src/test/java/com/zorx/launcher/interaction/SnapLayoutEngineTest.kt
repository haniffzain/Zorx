package com.zorx.launcher.interaction

import com.zorx.launcher.spatial.SpatialBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SnapLayoutEngineTest {
    private val area = SpatialBounds(10, 20, 1001, 701)

    @Test
    fun `half layouts cover work area without overlap`() {
        val left = SnapLayoutEngine.bounds(area, SnapSlot.LEFT_HALF)
        val right = SnapLayoutEngine.bounds(area, SnapSlot.RIGHT_HALF)

        assertEquals(500, left.width)
        assertEquals(501, right.width)
        assertEquals(left.x + left.width, right.x)
        assertEquals(area.width, left.width + right.width)
    }

    @Test
    fun `quarter layouts preserve odd remainder pixels`() {
        val topLeft = SnapLayoutEngine.bounds(area, SnapSlot.TOP_LEFT)
        val bottomRight = SnapLayoutEngine.bounds(area, SnapSlot.BOTTOM_RIGHT)

        assertEquals(SpatialBounds(10, 20, 500, 350), topLeft)
        assertEquals(SpatialBounds(510, 370, 501, 351), bottomRight)
    }

    @Test
    fun `every slot remains inside offset work area`() {
        SnapSlot.values().forEach { slot ->
            val bounds = SnapLayoutEngine.bounds(area, slot)
            assertTrue(bounds.x >= area.x)
            assertTrue(bounds.y >= area.y)
            assertTrue(bounds.x + bounds.width <= area.x + area.width)
            assertTrue(bounds.y + bounds.height <= area.y + area.height)
        }
    }
}
