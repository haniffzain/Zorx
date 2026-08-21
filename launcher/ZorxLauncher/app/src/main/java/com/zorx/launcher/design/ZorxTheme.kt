package com.zorx.launcher.design

import android.content.Context
import android.graphics.Color

enum class ZorxThemePreset { ZORX_DARK, GRAPHITE, MIDNIGHT, OCEAN, EMERALD, VIOLET, AMBER, MONOCHROME, CUSTOM }
data class ZorxTheme(
    val desktopBackground: Int, val surfaceBackground: Int, val panelBackground: Int, val borderColor: Int,
    val primaryAccent: Int, val secondaryAccent: Int, val textPrimary: Int, val textSecondary: Int,
    val buttonColor: Int, val buttonHoverColor: Int, val buttonPressedColor: Int,
    val activeWindowBorder: Int, val inactiveWindowBorder: Int, val widgetAccent: Int
)
object ZorxThemeManager {
    private var theme = dark(Color.parseColor("#3ED6D0"))
    private var preset = ZorxThemePreset.ZORX_DARK
    private val listeners = mutableSetOf<() -> Unit>()
    fun current() = theme
    fun currentPreset() = preset
    fun load(context: Context) { val p=context.getSharedPreferences("zorx_theme",0); preset=runCatching { ZorxThemePreset.valueOf(p.getString("preset", "ZORX_DARK")!!) }.getOrDefault(ZorxThemePreset.ZORX_DARK); theme=dark(p.getInt("accent", accentFor(preset))); notifyChanged() }
    fun apply(context: Context, next: ZorxTheme, nextPreset: ZorxThemePreset = ZorxThemePreset.CUSTOM) { theme=next; preset=nextPreset; context.getSharedPreferences("zorx_theme",0).edit().putString("preset",preset.name).putInt("accent",theme.primaryAccent).apply(); notifyChanged() }
    fun applyPreset(context: Context, value: ZorxThemePreset) = apply(context, dark(accentFor(value)), value)
    fun reset(context: Context) = applyPreset(context, ZorxThemePreset.ZORX_DARK)
    fun addListener(listener: () -> Unit) { listeners.add(listener) }; fun removeListener(listener: () -> Unit) { listeners.remove(listener) }
    private fun notifyChanged() = listeners.toList().forEach { it() }
    private fun accentFor(value: ZorxThemePreset) = when(value) { ZorxThemePreset.OCEAN->Color.parseColor("#36A9E8"); ZorxThemePreset.EMERALD->Color.parseColor("#38C98B"); ZorxThemePreset.VIOLET->Color.parseColor("#A57CFF"); ZorxThemePreset.AMBER->Color.parseColor("#FFB547"); ZorxThemePreset.MONOCHROME->Color.WHITE; ZorxThemePreset.GRAPHITE->Color.parseColor("#AAB2BF"); ZorxThemePreset.MIDNIGHT->Color.parseColor("#526BFF"); else->Color.parseColor("#3ED6D0") }
    private fun dark(accent: Int) = ZorxTheme(Color.parseColor("#101114"),Color.parseColor("#1A1C20"),Color.parseColor("#14161C"),Color.parseColor("#2D3036"),accent,Color.parseColor("#7A5CFA"),Color.parseColor("#F6F6F6"),Color.parseColor("#B6B6B6"),Color.parseColor("#252A33"),Color.parseColor("#303846"),Color.parseColor("#151A22"),accent,Color.parseColor("#59606B"),accent)
}
