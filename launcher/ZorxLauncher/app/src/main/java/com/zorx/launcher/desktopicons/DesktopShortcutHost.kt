package com.zorx.launcher.desktopicons

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.zorx.launcher.apps.AppManager
import com.zorx.launcher.design.ZorxThemeManager
import com.zorx.launcher.shell.ZorxShellSettingsStore
import com.zorx.launcher.spatial.DesktopGridPlacement
import com.zorx.launcher.spatial.DesktopGridSpec
import com.zorx.launcher.spatial.DesktopPlacementPolicy
import com.zorx.launcher.spatial.GridEngine
import com.zorx.launcher.spatial.SpatialBounds
import com.zorx.launcher.widgets.ZorxWidgetLayoutStore

class DesktopShortcutHost(context: Context) : FrameLayout(context) {
    private val store = DesktopShortcutStore(context)
    private val appManager = AppManager(context)

    fun removeLastShortcut() {
        val shortcuts = store.read()
        if (shortcuts.isNotEmpty()) store.save(shortcuts.dropLast(1))
        render()
    }

    fun addShortcut(app: ResolveInfo) {
        post {
            val grid = createGrid() ?: return@post
            val shortcuts = store.read().filter { grid.fits(it.placement) }
            val widgetPlacements = widgetPlacements()
            val id = "${app.activityInfo.packageName}|${app.activityInfo.name}"
            if (shortcuts.any { it.id == id }) {
                Toast.makeText(context, "Shortcut already exists", Toast.LENGTH_SHORT).show()
                return@post
            }
            val placement = grid.firstAvailable(1, 1, widgetPlacements + shortcuts.map { it.placement })
            if (placement == null) {
                Toast.makeText(context, "Desktop grid is full", Toast.LENGTH_SHORT).show()
                return@post
            }
            store.save(
                shortcuts + DesktopShortcut(
                    packageName = app.activityInfo.packageName,
                    activityName = app.activityInfo.name,
                    label = app.loadLabel(context.packageManager).toString(),
                    placement = placement
                )
            )
            render()
        }
    }

    fun render() {
        removeAllViews()
        val grid = createGrid() ?: return
        val installed = appManager.getInstalledApps().associateBy {
            "${it.activityInfo.packageName}|${it.activityInfo.name}"
        }
        val persisted = store.read().filter { installed.containsKey(it.id) }
        val reconciledPlacements = DesktopPlacementPolicy.reconcile(
            grid,
            persisted.map { it.placement },
            widgetPlacements()
        )
        val shortcuts = persisted.mapIndexedNotNull { index, shortcut ->
            reconciledPlacements[index]?.let { shortcut.copy(placement = it) }
        }
        if (shortcuts != store.read()) store.save(shortcuts)
        shortcuts.forEach { shortcut -> addShortcutView(shortcut, installed.getValue(shortcut.id), shortcuts, grid) }
    }

    private fun addShortcutView(
        shortcut: DesktopShortcut,
        app: ResolveInfo,
        all: List<DesktopShortcut>,
        grid: GridEngine
    ) {
        val density = resources.displayMetrics.density
        val bounds = grid.bounds(shortcut.placement)
        val view = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            isClickable = true
            contentDescription = shortcut.label
            addView(ImageView(context).apply {
                setImageDrawable(app.loadIcon(context.packageManager))
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }, LinearLayout.LayoutParams((44 * density).toInt(), (44 * density).toInt()))
            addView(TextView(context).apply {
                text = shortcut.label
                gravity = Gravity.CENTER
                maxLines = 2
                setTextColor(ZorxThemeManager.current().textPrimary)
                setShadowLayer(3f, 0f, 1f, Color.BLACK)
                textSize = 11f
            }, LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        }
        val params = LayoutParams(bounds.width, bounds.height).apply {
            leftMargin = bounds.x
            topMargin = bounds.y
        }
        var dragX = 0f
        var dragY = 0f
        var moved = false
        view.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragX = event.rawX - params.leftMargin
                    dragY = event.rawY - params.topMargin
                    moved = false
                }
                MotionEvent.ACTION_MOVE -> {
                    moved = moved || kotlin.math.abs(event.rawX - dragX - params.leftMargin) > density * 4 ||
                        kotlin.math.abs(event.rawY - dragY - params.topMargin) > density * 4
                    params.leftMargin = (event.rawX - dragX).toInt()
                    params.topMargin = (event.rawY - dragY).toInt()
                    view.layoutParams = params
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        appManager.launchApp(app)
                        return@setOnTouchListener true
                    }
                    val nextPlacement = DesktopGridPlacement(
                        grid.nearestColumn(params.leftMargin),
                        grid.nearestRow(params.topMargin)
                    )
                    val occupied = widgetPlacements() + all.filterNot { it.id == shortcut.id }.map { it.placement }
                    val next = if (DesktopPlacementPolicy.canPlace(grid, nextPlacement, occupied)) nextPlacement else shortcut.placement
                    store.save(all.map { if (it.id == shortcut.id) it.copy(placement = next) else it })
                    render()
                }
            }
            true
        }
        addView(view, params)
    }

    private fun createGrid(): GridEngine? {
        val gap = (16 * resources.displayMetrics.density).toInt()
        val metrics = ZorxShellSettingsStore.resolve(context, width.coerceAtLeast(1), height.coerceAtLeast(1))
        val usableHeight = height - metrics.taskbarHeightPx - metrics.taskbarBottomMarginPx
        if (width <= gap * 13 || usableHeight <= gap * 9) return null
        return GridEngine(DesktopGridSpec(SpatialBounds(0, 0, width, usableHeight), gap = gap, padding = gap))
    }

    private fun widgetPlacements(): List<DesktopGridPlacement> =
        ZorxWidgetLayoutStore.read(context).filter { it.visible }.map {
            DesktopPlacementPolicy.legacyWidgetPlacement(it.gridX, it.gridY, it.gridWidth, it.gridHeight)
        }
}
