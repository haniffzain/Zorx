package com.zorx.launcher.spatial

import android.content.Context
import com.zorx.launcher.workspace.ZorxWorkspaceManager

data class DesktopLayoutScope(
    val workspaceId: Int,
    val displayId: String
) {
    fun matches(workspaceId: Int, displayId: String): Boolean =
        this.workspaceId == workspaceId && this.displayId == displayId

    companion object {
        const val PRIMARY_DISPLAY = "primary"
        val LEGACY_DEFAULT = DesktopLayoutScope(workspaceId = 1, displayId = PRIMARY_DISPLAY)
    }
}

object DesktopLayoutScopeResolver {
    fun current(context: Context): DesktopLayoutScope = DesktopLayoutScope(
        workspaceId = ZorxWorkspaceManager.active(context).value,
        displayId = DesktopLayoutScope.PRIMARY_DISPLAY
    )
}
