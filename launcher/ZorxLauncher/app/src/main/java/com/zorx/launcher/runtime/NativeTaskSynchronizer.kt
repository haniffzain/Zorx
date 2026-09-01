package com.zorx.launcher.runtime

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import com.zorx.launcher.apps.AndroidWindowBackend
import com.zorx.launcher.spatial.DesktopObject
import com.zorx.launcher.window.ZorxWindowManager

/**
 * Applies Zorx desktop bounds to the associated native Android task.
 *
 * Synthetic task IDs remain a launch-time fallback only. Once AppManager
 * promotes a window to its Android task ID, this synchronizer forwards
 * subsequent drag, resize, maximize and restore bounds to ActivityTaskManager.
 */
class NativeTaskSynchronizer(
    context: Context
) {

    private val backend =
        AndroidWindowBackend(
            context
        )

    private val handler =
        Handler(
            Looper.getMainLooper()
        )

    private val pendingBounds =
        mutableMapOf<String, Rect>()

    private val scheduledObjects =
        mutableSetOf<String>()

    private val externallyRemovedTaskIds =
        mutableSetOf<Int>()

    private val reconciliationPolicy =
        NativeTaskReconciliationPolicy()

    private var reconciliationAction: Runnable? = null

    fun syncBounds(
        desktopObject: DesktopObject
    ) {

        val taskId = desktopObject.taskId ?: return
        if (taskId < 0) {
            return
        }

        pendingBounds[desktopObject.id] =
            Rect(
                desktopObject.bounds.x,
                desktopObject.bounds.y,
                desktopObject.bounds.x +
                    desktopObject.bounds.width,
                desktopObject.bounds.y +
                    desktopObject.bounds.height
            )

        if (!scheduledObjects.add(desktopObject.id)) {
            return
        }

        handler.postDelayed({

            scheduledObjects.remove(desktopObject.id)

            val bounds =
                pendingBounds.remove(desktopObject.id)
                    ?: return@postDelayed

            val currentTaskId = desktopObject.taskId
            if (currentTaskId != null && currentTaskId >= 0) {
                backend.moveTaskToFreeform(
                    currentTaskId,
                    bounds
                )
            }
        }, FRAME_INTERVAL_MS)
    }

    fun closeTask(desktopObject: DesktopObject) {
        pendingBounds.remove(desktopObject.id)
        scheduledObjects.remove(desktopObject.id)

        val taskId = desktopObject.taskId ?: return
        if (taskId >= 0 && !externallyRemovedTaskIds.remove(taskId)) {
            backend.removeTask(taskId)
        }
        ZorxWindowManager.unregisterWindow(taskId)
    }

    fun startReconciliation(
        objects: () -> List<DesktopObject>,
        removeObject: (String) -> Unit
    ) {
        if (reconciliationAction != null) return

        val action = object : Runnable {
            override fun run() {
                val runningTaskIds = backend.runningTaskIds()
                if (runningTaskIds != null) {
                    val nativeObjects = objects()
                        .filter { (it.taskId ?: -1) >= 0 }
                    val trackedIds = nativeObjects.mapNotNull { it.taskId }.toSet()
                    val removedTaskIds =
                        reconciliationPolicy.observe(trackedIds, runningTaskIds)

                    nativeObjects
                        .filter { it.taskId in removedTaskIds }
                        .forEach { missing ->
                            missing.taskId?.let(externallyRemovedTaskIds::add)
                            removeObject(missing.id)
                    }
                }
                handler.postDelayed(this, RECONCILIATION_INTERVAL_MS)
            }
        }
        reconciliationAction = action
        handler.postDelayed(action, RECONCILIATION_INTERVAL_MS)
    }

    /** Drop coalesced work when the owning desktop runtime is disposed. */
    fun destroy() {
        handler.removeCallbacksAndMessages(null)
        pendingBounds.clear()
        scheduledObjects.clear()
        externallyRemovedTaskIds.clear()
        reconciliationPolicy.reset()
        reconciliationAction = null
    }

    private companion object {

        const val FRAME_INTERVAL_MS =
            32L

        const val RECONCILIATION_INTERVAL_MS =
            1500L

    }
}
