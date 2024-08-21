package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class LocalDateTest : AbstractTest() {
    @Test
    fun localDate() {
        val json =
            parse(
                """
                {
                    "value": "2020-04-03"
                }
                """.trimIndent(),
            )
        val expected = LocalDate.parse("2020-04-03", DateTimeFormatter.ISO_LOCAL_DATE)
        assertEquals(expected, json.getLocalDate("value"))
        assertNotNull(json.getLocalDate("value"))
        assertEquals(expected, json.getLocalDateOrNull("value")!!)
        assertEquals(expected, json.getLocalDateOrDefault("value", LocalDate.now()))
    }

    @Test
    fun localDateNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLocalDate("value")
        }
        assertNull(json.getLocalDateOrNull("value"))
    }

    @Test
    fun localDateMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLocalDate("value")
        }
        assertNull(json.getLocalDateOrNull("value"))
    }

    @Test
    fun invalidLocalDate() {
        val json =
            parse(
                """
                {
                    "value": "not a local date",
                    "values": [
                        "not a local date"
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(DateTimeParseException::class) {
            json.getLocalDate("value")
        }
        assertFailsWithCause(DateTimeParseException::class) {
            json.getLocalDateOrNull("value")
        }
        assertFailsWithCause(DateTimeParseException::class) {
            json.getLocalDateListOrNull("values")
        }
    }

    @Test
    fun localDateList() {
        val json =
            parse(
                """
                {
                    "values": [
                        "2020-04-03",
                        "2019-02-01",
                        "2013-07-21"
                    ]
                }
                """.trimIndent(),
            )
        val localeDateList =
            listOf(
                LocalDate.parse("2020-04-03"),
                LocalDate.parse("2019-02-01"),
                LocalDate.parse("2013-07-21"),
            )
        assertEquals(localeDateList, json.getLocalDateList("values"))
        assertFailsWith(LeoJSONException::class) { json.getLocalDateList("invalid") }
        assertEquals(localeDateList, json.getLocalDateListOrDefault("values", listOf()))
    }

    @Test
    fun defaultLocalDate() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val default = LocalDate.now()
        assertEquals(default, json.getLocalDateOrDefault("value", default))
    }

    @Test
    fun defaultLocalDateList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultLocalDateList = listOf(LocalDate.now())
        assertEquals(defaultLocalDateList, json.getLocalDateListOrDefault("value", defaultLocalDateList))
    }

    @Test
    fun localDateToJson() {
        val localDateString = "2024-06-21"
        assertEquals(LocalDate.parse(localDateString).toJson(), JsonPrimitive(localDateString))
    }
}
