package com.suryadigital.leo.rateLimiter

/**
 * Thrown when for a particular client, the number of allowed requests exceeds in a given time interval.
 */
@Suppress("unused")
class RateLimitExceededException : Exception {
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
