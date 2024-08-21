package com.suryadigital.leo.kedwig

import java.io.InputStream

internal val METHODS_WITHOUT_BODY = listOf(Method.GET, Method.HEAD, Method.CONNECT, Method.OPTIONS, Method.TRACE)

/**
 * Container class for metadata regarding all the request parameters required to make an API request.
 *
 * @property method [Method] of the request.
 * @property path endpoint of the base URL on which the request should be made.
 * @property headers list of [Header] to be sent along with the request.
 * @property queryParameters list of [QueryParameter] to be sent along with the request.
 * @property body content of the request.
 * @property bodyStream stream of content that should be sent to the API in a streaming manner.
 * @property connectionTimeoutMS time period in which our client should establish a connection with a target host.
 * @property socketTimeoutMS maximum time of inactivity between two data packets when waiting for server's response.
 * @property omitDefaultHeaders if the request should append the default headers, or only send [headers] to the API.
 * @property omitDefaultQueryParameters if the request should append the default query parameters, or only send [queryParameters] to the API.
 */
class Request internal constructor(
    val method: Method,
    val path: String,
    val headers: Headers = Headers(),
    val queryParameters: QueryParameters = QueryParameters(),
    val body: ByteArray? = null,
    val bodyStream: InputStream? = null,
    val socketTimeoutMS: Long? = null,
    val connectionTimeoutMS: Long? = null,
    val omitDefaultHeaders: Boolean = false,
    val omitDefaultQueryParameters: Boolean = false,
)

/**
 * Annotates the DSL for [Request].
 */
@DslMarker
annotation class RequestDsl

/**
 * This is an implementation detail to enable the request DSL.
 */
@RequestDsl
class RequestBuilder {
    /**
     * [Method] of the request.
     */
    var method: Method = Method.GET

    /**
     * Endpoint of the base URL on which the request should be made.
     */
    var path: String = ""

    /**
     * Builder for the list of headers that should be sent along with the request.
     */
    private var headers: HeadersBuilder = HeadersBuilder()

    /**
     * Builder for the list of query parameters that should be sent along with the request.
     */
    private var queryParameters: QueryParametersBuilder = QueryParametersBuilder()

    /**
     * Body of the request.
     */
    private var body: ByteArray? = null

    /**
     * Body of the request as an [InputStream] for the streaming implementation.
     */
    private var bodyStream: InputStream? = null

    /**
     * Amount of time to wait for the connection to re-establish after the connection was lost, before terminating the request.
     */
    var socketTimeoutMS: Long? = null

    /**
     * Amount of time to wait for the connection to establish for the first time before terminating the request.
     */
    var connectionTimeoutMS: Long? = null

    /**
     * If the request should append the default headers, or only send [headers] to the API.
     */
    var omitDefaultHeaders: Boolean = false

    /**
     * If the request should append the default query parameters, or only send [queryParameters] to the API.
     */
    var omitDefaultQueryParameters: Boolean = false

    /**
     * Add [Headers] to the builder.
     *
     * @param block builder operations to perform on [HeadersBuilder].
     */
    fun headers(block: HeadersBuilder.() -> Unit) {
        headers.addAll(HeadersBuilder().apply(block))
    }

    /**
     * Add [QueryParameters] to the builder.
     *
     * @param block builder operations to perform on [QueryParametersBuilder].
     */
    fun queryParameters(block: QueryParametersBuilder.() -> Unit) {
        queryParameters.addAll(QueryParametersBuilder().apply(block))
    }

    /**
     * Add [Cookies] to the builder.
     *
     * @param block builder operations to perform on [CookiesBuilder].
     */
    fun cookies(block: CookiesBuilder.() -> Unit) {
        headers.addAll(CookiesBuilder().apply(block))
    }

    /**
     * Add body to the builder.
     *
     * @param str content that should be sent in the body in form of [String].
     */
    fun body(str: String) {
        body(str.toByteArray())
    }

    /**
     * Add body to the builder.
     *
     * @param byteArray content that should be sent in the body in form of [ByteArray].
     *
     * @throws RequestBuilderException if the [bodyStream] is not null. Either [body] or [bodyStream] can be sent in a [Request].
     */
    @Throws(RequestBuilderException::class)
    fun body(byteArray: ByteArray) {
        if (bodyStream != null) {
            throw RequestBuilderException("Cannot set body as both a ByteArray and an InputStream")
        }
        body = byteArray
    }

    /**
     * Add body to the builder.
     *
     * @param inputStream content that should be sent in the body in form of [InputStream].
     *
     * @throws RequestBuilderException if the [body] is not null. Either [body] or [bodyStream] can be sent in a [Request].
     */
    @Throws(RequestBuilderException::class)
    fun bodyStream(inputStream: InputStream) {
        if (body != null) {
            throw RequestBuilderException("Cannot set body as both a ByteArray and an InputStream")
        }
        bodyStream = inputStream
    }

    /**
     * Build and return the [Request] object.
     */
    @Throws(RequestBuilderException::class)
    fun build(): Request {
        if (body != null || bodyStream != null) {
            if (METHODS_WITHOUT_BODY.contains(method)) {
                throw RequestBuilderException("Request with HTTP method $method cannot contain a body")
            }
        }
        if (body == null && bodyStream == null) {
            if (method == Method.POST || method == Method.PUT) {
                throw RequestBuilderException("Request with HTTP method ${method.name} must contain a body")
            }
        }
        return Request(
            method = method,
            path = path,
            headers = Headers(headers),
            queryParameters = QueryParameters(queryParameters),
            body = body,
            bodyStream = bodyStream,
            socketTimeoutMS = socketTimeoutMS,
            connectionTimeoutMS = connectionTimeoutMS,
            omitDefaultHeaders = omitDefaultHeaders,
            omitDefaultQueryParameters = omitDefaultQueryParameters,
        )
    }
}

/**
 * DSL function to create request.
 *
 * @param block block of code that performs operations on [RequestBuilder].
 *
 * @return the built [Request] that was defined using the operations done in the [block].
 */
fun request(block: RequestBuilder.() -> Unit): Request = RequestBuilder().apply(block).build()

/**
 * Annotates the DSL for [Cookies].
 */
@DslMarker
annotation class CookiesDsl

/**
 * This is an implementation detail to enable the headers DSL - do not use directly.
 *
 * @param storage array list in which the [Cookies] should be stored.
 */
@CookiesDsl
class CookiesBuilder(storage: ArrayList<Header> = ArrayList()) : MutableList<Header> by storage {
    /**
     * Add a [Cookie] as a [Header] to the builder.
     *
     * @param name key of the [Cookie].
     * @param value value of the [Cookie].
     */
    fun cookie(
        name: String,
        value: String,
    ) {
        add(Header("Cookie", "$name=$value"))
    }

    /**
     * Add query parameters to the builder by using the `to` infix. This function must be called on the name of the [Cookie].
     *
     * @param value value of the [Cookie].
     */
    infix fun String.to(value: String) {
        add(Header("Cookie", "$this=$value"))
    }

    /**
     * Checks for the equality of [CookiesBuilder] based on its content.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as CookiesBuilder
        if (size != other.size) {
            return false
        }
        return (0 until size).none { this[it] != other[it] }
    }

    /**
     * @return a hash code for [CookiesBuilder] object.
     */
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
