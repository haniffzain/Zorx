package com.raven.launcher.startmenu

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.raven.launcher.apps.AppManager
import com.raven.launcher.apps.PinnedAppManager
import com.raven.launcher.design.LumaColors
import com.raven.launcher.design.LumaRadius

class StartMenuView(
    context: Context,
    private val onApplicationsClick: () -> Unit
) : ScrollView(context) {

    private val menuContainer =
        LinearLayout(context)

    private val appManager =
        AppManager(context)

    private val pinnedAppManager =
        PinnedAppManager(context)

    private val installedApps =
        appManager.getInstalledApps()

    init {

        isFillViewport = true

        background =
            GradientDrawable().apply {

                setColor(
                    LumaColors.Surface
                )

                cornerRadius =
                    LumaRadius.Widget
            }

        setPadding(
            18,
            18,
            18,
            18
        )

        menuContainer.orientation =
            LinearLayout.VERTICAL

        menuContainer.gravity =
            Gravity.TOP

        addView(
            menuContainer,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        )

        buildHeader()

        buildSearch()

        buildPinnedApps()

        buildMenuItems()

        buildPower()
    }

    private fun buildHeader() {

        val header =
            TextView(context).apply {

                text = "LumaOS"

                textSize = 24f

                setTextColor(
                    LumaColors.TextPrimary
                )

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    8,
                    4,
                    8,
                    14
                )
            }

        menuContainer.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun buildSearch() {

        val search =
            EditText(context).apply {

                hint =
                    "Search applications..."

                textSize = 15f

                setTextColor(
                    LumaColors.TextPrimary
                )

                setHintTextColor(
                    LumaColors.TextSecondary
                )

                setSingleLine(true)

                setPadding(
                    16,
                    10,
                    16,
                    10
                )

                background =
                    GradientDrawable().apply {

                        setColor(
                            LumaColors.Background
                        )

                        cornerRadius =
                            LumaRadius.Button
                    }
            }

        menuContainer.addView(
            search,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                52
            )
        )
    }

    private fun buildPinnedApps() {

        val title =
            TextView(context).apply {

                text = "Pinned"

                textSize = 14f

                setTextColor(
                    LumaColors.TextSecondary
                )

                setPadding(
                    8,
                    18,
                    8,
                    8
                )
            }

        menuContainer.addView(title)

        val pinnedArea =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL
            }

        val pinnedIds =
            pinnedAppManager
                .getPinnedIds()

        pinnedIds.forEach { appId ->

            val packageName =
                pinnedAppManager
                    .getPackageName(appId)

            val activityName =
                pinnedAppManager
                    .getActivityName(appId)

            val app =
                installedApps.find {

                    it.activityInfo.packageName ==
                        packageName &&
                    it.activityInfo.name ==
                        activityName
                }

            if (app != null) {

                pinnedArea.addView(
                    createPinnedApp(app),
                    LinearLayout.LayoutParams(
                        74,
                        82
                    )
                )
            }
        }

        menuContainer.addView(
            pinnedArea,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                86
            )
        )
    }

    private fun createPinnedApp(
        app: ResolveInfo
    ): View {

        val container =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.VERTICAL

                gravity =
                    Gravity.CENTER

                setPadding(
                    4,
                    4,
                    4,
                    4
                )

                background =
                    GradientDrawable().apply {

                        setColor(
                            LumaColors.Background
                        )

                        cornerRadius =
                            LumaRadius.Button
                    }

                setOnClickListener {

                    appManager.launchApp(app)
                }
            }

        val icon =
            ImageView(context).apply {

                setImageDrawable(
                    app.loadIcon(
                        context.packageManager
                    )
                )
            }

        container.addView(
            icon,
            LinearLayout.LayoutParams(
                42,
                42
            )
        )

        val label =
            TextView(context).apply {

                text =
                    app.loadLabel(
                        context.packageManager
                    )

                textSize = 10f

                setTextColor(
                    LumaColors.TextPrimary
                )

                gravity =
                    Gravity.CENTER

                maxLines = 1
            }

        container.addView(
            label,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                22
            )
        )

        return container
    }

    private fun buildMenuItems() {

        addMenuItem(
            "Applications",
            true
        ) {
            onApplicationsClick()
        }

        addMenuItem("Files")

        addMenuItem("Settings")

        addMenuItem("Search")
    }

    private fun addMenuItem(
        title: String,
        arrow: Boolean = false,
        onClick: (() -> Unit)? = null
    ) {

        val item =
            TextView(context).apply {

                text =
                    if (arrow) {
                        "$title    ›"
                    } else {
                        title
                    }

                textSize = 16f

                setTextColor(
                    LumaColors.TextPrimary
                )

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    14,
                    8,
                    14,
                    8
                )

                background =
                    GradientDrawable().apply {

                        setColor(
                            LumaColors.Surface
                        )

                        cornerRadius =
                            LumaRadius.Button
                    }

                if (onClick != null) {

                    isClickable = true

                    setOnClickListener {
                        onClick()
                    }
                }
            }

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48
            )

        params.setMargins(
            0,
            3,
            0,
            3
        )

        menuContainer.addView(
            item,
            params
        )
    }

    private fun buildPower() {

        val spacer =
            View(context)

        menuContainer.addView(
            spacer,
            LinearLayout.LayoutParams(
                1,
                10
            )
        )

        addMenuItem(
            "⏻  Power"
        )
    }
}
