package com.suryadigital.leo.kedwig

import java.io.InputStream

/**
 * Constructs the response of a [Request] sent by the API.
 *
 * @property statusCode status code of the response. A list of valid status codes can be found [here](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status).
 * @property headers [Headers] that were sent by the API call in response.
 * @property body content of the response.
 * @property url base URL of the server which sent the API request. This is used to make sure that the cookie is being sent to the correct domain.
 */
class Response(
    val statusCode: Int,
    val headers: Headers,
    val body: ByteArray,
    private val url: okhttp3.HttpUrl,
) {
    /**
     * Lazily reads [body] into a UTF-8 [String].
     */
    val stringBody: String by lazy {
        body.toString(Charsets.UTF_8)
    }

    /**
     * Lazily parses all Set-Cookie headers and returns them.
     */
    val cookies: Cookies by lazy {
        val headerValues = headers.getValues("Set-Cookie")
        if (headerValues.isEmpty()) {
            return@lazy Cookies()
        }
        return@lazy parse(headers, url)
    }

    /**
     * Checks for the equality of [Response] object.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Response

        if (statusCode != other.statusCode) return false
        if (headers != other.headers) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    /**
     * @return a hash code for [Response] object.
     */
    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }
}

/**
 * Streaming implementation of [Response], i.e., instead of sending the entire body as [ByteArray], the API will send an [InputStream] which can be parsed as a response.
 *
 * @property statusCode status code of the response. A list of valid status codes can be found [here](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status).
 * @property headers [Headers] that were sent by the API call in response.
 * @property body content of the response.
 * @property url base URL of the remote server which sent the API response. This is used to make sure that the cookie is being sent from the correct domain.
 */
class StreamingResponse(
    val statusCode: Int,
    val headers: Headers,
    val body: InputStream,
    private val url: okhttp3.HttpUrl,
) {
    /**
     * Lazily reads [body] into a UTF-8 [String].
     *
     * Please note that this can result in [OutOfMemoryError]s since it loads the entire contents of [body] into memory at once.
     */
    val stringBody: String by lazy {
        byteArrayBody.toString(Charsets.UTF_8)
    }

    /**
     * Lazily reads [body] into a [ByteArray]
     *
     * Please note that this can result in [OutOfMemoryError]s since it loads the entire contents of [body] into memory at once.
     */
    val byteArrayBody: ByteArray by lazy(body::readBytes)

    /**
     * Lazily reads [headers] to get the [Cookies] from `Set-Cookie` header.
     */
    val cookies: Cookies by lazy {
        val headerValues = headers.getValues("Set-Cookie")
        if (headerValues.isEmpty()) {
            return@lazy Cookies()
        }
        return@lazy parse(headers, url)
    }
}

/**
 * Kotlin object representation of cookie metadata.
 *
 * @property name name or key of the cookie.
 * @property value value assigned to the [name].
 * @property domain domain from which the cookie is sent.
 * @property path URL path that must exist in the requested URL to send the cookie header.
 * @property expiresAt the [java.time.Instant] value in long format of the time at which the cookie will expire.
 * @property httpOnly if set to true, the cookie will become inaccessible to client side javascript.
 * @property secure if set to true, the cookie will only be sent over an encrypted HTTPS request.
 */
data class Cookie internal constructor(
    var name: String,
    var value: String,
    var domain: String?,
    var path: String?,
    var expiresAt: Long?,
    var httpOnly: Boolean,
    var secure: Boolean,
)

/**
 * Defines the list of [Cookie] that can be sent as a part of [Request] or received as a part of the [Response].
 *
 * @property cookies list of [Cookie] that can be iterated over.
 */
data class Cookies internal constructor(private val cookies: List<Cookie> = listOf()) : Iterable<Cookie> {
    /**
     * Get the first cookie from the [Cookies] that matches the [name].
     *
     * @param name key of the [Header].
     *
     * @return [Cookie] if found or null.
     */
    fun getFirst(name: String): Cookie? {
        return cookies.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Get the value of the first cookie from the [Cookies] that matches the [name].
     *
     * @param name key of the [Cookie].
     *
     * @return [String] value if found or null.
     */
    fun getFirstValue(name: String): String? {
        return getFirst(name)?.value
    }

    /**
     * Get a list of [Cookie] that match the [name].
     *
     * @param name key of the [Cookie].
     *
     * @return list of [Cookie].
     */
    operator fun get(name: String): List<Cookie> {
        return cookies.filter { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Get a list of values that match the [name].
     *
     * @param name key of the [Cookie].
     *
     * @return list of [String] values that match the given key.
     */
    fun getValues(name: String): List<String> {
        return get(name).map(Cookie::value)
    }

    /**
     * Get all the cookies that are present in [Cookies].
     *
     * @return list of [Cookie].
     */
    fun getAll(): List<Cookie> {
        return cookies
    }

    /**
     * Get all the values that are present in [Cookies].
     *
     * @return list of [String] of values for all the [Cookies] present.
     */
    fun getAllValues(): List<String> {
        return cookies.map(Cookie::value)
    }

    /**
     * Get all the names (keys) that are present in [Cookies].
     *
     * @return list of [String] of names for all the [Cookies] present.
     */
    fun getAllNames(): List<String> {
        return cookies.map(Cookie::name)
    }

    /**
     * @return an [Iterator] for [cookies].
     */
    override fun iterator(): Iterator<Cookie> {
        return cookies.iterator()
    }
}

private fun parse(
    headers: Headers,
    url: okhttp3.HttpUrl,
): Cookies {
    val builder = okhttp3.Headers.Builder()
    for (header in headers) {
        builder.add(header.name, header.value)
    }
    val cookies = okhttp3.Cookie.parseAll(url, builder.build())
    return Cookies(
        cookies.map {
            Cookie(
                name = it.name,
                value = it.value,
                domain = it.domain,
                path = it.path,
                expiresAt = it.expiresAt,
                httpOnly = it.httpOnly,
                secure = it.secure,
            )
        },
    )
}
