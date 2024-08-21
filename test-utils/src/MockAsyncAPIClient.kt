package com.suryadigital.leo.testUtils

import com.suryadigital.leo.kedwig.AsyncAPIClient
import com.suryadigital.leo.kedwig.Request
import com.suryadigital.leo.kedwig.Response
import com.suryadigital.leo.kedwig.StreamingResponse

/**
 * Implementation for [AsyncAPIClient] to mock its functionality.
 */
class MockAsyncAPIClient : AsyncAPIClient {
    private var asyncResponseMap = mutableMapOf<String, ResultGenerator<Response>?>()
    private var asyncStreamResponseMap = mutableMapOf<String, ResultGenerator<StreamingResponse>?>()

    /**
     * Set the mock functionality for [AsyncAPIClient.sendRequestAsync].
     */
    fun setAsyncResponse(
        response: ResultGenerator<Response>,
        path: String = DEFAULT_PATH,
    ) {
        synchronized(this) {
            asyncResponseMap[path] = response
        }
    }

    /**
     * Set the mock functionality for [AsyncAPIClient.sendRequestWithStreamingResponseAsync].
     */
    fun setAsyncStreamResponse(
        response: ResultGenerator<StreamingResponse>,
        path: String = DEFAULT_PATH,
    ) {
        synchronized(this) {
            asyncStreamResponseMap[path] = response
        }
    }

    override suspend fun sendRequestAsync(request: Request): Response {
        val path = if (asyncResponseMap.containsKey(request.path)) request.path else DEFAULT_PATH
        return when (val asyncResponse = asyncResponseMap[path]) {
            is ResultGenerator.Response -> asyncResponse.value
            is ResultGenerator.Exception -> throw asyncResponse.value
            null -> throw IllegalStateException("asyncResponse is not set on MockAsyncAPIClient")
        }
    }

    override suspend fun sendRequestWithStreamingResponseAsync(request: Request): StreamingResponse {
        val path = if (asyncStreamResponseMap.containsKey(request.path)) request.path else DEFAULT_PATH
        return when (val asyncStreamResponse = asyncStreamResponseMap[path]) {
            is ResultGenerator.Response -> asyncStreamResponse.value
            is ResultGenerator.Exception -> throw asyncStreamResponse.value
            null -> throw IllegalStateException("asyncResponse is not set on MockAsyncAPIClient")
        }
    }
}

private const val DEFAULT_PATH = "/"
