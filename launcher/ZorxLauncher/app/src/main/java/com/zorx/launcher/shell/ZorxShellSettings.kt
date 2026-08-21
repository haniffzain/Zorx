package com.zorx.launcher.shell

enum class ShellShape { PILL, ROUNDED, SQUARE }
enum class CornerStyle { ROUNDED, SQUARE }

data class ZorxShellSettings(
    val titlebarHeightDp: Float = 32f,
    val taskbarHeightDp: Float = 44f,
    val taskbarWidthFraction: Float = 0.92f,
    val startMenuHeightDp: Float = 500f,
    val appDrawerWidthFraction: Float = 0.62f,
    val appDrawerHeightFraction: Float = 0.72f,
    val applicationIconSizeDp: Float = 72f,
    val taskbarIconSizeDp: Float = 32f,
    val generalUiScale: Float = 1f
)

data class ZorxAppearanceSettings(
    val taskbarShape: ShellShape = ShellShape.PILL,
    val menuCornerStyle: CornerStyle = CornerStyle.ROUNDED,
    val windowCornerStyle: CornerStyle = CornerStyle.ROUNDED,
    val widgetCornerStyle: CornerStyle = CornerStyle.ROUNDED
)

data class ZorxDisplaySettings(val displayScale: Float = 1f)

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
    val appDrawerWidthPx: Int,
    val applicationIconSizePx: Int,
    val taskbarIconSizePx: Int,
    val windowRadiusPx: Float,
    val taskbarRadiusPx: Float,
    val menuRadiusPx: Float,
    val widgetRadiusPx: Float,
    val uiScale: Float
    , val displayScale: Float
    , val effectiveWorkspaceWidthPx: Int
    , val effectiveWorkspaceHeightPx: Int
)
