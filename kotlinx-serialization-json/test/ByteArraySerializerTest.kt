package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ByteArraySerializerTest {
    @Serializable
    internal data class TestClassWithByteArray(
        @Serializable(with = ByteArraySerializer::class)
        val testVal: ByteArray,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestClassWithByteArray

            return testVal.contentEquals(other.testVal)
        }

        override fun hashCode(): Int {
            return testVal.contentHashCode()
        }
    }

    @Test
    fun ensureByteArraySerializerWorks() {
        val testObj = TestClassWithByteArray("Test String".toByteArray())
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "VGVzdCBTdHJpbmc="
                }
                """.trimIndent(),
            )
        val jsonSerial = Json.encodeToJsonElement(testObj)
        assertEquals(testJsonObj, jsonSerial)
        val parsedByteArray = Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        assertEquals(testObj, parsedByteArray)
    }

    @Test
    fun parseNullByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseInvalidByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": "erraneousStringCode "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseIntByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseDoubleByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseFloatByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": 1.1f
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseEmptyStringByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": ""
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseWhiteSpaceStringByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": " "
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseBooleanByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": true
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseArrayByteArray() {
        val testJsonObj =
            parse(
                """
                {
                    "testVal": [1,3,5]
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }

    @Test
    fun parseObjectByteArray() {
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
            Json.decodeFromJsonElement<TestClassWithByteArray>(testJsonObj)
        }
    }
}
