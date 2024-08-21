package com.suryadigital.leo.kedwig

import com.jayway.jsonpath.JsonPath
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Arrays
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OkHttpAPIClientTests : MockWebServerTest() {
    private val defaultOkHttpAPIClient: OkHttpAPIClient =
        OkHttpAPIClient(
            configuration = APIClientConfiguration(),
        )

    @Test
    fun postMethodTest() {
        val response = defaultOkHttpAPIClient.sendRequest(createRequestWithBody(Method.POST))
        assertEquals("prakash", JsonPath.read(response.stringBody, "$.name"))
        assertEquals("POST", server.takeRequest().method)
    }

    @Test
    fun putMethodTest() {
        val response = defaultOkHttpAPIClient.sendRequest(createRequestWithBody(Method.PUT))
        assertEquals("prakash", JsonPath.read(response.stringBody, "$.name"))
        assertEquals("PUT", server.takeRequest().method)
    }

    @Test
    fun deleteMethodTest() {
        defaultOkHttpAPIClient.sendRequest(createRequest(Method.DELETE))
        assertEquals("DELETE", server.takeRequest().method)
    }

    @Test
    fun headMethodTest() {
        defaultOkHttpAPIClient.sendRequest(createRequest(Method.HEAD))
        assertEquals("HEAD", server.takeRequest().method)
    }

    @Test
    fun optionsMethodTest() {
        defaultOkHttpAPIClient.sendRequest(createRequest(Method.OPTIONS))
        assertEquals("OPTIONS", server.takeRequest().method)
    }

    @Test
    fun connectMethodTest() {
        defaultOkHttpAPIClient.sendRequest(createRequest(Method.CONNECT))
        assertEquals("CONNECT", server.takeRequest().method)
    }

    @Test
    fun traceMethodTest() {
        defaultOkHttpAPIClient.sendRequest(createRequest(Method.TRACE))
        assertEquals("TRACE", server.takeRequest().method)
    }

    @Test
    fun getMethodTest() {
        val response = defaultOkHttpAPIClient.sendRequest(createRequest(Method.GET))
        assertEquals("success", JsonPath.read(response.stringBody, "$"))
    }

    @Test
    fun requestWithStreamingResponseTest() {
        val response = defaultOkHttpAPIClient.sendRequestWithStreamingResponse(createRequest(Method.GET))
        assertEquals("success", JsonPath.read(response.stringBody, "$"))
    }

    @Test
    fun requestWith_HEAD_MethodReponseMessageBodyTest() {
        val response = defaultOkHttpAPIClient.sendRequest(createRequest(Method.HEAD))
        val recordedRequest = server.takeRequest()
        assertEquals("HEAD", recordedRequest.method)
        assertEquals(0, recordedRequest.body.size)
        // HEAD Must Not return response message body from server
        assertEquals("", response.stringBody)
    }

    @Test
    fun requestWith_PUT_MethodReponseMessageBodyTest() {
        val response = defaultOkHttpAPIClient.sendRequest(createRequestWithBody(Method.PUT))
        assertEquals("PUT", server.takeRequest().method)
        // PUT doesn't follow the same as HEAD from server, and will return the response message body
        assertEquals("prakash", JsonPath.read(response.stringBody, "$.name"))
    }

    @Test
    fun requestWith_TRACE_MethodReponseMessageBodyTest() {
        val response = defaultOkHttpAPIClient.sendRequest(createRequest(Method.TRACE))
        val recordedRequest = server.takeRequest()
        assertEquals("TRACE", recordedRequest.method)
        assertEquals(0, recordedRequest.body.size)
        // TRACE doesn't follow the same as HEAD from server, and can return the response message body
        assertEquals("success", response.stringBody)
    }

    @Test
    fun connectionTimeoutTest() {
        val exception =
            assertFailsWith<TimeoutException> {
                OkHttpAPIClient(
                    configuration =
                        APIClientConfiguration(
                            // Using port number 8080 as it is not reachable and will give Timeout Exception
                            baseURL = "https://www.httpbin.org:8080",
                        ),
                ).sendRequest(
                    request {
                        path = "/anything"
                        method = Method.GET
                        connectionTimeoutMS = 1000
                    },
                )
            }
        assertEquals(exception.cause?.message, "Connect timed out")
    }

    /*
     * Test: When the client has sufficient time to connect to host.
     * */
    @Test
    fun connectionTimeoutTestTwo() {
        server.enqueue(MockResponse().setBody("success!"))
        val url = server.url("connect-success").toString()
        val response =
            defaultOkHttpAPIClient.sendRequest(
                request {
                    method = Method.GET
                    path = url
                    connectionTimeoutMS = 10000
                },
            )
        assertEquals("success!", JsonPath.read(response.stringBody, "$"))
    }

    @Test
    fun socketTimeoutTest() {
        server.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.STALL_SOCKET_AT_START),
        )
        val url = server.url("/socket-timeout").toString()
        assertFailsWith<TimeoutException> {
            defaultOkHttpAPIClient.sendRequest(
                request {
                    method = Method.GET
                    path = url
                    socketTimeoutMS = 1000
                },
            )
        }
    }

    @Test
    fun doubleBodyCallTest() {
        val bodyContent = "This is an expected string"
        server.enqueue(MockResponse().setBody(bodyContent))
        val url = server.url("/double-body-test").toString()
        val response =
            defaultOkHttpAPIClient.sendRequest(
                request {
                    method = Method.POST
                    path = url
                    body("This is not an expected string")
                    body(bodyContent) // will take last body called
                    headers {
                        "Content-Type" to "application/json"
                    }
                },
            )
        assertEquals(bodyContent, JsonPath.read(response.stringBody, "$"))
    }

    @Test
    fun testRequestCookie() {
        server.enqueue(MockResponse())
        val url = server.url("cookies").toString()
        defaultOkHttpAPIClient.sendRequest(
            request {
                method = Method.GET
                path = url
                cookies {
                    "foo" to "bar"
                    "blah" to "baz"
                }
            },
        )
        val recordedRequest = server.takeRequest()
        val cookieList =
            recordedRequest.headers.filter { it.first == "Cookie" }.let {
                mutableListOf<String>().apply {
                    it.forEach { pair ->
                        add(pair.second)
                    }
                }
            }
        assertEquals("$cookieList", "[foo=bar, blah=baz]")
    }

    @Test
    fun testResponseCookie() {
        val mockResponse =
            MockResponse().apply {
                addHeader("Set-Cookie: foo=bar")
                addHeader("Set-Cookie: blah=baz")
            }
        server.enqueue(mockResponse)
        val url = server.url("cookies").toString()
        val response =
            defaultOkHttpAPIClient.sendRequest(
                request {
                    method = Method.GET
                    path = url
                    cookies {
                        "foo" to "bar"
                        "blah" to "baz"
                    }
                },
            )
        val cookieList = response.cookies.map { "${it.name}=${it.value}" }
        assertEquals("$cookieList", "[foo=bar, blah=baz]")
    }

    @Test
    fun testDefaultHeaderGenerator() {
        val header = Header("Foo", "Bar")
        val fooGenerator: HeaderGenerator = { header }
        server.enqueue(MockResponse().addHeader(header.name, header.value))
        val url = server.url("/default-header-generator").toString()
        val client =
            OkHttpAPIClient(
                configuration =
                    APIClientConfiguration(
                        baseURL = url,
                        defaultHeaderGenerators =
                            listOf(
                                fooGenerator,
                            ),
                    ),
            )
        client.sendRequest(
            request {
                method = Method.GET
            },
        )
        val recordedRequest = server.takeRequest()
        assertEquals("Bar", recordedRequest.getHeader("Foo"))
    }

    @Test
    fun testDefaultQueryParameterGenerator() {
        server.enqueue(MockResponse())
        val url = server.url("/test-default-query-generator").toString()
        val fooGenerator: QueryParameterGenerator = { QueryParameter("Foo", "Bar") }
        val client =
            OkHttpAPIClient(
                configuration =
                    APIClientConfiguration(
                        baseURL = url,
                        defaultQueryParameterGenerators =
                            listOf(
                                fooGenerator,
                            ),
                    ),
            )
        client.sendRequest(
            request {
                method = Method.GET
            },
        )
        val recordedPath = server.takeRequest().path!!
        val queries = getQueryList(recordedPath)
        val queryValue = queries[0].split("=")[1]
        assertEquals("Bar", queryValue)
    }

    /**
     * This test asserts how APIClient doesn't load complete file in the heap memory.
     * We have 10MB file.
     * Mock web server will take 10Mb to store the file in the body.
     * 4-10Mb for mock web server thread creation and other processing.
     * Request body data will be loaded in memory 8kb(default-buffer size) to 10MB(Max file size).
     * Kotlin code coverage will take around 5-6MB of heap space.
     * If the request body is loaded completely in memory, it will take more than 10MB.
     * So a total of 30-40MB min heap space is required, but we are able to do in 20MB (actually it consumes 20MB for call operation) which proves it doesn't load all 10MB in memory.
     * */
    @Test
    fun requestBodyHeapSizePassWithInputStreamTest() {
        server.enqueue(MockResponse().setBody("success"))
        val inputStream = createFileInputStream()
        val url = server.url("/upload-large-body").toString()
        val response =
            defaultOkHttpAPIClient.sendRequest(
                request {
                    method = Method.POST
                    bodyStream(inputStream)
                    path = url
                },
            )
        assertEquals(response.stringBody, "success")
    }

    /**
     * We are loading a string of 10Mb in memory, Mock web server will take 10Mb to store the file in body and 4-10Mb for mock web server thread creation
     * and other processing, and request body data will be loaded in memory 8kb(default-buffer size) to 10MB(Max file size).
     * If the request body is loaded completely in memory, it will take more than 10Mb, so a total of 25-35Mb min heap sizes is required.
     * As the heap size is fixed to 20Mb, it will throw [OutOfMemoryError].
     * */
    @Test
    fun requestBodyHeapSizeFailsWithStringTest() {
        assertFailsWith<OutOfMemoryError> {
            server.enqueue(MockResponse().setBody("success"))
            val inputString = createFileInputStream().toStringBody() // loads everything in memory
            val url = server.url("/upload-large-body").toString()
            val response =
                defaultOkHttpAPIClient.sendRequest(
                    request {
                        method = Method.POST
                        body(inputString)
                        path = url
                    },
                )
            assertEquals(response.stringBody, "success")
        }
    }

    @Test
    fun methodWithValidBodyCheckTests() {
        assertFailsWith<RequestBuilderException> {
            // Try `GET` with body
            defaultOkHttpAPIClient.sendRequest(createRequestWithBody(Method.GET))
        }

        assertFailsWith<RequestBuilderException> {
            // Try `HEAD` with body
            defaultOkHttpAPIClient.sendRequest(createRequestWithBody(Method.HEAD))
        }

        assertFailsWith<RequestBuilderException> {
            // Try `POST` without body
            defaultOkHttpAPIClient.sendRequest(createRequest(Method.POST))
        }

        assertFailsWith<RequestBuilderException> {
            // Try `PUT` without body
            defaultOkHttpAPIClient.sendRequest(createRequest(Method.PUT))
        }
    }

    @Test
    fun testRequestMetadataLogging() {
        val logger = RecordingLogger()
        val client =
            OkHttpAPIClient(
                configuration =
                    APIClientConfiguration(
                        logConfiguration =
                            LogConfiguration(
                                logger = logger,
                                requestMetadata = LogLevel.DEBUG,
                            ),
                    ),
            )
        server.enqueue(MockResponse().setBody("success"))
        client.sendRequest(
            request {
                method = Method.GET
                path = server.url("/foo").toString()
                headers {
                    "foo" to "bar"
                }
                queryParameters {
                    "blah" to "baz"
                }
            },
        )
        assertEquals(1, logger.recordedMessages.size)
        assertEquals(LogLevel.DEBUG, logger.recordedMessages[0].first)
        assertNull(logger.recordedMessages[0].second)
        assertNotNull(logger.recordedMessages[0].third)
    }

    @Test
    fun testRequestBodyLogging() {
        val logger = RecordingLogger()
        val client =
            OkHttpAPIClient(
                configuration =
                    APIClientConfiguration(
                        logConfiguration =
                            LogConfiguration(
                                logger = logger,
                                requestMetadata = LogLevel.INFO,
                                requestBody = LogLevel.DEBUG,
                            ),
                    ),
            )
        server.enqueue(MockResponse().setBody("success"))
        client.sendRequest(
            request {
                method = Method.POST
                path = server.url("/foo").toString()
                headers {
                    "foo" to "bar"
                }
                queryParameters {
                    "blah" to "baz"
                }
                body("This is a body")
            },
        )
        assertEquals(2, logger.recordedMessages.size)
        assertEquals(LogLevel.INFO, logger.recordedMessages[0].first)
        assertNull(logger.recordedMessages[0].second)
        assertNotNull(logger.recordedMessages[0].third)
        logger.recordedMessages[0].third?.let { assertTrue(it.contains("Headers")) } ?: throw IllegalStateException("The message cannot be null. Recorded messages are: ${logger.recordedMessages}")
        assertEquals(LogLevel.DEBUG, logger.recordedMessages[1].first)
        assertNull(logger.recordedMessages[1].second)
        assertNotNull(logger.recordedMessages[1].third)
        logger.recordedMessages[1].third?.let { assertTrue(it.contains("This is a body")) } ?: throw IllegalStateException("The message cannot be null. Recorded messages are: ${logger.recordedMessages}")
    }

    @Test
    fun testResponseBodyLogging() {
        val logger = RecordingLogger()
        val client =
            OkHttpAPIClient(
                configuration =
                    APIClientConfiguration(
                        logConfiguration =
                            LogConfiguration(
                                logger = logger,
                                responseMetadata = LogLevel.INFO,
                                responseBody = LogLevel.DEBUG,
                            ),
                    ),
            )
        server.enqueue(MockResponse().setBody("success"))
        val response =
            client.sendRequest(
                request {
                    method = Method.GET
                    path = server.url("/foo").toString()
                    headers {
                        "foo" to "bar"
                    }
                    queryParameters {
                        "blah" to "baz"
                    }
                },
            )
        assertEquals(200, response.statusCode)
        assertEquals("success", response.stringBody)
        assertEquals(2, logger.recordedMessages.size)
        assertEquals(LogLevel.INFO, logger.recordedMessages[0].first)
        assertNull(logger.recordedMessages[0].second)
        assertNotNull(logger.recordedMessages[0].third)
        logger.recordedMessages[0].third?.let { assertTrue(it.contains("Headers")) } ?: throw IllegalStateException("The message cannot be null. Recorded messages are: ${logger.recordedMessages}")
        assertEquals(LogLevel.DEBUG, logger.recordedMessages[1].first)
        assertNull(logger.recordedMessages[1].second)
        assertNotNull(logger.recordedMessages[1].third)
        logger.recordedMessages[1].third?.let { assertTrue(it.contains("success")) } ?: throw IllegalStateException("The message cannot be null. Recorded messages are: ${logger.recordedMessages}")
    }

    @Test
    fun testResponseLoggingForWarnAndError() {
        val logger = RecordingLogger()
        val client =
            OkHttpAPIClient(
                configuration =
                    APIClientConfiguration(
                        logConfiguration =
                            LogConfiguration(
                                logger = logger,
                                responseMetadata = LogLevel.WARN,
                                responseBody = LogLevel.ERROR,
                            ),
                    ),
            )
        server.enqueue(MockResponse().setBody("success"))
        val response =
            client.sendRequest(
                request {
                    method = Method.GET
                    path = server.url("/foo").toString()
                    headers {
                        "foo" to "bar"
                    }
                    queryParameters {
                        "blah" to "baz"
                    }
                },
            )
        assertEquals(200, response.statusCode)
        assertEquals("success", response.stringBody)
        assertEquals(2, logger.recordedMessages.size)
        assertEquals(LogLevel.WARN, logger.recordedMessages[0].first)
        assertNull(logger.recordedMessages[0].second)
        assertNotNull(logger.recordedMessages[0].third)
        logger.recordedMessages[0].third?.let { assertTrue(it.contains("Headers")) } ?: throw IllegalStateException("The message cannot be null. Recorded messages are: ${logger.recordedMessages}")
        assertEquals(LogLevel.ERROR, logger.recordedMessages[1].first)
        assertNull(logger.recordedMessages[1].second)
        assertNotNull(logger.recordedMessages[1].third)
        logger.recordedMessages[1].third?.let { assertTrue(it.contains("success")) } ?: throw IllegalStateException("The message cannot be null. Recorded messages are: ${logger.recordedMessages}")
    }
}

