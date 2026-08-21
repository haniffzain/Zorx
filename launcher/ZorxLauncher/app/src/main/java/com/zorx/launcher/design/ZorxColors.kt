package com.zorx.launcher.design

import android.graphics.Color

/**
 * Official color palette used across Zorx.
 *
 * UI components should reference these values
 * instead of using hardcoded colors.
 */
object ZorxColors {

    val Primary get() = ZorxThemeManager.current().primaryAccent

    val Secondary =
        Color.parseColor("#7A5CFA")

    val Accent get() = ZorxThemeManager.current().primaryAccent

    val Background get() = ZorxThemeManager.current().desktopBackground

    val Surface get() = ZorxThemeManager.current().surfaceBackground

    val Border get() = ZorxThemeManager.current().borderColor

    val TextPrimary get() = ZorxThemeManager.current().textPrimary

    val TextSecondary get() = ZorxThemeManager.current().textSecondary

    val Success =
        Color.parseColor("#36D67A")

    val Warning =
        Color.parseColor("#FFB547")

    val Error =
        Color.parseColor("#FF5B6E")
}
