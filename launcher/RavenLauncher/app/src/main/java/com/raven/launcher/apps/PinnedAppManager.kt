package com.raven.launcher.apps

import android.content.Context
import android.content.pm.ResolveInfo

class PinnedAppManager(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "raven_taskbar",
            Context.MODE_PRIVATE
        )

    companion object {
        private const val KEY_PINNED_APPS = "pinned_apps"
        private const val SEPARATOR = "|"
    }

    fun pinApp(app: ResolveInfo) {

        val id = createAppId(app)

        val pinned =
            preferences.getStringSet(
                KEY_PINNED_APPS,
                emptySet()
            )?.toMutableSet() ?: mutableSetOf()

        pinned.add(id)

        preferences.edit()
            .putStringSet(
                KEY_PINNED_APPS,
                pinned
            )
            .apply()
    }

    fun unpinApp(app: ResolveInfo) {

        val id = createAppId(app)

        val pinned =
            preferences.getStringSet(
                KEY_PINNED_APPS,
                emptySet()
            )?.toMutableSet() ?: mutableSetOf()

        pinned.remove(id)

        preferences.edit()
            .putStringSet(
                KEY_PINNED_APPS,
                pinned
            )
            .apply()
    }

    fun isPinned(app: ResolveInfo): Boolean {

        return getPinnedIds()
            .contains(
                createAppId(app)
            )
    }

    fun getPinnedIds(): Set<String> {

        return preferences.getStringSet(
            KEY_PINNED_APPS,
            emptySet()
        )?.toSet() ?: emptySet()
    }

    fun getPackageName(
        appId: String
    ): String {

        return appId.substringBefore(SEPARATOR)
    }

    fun getActivityName(
        appId: String
    ): String {

        return appId.substringAfter(
            SEPARATOR,
            ""
        )
    }

    private fun createAppId(
        app: ResolveInfo
    ): String {

        return "${app.activityInfo.packageName}$SEPARATOR${app.activityInfo.name}"
    }
}
