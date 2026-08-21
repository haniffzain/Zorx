package com.zorx.launcher.shell

import android.content.Context
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.design.ZorxTypographySettings
import kotlin.math.roundToInt

object ZorxShellSettingsStore {
    private const val PREFS = "zorx_shell_settings"
    private const val TITLEBAR_MIN_DP = 23f
    private const val TITLEBAR_MAX_DP = 72f
    private const val TASKBAR_MIN_DP = 23f
    private const val TASKBAR_MAX_DP = 96f
    private val listeners = mutableSetOf<() -> Unit>()

    fun readShell(context: Context): ZorxShellSettings {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return ZorxShellSettings(
            titlebarHeightDp = p.getFloat("titlebar_height", 32f).coerceIn(TITLEBAR_MIN_DP, TITLEBAR_MAX_DP),
            taskbarHeightDp = p.getFloat("taskbar_height", 44f).coerceIn(TASKBAR_MIN_DP, TASKBAR_MAX_DP),
            taskbarWidthFraction = p.getFloat("taskbar_width", 0.92f),
            startMenuHeightDp = p.getFloat("start_menu_height", 500f),
            appDrawerHeightFraction = p.getFloat("app_drawer_height", 1f),
            generalUiScale = p.getFloat("ui_scale", 1f)
        )
    }

    fun readTypography(context: Context): ZorxTypographySettings {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return ZorxTypographySettings(
            globalFontScale = p.getFloat("font_global_scale", 1f)
                .coerceIn(ZorxTypography.MIN_SCALE, ZorxTypography.MAX_SCALE),
            interfaceTextSp = p.getFloat("font_interface_sp", 13f)
                .coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP),
            titlebarTextSp = p.getFloat("font_titlebar_sp", 13f)
                .coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP),
            startMenuTextSp = p.getFloat("font_start_menu_sp", 13f)
                .coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP),
            appDrawerTextSp = p.getFloat("font_app_drawer_sp", 13f)
                .coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP),
            taskbarTextSp = p.getFloat("font_taskbar_sp", 13f)
                .coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP),
            widgetTextSp = p.getFloat("font_widget_sp", 13f)
                .coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP)
        )
    }

    fun readAppearance(context: Context): ZorxAppearanceSettings {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return ZorxAppearanceSettings(
            taskbarShape = shellShape(p.getString("taskbar_shape", null)),
            menuCornerStyle = cornerStyle(p.getString("menu_corner", null)),
            windowCornerStyle = cornerStyle(p.getString("window_corner", null)),
            widgetCornerStyle = cornerStyle(p.getString("widget_corner", null))
        )
    }

    private fun shellShape(value: String?): ShellShape =
        when (value) {
            "ROUNDED_RECTANGLE" -> ShellShape.ROUNDED
            else -> enumValue(value, ShellShape.PILL)
        }

    private fun cornerStyle(value: String?): CornerStyle =
        when (value) {
            "LARGE", "MEDIUM", "SMALL" -> CornerStyle.ROUNDED
            else -> enumValue(value, CornerStyle.ROUNDED)
        }

    private inline fun <reified T : Enum<T>> enumValue(value: String?, fallback: T): T =
        runCatching { enumValueOf<T>(value ?: "") }.getOrDefault(fallback)

    fun save(context: Context, shell: ZorxShellSettings, appearance: ZorxAppearanceSettings, typography: ZorxTypographySettings = readTypography(context)) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putFloat("titlebar_height", shell.titlebarHeightDp.coerceIn(TITLEBAR_MIN_DP, TITLEBAR_MAX_DP))
            .putFloat("taskbar_height", shell.taskbarHeightDp.coerceIn(TASKBAR_MIN_DP, TASKBAR_MAX_DP))
            .putFloat("taskbar_width", shell.taskbarWidthFraction.coerceIn(0.5f, 1f))
            .putFloat("start_menu_height", shell.startMenuHeightDp.coerceIn(360f, 760f))
            .putFloat("app_drawer_height", shell.appDrawerHeightFraction.coerceIn(0.5f, 1f))
            .putFloat("ui_scale", shell.generalUiScale.coerceIn(0.75f, 1.5f))
            .putFloat("font_global_scale", typography.globalFontScale.coerceIn(ZorxTypography.MIN_SCALE, ZorxTypography.MAX_SCALE))
            .putFloat("font_interface_sp", typography.interfaceTextSp.coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP))
            .putFloat("font_titlebar_sp", typography.titlebarTextSp.coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP))
            .putFloat("font_start_menu_sp", typography.startMenuTextSp.coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP))
            .putFloat("font_app_drawer_sp", typography.appDrawerTextSp.coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP))
            .putFloat("font_taskbar_sp", typography.taskbarTextSp.coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP))
            .putFloat("font_widget_sp", typography.widgetTextSp.coerceIn(ZorxTypography.MIN_TEXT_SP, ZorxTypography.MAX_TEXT_SP))
            .putString("taskbar_shape", appearance.taskbarShape.name)
            .putString("menu_corner", appearance.menuCornerStyle.name)
            .putString("window_corner", appearance.windowCornerStyle.name)
            .putString("widget_corner", appearance.widgetCornerStyle.name)
            .apply()
        listeners.toList().forEach { it() }
    }

    fun resetAppearance(context: Context) {
        save(context, ZorxShellSettings(), ZorxAppearanceSettings(), readTypography(context))
    }

    fun resetTypography(context: Context) {
        save(
            context,
            readShell(context),
            readAppearance(context),
            ZorxTypographySettings()
        )
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
            CornerStyle.ROUNDED -> 18f
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
                ShellShape.ROUNDED -> px(18f).toFloat()
                ShellShape.SQUARE -> 0f
            },
            menuRadiusPx = radius(appearance.menuCornerStyle),
            widgetRadiusPx = radius(appearance.widgetCornerStyle),
            uiScale = scale
        )
    }
}
