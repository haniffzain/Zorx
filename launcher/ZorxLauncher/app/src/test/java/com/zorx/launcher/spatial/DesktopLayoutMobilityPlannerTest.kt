package com.zorx.launcher.spatial

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DesktopLayoutMobilityPlannerTest {
    private val engine = GridEngine(DesktopGridSpec(SpatialBounds(0, 0, 1200, 800), gap = 10, padding = 10))

    @Test
    fun `keeps requested placement when destination is free`() {
        val item = DesktopLayoutMoveItem("shortcut", DesktopGridPlacement(4, 2))
        assertEquals(item.placement, DesktopLayoutMobilityPlanner.plan(engine, listOf(item), emptyList(), 12, 8)?.get(item.id))
    }

    @Test
    fun `widget relocation stays on legacy column boundaries`() {
        val item = DesktopLayoutMoveItem("widget", DesktopGridPlacement(0, 0, 6, 1), columnStep = 3, maximumRows = 8)
        val plan = DesktopLayoutMobilityPlanner.plan(
            engine,
            listOf(item),
            listOf(DesktopGridPlacement(0, 0, 6, 1)),
            12,
            8
        )
        assertEquals(DesktopGridPlacement(6, 0, 6, 1), plan?.get(item.id))
    }

    @Test
    fun `returns no plan instead of partially moving a full layout`() {
        val incoming = listOf(
            DesktopLayoutMoveItem("a", DesktopGridPlacement(0, 0, 12, 8)),
            DesktopLayoutMoveItem("b", DesktopGridPlacement(0, 0))
        )
        assertNull(DesktopLayoutMobilityPlanner.plan(engine, incoming, emptyList(), 12, 8))
    }

    @Test
    fun `rejects duplicate identifiers without overwriting a placement`() {
        val items = listOf(
            DesktopLayoutMoveItem("duplicate", DesktopGridPlacement(0, 0)),
            DesktopLayoutMoveItem("duplicate", DesktopGridPlacement(1, 0))
        )
        assertNull(DesktopLayoutMobilityPlanner.plan(engine, items, emptyList(), 12, 8))
    }

    @Test
    fun `realigns an invalid legacy widget column`() {
        val item = DesktopLayoutMoveItem("widget", DesktopGridPlacement(1, 0, 3, 1), columnStep = 3)
        assertEquals(
            DesktopGridPlacement(0, 0, 3, 1),
            DesktopLayoutMobilityPlanner.plan(engine, listOf(item), emptyList(), 12, 8)?.get(item.id)
        )
    }

    @Test
    fun `failed planning does not mutate supplied occupancy`() {
        val occupied = mutableListOf(DesktopGridPlacement(0, 0, 12, 8))
        val before = occupied.toList()
        assertNull(DesktopLayoutMobilityPlanner.plan(
            engine, listOf(DesktopLayoutMoveItem("shortcut", DesktopGridPlacement(0, 0))), occupied, 12, 8
        ))
        assertEquals(before, occupied)
    }
}
