package com.zorx.launcher.apps
import com.zorx.launcher.service.ZorxWindowService
import com.zorx.launcher.window.ZorxWindow
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log

class AppManager(
    private val context: Context
) {

    private val launchingPackages =
        mutableSetOf<String>()

    /*
     * Temporary Zorx task IDs.
     *
     * Android task IDs are not available to the normal
     * launcher process on the current emulator.
     *
     * Negative IDs are reserved for Zorx synthetic windows.
     */
    private var nextSyntheticTaskId = -1

    private fun createSyntheticTaskId(): Int {

        return nextSyntheticTaskId--
    }

    companion object {
        private const val TAG = "AppManager"

        // Android WindowConfiguration.WINDOWING_MODE_FREEFORM
        private const val WINDOWING_MODE_FREEFORM = 5

        // Confirmed from Android 15 ActivityOptions framework.
        private const val KEY_LAUNCH_WINDOWING_MODE =
            "android.activity.windowingMode"
    }

    private val activeAppManager =
        ActiveAppManager(context)

    private val androidWindowBackend =
        AndroidWindowBackend(context)

    private val handler =
        Handler(Looper.getMainLooper())

        private val zorxWindowService =
    ZorxWindowService()

    fun getInstalledApps(): List<ResolveInfo> {

        val intent =
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

        return context.packageManager
            .queryIntentActivities(
                intent,
                0
            )
            .sortedBy {
                it.loadLabel(
                    context.packageManager
                )
                    .toString()
                    .lowercase()
            }
    }

    fun launchApp(
        resolveInfo: ResolveInfo
    ): Boolean {

        val activityInfo =
            resolveInfo.activityInfo

        val packageName =
            activityInfo.packageName

        synchronized(launchingPackages) {

            if (!launchingPackages.add(packageName)) {

                Log.w(
                    TAG,
                    "Ignoring duplicate launch request for $packageName"
                )

                return false
            }
        }

        val intent =
            Intent(Intent.ACTION_MAIN).apply {

                addCategory(
                    Intent.CATEGORY_LAUNCHER
                )

                setClassName(
                    activityInfo.packageName,
                    activityInfo.name
                )

                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }

        return try {

            /*
             * Create launch options with initial Zorx
             * desktop window bounds.
             */
            val options =
                ActivityOptions.makeBasic()
                    .setLaunchBounds(
                        Rect(
                            300,
                            150,
                            1800,
                            1250
                        )
                    )

            /*
             * ActivityOptions.setLaunchWindowingMode()
             * is hidden/blocked on Android 15.
             *
             * The framework Bundle key has been confirmed as:
             *
             * android.activity.windowingMode
             *
             * WINDOWING_MODE_FREEFORM = 5
             */
            val bundle =
                options.toBundle()

            bundle.putInt(
                KEY_LAUNCH_WINDOWING_MODE,
                WINDOWING_MODE_FREEFORM
            )

            Log.i(
                TAG,
                "Launching ${activityInfo.packageName} " +
                    "directly in FREEFORM mode"
            )

            context.startActivity(
                intent,
                bundle
            )

            /*
             * TEMPORARY TEST:
             * Allow Android to create the freeform task,
             * then ask ZorxWindowManager to resize it.
             */
            /*
             * Android task IDs are currently hidden from the
             * normal ZorxLauncher process.
             *
             * Create the Zorx window immediately using a
             * synthetic negative task ID.
             *
             * AndroidWindowBackend remains responsible for
             * attempting real FREEFORM manipulation.
             */

            val syntheticTaskId =
                createSyntheticTaskId()

            val zorxBounds =
                Rect(
                    300,
                    150,
                    1800,
                    1250
                )

            Log.i(
                TAG,
                "Creating synthetic Zorx task " +
                    "$syntheticTaskId for ${activityInfo.packageName}"
            )

            zorxWindowService.registerWindow(

                ZorxWindow(

                    taskId =
                        syntheticTaskId,

                    packageName =
                        activityInfo.packageName,

                    title =
                        resolveInfo.loadLabel(
                            context.packageManager
                        ).toString(),

                    bounds =
                        zorxBounds
                )
            )

            activeAppManager.setActiveApp(
                resolveInfo
            )

            promoteNativeTask(
                packageName,
                syntheticTaskId
            )

            true

        } catch (exception: Exception) {

            synchronized(launchingPackages) {
                launchingPackages.remove(packageName)
            }

            Log.e(
                TAG,
                "Failed launching ${activityInfo.packageName} " +
                    "in FREEFORM mode",
                exception
            )

            false
        }
    }

    private fun promoteNativeTask(
        packageName: String,
        syntheticTaskId: Int,
        attempt: Int = 0
    ) {

        handler.postDelayed({

            val nativeTaskId =
                androidWindowBackend.findTaskId(
                    packageName
                )

            if (nativeTaskId != null) {

                val promoted = zorxWindowService.promoteTaskId(
                    syntheticTaskId,
                    nativeTaskId
                )

                // The user may close the synthetic desktop object while native
                // discovery is still pending. Do not leave the late native task
                // running after its registry entry has already been removed.
                if (!promoted) {
                    androidWindowBackend.removeTask(nativeTaskId)
                }

                synchronized(launchingPackages) {
                    launchingPackages.remove(packageName)
                }

                return@postDelayed
            }

            if (attempt < 4) {

                promoteNativeTask(
                    packageName,
                    syntheticTaskId,
                    attempt + 1
                )

            } else {

                Log.w(
                    TAG,
                    "Android task was not visible for $packageName; " +
                        "keeping synthetic task $syntheticTaskId"
                )

                synchronized(launchingPackages) {
                    launchingPackages.remove(packageName)
                }
            }
        }, 400L)
    }
}
