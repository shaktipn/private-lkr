package com.suryadigital.leo.awskt.s3

import com.suryadigital.leo.awskt.exceptions.AWSClientException

/**
 * Exception thrown when the implementation is unable to upload the [S3Object].
 *
 * @see S3ClientImpl.uploadObject
 */
@Suppress("Unused")
class ObjectUploadFailedException : AWSClientException {
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
