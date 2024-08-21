package com.suryadigital.leo.types

import java.lang.Exception

/**
 * Stores a validated and normalized email Id.
 *
 * @property value email Id String.
 */
class LeoEmailId
    @Throws(LeoInvalidLeoEmailIdException::class)
    constructor(emailId: String) {
        val value: String

        init {
            val validEmailIdReges = Regex("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+$")
            if (!emailId.trim().matches(validEmailIdReges)) {
                throw LeoInvalidLeoEmailIdException("Email Id $emailId is invalid.")
            }
            value = emailId.trim().lowercase()
        }

        /**
         * Checks for the equality of [LeoEmailId] based on its value.
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LeoEmailId

            return value == other.value
        }

        /**
         * @return a hash code for [LeoEmailId] obejct.
         */
        override fun hashCode(): Int {
            return value.hashCode()
        }

        /**
         * @return the string representation for [LeoEmailId].
         */
        override fun toString(): String {
            return "LeoEmailId($value)"
        }
    }

/**
 * Exception thrown when the given string does not match the valid email Id format.
 */
@Suppress("unused")
class LeoInvalidLeoEmailIdException : Exception {
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
