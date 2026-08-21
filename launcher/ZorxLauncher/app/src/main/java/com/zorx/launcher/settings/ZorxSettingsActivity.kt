package com.zorx.launcher.settings

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.zorx.launcher.design.*
import com.zorx.launcher.display.ZorxDisplayInfo
import com.zorx.launcher.display.ZorxDisplayManager
import com.zorx.launcher.shell.*
import com.zorx.launcher.wallpaper.*
import com.zorx.launcher.workspace.ZorxWorkspaceManager
import kotlin.math.abs

class ZorxSettingsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SECTION = "zorx.settings.section"
        const val SECTION_DISPLAY = "display"
        const val SECTION_APPEARANCE = "appearance"
        const val SECTION_BACKGROUND = "background"
        private const val STATE_TAB = "zorx.settings.active_tab"
        private const val REQUEST_WALLPAPER_IMAGE = 4601

        fun intent(context: Context, section: String = SECTION_DISPLAY) =
            Intent(context, ZorxSettingsActivity::class.java).putExtra(EXTRA_SECTION, section)
    }

    private enum class SettingsTab(val label: String) {
        DISPLAY("DISPLAY"), SCALE("SCALE"), APPEARANCE("APPEARANCE"), WALLPAPER("WALLPAPER")
    }

    private lateinit var shell: ZorxShellSettings
    private lateinit var appearance: ZorxAppearanceSettings
    private lateinit var typography: ZorxTypographySettings
    private lateinit var displaySettings: ZorxDisplaySettings
    private lateinit var root: LinearLayout
    private lateinit var tabContent: FrameLayout
    private lateinit var tabStrip: LinearLayout
    private var activeTab = SettingsTab.DISPLAY
    private var isPanelMaximized = false
    private var minimizedToTaskbar = false
    private var wallpaperScope = ZorxWallpaperScope.ALL_WORKSPACES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ZorxThemeManager.load(this)
        reloadSettings()
        activeTab = savedInstanceState?.getString(STATE_TAB)?.let {
            runCatching { SettingsTab.valueOf(it) }.getOrNull()
        } ?: tabForSection(intent.getStringExtra(EXTRA_SECTION))
        ZorxShellPanelManager.setDisplaySettingsState(ShellPanelState.OPEN)
        renderWindow()
        configureFloatingWindow()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        showTab(tabForSection(intent.getStringExtra(EXTRA_SECTION)))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_TAB, activeTab.name)
        super.onSaveInstanceState(outState)
    }

    private fun reloadSettings() {
        shell = ZorxShellSettingsStore.readShell(this)
        appearance = ZorxShellSettingsStore.readAppearance(this)
        typography = ZorxShellSettingsStore.readTypography(this)
        displaySettings = ZorxShellSettingsStore.readDisplay(this)
        wallpaperScope = ZorxWallpaperManager.scope(this)
    }

    private fun tabForSection(section: String?) = when (section) {
        SECTION_APPEARANCE -> SettingsTab.APPEARANCE
        SECTION_BACKGROUND -> SettingsTab.WALLPAPER
        else -> SettingsTab.DISPLAY
    }

    private fun renderWindow() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(14), dp(20), dp(16))
        }
        root.addView(buildTitlebar())
        root.addView(buildTabs(), LinearLayout.LayoutParams(-1, dp(48)))
        tabContent = FrameLayout(this)
        root.addView(tabContent, LinearLayout.LayoutParams(-1, 0, 1f).apply {
            setMargins(0, dp(10), 0, dp(10))
        })
        root.addView(buildFooter(), LinearLayout.LayoutParams(-1, dp(46)))
        setContentView(root)
        showTab(activeTab)
        applyTypography()
    }

    private fun buildTitlebar(): View {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        var downX = 0f; var downY = 0f; var startX = 0; var startY = 0
        bar.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX; downY = event.rawY
                    startX = window.attributes.x; startY = window.attributes.y; true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    window.setGravity(Gravity.TOP or Gravity.START)
                    window.attributes = window.attributes.apply {
                        x = (startX + event.rawX - downX).toInt()
                        y = (startY + event.rawY - downY).toInt()
                    }; true
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> true
                else -> false
            }
        }
        bar.addView(TextView(this).apply {
            text = "Zorx Settings"; textSize = 14f; gravity = Gravity.CENTER_VERTICAL
            setTextColor(ZorxColors.TextPrimary)
        }, LinearLayout.LayoutParams(0, dp(40), 1f))
        bar.addView(windowControl("—", "Minimize") { minimizeToTaskbar() }, titlebarControlParams())
        bar.addView(windowControl("□", "Maximize or restore") { toggleMaximized() }, titlebarControlParams())
        bar.addView(windowControl("×", "Close") { closeSettings() }.apply {
            setTextColor(ZorxColors.Error)
        }, titlebarControlParams())
        return bar
    }

    private fun buildTabs(): View {
        tabStrip = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
        }
        SettingsTab.values().forEach { tab ->
            tabStrip.addView(TextView(this).apply {
                tag = tab; text = tab.label; textSize = 12f; gravity = Gravity.CENTER
                setPadding(dp(10), 0, dp(10), 0)
                setOnClickListener { showTab(tab) }
            }, LinearLayout.LayoutParams(dp(if (tab == SettingsTab.APPEARANCE) 138 else 116), dp(38)))
        }
        return HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false; isFillViewport = true
            addView(tabStrip, FrameLayout.LayoutParams(-2, -1, Gravity.CENTER))
        }
    }

    private fun showTab(tab: SettingsTab) {
        activeTab = tab
        if (!::tabContent.isInitialized) return
        tabStrip.children().forEach { view ->
            val selected = view.tag == tab
            (view as TextView).apply {
                setTextColor(if (selected) ZorxColors.TextPrimary else ZorxColors.TextSecondary)
                background = roundedBackground(
                    if (selected) ZorxColors.Primary else Color.TRANSPARENT,
                    if (selected) ZorxColors.Primary else ZorxColors.Border, 10
                )
            }
        }
        tabContent.removeAllViews()
        val panel = when (tab) {
            SettingsTab.DISPLAY -> buildDisplayTab()
            SettingsTab.SCALE -> buildScaleTab()
            SettingsTab.APPEARANCE -> buildAppearanceTab()
            SettingsTab.WALLPAPER -> buildWallpaperTab()
        }
        tabContent.addView(panel, FrameLayout.LayoutParams(-1, -1))
        applyTypography()
    }

    private fun buildDisplayTab(): View {
        val displays = ZorxDisplayManager(this).getDisplays()
        val topology = ZorxDisplayManager(this).topology(displaySettings.displayScale)
        return tabScroll(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(card("Monitor layout") {
                addView(MonitorPreviewView(context, displays, displaySettings.displayScale), LinearLayout.LayoutParams(-1, dp(150)))
            }, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
            addResponsiveCards(displays.mapIndexed { index, display ->
                val logical = topology.displays.getOrNull(index)
                card("Display ${index + 1} · ${display.name}") {
                    addView(infoLine("Primary", if (display.isPrimary) "Yes" else "No"))
                    addView(infoLine("Resolution", "${display.physicalWidthPx} × ${display.physicalHeightPx}"))
                    addView(infoLine("Orientation", orientationName(display.rotation)))
                    addView(infoLine("Refresh rate", "%.1f Hz".format(display.refreshRateHz)))
                    if (logical != null) addView(infoLine("Work area", "${logical.effectiveWidthPx} × ${logical.effectiveHeightPx}"))
                }
            })
            if (displays.isEmpty()) addView(card("Display") { addView(label("No display detected")) })
        })
    }

    private fun buildScaleTab(): View {
        val display = ZorxDisplayManager(this).getDisplays().firstOrNull()
        val metrics = ZorxDisplayManager(this).getMetrics(displaySettings.displayScale).firstOrNull()
        return tabScroll(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addResponsiveCards(listOf(
                card("Scale presets") {
                    val scales = listOf(.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)
                    addSpinner("Display scale", listOf("75%", "100%", "125%", "150%", "175%", "200%"), scales.nearestIndex(displaySettings.displayScale)) {
                        displaySettings = displaySettings.copy(displayScale = scales[it])
                        ZorxShellSettingsStore.saveDisplayScale(context, displaySettings.displayScale)
                        showTab(SettingsTab.SCALE)
                    }
                    addSlider("General UI scale", 75, 150, (shell.generalUiScale * 100).toInt(), "%") {
                        shell = shell.copy(generalUiScale = it / 100f); publishLiveSettings()
                    }
                },
                card("Effective desktop") {
                    addView(infoLine("Physical resolution", display?.let { "${it.physicalWidthPx} × ${it.physicalHeightPx}" } ?: "Unavailable"))
                    addView(infoLine("Scale", "${(displaySettings.displayScale * 100).toInt()}%"))
                    addView(infoLine("Effective desktop", metrics?.let { "${it.effectiveWidthPx} × ${it.effectiveHeightPx}" } ?: "Unavailable"))
                    addView(label("Display scale changes workspace metrics; general UI scale changes shell controls."))
                }
            ))
        })
    }

    private fun buildAppearanceTab(): View = tabScroll(LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addResponsiveCards(listOf(
            card("Shell size") {
                addSlider("Titlebar height", 23, 72, shell.titlebarHeightDp.toInt(), "dp") { shell = shell.copy(titlebarHeightDp = it.toFloat()); publishLiveSettings() }
                addSlider("Taskbar height", 23, 96, shell.taskbarHeightDp.toInt(), "dp") { shell = shell.copy(taskbarHeightDp = it.toFloat()); publishLiveSettings() }
                addSlider("Taskbar width", 50, 100, (shell.taskbarWidthFraction * 100).toInt(), "%") { shell = shell.copy(taskbarWidthFraction = it / 100f); publishLiveSettings() }
                addSlider("Start Menu height", 360, 760, shell.startMenuHeightDp.toInt(), "dp") { shell = shell.copy(startMenuHeightDp = it.toFloat()); publishLiveSettings() }
                addSlider("App Drawer width", 45, 90, (shell.appDrawerWidthFraction * 100).toInt(), "%") { shell = shell.copy(appDrawerWidthFraction = it / 100f); publishLiveSettings() }
                addSlider("App Drawer height", 45, 90, (shell.appDrawerHeightFraction * 100).toInt(), "%") { shell = shell.copy(appDrawerHeightFraction = it / 100f); publishLiveSettings() }
            },
            card("Icons") {
                addSlider("App icon size", 40, 96, shell.applicationIconSizeDp.toInt(), "dp") { shell = shell.copy(applicationIconSizeDp = it.toFloat()); publishLiveSettings() }
                addSlider("Taskbar icon size", 20, 52, shell.taskbarIconSizeDp.toInt(), "dp") { shell = shell.copy(taskbarIconSizeDp = it.toFloat()); publishLiveSettings() }
            }
        ))
        addResponsiveCards(listOf(
            card("Typography") {
                addSlider("Global font scale", 75, 150, (typography.globalFontScale * 100).toInt(), "%") { typography = typography.copy(globalFontScale = it / 100f); publishLiveSettings() }
                addSlider("Menu text", 10, 24, typography.interfaceTextSp.toInt(), "sp") { typography = typography.copy(interfaceTextSp = it.toFloat(), startMenuTextSp = it.toFloat(), appDrawerTextSp = it.toFloat()); publishLiveSettings() }
                addSlider("Titlebar text", 10, 24, typography.titlebarTextSp.toInt(), "sp") { typography = typography.copy(titlebarTextSp = it.toFloat()); publishLiveSettings() }
                addSlider("Taskbar text", 10, 24, typography.taskbarTextSp.toInt(), "sp") { typography = typography.copy(taskbarTextSp = it.toFloat()); publishLiveSettings() }
                addSlider("Widget text", 10, 24, typography.widgetTextSp.toInt(), "sp") { typography = typography.copy(widgetTextSp = it.toFloat()); publishLiveSettings() }
            },
            card("Shape") {
                addSpinner("Taskbar", ShellShape.values().map { it.name }, appearance.taskbarShape.ordinal) { appearance = appearance.copy(taskbarShape = ShellShape.values()[it]); publishLiveSettings() }
                addCornerSpinner("Window corners", appearance.windowCornerStyle) { appearance = appearance.copy(windowCornerStyle = it); publishLiveSettings(); configureFloatingWindow() }
                addCornerSpinner("Menu corners", appearance.menuCornerStyle) { appearance = appearance.copy(menuCornerStyle = it); publishLiveSettings() }
                addCornerSpinner("Widget corners", appearance.widgetCornerStyle) { appearance = appearance.copy(widgetCornerStyle = it); publishLiveSettings() }
            }
        ))
        addResponsiveCards(listOf(
            card("Theme") {
                addSpinner("Theme preset", ZorxThemePreset.values().map { it.name.replace('_', ' ') }, ZorxThemeManager.currentPreset().ordinal) {
                    ZorxThemeManager.applyPreset(context, ZorxThemePreset.values()[it]); rerender(SettingsTab.APPEARANCE)
                }
                addColorPalette("Desktop") { theme, color -> theme.copy(desktopBackground = color) }
                addColorPalette("Panel") { theme, color -> theme.copy(panelBackground = color) }
                addColorPalette("Accent") { theme, color -> theme.copy(primaryAccent = color, activeWindowBorder = color, widgetAccent = color) }
            },
            card("Theme surfaces") {
                addColorPalette("Surface") { theme, color -> theme.copy(surfaceBackground = color) }
                addColorPalette("Border") { theme, color -> theme.copy(borderColor = color) }
                addColorPalette("Primary text") { theme, color -> theme.copy(textPrimary = color) }
                addColorPalette("Secondary text") { theme, color -> theme.copy(textSecondary = color) }
                addColorPalette("Button") { theme, color -> theme.copy(buttonColor = color) }
            }
        ))
    })

    private fun buildWallpaperTab(): View {
        val workspace = ZorxWorkspaceManager.active(this)
        var wallpaper = ZorxWallpaperManager.current(this, workspace, null)
        return tabScroll(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addResponsiveCards(listOf(
                card("Wallpaper preview") {
                    val preview = FrameLayout(context).apply {
                        background = roundedBackground(ZorxColors.Background, ZorxColors.Border, 12)
                        addView(WallpaperView(context), FrameLayout.LayoutParams(-1, -1))
                        addView(TextView(context).apply {
                            text = "${wallpaper.source.name.replace('_', ' ')} · ${wallpaper.mode.name}"
                            setTextColor(ZorxColors.TextPrimary); setPadding(dp(10), dp(6), dp(10), dp(6))
                            background = roundedBackground(ZorxThemeManager.current().panelBackground, ZorxColors.Border, 8)
                        }, FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM or Gravity.START).apply { setMargins(dp(10), dp(10), dp(10), dp(10)) })
                    }
                    addView(preview, LinearLayout.LayoutParams(-1, dp(190)))
                },
                card("Wallpaper setup") {
                    addSpinner("Mode", ZorxWallpaperMode.values().map { it.name.lowercase().replaceFirstChar(Char::uppercase) }, wallpaper.mode.ordinal) {
                        wallpaper = wallpaper.copy(mode = ZorxWallpaperMode.values()[it])
                        ZorxWallpaperManager.apply(context, wallpaper, wallpaperScope, workspace, null)
                    }
                    addSpinner("Scope", listOf("All Workspaces", "Current Workspace"), wallpaperScope.ordinal) {
                        wallpaperScope = ZorxWallpaperScope.values()[it]
                        ZorxWallpaperManager.setScope(context, wallpaperScope)
                    }
                    addView(actionButton("Choose Image") { chooseWallpaperImage() })
                    addView(actionButton("Use Zorx Default") {
                        ZorxWallpaperManager.apply(context, ZorxWallpaper(), wallpaperScope, workspace, null); showTab(SettingsTab.WALLPAPER)
                    })
                    addView(actionButton("Solid Color") {
                        val colors = intArrayOf(Color.parseColor("#10151F"), Color.parseColor("#16283A"), Color.parseColor("#19324A"), Color.parseColor("#243447"), Color.parseColor("#302B63"))
                        val next = colors[(colors.indexOf(wallpaper.solidColor).coerceAtLeast(-1) + 1) % colors.size]
                        ZorxWallpaperManager.apply(context, wallpaper.copy(source = ZorxWallpaperSource.SOLID_COLOR, solidColor = next), wallpaperScope, workspace, null)
                        showTab(SettingsTab.WALLPAPER)
                    })
                }
            ))
        })
    }

    private fun buildFooter() = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.END or Gravity.CENTER_VERTICAL
        addView(actionButton("Reset") { resetActiveTab() }, footerButtonParams())
        addView(actionButton("Apply") {
            ZorxShellSettingsStore.save(context, shell, appearance, typography)
            Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
        }, footerButtonParams())
        addView(actionButton("Close") { closeSettings() }, footerButtonParams())
    }

    private fun resetActiveTab() {
        when (activeTab) {
            SettingsTab.DISPLAY, SettingsTab.SCALE -> {
                ZorxShellSettingsStore.saveDisplayScale(this, 1f)
                if (activeTab == SettingsTab.SCALE) {
                    shell = shell.copy(generalUiScale = 1f); publishLiveSettings()
                }
            }
            SettingsTab.APPEARANCE -> {
                ZorxShellSettingsStore.resetAppearance(this)
                ZorxShellSettingsStore.resetTypography(this)
                ZorxThemeManager.reset(this)
            }
            SettingsTab.WALLPAPER -> ZorxWallpaperManager.reset(this, wallpaperScope, ZorxWorkspaceManager.active(this), null)
        }
        reloadSettings(); rerender(activeTab)
        Toast.makeText(this, "Defaults restored", Toast.LENGTH_SHORT).show()
    }

    private fun rerender(tab: SettingsTab) {
        activeTab = tab; renderWindow(); configureFloatingWindow()
    }

    private fun LinearLayout.addResponsiveCards(cards: List<View>) {
        if (cards.isEmpty()) return
        val availableDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        if (availableDp >= 720 && cards.size > 1) {
            val row = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.TOP }
            cards.forEachIndexed { index, view ->
                row.addView(view, LinearLayout.LayoutParams(0, -2, 1f).apply {
                    leftMargin = if (index == 0) 0 else dp(7)
                    rightMargin = if (index == cards.lastIndex) 0 else dp(7)
                })
            }
            addView(row, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
        } else cards.forEach { addView(it, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) }) }
    }

    private fun tabScroll(content: View) = ScrollView(this).apply {
        isFillViewport = true; isVerticalScrollBarEnabled = true
        addView(content, FrameLayout.LayoutParams(-1, -2))
    }

    private fun card(title: String, block: LinearLayout.() -> Unit) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; setPadding(dp(14), dp(12), dp(14), dp(14))
        background = roundedBackground(ZorxThemeManager.current().surfaceBackground, ZorxColors.Border, 16)
        addView(TextView(context).apply {
            text = title; textSize = 13f; setTextColor(ZorxColors.TextPrimary); setPadding(0, 0, 0, dp(6))
        }); block()
    }

    private fun infoLine(name: String, value: String) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
        addView(label(name), LinearLayout.LayoutParams(0, -2, 1f))
        addView(TextView(context).apply { text = value; textSize = 13f; setTextColor(ZorxColors.TextPrimary) })
    }

    private fun LinearLayout.addSlider(name: String, min: Int, max: Int, value: Int, suffix: String, changed: (Int) -> Unit) {
        val valueLabel = label("$name: $value$suffix"); addView(valueLabel)
        addView(SeekBar(context).apply {
            this.max = max - min; progress = (value - min).coerceIn(0, max - min)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val current = min + progress; valueLabel.text = "$name: $current$suffix"
                    if (fromUser) changed(current)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }, LinearLayout.LayoutParams(-1, dp(34)))
    }

    private fun LinearLayout.addSpinner(name: String, values: List<String>, selected: Int, changed: (Int) -> Unit) {
        addView(label(name))
        addView(Spinner(context).apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, values)
            setSelection(selected.coerceIn(0, values.lastIndex))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                private var initial = true
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (initial) { initial = false; return }; changed(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }, LinearLayout.LayoutParams(-1, dp(46)))
    }

    private fun LinearLayout.addCornerSpinner(name: String, current: CornerStyle, changed: (CornerStyle) -> Unit) =
        addSpinner(name, CornerStyle.values().map { it.name }, current.ordinal) { changed(CornerStyle.values()[it]) }

    private fun LinearLayout.addColorPalette(name: String, changed: (ZorxTheme, Int) -> ZorxTheme) {
        val palette = intArrayOf(Color.parseColor("#101114"), Color.parseColor("#263238"), Color.parseColor("#1565C0"), Color.parseColor("#00897B"), Color.parseColor("#7E57C2"), Color.parseColor("#FFB300"), Color.WHITE)
        addView(actionButton("$name palette") {
            val next = palette[(palette.indexOf(ZorxThemeManager.current().primaryAccent).coerceAtLeast(-1) + 1) % palette.size]
            ZorxThemeManager.updateCustom(context) { changed(it, next) }; rerender(SettingsTab.APPEARANCE)
        })
    }

    private fun actionButton(label: String, action: () -> Unit) = Button(this).apply {
        text = label; setTextColor(ZorxColors.TextPrimary)
        background = roundedBackground(ZorxThemeManager.current().buttonColor, ZorxColors.Border, 10)
        setOnClickListener { action() }
    }

    private fun windowControl(glyph: String, description: String, action: () -> Unit) = TextView(this).apply {
        text = glyph; contentDescription = description; textSize = 13f; gravity = Gravity.CENTER
        setTextColor(ZorxColors.TextPrimary)
        background = roundedBackground(ZorxThemeManager.current().buttonColor, ZorxColors.Border, 10)
        setOnClickListener { action() }
    }

    private fun roundedBackground(color: Int, border: Int, cornerDp: Int) = GradientDrawable().apply {
        setColor(color); setStroke(dp(1), border); cornerRadius = dp(cornerDp).toFloat()
    }

    private fun label(value: String) = TextView(this).apply {
        text = value; textSize = 13f; setTextColor(ZorxColors.TextSecondary); setPadding(0, dp(6), 0, dp(3))
    }

    private fun applyTypography() {
        if (::root.isInitialized) ZorxTypography.applyToViewTree(root, this, typography, typography.interfaceTextSp)
    }

    private fun publishLiveSettings() {
        ZorxShellSettingsStore.save(this, shell, appearance, typography); applyTypography()
    }

    private fun chooseWallpaperImage() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }, REQUEST_WALLPAPER_IMAGE)
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_WALLPAPER_IMAGE || resultCode != RESULT_OK) return
        val uri: Uri = data?.data ?: return
        runCatching { contentResolver.takePersistableUriPermission(uri, data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        val workspace = ZorxWorkspaceManager.active(this)
        val current = ZorxWallpaperManager.current(this, workspace, null)
        ZorxWallpaperManager.apply(this, current.copy(source = ZorxWallpaperSource.USER_IMAGE, imageUri = uri.toString()), wallpaperScope, workspace, null)
        showTab(SettingsTab.WALLPAPER)
    }

    private fun closeSettings() {
        ZorxShellSettingsStore.save(this, shell, appearance, typography)
        ZorxShellPanelManager.setDisplaySettingsState(ShellPanelState.CLOSED); finish()
    }

    private fun minimizeToTaskbar() {
        minimizedToTaskbar = true
        ZorxShellPanelManager.setDisplaySettingsState(ShellPanelState.MINIMIZED); finish()
    }

    override fun onDestroy() {
        if (!minimizedToTaskbar) ZorxShellPanelManager.setDisplaySettingsState(ShellPanelState.CLOSED)
        super.onDestroy()
    }

    private fun toggleMaximized() { isPanelMaximized = !isPanelMaximized; applyPanelSize() }

    private fun applyPanelSize() {
        val metrics = resources.displayMetrics; val margin = dp(24)
        val maximumWidth = (metrics.widthPixels - margin * 2).coerceAtLeast(1)
        val maximumHeight = (metrics.heightPixels - margin * 2).coerceAtLeast(1)
        val width = if (isPanelMaximized) maximumWidth else dp(840).coerceAtMost((metrics.widthPixels * .9f).toInt()).coerceAtMost(maximumWidth)
        val height = if (isPanelMaximized) maximumHeight else dp(620).coerceAtMost((metrics.heightPixels * .86f).toInt()).coerceAtMost(maximumHeight)
        window.setLayout(width, height)
    }

    private fun configureFloatingWindow() {
        applyPanelSize(); window.setGravity(Gravity.CENTER)
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = window.attributes.apply { dimAmount = .42f }
        val corners = if (appearance.windowCornerStyle == CornerStyle.SQUARE) 0 else 20
        root.background = roundedBackground(ZorxThemeManager.current().panelBackground, ZorxColors.Border, corners)
        root.clipToOutline = true
    }

    private fun titlebarControlParams() = LinearLayout.LayoutParams(dp(38), dp(34)).apply { leftMargin = dp(6) }
    private fun footerButtonParams() = LinearLayout.LayoutParams(dp(104), dp(40)).apply { leftMargin = dp(8) }
    private fun orientationName(rotation: Int) = when (rotation) { 1 -> "90°"; 2 -> "180°"; 3 -> "270°"; else -> "0°" }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    private fun LinearLayout.children() = (0 until childCount).map { getChildAt(it) }
    private fun List<Float>.nearestIndex(value: Float) = indices.minByOrNull { abs(this[it] - value) } ?: 0
}

