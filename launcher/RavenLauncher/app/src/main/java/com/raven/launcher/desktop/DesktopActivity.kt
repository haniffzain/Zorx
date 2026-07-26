package com.raven.launcher.desktop

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.raven.launcher.R
import com.raven.launcher.taskbar.TaskbarView

class DesktopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_desktop)

        val desktop =
            findViewById<FrameLayout>(R.id.raven_desktop)

        val taskbar = TaskbarView(this)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            80
        )

        params.gravity =
            android.view.Gravity.BOTTOM

        desktop.addView(
            taskbar,
            params
        )
    }
}
