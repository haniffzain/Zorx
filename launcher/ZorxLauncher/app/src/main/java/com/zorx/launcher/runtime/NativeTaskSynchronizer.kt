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

    private val scheduledPackages =
        mutableSetOf<String>()

    fun syncBounds(
        desktopObject: DesktopObject
    ) {

        val packageName =
            desktopObject.id
                .removePrefix("window:")
                .substringBefore(":")

        if (packageName.isBlank() || packageName == desktopObject.id) {
            return
        }

        val window =
            ZorxWindowManager.getWindowForPackage(
                packageName
            ) ?: return

        if (window.taskId < 0) {
            return
        }

        pendingBounds[packageName] =
            Rect(
                desktopObject.bounds.x,
                desktopObject.bounds.y,
                desktopObject.bounds.x +
                    desktopObject.bounds.width,
                desktopObject.bounds.y +
                    desktopObject.bounds.height
            )

        if (!scheduledPackages.add(packageName)) {
            return
        }

        handler.postDelayed({

            scheduledPackages.remove(packageName)

            val bounds =
                pendingBounds.remove(packageName)
                    ?: return@postDelayed

            val currentWindow =
                ZorxWindowManager.getWindowForPackage(
                    packageName
                ) ?: return@postDelayed

            if (currentWindow.taskId >= 0) {
                backend.moveTaskToFreeform(
                    currentWindow.taskId,
                    bounds
                )
            }
        }, FRAME_INTERVAL_MS)
    }

    private companion object {

        const val FRAME_INTERVAL_MS =
            32L
    }
}
