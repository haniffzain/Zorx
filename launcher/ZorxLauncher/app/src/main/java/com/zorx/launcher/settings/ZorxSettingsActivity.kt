package com.zorx.launcher.settings

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.zorx.launcher.design.ZorxColors
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.design.ZorxTypographySettings
import com.zorx.launcher.display.ZorxDisplayManager
import com.zorx.launcher.shell.*

class ZorxSettingsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SECTION = "zorx.settings.section"
        const val SECTION_DISPLAY = "display"
        const val SECTION_APPEARANCE = "appearance"
        fun intent(context: Context, section: String = SECTION_DISPLAY) =
            Intent(context, ZorxSettingsActivity::class.java).putExtra(EXTRA_SECTION, section)
    }

    private lateinit var shell: ZorxShellSettings
    private lateinit var appearance: ZorxAppearanceSettings
    private lateinit var typography: ZorxTypographySettings
    private lateinit var root: LinearLayout
    private lateinit var settingsContent: ScrollView
    private var isPanelMaximized = false
    private var isPanelCollapsed = false
    private var minimizedToTaskbar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shell = ZorxShellSettingsStore.readShell(this)
        appearance = ZorxShellSettingsStore.readAppearance(this)
        typography = ZorxShellSettingsStore.readTypography(this)
        ZorxShellPanelManager.setDisplaySettingsState(
            ShellPanelState.OPEN
        )
        render()
        configureFloatingWindow()
    }

    private fun render() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(28), dp(24), dp(28), dp(28))
            setBackgroundColor(ZorxColors.Background)
        }
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(
            buildTitlebar()
        )
        val displays = ZorxDisplayManager(this).getDisplays()
        content.addView(card("Monitor preview") {
            val d = displays.firstOrNull()
            addView(MonitorPreviewView(context, d))
            addView(label(if (d == null) "No display detected" else
                "${d.name}  •  ${d.physicalWidthPx}×${d.physicalHeightPx}  •  ${"%.1f".format(d.refreshRateHz)} Hz"))
            if (d != null) addView(label("Effective ${d.logicalWidthPx}×${d.logicalHeightPx}  •  ${d.densityDpi} dpi  •  primary=${d.isPrimary}"))
        })
        content.addView(card("Display") {
            addView(label("Resolution, orientation, refresh rate and primary display are detected from Android. Platform changes will be enabled only when the system backend reports support."))
            addView(label("Resolution: ${displays.firstOrNull()?.let { "${it.physicalWidthPx}×${it.physicalHeightPx}" } ?: "Unavailable"}"))
            addView(label("Orientation: ${orientationName(displays.firstOrNull()?.rotation ?: 0)}"))
        })
        content.addView(card("Shell metrics") {
            addSlider("Titlebar height", 23, 72, shell.titlebarHeightDp.toInt()) {
                shell = shell.copy(titlebarHeightDp = it.toFloat()); publishLiveSettings()
            }
            addSlider("Taskbar height", 23, 96, shell.taskbarHeightDp.toInt()) {
                shell = shell.copy(taskbarHeightDp = it.toFloat()); publishLiveSettings()
            }
            addSlider("Taskbar width", 50, 100, (shell.taskbarWidthFraction * 100).toInt()) {
                shell = shell.copy(taskbarWidthFraction = it / 100f); publishLiveSettings()
            }
            addSlider("Start Menu height", 360, 760, shell.startMenuHeightDp.toInt()) {
                shell = shell.copy(startMenuHeightDp = it.toFloat()); publishLiveSettings()
            }
            addSlider("App Drawer height", 50, 100, (shell.appDrawerHeightFraction * 100).toInt()) {
                shell = shell.copy(appDrawerHeightFraction = it / 100f); publishLiveSettings()
            }
            addSlider("General UI scale", 75, 150, (shell.generalUiScale * 100).toInt()) {
                shell = shell.copy(generalUiScale = it / 100f); publishLiveSettings()
            }
        })
        content.addView(card("Shell shape") {
            addSpinner("Taskbar", ShellShape.values().map { it.name }, appearance.taskbarShape.ordinal) {
                appearance = appearance.copy(taskbarShape = ShellShape.values()[it]); publishLiveSettings()
            }
            addCornerSpinner("Menu", appearance.menuCornerStyle) {
                appearance = appearance.copy(menuCornerStyle = it); publishLiveSettings()
            }
            addCornerSpinner("Window", appearance.windowCornerStyle) {
                appearance = appearance.copy(windowCornerStyle = it); publishLiveSettings()
            }
            addCornerSpinner("Widget card", appearance.widgetCornerStyle) {
                appearance = appearance.copy(widgetCornerStyle = it); publishLiveSettings()
            }
        })
        content.addView(card("Appearance → Typography") {
            addSlider("Global Font Scale", 75, 150, (typography.globalFontScale * 100).toInt()) {
                typography = typography.copy(globalFontScale = it / 100f)
                publishLiveSettings()
            }
            addSlider("Interface Text", 10, 24, typography.interfaceTextSp.toInt()) {
                typography = typography.copy(interfaceTextSp = it.toFloat())
                publishLiveSettings()
            }
            addSlider("Titlebar Text", 10, 24, typography.titlebarTextSp.toInt()) {
                typography = typography.copy(titlebarTextSp = it.toFloat())
                publishLiveSettings()
            }
            addSlider("Start Menu Text", 10, 24, typography.startMenuTextSp.toInt()) {
                typography = typography.copy(startMenuTextSp = it.toFloat())
                publishLiveSettings()
            }
            addSlider("App Drawer Text", 10, 24, typography.appDrawerTextSp.toInt()) {
                typography = typography.copy(appDrawerTextSp = it.toFloat())
                publishLiveSettings()
            }
            addSlider("Taskbar Text", 10, 24, typography.taskbarTextSp.toInt()) {
                typography = typography.copy(taskbarTextSp = it.toFloat())
                publishLiveSettings()
            }
            addSlider("Widget Text", 10, 24, typography.widgetTextSp.toInt()) {
                typography = typography.copy(widgetTextSp = it.toFloat())
                publishLiveSettings()
            }
        })
        content.addView(Button(this).apply {
            text = "Reset typography to defaults"
            setOnClickListener {
                ZorxShellSettingsStore.resetTypography(context)
                typography = ZorxShellSettingsStore.readTypography(context)
                render()
                configureFloatingWindow()
                Toast.makeText(context, "Typography restored", Toast.LENGTH_SHORT).show()
            }
        })
        content.addView(Button(this).apply {
            text = "Reset appearance to defaults"
            setOnClickListener {
                ZorxShellSettingsStore.resetAppearance(context)
                shell = ZorxShellSettingsStore.readShell(context)
                appearance = ZorxShellSettingsStore.readAppearance(context)
                typography = ZorxShellSettingsStore.readTypography(context)
                render()
                configureFloatingWindow()
                Toast.makeText(context, "Appearance defaults restored", Toast.LENGTH_SHORT).show()
            }
        })
        content.addView(Button(this).apply {
            text = "Apply and Exit"
            setOnClickListener {
                ZorxShellSettingsStore.save(context, shell, appearance, typography)
                ZorxShellPanelManager.setDisplaySettingsState(
                    ShellPanelState.CLOSED
                )
                Toast.makeText(context, "Shell settings applied", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
        settingsContent =
            ScrollView(this).apply {
                addView(content)
            }

        root.addView(settingsContent,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        setContentView(root)
        ZorxTypography.applyToViewTree(
            root,
            this,
            typography,
            typography.interfaceTextSp
        )
    }

    private fun buildTitlebar(): View {

        val titlebar =
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, dp(12))
            }

        var downRawX = 0f
        var downRawY = 0f
        var startWindowX = 0
        var startWindowY = 0

        titlebar.setOnTouchListener { _, event ->

            when (event.actionMasked) {

                android.view.MotionEvent.ACTION_DOWN -> {
                    downRawX = event.rawX
                    downRawY = event.rawY
                    startWindowX = window.attributes.x
                    startWindowY = window.attributes.y
                    true
                }

                android.view.MotionEvent.ACTION_MOVE -> {
                    window.setGravity(
                        Gravity.TOP or Gravity.START
                    )
                    window.attributes =
                        window.attributes.apply {
                            x = (
                                startWindowX +
                                    event.rawX -
                                    downRawX
                                ).toInt()
                            y = (
                                startWindowY +
                                    event.rawY -
                                    downRawY
                                ).toInt()
                        }
                    true
                }

                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL ->
                    true

                else ->
                    false
            }
        }

        titlebar.addView(
            TextView(this).apply {
                text = "Display + Appearance"
                textSize = 13f
                setTextColor(ZorxColors.TextPrimary)
                gravity = Gravity.CENTER_VERTICAL
            },
            LinearLayout.LayoutParams(
                0,
                dp(42),
                1f
            )
        )

        fun control(
            glyph: String,
            description: String,
            action: () -> Unit
        ) =
            TextView(this).apply {
                text = glyph
                contentDescription = description
                textSize = 13f
                gravity = Gravity.CENTER
                setTextColor(ZorxColors.TextPrimary)
                background =
                    GradientDrawable().apply {
                        setColor(Color.rgb(43, 46, 54))
                        setStroke(dp(1), ZorxColors.Border)
                        cornerRadius = dp(10).toFloat()
                    }
                setOnClickListener {
                    action()
                }
            }

        titlebar.addView(
            control("—", "Minimize") {
                minimizeToTaskbar()
            },
            titlebarControlParams()
        )

        titlebar.addView(
            control("□", "Maximize or restore") {
                toggleMaximized()
            },
            titlebarControlParams()
        )

        titlebar.addView(
            control("×", "Close") {
                ZorxShellPanelManager.setDisplaySettingsState(
                    ShellPanelState.CLOSED
                )
                finish()
            }.apply {
                setTextColor(ZorxColors.Error)
            },
            titlebarControlParams()
        )

        return titlebar
    }

    private fun titlebarControlParams() =
        LinearLayout.LayoutParams(
            dp(38),
            dp(34)
        ).apply {
            setMargins(
                dp(6),
                0,
                0,
                0
            )
        }

    private fun toggleCollapsed() {

        isPanelCollapsed =
            !isPanelCollapsed

        settingsContent.visibility =
            if (isPanelCollapsed) {
                View.GONE
            } else {
                View.VISIBLE
            }

        if (isPanelCollapsed) {
            window.setLayout(
                normalPanelWidth(),
                dp(78)
            )
        } else {
            applyPanelSize()
        }
    }

    private fun minimizeToTaskbar() {

        minimizedToTaskbar =
            true

        ZorxShellPanelManager.setDisplaySettingsState(
            ShellPanelState.MINIMIZED
        )

        finish()
    }

    override fun onDestroy() {

        if (!minimizedToTaskbar) {
            ZorxShellPanelManager.setDisplaySettingsState(
                ShellPanelState.CLOSED
            )
        }

        super.onDestroy()
    }

    private fun toggleMaximized() {

        isPanelCollapsed =
            false

        settingsContent.visibility =
            View.VISIBLE

        isPanelMaximized =
            !isPanelMaximized

        applyPanelSize()
    }

    private fun normalPanelWidth(): Int {

        val metrics =
            resources.displayMetrics

        return (760 * metrics.density)
            .toInt()
            .coerceAtMost(
                (metrics.widthPixels * 0.78f).toInt()
            )
    }

    private fun applyPanelSize() {

        val metrics =
            resources.displayMetrics

        val width =
            if (isPanelMaximized) {
                (metrics.widthPixels * 0.94f).toInt()
            } else {
                normalPanelWidth()
            }

        val height =
            if (isPanelMaximized) {
                (metrics.heightPixels * 0.92f).toInt()
            } else {
                (680 * metrics.density)
                    .toInt()
                    .coerceAtMost(
                        (metrics.heightPixels * 0.84f).toInt()
                    )
            }

        window.setLayout(
            width,
            height
        )
    }

    private fun configureFloatingWindow() {

        val displayMetrics =
            resources.displayMetrics

        applyPanelSize()

        window.setGravity(
            Gravity.CENTER
        )

        window.addFlags(
            WindowManager.LayoutParams.FLAG_DIM_BEHIND
        )

        window.attributes =
            window.attributes.apply {
                dimAmount =
                    0.42f
            }

        root.background =
            GradientDrawable().apply {
                setColor(
                    ZorxColors.Background
                )
                setStroke(
                    dp(1),
                    ZorxColors.Border
                )
                cornerRadius =
                    dp(24).toFloat()
            }

        root.clipToOutline =
            true
    }

    private fun card(title: String, block: LinearLayout.() -> Unit) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(16), dp(18), dp(18))
        background = GradientDrawable().apply {
            setColor(ZorxColors.Surface); setStroke(dp(1), ZorxColors.Border); cornerRadius = dp(20).toFloat()
        }
        addView(TextView(context).apply { text = title; textSize = 13f; setTextColor(ZorxColors.TextPrimary) })
        block()
    }.also { it.layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(14)) } }

    private fun LinearLayout.addSlider(name: String, min: Int, max: Int, value: Int, changed: (Int) -> Unit) {
        val valueLabel = label("$name: $value")
        addView(valueLabel)
        addView(SeekBar(context).apply {
            this.max = max - min; progress = value - min
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                    val v = min + p
                    valueLabel.text = "$name: $v"
                    if (fromUser) changed(v)
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}
                override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        })
    }

    private fun LinearLayout.addCornerSpinner(name: String, current: CornerStyle, changed: (CornerStyle) -> Unit) =
        addSpinner(name, CornerStyle.values().map { it.name }, current.ordinal) { changed(CornerStyle.values()[it]) }

    private fun LinearLayout.addSpinner(name: String, values: List<String>, selected: Int, changed: (Int) -> Unit) {
        addView(label(name))
        addView(Spinner(context).apply {
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, values)
            setSelection(selected)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, id: Long) = changed(position)
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        })
    }

    private fun label(value: String) = TextView(this).apply {
        text = value; textSize = 13f; setTextColor(ZorxColors.TextSecondary); setPadding(0, dp(10), 0, dp(4))
    }
    private fun publishLiveSettings() {
        ZorxShellSettingsStore.save(this, shell, appearance, typography)
        if (::root.isInitialized) {
            ZorxTypography.applyToViewTree(
                root,
                this,
                typography,
                typography.interfaceTextSp
            )
        }
    }

    private fun orientationName(rotation: Int) = when (rotation) { 1 -> "90°"; 2 -> "180°"; 3 -> "270°"; else -> "0°" }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}

private class MonitorPreviewView(context: Context, private val info: com.zorx.launcher.display.ZorxDisplayInfo?) : View(context) {
    private val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
    override fun onMeasure(w: Int, h: Int) = setMeasuredDimension(resolveSize(520, w), 250)
    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)
        val outer = android.graphics.RectF(20f, 20f, width - 20f, height - 38f)
        paint.style = android.graphics.Paint.Style.FILL; paint.color = Color.rgb(16, 17, 20)
        canvas.drawRoundRect(outer, 20f, 20f, paint)
        paint.style = android.graphics.Paint.Style.STROKE; paint.strokeWidth = 3f; paint.color = ZorxColors.Primary
        canvas.drawRoundRect(outer, 20f, 20f, paint)
        paint.style = android.graphics.Paint.Style.FILL; paint.color = Color.rgb(42, 45, 52)
        canvas.drawRoundRect(outer.left + 18, outer.bottom - 38, outer.right - 18, outer.bottom - 18, 10f, 10f, paint)
    }
}
