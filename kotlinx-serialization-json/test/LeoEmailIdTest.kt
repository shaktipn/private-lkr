package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoEmailId
import com.suryadigital.leo.types.LeoInvalidLeoEmailIdException
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class LeoEmailIdTest : AbstractTest() {
    @Test
    fun leoEmailId() {
        val json =
            parse(
                """
                {
                    "value": "FOO@bar.com"
                }
                """.trimIndent(),
            )
        val leoEmailId = LeoEmailId("foo@bar.com")
        assertEquals(leoEmailId, json.getLeoEmailId("value"))
        assertNotNull(json.getLeoEmailIdOrNull("value"))
        assertEquals(leoEmailId, json.getLeoEmailIdOrNull("value")!!)
        assertEquals(leoEmailId, json.getLeoEmailIdOrDefault(key = "value", defaultValue = LeoEmailId("invalid@bar.com")))
    }

    @Test
    fun leoEmailIdNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLeoEmailId("value")
        }
        assertNull(json.getLeoEmailIdOrNull("value"))
    }

    @Test
    fun leoEmailIdMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getLeoEmailId("value")
        }
        assertNull(json.getLeoEmailIdOrNull("value"))
    }

    @Test
    fun invalidLeoEmailId() {
        val json =
            parse(
                """
                {
                    "value": "foo",
                    "listValue": [ 
                        "bar",
                        "foo"
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(LeoInvalidLeoEmailIdException::class) {
            json.getLeoEmailId("value")
        }
        assertFailsWithCause(LeoInvalidLeoEmailIdException::class) {
            json.getLeoEmailIdOrNull("value")
        }
        assertFailsWithCause(LeoInvalidLeoEmailIdException::class) {
            json.getLeoEmailIdListOrNull("listValue")
        }
        assertFailsWithCause(LeoInvalidLeoEmailIdException::class) {
            json.getLeoEmailIdListOrDefault(key = "listValue", defaultValue = listOf(LeoEmailId("foo@bar.com")))
        }
        assertFailsWith(LeoJSONException::class) {
            json.getLeoEmailIdList(key = "invalidKey")
        }
        assertEquals(null, json.getLeoEmailIdListOrNull("invalidKey"))
    }

    @Test
    fun defaultLeoEmailId() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultLeoEmailId = LeoEmailId("foo@bar.com")
        assertEquals(defaultLeoEmailId, json.getLeoEmailIdOrDefault("value", defaultLeoEmailId))
    }

    @Test
    fun leoEmailIdList() {
        val json =
            parse(
                """
                {
                    "values": [
                        "foo1@bar.com",
                        "foo2@bar.com",
                        "bar1@foo.com"
                    ]
                }
                """.trimIndent(),
            )
        val leoEmailIdList =
            listOf(
                LeoEmailId("foo1@bar.com"),
                LeoEmailId("foo2@bar.com"),
                LeoEmailId("bar1@foo.com"),
            )
        assertEquals(leoEmailIdList, json.getLeoEmailIdList("values"))
        assertEquals(leoEmailIdList, json.getLeoEmailIdListOrNull("values"))
        assertEquals(leoEmailIdList, json.getLeoEmailIdListOrDefault("values", listOf(LeoEmailId("invalid@bar.com"))))
    }

    @Test
    fun defaultLeoEmailIdList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultLeoEmailIdList = listOf(LeoEmailId("foo@bar.com"), LeoEmailId("abc@beta.com"))
        assertEquals(defaultLeoEmailIdList, json.getLeoEmailIdListOrDefault("value", defaultLeoEmailIdList))
    }

    @Test
    fun leoEmailIdToJson() {
        val email = "somemail@foo.com"
        assertEquals(LeoEmailId(email).toJson(), JsonPrimitive(email))
    }
}
