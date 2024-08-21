package com.suryadigital.leo.rpc

/**
 * Thrown when an LLT is used to get a new SLT, but the LLT is invalid.
 */
class LeoInvalidLLTException : LeoRPCException {
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
