package com.suryadigital.leo.rpc

/**
 * Thrown if the server returns a temporary error.
 * If this exception is thrown, it indicates that the LeoRPC request can be retried later.
 */
class LeoServerException : LeoRPCException {
    /**
     * Number of seconds to wait before retrying.
     */
    val retryAfterSeconds: Long?

    constructor(retryAfterSeconds: Long? = null) : super() {
        this.retryAfterSeconds = retryAfterSeconds
    }

    constructor(message: String?, retryAfterSeconds: Long? = null) : super(message) {
        this.retryAfterSeconds = retryAfterSeconds
    }

    constructor(message: String?, cause: Throwable?, retryAfterSeconds: Long? = null) : super(message, cause) {
        this.retryAfterSeconds = retryAfterSeconds
    }

    constructor(cause: Throwable?, retryAfterSeconds: Long? = null) : super(cause) {
        this.retryAfterSeconds = retryAfterSeconds
    }

    constructor(
        message: String?,
        cause: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean,
        retryAfterSeconds: Long? = null,
    ) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace,
    ) {
        this.retryAfterSeconds = retryAfterSeconds
    }
}
