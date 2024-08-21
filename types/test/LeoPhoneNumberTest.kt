package com.suryadigital.leo.types

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LeoPhoneNumberTest {
    @Test
    fun validLeoPhoneNumber() {
        val leoPhoneNumber = LeoPhoneNumber("+91 88624 03344")
        assertEquals("+918862403344", leoPhoneNumber.value)
    }

    @Test
    fun leoPhoneNumberWithoutPlus() {
        assertFailsWith(LeoInvalidLeoPhoneNumberException::class) {
            LeoPhoneNumber("8862403344")
        }
    }

    @Test
    fun invalidLeoPhoneNumberIndia() {
        assertFailsWith(LeoInvalidLeoPhoneNumberException::class) {
            LeoPhoneNumber("+91 88624033441")
        }
    }

    @Test
    fun invalidLeoPhoneNumberUSA() {
        assertFailsWith(LeoInvalidLeoPhoneNumberException::class) {
            LeoPhoneNumber("+1 (734) 867 53092")
        }
    }

    @Test
    fun invalidLeoPhoneNumberINRegion() {
        assertFailsWith(LeoInvalidLeoPhoneNumberException::class) {
            LeoPhoneNumber("+918862403344", listOf("GB", "US"))
        }
    }

    @Test
    fun validLeoPhoneNumberINRegion() {
        val leoPhoneNumber = LeoPhoneNumber("+91 88624 03344", listOf("IN", "US"))
        assertEquals("+918862403344", leoPhoneNumber.value)
    }

    @Test
    fun invalidCountryRegion() {
        assertFailsWith(LeoInvalidLeoPhoneNumberException::class) {
            LeoPhoneNumber("+91 88624 03344", listOf("IND", "US"))
        }
    }

    @Test
    fun testRegionCodeForINRLeoPhoneNumber() {
        val leoPhoneNumber = LeoPhoneNumber("+918862403344")
        assertEquals("IN", leoPhoneNumber.getRegionCode())
    }

    @Test
    fun testRegionCodeForMWKLeoPhoneNumber() {
        val leoPhoneNumber = LeoPhoneNumber("+2651348922")
        assertEquals("MW", leoPhoneNumber.getRegionCode())
    }

    @Test
    fun testRegionCodeForZMWLeoPhoneNumber() {
        val leoPhoneNumber = LeoPhoneNumber("+260211886240")
        assertEquals("ZM", leoPhoneNumber.getRegionCode())
    }

    @Test
    fun testRegionCodeForZARLeoPhoneNumber() {
        val leoPhoneNumber = LeoPhoneNumber("+27 11 978 5313")
        assertEquals("ZA", leoPhoneNumber.getRegionCode())
    }

    @Test
    fun testFormattedPhoneNumberIndia() {
        val leoPhoneNumber = LeoPhoneNumber("+918862403344")
        assertEquals("+91 88624 03344", leoPhoneNumber.getFormattedPhoneNumber())
    }

    @Test
    fun testFormattedPhoneNumberMalawi() {
        val leoPhoneNumber = LeoPhoneNumber("+265888912552")
        assertEquals("+265 888 91 25 52", leoPhoneNumber.getFormattedPhoneNumber())
    }

    @Test
    fun testFormattedPhoneNumberUSA() {
        val leoPhoneNumber = LeoPhoneNumber("+15853042917")
        assertEquals("+1 585-304-2917", leoPhoneNumber.getFormattedPhoneNumber())
    }

    @Test
    fun testInvalidRegionCode() {
        assertFailsWith(LeoInvalidLeoPhoneNumberException::class) {
            val leoPhoneNumber = LeoPhoneNumber("+0234 978 5313")
            leoPhoneNumber.getRegionCode()
        }
    }
}
