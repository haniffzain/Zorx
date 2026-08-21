package com.zorx.launcher.widgets
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.*
import com.zorx.launcher.design.ZorxThemeManager
import com.zorx.launcher.design.ZorxTypography
import com.zorx.launcher.shell.ZorxShellSettingsStore
import java.text.SimpleDateFormat
import java.util.*

class WidgetHost(private val ctx:Context):FrameLayout(ctx) {
 private val edit=ZorxWidgetEditController(); private val tick=object:Runnable{override fun run(){render();postDelayed(this,60000)}}
 init { ZorxThemeManager.addListener { post{render()} }; post(tick); setOnClickListener { if(edit.editModeEnabled){edit.selectedWidgetId=null;render()} } }
 fun setEditMode(value:Boolean){edit.layoutLocked=ZorxWidgetLayoutStore.locked(ctx);edit.editModeEnabled=value&&!edit.layoutLocked;render()}
 fun toggleLock(){ZorxWidgetLayoutStore.setLocked(ctx,!ZorxWidgetLayoutStore.locked(ctx));edit.layoutLocked=ZorxWidgetLayoutStore.locked(ctx);if(edit.layoutLocked)edit.editModeEnabled=false;render()}
 fun render(){removeAllViews(); val m=ZorxShellSettingsStore.resolve(ctx,width.coerceAtLeast(1),height.coerceAtLeast(1));val cell=(96*resources.displayMetrics.density*m.uiScale).toInt();val gap=(16*resources.displayMetrics.density).toInt();setBackgroundColor(if(edit.editModeEnabled)Color.argb(36,255,255,255) else Color.TRANSPARENT);val all=ZorxWidgetLayoutStore.read(ctx);all.filter{it.visible&&it.widgetType==WidgetType.CLOCK}.forEach{item->addWidget(item,all,cell,gap)} }
 private fun addWidget(item:ZorxWidgetInstance,all:List<ZorxWidgetInstance>,cell:Int,gap:Int){ val v=clock(item); val lp=params(item,cell,gap); if(edit.editModeEnabled){ if(edit.selectedWidgetId==item.instanceId) addToolbar(v,item,all,cell,gap); v.setOnClickListener{edit.selectedWidgetId=item.instanceId;render()}; var dx=0f;var dy=0f; v.setOnTouchListener{_,e-> when(e.actionMasked){ MotionEvent.ACTION_DOWN->{dx=e.rawX-lp.leftMargin;dy=e.rawY-lp.topMargin;edit.draggingWidgetId=item.instanceId}; MotionEvent.ACTION_MOVE->{lp.leftMargin=(e.rawX-dx).toInt().coerceAtLeast(gap);lp.topMargin=(e.rawY-dy).toInt().coerceAtLeast(gap);v.layoutParams=lp}; MotionEvent.ACTION_UP->{val next=item.copy(gridX=((lp.leftMargin-gap)/(cell+gap)).coerceIn(0,4-item.gridWidth),gridY=((lp.topMargin-gap)/(cell+gap)).coerceIn(0,7-item.gridHeight));if(ZorxWidgetLayoutStore.valid(next,all))ZorxWidgetLayoutStore.replace(ctx,next);edit.draggingWidgetId=null;render()} }; true } }; addView(v,lp) }
 private fun addToolbar(v:LinearLayout,item:ZorxWidgetInstance,all:List<ZorxWidgetInstance>,cell:Int,gap:Int){ v.background=(v.background as GradientDrawable).apply{setStroke((resources.displayMetrics.density*2).toInt(),ZorxThemeManager.current().widgetAccent)}; val bar=LinearLayout(ctx).apply{gravity=Gravity.CENTER}; val labels=listOf("Duplicate","Resize","Remove"); labels.forEach { label -> bar.addView(Button(ctx).apply{text=label;setOnClickListener{when(label){"Duplicate"->ZorxWidgetLayoutStore.duplicate(ctx,item);"Resize"->{val s=ZorxWidgetRegistry.clock.supportedSizes;val z=s[(s.indexOf(item.gridWidth to item.gridHeight)+1)%s.size];val n=item.copy(gridWidth=z.first,gridHeight=z.second);if(ZorxWidgetLayoutStore.valid(n,all))ZorxWidgetLayoutStore.replace(ctx,n)};else->{ZorxWidgetLayoutStore.remove(ctx,item.instanceId);edit.selectedWidgetId=null}};render()}},LinearLayout.LayoutParams(0,-2,1f)) }; v.addView(bar,0,LinearLayout.LayoutParams(-1,-2)) }
 private fun params(i:ZorxWidgetInstance,c:Int,g:Int)=LayoutParams(i.gridWidth*c+(i.gridWidth-1)*g,i.gridHeight*c+(i.gridHeight-1)*g).apply{leftMargin=g+i.gridX*(c+g);topMargin=g+i.gridY*(c+g)}
 private fun clock(i:ZorxWidgetInstance):LinearLayout { val t=ZorxThemeManager.current(); val f=ZorxShellSettingsStore.readTypography(ctx); val factor=if(i.gridWidth*i.gridHeight>1) 2f else 1.5f; return LinearLayout(ctx).apply { orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;background=GradientDrawable().apply{setColor(t.surfaceBackground);setStroke(resources.displayMetrics.density.toInt(),t.borderColor);cornerRadius=ZorxShellSettingsStore.resolve(ctx,width.coerceAtLeast(1),height.coerceAtLeast(1)).widgetRadiusPx}; addView(TextView(ctx).apply{text=SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date());setTextColor(t.textPrimary);setTextSize(TypedValue.COMPLEX_UNIT_PX,ZorxTypography.effectivePx(ctx,f,f.widgetTextSp*factor))}); addView(TextView(ctx).apply{text=SimpleDateFormat("EEE, dd MMM",Locale.getDefault()).format(Date());setTextColor(t.textSecondary);setTextSize(TypedValue.COMPLEX_UNIT_PX,ZorxTypography.effectivePx(ctx,f,f.widgetTextSp))}) } }
 override fun onDetachedFromWindow(){removeCallbacks(tick);super.onDetachedFromWindow()}
}
