package com.zorx.launcher.events

/** Published when a launch-time synthetic task is resolved to its Android task. */
data class WindowTaskPromotedEvent(
    val syntheticTaskId: Int,
    val nativeTaskId: Int
) : ZorxEvent()
