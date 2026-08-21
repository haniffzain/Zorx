package com.zorx.launcher.desktop

import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import com.zorx.launcher.R
import com.zorx.launcher.apps.AppDrawerView
import com.zorx.launcher.startmenu.StartMenuView
import com.zorx.launcher.taskbar.TaskbarController
import com.zorx.launcher.taskbar.TaskbarView
import com.zorx.launcher.design.ZorxColors
import com.zorx.launcher.design.ZorxThemeManager
import com.zorx.launcher.widgets.WidgetHost
import com.zorx.launcher.widgets.ZorxWidgetLayoutStore
import com.zorx.launcher.widgets.ZorxWidgetRegistry
import com.zorx.launcher.workspace.ZorxWorkspaceManager
import com.zorx.launcher.display.ZorxDisplayManager
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.settings.ZorxSettingsActivity
import com.zorx.launcher.shell.ZorxShellSettingsStore

class DesktopActivity : AppCompatActivity() {

    private lateinit var startMenu: StartMenuView
    private lateinit var appDrawer: AppDrawerView
    private lateinit var taskbar: TaskbarView
    private lateinit var taskbarController: TaskbarController
    private lateinit var desktopRoot: FrameLayout
    private lateinit var desktopSurface: DesktopSurface
    private lateinit var widgetHost: WidgetHost
    private var desktopContextMenu: PopupWindow? = null

    private val shellSettingsListener = {
        runOnUiThread {
            applyShellMetrics()
        }
    }
    private val themeListener = { runOnUiThread { applyShellMetrics(); desktopRoot.setBackgroundColor(ZorxColors.Background) } }
    private val workspaceListener = { runOnUiThread { desktopSurface.invalidate() } }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        ZorxThemeManager.load(this)
        ZorxThemeManager.addListener(themeListener)
        ZorxWorkspaceManager.addListener(workspaceListener)

        enterImmersiveDesktop()

        setContentView(R.layout.activity_desktop)

        val desktop =
            findViewById<FrameLayout>(R.id.zorx_desktop)

        desktopRoot =
            desktop

        // =====================================================
        // DESKTOP SURFACE
        // =====================================================

        widgetHost = WidgetHost(this)
        desktop.addView(widgetHost, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        val desktopSurface =
            DesktopSurface(this)

        this.desktopSurface =
            desktopSurface

        val desktopSurfaceParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        desktop.addView(
            desktopSurface,
            desktopSurfaceParams
        )

        desktopSurface.setOnTouchListener { _, event ->

            if (
                event.actionMasked ==
                    MotionEvent.ACTION_DOWN
            ) {

                dismissAppDrawer()
                dismissStartMenu()
            }

            false
        }

        desktopSurface.onEmptyDesktopSecondaryClick = { x, y ->
            dismissAppDrawer()
            dismissStartMenu()
            showDesktopContextMenu(x, y)
        }

        // =====================================================
        // APPLICATION DRAWER
        // =====================================================

        appDrawer =
            AppDrawerView(
                context = this,
                onPinnedAppsChanged = {
                    if (::taskbar.isInitialized) {
                        taskbar.refreshPinnedApps()
                    }
                },
                onAppLaunched = {
                    appDrawer.visibility =
                        View.GONE
                },
                onDismiss = {
                    appDrawer.visibility =
                        View.GONE
                }
            )

        val appDrawerParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        appDrawerParams.gravity = Gravity.CENTER

        appDrawer.visibility =
            View.GONE

        desktop.addView(
            appDrawer,
            appDrawerParams
        )

        // =====================================================
        // START MENU
        // =====================================================

        startMenu =
            StartMenuView(this) { query ->
                openApplications(query)
            }

        val density =
            resources.displayMetrics.density

        val startMenuWidth =
            (360 * density).toInt()

        val startMenuHeight =
            (500 * density).toInt()

        val startMenuParams =
            FrameLayout.LayoutParams(
                startMenuWidth,
                startMenuHeight
            )

        startMenuParams.gravity =
            Gravity.BOTTOM or Gravity.START

        startMenuParams.setMargins(
            (24 * density).toInt(),
            0,
            0,
            (92 * density).toInt()
        )

        startMenu.visibility =
            View.GONE

        desktop.addView(
            startMenu,
            startMenuParams
        )

        // =====================================================
        // FLOATING TASKBAR
        // =====================================================

        taskbar =
            TaskbarView(
                this,
                desktopSurface.spatialEngine,
                {
                    toggleStartMenu()
                },
                { objectId ->
                    taskbarController.onRunningWindowClicked(
                        objectId
                    )
                }
            )

        taskbarController =
            TaskbarController(
                taskbar,
                desktopSurface.spatialEngine
            )

        val taskbarWidth =
            (resources.displayMetrics.widthPixels * 0.92f).toInt()

        val taskbarHeight =
            (68 * density).toInt()

        val taskbarParams =
            FrameLayout.LayoutParams(
                taskbarWidth,
                taskbarHeight
            )

        taskbarParams.gravity =
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

        taskbarParams.setMargins(
            0,
            0,
            0,
            (18 * density).toInt()
        )

        desktop.addView(
            taskbar,
            taskbarParams
        )

        taskbar.setOnTouchListener { _, event ->

            if (
                event.actionMasked ==
                    MotionEvent.ACTION_DOWN
            ) {

                dismissAppDrawer()
                dismissStartMenu()
            }

            false
        }

        ZorxShellSettingsStore.addListener(
            shellSettingsListener
        )

        desktop.post {
            applyShellMetrics()
        }
    }

