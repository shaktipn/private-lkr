package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class URLSerializerTest {
    @Serializable
    internal data class TestClassWithURL(
        @Serializable(with = URLSerializer::class)
        val testVal: URL,
    )

    @Test
    fun ensureURLSerializerWorks() {
        val testObj = TestClassWithURL(URL("https://www.testurl.com"))
        val testObjJson =
            parse(
                """
                {
                    "testVal": "https://www.testurl.com"
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testObjJson, jsonSerial)
        val parsedURL = Json.decodeFromJsonElement<TestClassWithURL>(testObjJson)
        assertEquals(testObj, parsedURL)
    }

    @Test
    fun parseNullURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "www_testurl_com"
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseIntURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseFloatURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseArrayURL() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }

    @Test
    fun parseObjectURL() {
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
            Json.decodeFromJsonElement<TestClassWithURL>(testJsonObj)
        }
    }
}
