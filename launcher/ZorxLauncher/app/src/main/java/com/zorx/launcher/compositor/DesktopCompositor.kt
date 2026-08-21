package com.zorx.launcher.compositor

import android.graphics.Canvas
import com.zorx.launcher.runtime.DesktopRuntime
import com.zorx.launcher.shell.ZorxShellSettingsStore
import androidx.core.content.res.ResourcesCompat
import com.zorx.launcher.R
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.shell.ZorxShellSettingsStore

/**
 * Combines every desktop object into
 * a single rendered desktop frame.
 */
class DesktopCompositor(

    private val runtime: DesktopRuntime,

    private val windowPainter: WindowPainter =
        WindowPainter(
            {
                val metrics =
                    runtime.context.resources.displayMetrics
                ZorxShellSettingsStore.resolve(
                    runtime.context,
                    metrics.widthPixels,
                    metrics.heightPixels
                )
            },
            {
                ResourcesCompat.getFont(
                    runtime.context,
                    R.font.jetbrains_mono_regular
                )
            },
            {
                val typography =
                    ZorxShellSettingsStore.readTypography(
                        runtime.context
                    )

                ZorxTypography.effectivePx(
                    runtime.context,
                    typography,
                    typography.titlebarTextSp
                ) to ZorxTypography.effectivePx(
                    runtime.context,
                    typography,
                    typography.interfaceTextSp
                )
            }
        )

) {

    fun compose(
        canvas: Canvas
    ) {

        val clip =
            canvas.clipBounds

        runtime
            .spatialEngine
            .getAllObjects()
            .forEach { desktopObject ->

                if (
                    desktopObject.state ==
                        com.zorx.launcher.spatial.DesktopObjectState.MINIMIZED
                ) {
                    return@forEach
                }

                val bounds =
                    desktopObject.bounds

                val windowLeft =
                    bounds.x

                val windowTop =
                    bounds.y

                val windowRight =
                    bounds.x + bounds.width

                val windowBottom =
                    bounds.y + bounds.height

                if (
                    windowRight <= clip.left ||
                    windowLeft >= clip.right ||
                    windowBottom <= clip.top ||
                    windowTop >= clip.bottom
                ) {
                    return@forEach
                }

                windowPainter.paint(
                    canvas,
                    desktopObject
                )
            }
    }

}
