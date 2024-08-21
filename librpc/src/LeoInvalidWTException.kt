package com.suryadigital.leo.rpc

/**
 * Thrown if a LeoRPC request sends a WT that is invalid.
 */
class LeoInvalidWTException : LeoRPCException {
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
