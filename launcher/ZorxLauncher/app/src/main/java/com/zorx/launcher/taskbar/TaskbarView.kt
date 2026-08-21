package com.zorx.launcher.taskbar

import android.graphics.Color
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.shell.ZorxShellSettingsStore
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
import com.zorx.launcher.workspace.ZorxWorkspaceId
import com.zorx.launcher.workspace.ZorxWorkspaceManager
import com.zorx.launcher.display.ZorxDisplayId
import com.zorx.launcher.display.ZorxDisplayManager
import com.zorx.launcher.windowing.ZorxWindowLocationManager
import android.widget.PopupWindow
import android.widget.HorizontalScrollView

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

    private val onRunningWindowClicked: (String) -> Unit,
    private val onMoveWindowToWorkspace: (String, ZorxWorkspaceId) -> Unit,
    private val onMoveWindowToDisplay: (String, ZorxDisplayId) -> Unit

) : LinearLayout(context) {

    private var iconSizePx = dpPx(32)

    /** Independent from the taskbar height; rebuilds only the pinned icon strip. */
    fun applyIconSize(sizePx: Int) {
        val safeSize = sizePx.coerceAtLeast(dpPx(20))
        if (iconSizePx != safeSize) {
            iconSizePx = safeSize
            refreshPinnedApps()
        }
    }

    private val shellPanelListener = {
        post {
            refreshRunningWindows()
        }
        Unit
    }
    private val workspaceListener = { post { refreshWorkspaceSwitcher(); refreshRunningWindows() }; Unit }

    private val workspaceContainer = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dpPx(4), 0, dpPx(4), 0)
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

    private val runningScrollContainer = HorizontalScrollView(context).apply {
        isHorizontalScrollBarEnabled = false
        addView(runningContainer, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
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
        ZorxWorkspaceManager.addListener(workspaceListener)

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

                textSize = ZorxTypography.effectivePx(
                        context,
                        ZorxShellSettingsStore.readTypography(context),
                        ZorxShellSettingsStore.readTypography(context).taskbarTextSp
                    )

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

        addView(workspaceContainer, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))

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
            runningScrollContainer,
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

        refreshWorkspaceSwitcher()
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

                textSize = iconSizePx / resources.displayMetrics.scaledDensity * 0.58f

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
                iconSizePx,
                LayoutParams.MATCH_PARENT
            ).apply { setMargins(dpPx(2), 0, dpPx(2), 0) }
        )
    }

    // =========================================================
    // RUNNING WINDOWS
    // =========================================================

    private fun refreshWorkspaceSwitcher() {
        workspaceContainer.removeAllViews()
        val allWindows = spatialEngine.getAllObjects()
        val active = ZorxWorkspaceManager.active(context)
        ZorxWorkspaceManager.workspaces().forEach { workspace ->
            val hasWindows = allWindows.any { ZorxWorkspaceManager.workspaceFor(context, it.id) == workspace.id }
            workspaceContainer.addView(TextView(context).apply {
                text = workspace.order.toString()
                gravity = Gravity.CENTER
                textSize = ZorxTypography.effectivePx(context, ZorxShellSettingsStore.readTypography(context), ZorxShellSettingsStore.readTypography(context).taskbarTextSp)
                setTextColor(if (workspace.id == active) ZorxColors.TextPrimary else ZorxColors.TextSecondary)
                background = GradientDrawable().apply {
                    setColor(when { workspace.id == active -> ZorxColors.Accent; hasWindows -> ZorxColors.Surface; else -> Color.TRANSPARENT })
                    if (hasWindows && workspace.id != active) setStroke(dpPx(1), ZorxColors.Border)
                    cornerRadius = ZorxRadius.Button
                }
                alpha = if (workspace.id == active || hasWindows) 1f else .55f
                setOnClickListener { ZorxWorkspaceManager.switchWorkspace(context, workspace.id) }
            }, LayoutParams(dpPx(28), LayoutParams.MATCH_PARENT).apply { setMargins(dpPx(1), dpPx(6), dpPx(1), dpPx(6)) })
        }
    }

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

        val allWindows = spatialEngine.getAllObjects()
        val topology = ZorxDisplayManager(context).topology(ZorxShellSettingsStore.readDisplay(context).displayScale)
        val primaryDisplay = topology.primary()?.id
        val activeDisplay = ZorxActiveDisplayResolver.displayFor(context, allWindows)
        val targetDisplay = when (ZorxTaskbarPolicyStore.read(context)) {
            ZorxTaskbarDisplayPolicy.PRIMARY_ONLY -> primaryDisplay
            ZorxTaskbarDisplayPolicy.PER_DISPLAY, ZorxTaskbarDisplayPolicy.MIRRORED -> activeDisplay
        }
        val runningWindows = allWindows.filter { window ->
            val location = ZorxWindowLocationManager.ensure(context, window, topology)
            ZorxWorkspaceManager.workspaceFor(context, window.id) == ZorxWorkspaceManager.active(context) &&
                (targetDisplay == null || location?.displayId == targetDisplay)
        }.sortedByDescending { it.zIndex }

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

                    textSize = ZorxTypography.effectivePx(
                        context,
                        ZorxShellSettingsStore.readTypography(context),
                        ZorxShellSettingsStore.readTypography(context).taskbarTextSp
                    )

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
                    setOnLongClickListener {
                        showWindowActions(this, desktopObject.id)
                        true
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
                    textSize = ZorxTypography.effectivePx(
                        context,
                        ZorxShellSettingsStore.readTypography(context),
                        ZorxShellSettingsStore.readTypography(context).taskbarTextSp
                    )
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
        ZorxWorkspaceManager.removeListener(workspaceListener)
    }

    private fun showWindowActions(anchor: View, windowId: String) {
        val menu = LinearLayout(context).apply { orientation = VERTICAL; setPadding(dpPx(8), dpPx(8), dpPx(8), dpPx(8)); background = GradientDrawable().apply { setColor(ZorxColors.Surface); setStroke(dpPx(1), ZorxColors.Border); cornerRadius = ZorxRadius.Button } }
        val popup = PopupWindow(menu, dpPx(220), LayoutParams.WRAP_CONTENT, true).apply { isOutsideTouchable = true; elevation = dpPx(12).toFloat() }
        fun action(label: String, run: () -> Unit) = menu.addView(TextView(context).apply {
            text = label; gravity = Gravity.CENTER_VERTICAL; setTextColor(ZorxColors.TextPrimary); setPadding(dpPx(12), dpPx(9), dpPx(12), dpPx(9))
            setOnClickListener { popup.dismiss(); run() }
        }, LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        ZorxWorkspaceManager.workspaces().forEach { action("Move to Workspace ${it.order}") { onMoveWindowToWorkspace(windowId, it.id) } }
        ZorxDisplayManager(context).topology(ZorxShellSettingsStore.readDisplay(context).displayScale).displays.forEach { display -> action("Move to ${display.name}") { onMoveWindowToDisplay(windowId, display.id) } }
        popup.showAsDropDown(anchor)
    }


    private fun dpPx(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

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

                textSize = ZorxTypography.effectivePx(
                        context,
                        ZorxShellSettingsStore.readTypography(context),
                        ZorxShellSettingsStore.readTypography(context).taskbarTextSp
                    )

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
