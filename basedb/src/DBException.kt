package com.suryadigital.leo.basedb

/**
 * Custom exception thrown when some error occurs during jOOQ query execution.
 *
 * @see fetchExactlyOne
 * @see getNonNullValue
 */
class DBException : Exception {
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
