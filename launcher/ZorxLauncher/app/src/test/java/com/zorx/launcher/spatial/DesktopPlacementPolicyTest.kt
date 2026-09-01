package com.zorx.launcher.spatial

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DesktopPlacementPolicyTest {
    private val engine = GridEngine(DesktopGridSpec(SpatialBounds(0, 0, 1200, 800), gap = 10, padding = 10))

    @Test
    fun `legacy widget units map to shared columns`() {
        assertEquals(
            DesktopGridPlacement(3, 2, 6, 2),
            DesktopPlacementPolicy.legacyWidgetPlacement(1, 2, 2, 2)
        )
    }

    @Test
    fun `widget reservation rejects shortcut collision`() {
        val widget = DesktopPlacementPolicy.legacyWidgetPlacement(0, 0, 2, 1)
        assertFalse(DesktopPlacementPolicy.canPlace(engine, DesktopGridPlacement(4, 0), listOf(widget)))
    }

    @Test
    fun `reconciliation moves collisions deterministically`() {
        val reserved = listOf(DesktopPlacementPolicy.legacyWidgetPlacement(0, 0, 2, 1))
        assertEquals(
            listOf(DesktopGridPlacement(6, 0), DesktopGridPlacement(7, 0)),
            DesktopPlacementPolicy.reconcile(
                engine,
                listOf(DesktopGridPlacement(0, 0), DesktopGridPlacement(0, 0)),
                reserved
            )
        )
    }
}
