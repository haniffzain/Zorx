package com.zorx.launcher.runtime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NativeTaskReconciliationPolicyTest {
    @Test
    fun `removes a task only after two consecutive misses`() {
        val policy = NativeTaskReconciliationPolicy()

        assertTrue(policy.observe(setOf(42), emptySet()).isEmpty())
        assertEquals(setOf(42), policy.observe(setOf(42), emptySet()))
    }

    @Test
    fun `running observation clears an earlier miss`() {
        val policy = NativeTaskReconciliationPolicy()

        policy.observe(setOf(42), emptySet())
        policy.observe(setOf(42), setOf(42))

        assertTrue(policy.observe(setOf(42), emptySet()).isEmpty())
    }

    @Test
    fun `stops tracking tasks removed from the desktop model`() {
        val policy = NativeTaskReconciliationPolicy()

        policy.observe(setOf(42), emptySet())
        policy.observe(emptySet(), emptySet())

        assertTrue(policy.observe(setOf(42), emptySet()).isEmpty())
    }

    @Test
    fun `handles multiple tasks independently`() {
        val policy = NativeTaskReconciliationPolicy()

        policy.observe(setOf(10, 20), setOf(20))

        assertEquals(setOf(10), policy.observe(setOf(10, 20), setOf(20)))
    }
}
