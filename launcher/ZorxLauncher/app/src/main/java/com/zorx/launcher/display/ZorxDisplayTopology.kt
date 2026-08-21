package com.zorx.launcher.display
import android.content.Context

@JvmInline value class ZorxDisplayId(val value:String)
data class ZorxWorkArea(val x:Int,val y:Int,val width:Int,val height:Int)
data class ZorxTopologyDisplay(val id:ZorxDisplayId,val name:String,val physicalWidthPx:Int,val physicalHeightPx:Int,val effectiveWidthPx:Int,val effectiveHeightPx:Int,val scale:Float,val refreshRateHz:Float,val rotation:Int,val positionX:Int,val positionY:Int,val isPrimary:Boolean,val workArea:ZorxWorkArea)
data class ZorxDisplayTopology(val displays:List<ZorxTopologyDisplay>) { fun primary()=displays.firstOrNull { it.isPrimary }?:displays.firstOrNull() }
object ZorxDisplayTopologyStore {
 private const val PREF="zorx_display_topology"
 fun load(context:Context,infos:List<ZorxDisplayInfo>,fallbackScale:Float):ZorxDisplayTopology { val p=context.getSharedPreferences(PREF,0); val primary=p.getString("primary",null); return ZorxDisplayTopology(infos.mapIndexed { index,info-> val id=ZorxDisplayId(info.uniqueId);val scale=p.getFloat("scale_${id.value}",fallbackScale);val x=p.getInt("x_${id.value}",if(index==0)0 else infos.take(index).sumOf{it.physicalWidthPx});val y=p.getInt("y_${id.value}",0);val w=(info.physicalWidthPx/scale).toInt();val h=(info.physicalHeightPx/scale).toInt();ZorxTopologyDisplay(id,info.name,info.physicalWidthPx,info.physicalHeightPx,w,h,scale,info.refreshRateHz,info.rotation,x,y,primary?.let{it==id.value}?:info.isPrimary,ZorxWorkArea(x,y,w,h)) }) }
 fun save(context:Context,topology:ZorxDisplayTopology){val e=context.getSharedPreferences(PREF,0).edit();topology.displays.forEach{e.putInt("x_${it.id.value}",it.positionX).putInt("y_${it.id.value}",it.positionY).putFloat("scale_${it.id.value}",it.scale);if(it.isPrimary)e.putString("primary",it.id.value)};e.apply()}
}
