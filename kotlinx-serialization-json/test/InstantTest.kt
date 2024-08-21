package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class InstantTest : AbstractTest() {
    @Test
    fun instant() {
        val json =
            parse(
                """
                {
                    "value": "2020-04-03T14:32:44+05:30"
                }
                """.trimIndent(),
            )
        val expected =
            OffsetDateTime.parse("2020-04-03T14:32:44+05:30", DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
        assertEquals(expected, json.getInstant("value"))
        assertNotNull(json.getInstant("value"))
        assertEquals(expected, json.getInstantOrNull("value")!!)
        assertEquals(expected, json.getInstantOrDefault("value", Instant.now()))
    }

    @Test
    fun instantNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getInstant("value")
        }
        assertNull(json.getInstantOrNull("value"))
    }

    @Test
    fun instantMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getInstant("value")
        }
        assertNull(json.getInstantOrNull("value"))
    }

    @Test
    fun invalidInstant() {
        val json =
            parse(
                """
                {
                    "value": "not an instant",
                    "invalidList": [
                        "2020-04-03T14:32:44+05:30",
                        "2020-04-03T14:32:44+05-30"
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(DateTimeParseException::class) {
            json.getInstant("value")
        }
        assertFailsWithCause(DateTimeParseException::class) {
            json.getInstantOrNull("value")
        }
        assertFailsWithCause(DateTimeParseException::class) {
            json.getInstantListOrNull("invalidList")
        }
    }

    @Test
    fun defaultInstant() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val default = Instant.now()
        assertEquals(default, json.getInstantOrDefault("value", default))
    }

    @Test
    fun instantList() {
        val json =
            parse(
                """
                {
                    "value": [
                        "2020-04-03T14:32:44+05:30",
                        "2022-04-03T14:32:44+05:30",
                        "2023-05-03T14:32:44+05:30"
                        ]
                }
                """.trimIndent(),
            )
        val instantList =
            listOf(
                Instant.parse("2020-04-03T14:32:44+05:30"),
                Instant.parse("2022-04-03T14:32:44+05:30"),
                Instant.parse("2023-05-03T14:32:44+05:30"),
            )
        assertEquals(instantList, json.getInstantListOrNull("value"))
        assertEquals(instantList, json.getInstantListOrDefault(key = "value", defaultValue = listOf()))
        assertEquals(instantList, json.getInstantList(key = "value"))
        assertFailsWith<LeoJSONException> { json.getInstantList("invalidKey") }
    }

    @Test
    fun defaultInstantList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultInstantList = listOf(Instant.now())
        assertEquals(defaultInstantList, json.getInstantListOrDefault("value", defaultInstantList))
    }

    @Test
    fun instantToJson() {
        val instantString = "2024-06-20T08:36:51.629471876Z"
        assertEquals(Instant.parse(instantString).toJson(), JsonPrimitive(instantString))
    }
}
