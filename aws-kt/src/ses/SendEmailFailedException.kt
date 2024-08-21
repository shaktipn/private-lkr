package com.suryadigital.leo.awskt.ses

import com.suryadigital.leo.awskt.exceptions.AWSClientException

/**
 * Exception thrown when sending the email fails.
 *
 * @see SESClientImpl.sendEmail
 */
@Suppress("Unused")
class SendEmailFailedException : AWSClientException {
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
