package com.suryadigital.leo.ktor.tests

import com.suryadigital.leo.ktor.installCORS
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CorsTest {
    private val exampleURL = "https://example.com"
    private val exampleHost = "example.com"

    @Test
    fun testCORS() {
        testApplication {
            application {
                installCORS(hosts = listOf(exampleHost))
            }

            val response =
                client.options("/test") {
                    header(HttpHeaders.Origin, exampleURL)
                    header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Get.value)
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(exampleURL, response.headers[HttpHeaders.AccessControlAllowOrigin])
        }
    }

    @Test
    fun testCORSReject() {
        testApplication {
            application {
                installCORS(hosts = listOf(exampleHost))
            }

            val response =
                client.options("/test") {
                    header(HttpHeaders.Origin, "https://notallowed.com")
                    header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Get.value)
                }
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun testCORSWithPut() {
        testApplication {
            application {
                installCORS(hosts = listOf(exampleHost), httpMethod = listOf(HttpMethod.Put))
            }

            val response =
                client.options("/test") {
                    header(HttpHeaders.Origin, exampleURL)
                    header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Put.value)
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("PUT", response.headers[HttpHeaders.AccessControlAllowMethods])
        }
    }

    @Test
    fun testCORSWithAdditionalHeader() {
        testApplication {
            application {
                installCORS(hosts = listOf(exampleHost), httpHeader = listOf("X-Custom-Header"))
            }

            val response =
                client.options("/test") {
                    header(HttpHeaders.Origin, exampleURL)
                    header(HttpHeaders.AccessControlRequestHeaders, "X-Custom-Header")
                    header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Get.value)
                }
            assertEquals(HttpStatusCode.OK, response.status)
            response.headers[HttpHeaders.AccessControlAllowHeaders]?.contains("X-Custom-Header")?.let(::assertTrue)
        }
    }

    @Test
    fun testCORSWithCredentials() {
        testApplication {
            application {
                installCORS(hosts = listOf(exampleHost), allowCredentials = true)
            }
            val response =
                client.options("/test") {
                    header(HttpHeaders.Origin, exampleURL)
                    header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Get.value)
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("true", response.headers[HttpHeaders.AccessControlAllowCredentials])
        }
    }

    @Test
    fun testCORSWithNonSimpleContents() {
        testApplication {
            application {
                installCORS(hosts = listOf(exampleHost), allowNonSimpleContentTypes = true)
            }

            val response =
                client.options("/test") {
                    header(HttpHeaders.Origin, exampleURL)
                    header(HttpHeaders.AccessControlRequestHeaders, HttpHeaders.ContentType)
                    header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Get.value)
                }
            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue { response.headers[HttpHeaders.AccessControlAllowHeaders]?.contains(HttpHeaders.ContentType) ?: false }
        }
    }
}
