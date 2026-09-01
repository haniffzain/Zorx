package com.zorx.launcher.desktopicons

import android.content.Context
import com.zorx.launcher.spatial.DesktopGridPlacement
import com.zorx.launcher.spatial.DesktopLayoutScope
import org.json.JSONArray
import org.json.JSONObject

data class DesktopShortcut(
    val packageName: String,
    val activityName: String,
    val label: String,
    val placement: DesktopGridPlacement,
    val workspaceId: Int = DesktopLayoutScope.LEGACY_DEFAULT.workspaceId,
    val displayId: String = DesktopLayoutScope.LEGACY_DEFAULT.displayId
) {
    val id: String get() = "$packageName|$activityName"
    val scopedId: String get() = "$id|$workspaceId|$displayId"
}

class DesktopShortcutStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun read(): List<DesktopShortcut> = DesktopShortcutCodec.decode(
        preferences.getString(KEY_SHORTCUTS, null)
    )

    fun save(shortcuts: List<DesktopShortcut>) {
        preferences.edit().putString(KEY_SHORTCUTS, DesktopShortcutCodec.encode(shortcuts)).apply()
    }

    companion object {
        private const val PREFERENCES = "zorx_desktop_shortcuts"
        private const val KEY_SHORTCUTS = "shortcuts"
    }
}

object DesktopShortcutCodec {
    fun encode(shortcuts: List<DesktopShortcut>): String {
        val array = JSONArray()
        shortcuts.forEach { shortcut ->
            array.put(JSONObject().apply {
                put("package", shortcut.packageName)
                put("activity", shortcut.activityName)
                put("label", shortcut.label)
                put("column", shortcut.placement.column)
                put("row", shortcut.placement.row)
                put("workspace", shortcut.workspaceId)
                put("display", shortcut.displayId)
            })
        }
        return array.toString()
    }

    fun decode(value: String?): List<DesktopShortcut> {
        if (value.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(value)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        DesktopShortcut(
                            packageName = item.getString("package"),
                            activityName = item.getString("activity"),
                            label = item.getString("label"),
                            placement = DesktopGridPlacement(
                                item.getInt("column"),
                                item.getInt("row")
                            ),
                            workspaceId = item.optInt("workspace", DesktopLayoutScope.LEGACY_DEFAULT.workspaceId),
                            displayId = item.optString("display", DesktopLayoutScope.LEGACY_DEFAULT.displayId)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }
}
