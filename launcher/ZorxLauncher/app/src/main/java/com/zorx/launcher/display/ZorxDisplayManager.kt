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
}