    private fun applyShellMetrics() {

        if (
            !::desktopRoot.isInitialized ||
            desktopRoot.width <= 0 ||
            desktopRoot.height <= 0
        ) {
            return
        }

        val metrics =
            ZorxShellSettingsStore.resolve(
                this,
                desktopRoot.width,
                desktopRoot.height
            )

        val typography =
            ZorxShellSettingsStore.readTypography(this)

        if (::taskbar.isInitialized) {
            val compactPadding =
                if (metrics.taskbarHeightPx < 40) {
                    0
                } else {
                    (6 * resources.displayMetrics.density).toInt()
                }

            taskbar.setPadding(
                (12 * resources.displayMetrics.density).toInt(),
                compactPadding,
                (12 * resources.displayMetrics.density).toInt(),
                compactPadding
            )

            taskbar.layoutParams =
                (taskbar.layoutParams as FrameLayout.LayoutParams).apply {
                    width = metrics.taskbarWidthPx
                    height = metrics.taskbarHeightPx
                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    setMargins(0, 0, 0, metrics.taskbarBottomMarginPx)
                }
            taskbar.applyShellRadius(metrics.taskbarRadiusPx)
            ZorxTypography.applyToViewTree(
                taskbar,
                this,
                typography,
                typography.taskbarTextSp
            )
        }

        if (::startMenu.isInitialized) {
            startMenu.layoutParams =
                (startMenu.layoutParams as FrameLayout.LayoutParams).apply {
                    width = metrics.startMenuWidthPx
                    height = metrics.startMenuHeightPx
                    gravity = Gravity.BOTTOM or Gravity.START
                    setMargins(
                        (24 * resources.displayMetrics.density).toInt(),
                        0,
                        0,
                        metrics.startMenuBottomMarginPx
                    )
                }
            startMenu.applyShellRadius(metrics.menuRadiusPx)
            ZorxTypography.applyToViewTree(
                startMenu,
                this,
                typography,
                typography.startMenuTextSp
            )
        }

        if (::appDrawer.isInitialized) {
            appDrawer.layoutParams =
                (appDrawer.layoutParams as FrameLayout.LayoutParams).apply {
                    width = metrics.appDrawerWidthPx
                    height = metrics.appDrawerHeightPx
                    gravity = Gravity.CENTER
                    setMargins(0, 0, 0, 0)
                }
            appDrawer.applyShellRadius(metrics.menuRadiusPx)
            appDrawer.applyIconMetrics(metrics.applicationIconSizePx, metrics.uiScale)
            ZorxTypography.applyToViewTree(
                appDrawer,
                this,
                typography,
                typography.appDrawerTextSp
            )
        }

        if (::taskbar.isInitialized) {
            taskbar.applyIconSize(metrics.taskbarIconSizePx)
        }

        if (::desktopSurface.isInitialized) {
            desktopSurface.invalidate()
        }
    }

    private fun showDesktopContextMenu(
        x: Float,
        y: Float
    ) {

        desktopContextMenu?.dismiss()

        val density =
            resources.displayMetrics.density

        val menu =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(
                    (10 * density).toInt(),
                    (10 * density).toInt(),
                    (10 * density).toInt(),
                    (10 * density).toInt()
                )
                background =
                    GradientDrawable().apply {
                        setColor(ZorxColors.Surface)
                        setStroke(1, ZorxColors.Border)
                        cornerRadius =
                            (18 * density)
                    }
            }

        val popup =
            PopupWindow(
                menu,
                (260 * density).toInt(),
                FrameLayout.LayoutParams.WRAP_CONTENT,
                true
            ).apply {
                isOutsideTouchable = true
                elevation = 14 * density
                setBackgroundDrawable(
                    GradientDrawable().apply {
                        setColor(Color.TRANSPARENT)
                    }
                )
            }

