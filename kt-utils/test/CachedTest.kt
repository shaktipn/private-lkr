package com.suryadigital.leo.ktUtils

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals

class CachedTest {
    @Test
    fun testNever() {
        one = 0
        val t = TestNever()
        assertEquals(0, t.cachedProperty)
        assertEquals(0, t.cachedProperty)
    }

    @Test
    fun testDuration1Second() {
        one = 0
        val t = TestOneSecond()
        assertEquals(0, t.cachedProperty)
        Thread.sleep(1100)
        assertEquals(1, t.cachedProperty)
        assertEquals(1, t.cachedProperty)
    }
}

private var one: Int = 0

private fun retrieveOne(): Int {
    val ret = one
    one += 1
    return ret
}

private class TestNever {
    val cachedProperty: Int by cached(producer = ::retrieveOne)
}

private class TestOneSecond {
    val cachedProperty: Int by cached(Cached.InvalidationPolicy.Duration(Duration.ofSeconds(1)), ::retrieveOne)
}
