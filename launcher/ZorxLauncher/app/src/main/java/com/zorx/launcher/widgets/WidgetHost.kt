package com.zorx.launcher.widgets
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.zorx.launcher.design.ZorxThemeManager
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.shell.ZorxShellSettingsStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WidgetHost(private val ctx:Context):FrameLayout(ctx) {
    private val edit = ZorxWidgetEditController()
    private val refresh=object:Runnable { override fun run(){ render(); postDelayed(this,60000) } }
    init { ZorxThemeManager.addListener { post { render() } }; post(refresh) }
    fun setEditMode(enabled:Boolean) { edit.layoutLocked=ZorxWidgetLayoutStore.locked(ctx); edit.editModeEnabled=enabled && !edit.layoutLocked; render() }
    fun toggleLock(){ ZorxWidgetLayoutStore.setLocked(ctx,!ZorxWidgetLayoutStore.locked(ctx)); edit.layoutLocked=ZorxWidgetLayoutStore.locked(ctx); if(edit.layoutLocked) edit.editModeEnabled=false; render() }
    fun render(){ removeAllViews(); val m=ZorxShellSettingsStore.resolve(ctx,width.coerceAtLeast(1),height.coerceAtLeast(1)); val cell=(96*resources.displayMetrics.density*m.uiScale).toInt(); val gap=(16*resources.displayMetrics.density).toInt(); if(edit.editModeEnabled) setBackgroundColor(android.graphics.Color.argb(36,255,255,255)); else setBackgroundColor(android.graphics.Color.TRANSPARENT); ZorxWidgetLayoutStore.read(ctx).filter { it.visible && it.widgetType==WidgetType.CLOCK }.forEach { w -> val view=clock(); val lp=LayoutParams(w.gridWidth*cell+(w.gridWidth-1)*gap,w.gridHeight*cell+(w.gridHeight-1)*gap).apply { leftMargin=gap+w.gridX*(cell+gap); topMargin=gap+w.gridY*(cell+gap) }; if(edit.editModeEnabled) { view.background=(view.background as GradientDrawable).apply { setStroke((resources.displayMetrics.density*2).toInt(),ZorxThemeManager.current().widgetAccent) }; view.setOnLongClickListener { edit.selectedWidgetId=w.instanceId; true }; view.setOnTouchListener { _,e -> if(edit.layoutLocked) false else { when(e.actionMasked){ android.view.MotionEvent.ACTION_MOVE -> { lp.leftMargin=(e.rawX-cell/2).toInt().coerceAtLeast(gap); lp.topMargin=(e.rawY-cell/2).toInt().coerceAtLeast(gap); view.layoutParams=lp }; android.view.MotionEvent.ACTION_UP -> { val nx=((lp.leftMargin-gap)/(cell+gap)).coerceAtLeast(0); val ny=((lp.topMargin-gap)/(cell+gap)).coerceAtLeast(0); ZorxWidgetLayoutStore.replace(ctx,w.copy(gridX=nx,gridY=ny)); render() } }; true } } }; addView(view,lp) } }
    private fun clock(): LinearLayout {
        val theme = ZorxThemeManager.current()
        val typography = ZorxShellSettingsStore.readTypography(ctx)
        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            background = GradientDrawable().apply { setColor(theme.surfaceBackground); setStroke(resources.displayMetrics.density.toInt(), theme.borderColor); cornerRadius = ZorxShellSettingsStore.resolve(ctx,width.coerceAtLeast(1),height.coerceAtLeast(1)).widgetRadiusPx }
            addView(TextView(ctx).apply { text=SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date()); setTextColor(theme.textPrimary); setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,ZorxTypography.effectivePx(ctx,typography,typography.widgetTextSp*2)) })
            addView(TextView(ctx).apply { text=SimpleDateFormat("EEEE, dd MMM",Locale.getDefault()).format(Date()); setTextColor(theme.textSecondary); setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,ZorxTypography.effectivePx(ctx,typography,typography.widgetTextSp)) })
        }
    }
    override fun onDetachedFromWindow(){ removeCallbacks(refresh); super.onDetachedFromWindow() }
}
