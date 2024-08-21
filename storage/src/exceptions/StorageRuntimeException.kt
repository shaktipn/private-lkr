package com.suryadigital.leo.storage.exceptions

/**
 * Base exception for all the exception thrown by the storage runtime features.
 *
 * @property message describes the reason why the exception was thrown.
 * @property cause cause due to which the exception was thrown.
 */
abstract class StorageRuntimeException(
    override val message: String,
    override val cause: Throwable?,
) : Exception(message, cause)
