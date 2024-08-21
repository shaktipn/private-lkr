package com.suryadigital.leo.firebasekt

/**
 * Exception thrown when [PushNotification] implementation fails to send notification.
 */
@Suppress("Unused")
class SendNotificationFailedException : Exception {
    private val errorCode: FCMErrorCode?

    /**
     * Function used by test cases to get the corresponding error code for [SendNotificationFailedException].
     *
     * @return [FCMErrorCode] of the exception, or null.
     */
    fun getErrorCode(): FCMErrorCode? = errorCode

    constructor(errorCode: FCMErrorCode? = null) : super() {
        this.errorCode = errorCode
    }

    constructor(message: String?, errorCode: FCMErrorCode? = null) : super(message) {
        this.errorCode = errorCode
    }

    constructor(message: String?, cause: Throwable?, errorCode: FCMErrorCode? = null) : super(message, cause) {
        this.errorCode = errorCode
    }

    constructor(cause: Throwable?, errorCode: FCMErrorCode? = null) : super(cause) {
        this.errorCode = errorCode
    }

    constructor(
        message: String?,
        cause: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean,
        errorCode: FCMErrorCode? = null,
    ) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace,
    ) {
        this.errorCode = errorCode
    }
}

/**
 * Details of error codes are listed [here](https://firebase.google.com/docs/reference/fcm/rest/v1/ErrorCode)
 */
enum class FCMErrorCode {
    /**
     * Denotes that no more information is available about this error.
     */
    UNSPECIFIED_ERROR,

    /**
     * Denotes that request parameters were invalid.
     */
    INVALID_ARGUMENT,

    /**
     * Denotes that app instance was unregistered from FCM.
     */
    UNREGISTERED,

    /**
     * Denotes that the authenticated sender ID is different from the sender ID for the registration token.
     */
    SENDER_ID_MISMATCH,

    /**
     * Denotes that sending limit exceeded for the message target.
     */
    QUOTA_EXCEEDED,

    /**
     * Denotes that the server is overloaded.
     */
    UNAVAILABLE,

    /**
     * Denotes that an unknown internal error occurred.
     */
    INTERNAL,

    /**
     * Denotes that APNs certificate or web push auth key was invalid or missing.
     */
    THIRD_PARTY_AUTH_ERROR,
}
