package com.suryadigital.leo.rpc

/**
 * Thrown if the authenticated sender of an LeoRPC request did not have the privileges to execute the LeoRPC.
 */
class LeoUnauthorizedException : LeoRPCException {
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
