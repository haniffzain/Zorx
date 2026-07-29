package com.raven.launcher.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Rect
import android.os.Handler
import android.os.Looper

class AppManager(
    private val context: Context
) {

    private val activeAppManager =
        ActiveAppManager(context)

    private val ravenWindowManager =
        RavenWindowManager(context)

    private val handler =
        Handler(Looper.getMainLooper())

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

            context.startActivity(intent)

            /*
             * Give Android time to create the task.
             * Raven will then locate the task and
             * attempt to convert it to FREEFORM.
             */
            handler.postDelayed(
                {
                    ravenWindowManager.movePackageToFreeform(
                        activityInfo.packageName,
                        Rect(
                            300,
                            150,
                            1800,
                            1250
                        )
                    )
                },
                500
            )

            activeAppManager.setActiveApp(
                resolveInfo
            )

            true

        } catch (exception: Exception) {

            false
        }
    }
}
