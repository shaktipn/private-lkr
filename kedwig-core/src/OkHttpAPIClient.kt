package com.suryadigital.leo.kedwig

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * An [OkHttp](https://square.github.io/okhttp/) implementation of the [APIClient].
 *
 * @property configuration [APIClientConfiguration] for the instance of the current implementation.
 */
class OkHttpAPIClient(private val configuration: APIClientConfiguration) : APIClient {
    private val defaultOkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(
                Duration.ofMillis(
                    configuration.connectionTimeoutMS,
                ),
            ).readTimeout(
                Duration.ofMillis(
                    configuration.socketTimeoutMS,
                ),
            )
            .build()

    @Throws(TimeoutException::class, NetworkException::class)
    override fun sendRequest(request: Request): Response {
        val (code, headers, responseBody, url) = sendRequestHelper(request)
        val responseBodyBytes = responseBody.bytes()
        logResponse(url, code, headers, responseBodyBytes)
        return Response(code, headers, responseBodyBytes, url)
    }

    @Throws(TimeoutException::class, NetworkException::class)
    override fun sendRequestWithStreamingResponse(request: Request): StreamingResponse {
        val (code, headers, responseBody, url) = sendRequestHelper(request)
        return StreamingResponse(code, headers, responseBody.byteStream(), url)
    }

    @Throws(TimeoutException::class, NetworkException::class)
    private fun sendRequestHelper(request: Request): NTuple4<Int, Headers, ResponseBody, HttpUrl> {
        val okHttpRequest = request.buildRequest()
        try {
            val okHttpClient = getClient(request)

            logRequest(request, okHttpRequest.url)

            val okHttpResponse = okHttpClient.newCall(okHttpRequest).execute()

            val responseHeaders =
                Headers(
                    okHttpResponse.headers.map {
                        Header(it.first, it.second)
                    },
                )

            // Response body should not be null, per documentation, since we are using `execute` to make the request
            val responseBody =
                okHttpResponse.body ?: throw IllegalStateException("Got null response body from OkHttp")

            return NTuple4(okHttpResponse.code, responseHeaders, responseBody, okHttpRequest.url)
        } catch (e: SocketTimeoutException) {
            log(
                configuration.logConfiguration.logger,
                configuration.logConfiguration.responseMetadata,
                configuration.logConfiguration.messageSanitizer,
                e,
            ) {
                "Request timed out: ${okHttpRequest.url}"
            }
            throw TimeoutException(e)
        } catch (e: IOException) {
            log(
                configuration.logConfiguration.logger,
                configuration.logConfiguration.responseMetadata,
                configuration.logConfiguration.messageSanitizer,
                e,
            ) {
                "Request encountered network error: ${okHttpRequest.url}"
            }
            throw NetworkException(e)
        }
    }

    override suspend fun sendRequestAsync(request: Request): Response {
        return sendRequestAsyncHelper(request) { code, headers, responseBody, url ->
            val responseBodyBytes = responseBody.bytes()
            logResponse(url, code, headers, responseBodyBytes)
            Response(code, headers, responseBodyBytes, url)
        }
    }

    override suspend fun sendRequestWithStreamingResponseAsync(request: Request): StreamingResponse {
        return sendRequestAsyncHelper(request) { code, headers, responseBody, url ->
            StreamingResponse(code, headers, responseBody.byteStream(), url)
        }
    }

    private suspend fun <T> sendRequestAsyncHelper(
        request: Request,
        responseMapper: (Int, Headers, ResponseBody, HttpUrl) -> T,
    ): T =
        suspendCancellableCoroutine { continuation ->
            val okHttpRequest = request.buildRequest()
            val okHttpClient = getClient(request)
            val call = okHttpClient.newCall(okHttpRequest)
            logRequest(request, okHttpRequest.url)
            continuation.invokeOnCancellation {
                call.cancel()
            }
            call.enqueue(
                object : Callback {
                    override fun onFailure(
                        call: Call,
                        e: IOException,
                    ) {
                        if (e is SocketTimeoutException) {
                            log(
                                configuration.logConfiguration.logger,
                                configuration.logConfiguration.responseMetadata,
                                configuration.logConfiguration.messageSanitizer,
                                e,
                            ) {
                                "Request timed out: ${okHttpRequest.url}"
                            }
                            continuation.resumeWithException(TimeoutException(e))
                        } else {
                            log(
                                configuration.logConfiguration.logger,
                                configuration.logConfiguration.responseMetadata,
                                configuration.logConfiguration.messageSanitizer,
                                e,
                            ) {
                                "Request encountered network error: ${okHttpRequest.url}"
                            }
                            continuation.resumeWithException(NetworkException(e))
                        }
                    }

                    override fun onResponse(
                        call: Call,
                        response: okhttp3.Response,
                    ) {
                        val responseHeaders =
                            Headers(
                                response.headers.map {
                                    Header(it.first, it.second)
                                },
                            )
                        // Response body should not be null, per documentation, since we are using `execute` to make the request
                        val responseBody =
                            response.body ?: throw IllegalStateException("Got null response body from OkHttp")
                        continuation.resume(
                            responseMapper(response.code, responseHeaders, responseBody, okHttpRequest.url),
                        )
                    }
                },
            )
        }

    private fun getClient(request: Request): OkHttpClient {
        if (request.connectionTimeoutMS == null && request.socketTimeoutMS == null) {
            return defaultOkHttpClient
        }
        val perCallBuilder = defaultOkHttpClient.newBuilder()
        request.connectionTimeoutMS?.let {
            perCallBuilder.connectTimeout(Duration.ofMillis(it))
        }
        request.socketTimeoutMS?.let {
            perCallBuilder.readTimeout(Duration.ofMillis(it))
        }
        return perCallBuilder.build()
    }

    private fun Request.buildRequest(): okhttp3.Request {
        val requestBuilder = okhttp3.Request.Builder()
        requestBuilder.setURL(this)
        requestBuilder.addHeaders(this)
        requestBuilder.addBody(this)
        return requestBuilder.build()
    }

    private fun okhttp3.Request.Builder.setURL(request: Request) {
        val url: String
        if (configuration.baseURL.endsWith("/") && request.path.startsWith("/")) {
            url = configuration.baseURL + request.path.substring(1)
        } else {
            url = configuration.baseURL + request.path
        }
        val urlBuilder = url.toHttpUrl().newBuilder()
        if (!request.omitDefaultQueryParameters) {
            for (queryParameter in configuration.defaultQueryParameters) {
                urlBuilder.addQueryParameter(queryParameter.name, queryParameter.value)
            }
            for (generator in configuration.defaultQueryParameterGenerators) {
                val (name, value) = generator(request)
                urlBuilder.addQueryParameter(name, value)
            }
        }
        for (queryParameter in request.queryParameters) {
            urlBuilder.addQueryParameter(queryParameter.name, queryParameter.value)
        }
        url(urlBuilder.build())
    }

    private fun okhttp3.Request.Builder.addHeaders(request: Request) {
        if (!request.omitDefaultHeaders) {
            for (header in configuration.defaultHeaders) {
                addHeader(header.name, header.value)
            }
            for (generator in configuration.defaultHeaderGenerators) {
                val (name, value) = generator(request)
                addHeader(name, value)
            }
        }
        for (header in request.headers) {
            addHeader(header.name, header.value)
        }
    }

    private fun okhttp3.Request.Builder.addBody(request: Request) {
        val requestBodyBytes = request.body
        val requestBodyStream = request.bodyStream
        when {
            requestBodyBytes != null -> {
                method(request.method.toString(), requestBodyBytes.toRequestBody())
            }
            requestBodyStream != null -> {
                method(request.method.toString(), InputStreamRequestBody(requestBodyStream))
            }
            else -> {
                method(request.method.toString(), null)
            }
        }
    }

    private fun logRequest(
        request: Request,
        url: HttpUrl,
    ) {
        log(
            configuration.logConfiguration.logger,
            configuration.logConfiguration.requestMetadata,
            configuration.logConfiguration.messageSanitizer,
            messageGenerator = """Sending request:
                        |URL: $url
                        |Method: ${request.method}
                        |Headers:
                        |${request.headers.joinToString("\n", transform = Header::toString)}
                        |Query Parameters:
                        |${request.queryParameters.joinToString("\n", transform = QueryParameter::toString)}
                    """::trimMargin,
        )
        log(
            configuration.logConfiguration.logger,
            configuration.logConfiguration.requestBody,
            messageSanitizer = configuration.logConfiguration.messageSanitizer,
        ) {
            request.body?.let {
                try {
                    val body = String(it, Charsets.UTF_8)
                    return@log "Request body: $url\n$body"
                } catch (e: Exception) {
                    return@log "Request body: $url\nBody cannot be parsed to string. Content size: ${it.size}"
                }
            }
            return@log ""
        }
    }

    private fun logResponse(
        url: HttpUrl,
        code: Int,
        headers: Headers,
        responseBody: ByteArray,
    ) {
        log(
            configuration.logConfiguration.logger,
            configuration.logConfiguration.responseMetadata,
            configuration.logConfiguration.messageSanitizer,
            messageGenerator = """Received response:
                       |URL: $url
                       |Status Code: $code 
                       |Headers:
                       |${headers.joinToString("\n", transform = Header::toString)}
                    """::trimMargin,
        )
        log(
            configuration.logConfiguration.logger,
            configuration.logConfiguration.responseBody,
            configuration.logConfiguration.messageSanitizer,
        ) {
            responseBody.let {
                try {
                    val body = String(it, Charsets.UTF_8)
                    return@log "Response body: $url\n$body"
                } catch (e: Exception) {
                    return@log "Response body: $url\nBody cannot be parsed as UTF-8 string. Content size: ${it.size}"
                }
            }
        }
    }
}

private data class NTuple4<T1, T2, T3, T4>(val t1: T1, val t2: T2, val t3: T3, val t4: T4)

private fun log(
    logger: Logger,
    level: LogLevel,
    messageSanitizer: LogMessageSanitizer,
    throwable: Throwable? = null,
    messageGenerator: () -> String,
) {
    when (level) {
        LogLevel.DEBUG ->
            logger.debug(throwable = throwable, message = {
                messageSanitizer(messageGenerator())
            })
        LogLevel.INFO ->
            logger.info(throwable = throwable, message = {
                messageSanitizer(messageGenerator())
            })
        LogLevel.WARN ->
            logger.warn(throwable = throwable, message = {
                messageSanitizer(messageGenerator())
            })
        LogLevel.ERROR ->
            logger.error(throwable = throwable, message = {
                messageSanitizer(messageGenerator())
            })
        LogLevel.NONE -> {
        } // Nothing to do
    }
}
