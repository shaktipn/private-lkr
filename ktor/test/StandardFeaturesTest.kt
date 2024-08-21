package com.suryadigital.leo.ktor.tests

import com.suryadigital.leo.ktor.ServiceWarmup
import com.suryadigital.leo.ktor.installStandardFeatures
import com.suryadigital.leo.ktor.metrics.MetricsCoroutineContext
import com.suryadigital.leo.ktor.metrics.MetricsPlugin
import com.suryadigital.leo.ktor.metrics.MetricsPlugin.Plugin.metricsKey
import com.suryadigital.leo.ktor.metrics.TraceIdCoroutineContext
import com.suryadigital.leo.ktor.metrics.metrics
import com.suryadigital.leo.ktor.metrics.traceId
import com.suryadigital.leo.rpc.LeoInvalidLLTException
import com.suryadigital.leo.rpc.LeoInvalidRequestException
import com.suryadigital.leo.rpc.LeoInvalidS2STokenException
import com.suryadigital.leo.rpc.LeoInvalidSLTException
import com.suryadigital.leo.rpc.LeoInvalidWTException
import com.suryadigital.leo.rpc.LeoServerException
import com.suryadigital.leo.rpc.LeoUnauthenticatedException
import com.suryadigital.leo.rpc.LeoUnauthorizedException
import com.suryadigital.leo.rpc.LeoUnsupportedClientException
import com.suryadigital.leo.rpc.LeoUserDisabledException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callId
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StandardFeaturesTest {
    private val testModule =
        module {
            single {
                Json { ignoreUnknownKeys = true }
            }
        }

    @BeforeTest
    fun setup() {
        startKoin {
            modules(testModule)
        }
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun testTraceIdCoroutineContextPlugin() {
        val traceId = "${UUID.randomUUID()}"
        testApplication {
            install(CallId) {
                retrieveFromHeader("X-Trace-Id")
            }
            install(TraceIdCoroutineContext)

            routing {
                get("/test") {
                    val contextTraceId = coroutineContext.traceId ?: ""
                    call.response.header("Context-Trace-Id", contextTraceId)
                }
            }
            client.get("/test") {
                headers["X-Trace-Id"] = traceId
            }.let { response ->
                assertTrue(response.headers.contains("Context-Trace-Id"))
                assertEquals(response.headers["Context-Trace-Id"], traceId)
            }
        }
    }

    @Test
    fun testMetricsPlugin() {
        val traceId = "${UUID.randomUUID()}"
        val metricsId = "testMetrics"
        testApplication {
            install(CallId) {
                retrieveFromHeader("X-Trace-Id")
            }
            install(MetricsPlugin)

            routing {
                get("/test") {
                    val metrics = call.attributes[metricsKey]
                    val contextTraceId = metrics.timed(metricsId, call.callId::toString)
                    call.response.header("Metrics-Key", metrics.timers.first().identifier)
                    call.response.header("Context-Trace-Id", contextTraceId)
                }
            }
            client.get("/test") {
                headers["X-Trace-Id"] = traceId
            }.let { response ->
                assertTrue(response.headers.contains("Metrics-Key"))
                assertEquals(response.headers["Metrics-Key"], metricsId)
                assertTrue(response.headers.contains("Context-Trace-Id"))
                assertEquals(response.headers["Context-Trace-Id"], traceId)
            }
        }
    }

    @Test
    fun testMetricsCoRoutineContextPlugin() {
        val traceId = "${UUID.randomUUID()}"
        val metricsId = "testMetrics"
        testApplication {
            install(CallId) {
                retrieveFromHeader("X-Trace-Id")
            }
            install(MetricsPlugin)
            install(MetricsCoroutineContext)

            routing {
                get("/test") {
                    val metrics = coroutineContext.metrics
                    val contextTraceId = metrics.timed(metricsId, call.callId::toString)
                    call.response.header("Metrics-Key", metrics.timers.first().identifier)
                    call.response.header("Context-Trace-Id", contextTraceId)
                }
            }
            client.get("/test") {
                headers["X-Trace-Id"] = traceId
            }.let { response ->
                assertTrue(response.headers.contains("Metrics-Key"))
                assertEquals(response.headers["Metrics-Key"], metricsId)
                assertTrue(response.headers.contains("Context-Trace-Id"))
                assertEquals(response.headers["Context-Trace-Id"], traceId)
            }
        }
    }

    @Test
    fun testStatusPagePlugin() {
        testApplication {
            install(StatusPages) {
                exception<Exception> { call, _ ->
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
            routing {
                get("/test") {
                    throw Exception("Trigger Status Page")
                }
            }
            assertEquals(HttpStatusCode.InternalServerError, client.get("/test").status)
        }
    }

    @Test
    fun testHealthCheck() {
        testApplication {
            application {
                installStandardFeatures(serviceWarmup = ServiceWarmup.Enabled { true })
            }
            val response = client.get("/healthCheck")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Healthy", response.bodyAsText())
            assertEquals("1.0.0-1.2.0", response.headers["X-Service-Version"])
        }
    }

    @Test
    fun testHealthCheckServiceUnavailable() {
        testApplication {
            application {
                installStandardFeatures(serviceWarmup = ServiceWarmup.Enabled { false })
            }

            val response = client.get("/healthCheck")
            assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
        }
    }

    @Test
    fun testHealthCheckWithWarmUpDisabled() {
        testApplication {
            application {
                installStandardFeatures(serviceWarmup = ServiceWarmup.Disabled)
            }

            val response = client.get("/healthCheck")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Healthy", response.bodyAsText())
        }
    }

    @Test
    fun testLeoInvalidRequestException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw-invalid-request") {
                        throw LeoInvalidRequestException()
                    }
                }
            }
            val response = client.get("/throw-invalid-request")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"INVALID_REQUEST\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoUnauthenticatedException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoUnauthenticatedException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"UNAUTHENTICATED\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoUserDisabledException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoUserDisabledException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"USER_DISABLED\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoInvalidSLTException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoInvalidSLTException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"INVALID_SLT\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoInvalidLLTException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoInvalidLLTException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"INVALID_LLT\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoInvalidWTException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoInvalidWTException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"INVALID_WT\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoUnauthorizedException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoUnauthorizedException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"UNAUTHORIZED\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoInvalidS2STokenException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoInvalidS2STokenException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"INVALID_S2S_TOKEN\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testLeoUnsupportedClientException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw") {
                        throw LeoUnsupportedClientException()
                    }
                }
            }
            val response = client.get("/throw")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"UNSUPPORTED_CLIENT\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testInternalServerErrorWithDetails() {
        testApplication {
            application {
                installStandardFeatures(returnExceptionDetailsInResponse = true)
                routing {
                    get("/throw-exception") {
                        throw Exception("Unexpected error")
                    }
                }
            }

            val response = client.get("/throw-exception")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"java.lang.Exception: Unexpected error\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testInternalServerError() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw-exception") {
                        throw Exception("Unexpected error")
                    }
                }
            }

            val response = client.get("/throw-exception")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }
    }

    @Test
    fun testLeoServerException() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/throw-exception") {
                        throw LeoServerException("Unexpected error", retryAfterSeconds = 10)
                    }
                }
            }

            val response = client.get("/throw-exception")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("10", response.headers["Retry-After"])
        }
    }

    @Test
    fun testExceptionDetailsInResponse() {
        testApplication {
            application {
                installStandardFeatures(returnExceptionDetailsInResponse = true)
                routing {
                    get("/detailed-exception") {
                        throw LeoServerException("Server error", retryAfterSeconds = null)
                    }
                }
            }
            val response = client.get("/detailed-exception")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals("{\"meta\":{\"status\":\"ERROR\",\"error\":{\"code\":\"com.suryadigital.leo.rpc.LeoServerException: Server error\"}}}", response.bodyAsText())
        }
    }

    @Test
    fun testExceptionDetailsInResponseDisabled() {
        testApplication {
            application {
                installStandardFeatures(returnExceptionDetailsInResponse = false)
                routing {
                    get("/simple-exception") {
                        throw LeoServerException("Server error", retryAfterSeconds = null)
                    }
                }
            }
            val response = client.get("/simple-exception")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertTrue { !response.bodyAsText().contains("Server error") }
            assertEquals("", response.bodyAsText())
        }
    }

    @Test
    fun testInvalidTraceId() {
        testApplication {
            application {
                installStandardFeatures()
                routing {
                    get("/test") {
                        val contextTraceId = coroutineContext.traceId ?: ""
                        call.response.header("Context-Trace-Id", contextTraceId)
                    }
                }
            }
            client.get("/test") {
                headers["X-Trace-Id"] = "traceId"
            }.let { response ->
                assertTrue(response.headers.contains("Context-Trace-Id"))
                assertNotNull(response.headers["Context-Trace-Id"])
            }
        }
    }
}
