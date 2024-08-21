package com.suryadigital.leo.kedwig

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(DelicateCoroutinesApi::class)
class OkHttpAPIClientAsyncRequestTest : MockWebServerTest() {
    private val client: OkHttpAPIClient = OkHttpAPIClient(APIClientConfiguration())

    @Test
    fun connectionTimeoutTest() {
        val exception =
            assertFailsWith<TimeoutException> {
                runBlocking {
                    OkHttpAPIClient(
                        configuration =
                            APIClientConfiguration(
                                // Using port number 8080 as it is not reachable and will give Timeout Exception.
                                baseURL = "https://httpbin.org:8080",
                            ),
                    ).sendRequestAsync(
                        request {
                            method = Method.GET
                            connectionTimeoutMS = 1000
                        },
                    )
                }
            }
        exception.cause?.message?.let { assertTrue(it.contains("Connect timed out")) } ?: throw IllegalStateException("Message cannot be null for exception: $exception.")
    }

    @Test
    fun socketTimeoutTest() {
        server.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.STALL_SOCKET_AT_START),
        )
        val url = server.url("/socket-timeout").toString()
        assertFailsWith<TimeoutException> {
            runBlocking {
                client.sendRequest(
                    request {
                        method = Method.GET
                        path = url
                        headers {
                            "Content-Type" to "application/json"
                        }
                        socketTimeoutMS = 1000
                    },
                )
            }
        }
    }

    /**
     * This test asserts the cancellation of a job launched in global scope.
     * */
    @Test
    fun cancelJob() {
        val job =
            GlobalScope.launch {
                delay(1)
                client.sendRequestAsync(
                    request {
                        method = Method.GET
                        path = "${server.url("/request-cancel-test")}"
                        headers {
                            "Content-Type" to "application/json"
                        }
                    },
                )
            }
        runBlocking {
            job.cancel()
            job.join()
        }
        assertEquals(0, server.requestCount)
    }

    /**
     * This test asserts cancellation of a child job, while parent is still doing its job.
     */
    @Test
    fun cancelledChildJob() {
        server.enqueue(MockResponse().setBody("success"))
        val parent =
            GlobalScope.launch(Dispatchers.Default) {
                val child =
                    async {
                        client.sendRequestAsync(
                            request {
                                method = Method.GET
                                path = "${server.url("/cancel-child-test")}"
                                headers {
                                    "Content-Type" to "application/json"
                                }
                            },
                        )
                    }
                child.cancel() // cancelling the deferred, it will cancel the child job
                val response =
                    client.sendRequestAsync(
                        request {
                            method = Method.GET
                            path = "${server.url("/parent-job")}"
                        },
                    )
                assertEquals(response.stringBody, "success")
            }
        runBlocking {
            parent.join()
            assertEquals(server.requestCount, 1)
            assertEquals("/parent-job", server.takeRequest().path)
        }
    }

    /**
     * Test asserts if you cancel the parent job, the child job will get canceled automatically.
     */
    @Test
    fun cancelParentJob() {
        server.enqueue(MockResponse().setBody("success"))
        val parent =
            GlobalScope.launch(Dispatchers.Default) {
                withContext(Dispatchers.IO) {
                    client.sendRequestAsync(
                        request {
                            method = Method.GET
                            path = "${server.url("/cancel-parent")}"
                            headers {
                                "Content-Type" to "application/json"
                            }
                        },
                    )
                }
            }
        runBlocking {
            parent.cancel()
            parent.join()
        }
        // As the child job is canceled, response string will be null
        assertEquals(server.requestCount, 0)
    }

    @Test
    fun connectionTimeoutStreamingTest() {
        val exception =
            assertFailsWith<TimeoutException> {
                runBlocking {
                    OkHttpAPIClient(
                        configuration =
                            APIClientConfiguration(
                                // Using port number 8080 as it is not reachable and will give Timeout Exception.
                                baseURL = "https://httpbin.org:8080",
                            ),
                    ).sendRequestWithStreamingResponseAsync(
                        request {
                            method = Method.GET
                            connectionTimeoutMS = 1000
                        },
                    )
                }
            }
        exception.cause?.message?.let { assertTrue(it.contains("Connect timed out")) } ?: throw IllegalStateException("Message cannot be null for exception: $exception.")
    }

    @Test
    fun networkErrorStreamingTest() {
        assertFailsWith<NetworkException> {
            runBlocking {
                OkHttpAPIClient(
                    configuration =
                        APIClientConfiguration(
                            // Using port number 8080 as it is not reachable and will give Timeout Exception.
                            baseURL = "http://localhost:10999/",
                        ),
                ).sendRequestWithStreamingResponseAsync(
                    request {
                        path = "/fake-path"
                        method = Method.GET
                        connectionTimeoutMS = 1000
                    },
                )
            }
        }
    }

    @Test
    fun socketTimeoutStreamingTest() {
        server.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.STALL_SOCKET_AT_START),
        )
        val url = server.url("/socket-timeout").toString()
        assertFailsWith<TimeoutException> {
            runBlocking {
                client.sendRequestWithStreamingResponseAsync(
                    request {
                        method = Method.GET
                        path = url
                        headers {
                            "Content-Type" to "application/json"
                        }
                        socketTimeoutMS = 1000
                    },
                )
            }
        }
    }
}
