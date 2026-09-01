package com.zorx.launcher.desktop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import com.zorx.launcher.events.ZorxEvent
import com.zorx.launcher.events.ZorxEventBus
import com.zorx.launcher.events.ZorxEventListener
import com.zorx.launcher.runtime.DesktopRuntime
import com.zorx.launcher.interaction.WindowInteractionController
import com.zorx.launcher.events.desktop.DesktopAddedEvent
import com.zorx.launcher.events.desktop.DesktopFocusedEvent
import com.zorx.launcher.events.desktop.DesktopMovedEvent
import com.zorx.launcher.events.desktop.DesktopRemovedEvent
import com.zorx.launcher.events.desktop.DesktopStateChangedEvent
import com.zorx.launcher.spatial.SpatialBounds
import com.zorx.launcher.shell.ZorxShellSettingsStore
import com.zorx.launcher.workspace.ZorxWorkspaceId
import com.zorx.launcher.display.ZorxDisplayId

class DesktopSurface @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs), ZorxEventListener {

    var onEmptyDesktopSecondaryClick:
        ((Float, Float) -> Unit)? = null

    private val runtime =
        DesktopRuntime(context)

    val spatialEngine
        get() = runtime.spatialEngine

    fun moveWindowToWorkspace(windowId: String, workspaceId: ZorxWorkspaceId) =
        runtime.moveWindowToWorkspace(windowId, workspaceId)

    fun moveWindowToDisplay(windowId: String, displayId: ZorxDisplayId) =
        runtime.moveWindowToDisplay(windowId, displayId)

    private val scene =
        DesktopScene(runtime)
    private val interactionController =
        WindowInteractionController(
            runtime.spatialEngine,
            {
                width to height
            },
            {
                val metrics = ZorxShellSettingsStore.resolve(
                    context,
                    width.coerceAtLeast(1),
                    height.coerceAtLeast(1)
                )
                SpatialBounds(
                    0,
                    0,
                    width.coerceAtLeast(1),
                    (height - metrics.taskbarHeightPx - metrics.taskbarBottomMarginPx)
                        .coerceAtLeast(1)
                )
            },
            {
                ZorxShellSettingsStore.resolve(
                    context,
                    width.coerceAtLeast(1),
                    height.coerceAtLeast(1)
                ).titlebarHitHeightPx
            }
        )

    private val lastRenderedBounds =
        mutableMapOf<String, SpatialBounds>()

    private val pendingDirtyRect =
        Rect()

    private fun scheduleDesktopRedraw(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {

        if (pendingDirtyRect.isEmpty) {

            pendingDirtyRect.set(
                left,
                top,
                right,
                bottom
            )

        } else {

            pendingDirtyRect.union(
                left,
                top,
                right,
                bottom
            )
        }

        invalidate(
            pendingDirtyRect
        )

        pendingDirtyRect.setEmpty()
    }


    init {

        ZorxEventBus.register(this)

    }

    override fun onEvent(
        event: ZorxEvent
    ) {

        when (event) {

            is DesktopMovedEvent -> {

                val objectId =
                    event.desktopObject.id

                val oldBounds =
                    lastRenderedBounds[objectId]

                val newBounds =
                    event.desktopObject.bounds

                if (oldBounds != null) {

                    val dirtyLeft =
                        minOf(
                            oldBounds.x,
                            newBounds.x
                        )

                    val dirtyTop =
                        minOf(
                            oldBounds.y,
                            newBounds.y
                        )

                    val dirtyRight =
                        maxOf(
                            oldBounds.x + oldBounds.width,
                            newBounds.x + newBounds.width
                        )

                    val dirtyBottom =
                        maxOf(
                            oldBounds.y + oldBounds.height,
                            newBounds.y + newBounds.height
                        )

                    scheduleDesktopRedraw(
                        dirtyLeft,
                        dirtyTop,
                        dirtyRight,
                        dirtyBottom
                    )

                } else {

                    scheduleDesktopRedraw(
                        newBounds.x,
                        newBounds.y,
                        newBounds.x + newBounds.width,
                        newBounds.y + newBounds.height
                    )
                }

                lastRenderedBounds[objectId] =
                    newBounds.copy()
            }

            is DesktopAddedEvent -> {

                val bounds =
                    event.desktopObject.bounds

                lastRenderedBounds[
                    event.desktopObject.id
                ] = bounds.copy()

                scheduleDesktopRedraw(
                    bounds.x,
                    bounds.y,
                    bounds.x + bounds.width,
                    bounds.y + bounds.height
                )
            }

            is DesktopRemovedEvent -> {

                lastRenderedBounds.remove(
                    event.objectId
                )

                invalidate()
            }

            is DesktopFocusedEvent,
            is DesktopStateChangedEvent -> {

                scheduleDesktopRedraw(
                    0,
                    0,
                    width,
                    height
                )
            }

        }

    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        val secondaryClick =
            event.actionMasked == MotionEvent.ACTION_DOWN &&
                (
                    event.buttonState and MotionEvent.BUTTON_SECONDARY != 0 ||
                        event.actionButton == MotionEvent.BUTTON_SECONDARY
                )

        if (secondaryClick) {

            if (
                spatialEngine.findTopmostAt(
                    event.x,
                    event.y
                ) == null
            ) {
                onEmptyDesktopSecondaryClick?.invoke(
                    event.x,
                    event.y
                )
            }

            return true
        }

        if (
            event.actionMasked ==
                MotionEvent.ACTION_MOVE
        ) {

            return interactionController.onTouchEvent(
                event
            ) {
                postOnAnimation {
                    interactionController.processQueuedMove()
                }
            }
        }

        if (interactionController.onTouchEvent(event)) {
            return true
        }

        return super.onTouchEvent(event)

    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        scene.render(
            canvas
        )

    }

    override fun onDetachedFromWindow() {

        ZorxEventBus.unregister(this)

        runtime.destroy()

        super.onDetachedFromWindow()

    }
}
