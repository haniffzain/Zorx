package com.raven.launcher.interaction

import android.view.MotionEvent
import com.raven.launcher.spatial.DesktopObject
import com.raven.launcher.spatial.DesktopObjectState
import com.raven.launcher.spatial.SpatialBounds
import com.raven.launcher.spatial.SpatialEngine
import kotlin.math.max

/**
 * Handles direct manipulation of Luma desktop windows.
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
    private val viewportSizeProvider: () -> Pair<Int, Int>
) {

    companion object {

        private const val TITLE_BAR_HEIGHT = 56
        private const val CONTROL_WIDTH = 40
        private const val RESIZE_HANDLE_SIZE = 40

        private const val SNAP_THRESHOLD = 64
    }

    private var draggedObjectId: String? = null

    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    private var resizing = false

    private var resizeStartX = 0f
    private var resizeStartY = 0f

    private var resizeStartWidth = 0
    private var resizeStartHeight = 0

    fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        return when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {

                beginInteraction(
                    event.x,
                    event.y
                )
            }

            MotionEvent.ACTION_MOVE -> {

                continueInteraction(
                    event.x,
                    event.y
                )
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {

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
            y <= bounds.y + TITLE_BAR_HEIGHT

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

        if (nearLeft) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = 0,
                    y = 0,
                    width = viewportWidth / 2,
                    height = viewportHeight
                )
            )

            return
        }

        if (nearRight) {

            spatialEngine.moveObject(
                objectId,
                SpatialBounds(
                    x = viewportWidth / 2,
                    y = 0,
                    width = viewportWidth / 2,
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
