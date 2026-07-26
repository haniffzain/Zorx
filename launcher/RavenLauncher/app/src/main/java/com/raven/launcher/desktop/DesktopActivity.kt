package com.raven.launcher.desktop

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.raven.launcher.R
import com.raven.launcher.startmenu.StartMenuView
import com.raven.launcher.taskbar.TaskbarView

class DesktopActivity : AppCompatActivity() {

    private lateinit var startMenu: StartMenuView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_desktop)

        val desktop =
            findViewById<FrameLayout>(R.id.raven_desktop)

        // =========================
        // START MENU
        // =========================

        startMenu = StartMenuView(this)

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
}
