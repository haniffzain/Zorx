package com.zorx.launcher.desktop

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.zorx.launcher.R
import com.zorx.launcher.apps.AppDrawerView
import com.zorx.launcher.startmenu.StartMenuView
import com.zorx.launcher.taskbar.TaskbarController
import com.zorx.launcher.taskbar.TaskbarView

class DesktopActivity : AppCompatActivity() {

    private lateinit var startMenu: StartMenuView
    private lateinit var appDrawer: AppDrawerView
    private lateinit var taskbar: TaskbarView
    private lateinit var taskbarController: TaskbarController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enterImmersiveDesktop()

        setContentView(R.layout.activity_desktop)

        val desktop =
            findViewById<FrameLayout>(R.id.zorx_desktop)

        // =====================================================
        // DESKTOP SURFACE
        // =====================================================

        val desktopSurface =
            DesktopSurface(this)

        val desktopSurfaceParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        desktop.addView(
            desktopSurface,
            desktopSurfaceParams
        )

        // =====================================================
        // APPLICATION DRAWER
        // =====================================================

        appDrawer =
            AppDrawerView(this) {

                if (::taskbar.isInitialized) {
                    taskbar.refreshPinnedApps()
                }
            }

        val appDrawerParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        appDrawerParams.setMargins(
            0,
            0,
            0,
            96
        )

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
            StartMenuView(this) {
                openApplications()
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

    private fun openApplications() {

        startMenu.visibility =
            View.GONE

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

        if (::taskbarController.isInitialized) {
            taskbarController.destroy()
        }

        super.onDestroy()
    }


}