private fun createFileInputStream(): FileInputStream {
    val file = File.createTempFile("test", null).apply { deleteOnExit() }
    CharArray(500_000).let {
        Arrays.fill(it, 'x')
        it.joinToString { "" }
    }.apply {
        repeat(10) { file.appendText(this) }
    }
    return file.inputStream()
}

internal fun InputStream.toStringBody(): String {
    return readBytes().toString(Charsets.UTF_8)
}

private class RecordingLogger : Logger {
    private val messages = mutableListOf<Triple<LogLevel, Throwable?, String?>>()

    val recordedMessages: List<Triple<LogLevel, Throwable?, String?>> = messages

    override fun debug(
        throwable: Throwable?,
        message: () -> String,
    ) {
        messages.add(Triple(LogLevel.DEBUG, throwable, message()))
    }

    override fun debug(throwable: Throwable) {
        messages.add(Triple(LogLevel.DEBUG, throwable, null))
    }

    override fun info(
        throwable: Throwable?,
        message: () -> String,
    ) {
        messages.add(Triple(LogLevel.INFO, throwable, message()))
    }

    override fun info(throwable: Throwable) {
        messages.add(Triple(LogLevel.INFO, throwable, null))
    }

    override fun warn(
        throwable: Throwable?,
        message: () -> String,
    ) {
        messages.add(Triple(LogLevel.WARN, throwable, message()))
    }

    override fun warn(throwable: Throwable) {
        messages.add(Triple(LogLevel.WARN, throwable, null))
    }

    override fun error(
        throwable: Throwable?,
        message: () -> String,
    ) {
        messages.add(Triple(LogLevel.ERROR, throwable, message()))
    }

    override fun error(throwable: Throwable) {
        messages.add(Triple(LogLevel.ERROR, throwable, null))
    }
}
