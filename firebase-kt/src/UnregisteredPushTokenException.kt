package com.suryadigital.leo.firebasekt

/**
 * Exception thrown when app instance was unregistered from FCM.
 */
@Suppress("Unused")
class UnregisteredPushTokenException : Exception {
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
