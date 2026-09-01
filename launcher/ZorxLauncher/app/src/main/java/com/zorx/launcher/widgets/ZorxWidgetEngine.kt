package com.zorx.launcher.widgets

import android.content.Context
import com.zorx.launcher.spatial.DesktopGridPlacement
import com.zorx.launcher.spatial.DesktopPlacementPolicy
import org.json.JSONArray
import org.json.JSONObject

enum class WidgetType { CLOCK, CALENDAR, SYSTEM_MONITOR, NETWORK, WEATHER, MEDIA, NOTES, QUICK_CONTROLS }
data class ZorxWidgetInstance(val instanceId:String, val widgetType:WidgetType, val gridX:Int, val gridY:Int, val gridWidth:Int=2, val gridHeight:Int=1, val visible:Boolean=true, val displayId:String="primary")
data class ZorxWidgetMetadata(val type:WidgetType, val name:String, val defaultWidth:Int, val defaultHeight:Int, val supportedSizes:List<Pair<Int,Int>>)
object ZorxWidgetRegistry { val clock=ZorxWidgetMetadata(WidgetType.CLOCK,"Clock",2,1,listOf(1 to 1,2 to 1,1 to 2,2 to 2)); val calendar=ZorxWidgetMetadata(WidgetType.CALENDAR,"Calendar",2,2,listOf(2 to 1,2 to 2)); val system=ZorxWidgetMetadata(WidgetType.SYSTEM_MONITOR,"System Monitor",2,1,listOf(2 to 1,2 to 2)); val network=ZorxWidgetMetadata(WidgetType.NETWORK,"Network",2,1,listOf(2 to 1,2 to 2)); val controls=ZorxWidgetMetadata(WidgetType.QUICK_CONTROLS,"Quick Controls",2,1,listOf(2 to 1,2 to 2)); val weather=ZorxWidgetMetadata(WidgetType.WEATHER,"Weather",2,1,listOf(2 to 1,2 to 2)); val media=ZorxWidgetMetadata(WidgetType.MEDIA,"Media",2,1,listOf(2 to 1,2 to 2)); val notes=ZorxWidgetMetadata(WidgetType.NOTES,"Notes",2,2,listOf(2 to 1,2 to 2)); fun available()=listOf(clock,calendar,system,network,controls,weather,media,notes); fun metadata(type:WidgetType)=available().first { it.type==type } }
data class WeatherSnapshot(val location:String,val temperature:String,val condition:String,val highLow:String)
interface WeatherProvider { fun snapshot():WeatherSnapshot? }
data class MediaState(val title:String,val artist:String,val active:Boolean)
interface MediaSessionProvider { fun state():MediaState? }
object ZorxWidgetConfigStore { private const val PREF="zorx_widget_config"; fun note(context:Context,id:String)=context.getSharedPreferences(PREF,0).getString("note_$id","")?:""; fun saveNote(context:Context,id:String,value:String)=context.getSharedPreferences(PREF,0).edit().putString("note_$id",value).apply() }
object ZorxWidgetLayoutStore {
    private const val PREF="zorx_widget_layout"; private const val KEY="instances"
    fun read(context:Context):List<ZorxWidgetInstance> = runCatching { val a=JSONArray(context.getSharedPreferences(PREF,0).getString(KEY,"[]")); (0 until a.length()).map { i -> a.getJSONObject(i).let { ZorxWidgetInstance(it.getString("id"),WidgetType.valueOf(it.getString("type")),it.getInt("x"),it.getInt("y"),it.getInt("w"),it.getInt("h"),it.optBoolean("visible",true),it.optString("display","primary")) } } }.getOrDefault(emptyList())
    fun save(context:Context, items:List<ZorxWidgetInstance>) { val a=JSONArray(); items.forEach { a.put(JSONObject().put("id",it.instanceId).put("type",it.widgetType.name).put("x",it.gridX).put("y",it.gridY).put("w",it.gridWidth).put("h",it.gridHeight).put("visible",it.visible).put("display",it.displayId)) }; context.getSharedPreferences(PREF,0).edit().putString(KEY,a.toString()).apply() }
    fun addClock(context:Context):ZorxWidgetInstance { val all=read(context); val used=all.map { it.gridX to it.gridY }.toSet(); val slot=(0..40).map { it%4 to it/4 }.first { it !in used }; val item=ZorxWidgetInstance("clock-${System.currentTimeMillis()}",WidgetType.CLOCK,slot.first,slot.second); save(context,all+item); return item }
    fun add(context:Context,type:WidgetType,reserved:List<DesktopGridPlacement> = emptyList()):ZorxWidgetInstance? { val all=read(context); val meta=ZorxWidgetRegistry.metadata(type); val slot=(0 until 32).map { it%4 to it/4 }.firstOrNull { (x,y)->validShared(ZorxWidgetInstance("new",type,x,y,meta.defaultWidth,meta.defaultHeight),all,reserved) }?:return null; val item=ZorxWidgetInstance("${type.name.lowercase()}-${System.currentTimeMillis()}",type,slot.first,slot.second,meta.defaultWidth,meta.defaultHeight);save(context,all+item);return item }
    fun removeLastClock(context:Context) { val all=read(context); val index=all.indexOfLast { it.widgetType==WidgetType.CLOCK }; if(index>=0) save(context,all.filterIndexed { i,_ -> i!=index }) }
    fun replace(context:Context, next:ZorxWidgetInstance) = save(context,read(context).map { if(it.instanceId==next.instanceId) next else it })
    fun remove(context:Context, id:String) = save(context,read(context).filterNot { it.instanceId==id })
    fun overlaps(candidate:ZorxWidgetInstance, others:List<ZorxWidgetInstance>) = others.any { it.instanceId!=candidate.instanceId && it.visible && candidate.gridX < it.gridX+it.gridWidth && candidate.gridX+candidate.gridWidth > it.gridX && candidate.gridY < it.gridY+it.gridHeight && candidate.gridY+candidate.gridHeight > it.gridY }
    fun valid(candidate:ZorxWidgetInstance, others:List<ZorxWidgetInstance>, columns:Int=4, rows:Int=8) = candidate.gridX>=0 && candidate.gridY>=0 && candidate.gridX+candidate.gridWidth<=columns && candidate.gridY+candidate.gridHeight<=rows && !overlaps(candidate,others)
    fun validShared(candidate:ZorxWidgetInstance, others:List<ZorxWidgetInstance>, reserved:List<DesktopGridPlacement>):Boolean { val widget=DesktopPlacementPolicy.legacyWidgetPlacement(candidate.gridX,candidate.gridY,candidate.gridWidth,candidate.gridHeight); return valid(candidate,others) && reserved.none { DesktopPlacementPolicy.overlaps(widget,it) } }
    fun duplicate(context:Context, source:ZorxWidgetInstance, reserved:List<DesktopGridPlacement> = emptyList()):ZorxWidgetInstance? { val all=read(context); val slot=(0 until 32).map { it%4 to it/4 }.firstOrNull { (x,y) -> validShared(source.copy(gridX=x,gridY=y),all,reserved) } ?: return null; val copy=source.copy(instanceId="${source.widgetType.name.lowercase()}-${System.currentTimeMillis()}",gridX=slot.first,gridY=slot.second); save(context,all+copy); return copy }
    fun locked(context:Context)=context.getSharedPreferences(PREF,0).getBoolean("locked",false)
    fun setLocked(context:Context,value:Boolean)=context.getSharedPreferences(PREF,0).edit().putBoolean("locked",value).apply()
}
class ZorxWidgetEditController { var editModeEnabled=false; var selectedWidgetId:String?=null; var draggingWidgetId:String?=null; var resizingWidgetId:String?=null; var layoutLocked=false }
