package com.suryadigital.leo.ktor.tests

import com.suryadigital.leo.ktor.metrics.Metrics
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MetricsTest {
    @Test
    fun testSyncTimed() {
        val metrics = Metrics()
        val identifier = "performSomeTask"
        metrics.syncTimed(identifier, ::performSomeTask)
        assertTrue(metrics.timers[0].identifier.contains(identifier))
        assertTrue(metrics.timers[0].duration.toMillis() >= 5)
        assertTrue("$metrics".contains("msPerformSomeTask="))
    }

    @Test
    fun testTimedFunction() {
        val metrics = Metrics()
        val identifier = "performSomeTask"
        runBlocking {
            metrics.timed(identifier, ::performSomeSuspendingTask)
        }
        assertTrue(metrics.timers[0].identifier.contains(identifier))
        assertTrue(metrics.timers[0].duration.toMillis() >= 5)
    }

    @Test
    fun testStartAndStopTimers() {
        val metrics = Metrics()
        val identifier = "performSomeTask"
        metrics.startTimer(identifier)
        performSomeTask()
        metrics.stopTimer(identifier)
        assertTrue(metrics.timers[0].identifier.contains(identifier))
        assertTrue(metrics.timers[0].duration.toMillis() >= 5)
    }

    @Test
    fun testStopTimerWithoutStart() {
        val metrics = Metrics()
        val identifier = "performSomeTask"
        performSomeTask()
        metrics.stopTimer(identifier)
        assertTrue(metrics.timers[0].identifier.contains(identifier))
        assertEquals(0, metrics.timers[0].count)
    }

    @Test
    fun testTimerClass() {
        val timer1 =
            Metrics.Timer(
                identifier = "timer",
                startTime = 100,
            )
        val timer2 =
            Metrics.Timer(
                identifier = "timer",
                startTime = 100,
            )
        val timer3 =
            Metrics.Timer(
                identifier = "timer",
                startTime = 200,
            )
        assertEquals(timer1, timer2)
        assertEquals(timer1, timer3)
        assertNotNull(timer1)
        assertEquals(timer1.hashCode(), timer2.hashCode())
        assertEquals("timer=0", "$timer1")
    }

    private fun performSomeTask() {
        sleep(5)
    }

    private suspend fun performSomeSuspendingTask() {
        delay(5)
    }
}
