package com.zorx.launcher.interaction

import android.view.MotionEvent
import com.zorx.launcher.spatial.DesktopObject
import com.zorx.launcher.spatial.DesktopObjectState
import com.zorx.launcher.spatial.SpatialBounds
import com.zorx.launcher.spatial.SpatialEngine
import kotlin.math.max

/**
 * Handles direct manipulation of Zorx desktop windows.
 *
 * Current capabilities:
 * - Focus
 * - Z-order
 * - Drag
 * - Bottom-right resize
 * - Minimize
 * - Maximize / restore
 * - Close
 * - Left/right edge snapping
 */
class WindowInteractionController(
    private val spatialEngine: SpatialEngine,
    private val viewportSizeProvider: () -> Pair<Int, Int>,
    private val titlebarHeightProvider: () -> Int = {
        TITLE_BAR_HEIGHT
    }
) {

    companion object {

        private const val TITLE_BAR_HEIGHT = 56
        private const val CONTROL_WIDTH = 44
        private const val RESIZE_HANDLE_SIZE = 40

        private const val SNAP_THRESHOLD = 64
    }

    private var draggedObjectId: String? = null

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    private var resizing = false

    private var pendingMoveX: Float? = null
    private var pendingMoveY: Float? = null

    private var moveFrameScheduled = false

    private var resizeStartX = 0f
    private var resizeStartY = 0f

    private var resizeStartWidth = 0
    private var resizeStartHeight = 0

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

                    val viewport =
                        viewportSizeProvider()

                    spatialEngine.maximizeObject(
                        objectToSelect.id,
                        viewport.first,
                        viewport.second
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

        /*
         * -----------------------------------------------------
         * BOTTOM-RIGHT RESIZE
         * -----------------------------------------------------
         */

        val onBottomRightHandle =
            x >=
                bounds.x +
                bounds.width -
                RESIZE_HANDLE_SIZE &&
            y >=
                bounds.y +
                bounds.height -
                RESIZE_HANDLE_SIZE

        if (onBottomRightHandle) {

            draggedObjectId =
                objectToSelect.id

            resizing = true

            resizeStartX = x
            resizeStartY = y

            resizeStartWidth =
                bounds.width

            resizeStartHeight =
                bounds.height

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

        if (resizing) {

            val newWidth =
                max(
                    240,
                    resizeStartWidth +
                        (x - resizeStartX).toInt()
                )

            val newHeight =
                max(
                    180,
                    resizeStartHeight +
                        (y - resizeStartY).toInt()
                )

            val bounds =
                objectToModify.bounds

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = bounds.x,
                    y = bounds.y,
                    width = newWidth,
                    height = newHeight
                )
            )

            return true
        }

        /*
         * -----------------------------------------------------
         * DRAG
         * -----------------------------------------------------
         */

        val newX =
            max(
                0,
                (x - dragOffsetX).toInt()
            )

        val newY =
            max(
                0,
                (y - dragOffsetY).toInt()
            )

        val oldBounds =
            objectToModify.bounds

        spatialEngine.moveObject(
            objectId,
            SpatialBounds(
                x = newX,
                y = newY,
                width = oldBounds.width,
                height = oldBounds.height
            )
        )

        return true
    }

    private fun endInteraction() {

        val objectId =
            draggedObjectId

        if (
            objectId != null &&
            !resizing
        ) {

            applyEdgeSnap(
                objectId
            )
        }

        draggedObjectId = null

        resizing = false
    }

    private fun applyEdgeSnap(
        objectId: String
    ) {

        val desktopObject =
            spatialEngine.findObject(
                objectId
            ) ?: return

        val viewport =
            viewportSizeProvider()

        val viewportWidth =
            viewport.first

        val viewportHeight =
            viewport.second

        val bounds =
            desktopObject.bounds

        val nearLeft =
            bounds.x <=
                SNAP_THRESHOLD

        val nearRight =
            bounds.x +
                bounds.width >=
                viewportWidth -
                SNAP_THRESHOLD

        val nearTop =
            bounds.y <=
                SNAP_THRESHOLD

        val nearBottom =
            bounds.y +
                bounds.height >=
                viewportHeight -
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
                    x = 0,
                    y = 0,
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
                    x = halfWidth,
                    y = 0,
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
                    x = 0,
                    y = halfHeight,
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
                    x = halfWidth,
                    y = halfHeight,
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
                    x = 0,
                    y = 0,
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
                    x = halfWidth,
                    y = 0,
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
