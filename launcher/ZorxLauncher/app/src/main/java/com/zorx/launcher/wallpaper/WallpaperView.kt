package com.zorx.launcher.wallpaper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.zorx.launcher.display.ZorxDisplayId
import com.zorx.launcher.workspace.ZorxWorkspaceManager

/** Non-interactive bottom desktop layer. It redraws only on bounds or wallpaper changes. */
class WallpaperView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val wallpaperListener = { post { invalidate() }; Unit }
    init { isClickable = false; ZorxWallpaperManager.addListener(wallpaperListener) }
    override fun onDraw(canvas: Canvas) {
        val workspace = ZorxWorkspaceManager.active(context)
        val wallpaper = ZorxWallpaperManager.current(context, workspace, null as ZorxDisplayId?)
        if (wallpaper.source == ZorxWallpaperSource.SOLID_COLOR) { canvas.drawColor(wallpaper.solidColor); return }
        if (wallpaper.source == ZorxWallpaperSource.BUILT_IN) {
            paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(0xFF0B1018.toInt(), 0xFF16283A.toInt(), 0xFF10151F.toInt()), null, Shader.TileMode.CLAMP)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint); paint.shader = null; return
        }
        val bitmap = ZorxWallpaperManager.bitmap(context, wallpaper, width.coerceAtLeast(1), height.coerceAtLeast(1))
        if (bitmap == null) { canvas.drawColor(wallpaper.solidColor); return }
        drawBitmap(canvas, bitmap, wallpaper.mode)
    }
    private fun drawBitmap(canvas: Canvas, bitmap: Bitmap, mode: ZorxWallpaperMode) {
        val w = width.toFloat(); val h = height.toFloat(); val bw = bitmap.width.toFloat(); val bh = bitmap.height.toFloat()
        if (mode == ZorxWallpaperMode.TILE) { var y=0f; while(y<h){ var x=0f; while(x<w){ canvas.drawBitmap(bitmap,x,y,paint); x+=bw }; y+=bh }; return }
        val scale = when(mode){ ZorxWallpaperMode.FILL -> maxOf(w/bw,h/bh); ZorxWallpaperMode.FIT -> minOf(w/bw,h/bh); ZorxWallpaperMode.STRETCH -> 0f; else -> 1f }
        val dest = if(mode==ZorxWallpaperMode.STRETCH) RectF(0f,0f,w,h) else { val dw=bw*scale; val dh=bh*scale; RectF((w-dw)/2,(h-dh)/2,(w+dw)/2,(h+dh)/2) }
        canvas.drawBitmap(bitmap, null, dest, paint)
    }
    override fun onDetachedFromWindow() { ZorxWallpaperManager.removeListener(wallpaperListener); super.onDetachedFromWindow() }
}
