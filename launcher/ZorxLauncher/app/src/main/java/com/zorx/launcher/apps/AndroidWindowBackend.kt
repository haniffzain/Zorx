package com.zorx.launcher.apps

import android.app.ActivityManager
import android.content.Context
import android.graphics.Rect
import android.util.Log

class AndroidWindowBackend(
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
        private const val TAG = "AndroidWindowBackend"

        // WindowConfiguration.WINDOWING_MODE_FREEFORM
        private const val WINDOWING_MODE_FREEFORM = 5
    }

    fun findTaskId(
        packageName: String
    ): Int? {

        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE)
                as ActivityManager

        val tasks =
            activityManager.getRunningTasks(100)

        val task =
            tasks.firstOrNull {
                it.baseActivity?.packageName == packageName ||
                it.topActivity?.packageName == packageName
            }

        return task?.id
    }

    fun moveTaskToFreeform(
        taskId: Int,
        bounds: Rect
    ): Boolean {

        return try {

            Log.i(
                TAG,
                "resizeTask: taskId=$taskId bounds=$bounds"
            )

            /*
             * Android 15 ZorxOS:
             *
             * ServiceManager.getService("activity_task")
             *     -> IBinder
             *
             * IActivityTaskManager.Stub.asInterface(IBinder)
             *     -> IActivityTaskManager
             *
             * IActivityTaskManager.resizeTask(
             *     int taskId,
             *     Rect bounds,
             *     int flags
             * )
             */

            val serviceManagerClass =
                Class.forName(
                    "android.os.ServiceManager"
                )

            val getServiceMethod =
                serviceManagerClass.getMethod(
                    "getService",
                    String::class.java
                )

            val binder =
                getServiceMethod.invoke(
                    null,
                    "activity_task"
                )
                    ?: throw IllegalStateException(
                        "activity_task binder unavailable"
                    )

            Log.i(
                TAG,
                "activity_task binder acquired"
            )

            val iBinderClass =
                Class.forName(
                    "android.os.IBinder"
                )

            val stubClass =
                Class.forName(
                    "android.app.IActivityTaskManager\$Stub"
                )

            val asInterfaceMethod =
                stubClass.getMethod(
                    "asInterface",
                    iBinderClass
                )

            val service =
                asInterfaceMethod.invoke(
                    null,
                    binder
                )
                    ?: throw IllegalStateException(
                        "IActivityTaskManager unavailable"
                    )

            Log.i(
                TAG,
                "IActivityTaskManager acquired"
            )

            val iAtmClass =
                Class.forName(
                    "android.app.IActivityTaskManager"
                )

            val resizeTaskMethod =
                iAtmClass.getMethod(
                    "resizeTask",
                    Int::class.javaPrimitiveType,
                    Rect::class.java,
                    Int::class.javaPrimitiveType
                )

            resizeTaskMethod.invoke(
                service,
                taskId,
                bounds,
                0
            )

            Log.i(
                TAG,
                "resizeTask SUCCESS: taskId=$taskId bounds=$bounds"
            )

            true

        } catch (exception: Throwable) {

            Log.e(
                TAG,
                "resizeTask FAILED: taskId=$taskId",
                exception
            )

            false
        }
    }

}
