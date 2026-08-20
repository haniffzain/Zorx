package com.zorx.launcher.startmenu

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.zorx.launcher.apps.AppManager
import com.zorx.launcher.apps.PinnedAppManager
import com.zorx.launcher.design.ZorxColors
import com.zorx.launcher.design.ZorxRadius

class StartMenuView(
    context: Context,
    private val onApplicationsClick: (String) -> Unit
) : ScrollView(context) {

    private val appManager =
        AppManager(context)

    private val pinnedAppManager =
        PinnedAppManager(context)

    private val installedApps =
        appManager.getInstalledApps()

    private val menuContainer =
        LinearLayout(context).apply {

            orientation =
                LinearLayout.VERTICAL

            setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
            )
        }

    init {

        isFillViewport = true

        clipToOutline = true

        background =
            GradientDrawable().apply {

                setColor(
                    Color.argb(
                        246,
                        24,
                        26,
                        31
                    )
                )

                setStroke(
                    dp(1),
                    Color.argb(
                        75,
                        255,
                        255,
                        255
                    )
                )

                cornerRadius =
                    dpF(ZorxRadius.Widget)
            }

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

        buildQuickActions()

        buildFooter()
    }

    // =========================================================
    // HEADER
    // =========================================================

    private fun buildHeader() {

        val header =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    dp(4),
                    dp(2),
                    dp(4),
                    dp(14)
                )
            }

        val icon =
            TextView(context).apply {

                text =
                    "✦"

                textSize =
                    24f

                gravity =
                    Gravity.CENTER

                setTextColor(
                    ZorxColors.Accent
                )

                background =
                    roundedBackground(
                        Color.argb(
                            35,
                            62,
                            214,
                            208
                        ),
                        ZorxRadius.Button
                    )
            }

        header.addView(
            icon,
            LinearLayout.LayoutParams(
                dp(46),
                dp(46)
            )
        )

        val titleArea =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(12),
                    0,
                    0,
                    0
                )
            }

        val title =
            TextView(context).apply {

                text =
                    "Zorx"

                textSize =
                    21f

                setTextColor(
                    ZorxColors.TextPrimary
                )
            }

        val subtitle =
            TextView(context).apply {

                text =
                    "Your desktop, your space"

                textSize =
                    11f

                setTextColor(
                    ZorxColors.TextSecondary
                )

                setPadding(
                    0,
                    dp(2),
                    0,
                    0
                )
            }

        titleArea.addView(title)

        titleArea.addView(subtitle)

        header.addView(
            titleArea,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val status =
            TextView(context).apply {

                text =
                    "●"

                textSize =
                    10f

                setTextColor(
                    ZorxColors.Success
                )

                gravity =
                    Gravity.CENTER
            }

        header.addView(
            status,
            LinearLayout.LayoutParams(
                dp(30),
                dp(46)
            )
        )

        menuContainer.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private fun buildSearch() {

        val searchContainer =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                background =
                    roundedBackground(
                        ZorxColors.Background,
                        ZorxRadius.Button
                    )

                setPadding(
                    dp(14),
                    0,
                    dp(14),
                    0
                )
            }

        val icon =
            TextView(context).apply {

                text =
                    "⌕"

                textSize =
                    22f

                setTextColor(
                    ZorxColors.TextSecondary
                )

                gravity =
                    Gravity.CENTER
            }

        searchContainer.addView(
            icon,
            LinearLayout.LayoutParams(
                dp(32),
                dp(52)
            )
        )

        val search =
            EditText(context).apply {

                hint =
                    "Search applications..."

                textSize =
                    14f

                setTextColor(
                    ZorxColors.TextPrimary
                )

                setHintTextColor(
                    ZorxColors.TextSecondary
                )

                setSingleLine(true)

                background =
                    ColorDrawableTransparent()

                setPadding(
                    dp(4),
                    0,
                    dp(4),
                    0
                )

                isSingleLine = true

                imeOptions =
                    EditorInfo.IME_ACTION_SEARCH

                setOnEditorActionListener { _, actionId, _ ->

                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        onApplicationsClick(
                            text.toString().trim()
                        )

                        true

                    } else {

                        false
                    }
                }
            }

        searchContainer.addView(
            search,
            LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            )
        )

        val shortcut =
            TextView(context).apply {

                text =
                    "⌘"

                textSize =
                    13f

                setTextColor(
                    ZorxColors.TextSecondary
                )

                gravity =
                    Gravity.CENTER
            }

        searchContainer.addView(
            shortcut,
            LinearLayout.LayoutParams(
                dp(30),
                dp(52)
            )
        )

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(54)
            )

        params.setMargins(
            0,
            0,
            0,
            dp(14)
        )

        menuContainer.addView(
            searchContainer,
            params
        )
    }

    // =========================================================
    // PINNED
    // =========================================================

    private fun buildPinnedApps() {

        addSectionTitle(
            "PINNED"
        )

        val pinnedArea =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL
            }

        val pinnedIds =
            pinnedAppManager.getPinnedIds()

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
                        dp(76),
                        dp(82)
                    ).apply {
                        setMargins(
                            0,
                            0,
                            dp(8),
                            0
                        )
                    }
                )
            }
        }

        if (pinnedIds.isEmpty()) {

            val empty =
                TextView(context).apply {

                    text =
                        "No pinned applications yet"

                    textSize =
                        12f

                    setTextColor(
                        ZorxColors.TextSecondary
                    )

                    gravity =
                        Gravity.CENTER_VERTICAL

                    setPadding(
                        dp(12),
                        0,
                        0,
                        0
                    )
                }

            pinnedArea.addView(
                empty,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(68)
                )
            )
        }

        menuContainer.addView(
            pinnedArea,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(86)
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

                background =
                    roundedBackground(
                        ZorxColors.Surface,
                        ZorxRadius.Button
                    )

                setPadding(
                    dp(4),
                    dp(5),
                    dp(4),
                    dp(4)
                )

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
                dp(42),
                dp(42)
            )
        )

        val label =
            TextView(context).apply {

                text =
                    app.loadLabel(
                        context.packageManager
                    )

                textSize =
                    9f

                setTextColor(
                    ZorxColors.TextPrimary
                )

                gravity =
                    Gravity.CENTER

                maxLines =
                    1
            }

        container.addView(
            label,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(20)
            )
        )

        return container
    }

    // =========================================================
    // QUICK ACTIONS
    // =========================================================

    private fun buildQuickActions() {

        addSectionTitle(
            "QUICK ACCESS"
        )

        val row1 =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL
            }

        row1.addView(
            createAction(
                "◈",
                "Applications"
            ) {
                onApplicationsClick("")
            },
            actionParams()
        )

        row1.addView(
            createAction(
                "⚙",
                "Settings"
            ) {
                openSettings()
            },
            actionParams()
        )

        menuContainer.addView(
            row1,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(70)
            )
        )

        val row2 =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                setPadding(
                    0,
                    dp(8),
                    0,
                    0
                )
            }

        row2.addView(
            createAction(
                "◇",
                "Files"
            ) {
                openFiles()
            },
            actionParams()
        )

        row2.addView(
            createAction(
                "⌕",
                "Search"
            ) {
                onApplicationsClick("")
            },
            actionParams()
        )

        menuContainer.addView(
            row2,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(78)
            )
        )
    }

    private fun createAction(
        iconText: String,
        title: String,
        onClick: () -> Unit
    ): View {

        val action =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                background =
                    roundedBackground(
                        ZorxColors.Surface,
                        ZorxRadius.Button
                    )

                setPadding(
                    dp(12),
                    0,
                    dp(10),
                    0
                )

                isClickable = true

                setOnClickListener {
                    onClick()
                }
            }

        val icon =
            TextView(context).apply {

                text =
                    iconText

                textSize =
                    20f

                setTextColor(
                    ZorxColors.Accent
                )

                gravity =
                    Gravity.CENTER
            }

        action.addView(
            icon,
            LinearLayout.LayoutParams(
                dp(36),
                dp(54)
            )
        )

        val label =
            TextView(context).apply {

                text =
                    title

                textSize =
                    13f

                setTextColor(
                    ZorxColors.TextPrimary
                )

                gravity =
                    Gravity.CENTER_VERTICAL
            }

        action.addView(
            label,
            LinearLayout.LayoutParams(
                0,
                dp(54),
                1f
            )
        )

        return action
    }

    private fun actionParams():
        LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1f
        ).apply {

            setMargins(
                0,
                0,
                dp(6),
                0
            )
        }
    }

    // =========================================================
    // FOOTER
    // =========================================================

    private fun buildFooter() {

        val separator =
            View(context).apply {

                setBackgroundColor(
                    Color.argb(
                        65,
                        255,
                        255,
                        255
                    )
                )
            }

        val separatorParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            )

        separatorParams.setMargins(
            0,
            dp(16),
            0,
            dp(12)
        )

        menuContainer.addView(
            separator,
            separatorParams
        )

        val footer =
            LinearLayout(context).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    dp(10),
                    0,
                    dp(10),
                    0
                )
            }

        val version =
            TextView(context).apply {

                text =
                    "Zorx Shell"

                textSize =
                    11f

                setTextColor(
                    ZorxColors.TextSecondary
                )
            }

        footer.addView(
            version,
            LinearLayout.LayoutParams(
                0,
                dp(44),
                1f
            )
        )

        val power =
            TextView(context).apply {

                text =
                    "⏻  Power"

                textSize =
                    12f

                setTextColor(
                    ZorxColors.TextSecondary
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    dp(12),
                    0,
                    dp(12),
                    0
                )

                background =
                    roundedBackground(
                        Color.TRANSPARENT,
                        ZorxRadius.Button
                    )
            }

        footer.addView(
            power,
            LinearLayout.LayoutParams(
                dp(90),
                dp(40)
            )
        )

        menuContainer.addView(
            footer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(44)
            )
        )
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private fun addSectionTitle(
        title: String
    ) {

        val label =
            TextView(context).apply {

                text =
                    title

                textSize =
                    10f

                setTextColor(
                    ZorxColors.TextSecondary
                )

                setPadding(
                    dp(4),
                    dp(3),
                    0,
                    dp(7)
                )
            }

        menuContainer.addView(
            label,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(28)
            )
        )
    }

    // =========================================================
    // ACTIONS
    // =========================================================

    private fun openSettings() {

        try {

            context.startActivity(
                Intent(
                    Settings.ACTION_SETTINGS
                ).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                }
            )

        } catch (_: Exception) {
        }
    }

    private fun openFiles() {

        try {

            context.startActivity(
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply {

                    type =
                        "*/*"

                    addCategory(
                        Intent.CATEGORY_OPENABLE
                    )

                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                }
            )

        } catch (_: Exception) {
        }
    }

    // =========================================================
    // DRAWABLE HELPERS
    // =========================================================

    private fun roundedBackground(
        color: Int,
        radius: Float
    ): GradientDrawable {

        return GradientDrawable().apply {

            setColor(color)

            cornerRadius =
                dpF(radius)
        }
    }

    private fun ColorDrawableTransparent():
        android.graphics.drawable.ColorDrawable {

        return android.graphics.drawable.ColorDrawable(
            Color.TRANSPARENT
        )
    }

    // =========================================================
    // DP HELPERS
    // =========================================================

    private fun dp(
        value: Int
    ): Int {

        return (
            value *
                resources.displayMetrics.density
        ).toInt()
    }

    private fun dpF(
        value: Float
    ): Float {

        return value *
            resources.displayMetrics.density
    }
}
