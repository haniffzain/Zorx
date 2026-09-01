package com.zorx.launcher.desktopicons

import com.zorx.launcher.spatial.DesktopGridPlacement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DesktopShortcutCodecTest {
    @Test
    fun `round trips desktop shortcut identity and placement`() {
        val shortcut = DesktopShortcut("com.example", "com.example.Main", "Example", DesktopGridPlacement(4, 2))
        assertEquals(listOf(shortcut), DesktopShortcutCodec.decode(DesktopShortcutCodec.encode(listOf(shortcut))))
    }

    @Test
    fun `invalid persisted data fails closed`() {
        assertTrue(DesktopShortcutCodec.decode("not-json").isEmpty())
    }
}
