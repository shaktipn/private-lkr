package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class InstantSerializerTest {
    @Serializable
    internal data class TestClassWithInstant(
        @Serializable(with = InstantSerializer::class)
        val testVal: Instant,
    )

    @Test
    fun ensureInstantSerializerWorks() {
        val testObj = TestClassWithInstant(Instant.parse("2020-04-03T14:32:44Z"))
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "2020-04-03T14:32:44Z"
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testJsonObj, jsonSerial)
        val parsedInstant = Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        assertEquals(testObj, parsedInstant)
    }

    @Test
    fun parseNullInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "2020-04-03T14:32:44"
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseIntInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseFloatInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseArrayInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }

    @Test
    fun parseObjectInstant() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": {
                        "anotherValue": 42.5
                    }
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithInstant>(testJsonObj)
        }
    }
}
