package com.suryadigital.leo.kedwig

import java.io.BufferedInputStream
import java.io.InputStream

/**
 * Equates one [Request] to another.
 *
 * There is a reason why this method exists, as opposed to overriding [Request.equals], which is unimplemented.
 *
 * Please be careful of using [compareBody] - this may render the [Request] object useless, and result in silent failures.
 *
 * To equate request bodies, we need to consume the [Request.body] [InputStream]. This means that when the request
 * tries to get sent, the stream will already have been read, and the sending may fail.
 *
 * This function does its best to avoid such failures by using [InputStream.mark] and [InputStream.reset] if they are supported.
 *
 * Also note that if you compare a [Request] with [Request.body] set on it, with another with [Request.bodyStream] set on it,
 * they will not be considered equal when [compareBody] is set to true, even if the content of the stream and the byte array are the same.
 *
 * @param other [Request] to compare to.
 * @param compareBody If true, request bodies will be considered. If false, request bodies will not be considered.
 * @param maxBodyMarkSize If the two body [InputStream]s return true from [InputStream.markSupported], the maximum mark size to set. This parameter is only relevant if [compareBody] is true.
 * @return True if the requests are identical, false otherwise.
 */
fun Request.isEqualTo(
    other: Request,
    compareBody: Boolean = false,
    maxBodyMarkSize: Int = MAX_MARK_SIZE,
): Boolean {
    if (this === other) return true
    if (method != other.method) return false
    if (path != other.path) return false
    if (headers != other.headers) return false
    if (queryParameters != other.queryParameters) return false
    if (socketTimeoutMS != other.socketTimeoutMS) return false
    if (connectionTimeoutMS != other.connectionTimeoutMS) return false
    if (omitDefaultHeaders != other.omitDefaultHeaders) return false
    if (omitDefaultQueryParameters != other.omitDefaultQueryParameters) return false
    if (compareBody) {
        if (body == null) {
            if (other.body != null) {
                return false
            }
        } else {
            if (other.body == null) {
                return false
            }
            if (!body.contentEquals(other.body)) {
                return false
            }
        }
        if (bodyStream == null) {
            if (other.bodyStream != null) {
                return false
            }
        } else {
            if (other.bodyStream == null) {
                return false
            }
            if (!bodyStream.isEqualTo(other.bodyStream, maxBodyMarkSize)) {
                return false
            }
        }
    }
    return true
}

/**
 * Equates one [StreamingResponse] to another.
 *
 * Please be careful of using [compareBody] - this may render the [StreamingResponse] object useless, and result in silent failures.
 *
 * There is a reason why this method exists, as opposed to overriding [StreamingResponse.equals], which is unimplemented.
 *
 * To equate response bodies, we need to consume the [StreamingResponse.body] [InputStream]. This means that when you
 * try to read from [StreamingResponse.body] again, it may fail.
 *
 * This function does its best to avoid such failures by using [InputStream.mark] and [InputStream.reset] if they are supported.
 *
 * @param other [Response] to compare to.
 * @param compareBody If true, response bodies will be considered. If false, response bodies will not be considered.
 * @param loadAll If true, loads the entire response body into memory with [StreamingResponse.byteArrayBody].
 * @param maxBodyMarkSize If the two body [InputStream]s return true from [InputStream.markSupported], the maximum mark size to set. This parameter is only relevant if [compareBody] is true, and [loadAll] is false.
 * @return True if the responses are identical, false otherwise.
 */
fun StreamingResponse.isEqualTo(
    other: StreamingResponse,
    compareBody: Boolean = false,
    loadAll: Boolean = true,
    maxBodyMarkSize: Int = MAX_MARK_SIZE,
): Boolean {
    if (this === other) return true
    if (statusCode != other.statusCode) return false
    if (headers != other.headers) return false
    if (compareBody) {
        if (loadAll) {
            return byteArrayBody.contentEquals(other.byteArrayBody)
        }
        return body.isEqualTo(other.body, maxBodyMarkSize)
    }
    return true
}

/**
 * Reads this [InputStream], and [other] and compares them for equality byte by byte.
 *
 * If [this], or [other] support marking and resetting, the streams are marked before being read (up to [maxMarkSize])
 * and reset after being read.
 *
 * @param other [InputStream] to compare to.
 * @param maxMarkSize If [this] and [other] return true from [InputStream.markSupported], the maximum mark size to set.
 * @return True if both [InputStream]s contain the same data, false otherwise.
 */
internal fun InputStream.isEqualTo(
    other: InputStream,
    maxMarkSize: Int = MAX_MARK_SIZE,
): Boolean {
    if (this === other) return true

    if (markSupported()) {
        mark(maxMarkSize)
    }
    if (other.markSupported()) {
        other.mark(maxMarkSize)
    }

    val bufferedThis: BufferedInputStream
    val bufferedOther: BufferedInputStream

    if (this is BufferedInputStream) {
        bufferedThis = this
    } else {
        bufferedThis = BufferedInputStream(this)
    }
    if (other is BufferedInputStream) {
        bufferedOther = other
    } else {
        bufferedOther = BufferedInputStream(other)
    }

    val eof = -1

    try {
        var ch1 = bufferedThis.read()
        while (ch1 != eof) {
            val ch2 = bufferedOther.read()
            if (ch1 != ch2) {
                return false
            }
            ch1 = bufferedThis.read()
        }
        val ch2 = bufferedOther.read()

        return ch2 == eof
    } finally {
        if (markSupported()) {
            reset()
        } else {
            close()
        }
        if (other.markSupported()) {
            other.reset()
        } else {
            other.close()
        }
    }
}

private const val MAX_MARK_SIZE = 10 * 1024 * 1024 // 10 MB
