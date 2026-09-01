package com.zorx.launcher.spatial

import android.content.Context

enum class DesktopGridProfile(
    val columns: Int,
    val rows: Int,
    val gapDp: Int,
    val paddingDp: Int
) {
    COMFORTABLE(columns = 12, rows = 8, gapDp = 16, paddingDp = 16),
    COMPACT(columns = 12, rows = 10, gapDp = 10, paddingDp = 10)
}

object DesktopGridSettingsStore {
    private const val PREFERENCES = "zorx_desktop_grid"
    private const val KEY_PROFILE = "profile"

    fun read(context: Context): DesktopGridProfile = runCatching {
        DesktopGridProfile.valueOf(
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getString(KEY_PROFILE, DesktopGridProfile.COMFORTABLE.name)!!
        )
    }.getOrDefault(DesktopGridProfile.COMFORTABLE)

    fun save(context: Context, profile: DesktopGridProfile) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putString(KEY_PROFILE, profile.name).apply()
    }

    fun spec(context: Context, workArea: SpatialBounds): DesktopGridSpec {
        val profile = read(context)
        val density = context.resources.displayMetrics.density
        return DesktopGridSpec(
            workArea = workArea,
            columns = profile.columns,
            rows = profile.rows,
            gap = (profile.gapDp * density).toInt(),
            padding = (profile.paddingDp * density).toInt()
        )
    }
}
