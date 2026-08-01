package com.raven.launcher.renderer

import com.raven.launcher.runtime.DesktopRuntime

/**
 * Responsible for rendering the desktop surface.
 *
 * Future responsibilities:
 * - draw windows
 * - draw widgets
 * - draw desktop icons
 */
class DesktopRenderer(

    private val runtime: DesktopRuntime

) {

    fun render() {

        runtime
            .spatialEngine
            .getAllObjects()

    }

}