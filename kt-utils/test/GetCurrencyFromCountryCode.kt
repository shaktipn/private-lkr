package com.suryadigital.leo.ktUtils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetCurrencyFromCountryCode {
    @Test
    fun testForValidCountryCode() {
        assertEquals("INR", getCurrencyFromCountryCode("IN"))
        assertEquals("MWK", getCurrencyFromCountryCode("MW"))
        assertEquals("ZMW", getCurrencyFromCountryCode("ZM"))
        assertEquals("ZAR", getCurrencyFromCountryCode("ZA"))
    }

    @Test
    fun testForInValidCountryCode() {
        assertFailsWith(InvalidCurrencyCode::class) {
            getCurrencyFromCountryCode("India")
        }
        assertFailsWith(InvalidCurrencyCode::class) {
            getCurrencyFromCountryCode("INR")
        }
        assertFailsWith(InvalidCurrencyCode::class) {
            getCurrencyFromCountryCode("MWK")
        }
    }
}
