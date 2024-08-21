package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class LongTest : AbstractTest() {
    @Test
    fun long() {
        val json =
            parse(
                """
                {
                    "value": 42
                }
                """.trimIndent(),
            )
        assertEquals(42L, json.getLong("value"))
        assertNotNull(json.getLongOrNull("value"))
        assertEquals(42L, json.getLongOrNull("value")!!)
        assertEquals(42L, json.getLongOrDefault("value", 33L))
    }

    @Test
    fun longNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLong("value")
        }
        assertNull(json.getLongOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getLongList("value")
        }
        assertNull(json.getLongListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun longMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLong("value")
        }
        assertNull(json.getLongOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getLongList("value")
        }
        assertNull(json.getLongListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun longDouble() {
        val json =
            parse(
                """
                {
                    "value": 42.5
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getLong("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getLongOrNull("value")
        }
    }

    @Test
    fun longDoubleList() {
        val json =
            parse(
                """
                {
                    "values": [42.5, 44.6, 76.7]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getLongList("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getLongListOrNull("values")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            Long.Companion.fromJson(json)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Long.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Long.Companion::fromJson)
        }
    }

    @Test
    fun longObject() {
        val json =
            parse(
                """
                {
                    "value": {
                        "anotherValue": 42
                    }
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getLong("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getLongOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getLongList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getLongListOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value").map(Long.Companion::fromJson)
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementListOrNull("value")?.map(Long.Companion::fromJson)
        }
    }

    @Test
    fun defaultLong() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertEquals(42L, json.getLongOrDefault("value", 42L))
    }

    @Test
    fun longList() {
        val json =
            parse(
                """
                {
                    "values": [42, 66, 77, 88]
                }
                """.trimIndent(),
            )
        val longList = listOf(42L, 66L, 77L, 88L)
        assertEquals(4, json.getLongList("values").size)
        assertEquals(longList, json.getLongList("values"))
        assertNotNull(json.getLongListOrNull("values"))
        assertEquals(longList, json.getLongListOrNull("values"))
        assertEquals(4, json.getJsonElementList("values").size)
        assertEquals(longList, json.getJsonElementList("values").map(Long.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(longList, json.getJsonElementListOrNull("values")?.map(Long.Companion::fromJson))
        assertEquals(longList, json.getLongListOrDefault("values", listOf(42L, 22L, 77L, 88L)))
    }

    @Test
    fun longListInvalidType() {
        val json =
            parse(
                """
                {
                    "values": [32, null]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getLongList("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getLongListOrNull("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Long.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Long.Companion::fromJson)
        }
    }

    @Test
    fun longString() {
        val json =
            parse(
                """
                {
                    "value": "42"
                }
                """.trimIndent(),
            )
        assertEquals(42L, json.getLong("value"))
        assertNotNull(json.getLongOrNull("value"))
        assertEquals(42L, json.getLongOrNull("value"))
    }

    @Test
    fun longInvalidString() {
        val json =
            parse(
                """
                {
                    "value": "32M"
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getLong("value")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getLongOrNull("value")
        }
    }

    @Test
    fun longStringList() {
        val json =
            parse(
                """
                {
                    "values": ["42", "66", "77", "88"]
                }
                """.trimIndent(),
            )
        val longList = listOf(42L, 66L, 77L, 88L)
        assertEquals(4, json.getLongList("values").size)
        assertEquals(longList, json.getLongList("values"))
        assertNotNull(json.getLongListOrNull("values"))
        assertEquals(longList, json.getLongListOrNull("values"))
        assertEquals(4, json.getJsonElementList("values").size)
        assertEquals(longList, json.getJsonElementList("values").map(Long.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(longList, json.getJsonElementListOrNull("values")?.map(Long.Companion::fromJson))
    }

    @Test
    fun longInvalidStringList() {
        val json =
            parse(
                """
                {
                    "values": ["42m", "66", "77", "88"]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getLongList("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getLongListOrNull("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Long.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Long.Companion::fromJson)
        }
    }

    @Test
    fun defaultLongList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultLongList = listOf(42L, 54L)
        assertEquals(defaultLongList, json.getLongListOrDefault("value", defaultLongList))
    }

    @Test
    fun longToJson() {
        val long = 1000000000000000L
        assertEquals(long.toJson(), JsonPrimitive(long))
    }
}
