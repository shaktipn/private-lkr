package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoPhoneNumber
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeoPhoneNumberSerializerTest {
    @Serializable
    internal data class TestClassWithLeoPhoneNumber(
        @Serializable(with = LeoPhoneNumberSerializer::class)
        val testVal: LeoPhoneNumber,
    )

    @Test
    fun ensureLeoPhoneNumberSerializerWorks() {
        val testObj = TestClassWithLeoPhoneNumber(LeoPhoneNumber("+919806573241"))
        val testObjJson =
            parse(
                """
                {
                    "testVal": "+919806573241"
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testObjJson, jsonSerial)
        val parsedLeoPhoneNumber = Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testObjJson)
        assertEquals(testObj, parsedLeoPhoneNumber)
    }

    @Test
    fun parseNullInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "9806573241"
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseIntInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseFloatInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseArrayInsteadOfLeoPhoneNumber() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }

    @Test
    fun parseObjectInsteadOfLeoPhoneNumber() {
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
            Json.decodeFromJsonElement<TestClassWithLeoPhoneNumber>(testJsonObj)
        }
    }
}
