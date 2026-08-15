package com.raven.launcher.desktop

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.raven.launcher.R
import com.raven.launcher.apps.AppDrawerView
import com.raven.launcher.startmenu.StartMenuView
import com.raven.launcher.taskbar.TaskbarView

class DesktopActivity : AppCompatActivity() {

    private lateinit var startMenu: StartMenuView
    private lateinit var appDrawer: AppDrawerView
    private lateinit var taskbar: TaskbarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_desktop)

        val desktop =
            findViewById<FrameLayout>(R.id.raven_desktop)

            val desktopSurface =
    DesktopSurface(this)

val desktopSurfaceParams =
    FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    )

desktopSurfaceParams.setMargins(
    0,
    0,
    0,
    80
)

desktop.addView(
    desktopSurface,
    desktopSurfaceParams
)

        // =========================
        // APPLICATION DRAWER
        // =========================


appDrawer = AppDrawerView(this) {

    if (::taskbar.isInitialized) {
        taskbar.refreshPinnedApps()
    }
}
        val appDrawerParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        appDrawerParams.setMargins(
            0,
            0,
            0,
            80
        )

        appDrawer.visibility = View.GONE

        desktop.addView(
            appDrawer,
            appDrawerParams
        )

        // =========================
        // START MENU
        // =========================

        startMenu = StartMenuView(this) {
            openApplications()
        }

        val density = resources.displayMetrics.density

        val startMenuWidth =
            (320 * density).toInt()

        val startMenuHeight =
            (430 * density).toInt()

        val startMenuParams =
            FrameLayout.LayoutParams(
                startMenuWidth,
                startMenuHeight
            )

        startMenuParams.gravity =
            Gravity.BOTTOM or Gravity.START

        startMenuParams.setMargins(
            15,
            0,
            0,
            90
        )

        startMenu.visibility = View.GONE

        desktop.addView(
            startMenu,
            startMenuParams
        )

        // =========================
        // TASKBAR
        // =========================

        taskbar = TaskbarView(
            this,
            desktopSurface.spatialEngine
        ) {
            toggleStartMenu()
        }

        val taskbarParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            80
        )

        taskbarParams.gravity = Gravity.BOTTOM

        desktop.addView(
            taskbar,
            taskbarParams
        )
    }

private fun toggleStartMenu() {

    if (startMenu.visibility == View.VISIBLE) {

        startMenu.visibility = View.GONE

    } else {

        // Tutup App Drawer sebelum membuka Start Menu
        appDrawer.visibility = View.GONE

        startMenu.visibility = View.VISIBLE
    }
}
    private fun openApplications() {

        startMenu.visibility = View.GONE

        appDrawer.visibility =
            if (appDrawer.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }
}
