package com.suryadigital.leo.kotlinxserializationjson

import java.lang.Exception

/**
 * Thrown when there is an error in extracting values from JSON.
 */
class LeoJSONException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace,
    )
}
