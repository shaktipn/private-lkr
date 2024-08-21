package com.suryadigital.leo.distributedStore.redis

import com.suryadigital.leo.distributedStore.DistributedSynchronizedStore
import com.suryadigital.leo.distributedStore.DistributedSynchronizedStore.ComputeResult
import com.suryadigital.leo.distributedStore.DistributedSynchronizedStore.Lifetime
import com.suryadigital.leo.distributedStore.redis.exceptions.LockNotAcquiredException
import com.suryadigital.leo.distributedStore.redis.exceptions.ValueNotSetException
import com.suryadigital.leo.inlineLogger.getInlineLogger
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisCommandExecutionException
import io.lettuce.core.SetArgs
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.max

private val logger = getInlineLogger(RedisDistributedSynchronizedStore::class)

/**
 * Use Redis to fetch and write values synchronously over multiple processes or services
 *
 * - A lock is acquired when the value is successfully set for a key shared across all processes. The lock's key is derived from the provided key in [getValue].
 *   Locking is done with a combination of [SETNX](https://redis.io/commands/setnx/) and [PEXPIRE](https://redis.io/commands/pexpire/).
 * - If the value for the required key was not previously set, a new value is computed and set along with [Lifetime].
 * - The value for the required key is returned if it was previously set and not expired.
 * - The lock is held for a duration of [lockTimeoutMillis] before which the new value must be computed.
 * - The lock is released by deleting the entry with the lock's key using [DEL](https://redis.io/commands/del/).
 *
 * @param lockTimeoutMillis timeout to hold the write-lock. `computeIfAbsent` must return the new value within this duration.
 * @param maxRetryCount number of attempts to try and obtain a write-lock. Retry interval is computed as a minimum of [lockTimeoutMillis]/[maxRetryCount] and 100 milliseconds.
 */
@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisDistributedSynchronizedStore(
    private val lockTimeoutMillis: Long,
    private val maxRetryCount: Int = 0,
) : KoinComponent, DistributedSynchronizedStore {
    private val redisClient by inject<RedisCoroutinesCommands<String, String>>()
    private val retryIntervalMillis: Long =
        if (maxRetryCount > 0) {
            max(100L, ceil(lockTimeoutMillis.toDouble() / maxRetryCount).toLong())
        } else {
            0
        }

    /**
     * Gets the value from Redis for a given [key]. A new value is generated and stored if the key has expired or absent.
     *
     * @param key key to retrieve value for.
     * @param computeIfAbsent function to call if [key] is not found, or has expired. Should return [ComputeResult] that will be stored and returned as the value.
     * @return value corresponding to [key].
     */
    @Throws(LockNotAcquiredException::class, ValueNotSetException::class)
    override suspend fun getValue(
        key: String,
        computeIfAbsent: suspend () -> ComputeResult,
    ): String {
        var retryCount = 0
        while (true) {
            try {
                return redisClient.get(key)?.let { curValue ->
                    val storeValue = Json.decodeFromString(StoreValue.serializer(), curValue)
                    if (isValueExpired(storeValue)) {
                        setNewValue(key, retryCount, computeIfAbsent)
                    } else {
                        storeValue.value
                    }
                } ?: run {
                    setNewValue(key, retryCount, computeIfAbsent)
                }
            } catch (e: LockNotAcquiredException) {
                if (retryCount == maxRetryCount) {
                    throw LockNotAcquiredException("Exhausted retries after $retryCount attempts.", e)
                }
                retryCount++
                delay(retryIntervalMillis)
            }
        }
    }

    /**
     * Deletes the entry if the key is present in Redis.
     *
     * @param key key to delete value for.
     */
    override suspend fun delete(key: String) {
        redisClient.get(key)?.let {
            redisClient.del(key)
        }
    }

    private suspend fun setNewValue(
        key: String,
        retryCount: Int,
        computeIfAbsent: suspend () -> ComputeResult,
    ): String {
        val clientId = UUID.randomUUID().toString()
        val keyForLocking = "${key}Lock"
        val keyForFenceToken = "${key}Token"
        try {
            if (getLock(keyForLocking, clientId, lockTimeoutMillis)) {
                val fenceToken = incrementValue(keyForFenceToken)
                val newValue = computeIfAbsent()
                if (redisClient.get(keyForFenceToken)?.toLong() != fenceToken) {
                    logger.debug { "Fence token $fenceToken is no longer valid." }
                    throw LockNotAcquiredException("Fence token is no longer valid.")
                }
                setValue(key, getEncodedValue(newValue))
                return newValue.value
            } else {
                logger.debug { "Unable to acquire lock for $clientId, retryCount = $retryCount." }
                throw LockNotAcquiredException("Unable to acquire lock after $retryCount attempts.")
            }
        } finally {
            releaseLock(keyForLocking, clientId)
        }
    }

    private suspend fun incrementValue(key: String): Long? {
        try {
            return redisClient.incr(key)
        } catch (e: RedisCommandExecutionException) {
            if (e.message?.contains(OVERFLOW_ERROR_MESSAGE) == true) {
                logger.debug { "Fence Token Overflow detected, resetting value to 0." }
                redisClient.set(key, "0")
                return 0
            }
            throw e
        }
    }

    private suspend fun getLock(
        key: String,
        value: String,
        lockTimeoutMillis: Long,
    ): Boolean {
        val setArgs = SetArgs().nx().px(lockTimeoutMillis)
        val res = redisClient.set(key, value, setArgs)
        return res?.let {
            if (it == OK_STATUS) {
                logger.debug { "Lock with key $key acquired for value $value." }
                true
            } else {
                false
            }
        } ?: false
    }

    private suspend fun releaseLock(
        key: String,
        value: String,
    ) {
        val lockValue = redisClient.get(key)
        lockValue?.let {
            if (it == value) {
                redisClient.del(key)
            } else {
                logger.warn { "Cannot delete key `$key`, value was changed." }
            }
        }
    }

    private suspend fun setValue(
        key: String,
        value: String,
    ): String {
        return redisClient.set(key, value)?.let { status ->
            if (status != OK_STATUS) {
                throw ValueNotSetException()
            }
            logger.debug { "Value set for key $key." }
            value
        } ?: throw ValueNotSetException()
    }

    private fun isValueExpired(storeValue: StoreValue): Boolean {
        return storeValue.expiresAt?.let { expiresAt ->
            Instant.parse(
                expiresAt,
            ) <= Instant.now()
        } ?: false
    }

    private fun getEncodedValue(newValue: ComputeResult): String {
        val expiresAt =
            when (val lifetime = newValue.lifetime) {
                is Lifetime.ExpiresAt -> lifetime.expiresAt
                Lifetime.Infinite -> null
                is Lifetime.TimeToLive ->
                    Instant.now()
                        .plusMillis(TimeUnit.MILLISECONDS.convert(lifetime.timeToLive, lifetime.timeUnit))
            }?.toString()
        return Json.encodeToString(
            StoreValue.serializer(),
            StoreValue(
                value = newValue.value,
                expiresAt = expiresAt,
            ),
        )
    }

    @Serializable
    internal data class StoreValue(
        val value: String,
        val expiresAt: String?,
    )
}

private const val OK_STATUS = "OK"
private const val OVERFLOW_ERROR_MESSAGE = "ERR increment or decrement would overflow"
