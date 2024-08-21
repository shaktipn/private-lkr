package com.suryadigital.leo.ktUtils

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals

class InstantTest {
    @Test
    fun testToUTCOffsetDateTime() {
        val offsetDateTime: OffsetDateTime = Instant.parse("2018-11-30T18:35:24.00Z").toUTCOffsetDateTime()
        assertEquals(2018, offsetDateTime.year)
        assertEquals(11, offsetDateTime.monthValue)
        assertEquals(30, offsetDateTime.dayOfMonth)
        assertEquals(18, offsetDateTime.hour)
        assertEquals(35, offsetDateTime.minute)
        assertEquals(24, offsetDateTime.second)
        assertEquals(ZoneOffset.UTC, offsetDateTime.offset)
    }
}
