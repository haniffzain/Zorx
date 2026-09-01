package com.zorx.launcher.interaction

import com.zorx.launcher.spatial.SpatialBounds
import org.junit.Assert.assertEquals
import org.junit.Test

class WindowGroupLayoutEngineTest {
    private val area = SpatialBounds(10, 20, 1001, 701)

    @Test
    fun `two columns reuse gapless half slots`() {
        assertEquals(
            listOf(SpatialBounds(10, 20, 500, 701), SpatialBounds(510, 20, 501, 701)),
            WindowGroupLayoutEngine.bounds(area, WindowGroupLayout.TWO_COLUMNS)
        )
    }

    @Test
    fun `three columns preserve remainder in final column`() {
        assertEquals(
            listOf(
                SpatialBounds(10, 20, 333, 701),
                SpatialBounds(343, 20, 333, 701),
                SpatialBounds(676, 20, 335, 701)
            ),
            WindowGroupLayoutEngine.bounds(area, WindowGroupLayout.THREE_COLUMNS)
        )
    }

    @Test
    fun `main and stack arrangement covers both right quarters`() {
        assertEquals(
            listOf(
                SpatialBounds(10, 20, 500, 701),
                SpatialBounds(510, 20, 501, 350),
                SpatialBounds(510, 370, 501, 351)
            ),
            WindowGroupLayoutEngine.bounds(area, WindowGroupLayout.MAIN_LEFT_STACKED_RIGHT)
        )
    }
}
