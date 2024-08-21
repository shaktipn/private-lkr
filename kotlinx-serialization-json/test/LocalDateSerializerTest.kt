package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LocalDateSerializerTest {
    @Serializable
    internal data class TestClassWithLocalDate(
        @Serializable(with = LocalDateSerializer::class)
        val testVal: LocalDate,
    )

    @Test
    fun ensureLocalDateSerializerWorks() {
        val testObj = TestClassWithLocalDate(LocalDate.parse("2020-04-03"))
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "2020-04-03"
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testJsonObj, jsonSerial)
        val parsedLocalDate = Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        assertEquals(testObj, parsedLocalDate)
    }

    @Test
    fun parseNullLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "2020-04-03T14:32:44"
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseIntLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseFloatLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseArrayLocalDate() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }

    @Test
    fun parseObjectLocalDate() {
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
            Json.decodeFromJsonElement<TestClassWithLocalDate>(testJsonObj)
        }
    }
}
