package com.zorx.launcher.spatial

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DesktopGridProfileTest {
    @Test
    fun `profiles preserve legacy four column widget mapping`() {
        DesktopGridProfile.values().forEach { profile ->
            assertEquals(12, profile.columns)
            assertTrue(profile.rows >= 8)
        }
    }

    @Test
    fun `compact profile exposes more placement cells`() {
        val comfortable = DesktopGridProfile.COMFORTABLE
        val compact = DesktopGridProfile.COMPACT
        assertTrue(compact.columns * compact.rows > comfortable.columns * comfortable.rows)
        assertTrue(compact.gapDp < comfortable.gapDp)
    }
}
