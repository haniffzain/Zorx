package com.zorx.launcher.spatial

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DesktopLayoutScopeTest {
    @Test
    fun `scope requires both workspace and display identity`() {
        val scope = DesktopLayoutScope(2, "display-a")
        assertTrue(scope.matches(2, "display-a"))
        assertFalse(scope.matches(1, "display-a"))
        assertFalse(scope.matches(2, "display-b"))
    }
}
