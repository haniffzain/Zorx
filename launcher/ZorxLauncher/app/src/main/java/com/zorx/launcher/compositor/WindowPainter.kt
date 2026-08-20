package com.zorx.launcher.compositor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.zorx.launcher.spatial.DesktopObject
import com.zorx.launcher.spatial.DesktopObjectState

/**
 * Zorx Window Painter
 *
 * Visual shell for desktop windows.
 *
 * Design language:
 * - Dark spatial surface
 * - Rounded geometry
 * - Minimal controls
 * - Cyan / violet focus accent
 * - Soft depth without expensive blur
 */
class WindowPainter {

    // =========================================================
    // GEOMETRY
    // =========================================================

    private val windowRadius = 18f
    private val titleBarHeight = 58f
    private val buttonRadius = 7f

    // =========================================================
    // WINDOW
    // =========================================================

    private val windowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(27, 29, 36)
            style = Paint.Style.FILL
        }

    private val windowInnerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(31, 33, 41)
            style = Paint.Style.FILL
        }

    // =========================================================
    // DEPTH
    // =========================================================

    private val shadowPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(
                70,
                0,
                0,
                0
            )

            style = Paint.Style.FILL
        }

    // =========================================================
    // TITLE BAR
    // =========================================================

    private val titleBarPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(24, 26, 33)
            style = Paint.Style.FILL
        }

    private val focusedTitleBarPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(29, 31, 40)
            style = Paint.Style.FILL
        }

    // =========================================================
    // BORDER
    // =========================================================

    private val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }

    // =========================================================
    // TITLE
    // =========================================================

    private val titlePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(238, 241, 247)
            textSize = 22f
            isAntiAlias = true
        }

    // =========================================================
    // CONTENT
    // =========================================================

    private val contentPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(178, 182, 193)
            textSize = 17f
            isAntiAlias = true
        }

    // =========================================================
    // WINDOW CONTROLS
    // =========================================================

    private val controlPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

    private val controlGlyphPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(220, 224, 232)
            style = Paint.Style.STROKE
            strokeWidth = 1.7f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

    // =========================================================
    // ACCENT
    // =========================================================

    private val accentPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

    // =========================================================
    // RECTANGLES
    // =========================================================

    private val shadowRect =
        RectF()

    private val windowRect =
        RectF()

    private val titleRect =
        RectF()

    // =========================================================
    // PAINT
    // =========================================================

    fun paint(
        canvas: Canvas,
        desktopObject: DesktopObject
    ) {

        val bounds =
            desktopObject.bounds

        val left =
            bounds.x.toFloat()

        val top =
            bounds.y.toFloat()

        val right =
            (bounds.x + bounds.width).toFloat()

        val bottom =
            (bounds.y + bounds.height).toFloat()

        if (
            right <= left ||
            bottom <= top
        ) {
            return
        }

        // =====================================================
        // CLIP CHECK
        // =====================================================

        if (
            canvas.quickReject(
                left,
                top,
                right,
                bottom,
                Canvas.EdgeType.AA
            )
        ) {
            return
        }

        windowRect.set(
            left,
            top,
            right,
            bottom
        )

        // =====================================================
        // STATE
        // =====================================================

        val focused =
            desktopObject.state ==
                DesktopObjectState.FOCUSED

        val minimized =
            desktopObject.state ==
                DesktopObjectState.MINIMIZED

        // =====================================================
        // DEPTH
        // =====================================================

        val shadowOffset = 8f

        shadowRect.set(
            left + 2f,
            top + shadowOffset,
            right + 2f,
            bottom + shadowOffset
        )

        shadowPaint.alpha =
            if (focused) {
                90
            } else {
                55
            }

        canvas.drawRoundRect(
            shadowRect,
            windowRadius,
            windowRadius,
            shadowPaint
        )

        // =====================================================
        // WINDOW BODY
        // =====================================================

        windowPaint.color =
            if (focused) {
                Color.rgb(28, 30, 39)
            } else {
                Color.rgb(25, 27, 34)
            }

        windowPaint.alpha =
            if (minimized) {
                185
            } else {
                255
            }

        canvas.drawRoundRect(
            windowRect,
            windowRadius,
            windowRadius,
            windowPaint
        )

        windowPaint.alpha = 255

        // =====================================================
        // INNER SURFACE
        // =====================================================

        val innerRect =
            RectF(
                left + 1.5f,
                top + 1.5f,
                right - 1.5f,
                bottom - 1.5f
            )

        windowInnerPaint.alpha =
            if (minimized) {
                130
            } else {
                255
            }

        canvas.drawRoundRect(
            innerRect,
            windowRadius - 1.5f,
            windowRadius - 1.5f,
            windowInnerPaint
        )

        windowInnerPaint.alpha = 255

        // =====================================================
        // TITLE BAR
        // =====================================================

        titleRect.set(
            left + 1.5f,
            top + 1.5f,
            right - 1.5f,
            minOf(
                bottom - 1.5f,
                top + titleBarHeight
            )
        )

        titleBarPaint.alpha =
            if (minimized) {
                150
            } else {
                255
            }

        titleBarPaint.color =
            if (focused) {
                Color.rgb(30, 32, 41)
            } else {
                Color.rgb(23, 25, 32)
            }

        canvas.drawRoundRect(
            titleRect,
            windowRadius - 1.5f,
            windowRadius - 1.5f,
            titleBarPaint
        )

        titleBarPaint.alpha = 255

        // =====================================================
        // TITLE BAR DIVIDER
        // =====================================================

        val dividerPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    if (focused) {
                        Color.argb(
                            70,
                            92,
                            225,
                            230
                        )
                    } else {
                        Color.argb(
                            40,
                            255,
                            255,
                            255
                        )
                    }

                style = Paint.Style.FILL
            }

        canvas.drawRect(
            left + 18f,
            top + titleBarHeight - 1f,
            right - 18f,
            top + titleBarHeight,
            dividerPaint
        )

        // =====================================================
        // FOCUS ACCENT
        // =====================================================

        if (focused) {

            accentPaint.color =
                Color.rgb(
                    70,
                    214,
                    220
                )

            canvas.drawRoundRect(
                left + 14f,
                top + 8f,
                left + 18f,
                top + 34f,
                2f,
                2f,
                accentPaint
            )
        }

        // =====================================================
        // TITLE
        // =====================================================

        titlePaint.alpha =
            if (minimized) {
                150
            } else {
                255
            }

        val titleX =
            if (focused) {
                left + 30f
            } else {
                left + 22f
            }

        val titleY =
            top + 36f

        canvas.drawText(
            desktopObject.title,
            titleX,
            titleY,
            titlePaint
        )

        titlePaint.alpha = 255

        // =====================================================
        // WINDOW CONTROLS
        // =====================================================

        val controlsY =
            top + 29f

        val closeX =
            right - 27f

        val maximizeX =
            right - 55f

        val minimizeX =
            right - 83f

        drawControl(
            canvas,
            minimizeX,
            controlsY,
            Control.MINIMIZE,
            focused
        )

        drawControl(
            canvas,
            maximizeX,
            controlsY,
            Control.MAXIMIZE,
            focused
        )

        drawControl(
            canvas,
            closeX,
            controlsY,
            Control.CLOSE,
            focused
        )

        // =====================================================
        // CONTENT
        // =====================================================

        if (!minimized) {

            contentPaint.alpha =
                if (focused) {
                    230
                } else {
                    175
                }

            canvas.drawText(
                "Zorx Window",
                left + 26f,
                top + titleBarHeight + 42f,
                contentPaint
            )

            contentPaint.alpha = 255

            // subtle content indicator
            val indicatorPaint =
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color =
                        if (focused) {
                            Color.argb(
                                120,
                                70,
                                214,
                                220
                            )
                        } else {
                            Color.argb(
                                55,
                                180,
                                185,
                                195
                            )
                        }

                    style = Paint.Style.FILL
                }

            canvas.drawRoundRect(
                left + 26f,
                top + titleBarHeight + 58f,
                left + 90f,
                top + titleBarHeight + 62f,
                2f,
                2f,
                indicatorPaint
            )
        }

        // =====================================================
        // BORDER
        // =====================================================

        borderPaint.color =
            if (focused) {
                Color.rgb(
                    75,
                    205,
                    214
                )
            } else {
                Color.rgb(
                    72,
                    75,
                    86
                )
            }

        borderPaint.alpha =
            if (focused) {
                220
            } else {
                150
            }

        borderPaint.strokeWidth =
            if (focused) {
                1.8f
            } else {
                1.2f
            }

        canvas.drawRoundRect(
            windowRect,
            windowRadius,
            windowRadius,
            borderPaint
        )

        borderPaint.alpha = 255
    }

    // =========================================================
    // WINDOW CONTROL
    // =========================================================

    private fun drawControl(
        canvas: Canvas,
        x: Float,
        y: Float,
        control: Control,
        focused: Boolean
    ) {

        controlPaint.color =
            when (control) {

                Control.CLOSE ->
                    if (focused) {
                        Color.rgb(
                            75,
                            47,
                            57
                        )
                    } else {
                        Color.rgb(
                            52,
                            53,
                            61
                        )
                    }

                else ->
                    if (focused) {
                        Color.rgb(
                            48,
                            51,
                            61
                        )
                    } else {
                        Color.rgb(
                            45,
                            47,
                            55
                        )
                    }
            }

        canvas.drawCircle(
            x,
            y,
            buttonRadius,
            controlPaint
        )

        controlGlyphPaint.color =
            when (control) {

                Control.CLOSE ->
                    Color.rgb(
                        235,
                        170,
                        180
                    )

                else ->
                    Color.rgb(
                        185,
                        190,
                        201
                    )
            }

        when (control) {

            Control.MINIMIZE -> {

                canvas.drawLine(
                    x - 3.5f,
                    y,
                    x + 3.5f,
                    y,
                    controlGlyphPaint
                )
            }

            Control.MAXIMIZE -> {

                canvas.drawRect(
                    x - 3.5f,
                    y - 3.5f,
                    x + 3.5f,
                    y + 3.5f,
                    controlGlyphPaint
                )
            }

            Control.CLOSE -> {

                canvas.drawLine(
                    x - 3f,
                    y - 3f,
                    x + 3f,
                    y + 3f,
                    controlGlyphPaint
                )

                canvas.drawLine(
                    x + 3f,
                    y - 3f,
                    x - 3f,
                    y + 3f,
                    controlGlyphPaint
                )
            }
        }
    }

    // =========================================================
    // CONTROL TYPE
    // =========================================================

    private enum class Control {

        MINIMIZE,
        MAXIMIZE,
        CLOSE
    }
}
