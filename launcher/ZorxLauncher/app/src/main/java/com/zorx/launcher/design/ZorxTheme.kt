package com.zorx.launcher.design

import android.content.Context
import android.graphics.Color

enum class ZorxThemePreset { ZORX_DARK, GRAPHITE, MIDNIGHT, OCEAN, EMERALD, VIOLET, AMBER, MONOCHROME, HUNT_NORD, HUNT_FOREST, HUNT_SUNSET, HUNT_BLOSSOM, HUNT_COFFEE, CUSTOM }
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
    fun load(context: Context) { val p=context.getSharedPreferences("zorx_theme",0); preset=runCatching { ZorxThemePreset.valueOf(p.getString("preset", "ZORX_DARK")!!) }.getOrDefault(ZorxThemePreset.ZORX_DARK); val base=themeFor(preset); theme=base.copy(desktopBackground=p.getInt("desktop",base.desktopBackground),surfaceBackground=p.getInt("surface",base.surfaceBackground),panelBackground=p.getInt("panel",base.panelBackground),borderColor=p.getInt("border",base.borderColor),primaryAccent=p.getInt("accent",base.primaryAccent),textPrimary=p.getInt("text_primary",base.textPrimary),textSecondary=p.getInt("text_secondary",base.textSecondary),buttonColor=p.getInt("button",base.buttonColor),activeWindowBorder=p.getInt("active_border",base.activeWindowBorder)); notifyChanged() }
    fun apply(context: Context, next: ZorxTheme, nextPreset: ZorxThemePreset = ZorxThemePreset.CUSTOM) { theme=next; preset=nextPreset; context.getSharedPreferences("zorx_theme",0).edit().putString("preset",preset.name).putInt("desktop",theme.desktopBackground).putInt("surface",theme.surfaceBackground).putInt("panel",theme.panelBackground).putInt("border",theme.borderColor).putInt("accent",theme.primaryAccent).putInt("text_primary",theme.textPrimary).putInt("text_secondary",theme.textSecondary).putInt("button",theme.buttonColor).putInt("active_border",theme.activeWindowBorder).apply(); notifyChanged() }
    fun updateCustom(context: Context, transform: (ZorxTheme) -> ZorxTheme) = apply(context, transform(theme), ZorxThemePreset.CUSTOM)
    fun applyPreset(context: Context, value: ZorxThemePreset) = apply(context, themeFor(value), value)
    fun reset(context: Context) = applyPreset(context, ZorxThemePreset.ZORX_DARK)
    fun addListener(listener: () -> Unit) { listeners.add(listener) }; fun removeListener(listener: () -> Unit) { listeners.remove(listener) }
    private fun notifyChanged() = listeners.toList().forEach { it() }
    private fun themeFor(value: ZorxThemePreset): ZorxTheme = when(value) {
        ZorxThemePreset.HUNT_NORD -> palette("#2E3440", "#3B4252", "#434C5E", "#4C566A", "#88C0D0", "#ECEFF4")
        ZorxThemePreset.HUNT_FOREST -> palette("#1B262C", "#0F4C75", "#3282B8", "#BBE1FA", "#66BFBF", "#F1FAEE")
        ZorxThemePreset.HUNT_SUNSET -> palette("#22223B", "#4A4E69", "#9A8C98", "#C9ADA7", "#F2E9E4", "#FFFFFF")
        ZorxThemePreset.HUNT_BLOSSOM -> palette("#352F44", "#5C5470", "#B9B4C7", "#FAF0E6", "#DBA39A", "#FFFFFF")
        ZorxThemePreset.HUNT_COFFEE -> palette("#2C1810", "#5C3D2E", "#A36A4F", "#D9B08C", "#E6C9A8", "#FFF8F0")
        else -> dark(accentFor(value))
    }
    private fun palette(bg:String, surface:String, panel:String, border:String, accent:String, text:String): ZorxTheme {
        val a=Color.parseColor(accent); return ZorxTheme(Color.parseColor(bg),Color.parseColor(surface),Color.parseColor(panel),Color.parseColor(border),a,a,Color.parseColor(text),Color.parseColor("#C9D1D9"),Color.parseColor(panel),Color.parseColor(surface),Color.parseColor(bg),a,Color.parseColor(border),a)
    }
    private fun accentFor(value: ZorxThemePreset) = when(value) { ZorxThemePreset.OCEAN->Color.parseColor("#36A9E8"); ZorxThemePreset.EMERALD->Color.parseColor("#38C98B"); ZorxThemePreset.VIOLET->Color.parseColor("#A57CFF"); ZorxThemePreset.AMBER->Color.parseColor("#FFB547"); ZorxThemePreset.MONOCHROME->Color.WHITE; ZorxThemePreset.GRAPHITE->Color.parseColor("#AAB2BF"); ZorxThemePreset.MIDNIGHT->Color.parseColor("#526BFF"); else->Color.parseColor("#3ED6D0") }
    private fun dark(accent: Int) = ZorxTheme(Color.parseColor("#101114"),Color.parseColor("#1A1C20"),Color.parseColor("#14161C"),Color.parseColor("#2D3036"),accent,Color.parseColor("#7A5CFA"),Color.parseColor("#F6F6F6"),Color.parseColor("#B6B6B6"),Color.parseColor("#252A33"),Color.parseColor("#303846"),Color.parseColor("#151A22"),accent,Color.parseColor("#59606B"),accent)
}
