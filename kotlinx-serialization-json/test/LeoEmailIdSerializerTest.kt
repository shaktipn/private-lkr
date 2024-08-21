package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoEmailId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LeoEmailIdSerializerTest {
    @Serializable
    internal data class TestClassWithLeoEmailId(
        @Serializable(with = LeoEmailIdSerializer::class)
        val testVal: LeoEmailId,
    )

    @Test
    fun ensureLeoEmailIdSerializerWorks() {
        val testObj = TestClassWithLeoEmailId(LeoEmailId("testemail@mail.com"))
        val testObjJson =
            parse(
                """
                {
                    "testVal": "testemail@mail.com"
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testObjJson, jsonSerial)
        val parsedLeoEmailId = Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testObjJson)
        assertEquals(testObj, parsedLeoEmailId)
    }

    @Test
    fun parseNullLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "testemail_mail.com"
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseIntInsteadOfLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleInsteadOfLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseFloatInsteadOfLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringInsteadOfLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringInsteadOfLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanInsteadOfLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseArrayInsteadOfLeoEmailId() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }

    @Test
    fun parseObjectInsteadOfLeoEmailId() {
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
            Json.decodeFromJsonElement<TestClassWithLeoEmailId>(testJsonObj)
        }
    }
}
