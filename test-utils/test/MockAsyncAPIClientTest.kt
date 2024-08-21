package com.suryadigital.leo.testUtils

import com.suryadigital.leo.kedwig.Method
import com.suryadigital.leo.kedwig.Response
import com.suryadigital.leo.kedwig.StreamingResponse
import com.suryadigital.leo.kedwig.headers
import com.suryadigital.leo.kedwig.request
import okhttp3.HttpUrl.Companion.toHttpUrl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class MockAsyncAPIClientTest {
    companion object {
        private const val TEST_API_PATH = "/api-path/url"
        private const val TEST_RESPONSE_BODY = "thisIsTheResponseBody"
        private const val EXCEPTION_MESSAGE = "ThisIsASampleExceptionMessage"
        private val testURl = "https://www.google.com/".toHttpUrl()
        private val testHeaders =
            headers {
                header("Accept", "application/json")
                header("Content-Type", "application/json")
            }
        private val emptyTestRequest =
            request {
                method = Method.GET
            }
    }

    private val mockAsyncAPIClient = MockAsyncAPIClient()

    private val resultGeneratorResponse =
        ResultGenerator.Response(
            Response(
                statusCode = 500,
                headers = testHeaders,
                body = TEST_RESPONSE_BODY.toByteArray(),
                url = testURl,
            ),
        )

    private val resultGeneratorStreamingResponse =
        ResultGenerator.Response(
            StreamingResponse(
                statusCode = 500,
                headers = testHeaders,
                body = TEST_RESPONSE_BODY.byteInputStream(),
                url = testURl,
            ),
        )

    @Test
    fun testSetAsyncResponseWithDefaultPath() {
        mockAsyncAPIClient.setAsyncResponse(response = resultGeneratorResponse)
        runWithKtorMetricsContext {
            val response =
                mockAsyncAPIClient.sendRequestAsync(
                    request = emptyTestRequest,
                )
            assertEquals(TEST_RESPONSE_BODY, response.body.toString(Charsets.UTF_8))
        }
    }

    @Test
    fun testSetAsyncResponseWithCustomPath() {
        mockAsyncAPIClient.setAsyncResponse(
            response = resultGeneratorResponse,
            path = TEST_API_PATH,
        )
        runWithKtorMetricsContext {
            val response =
                mockAsyncAPIClient.sendRequestAsync(
                    request =
                        request {
                            method = Method.GET
                            path = TEST_API_PATH
                        },
                )
            assertEquals(TEST_RESPONSE_BODY, response.body.toString(Charsets.UTF_8))
        }
    }

    @Test
    fun testSetAsyncStreamResponseWithDefaultPath() {
        mockAsyncAPIClient.setAsyncStreamResponse(response = resultGeneratorStreamingResponse)
        runWithKtorMetricsContext {
            val response =
                mockAsyncAPIClient.sendRequestWithStreamingResponseAsync(
                    request = emptyTestRequest,
                )
            assertEquals(TEST_RESPONSE_BODY, response.body.readAllBytes().toString(Charsets.UTF_8))
        }
    }

    @Test
    fun testSetAsyncStreamResponseWithCustomPath() {
        mockAsyncAPIClient.setAsyncStreamResponse(
            response = resultGeneratorStreamingResponse,
            path = TEST_API_PATH,
        )
        runWithKtorMetricsContext {
            val response =
                mockAsyncAPIClient.sendRequestWithStreamingResponseAsync(
                    request =
                        request {
                            method = Method.GET
                            path = TEST_API_PATH
                        },
                )
            assertEquals(TEST_RESPONSE_BODY, response.body.readAllBytes().toString(Charsets.UTF_8))
        }
    }

    @Test
    fun testExceptionAsyncResponse() {
        mockAsyncAPIClient.setAsyncResponse(
            ResultGenerator.Exception(
                value = IllegalArgumentException(EXCEPTION_MESSAGE),
            ),
        )
        runWithKtorMetricsContext {
            val exception =
                assertFailsWith<IllegalArgumentException> {
                    mockAsyncAPIClient.sendRequestAsync(
                        request = emptyTestRequest,
                    )
                }
            assertEquals(EXCEPTION_MESSAGE, exception.message)
        }
    }

    @Test
    fun testExceptionAsyncStreamResponse() {
        mockAsyncAPIClient.setAsyncStreamResponse(
            ResultGenerator.Exception(
                value = IllegalArgumentException(EXCEPTION_MESSAGE),
            ),
        )
        runWithKtorMetricsContext {
            val exception =
                assertFailsWith<IllegalArgumentException> {
                    mockAsyncAPIClient.sendRequestWithStreamingResponseAsync(
                        request = emptyTestRequest,
                    )
                }
            assertEquals(EXCEPTION_MESSAGE, exception.message)
        }
    }

    @Test
    fun testFailureWhenResponseIsNotSet() {
        assertFailsWith<IllegalStateException> {
            runWithKtorMetricsContext {
                mockAsyncAPIClient.sendRequestAsync(
                    request = emptyTestRequest,
                )
            }
        }
    }

    @Test
    fun testFailureWhenStreamingResponseIsNotSet() {
        assertFailsWith<IllegalStateException> {
            runWithKtorMetricsContext {
                mockAsyncAPIClient.sendRequestWithStreamingResponseAsync(
                    request = emptyTestRequest,
                )
            }
        }
    }
}
