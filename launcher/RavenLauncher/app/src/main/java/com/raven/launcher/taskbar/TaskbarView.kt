package com.raven.launcher.taskbar

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.raven.launcher.apps.ActiveAppManager
import com.raven.launcher.apps.AppManager
import com.raven.launcher.apps.PinnedAppManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskbarView(
    context: Context,
    private val onStartClick: () -> Unit
) : LinearLayout(context) {

    private val clockView: TextView

    private val handler =
        Handler(Looper.getMainLooper())

    private val appManager =
        AppManager(context)

    private val pinnedAppManager =
        PinnedAppManager(context)

    private val activeAppManager =
        ActiveAppManager(context)

    private val appArea =
        LinearLayout(context)

    private val clockUpdater =
        object : Runnable {

            override fun run() {

                updateClock()

                handler.postDelayed(
                    this,
                    1000
                )
            }
        }

    init {

        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        setBackgroundColor(Color.BLACK)

        // =========================
        // START BUTTON
        // =========================

        val startButton =
            TextView(context)

        startButton.text = "🦅 Raven"
        startButton.textSize = 18f
        startButton.setTextColor(Color.WHITE)

        startButton.gravity =
            Gravity.CENTER_VERTICAL

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

        appArea.orientation =
            HORIZONTAL

        appArea.gravity =
            Gravity.CENTER_VERTICAL

        addView(
            appArea,
            LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                1f
            )
        )

        refreshPinnedApps()

        // =========================
        // SYSTEM CLOCK
        // =========================

        clockView =
            TextView(context)

        clockView.textSize = 16f
        clockView.setTextColor(Color.WHITE)

        clockView.gravity =
            Gravity.CENTER

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

    // =========================
    // PINNED APPS
    // =========================

    fun refreshPinnedApps() {

        appArea.removeAllViews()

        val installedApps =
            appManager.getInstalledApps()

        val pinnedIds =
            pinnedAppManager.getPinnedIds()

        pinnedIds.forEach { pinnedId ->

            val packageName =
                pinnedAppManager.getPackageName(
                    pinnedId
                )

            val activityName =
                pinnedAppManager.getActivityName(
                    pinnedId
                )

            val app =
                installedApps.find {

                    it.activityInfo.packageName ==
                        packageName &&
                    it.activityInfo.name ==
                        activityName
                }

            if (app != null) {

                // Container holds icon + active indicator
                val appContainer =
                    LinearLayout(context).apply {

                        orientation = VERTICAL
                        gravity = Gravity.CENTER
                    }

                val icon =
                    ImageView(context)

                icon.setImageDrawable(
                    app.loadIcon(
                        context.packageManager
                    )
                )

                icon.contentDescription =
                    app.loadLabel(
                        context.packageManager
                    ).toString()

                icon.setPadding(
                    12,
                    8,
                    12,
                    8
                )

                val indicator =
                    View(context)

                indicator.setBackgroundColor(
                    Color.rgb(
                        106,
                        90,
                        205
                    )
                )

                indicator.visibility =
                    if (
                        activeAppManager.isActive(app)
                    ) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }

                appContainer.addView(
                    icon,
                    LinearLayout.LayoutParams(
                        64,
                        58
                    )
                )

                appContainer.addView(
                    indicator,
                    LinearLayout.LayoutParams(
                        32,
                        4
                    )
                )

                appContainer.setOnClickListener {

                    val launched =
                        appManager.launchApp(app)

                    if (launched) {

                        /*
                         * AppManager has already updated
                         * ActiveAppManager at this point.
                         *
                         * Rebuild the pinned area so the
                         * indicator moves to this app.
                         */
                        refreshPinnedApps()
                    }
                }

                appArea.addView(
                    appContainer,
                    LayoutParams(
                        70,
                        70
                    )
                )
            }
        }
    }

    // =========================
    // CLOCK
    // =========================

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

        handler.removeCallbacks(
            clockUpdater
        )
    }
}
