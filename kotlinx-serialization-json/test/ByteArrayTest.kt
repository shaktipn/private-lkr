package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ByteArrayTest : AbstractTest() {
    @Test
    fun byteArray() {
        val byteArray = "foo".toByteArray()
        val base64EncodedByteArray = Base64.getEncoder().encodeToString(byteArray)
        val json =
            parse(
                """
                {
                    "value": "$base64EncodedByteArray"
                }
                """.trimIndent(),
            )
        assertTrue(byteArray.contentEquals(json.getByteArray("value")))
        assertTrue(byteArray.contentEquals(json.getByteArrayOrDefault("value", defaultValue = byteArrayOf(108, 111))))
        assertNotNull(json.getByteArrayOrNull("value"))
        assertTrue(byteArray.contentEquals(json.getByteArrayOrNull("value")!!))
    }

    @Test
    fun byteArrayNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getByteArray("value")
        }
        assertNull(json.getByteArrayOrNull("value"))
    }

    @Test
    fun byteArrayMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getByteArray("value")
        }
        assertNull(json.getByteArrayOrNull("value"))
    }

    @Test
    fun invalidByteArray() {
        val json =
            parse(
                """
                {
                    "value": "not a byte array"
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getByteArray("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getByteArrayOrNull("value")
        }
    }

    @Test
    fun defaultByteArray() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val default = "foo".toByteArray()
        assertTrue("foo".toByteArray().contentEquals(json.getByteArrayOrDefault("value", default)))
    }

    @Test
    fun arrayOfByteArraysNull() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertNull(json.getByteArrayListOrNull("values"))
    }

    @Test
    fun arrayOfByteArrays() {
        val json =
            parse(
                """
                {
                    "values": [
                        "Zm9v",
                        "YmFy",
                        "YmF6"
                    ]
                }
                """.trimIndent(),
            )
        val parsed = json.getByteArrayList("values")
        assertEquals(3, parsed.size)
        assertTrue("foo".toByteArray().contentEquals(parsed[0]))
        assertTrue("bar".toByteArray().contentEquals(parsed[1]))
        assertTrue("baz".toByteArray().contentEquals(parsed[2]))
        assertFailsWith(LeoJSONException::class) { json.getByteArrayList("invalidKey") }
    }

    @Test
    fun arrayOfInvalidTypes() {
        val json =
            parse(
                """
                {
                    "values": [
                        "@",
                        "Zm9v",
                        1
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getByteArrayList("values")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getByteArrayListOrNull("values")
        }
    }

    @Test
    fun byteArrayToJson() {
        assertEquals(
            expected = Base64.getDecoder().decode("conv66Z+/22ToByteArray").toJson(),
            actual = JsonPrimitive("conv66Z+/22ToByteArraw=="),
        )
    }
}
