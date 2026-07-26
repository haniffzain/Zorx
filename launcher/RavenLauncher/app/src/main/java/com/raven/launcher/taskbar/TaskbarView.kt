package com.raven.launcher.taskbar

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.raven.launcher.apps.AppManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskbarView(
    context: Context,
    private val onStartClick: () -> Unit
) : LinearLayout(context) {

    private val clockView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val appManager = AppManager(context)

    private val clockUpdater = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000)
        }
    }

    init {

        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundColor(Color.BLACK)

        // =========================
        // START BUTTON
        // =========================

        val startButton = TextView(context)

        startButton.text = "🦅 Raven"
        startButton.textSize = 18f
        startButton.setTextColor(Color.WHITE)
        startButton.gravity = Gravity.CENTER_VERTICAL

        startButton.setPadding(
            30,
            15,
            30,
            15
        )

        startButton.setOnClickListener {
            onStartClick()
        }

        addView(startButton)

        // =========================
        // PINNED APPLICATION AREA
        // =========================

        val appArea = LinearLayout(context)

        appArea.orientation = HORIZONTAL
        appArea.gravity = Gravity.CENTER_VERTICAL

        addView(
            appArea,
            LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                1f
            )
        )

        loadPinnedApps(appArea)

        // =========================
        // SYSTEM CLOCK
        // =========================

        clockView = TextView(context)

        clockView.textSize = 16f
        clockView.setTextColor(Color.WHITE)
        clockView.gravity = Gravity.CENTER

        clockView.setPadding(
            25,
            0,
            25,
            0
        )

        addView(
            clockView,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            )
        )

        updateClock()
        handler.post(clockUpdater)
    }

    private fun loadPinnedApps(
        container: LinearLayout
    ) {

        val apps = appManager.getInstalledApps()

        /*
         * Peringkat awal Taskbar v3:
         * paparkan maksimum 4 aplikasi launcher.
         */
        apps.take(4).forEach { app ->

            val icon = ImageView(context)

            icon.setImageDrawable(
                app.loadIcon(context.packageManager)
            )

            icon.contentDescription =
                app.loadLabel(context.packageManager)
                    .toString()

            icon.setPadding(
                12,
                12,
                12,
                12
            )

            val params = LayoutParams(
                70,
                70
            )

            icon.setOnClickListener {
                appManager.launchApp(app)
            }

            container.addView(
                icon,
                params
            )
        }
    }

    private fun updateClock() {

        val formatter =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            )

        clockView.text =
            formatter.format(Date())
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        handler.removeCallbacks(clockUpdater)
    }
}
