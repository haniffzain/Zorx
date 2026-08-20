package com.zorx.launcher.taskbar

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.zorx.launcher.design.ZorxColors
import com.zorx.launcher.design.ZorxRadius
import com.zorx.launcher.spatial.SpatialEngine
import com.zorx.launcher.settings.ZorxSettingsActivity
import com.zorx.launcher.shell.ShellPanelState
import com.zorx.launcher.shell.ZorxShellPanelManager

/**
 * Zorx floating taskbar.
 *
 * Visual direction:
 * - Floating desktop dock
 * - Rounded glass-like surface
 * - Zorx colour system
 * - Compact application area
 * - System status area
 *
 * This view intentionally remains lightweight.
 * Window/compositor logic stays outside the taskbar.
 */
class TaskbarView(

    private val context: android.content.Context,

    private val spatialEngine: SpatialEngine,

    private val onStartClicked: () -> Unit,

    private val onRunningWindowClicked: (String) -> Unit

) : LinearLayout(context) {

    private val shellPanelListener = {
        post {
            refreshRunningWindows()
        }
        Unit
    }

    fun applyShellRadius(
        radiusPx: Float
    ) {

        background =
            GradientDrawable().apply {

                setColor(
                    Color.argb(
                        238,
                        26,
                        28,
                        32
                    )
                )

                setStroke(
                    1,
                    Color.argb(
                        90,
                        255,
                        255,
                        255
                    )
                )

                cornerRadius =
                    radiusPx
            }

        invalidate()
    }

    private val appContainer =
        LinearLayout(context).apply {

            orientation =
                HORIZONTAL

            gravity =
                Gravity.CENTER_VERTICAL

            setPadding(
                4,
                0,
                4,
                0
            )
        }

    /*
     * Zorx running-window strip.
     *
     * Kept separate from appContainer so refreshPinnedApps()
     * cannot accidentally remove running-window buttons.
     */
    private val runningContainer =
        LinearLayout(context).apply {

            orientation =
                HORIZONTAL

            gravity =
                Gravity.CENTER_VERTICAL

            setPadding(
                6,
                0,
                6,
                0
            )
        }

    private val statusContainer =
        LinearLayout(context).apply {

            orientation =
                HORIZONTAL

            gravity =
                Gravity.CENTER_VERTICAL

            setPadding(
                8,
                0,
                4,
                0
            )
        }

    init {

        ZorxShellPanelManager.addListener(
            shellPanelListener
        )

        orientation =
            HORIZONTAL

        gravity =
            Gravity.CENTER_VERTICAL

        setPadding(
            12,
            6,
            12,
            6
        )

        background =
            GradientDrawable().apply {

                setColor(
                    Color.argb(
                        238,
                        26,
                        28,
                        32
                    )
                )

                setStroke(
                    1,
                    Color.argb(
                        90,
                        255,
                        255,
                        255
                    )
                )

                cornerRadius =
                    ZorxRadius.Taskbar
            }

        elevation =
            18f

        // =====================================================
        // ZORX START BUTTON
        // =====================================================

        val startButton =
            TextView(context).apply {

                text =
                    "✦  Zorx"

                textSize = 13f

                setTextColor(
                    ZorxColors.TextPrimary
                )

                gravity =
                    Gravity.CENTER

                setPadding(
                    18,
                    6,
                    18,
                    6
                )

                background =
                    GradientDrawable().apply {

                        setColor(
                            Color.argb(
                                150,
                                16,
                                17,
                                20
                            )
                        )

                        cornerRadius =
                            ZorxRadius.Button
                    }

                setOnClickListener {

                    onStartClicked()

                }
            }

        addView(
            startButton,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            )
        )

        // =====================================================
        // SEPARATOR
        // =====================================================

        val separator =
            View(context).apply {

                setBackgroundColor(
                    Color.argb(
                        80,
                        255,
                        255,
                        255
                    )
                )
            }

        val separatorParams =
            LayoutParams(
                1,
                28
            ).apply {

                setMargins(
                    12,
                    0,
                    12,
                    0
                )
            }

        addView(
            separator,
            separatorParams
        )

        // =====================================================
        // APPLICATION AREA
        // =====================================================

        val appParams =
            LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                1f
            )

        addView(
            appContainer,
            appParams
        )

        // =====================================================
        // RUNNING WINDOWS
        // =====================================================

        addView(
            runningContainer,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            )
        )

        // =====================================================
        // STATUS AREA
        // =====================================================

        addView(
            statusContainer,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            )
        )

        buildStatusArea()

        refreshPinnedApps()
    }

    // =========================================================
    // PINNED APPLICATIONS
    // =========================================================

    fun refreshPinnedApps() {

        appContainer.removeAllViews()

        addApplicationButton(
            "◉"
        )

        addApplicationButton(
            "⌂"
        )

        addApplicationButton(
            "▣"
        )

        addApplicationButton(
            ">"
        )

        /*
         * Keep the running-window strip synchronized
         * whenever the pinned section is rebuilt.
         */
        refreshRunningWindows()
    }

    private fun addApplicationButton(
        symbol: String
    ) {

        val button =
            TextView(context).apply {

                text =
                    symbol

                textSize = 13f

                gravity =
                    Gravity.CENTER

                setTextColor(
                    ZorxColors.TextSecondary
                )

                setPadding(
                    14,
                    0,
                    14,
                    0
                )

                background =
                    GradientDrawable().apply {

                        setColor(
                            Color.TRANSPARENT
                        )

                        cornerRadius =
                            ZorxRadius.Button
                    }

                setOnClickListener {

                    // Application launch behaviour
                    // will be connected later.

                }
            }

        appContainer.addView(
            button,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            )
        )
    }

    // =========================================================
    // RUNNING WINDOWS
    // =========================================================

    /**
     * Refresh hook used by TaskbarController.
     *
     * The new Zorx Shell taskbar does not yet expose
     * a dedicated running-window strip. Keep this method
     * as the synchronization hook so the controller can
     * safely notify the taskbar when desktop state changes.
     */
    fun refreshRunningWindows() {

        /*
         * Rebuild the running-window strip from
         * the shared Zorx SpatialEngine.
         *
         * Minimized windows remain here so they can
         * be restored from the taskbar.
         */

        runningContainer.removeAllViews()

        val runningWindows =
            spatialEngine
                .getAllObjects()
                .sortedByDescending {

                    it.zIndex
                }

        runningWindows.forEach { desktopObject ->

            val isMinimized =
                desktopObject.state ==
                    com.zorx.launcher.spatial.DesktopObjectState.MINIMIZED

            val isFocused =
                desktopObject.state ==
                    com.zorx.launcher.spatial.DesktopObjectState.FOCUSED

            val button =
                TextView(context).apply {

                    /*
                     * Zorx window indicator.
                     */

                    text =
                        when {

                            isMinimized ->
                                "▱  ${desktopObject.title}"

                            isFocused ->
                                "◆  ${desktopObject.title}"

                            else ->
                                "◇  ${desktopObject.title}"
                        }

                    textSize = 13f

                    setTextColor(
                        if (isFocused) {
                            ZorxColors.TextPrimary
                        } else {
                            ZorxColors.TextSecondary
                        }
                    )

                    gravity =
                        Gravity.CENTER

                    maxLines =
                        1

                    ellipsize =
                        android.text.TextUtils.TruncateAt.END

                    setPadding(
                        14,
                        0,
                        14,
                        0
                    )

                    background =
                        GradientDrawable().apply {

                            when {

                                isFocused -> {

                                    setColor(
                                        Color.argb(
                                            155,
                                            35,
                                            150,
                                            160
                                        )
                                    )

                                    setStroke(
                                        1,
                                        Color.argb(
                                            210,
                                            70,
                                            215,
                                            220
                                        )
                                    )
                                }

                                isMinimized -> {

                                    setColor(
                                        Color.argb(
                                            105,
                                            20,
                                            22,
                                            27
                                        )
                                    )

                                    setStroke(
                                        1,
                                        Color.argb(
                                            85,
                                            130,
                                            140,
                                            155
                                        )
                                    )
                                }

                                else -> {

                                    setColor(
                                        Color.argb(
                                            100,
                                            38,
                                            40,
                                            47
                                        )
                                    )

                                    setStroke(
                                        1,
                                        Color.argb(
                                            55,
                                            255,
                                            255,
                                            255
                                        )
                                    )
                                }
                            }

                            cornerRadius =
                                ZorxRadius.Button
                        }

                    alpha =
                        if (isMinimized) {
                            0.78f
                        } else {
                            1f
                        }

                    setOnClickListener {

                        onRunningWindowClicked(
                            desktopObject.id
                        )
                    }
                }

            runningContainer.addView(
                button,
                LayoutParams(
                    170,
                    LayoutParams.MATCH_PARENT
                ).apply {

                    setMargins(
                        3,
                        6,
                        3,
                        6
                    )
                }
            )
        }

        if (
            ZorxShellPanelManager.displaySettingsState ==
                ShellPanelState.MINIMIZED
        ) {

            runningContainer.addView(
                TextView(context).apply {
                    text = "▱  Display Settings"
                    textSize = 13f
                    setTextColor(ZorxColors.TextSecondary)
                    gravity = Gravity.CENTER
                    maxLines = 1
                    setPadding(14, 0, 14, 0)
                    background =
                        GradientDrawable().apply {
                            setColor(
                                Color.argb(
                                    105,
                                    20,
                                    22,
                                    27
                                )
                            )
                            setStroke(
                                1,
                                Color.argb(
                                    85,
                                    130,
                                    140,
                                    155
                                )
                            )
                            cornerRadius =
                                ZorxRadius.Button
                        }
                    setOnClickListener {
                        context.startActivity(
                            ZorxSettingsActivity.intent(
                                context,
                                ZorxSettingsActivity.SECTION_DISPLAY
                            )
                        )
                    }
                },
                LayoutParams(
                    170,
                    LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(3, 6, 3, 6)
                }
            )
        }

        runningContainer.requestLayout()
        runningContainer.invalidate()
    }

    fun destroy() {
        ZorxShellPanelManager.removeListener(
            shellPanelListener
        )
    }


    // =========================================================
    // STATUS AREA
    // =========================================================

    private fun buildStatusArea() {

        statusContainer.removeAllViews()

        addStatusItem(
            "⌁"
        )

        addStatusItem(
            "◉"
        )

        addStatusItem(
            "08:15"
        )
    }

    private fun addStatusItem(
        textValue: String
    ) {

        val item =
            TextView(context).apply {

                text =
                    textValue

                textSize = 13f

                gravity =
                    Gravity.CENTER

                setTextColor(
                    ZorxColors.TextSecondary
                )

                setPadding(
                    7,
                    0,
                    7,
                    0
                )
            }

        statusContainer.addView(
            item,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            )
        )
    }
}