private class MonitorPreviewView(
    context: Context,
    private val displays: List<ZorxDisplayInfo>,
    private val scale: Float
) : View(context) {
    private val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)
        if (displays.isEmpty()) return
        val gap = 12f
        val cardWidth = ((width - 24f - gap * (displays.size - 1)) / displays.size).coerceAtLeast(90f)
        displays.forEachIndexed { index, display ->
            val left = 12f + index * (cardWidth + gap)
            val outer = android.graphics.RectF(left, 12f, left + cardWidth, height - 20f)
            paint.style = android.graphics.Paint.Style.FILL; paint.color = ZorxThemeManager.current().desktopBackground
            canvas.drawRoundRect(outer, 14f, 14f, paint)
            paint.style = android.graphics.Paint.Style.STROKE; paint.strokeWidth = if (display.isPrimary) 4f else 2f
            paint.color = if (display.isPrimary) ZorxColors.Primary else ZorxColors.Border
            canvas.drawRoundRect(outer, 14f, 14f, paint)
            paint.style = android.graphics.Paint.Style.FILL; paint.color = ZorxColors.TextPrimary
            paint.textAlign = android.graphics.Paint.Align.CENTER; paint.textSize = 20f
            canvas.drawText("${index + 1}", outer.centerX(), outer.centerY() - 8f, paint)
            paint.textSize = 13f
            canvas.drawText("${display.physicalWidthPx}×${display.physicalHeightPx} · ${(scale * 100).toInt()}%", outer.centerX(), outer.centerY() + 20f, paint)
        }
    }
}
