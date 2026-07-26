package com.raven.launcher.apps

import android.content.Context
import android.content.pm.ResolveInfo

class ActiveAppManager(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "raven_active_app",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val KEY_ACTIVE_APP = "active_app"
        private const val SEPARATOR = "|"
    }

    fun setActiveApp(
        app: ResolveInfo
    ) {

        val appId =
            "${app.activityInfo.packageName}$SEPARATOR${app.activityInfo.name}"

        preferences.edit()
            .putString(
                KEY_ACTIVE_APP,
                appId
            )
            .apply()
    }

    fun getActiveAppId(): String? {

        return preferences.getString(
            KEY_ACTIVE_APP,
            null
        )
    }

    fun isActive(
        app: ResolveInfo
    ): Boolean {

        val appId =
            "${app.activityInfo.packageName}$SEPARATOR${app.activityInfo.name}"

        return getActiveAppId() == appId
    }

    fun clearActiveApp() {

        preferences.edit()
            .remove(KEY_ACTIVE_APP)
            .apply()
    }
}