        fun addAction(
            label: String,
            action: () -> Unit = {}
        ) {
            menu.addView(
                TextView(this).apply {
                    text = label
                    textSize = ZorxTypography.effectivePx(
                        this@DesktopActivity,
                        ZorxShellSettingsStore.readTypography(this@DesktopActivity),
                        ZorxShellSettingsStore.readTypography(this@DesktopActivity).interfaceTextSp
                    )
                    setTextColor(ZorxColors.TextPrimary)
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(
                        (16 * density).toInt(),
                        (12 * density).toInt(),
                        (16 * density).toInt(),
                        (12 * density).toInt()
                    )
                    setOnClickListener {
                        popup.dismiss()
                        action()
                    }
                },
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        addAction("View")
        addAction("Refresh") {
            desktopSurface.invalidate()
        }
        ZorxWidgetRegistry.available().forEach { metadata -> addAction("Widgets → Add ${metadata.name}") { ZorxWidgetLayoutStore.add(this, metadata.type); widgetHost.render() } }
        addAction("Widgets → Remove Last Clock") { ZorxWidgetLayoutStore.removeLastClock(this); widgetHost.render() }
        addAction("Widgets → Edit Layout") { widgetHost.setEditMode(true) }
        addAction("Widgets → Lock / Unlock Layout") { widgetHost.toggleLock() }
        ZorxWorkspaceManager.workspaces().forEach { workspace -> addAction("Workspace → ${workspace.order}${if(ZorxWorkspaceManager.active(this)==workspace.id) " (Active)" else ""}") { ZorxWorkspaceManager.switchWorkspace(this,workspace.id) } }
        addAction("Workspace → Next") { ZorxWorkspaceManager.nextWorkspace(this) }
        addAction("Workspace → Previous") { ZorxWorkspaceManager.previousWorkspace(this) }
        desktopSurface.spatialEngine.getAllObjects().maxByOrNull { it.zIndex }?.let { focused ->
            ZorxWorkspaceManager.workspaces().forEach { workspace ->
                addAction("Window → Move to Workspace ${workspace.order}") {
                    desktopSurface.moveWindowToWorkspace(focused.id, workspace.id)
                    desktopSurface.invalidate()
                }
            }
            ZorxDisplayManager(this).topology(ZorxShellSettingsStore.readDisplay(this).displayScale).displays.forEach { display ->
                addAction("Window → Move to ${display.name}") {
                    desktopSurface.moveWindowToDisplay(focused.id, display.id)
                    desktopSurface.invalidate()
                }
            }
        }
        addAction("Display Settings") {
            startActivity(
                ZorxSettingsActivity.intent(
                    this,
                    ZorxSettingsActivity.SECTION_DISPLAY
                )
            )
        }
        addAction("Personalization") {
            startActivity(
                ZorxSettingsActivity.intent(
                    this,
                    ZorxSettingsActivity.SECTION_APPEARANCE
                )
            )
        }

        desktopContextMenu =
            popup

        val popupWidth =
            (260 * density).toInt()

        val safeX =
            x.toInt().coerceIn(
                0,
                (desktopRoot.width - popupWidth).coerceAtLeast(0)
            )

        val safeY =
            y.toInt().coerceAtMost(
                (desktopRoot.height - (300 * density).toInt())
                    .coerceAtLeast(0)
            )

        popup.showAtLocation(
            desktopRoot,
            Gravity.TOP or Gravity.START,
            safeX,
            safeY
        )
    }

    // =========================================================
    // IMMERSIVE DESKTOP
    // =========================================================

    private fun enterImmersiveDesktop() {

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    // =========================================================
    // START MENU
    // =========================================================


    private fun dismissStartMenu() {

        if (
            ::startMenu.isInitialized &&
            startMenu.visibility == View.VISIBLE
        ) {

            startMenu.visibility =
                View.GONE
        }
    }

    private fun dismissAppDrawer() {

        if (
            ::appDrawer.isInitialized &&
            appDrawer.visibility == View.VISIBLE
        ) {

            appDrawer.visibility =
                View.GONE
        }
    }

    private fun toggleStartMenu() {

        if (startMenu.visibility == View.VISIBLE) {

            startMenu.visibility =
                View.GONE

        } else {

            appDrawer.visibility =
                View.GONE

            startMenu.visibility =
                View.VISIBLE
        }
    }

    // =========================================================
    // APPLICATIONS
    // =========================================================

    private fun openApplications(query: String = "") {

        startMenu.visibility =
            View.GONE

        appDrawer.setQuery(query)

        appDrawer.visibility =
            if (appDrawer.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    override fun onWindowFocusChanged(
        hasFocus: Boolean
    ) {

        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            enterImmersiveDesktop()
        }
    }

    override fun onDestroy() {

        ZorxShellSettingsStore.removeListener(
            shellSettingsListener
        )
        ZorxThemeManager.removeListener(themeListener)
        ZorxWorkspaceManager.removeListener(workspaceListener)

        desktopContextMenu?.dismiss()

        if (::taskbarController.isInitialized) {
            taskbarController.destroy()
        }

        if (::taskbar.isInitialized) {
            taskbar.destroy()
        }

        super.onDestroy()
    }


}
