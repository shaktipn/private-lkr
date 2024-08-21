package com.suryadigital.leo.rateLimiter

import com.suryadigital.leo.inlineLogger.getInlineLogger
import com.suryadigital.leo.ktUtils.cached
import com.typesafe.config.Config
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.Range
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.async.multi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * This is an implementation of [RateLimiter] that uses the Redis database for limiting network traffic.
 */
@Suppress("unused")
class RedisRateLimiter : RateLimiter, KoinComponent {
    private val config: Config by inject()
    private val redis by inject<RedisAsyncCommands<String, String>>()
    private val logger = getInlineLogger(RedisRateLimiter::class)

    /**
     *  Stores number of requests which should be allowed in a given interval for a particular client.
     */
    override val allowedRequestsPerInterval: Int by cached {
        config.getInt("externalIncomingTransfers.maxAllowedRequestInGivenInterval")
    }

    /**
     *  Stores the interval in which number of requests are allowed.
     */
    override val intervalInSeconds: Int by cached {
        config.getInt("externalIncomingTransfers.tooManyRequestTimeoutDurationInSeconds")
    }

    /**
     *  Checks if the number of requests for a particular client exceeds, in a given interval.
     *
     *  @param key used for uniquely identifying the client. It can be an IP address of the client or some unique clientId or combination of both.
     *  @throws [RateLimitExceededException] when number of allowed requests for [key] exceeds [allowedRequestsPerInterval] in the last [intervalInSeconds].
     */
    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun check(key: String) {
        val currentTime = System.currentTimeMillis()
        val result =
            redis.multi {
                zremrangebyscore(key, Range.create(0, (currentTime - TimeUnit.SECONDS.toMillis(intervalInSeconds.toLong()))))
                zcount(key, Range.unbounded())
                zadd(key, currentTime.toDouble(), "$currentTime")
                expire(key, intervalInSeconds.toLong())
            }
        if (result.wasDiscarded()) {
            logger.error { "Redis transactional batch execution has been discarded $result" }
        }
        val noOfRequestMade = result.get<Long>(1).toInt()
        if (noOfRequestMade >= allowedRequestsPerInterval) {
            throw RateLimitExceededException("For key - $key, number of allowed requests exceeded - $noOfRequestMade")
        }
    }
}
