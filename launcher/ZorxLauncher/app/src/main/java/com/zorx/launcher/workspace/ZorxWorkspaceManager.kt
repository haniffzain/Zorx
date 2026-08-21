package com.zorx.launcher.workspace

import android.content.Context

@JvmInline value class ZorxWorkspaceId(val value:Int)
data class ZorxWorkspace(val id:ZorxWorkspaceId,val name:String,val order:Int)
object ZorxWorkspaceManager {
 private const val PREF="zorx_workspaces"; private val listeners=mutableSetOf<() -> Unit>()
 fun workspaces()=(1..4).map{ZorxWorkspace(ZorxWorkspaceId(it),"Workspace $it",it)}
 fun active(context:Context)=ZorxWorkspaceId(context.getSharedPreferences(PREF,0).getInt("active",1).coerceIn(1,4))
 fun switchWorkspace(context:Context,id:ZorxWorkspaceId){context.getSharedPreferences(PREF,0).edit().putInt("active",id.value.coerceIn(1,4)).apply();listeners.toList().forEach{it()}}
 fun nextWorkspace(context:Context)=switchWorkspace(context,ZorxWorkspaceId(if(active(context).value==4)1 else active(context).value+1))
 fun previousWorkspace(context:Context)=switchWorkspace(context,ZorxWorkspaceId(if(active(context).value==1)4 else active(context).value-1))
 fun workspaceFor(context:Context,windowId:String)=ZorxWorkspaceId(context.getSharedPreferences(PREF,0).getInt("window_$windowId",active(context).value))
 fun assignIfAbsent(context:Context,windowId:String){val p=context.getSharedPreferences(PREF,0);if(!p.contains("window_$windowId"))p.edit().putInt("window_$windowId",active(context).value).apply()}
 fun moveWindowToWorkspace(context:Context,windowId:String,id:ZorxWorkspaceId){context.getSharedPreferences(PREF,0).edit().putInt("window_$windowId",id.value.coerceIn(1,4)).apply();listeners.toList().forEach{it()}}
 fun addListener(l:()->Unit){listeners.add(l)};fun removeListener(l:()->Unit){listeners.remove(l)}
}
