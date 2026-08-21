package com.zorx.launcher.apps

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.shell.ZorxShellSettingsStore
import kotlin.math.max

/** A compact, floating launcher which reflows from its current shell metrics. */
class AppDrawerView(
    context: Context,
    private val onPinnedAppsChanged: () -> Unit = {},
    private val onAppLaunched: () -> Unit = {},
    private val onDismiss: () -> Unit = {}
) : ScrollView(context) {
    private val density = resources.displayMetrics.density
    private val appManager = AppManager(context)
    private val pinnedAppManager = PinnedAppManager(context)
    private val installedApps = appManager.getInstalledApps()
    private val content = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
    private val search = EditText(context).apply {
        hint = "Search applications…"
        setHintTextColor(Color.rgb(150, 155, 165))
        setTextColor(Color.WHITE)
        setSingleLine(true)
        background = GradientDrawable().apply { setColor(Color.argb(115, 8, 10, 14)); cornerRadius = dp(12).toFloat() }
        setPadding(dp(14), 0, dp(14), 0)
    }
    private val grid = GridLayout(context)
    private var appIconSizePx = dp(72)
    private var uiScale = 1f
    private var visibleApps: List<ResolveInfo> = installedApps
    private var backgroundDownX = 0f
    private var backgroundDownY = 0f

    init {
        isFillViewport = true
        applyShellRadius(0f)
        content.setPadding(dp(16), dp(16), dp(16), dp(18))
        content.addView(search, LinearLayout.LayoutParams(-1, dp(44)))
        content.addView(grid, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(12) })
        addView(content, LayoutParams(-1, -2))
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = setQuery(s?.toString().orEmpty())
            override fun afterTextChanged(s: Editable?) = Unit
        })
        renderApps(installedApps)
    }

    fun applyShellRadius(radiusPx: Float) {
        background = GradientDrawable().apply {
            setColor(Color.rgb(20, 22, 28)); setStroke(dp(1), Color.argb(75, 255, 255, 255)); cornerRadius = radiusPx
        }
        clipToOutline = radiusPx > 0f
        invalidate()
    }

    fun applyIconMetrics(iconSizePx: Int, scale: Float) {
        val changed = appIconSizePx != iconSizePx || uiScale != scale
        appIconSizePx = iconSizePx
        uiScale = scale
        if (changed) renderApps(visibleApps)
    }

    fun setQuery(query: String) {
        val normalized = query.trim()
        visibleApps = if (normalized.isBlank()) installedApps else installedApps.filter { app ->
            app.loadLabel(context.packageManager).toString().contains(normalized, ignoreCase = true) ||
                app.activityInfo.packageName.contains(normalized, ignoreCase = true)
        }
        renderApps(visibleApps)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw) renderApps(visibleApps)
    }

    private fun renderApps(apps: List<ResolveInfo>) {
        val spacing = dpScaled(10)
        val horizontalPadding = dpScaled(4)
        val labelHeight = max(dpScaled(34), (appIconSizePx * .48f).toInt())
        val cellWidth = max(dpScaled(96), appIconSizePx + spacing * 2)
        val cellHeight = appIconSizePx + labelHeight + spacing * 2
        val available = (if (width > 0) width else resources.displayMetrics.widthPixels * .62f).toInt() - horizontalPadding * 2
        val columns = (available / (cellWidth + spacing)).coerceIn(2, 8)
        grid.removeAllViews()
        grid.columnCount = columns
        grid.setPadding(horizontalPadding, spacing, horizontalPadding, spacing)
        if (apps.isEmpty()) {
            grid.addView(TextView(context).apply {
                text = "No applications found"; setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, drawerTextPx()); setPadding(spacing, spacing, spacing, spacing)
            })
            return
        }
        apps.forEach { app ->
            val cell = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; isClickable = true; isFocusable = true
                setPadding(spacing, spacing, spacing, spacing)
                setOnClickListener { appManager.launchApp(app); onAppLaunched() }
                setOnLongClickListener { showPinAction(app); true }
            }
            cell.addView(ImageView(context).apply { setImageDrawable(app.loadIcon(context.packageManager)) }, LinearLayout.LayoutParams(appIconSizePx, appIconSizePx))
            cell.addView(TextView(context).apply {
                text = app.loadLabel(context.packageManager); setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, drawerTextPx()); gravity = Gravity.CENTER; maxLines = 2
                ellipsize = android.text.TextUtils.TruncateAt.END
            }, LinearLayout.LayoutParams(-1, labelHeight))
            grid.addView(cell, GridLayout.LayoutParams().apply {
                width = cellWidth; height = cellHeight; setMargins(spacing / 2, spacing / 2, spacing / 2, spacing / 2)
            })
        }
    }

    private fun showPinAction(app: ResolveInfo) {
        val name = app.loadLabel(context.packageManager).toString()
        val pinned = pinnedAppManager.isPinned(app)
        val action = if (pinned) "Unpin from Taskbar" else "Pin to Taskbar"
        AlertDialog.Builder(context).setTitle(name).setItems(arrayOf(action)) { _, _ ->
            if (pinned) pinnedAppManager.unpinApp(app) else pinnedAppManager.pinApp(app)
            Toast.makeText(context, if (pinned) "$name removed from Taskbar" else "$name pinned to Taskbar", Toast.LENGTH_SHORT).show()
            onPinnedAppsChanged()
        }.show()
    }

    private fun drawerTextPx(): Float {
        val typography = ZorxShellSettingsStore.readTypography(context)
        return ZorxTypography.effectivePx(context, typography, typography.appDrawerTextSp)
    }
    private fun dp(value: Int) = (value * density).toInt()
    private fun dpScaled(value: Int) = (value * density * uiScale).toInt()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { backgroundDownX = event.x; backgroundDownY = event.y }
            MotionEvent.ACTION_UP -> if (kotlin.math.abs(event.x - backgroundDownX) < 12f && kotlin.math.abs(event.y - backgroundDownY) < 12f) {
                onDismiss(); return true
            }
        }
        return super.onTouchEvent(event)
    }
}
