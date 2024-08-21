package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoInvalidLeoPhoneNumberException
import com.suryadigital.leo.types.LeoPhoneNumber
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class LeoPhoneNumberTest : AbstractTest() {
    @Test
    fun leoPhoneNumber() {
        val json =
            parse(
                """
                {
                    "value": "+91 88623-88765"
                }
                """.trimIndent(),
            )
        val leoPhoneNumber = LeoPhoneNumber("+918862388765")
        assertEquals(leoPhoneNumber, json.getLeoPhoneNumber("value"))
        assertNotNull(json.getLeoPhoneNumber("value"))
        assertEquals(leoPhoneNumber, json.getLeoPhoneNumberOrNull("value")!!)
        assertEquals(leoPhoneNumber, json.getLeoPhoneNumberOrDefault(key = "value", defaultValue = LeoPhoneNumber("+912862388765")))
    }

    @Test
    fun leoPhoneNumberNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLeoPhoneNumber("value")
        }
        assertNull(json.getLeoPhoneNumberOrNull("value"))
    }

    @Test
    fun leoPhoneNumberMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLeoPhoneNumber("value")
        }
        assertNull(json.getLeoPhoneNumberOrNull("value"))
    }

    @Test
    fun invalidLeoPhoneNumber() {
        val json =
            parse(
                """
                {
                    "value": "+91 88623-887653",
                    "values": [
                        "+91 88623-887653"
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(LeoInvalidLeoPhoneNumberException::class) {
            json.getLeoPhoneNumber("value")
        }
        assertFailsWithCause(LeoInvalidLeoPhoneNumberException::class) {
            json.getLeoPhoneNumberOrNull("value")
        }
        assertFailsWithCause(LeoInvalidLeoPhoneNumberException::class) {
            json.getLeoPhoneNumberListOrNull("values")
        }
    }

    @Test
    fun defaultLeoPhoneNumber() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val default = LeoPhoneNumber("+918823489845")
        assertEquals(default, json.getLeoPhoneNumberOrDefault("value", default))
    }

    @Test
    fun leoPhoneNumberList() {
        val json =
            parse(
                """
                {
                    "values": [
                        "+91 88623-87653",
                        "+91 88223-87093",
                        "+91 68223-87094"
                    ]
                }
                """.trimIndent(),
            )
        val leoPhoneNumberList =
            listOf(
                LeoPhoneNumber("+918862387653"),
                LeoPhoneNumber("+918822387093"),
                LeoPhoneNumber("+916822387094"),
            )
        assertEquals(leoPhoneNumberList, json.getLeoPhoneNumberList("values"))
        assertFailsWith(LeoJSONException::class) { json.getLeoPhoneNumberList("invalid") }
        assertEquals(
            leoPhoneNumberList,
            json.getLeoPhoneNumberListOrDefault(
                key = "values",
                defaultValue = listOf(LeoPhoneNumber("+918862387653")),
            ),
        )
    }

    @Test
    fun defaultLeoPhoneNumberList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultLeoPhoneNumberList = listOf(LeoPhoneNumber("+918823489845"))
        assertEquals(defaultLeoPhoneNumberList, json.getLeoPhoneNumberListOrDefault("value", defaultLeoPhoneNumberList))
    }

    @Test
    fun leoPhoneNumberToJson() {
        val phoneNumberString = "+918823489845"
        assertEquals(LeoPhoneNumber(phoneNumberString).toJson(), JsonPrimitive(phoneNumberString))
    }
}
