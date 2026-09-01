package com.zorx.launcher.interaction

import android.view.MotionEvent
import com.zorx.launcher.spatial.DesktopObject
import com.zorx.launcher.spatial.DesktopObjectState
import com.zorx.launcher.spatial.SpatialBounds
import com.zorx.launcher.spatial.SpatialEngine

/**
 * Handles direct manipulation of Zorx desktop windows.
 *
 * Current capabilities:
 * - Focus
 * - Z-order
 * - Drag
 * - Full-edge and corner resize
 * - Minimize
 * - Maximize / restore
 * - Close
 * - Left/right edge snapping
 */
class WindowInteractionController(
    private val spatialEngine: SpatialEngine,
    private val viewportSizeProvider: () -> Pair<Int, Int>,
    private val workAreaProvider: () -> SpatialBounds = {
        val viewport = viewportSizeProvider()
        SpatialBounds(0, 0, viewport.first, viewport.second)
    },
    private val titlebarHeightProvider: () -> Int = {
        TITLE_BAR_HEIGHT
    }
) {

    companion object {

        private const val TITLE_BAR_HEIGHT = 56
        private const val CONTROL_WIDTH = 44
        private const val RESIZE_HANDLE_SIZE = 16
        private const val MINIMUM_WIDTH = 240
        private const val MINIMUM_HEIGHT = 180

        private const val SNAP_THRESHOLD = 64
    }

    private var draggedObjectId: String? = null

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    private var resizeDirection: ResizeDirection? = null

    private var pendingMoveX: Float? = null
    private var pendingMoveY: Float? = null

    private var moveFrameScheduled = false

    private var resizeStartX = 0f
    private var resizeStartY = 0f

    private var resizeStartBounds: SpatialBounds? = null

    fun queueMove(
        x: Float,
        y: Float,
        scheduleFrame: (() -> Unit)
    ) {

        pendingMoveX = x
        pendingMoveY = y

        if (moveFrameScheduled) {
            return
        }

        moveFrameScheduled = true

        scheduleFrame()
    }

    fun processQueuedMove() {

        moveFrameScheduled = false

        val x =
            pendingMoveX
                ?: return

        val y =
            pendingMoveY
                ?: return

        pendingMoveX = null
        pendingMoveY = null

        continueInteraction(
            x,
            y
        )
    }

    fun onTouchEvent(
        event: MotionEvent,
        scheduleFrame: (() -> Unit)? = null
    ): Boolean {

        return when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {

                beginInteraction(
                    event.x,
                    event.y
                )
            }

            MotionEvent.ACTION_MOVE -> {

                val scheduler =
                    scheduleFrame
                        ?: return false

                queueMove(
                    event.x,
                    event.y,
                    scheduler
                )

                true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {

                pendingMoveX = null
                pendingMoveY = null
                moveFrameScheduled = false

                endInteraction()

                true
            }

            else -> false
        }
    }

    private fun beginInteraction(
        x: Float,
        y: Float
    ): Boolean {

        val objectToSelect =
            findTopMostObject(
                x,
                y
            ) ?: return false

        val bounds =
            objectToSelect.bounds

        val inTitleBar =
            y >= bounds.y &&
            y <= bounds.y +
                titlebarHeightProvider()

        /*
         * -----------------------------------------------------
         * WINDOW CONTROLS
         * -----------------------------------------------------
         */

        if (inTitleBar) {

            val closeLeft =
                bounds.x +
                    bounds.width -
                    CONTROL_WIDTH

            val maximizeLeft =
                bounds.x +
                    bounds.width -
                    CONTROL_WIDTH * 2

            val minimizeLeft =
                bounds.x +
                    bounds.width -
                    CONTROL_WIDTH * 3

            /*
             * CLOSE
             */
            if (x >= closeLeft) {

                spatialEngine.focusObject(
                    objectToSelect.id
                )

                spatialEngine.removeObject(
                    objectToSelect.id
                )

                return true
            }

            /*
             * MAXIMIZE / RESTORE
             */
            if (x >= maximizeLeft) {

                if (
                    objectToSelect.state ==
                        DesktopObjectState.MAXIMIZED
                ) {

                    spatialEngine.restoreObject(
                        objectToSelect.id
                    )

                } else {

                    val workArea = workAreaProvider()

                    spatialEngine.maximizeObject(
                        objectToSelect.id,
                        workArea.width,
                        workArea.height
                    )
                }

                spatialEngine.focusObject(
                    objectToSelect.id
                )

                return true
            }

            /*
             * MINIMIZE
             */
            if (x >= minimizeLeft) {

                spatialEngine.focusObject(
                    objectToSelect.id
                )

                spatialEngine.minimizeObject(
                    objectToSelect.id
                )

                return true
            }
        }

        /*
         * -----------------------------------------------------
         * NORMAL FOCUS
         * -----------------------------------------------------
         */

        spatialEngine.focusObject(
            objectToSelect.id
        )

        val resizeHandle = WindowResizeGeometry.directionAt(
            bounds,
            x,
            y,
            RESIZE_HANDLE_SIZE
        )

        if (resizeHandle != null) {

            draggedObjectId =
                objectToSelect.id

            resizeDirection = resizeHandle

            resizeStartX = x
            resizeStartY = y

            resizeStartBounds = bounds.copy()

            return true
        }

        /*
         * -----------------------------------------------------
         * TITLE BAR DRAG
         * -----------------------------------------------------
         */

        if (inTitleBar) {

            draggedObjectId =
                objectToSelect.id

            dragOffsetX =
                x - bounds.x

            dragOffsetY =
                y - bounds.y

            return true
        }

        /*
         * Content click = focus only.
         */

        return true
    }

    private fun continueInteraction(
        x: Float,
        y: Float
    ): Boolean {

        val objectId =
            draggedObjectId
                ?: return false

        val objectToModify =
            spatialEngine.findObject(
                objectId
            ) ?: return false

        /*
         * -----------------------------------------------------
         * RESIZE
         * -----------------------------------------------------
         */

        val activeResizeDirection = resizeDirection
        val startBounds = resizeStartBounds

        if (activeResizeDirection != null && startBounds != null) {

            spatialEngine.moveObject(
                objectId,
                WindowResizeGeometry.resize(
                    start = startBounds,
                    direction = activeResizeDirection,
                    deltaX = (x - resizeStartX).toInt(),
                    deltaY = (y - resizeStartY).toInt(),
                    workArea = workAreaProvider(),
                    minimumWidth = MINIMUM_WIDTH,
                    minimumHeight = MINIMUM_HEIGHT
                )
            )

            return true
        }

        /*
         * -----------------------------------------------------
         * DRAG
         * -----------------------------------------------------
         */

        val oldBounds =
            objectToModify.bounds

        val constrainedBounds = WindowResizeGeometry.constrain(
            SpatialBounds(
                x = (x - dragOffsetX).toInt(),
                y = (y - dragOffsetY).toInt(),
                width = oldBounds.width,
                height = oldBounds.height
            ),
            workAreaProvider()
        )

        spatialEngine.moveObject(
            objectId,
            constrainedBounds
        )

        return true
    }

    private fun endInteraction() {

        val objectId =
            draggedObjectId

        if (
            objectId != null &&
            resizeDirection == null
        ) {

            applyEdgeSnap(
                objectId
            )
        }

        draggedObjectId = null

        resizeDirection = null
        resizeStartBounds = null
    }

    private fun applyEdgeSnap(
        objectId: String
    ) {

        val desktopObject =
            spatialEngine.findObject(
                objectId
            ) ?: return

        val workArea = workAreaProvider()
        val viewportWidth = workArea.width
        val viewportHeight = workArea.height

        val bounds =
            desktopObject.bounds

        val nearLeft =
            bounds.x <=
                workArea.x + SNAP_THRESHOLD

        val nearRight =
            bounds.x +
                bounds.width >=
                workArea.x + viewportWidth -
                SNAP_THRESHOLD

        val nearTop =
            bounds.y <=
                workArea.y + SNAP_THRESHOLD

        val nearBottom =
            bounds.y +
                bounds.height >=
                workArea.y + viewportHeight -
                SNAP_THRESHOLD

        val halfWidth =
            viewportWidth / 2

        val halfHeight =
            viewportHeight / 2

        /*
         * -----------------------------------------------------
         * QUARTER SNAP
         * -----------------------------------------------------
         *
         * Corners have priority over left/right edge snap.
         */

        if (nearLeft && nearTop) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = workArea.x,
                    y = workArea.y,
                    width = halfWidth,
                    height = halfHeight
                )
            )

            return
        }

        if (nearRight && nearTop) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = workArea.x + halfWidth,
                    y = workArea.y,
                    width = halfWidth,
                    height = halfHeight
                )
            )

            return
        }

        if (nearLeft && nearBottom) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = workArea.x,
                    y = workArea.y + halfHeight,
                    width = halfWidth,
                    height = halfHeight
                )
            )

            return
        }

        if (nearRight && nearBottom) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = workArea.x + halfWidth,
                    y = workArea.y + halfHeight,
                    width = halfWidth,
                    height = halfHeight
                )
            )

            return
        }

        /*
         * -----------------------------------------------------
         * HALF SNAP
         * -----------------------------------------------------
         */

        if (nearLeft) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = workArea.x,
                    y = workArea.y,
                    width = halfWidth,
                    height = viewportHeight
                )
            )

            return
        }

        if (nearRight) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = workArea.x + halfWidth,
                    y = workArea.y,
                    width = halfWidth,
                    height = viewportHeight
                )
            )
        }
    }

    private fun findTopMostObject(
        x: Float,
        y: Float
    ): DesktopObject? {

        return spatialEngine
            .getAllObjects()
            .filter {
                objectInside(
                    it,
                    x,
                    y
                )
            }
            .maxByOrNull {
                it.zIndex
            }
    }

    private fun objectInside(
        desktopObject: DesktopObject,
        x: Float,
        y: Float
    ): Boolean {

        val bounds =
            desktopObject.bounds

        return x >= bounds.x &&
            x <= bounds.x + bounds.width &&
            y >= bounds.y &&
            y <= bounds.y + bounds.height
    }
}
