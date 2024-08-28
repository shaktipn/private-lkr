package com.suryadigital.leo.testUtils

import com.suryadigital.leo.kedwig.APIClientConfiguration
import com.suryadigital.leo.kedwig.Header
import com.suryadigital.leo.kedwig.LogConfiguration
import com.suryadigital.leo.kedwig.LogLevel
import com.suryadigital.leo.kedwig.NetworkException
import com.suryadigital.leo.kedwig.OkHttpAPIClient
import com.suryadigital.leo.kedwig.headers
import com.suryadigital.leo.kedwig.jvm.SLF4JLogger
import com.suryadigital.leo.rpc.ClientToServerAuthenticationProvider
import com.suryadigital.leo.rpc.LeoRPCRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import org.junit.BeforeClass
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class ExecuteClientRPCAndValidateInternalServerErrorTest {
    companion object {
        private val leoRPCRequest = Request(1L)
        private const val RPC_PATH = "mock/url"
        private const val SERVER_PORT = 30004

        @JvmStatic
        @BeforeClass
        fun startServer() {
            TestApplicationServer(Application::module)
        }
    }

    @Test
    fun testPositiveForSLTAuthenticationProvider() {
        runBlocking {
            executeRPCAndValidateInternalServerError(
                request = leoRPCRequest,
                expectedException = expectedException,
                authenticationProvider = FakeClientToServerAuthenticationProviderSLT(),
                json = Json,
                client = client(),
                routePath = RPC_PATH,
            )
        }
    }

    @Test
    fun testPositiveForWTAuthenticationProvider() {
        runBlocking {
            executeRPCAndValidateInternalServerError(
                request = leoRPCRequest,
                expectedException = expectedException,
                authenticationProvider = FakeClientToServerAuthenticationProviderWT(),
                json = Json,
                client = client(),
                routePath = RPC_PATH,
            )
        }
    }

    @Test
    fun testPositiveForNoAuthenticationProvider() {
        runBlocking {
            executeRPCAndValidateInternalServerError(
                request = leoRPCRequest,
                expectedException = expectedException,
                authenticationProvider = null,
                json = Json,
                client = client(),
                routePath = RPC_PATH,
            )
        }
    }

    @Test
    fun testFailsWithMismatchedException() {
        assertFailsWith<AssertionError> {
            runBlocking {
                executeRPCAndValidateInternalServerError(
                    request = leoRPCRequest,
                    expectedException = IllegalStateException("wrong exception."),
                    authenticationProvider = null,
                    json = Json,
                    client = client(),
                    routePath = RPC_PATH,
                )
            }
        }
    }

    @Test
    fun testFailsForWrongPath() {
        assertFailsWith<SerializationException> {
            runBlocking {
                executeRPCAndValidateInternalServerError(
                    request = leoRPCRequest,
                    expectedException = expectedException,
                    authenticationProvider = null,
                    json = Json,
                    client = client(),
                    routePath = "mock/",
                )
            }
        }
    }

    @Test
    fun testFailsWithWrongServerPort() {
        assertFailsWith<NetworkException> {
            runBlocking {
                executeRPCAndValidateInternalServerError(
                    request = leoRPCRequest,
                    expectedException = expectedException,
                    authenticationProvider = null,
                    json = Json,
                    client = client(serverPort = 30005),
                    routePath = RPC_PATH,
                )
            }
        }
    }

    private fun client(serverPort: Int = SERVER_PORT) =
        OkHttpAPIClient(
            APIClientConfiguration(
                baseURL = "http://0.0.0.0:$serverPort/",
                connectionTimeoutMS = 20_000,
                socketTimeoutMS = 20_000,
                logConfiguration =
                    LogConfiguration(
                        logger = SLF4JLogger(LoggerFactory.getLogger(ExecuteClientRPCAndValidateInternalServerErrorTest::class.java)),
                        requestMetadata = LogLevel.DEBUG,
                        requestBody = LogLevel.DEBUG,
                        responseMetadata = LogLevel.DEBUG,
                        responseBody = LogLevel.DEBUG,
                    ),
                defaultHeaders = headers { listOf<Header>() },
            ),
        )

    private data class Request(val id: Long) : LeoRPCRequest {
        override fun toJson(): JsonObject =
            buildJsonObject {
                "id" to id
            }
    }

    private class FakeClientToServerAuthenticationProviderSLT : ClientToServerAuthenticationProvider.SLT() {
        override suspend fun getSLT(): String {
            return "FAKE TOKEN"
        }

        override suspend fun refreshSLT() {
        }

        override suspend fun setSLT(value: String) {
        }
    }

    private class FakeClientToServerAuthenticationProviderWT(private val wt: String = "FAKE TOKEN") :
        ClientToServerAuthenticationProvider.WT() {
        override suspend fun getWT(): String {
            return wt
        }
    }

    private class TestApplicationServer(private val testUnit: Application.() -> Unit) {
        private val server: NettyApplicationEngine

        init {
            val env =
                applicationEngineEnvironment {
                    module {
                        testUnit()
                    }
                    connector {
                        host = "0.0.0.0"
                        port = SERVER_PORT
                    }
                }
            server = embeddedServer(Netty, env).start(false)
        }
    }
}

private fun Application.module() {
    install(CallLogging) {
        format { call ->
            val statusCode = call.response.status()?.value ?: 0
            val method = call.request.httpMethod.value
            val path = call.request.path()
            "Response Sent statusCode=$statusCode method=$method path=$path "
        }
    }
    routing {
        route("mock") {
            post("url") {
                parseToJsonElement(call.receiveText()).jsonObject

                val body =
                    buildJsonObject {
                        put(
                            "meta",
                            buildJsonObject {
                                put("status", "ERROR")
                                put(
                                    "error",
                                    buildJsonObject {
                                        put("code", "$expectedException")
                                    },
                                )
                            },
                        )
                    }
                val responseText = Json.encodeToString(JsonObject.serializer(), body)
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = responseText,
                )
            }
        }
    }
}

private val expectedException = IllegalStateException("Invalid state.")
