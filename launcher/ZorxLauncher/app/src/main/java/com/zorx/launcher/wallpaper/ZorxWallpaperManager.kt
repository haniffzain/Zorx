package com.zorx.launcher.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.zorx.launcher.display.ZorxDisplayId
import com.zorx.launcher.workspace.ZorxWorkspaceId

enum class ZorxWallpaperSource { BUILT_IN, USER_IMAGE, SOLID_COLOR }
enum class ZorxWallpaperMode { FILL, FIT, STRETCH, CENTER, TILE }
enum class ZorxWallpaperScope { ALL_WORKSPACES, CURRENT_WORKSPACE }
data class ZorxWallpaper(
    val source: ZorxWallpaperSource = ZorxWallpaperSource.BUILT_IN,
    val mode: ZorxWallpaperMode = ZorxWallpaperMode.FILL,
    val solidColor: Int = 0xFF10151FFF.toInt(),
    val imageUri: String? = null
)

/** Central persistence, source decoding and live-update authority for desktop wallpaper. */
object ZorxWallpaperManager {
    private const val PREF = "zorx_wallpaper"
    private val listeners = mutableSetOf<() -> Unit>()
    private var cachedUri: String? = null
    private var cachedBitmap: Bitmap? = null

    private fun prefs(context: Context) = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    private fun key(workspace: ZorxWorkspaceId?, display: ZorxDisplayId?) =
        if (workspace == null) "all" else "ws_${workspace.value}_${display?.value?.hashCode() ?: "primary"}"

    fun current(context: Context, workspace: ZorxWorkspaceId, display: ZorxDisplayId?): ZorxWallpaper =
        read(context, key(workspace, display)).takeIf { prefs(context).getBoolean("${key(workspace, display)}.assigned", false) }
            ?: read(context, "all")

    fun scope(context: Context): ZorxWallpaperScope = runCatching {
        ZorxWallpaperScope.valueOf(prefs(context).getString("scope", ZorxWallpaperScope.ALL_WORKSPACES.name)!!)
    }.getOrDefault(ZorxWallpaperScope.ALL_WORKSPACES)

    fun setScope(context: Context, scope: ZorxWallpaperScope) {
        prefs(context).edit().putString("scope", scope.name).apply()
    }

    fun apply(context: Context, wallpaper: ZorxWallpaper, scope: ZorxWallpaperScope, workspace: ZorxWorkspaceId, display: ZorxDisplayId?) {
        val target = if (scope == ZorxWallpaperScope.ALL_WORKSPACES) "all" else key(workspace, display)
        val e = prefs(context).edit().putBoolean("$target.assigned", true)
        e.putString("$target.source", wallpaper.source.name).putString("$target.mode", wallpaper.mode.name)
            .putInt("$target.color", wallpaper.solidColor).putString("$target.uri", wallpaper.imageUri).apply()
        notifyChanged()
    }

    fun reset(context: Context, scope: ZorxWallpaperScope, workspace: ZorxWorkspaceId, display: ZorxDisplayId?) {
        val target = if (scope == ZorxWallpaperScope.ALL_WORKSPACES) "all" else key(workspace, display)
        prefs(context).edit().remove("$target.assigned").remove("$target.source").remove("$target.mode").remove("$target.color").remove("$target.uri").apply()
        clearCache(); notifyChanged()
    }

    fun addListener(listener: () -> Unit) { listeners.add(listener) }
    fun removeListener(listener: () -> Unit) { listeners.remove(listener) }
    private fun notifyChanged() = listeners.toList().forEach { it() }

    fun bitmap(context: Context, wallpaper: ZorxWallpaper, targetWidth: Int, targetHeight: Int): Bitmap? {
        val uriText = wallpaper.imageUri ?: return null
        if (cachedUri == uriText && cachedBitmap?.isRecycled == false) return cachedBitmap
        clearCache()
        val uri = runCatching { Uri.parse(uriText) }.getOrNull() ?: return null
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        runCatching { resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) } }.getOrNull()
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        var sample = 1
        while (bounds.outWidth / sample > targetWidth * 2 || bounds.outHeight / sample > targetHeight * 2) sample *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sample; inPreferredConfig = Bitmap.Config.ARGB_8888 }
        cachedBitmap = runCatching { resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) } }.getOrNull()
        cachedUri = uriText
        return cachedBitmap
    }

    private fun read(context: Context, target: String): ZorxWallpaper {
        val p = prefs(context)
        val source = runCatching { ZorxWallpaperSource.valueOf(p.getString("$target.source", ZorxWallpaperSource.BUILT_IN.name)!!) }.getOrDefault(ZorxWallpaperSource.BUILT_IN)
        val mode = runCatching { ZorxWallpaperMode.valueOf(p.getString("$target.mode", ZorxWallpaperMode.FILL.name)!!) }.getOrDefault(ZorxWallpaperMode.FILL)
        return ZorxWallpaper(source, mode, p.getInt("$target.color", 0xFF10151FFF.toInt()), p.getString("$target.uri", null))
    }

    private fun clearCache() { cachedBitmap?.takeUnless { it.isRecycled }?.recycle(); cachedBitmap = null; cachedUri = null }
}
