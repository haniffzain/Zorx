package com.zorx.launcher.display

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display

data class ZorxDisplayMode(
    val modeId: Int,
    val widthPx: Int,
    val heightPx: Int,
    val refreshRateHz: Float
)

data class ZorxDisplayInfo(
    val displayId: Int,
    val uniqueId: String,
    val name: String,
    val physicalWidthPx: Int,
    val physicalHeightPx: Int,
    val logicalWidthPx: Int,
    val logicalHeightPx: Int,
    val densityDpi: Int,
    val rotation: Int,
    val refreshRateHz: Float,
    val isPrimary: Boolean,
    val supportedModes: List<ZorxDisplayMode>
)

/** Physical pixels remain the native-task coordinate system. Effective pixels describe desktop workspace. */
data class ZorxDisplayMetrics(
    val physicalWidthPx: Int,
    val physicalHeightPx: Int,
    val displayScale: Float,
    val effectiveWidthPx: Int,
    val effectiveHeightPx: Int,
    val densityDpi: Int,
    val refreshRateHz: Float,
    val rotation: Int
) {
    companion object {
        fun from(info: ZorxDisplayInfo, scale: Float) = fromPhysical(
            info.physicalWidthPx, info.physicalHeightPx, scale, info.densityDpi, info.refreshRateHz, info.rotation
        )
        fun fromPhysical(width: Int, height: Int, scale: Float, density: Int, refresh: Float = 0f, rotation: Int = 0): ZorxDisplayMetrics {
            val safeScale = scale.coerceIn(.75f, 2f)
            return ZorxDisplayMetrics(width, height, safeScale, (width / safeScale).toInt(), (height / safeScale).toInt(), density, refresh, rotation)
        }
    }
}

class ZorxDisplayManager(private val context: Context) {
    private val manager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    fun getDisplays(): List<ZorxDisplayInfo> = manager.displays.map { display ->
        val metrics = android.util.DisplayMetrics()
        @Suppress("DEPRECATION")
        display.getRealMetrics(metrics)
        val mode = display.mode
        ZorxDisplayInfo(
            displayId = display.displayId,
            uniqueId =
                "${display.displayId}:${display.name}:" +
                    "${mode.physicalWidth}x${mode.physicalHeight}",
            name = display.name,
            physicalWidthPx = mode.physicalWidth,
            physicalHeightPx = mode.physicalHeight,
            logicalWidthPx = metrics.widthPixels,
            logicalHeightPx = metrics.heightPixels,
            densityDpi = metrics.densityDpi,
            rotation = display.rotation,
            refreshRateHz = mode.refreshRate,
            isPrimary = display.displayId == Display.DEFAULT_DISPLAY,
            supportedModes = display.supportedModes.map {
                ZorxDisplayMode(it.modeId, it.physicalWidth, it.physicalHeight, it.refreshRate)
            }
        )
    }

    fun getMetrics(displayScale: Float): List<ZorxDisplayMetrics> =
        getDisplays().map { ZorxDisplayMetrics.from(it, displayScale) }

    fun topology(defaultScale:Float):ZorxDisplayTopology = ZorxDisplayTopologyStore.load(context,getDisplays(),defaultScale)
}
