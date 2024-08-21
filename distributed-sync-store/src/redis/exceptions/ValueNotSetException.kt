package com.suryadigital.leo.distributedStore.redis.exceptions

/**
 * Exception thrown when the implementation is not able to set the value for the key.
 */
@Suppress("Unused")
class ValueNotSetException : Exception {
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
