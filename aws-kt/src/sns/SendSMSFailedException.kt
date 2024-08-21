package com.suryadigital.leo.awskt.sns

import com.suryadigital.leo.awskt.exceptions.AWSClientException

/**
 * This is a wrapper exception for exceptions thrown by SdkClient or SnsAsyncClient while trying to perform an SMS operation.
 *
 * @see SNSClientImpl.sendSMS
 */
@Suppress("Unused")
class SendSMSFailedException : AWSClientException {
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
