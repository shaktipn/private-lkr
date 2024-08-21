package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonPrimitive
import java.net.MalformedURLException
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class URLTest : AbstractTest() {
    @Test
    fun url() {
        val json =
            parse(
                """
                {
                    "value": "https://google.com"
                }
                """.trimIndent(),
            )
        val testURL = URL("https://google.com")
        assertEquals(testURL, json.getURL("value"))
        assertNotNull(json.getURL("value"))
        assertEquals(testURL, json.getURLOrNull("value")!!)
        assertEquals(testURL, json.getURLOrDefault("value", URL("https://invalid.com")))
    }

    @Test
    fun urlNull() {
        val json =
            parse(
                """
                {
                    "value": null
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getURL("value")
        }
        assertNull(json.getURLOrNull("value"))
    }

    @Test
    fun urlMissing() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        assertFailsWith(LeoJSONException::class) {
            json.getURL("value")
        }
        assertNull(json.getURLOrNull("value"))
    }

    @Test
    fun invalidURL() {
        val json =
            parse(
                """
                {
                    "value": "not a URL",
                    "values": [
                        "https/google.com"
                    ]
                }
                """.trimIndent(),
            )
        assertFailsWithCause(MalformedURLException::class) {
            json.getURL("value")
        }
        assertFailsWithCause(MalformedURLException::class) {
            json.getURLOrNull("value")
        }
        assertFailsWithCause(MalformedURLException::class) {
            json.getURLListOrNull("values")
        }
    }

    @Test
    fun defaultURL() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultUrl = URL("https://google.com")
        assertEquals(defaultUrl, json.getURLOrDefault("value", defaultUrl))
    }

    @Test
    fun urlList() {
        val json =
            parse(
                """
                {
                    "values": [
                        "https://google.com",
                        "https://bing.com",
                        "https://yahoo.in"
                    ]
                }
                """.trimIndent(),
            )
        val urlValues =
            listOf(
                URL("https://google.com"),
                URL("https://bing.com"),
                URL("https://yahoo.in"),
            )
        assertEquals(urlValues, json.getURLList("values"))
        assertFailsWith(LeoJSONException::class) { json.getURLList("invalid") }
        assertEquals(urlValues, json.getURLListOrDefault("values", listOf()))
    }

    @Test
    fun defaultURLList() {
        val json =
            parse(
                """
                {
                }
                """.trimIndent(),
            )
        val defaultURLList = listOf(URL("https://google.com"))
        assertEquals(defaultURLList, json.getURLListOrDefault("value", defaultURLList))
    }

    @Test
    fun urlToJson() {
        val urlString = "https://google.com"
        assertEquals(URL(urlString).toJson(), JsonPrimitive(urlString))
    }
}
