package com.suryadigital.leo.rpc

/**
 * Thrown if S2S(Server to Server) request sends a token that is invalid.
 */
class LeoInvalidS2STokenException : LeoRPCException {
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
