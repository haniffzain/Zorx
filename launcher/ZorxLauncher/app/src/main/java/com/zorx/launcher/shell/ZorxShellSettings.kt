package com.zorx.launcher.shell

enum class ShellShape { PILL, ROUNDED_RECTANGLE, SQUARE }
enum class CornerStyle { LARGE, MEDIUM, SMALL, SQUARE }

data class ZorxShellSettings(
    val titlebarHeightDp: Float = 23f,
    val taskbarHeightDp: Float = 23f,
    val taskbarWidthFraction: Float = 0.92f,
    val startMenuHeightDp: Float = 500f,
    val appDrawerHeightFraction: Float = 1f,
    val generalUiScale: Float = 1f
)

data class ZorxAppearanceSettings(
    val taskbarShape: ShellShape = ShellShape.PILL,
    val menuCornerStyle: CornerStyle = CornerStyle.LARGE,
    val windowCornerStyle: CornerStyle = CornerStyle.MEDIUM,
    val widgetCornerStyle: CornerStyle = CornerStyle.LARGE
)

data class ZorxShellMetrics(
    val titlebarHeightPx: Float,
    val titlebarHitHeightPx: Int,
    val taskbarHeightPx: Int,
    val taskbarWidthPx: Int,
    val taskbarBottomMarginPx: Int,
    val startMenuWidthPx: Int,
    val startMenuHeightPx: Int,
    val startMenuBottomMarginPx: Int,
    val appDrawerHeightPx: Int,
    val appDrawerBottomInsetPx: Int,
    val windowRadiusPx: Float,
    val taskbarRadiusPx: Float,
    val menuRadiusPx: Float,
    val widgetRadiusPx: Float,
    val uiScale: Float
)
