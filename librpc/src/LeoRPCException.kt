package com.suryadigital.leo.rpc

/**
 * Root exception for all LeoRPC exceptions.
 *
 * You probably should not be catching this, but instead subclasses of this.
 */
open class LeoRPCException : Exception {
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
