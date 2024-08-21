package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class StringTest : AbstractTest() {
    @Test
    fun string() {
        val json =
            parse(
                """
                {
                    "value": "foo"
                }
                """.trimIndent(),
            )
        val testString = "foo"
        assertEquals(testString, json.getString("value"))
        assertNotNull(json.getStringOrNull("value"))
        assertEquals(testString, json.getStringOrNull("value")!!)
        assertEquals(testString, json.getStringOrDefault("value", "DefaultValue"))
    }

    @Test
    fun stringNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getString("value")
        }
        assertNull(json.getStringOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getStringList("value")
        }
        assertNull(json.getStringListOrNull("value"))
    }

    @Test
    fun stringMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getString("value")
        }
        assertNull(json.getStringOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getStringList("value")
        }
        assertNull(json.getStringListOrNull("value"))
    }

    @Test
    fun stringInt() {
        val json =
            parse(
                """
                {
                    "value": 42
                }
                """.trimIndent(),
            )
        assertEquals("42", json.getString("value"))
        assertNotNull(json.getStringOrNull("value"))
        assertEquals("42", json.getStringOrNull("value")!!)
    }

    @Test
    fun stringIntList() {
        val json =
            parse(
                """
                {
                    "values": [42, 43, 44]
                }
                """.trimIndent(),
            )
        val stringList = listOf("42", "43", "44")
        assertEquals(stringList, json.getStringList("values"))
        assertNotNull(json.getStringListOrNull("values"))
        assertEquals(stringList, json.getStringListOrNull("values"))
        assertEquals(stringList, json.getJsonElementList("values").map(String.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(stringList, json.getJsonElementListOrNull("values")?.map(String.Companion::fromJson))
    }

    @Test
    fun stringDouble() {
        val json =
            parse(
                """
                {
                    "value": 42.5
                }
                """.trimIndent(),
            )
        assertEquals("42.5", json.getString("value"))
        assertNotNull(json.getStringOrNull("value"))
        assertEquals("42.5", json.getStringOrNull("value")!!)
    }

    @Test
    fun stringDoubleList() {
        val json =
            parse(
                """
                {
                    "values": [42.5, 55.55, 67.77]
                }
                """.trimIndent(),
            )
        val stringList = listOf("42.5", "55.55", "67.77")
        assertEquals(stringList, json.getStringList("values"))
        assertNotNull(json.getStringListOrNull("values"))
        assertEquals(stringList, json.getStringListOrNull("values"))
        assertEquals(stringList, json.getJsonElementList("values").map(String.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(stringList, json.getJsonElementListOrNull("values")?.map(String.Companion::fromJson))
    }

    @Test
    fun stringObject() {
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
            json.getString("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getStringOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementListOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getStringList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getStringListOrNull("value")
        }
    }

    @Test
    fun defaultString() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertEquals("foo", json.getStringOrDefault("value", "foo"))
    }

    @Test
    fun stringList() {
        val json =
            parse(
                """
                {
                    "values" : ["String-1", "String-2"]
                }
                """.trimIndent(),
            )
        val stringList = listOf("String-1", "String-2")
        assertEquals(stringList, json.getJsonElementList("values").map(String.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(stringList, json.getJsonElementListOrNull("values")?.map(String.Companion::fromJson))
        assertEquals(2, json.getStringList("values").size)
        assertEquals(stringList, json.getStringList("values"))
        assertEquals(2, json.getStringListOrNull("values")?.size)
        assertNotNull(json.getStringListOrNull("values"))
        assertEquals(stringList, json.getStringListOrNull("values"))
        assertEquals(stringList, json.getStringListOrDefault("values", listOf()))
    }

    @Test
    fun stringListInvalidType() {
        val json =
            parse(
                """
                {
                    "values" : ["String-1", null]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalArgumentException::class) {
            String.Companion.fromJson(json)
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("values").map(String.Companion::fromJson)
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementListOrNull("values")?.map(String.Companion::fromJson)
        }
        assertFailsWith(LeoJSONException::class) {
            json.getStringList("values")
        }
        assertNull(json.getStringListOrNull("values"))
    }

    @Test
    fun defaultStringList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultStringList = listOf("foo", "bar")
        assertEquals(defaultStringList, json.getStringListOrDefault("value", defaultStringList))
    }

    @Test
    fun stringToJson() {
        val string = "thisIsAString"
        assertEquals(string.toJson(), JsonPrimitive(string))
    }
}
