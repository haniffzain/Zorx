package com.zorx.launcher.widgets

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

enum class WidgetType { CLOCK, CALENDAR, SYSTEM_MONITOR, NETWORK, WEATHER, MEDIA, NOTES, QUICK_CONTROLS }
data class ZorxWidgetInstance(val instanceId:String, val widgetType:WidgetType, val gridX:Int, val gridY:Int, val gridWidth:Int=2, val gridHeight:Int=1, val visible:Boolean=true)
data class ZorxWidgetMetadata(val type:WidgetType, val name:String, val defaultWidth:Int, val defaultHeight:Int, val supportedSizes:List<Pair<Int,Int>>)
object ZorxWidgetRegistry { val clock=ZorxWidgetMetadata(WidgetType.CLOCK,"Clock",2,1,listOf(1 to 1,2 to 1,1 to 2,2 to 2)); fun available()=listOf(clock) }
object ZorxWidgetLayoutStore {
    private const val PREF="zorx_widget_layout"; private const val KEY="instances"
    fun read(context:Context):List<ZorxWidgetInstance> = runCatching { val a=JSONArray(context.getSharedPreferences(PREF,0).getString(KEY,"[]")); (0 until a.length()).map { i -> a.getJSONObject(i).let { ZorxWidgetInstance(it.getString("id"),WidgetType.valueOf(it.getString("type")),it.getInt("x"),it.getInt("y"),it.getInt("w"),it.getInt("h"),it.optBoolean("visible",true)) } } }.getOrDefault(emptyList())
    fun save(context:Context, items:List<ZorxWidgetInstance>) { val a=JSONArray(); items.forEach { a.put(JSONObject().put("id",it.instanceId).put("type",it.widgetType.name).put("x",it.gridX).put("y",it.gridY).put("w",it.gridWidth).put("h",it.gridHeight).put("visible",it.visible)) }; context.getSharedPreferences(PREF,0).edit().putString(KEY,a.toString()).apply() }
    fun addClock(context:Context):ZorxWidgetInstance { val all=read(context); val used=all.map { it.gridX to it.gridY }.toSet(); val slot=(0..40).map { it%4 to it/4 }.first { it !in used }; val item=ZorxWidgetInstance("clock-${System.currentTimeMillis()}",WidgetType.CLOCK,slot.first,slot.second); save(context,all+item); return item }
    fun removeLastClock(context:Context) { val all=read(context); val index=all.indexOfLast { it.widgetType==WidgetType.CLOCK }; if(index>=0) save(context,all.filterIndexed { i,_ -> i!=index }) }
    fun replace(context:Context, next:ZorxWidgetInstance) = save(context,read(context).map { if(it.instanceId==next.instanceId) next else it })
    fun remove(context:Context, id:String) = save(context,read(context).filterNot { it.instanceId==id })
    fun overlaps(candidate:ZorxWidgetInstance, others:List<ZorxWidgetInstance>) = others.any { it.instanceId!=candidate.instanceId && it.visible && candidate.gridX < it.gridX+it.gridWidth && candidate.gridX+candidate.gridWidth > it.gridX && candidate.gridY < it.gridY+it.gridHeight && candidate.gridY+candidate.gridHeight > it.gridY }
    fun valid(candidate:ZorxWidgetInstance, others:List<ZorxWidgetInstance>, columns:Int=4, rows:Int=8) = candidate.gridX>=0 && candidate.gridY>=0 && candidate.gridX+candidate.gridWidth<=columns && candidate.gridY+candidate.gridHeight<=rows && !overlaps(candidate,others)
    fun duplicate(context:Context, source:ZorxWidgetInstance):ZorxWidgetInstance? { val all=read(context); val slot=(0 until 32).map { it%4 to it/4 }.firstOrNull { (x,y) -> valid(source.copy(gridX=x,gridY=y),all) } ?: return null; val copy=source.copy(instanceId="${source.widgetType.name.lowercase()}-${System.currentTimeMillis()}",gridX=slot.first,gridY=slot.second); save(context,all+copy); return copy }
    fun locked(context:Context)=context.getSharedPreferences(PREF,0).getBoolean("locked",false)
    fun setLocked(context:Context,value:Boolean)=context.getSharedPreferences(PREF,0).edit().putBoolean("locked",value).apply()
}
class ZorxWidgetEditController { var editModeEnabled=false; var selectedWidgetId:String?=null; var draggingWidgetId:String?=null; var resizingWidgetId:String?=null; var layoutLocked=false }
