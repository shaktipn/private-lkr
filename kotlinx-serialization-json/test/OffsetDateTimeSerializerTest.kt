package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.OffsetDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class OffsetDateTimeSerializerTest {
    @Serializable
    internal data class TestClassWithOffsetDateTime(
        @Serializable(with = OffsetDateTimeSerializer::class)
        val testVal: OffsetDateTime,
    )

    @Test
    fun ensureOffsetDateTimeSerializerWorks() {
        val testObj = TestClassWithOffsetDateTime(OffsetDateTime.parse("2020-04-03T14:32:44+05:30"))
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "2020-04-03T14:32:44+05:30"
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testJsonObj, jsonSerial)
        val parsedOffsetDateTime = Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        assertEquals(testObj, parsedOffsetDateTime)
    }

    @Test
    fun parseNullOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "2020-04-03T14:32:44"
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseIntOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseFloatOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseArrayOffsetDateTime() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }

    @Test
    fun parseObjectOffsetDateTime() {
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
            Json.decodeFromJsonElement<TestClassWithOffsetDateTime>(testJsonObj)
        }
    }
}
