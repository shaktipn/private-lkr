package com.suryadigital.leo.storage.s3.exceptions

import com.suryadigital.leo.storage.exceptions.StorageRuntimeException

/**
 * Generic wrapper exception for all the exceptions thrown during an S3 operation.
 *
 * @property message describes the reason why the exception was thrown.
 * @property cause cause due to which the exception was thrown.
 */
class S3RuntimeException(
    override val message: String,
    override val cause: Throwable? = null,
) : StorageRuntimeException(message, cause)
