package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class IntTest : AbstractTest() {
    @Test
    fun int() {
        val json =
            parse(
                """
                {
                    "value": 42
                }
                """.trimIndent(),
            )
        assertEquals(42, json.getInt("value"))
        assertNotNull(json.getIntOrNull("value"))
        assertEquals(42, json.getIntOrNull("value")!!)
        assertEquals(42, json.getIntOrDefault(key = "value", defaultValue = -1))
    }

    @Test
    fun intNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getInt("value")
        }
        assertNull(json.getIntOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getIntList("value")
        }
        assertNull(json.getIntListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun intMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getInt("value")
        }
        assertNull(json.getIntOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getIntList("value")
        }
        assertNull(json.getIntListOrNull("value"))
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value")
        }
        assertNull(json.getJsonElementListOrNull("value"))
    }

    @Test
    fun intDouble() {
        val json =
            parse(
                """
                {
                    "value": 42.5
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getInt("value")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntOrNull("value")
        }
    }

    @Test
    fun intDoubleList() {
        val json =
            parse(
                """
                {
                    "values": [42.5, 44.6, 76.7]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntList("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntListOrNull("values")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            Int.Companion.fromJson(json)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Int.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Int.Companion::fromJson)
        }
    }

    @Test
    fun intObject() {
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
            json.getInt("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getIntOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getIntList("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getIntListOrNull("value")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementList("value").map(Int.Companion::fromJson)
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonElementListOrNull("value")?.map(Int.Companion::fromJson)
        }
    }

    @Test
    fun defaultInt() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertEquals(42, json.getIntOrDefault("value", 42))
    }

    @Test
    fun intList() {
        val json =
            parse(
                """
                {
                    "values": [42, 66, 77, 88]
                }
                """.trimIndent(),
            )
        val intList = listOf(42, 66, 77, 88)
        assertEquals(4, json.getIntList("values").size)
        assertEquals(intList, json.getIntList("values"))
        assertNotNull(json.getIntListOrNull("values"))
        assertEquals(intList, json.getIntListOrNull("values"))
        assertEquals(4, json.getJsonElementList("values").size)
        assertEquals(intList, json.getJsonElementList("values").map(Int.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(intList, json.getJsonElementListOrNull("values")?.map(Int.Companion::fromJson))
        assertEquals(intList, json.getIntListOrDefault("values", listOf(21, 33)))
    }

    @Test
    fun intListInvalidType() {
        val json =
            parse(
                """
                {
                    "values": [42, 66, 77, null],
                    "invalidValues": [22, 33.0]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntList("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntListOrNull("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Int.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Int.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntListOrNull("invalidValues")
        }
    }

    @Test
    fun intString() {
        val json =
            parse(
                """
                {
                    "value": "32"
                }
                """.trimIndent(),
            )
        assertEquals(32, json.getInt("value"))
        assertNotNull(json.getIntOrNull("value"))
        assertEquals(32, json.getIntOrNull("value"))
    }

    @Test
    fun intInvalidString() {
        val json =
            parse(
                """
                {
                    "value": "not an int"
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getInt("value")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntOrNull("value")
        }
    }

    @Test
    fun intStringList() {
        val json =
            parse(
                """
                {
                    "values": ["42", 66, "77", "88"]
                }
                """.trimIndent(),
            )
        val intList = listOf(42, 66, 77, 88)
        assertEquals(4, json.getIntList("values").size)
        assertEquals(intList, json.getIntList("values"))
        assertNotNull(json.getIntListOrNull("values"))
        assertEquals(intList, json.getIntListOrNull("values"))
        assertEquals(4, json.getJsonElementList("values").size)
        assertEquals(intList, json.getJsonElementList("values").map(Int.Companion::fromJson))
        assertNotNull(json.getJsonElementListOrNull("values"))
        assertEquals(intList, json.getJsonElementListOrNull("values")?.map(Int.Companion::fromJson))
    }

    @Test
    fun intInvalidStringList() {
        val json =
            parse(
                """
                {
                    "values": ["not an int", "66", "77", "88"]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntList("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getIntListOrNull("values")
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementList("values").map(Int.Companion::fromJson)
        }
        assertFailsWithCause(NumberFormatException::class) {
            json.getJsonElementListOrNull("values")?.map(Int.Companion::fromJson)
        }
    }

    @Test
    fun defaultIntList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultIntList = listOf(42, 43, 44)
        assertEquals(defaultIntList, json.getIntListOrDefault("value", defaultIntList))
    }

    @Test
    fun intToJson() {
        assertEquals(29.toJson(), JsonPrimitive(29))
    }
}
