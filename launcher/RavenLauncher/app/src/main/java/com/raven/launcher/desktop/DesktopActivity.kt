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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_desktop)

        val desktop =
            findViewById<FrameLayout>(R.id.raven_desktop)

        // =========================
        // APPLICATION DRAWER
        // =========================

        appDrawer = AppDrawerView(this)

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

        val startMenuParams = FrameLayout.LayoutParams(
            500,
            600
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

        val taskbar = TaskbarView(this) {
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

        startMenu.visibility =
            if (startMenu.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
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
