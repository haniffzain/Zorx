package com.zorx.launcher.shell

import android.content.Context
import kotlin.math.roundToInt

object ZorxShellSettingsStore {
    private const val PREFS = "zorx_shell_settings"
    private val listeners = mutableSetOf<() -> Unit>()

    fun readShell(context: Context): ZorxShellSettings {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return ZorxShellSettings(
            titlebarHeightDp = p.getFloat("titlebar_height", 23f),
            taskbarHeightDp = p.getFloat("taskbar_height", 23f),
            taskbarWidthFraction = p.getFloat("taskbar_width", 0.92f),
            startMenuHeightDp = p.getFloat("start_menu_height", 500f),
            appDrawerHeightFraction = p.getFloat("app_drawer_height", 1f),
            generalUiScale = p.getFloat("ui_scale", 1f)
        )
    }

    fun readAppearance(context: Context): ZorxAppearanceSettings {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return ZorxAppearanceSettings(
            taskbarShape = enumValue(p.getString("taskbar_shape", null), ShellShape.PILL),
            menuCornerStyle = enumValue(p.getString("menu_corner", null), CornerStyle.LARGE),
            windowCornerStyle = enumValue(p.getString("window_corner", null), CornerStyle.MEDIUM),
            widgetCornerStyle = enumValue(p.getString("widget_corner", null), CornerStyle.LARGE)
        )
    }

    private inline fun <reified T : Enum<T>> enumValue(value: String?, fallback: T): T =
        runCatching { enumValueOf<T>(value ?: "") }.getOrDefault(fallback)

    fun save(context: Context, shell: ZorxShellSettings, appearance: ZorxAppearanceSettings) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putFloat("titlebar_height", shell.titlebarHeightDp.coerceIn(23f, 72f))
            .putFloat("taskbar_height", shell.taskbarHeightDp.coerceIn(23f, 96f))
            .putFloat("taskbar_width", shell.taskbarWidthFraction.coerceIn(0.5f, 1f))
            .putFloat("start_menu_height", shell.startMenuHeightDp.coerceIn(360f, 760f))
            .putFloat("app_drawer_height", shell.appDrawerHeightFraction.coerceIn(0.5f, 1f))
            .putFloat("ui_scale", shell.generalUiScale.coerceIn(0.75f, 1.5f))
            .putString("taskbar_shape", appearance.taskbarShape.name)
            .putString("menu_corner", appearance.menuCornerStyle.name)
            .putString("window_corner", appearance.windowCornerStyle.name)
            .putString("widget_corner", appearance.widgetCornerStyle.name)
            .apply()
        listeners.toList().forEach { it() }
    }

    fun addListener(listener: () -> Unit) { listeners.add(listener) }
    fun removeListener(listener: () -> Unit) { listeners.remove(listener) }

    fun resolve(context: Context, widthPx: Int, heightPx: Int): ZorxShellMetrics {
        val shell = readShell(context)
        val appearance = readAppearance(context)
        val density = context.resources.displayMetrics.density
        val scale = shell.generalUiScale
        fun px(dp: Float) = (dp * density * scale).roundToInt()
        fun radius(style: CornerStyle) = px(when (style) {
            CornerStyle.LARGE -> 20f
            CornerStyle.MEDIUM -> 18f
            CornerStyle.SMALL -> 10f
            CornerStyle.SQUARE -> 0f
        }).toFloat()
        val taskbarHeight = px(shell.taskbarHeightDp)
        val bottomMargin = px(18f)
        return ZorxShellMetrics(
            titlebarHeightPx = px(shell.titlebarHeightDp).toFloat(),
            titlebarHitHeightPx = px(shell.titlebarHeightDp).coerceAtLeast(23),
            taskbarHeightPx = taskbarHeight,
            taskbarWidthPx = (widthPx * shell.taskbarWidthFraction).roundToInt(),
            taskbarBottomMarginPx = bottomMargin,
            startMenuWidthPx = px(360f).coerceAtMost((widthPx * .8f).roundToInt()),
            startMenuHeightPx = px(shell.startMenuHeightDp).coerceAtMost((heightPx * .9f).roundToInt()),
            startMenuBottomMarginPx = taskbarHeight + bottomMargin + px(6f),
            appDrawerHeightPx = (heightPx * shell.appDrawerHeightFraction).roundToInt(),
            appDrawerBottomInsetPx = taskbarHeight + bottomMargin + px(10f),
            windowRadiusPx = radius(appearance.windowCornerStyle),
            taskbarRadiusPx = when (appearance.taskbarShape) {
                ShellShape.PILL -> taskbarHeight / 2f
                ShellShape.ROUNDED_RECTANGLE -> px(18f).toFloat()
                ShellShape.SQUARE -> 0f
            },
            menuRadiusPx = radius(appearance.menuCornerStyle),
            widgetRadiusPx = radius(appearance.widgetCornerStyle),
            uiScale = scale
        )
    }
}
