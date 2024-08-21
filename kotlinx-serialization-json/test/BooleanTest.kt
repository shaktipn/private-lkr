package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class BooleanTest : AbstractTest() {
    @Test
    fun boolean() {
        val json =
            parse(
                """
                {
                    "value": true
                }
                """.trimIndent(),
            )
        assertTrue(json.getBoolean("value"))
        assertNotNull(json.getBooleanOrNull("value"))
        assertTrue(json.getBooleanOrNull("value")!!)
        assertTrue { json.getBooleanOrDefault(key = "value", defaultValue = false) }
    }

    @Test
    fun booleanNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getBoolean("value")
        }
        assertNull(json.getBooleanOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getBooleanList("value")
        }
        assertNull(json.getBooleanListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun booleanMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getBoolean("value")
        }
        assertNull(json.getBooleanOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getBooleanList("value")
        }
        assertNull(json.getBooleanListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun booleanInt() {
        val json =
            parse(
                """
                {
                    "value": 42
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalStateException::class) {
            json.getBoolean("value")
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getBooleanOrNull("value")
        }
    }

    @Test
    fun booleanIntList() {
        val json =
            parse(
                """
                {
                    "values": [42, 43]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalStateException::class) {
            json.getBooleanList("values")
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getBooleanListOrNull("values")
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getJsonElementList("values").map(Boolean.Companion::fromJson)
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getJsonElementListOrNull("values")?.map(Boolean.Companion::fromJson)
        }
    }

    @Test
    fun booleanObject() {
        val json =
            parse(
                """
                {
                    "value": {
                        "anotherValue": 42.5
                    }
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getBoolean("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getBooleanOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getBooleanList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getBooleanListOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementListOrNull("value")
        }
    }

    @Test
    fun defaultBoolean() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFalse(json.getBooleanOrDefault("value", false))
    }

    @Test
    fun booleanList() {
        val json =
            parse(
                """
                {
                    "values": [
                        true,
                        false,
                        false
                    ]
                }
                """.trimIndent(),
            )
        val booleanList = listOf(true, false, false)
        assertEquals(3, json.getBooleanList("values").size)
        assertEquals(booleanList, json.getBooleanList("values"))
        assertNotNull(json.getBooleanListOrNull("values"))
        assertEquals(booleanList, json.getBooleanListOrNull("values"))
        assertEquals(3, json.getJsonElementList("values").map(Boolean.Companion::fromJson).size)
        assertEquals(booleanList, json.getJsonElementList("values").map(Boolean.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(booleanList, json.getJsonElementListOrNull("values")?.map(Boolean.Companion::fromJson))
        assertEquals(booleanList, json.getBooleanListOrDefault(key = "values", defaultValue = listOf(false, false)))
    }

    @Test
    fun booleanListInvalidType() {
        val json =
            parse(
                """
                {
                    "values": [
                        true,
                        false,
                        null
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalStateException::class) {
            json.getBooleanList("values")
        }
        assertNull(json.getBooleanListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value").map(Boolean.Companion::fromJson)
        }
        assertNull(json.getJsonElementListOrNull("value")?.map(Boolean.Companion::fromJson))
    }

    @Test
    fun booleanString() {
        val json =
            parse(
                """
                {
                    "value": "true"
                }
                """.trimIndent(),
            )
        assertEquals(true, json.getBoolean("value"))
        assertNotNull(json.getBooleanOrNull("value"))
        assertEquals(true, json.getBooleanOrNull("value"))
    }

    @Test
    fun booleanInvalidString() {
        val json =
            parse(
                """
                {
                    "value": "not a boolean"
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalStateException::class) {
            json.getBoolean("value")
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getBooleanOrNull("value")
        }
    }

    @Test
    fun booleanStringList() {
        val json =
            parse(
                """
                {
                    "values": [
                        "true",
                        "false",
                        false
                    ]
                }
                """.trimIndent(),
            )
        val booleanList = listOf(true, false, false)
        assertEquals(3, json.getBooleanList("values").size)
        assertEquals(booleanList, json.getBooleanList("values"))
        assertNotNull(json.getBooleanListOrNull("values"))
        assertEquals(booleanList, json.getBooleanListOrNull("values"))
        assertEquals(3, json.getJsonElementList("values").map(Boolean.Companion::fromJson).size)
        assertEquals(booleanList, json.getJsonElementList("values").map(Boolean.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(booleanList, json.getJsonElementListOrNull("values")?.map(Boolean.Companion::fromJson))
    }

    @Test
    fun booleanInvalidStringList() {
        val json =
            parse(
                """
                {
                    "values": ["not a boolean", true, null]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalStateException::class) {
            json.getBooleanList("values")
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getBooleanListOrNull("values")
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getJsonElementList("values").map(Boolean.Companion::fromJson)
        }
        assertFailsWithCause(IllegalStateException::class) {
            json.getJsonElementListOrNull("values")?.map(Boolean.Companion::fromJson)
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            Boolean.fromJson(json)
        }
    }

    @Test
    fun defaultBooleanList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultBooleanList = listOf(false, true, false)
        assertEquals(defaultBooleanList, json.getBooleanListOrDefault("value", defaultBooleanList))
    }

    @Test
    fun booleanToJson() {
        assertEquals(true.toJson(), JsonPrimitive(value = true))
    }
}
