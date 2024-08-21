package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class DoubleTest : AbstractTest() {
    @Test
    fun double() {
        val json =
            parse(
                """
                {
                    "value": 42.5
                }
                """.trimIndent(),
            )
        assertTrue(json.getDouble("value").equalsDelta(42.5))
        assertFalse(json.getDouble("value").equalsDelta(42.6))
        assertNotNull(json.getDoubleOrNull("value"))
        json.getDoubleOrNull("value")?.equalsDelta(42.5)?.let(::assertTrue)
        assertEquals(42.5, json.getDoubleOrDefault("value", 42.99))
    }

    @Test
    fun doubleNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getDouble("value")
        }
        assertNull(json.getDoubleOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getDoubleList("value")
        }
        assertNull(json.getDoubleListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun doubleMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getDouble("value")
        }
        assertNull(json.getDoubleOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getDoubleList("value")
        }
        assertNull(json.getDoubleListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun doubleInt() {
        val json =
            parse(
                """
                {
                    "value": 42
                }
                """.trimIndent(),
            )
        assertTrue(json.getDouble("value").equalsDelta(42))
        assertFalse(json.getDouble("value").equalsDelta(41))
        assertNotNull(json.getDoubleOrNull("value"))
        json.getDoubleOrNull("value")?.equalsDelta(42)?.let(::assertTrue)
    }

    @Test
    fun doubleIntList() {
        val json =
            parse(
                """
                {
                    "values": [42, 44, 76]
                }
                """.trimIndent(),
            )
        val parsed = json.getDoubleList("values")
        assertEquals(3, parsed.size)
        assertTrue(parsed[0].equalsDelta(42))
        assertTrue(parsed[1].equalsDelta(44))
        assertTrue(parsed[2].equalsDelta(76))
        assertNotNull(json.getDoubleListOrNull("values"))
        val parsedValues = json.getJsonElementList("values").map(Double.Companion::fromJson)
        assertEquals(3, parsedValues.size)
        assertTrue(parsedValues[0].equalsDelta(42))
        assertTrue(parsedValues[1].equalsDelta(44))
        assertTrue(parsedValues[2].equalsDelta(76))
        assertNotNull(json.getJsonElementListOrNull("values")?.map(Double.Companion::fromJson))
    }

    @Test
    fun doubleObject() {
        val json =
            parse(
                """
                {
                    "value": {
                        "anotherValue": 42.5
                    },
                    "strValue": "no"
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getDouble("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getDoubleOrNull("value")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getDoubleOrNull("strValue")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getDoubleList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getDoubleListOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementListOrNull("value")
        }
    }

    @Test
    fun defaultDouble() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertTrue(json.getDoubleOrDefault("value", 42.5).equalsDelta(42.5))
    }

    @Test
    fun doubleList() {
        val json =
            parse(
                """
                {
                    "values": [
                        1.2,
                        3.898,
                        4.5
                    ]
                }
                """.trimIndent(),
            )
        val parsed = json.getDoubleList("values")
        assertEquals(3, parsed.size)
        assertTrue(parsed[0].equalsDelta(1.2))
        assertTrue(parsed[1].equalsDelta(3.898))
        assertTrue(parsed[2].equalsDelta(4.5))
        assertNotNull(json.getDoubleListOrNull("values"))
        val parsedValues = json.getJsonElementList("values").map(Double.Companion::fromJson)
        assertEquals(3, parsedValues.size)
        assertTrue(parsedValues[0].equalsDelta(1.2))
        assertTrue(parsedValues[1].equalsDelta(3.898))
        assertTrue(parsedValues[2].equalsDelta(4.5))
        assertNotNull(json.getJsonElementListOrNull("values")?.map(Double.Companion::fromJson))
        assertEquals(listOf(1.2, 3.898, 4.5), json.getDoubleListOrDefault("values", listOf(10.5, 11.2)))
    }

    @Test
    fun doubleListInvalidType() {
        val json =
            parse(
                """
                {
                    "values": [
                        1.3,
                        4,
                        null
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getDoubleList("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getDoubleListOrNull("values")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            Double.Companion.fromJson(json)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Double.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Double.Companion::fromJson)
        }
    }

    @Test
    fun doubleString() {
        val json =
            parse(
                """
                {
                    "value": "42.5"
                }
                """.trimIndent(),
            )
        assertTrue(json.getDouble("value").equalsDelta(42.5))
        assertNotNull(json.getDoubleOrNull("value"))
        json.getDoubleOrNull("value")?.equalsDelta(42.5)?.let(::assertTrue)
    }

    @Test
    fun doubleInvalidString() {
        val json =
            parse(
                """
                {
                    "value": "not a double"
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getDouble("value")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getDoubleOrNull("value")
        }
    }

    @Test
    fun doubleStringList() {
        val json =
            parse(
                """
                {
                    "values": ["42.5", 32, 44.8, "65.67"]
                }
                """.trimIndent(),
            )
        val parsed = json.getDoubleList("values")
        assertEquals(4, parsed.size)
        assertTrue(parsed[0].equalsDelta(42.5))
        assertTrue(parsed[1].equalsDelta(32))
        assertTrue(parsed[2].equalsDelta(44.8))
        assertTrue(parsed[3].equalsDelta(65.67))
        assertNotNull(json.getDoubleListOrNull("values"))
        val parsedValues = json.getJsonElementList("values").map(Double.Companion::fromJson)
        assertEquals(4, parsedValues.size)
        assertTrue(parsedValues[0].equalsDelta(42.5))
        assertTrue(parsedValues[1].equalsDelta(32))
        assertTrue(parsedValues[2].equalsDelta(44.8))
        assertTrue(parsedValues[3].equalsDelta(65.67))
        assertNotNull(json.getJsonElementListOrNull("values")?.map(Double.Companion::fromJson))
    }

    @Test
    fun doubleInvalidStringList() {
        val json =
            parse(
                """
                {
                    "values": ["42.5", 32, "not a double"]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getDoubleList("values")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getDoubleOrNull("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Double.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Double.Companion::fromJson)
        }
    }

    @Test
    fun defaultDoubleList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertEquals(listOf(10.5, 11.2), json.getDoubleListOrDefault("value", listOf(10.5, 11.2)))
    }

    @Test
    fun doubleToJson() {
        assertEquals(expected = 22.0.toJson(), actual = JsonPrimitive(22.0))
    }
}
