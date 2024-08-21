package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class UUIDTest : AbstractTest() {
    @Test
    fun uuid() {
        val json =
            parse(
                """
                {
                    "value": "d9fdf316-fec5-44f1-9a92-66a51f439eb3"
                }
                """.trimIndent(),
            )
        val testUUID = UUID.fromString("d9fdf316-fec5-44f1-9a92-66a51f439eb3")
        assertEquals(testUUID, json.getUUID("value"))
        assertNotNull(json.getUUID("value"))
        assertEquals(testUUID, json.getUUIDOrNull("value")!!)
        assertEquals(testUUID, json.getUUIDOrDefault("value", UUID.randomUUID()))
    }

    @Test
    fun uuidNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getUUID("value")
        }
        assertNull(json.getUUIDOrNull("value"))
    }

    @Test
    fun uuidMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getUUID("value")
        }
        assertNull(json.getUUIDOrNull("value"))
    }

    @Test
    fun invalidUUID() {
        val json =
            parse(
                """
                {
                    "value": "not a UUID",
                    "values": [
                        "not a UUID"
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getUUID("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getUUIDOrNull("value")
        }
        assertFailsWithCause(IllegalArgumentException::class) {
            json.getUUIDListOrNull("values")
        }
    }

    @Test
    fun defaultUUID() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultUUID = UUID.fromString("13cd6a7f-9767-4b20-b29b-5daf38dcf0c1")
        assertEquals(defaultUUID, json.getUUIDOrDefault("value", defaultUUID))
    }

    @Test
    fun uuidList() {
        val json =
            parse(
                """
                {
                    "values": [
                        "d9fdf316-fec5-44f1-9a92-66a51f439eb3",
                        "fba6120b-cd03-4f33-869a-e8e99d69ba09",
                        "d50682cd-3840-48e2-a2a2-95b4570bcd6d"
                    ]
                }
                """.trimIndent(),
            )
        val uuidList =
            listOf(
                UUID.fromString("d9fdf316-fec5-44f1-9a92-66a51f439eb3"),
                UUID.fromString("fba6120b-cd03-4f33-869a-e8e99d69ba09"),
                UUID.fromString("d50682cd-3840-48e2-a2a2-95b4570bcd6d"),
            )
        assertEquals(uuidList, json.getUUIDList("values"))
        assertFailsWith(LeoJSONException::class) { json.getUUIDList("invalid") }
        assertEquals(uuidList, json.getUUIDListOrDefault("values", listOf()))
    }

    @Test
    fun defaultUUIDList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultUUIDList = listOf(UUID.fromString("13cd6a7f-9767-4b20-b29b-5daf38dcf0c1"))
        assertEquals(defaultUUIDList, json.getUUIDListOrDefault("value", defaultUUIDList))
    }

    @Test
    fun uuidToJson() {
        val uuidString = "13cd6a7f-9767-4b20-b29b-5daf38dcf0c1"
        assertEquals(UUID.fromString(uuidString).toJson(), JsonPrimitive(uuidString))
    }
}
