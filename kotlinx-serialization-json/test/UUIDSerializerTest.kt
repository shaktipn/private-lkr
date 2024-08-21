package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class UUIDSerializerTest {
    @Serializable
    internal data class TestClassWithUUID(
        @Serializable(with = UUIDSerializer::class)
        val testVal: UUID,
    )

    @Test
    fun ensureUUIDSerializerWorks() {
        val testObj = TestClassWithUUID(UUID.fromString("19450fcb-e78c-4687-9d45-d57dc950c98e"))
        val testObjJson =
            parse(
                """
                {
                    "testVal": "19450fcb-e78c-4687-9d45-d57dc950c98e"
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testObjJson, jsonSerial)
        val parsedUUID = Json.decodeFromJsonElement<TestClassWithUUID>(testObjJson)
        assertEquals(testObj, parsedUUID)
    }

    @Test
    fun parseNullUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "testemail_mail.com"
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseIntUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseFloatUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseArrayUUID() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }

    @Test
    fun parseObjectUUID() {
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
            Json.decodeFromJsonElement<TestClassWithUUID>(testJsonObj)
        }
    }
}
