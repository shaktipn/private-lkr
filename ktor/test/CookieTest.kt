package com.suryadigital.leo.ktor.tests

import com.suryadigital.leo.ktor.setCookie
import io.ktor.client.request.get
import io.ktor.http.CookieEncoding
import io.ktor.http.decodeURLQueryComponent
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val application =
    TestApplication {
        routing {
            get("/{type}") {
                when (val path = call.parameters["type"]) {
                    "basic" -> call.response.setCookie("Leo-WT", "web-token")
                    "withAllProperty" ->
                        call.response.setCookie(
                            "Leo-WT",
                            "web-token",
                            maxAge = 90,
                            domain = "0.0.0.0",
                            path = "/",
                            secure = true,
                            httpOnly = true,
                        )

                    "withMaxAge" -> call.response.setCookie("Leo-WT", "web-token", maxAge = 90)
                    "withDomain" -> call.response.setCookie("Leo-WT", "web-token", domain = "0.0.0.0")
                    "withPath" -> call.response.setCookie("Leo-WT", "web-token", path = "/")
                    "withSecure" -> call.response.setCookie("Leo-WT", "web-token", secure = true)
                    "withHttpOnly" -> call.response.setCookie("Leo-WT", "web-token", httpOnly = true)
                    "withIncludingEncoding" -> call.response.setCookie("Leo-WT", "web-token", includeEncoding = true)
                    "withDQUOTESEncoding" ->
                        call.response.setCookie(
                            "Leo-WT",
                            "web-token",
                            encoding = CookieEncoding.DQUOTES,
                        )

                    "withRawEncoding" -> call.response.setCookie("Leo-WT", "web-token", encoding = CookieEncoding.RAW)
                    "withRawEncodingFailed" ->
                        call.response.setCookie(
                            "Leo-WT",
                            "web-to;ken",
                            encoding = CookieEncoding.RAW,
                        )
                    else -> throw IllegalArgumentException("Unknown path $path")
                }
                call.respondText("Hello, world!")
            }
        }
    }

class CookieTest {
    @Test
    fun testBasicCookie() {
        runBlocking {
            application.client.get("/basic").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(response.headers["Set-Cookie"]?.decodeURLQueryComponent(), "Leo-WT=web-token")
            }
        }
    }

    @Test
    fun testCookieWithRawEncoding() {
        runBlocking {
            application.client.get("/withRawEncoding").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(response.headers["Set-Cookie"], "Leo-WT=web-token")
            }
        }
    }

    @Test
    fun testCookieWithRawEncodingFailed() {
        runBlocking {
            assertFailsWith<IllegalArgumentException> {
                application.client.get("/withRawEncodingFailed")
            }
        }
    }

    @Test
    fun testCookieWithDQUOTESEncoding() {
        runBlocking {
            application.client.get("/withDQUOTESEncoding").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(response.headers["Set-Cookie"], "Leo-WT=web-token")
            }
        }
    }

    @Test
    fun testCookieWithAllProperty() {
        runBlocking {
            application.client.get("/withAllProperty").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(
                    response.headers["Set-Cookie"]?.decodeURLQueryComponent(),
                    "Leo-WT=web-token; Max-Age=90; Domain=0.0.0.0; Path=/; Secure; HttpOnly",
                )
            }
        }
    }

    @Test
    fun testCookieWithMaxAge() {
        runBlocking {
            application.client.get("/withMaxAge").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(
                    response.headers["Set-Cookie"]?.decodeURLQueryComponent(),
                    "Leo-WT=web-token; Max-Age=90",
                )
            }
        }
    }

    @Test
    fun testCookieWithDomain() {
        runBlocking {
            application.client.get("/withDomain").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(
                    response.headers["Set-Cookie"]?.decodeURLQueryComponent(),
                    "Leo-WT=web-token; Domain=0.0.0.0",
                )
            }
        }
    }

    @Test
    fun testCookieWithPath() {
        runBlocking {
            application.client.get("/withPath").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(response.headers["Set-Cookie"]?.decodeURLQueryComponent(), "Leo-WT=web-token; Path=/")
            }
        }
    }

    @Test
    fun testCookieWithSecure() {
        runBlocking {
            application.client.get("/withSecure").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(response.headers["Set-Cookie"]?.decodeURLQueryComponent(), "Leo-WT=web-token; Secure")
            }
        }
    }

    @Test
    fun testCookieWithHttpOnly() {
        runBlocking {
            application.client.get("/withHttpOnly").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(
                    response.headers["Set-Cookie"]?.decodeURLQueryComponent(),
                    "Leo-WT=web-token; HttpOnly",
                )
            }
        }
    }

    @Test
    fun testCookieWithIncludingEncoding() {
        runBlocking {
            application.client.get("/withIncludingEncoding").let { response ->
                assertTrue(response.headers.contains("Set-Cookie"))
                assertEquals(
                    response.headers["Set-Cookie"]?.decodeURLQueryComponent(),
                    "Leo-WT=web-token; \$x-enc=URI_ENCODING",
                )
            }
        }
    }
}
