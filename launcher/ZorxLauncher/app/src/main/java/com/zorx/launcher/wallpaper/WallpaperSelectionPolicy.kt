package com.zorx.launcher.wallpaper

object WallpaperSelectionPolicy {
    fun select(
        scope: ZorxWallpaperScope,
        global: ZorxWallpaper,
        workspace: ZorxWallpaper,
        workspaceAssigned: Boolean
    ): ZorxWallpaper = if (
        scope == ZorxWallpaperScope.CURRENT_WORKSPACE && workspaceAssigned
    ) workspace else global
}
