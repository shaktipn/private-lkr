package com.suryadigital.leo.awskt.exceptions

/**
 * Exception thrown when `messageId` is not found in the response given when sending an Email or an SMS.
 */
@Suppress("Unused")
class InvalidMessageIdException : AWSClientException {
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
