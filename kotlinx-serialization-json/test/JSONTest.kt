package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class JSONTest : AbstractTest() {
    @Test
    fun jsonObject() {
        val json =
            parse(
                """
                {
                    "object": {
                        "key": "value"
                    },
                    "null": null,
                    "invalidObject": "primitive",
                    "stuff": 24
                }
                """.trimIndent(),
            )
        assertEquals(json.getJsonObject("object"), JsonObject(mapOf("key" to JsonPrimitive("value"))))
        assertFailsWith(LeoJSONException::class) { json.getJsonObject("invalid") }
        assertEquals(json.getJsonObjectOrNull("null"), null)
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getJsonObjectOrNull("invalidObject")
        }
        assertFailsWith(LeoJSONException::class) {
            json.getJsonObjectOrNull("stuff")
        }
    }

    @Test
    fun jsonObjectList() {
        val json =
            parse(
                """
                {
                    "objects": [
                        {
                            "key": "value"
                        },
                        {
                            "key2": "value2"
                        }
                    ],
                    "invalidObjects": [
                        {
                            "key": "value"
                        },
                        "primitive"
                    ]
                }
                """.trimIndent(),
            )
        val jsonObjectList =
            listOf(
                JsonObject(mapOf("key" to JsonPrimitive("value"))),
                JsonObject(mapOf("key2" to JsonPrimitive("value2"))),
            )
        assertEquals(json.getJsonObjectList("objects"), jsonObjectList)
        assertEquals(json.getJsonObjectListOrDefault("invalid", jsonObjectList), jsonObjectList)
        assertEquals(json.getJsonObjectListOrDefault("objects", listOf()), jsonObjectList)
        assertFailsWith(LeoJSONException::class) { json.getJsonObjectList("invalid") }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getJsonObjectListOrNull("invalidObjects")
        }
    }

    @Test
    fun jsonArray() {
        val json =
            parse(
                """
                {
                    "values": [
                        "someValue1",
                        "someValue2",
                        "someValue3"
                    ],
                    "invalidArray": "primitive"
                }
                """.trimIndent(),
            )
        assertEquals(
            json.getJsonArray("values"),
            JsonArray(
                listOf(
                    JsonPrimitive("someValue1"),
                    JsonPrimitive("someValue2"),
                    JsonPrimitive("someValue3"),
                ),
            ),
        )
        assertFailsWith(LeoJSONException::class) {
            json.getJsonArray("invalid")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getJsonArrayOrNull("invalidArray")
        }
    }
}
