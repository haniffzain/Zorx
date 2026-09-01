package com.zorx.launcher.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Test

class WallpaperSelectionPolicyTest {
    private val global = ZorxWallpaper(solidColor = 1)
    private val workspace = ZorxWallpaper(solidColor = 2)

    @Test
    fun `all workspace scope always selects global wallpaper`() {
        assertEquals(
            global,
            WallpaperSelectionPolicy.select(ZorxWallpaperScope.ALL_WORKSPACES, global, workspace, true)
        )
    }

    @Test
    fun `current workspace scope uses assigned wallpaper`() {
        assertEquals(
            workspace,
            WallpaperSelectionPolicy.select(ZorxWallpaperScope.CURRENT_WORKSPACE, global, workspace, true)
        )
    }

    @Test
    fun `current workspace scope falls back to global wallpaper`() {
        assertEquals(
            global,
            WallpaperSelectionPolicy.select(ZorxWallpaperScope.CURRENT_WORKSPACE, global, workspace, false)
        )
    }
}
