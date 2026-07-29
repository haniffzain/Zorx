package com.raven.launcher.apps

import android.app.ActivityManager
import android.content.Context
import android.graphics.Rect
import android.util.Log

class RavenWindowManager(
    private val context: Context
) {

fun movePackageToFreeform(
    packageName: String,
    bounds: Rect
): Boolean {

    val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE)
                as ActivityManager

    val tasks =
        activityManager.getRunningTasks(100)

    Log.i(
        TAG,
        "getRunningTasks returned ${tasks.size} tasks"
    )

    tasks.forEach { task ->
        Log.i(
            TAG,
            "TASK id=${task.id} " +
            "base=${task.baseActivity} " +
            "top=${task.topActivity}"
        )
    }

    val task =
        tasks.firstOrNull {
            it.baseActivity?.packageName == packageName ||
            it.topActivity?.packageName == packageName
        }

    if (task == null) {
        Log.e(
            TAG,
            "No running task found for $packageName"
        )
        return false
    }

    Log.i(
        TAG,
        "Found $packageName as task ${task.id}"
    )

    return moveTaskToFreeform(
        task.id,
        bounds
    )
}

    companion object {
        private const val TAG = "RavenWindowManager"

        // WindowConfiguration.WINDOWING_MODE_FREEFORM
        private const val WINDOWING_MODE_FREEFORM = 5
    }

    fun moveTaskToFreeform(
        taskId: Int,
        bounds: Rect
    ): Boolean {

        return try {

            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE)
                        as ActivityManager

            val tasks =
                activityManager.getRunningTasks(100)

            val task =
                tasks.firstOrNull {
                    it.id == taskId
                }
                    ?: run {
                        Log.e(TAG, "Task $taskId not found")
                        return false
                    }

            /*
             * RunningTaskInfo extends TaskInfo.
             * TaskInfo contains the hidden field:
             *
             * WindowContainerToken token
             */

val taskInfoClass =
    Class.forName("android.app.TaskInfo")

val getTokenMethod =
    taskInfoClass.getDeclaredMethod("getToken")

getTokenMethod.isAccessible = true

val token =
    getTokenMethod.invoke(task)

            if (token == null) {
                Log.e(TAG, "WindowContainerToken is null")
                return false
            }

            val tokenClass =
                Class.forName(
                    "android.window.WindowContainerToken"
                )

            val wctClass =
                Class.forName(
                    "android.window.WindowContainerTransaction"
                )

            val wct =
                wctClass
                    .getDeclaredConstructor()
                    .newInstance()

            val setWindowingMode =
                wctClass.getDeclaredMethod(
                    "setWindowingMode",
                    tokenClass,
                    Int::class.javaPrimitiveType
                )

            setWindowingMode.invoke(
                wct,
                token,
                WINDOWING_MODE_FREEFORM
            )

            val setBounds =
                wctClass.getDeclaredMethod(
                    "setBounds",
                    tokenClass,
                    Rect::class.java
                )

            setBounds.invoke(
                wct,
                token,
                bounds
            )

            /*
             * ActivityTaskManager.getService()
             */
            val atmClass =
                Class.forName(
                    "android.app.ActivityTaskManager"
                )

            val getService =
                atmClass.getDeclaredMethod("getService")

            getService.isAccessible = true

            val activityTaskManager =
                getService.invoke(null)

            val getWindowOrganizerController =
                activityTaskManager.javaClass.interfaces
                    .firstOrNull {
                        it.name ==
                            "android.app.IActivityTaskManager"
                    }
                    ?.getMethod(
                        "getWindowOrganizerController"
                    )
                    ?: throw NoSuchMethodException(
                        "getWindowOrganizerController"
                    )

            val organizerController =
                getWindowOrganizerController.invoke(
                    activityTaskManager
                )

            val organizerInterface =
                Class.forName(
                    "android.window.IWindowOrganizerController"
                )

            val applyTransaction =
                organizerInterface.getMethod(
                    "applyTransaction",
                    wctClass
                )

            applyTransaction.invoke(
                organizerController,
                wct
            )

            Log.i(
                TAG,
                "Task $taskId moved to FREEFORM: $bounds"
            )

            true

        } catch (exception: Throwable) {

            Log.e(
                TAG,
                "Failed moving task to FREEFORM",
                exception
            )

            false
        }
    }

}
