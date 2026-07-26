package com.raven.launcher.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo

class AppManager(
    private val context: Context
) {

    fun getInstalledApps(): List<ResolveInfo> {

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        return context.packageManager
            .queryIntentActivities(intent, 0)
            .sortedBy {
                it.loadLabel(context.packageManager)
                    .toString()
                    .lowercase()
            }
    }

    fun launchApp(resolveInfo: ResolveInfo): Boolean {

        val activityInfo = resolveInfo.activityInfo

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)

            setClassName(
                activityInfo.packageName,
                activityInfo.name
            )

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return try {
            context.startActivity(intent)
            true
        } catch (exception: Exception) {
            false
        }
    }
}
