package com.suryadigital.leo.awskt.secretsManager

import com.suryadigital.leo.awskt.exceptions.AWSClientException

/**
 * Exception thrown when secret with the given key is not found.
 *
 * @see SecretsManagerClientImpl.getSecretValueByKey
 */
@Suppress("Unused")
class SecretNotFoundException : AWSClientException {
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
