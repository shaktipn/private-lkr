package com.suryadigital.leo.kedwig

/**
 * Typealias for a block that takes in [Request] and returns back [Header].
 */
typealias HeaderGenerator = (Request) -> Header

/**
 * Typealias for a block that takes in [Request] and returns back [QueryParameter]
 */
typealias QueryParameterGenerator = (Request) -> QueryParameter

/**
 * Typealias for a block that takes in a log message [String] and returns a sanitized [String].
 */
typealias LogMessageSanitizer = (String) -> String

/**
 * Defines the level of logging for a particular log message.
 */
enum class LogLevel {
    /**
     * Denotes that the log is required for troubleshooting or diagnosing issues.
     */
    DEBUG,

    /**
     * Denotes that the log is a standard log and is used to identify that something happened.
     */
    INFO,

    /**
     * Denotes that the application has not failed, but something has happened that might disturb one of the processes.
     */
    WARN,

    /**
     * Denotes that an error has occurred, and is preventing some functionalities of the application from working properly.
     */
    ERROR,

    /**
     * Denotes that the information should not be logged.
     */
    NONE,
}

/**
 * Defines configuration for logging of requests and responses.
 *
 * @property logger [Logger] implementation to send logs to.
 * @property requestMetadata [LogLevel] at which request metadata (URL, HTTP method, headers, query parameters) should be logged at.
 * @property requestBody [LogLevel] at which request body should be logged at.
 * @property responseMetadata [LogLevel] at which response metadata (URL, status code, headers) should be logged at.
 * @property responseBody [LogLevel] at which response body should be logged at.
 * @property messageSanitizer this function is called with a fully hydrated log message.
 * If you’d like to censor/sanitize certain details, such as passwords, auth tokens, etc., return a sanitized [String] here. The sanitized [String] will be logged instead.
 */
data class LogConfiguration(
    val logger: Logger = NoOpLogger(),
    val requestMetadata: LogLevel = LogLevel.NONE,
    val requestBody: LogLevel = LogLevel.NONE,
    val responseMetadata: LogLevel = LogLevel.NONE,
    val responseBody: LogLevel = LogLevel.NONE,
    val messageSanitizer: LogMessageSanitizer = { it },
)

/**
 * Defines the configuration parameter metadata for an [APIClient].
 *
 * @property baseURL URL [String] to which the [APIClient] should send the request.
 * @property connectionTimeoutMS time period in which our client should establish a connection with a target host.
 * @property socketTimeoutMS maximum time of inactivity between two data packets when waiting for server's response.
 * @property defaultHeaders [Headers] to pass to the request by default.
 * @property defaultHeaderGenerators list of function blocks that will execute, and the resulting headers will be added to the default headers of the request.
 * @property defaultQueryParameters default query parameters to pass to the request.
 * @property defaultQueryParameterGenerators list of function blocks that will execute, and the resulting query parameters will be added to the default query parameters of the request.
 * @property logConfiguration configuration for logging request and response.
 */
data class APIClientConfiguration(
    val baseURL: String = "",
    val connectionTimeoutMS: Long = 20_000,
    val socketTimeoutMS: Long = 20_000,
    val defaultHeaders: Headers = Headers(),
    val defaultHeaderGenerators: List<HeaderGenerator> = listOf(),
    val defaultQueryParameters: QueryParameters = QueryParameters(),
    val defaultQueryParameterGenerators: List<QueryParameterGenerator> = listOf(),
    val logConfiguration: LogConfiguration = LogConfiguration(),
)

/**
 * Interface defined for performing common API operations in a synchronous manner.
 */
interface SyncAPIClient {
    /**
     * Sends request to the endpoint and returns a response.
     *
     * @param request metadata defining the information required for the API call.
     *
     * @return the response sent by the API for the [request].
     */
    fun sendRequest(request: Request): Response

    /**
     * Sends request to the endpoint and returns a stream of response.
     *
     * @param request metadata defining the information required for the API call.
     *
     * @return the response sent by the API for the [request], in a streaming manner, i.e., the response is sent bit by bit, instead of sending the entire response.
     */
    fun sendRequestWithStreamingResponse(request: Request): StreamingResponse
}

/**
 * Interface defined for performing common API operations in an asynchronous manner.
 */
interface AsyncAPIClient {
    /**
     * Sends request to the endpoint and returns a response.
     *
     * @param request metadata defining the information required for the API call.
     *
     * @return the response sent by the API for the [request].
     */
    suspend fun sendRequestAsync(request: Request): Response

    /**
     * Sends request to the endpoint and returns a stream of response.
     *
     * @param request metadata defining the information required for the API call.
     *
     * @return the response sent by the API for the [request], in a streaming manner, i.e., the response is sent bit by bit, instead of sending the entire response.
     */
    suspend fun sendRequestWithStreamingResponseAsync(request: Request): StreamingResponse
}

/**
 * Interface defined for providing a common interface for both [SyncAPIClient] and [AsyncAPIClient].
 */
interface APIClient : SyncAPIClient, AsyncAPIClient
