package com.suryadigital.leo.ktUtils

import kotlinx.coroutines.delay
import kotlin.reflect.KClass

/**
 * Implements a retry mechanism based on provided [Exception] classes.
 * If one of the valid exceptions occurs, retries are triggered until [maxRetryCount] is reached.
 *
 * A delay can be introduced between retries, if required.
 *
 * @param validExceptionList list of exceptions that trigger a retry.
 * @param maxRetryCount maximum retry attempts.
 * @param retryIntervalMillis introduce delay (in milliseconds) between retries; default is 0.
 * @param block function block to be invoked.
 *
 * @return [T] value of the type returned by the [block].
 */
suspend fun <T> executeWithRetries(
    validExceptionList: List<KClass<out Exception>>,
    maxRetryCount: Int,
    retryIntervalMillis: Long = 0,
    block: suspend () -> T,
): T {
    var retryCount = 0
    while (true) {
        try {
            return block.invoke()
        } catch (e: Exception) {
            if (e::class !in validExceptionList || retryCount == maxRetryCount) {
                throw e
            }
            retryCount++
            if (retryIntervalMillis > 0) {
                delay(retryIntervalMillis)
            }
        }
    }
}
