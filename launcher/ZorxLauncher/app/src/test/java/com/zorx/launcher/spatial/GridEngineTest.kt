package com.zorx.launcher.spatial

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridEngineTest {
    private val engine = GridEngine(
        DesktopGridSpec(SpatialBounds(0, 0, 1200, 800), columns = 12, rows = 8, gap = 10, padding = 10)
    )

    @Test
    fun `twelve columns fill the padded work area deterministically`() {
        val first = engine.bounds(DesktopGridPlacement(0, 0))
        val last = engine.bounds(DesktopGridPlacement(11, 0))
        assertEquals(10, first.x)
        assertEquals(1190, last.x + last.width)
    }

    @Test
    fun `spans include internal gaps`() {
        val single = engine.bounds(DesktopGridPlacement(0, 0))
        val triple = engine.bounds(DesktopGridPlacement(0, 0, 3, 1))
        assertEquals(289, triple.width)
    }

    @Test
    fun `nearest cell respects placement span`() {
        assertEquals(9, engine.nearestColumn(2000, 3))
        assertEquals(7, engine.nearestRow(2000, 1))
    }

    @Test
    fun `fit and overlap checks are deterministic`() {
        assertTrue(engine.fits(DesktopGridPlacement(9, 6, 3, 2)))
        assertFalse(engine.fits(DesktopGridPlacement(10, 6, 3, 2)))
        assertTrue(engine.overlaps(DesktopGridPlacement(0, 0, 3, 2), DesktopGridPlacement(2, 1, 3, 2)))
        assertFalse(engine.overlaps(DesktopGridPlacement(0, 0), DesktopGridPlacement(1, 0)))
    }

    @Test
    fun `finds first collision-free placement`() {
        val occupied = listOf(DesktopGridPlacement(0, 0, 6, 1), DesktopGridPlacement(6, 0, 6, 1))
        assertEquals(DesktopGridPlacement(0, 1, 3, 1), engine.firstAvailable(3, 1, occupied))
        assertNull(engine.firstAvailable(12, 8, occupied))
    }
}
