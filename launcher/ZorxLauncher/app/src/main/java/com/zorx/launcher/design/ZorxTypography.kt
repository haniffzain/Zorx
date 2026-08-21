package com.zorx.launcher.design

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Persisted typography preferences for the Zorx desktop shell.
 * Per-surface values are expressed in sp; globalFontScale is applied once.
 */
data class ZorxTypographySettings(
    val globalFontScale: Float = 1f,
    val interfaceTextSp: Float = 13f,
    val titlebarTextSp: Float = 13f,
    val startMenuTextSp: Float = 13f,
    val appDrawerTextSp: Float = 13f,
    val taskbarTextSp: Float = 13f,
    val widgetTextSp: Float = 13f
)

object ZorxTypography {
    const val MIN_SCALE = 0.75f
    const val MAX_SCALE = 1.50f
    const val MIN_TEXT_SP = 10f
    const val MAX_TEXT_SP = 24f

    fun effectiveSp(
        settings: ZorxTypographySettings,
        baseTextSp: Float
    ): Float =
        baseTextSp.coerceIn(MIN_TEXT_SP, MAX_TEXT_SP) *
            settings.globalFontScale.coerceIn(MIN_SCALE, MAX_SCALE)

    fun effectivePx(
        context: Context,
        settings: ZorxTypographySettings,
        baseTextSp: Float
    ): Float =
        effectiveSp(settings, baseTextSp) *
            context.resources.displayMetrics.scaledDensity

    fun applyToViewTree(
        root: View,
        context: Context,
        settings: ZorxTypographySettings,
        baseTextSp: Float
    ) {
        if (root is TextView) {
            root.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                effectivePx(context, settings, baseTextSp)
            )
        }

        if (root is ViewGroup) {
            for (index in 0 until root.childCount) {
                applyToViewTree(
                    root.getChildAt(index),
                    context,
                    settings,
                    baseTextSp
                )
            }
        }
    }
}
