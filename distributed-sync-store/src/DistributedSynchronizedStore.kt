package com.suryadigital.leo.distributedStore

import java.time.Instant
import java.util.concurrent.TimeUnit

/**
 * A mechanism to store and retrieve a key value pair of the type <String, String> in a concurrent distributed manner.
 *
 * It is expected that implementations of this class are safe when called from multiple JVM processes.
 * Synchronization must be handled outside the JVM, for example, through Redis, or a database.
 */
interface DistributedSynchronizedStore {
    /**
     * @param key key to retrieve value for.
     * @param computeIfAbsent function to call if [key] is not found, or has expired. Should return [ComputeResult] that will be stored and returned as the value.
     *
     * @return value corresponding to [key].
     */
    suspend fun getValue(
        key: String,
        computeIfAbsent: suspend () -> ComputeResult,
    ): String

    /**
     * @param key key to delete value for.
     */
    suspend fun delete(key: String)

    /**
     * Result obtained after `computeIfAbsent` is called.
     *
     * @property value new value to be set.
     * @property lifetime life span of the value until expiry.
     *
     * @constructor create empty compute result.
     */
    data class ComputeResult(
        val value: String,
        val lifetime: Lifetime = Lifetime.Infinite,
    )

    /**
     * Lifetime of the value before expiry.
     */
    sealed class Lifetime {
        /**
         * Denotes the time at which the value will expire.
         *
         * @property expiresAt time in [Instant] at which the value will expire.
         */
        data class ExpiresAt(val expiresAt: Instant) : Lifetime()

        /**
         * Denotes the time remaining before the value expires.
         *
         * @property timeToLive time duration after which the value will expire. The unit of this is measured based on [timeUnit].
         * @property timeUnit unit of time to denote the measurement of [timeToLive]. If the value is [TimeUnit.MILLISECONDS] and [timeToLive] is equal to 100, that means that the value will expire after 100 milliseconds.
         */
        data class TimeToLive(val timeToLive: Long, val timeUnit: TimeUnit) : Lifetime()

        /**
         * Denotes that the value will never expire.
         */
        data object Infinite : Lifetime()
    }
}
