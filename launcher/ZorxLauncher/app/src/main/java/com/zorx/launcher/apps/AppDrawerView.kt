package com.zorx.launcher.apps

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class AppDrawerView(
    context: Context,
    private val onPinnedAppsChanged: () -> Unit = {},
    private val onAppLaunched: () -> Unit = {},
    private val onDismiss: () -> Unit = {}
) : ScrollView(context) {

    fun applyShellRadius(radiusPx: Float) {
        background = GradientDrawable().apply {
            setColor(Color.rgb(20, 22, 28))
            setStroke(1, Color.argb(75, 255, 255, 255))
            cornerRadius = radiusPx
        }
        clipToOutline = radiusPx > 0f
        invalidate()
    }

    private var backgroundDownX = 0f
    private var backgroundDownY = 0f

    private val appManager =
        AppManager(context)

    private val pinnedAppManager =
        PinnedAppManager(context)

    private val installedApps =
        appManager.getInstalledApps()

    private val grid =
        GridLayout(context).apply {

            columnCount = 5

            setPadding(
                30,
                30,
                30,
                30
            )
        }

    init {

        applyShellRadius(0f)

        addView(grid)

        renderApps(installedApps)
    }

    fun setQuery(query: String) {

        val normalizedQuery =
            query.trim()

        val apps =
            if (normalizedQuery.isBlank()) {

                installedApps

            } else {

                installedApps.filter { app ->

                    app.loadLabel(
                        context.packageManager
                    )
                        .toString()
                        .contains(
                            normalizedQuery,
                            ignoreCase = true
                        ) ||
                        app.activityInfo.packageName.contains(
                            normalizedQuery,
                            ignoreCase = true
                        )
                }
            }

        renderApps(apps)
    }



    private fun renderApps(
        apps: List<ResolveInfo>
    ) {

        grid.removeAllViews()

        if (apps.isEmpty()) {

            grid.addView(
                TextView(context).apply {

                    text =
                        "No applications found"

                    setTextColor(
                        Color.WHITE
                    )

                    textSize = 13f

                    setPadding(
                        30,
                        30,
                        30,
                        30
                    )
                }
            )

            return
        }

        apps.forEach { app ->

            val appContainer =
                LinearLayout(context).apply {

                    orientation =
                        LinearLayout.VERTICAL

                    gravity =
                        Gravity.CENTER

                    setPadding(
                        20,
                        20,
                        20,
                        20
                    )

                    isClickable = true
                    isFocusable = true

                    setOnClickListener {

                        appManager.launchApp(app)

                        onAppLaunched()
                    }

                    setOnLongClickListener {

                        val appName =
                            app.loadLabel(
                                context.packageManager
                            )
                                .toString()

                        val currentlyPinned =
                            pinnedAppManager.isPinned(app)

                        val action =
                            if (currentlyPinned) {
                                "Unpin from Taskbar"
                            } else {
                                "Pin to Taskbar"
                            }

                        AlertDialog.Builder(context)
                            .setTitle(appName)
                            .setItems(
                                arrayOf(action)
                            ) { _, _ ->

                                if (currentlyPinned) {

                                    pinnedAppManager.unpinApp(app)

                                    Toast.makeText(
                                        context,
                                        "$appName removed from Taskbar",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {

                                    pinnedAppManager.pinApp(app)

                                    Toast.makeText(
                                        context,
                                        "$appName pinned to Taskbar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                onPinnedAppsChanged()
                            }
                            .show()

                        true
                    }
                }

            val icon =
                ImageView(context).apply {

                    setImageDrawable(
                        app.loadIcon(
                            context.packageManager
                        )
                    )

                    layoutParams =
                        LinearLayout.LayoutParams(
                            72,
                            72
                        )
                }

            val label =
                TextView(context).apply {

                    text =
                        app.loadLabel(
                            context.packageManager
                        )

                    setTextColor(
                        Color.WHITE
                    )

                    textSize = 13f

                    gravity =
                        Gravity.CENTER

                    maxLines =
                        2

                    setPadding(
                        5,
                        8,
                        5,
                        5
                    )
                }

            appContainer.addView(icon)
            appContainer.addView(label)

            val itemParams =
                GridLayout.LayoutParams().apply {

                    width =
                        180

                    height =
                        150

                    setMargins(
                        10,
                        10,
                        10,
                        10
                    )
                }

            grid.addView(
                appContainer,
                itemParams
            )
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {

                backgroundDownX =
                    event.x

                backgroundDownY =
                    event.y
            }

            MotionEvent.ACTION_UP -> {

                val movedX =
                    kotlin.math.abs(
                        event.x - backgroundDownX
                    )

                val movedY =
                    kotlin.math.abs(
                        event.y - backgroundDownY
                    )

                if (
                    movedX < 12f &&
                    movedY < 12f
                ) {

                    onDismiss()

                    return true
                }
            }
        }

        return super.onTouchEvent(event)
    }

}
