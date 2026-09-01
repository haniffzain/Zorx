package com.zorx.launcher.runtime

/** Pure task-presence policy kept separate from Android polling for unit testing. */
class NativeTaskReconciliationPolicy(
    private val requiredMissingObservations: Int = 2
) {
    private val missingObservations = mutableMapOf<Int, Int>()

    init {
        require(requiredMissingObservations > 0)
    }

    fun observe(
        trackedTaskIds: Set<Int>,
        runningTaskIds: Set<Int>
    ): Set<Int> {
        missingObservations.keys.retainAll(trackedTaskIds)
        val removed = mutableSetOf<Int>()

        trackedTaskIds.forEach { taskId ->
            if (taskId in runningTaskIds) {
                missingObservations.remove(taskId)
                return@forEach
            }

            val count = (missingObservations[taskId] ?: 0) + 1
            if (count >= requiredMissingObservations) {
                missingObservations.remove(taskId)
                removed.add(taskId)
            } else {
                missingObservations[taskId] = count
            }
        }

        return removed
    }

    fun reset() {
        missingObservations.clear()
    }
}
