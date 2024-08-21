package com.suryadigital.leo.rateLimiter

/**
 *  Checks if the number of requests from a particular client exceeds in a given interval or not.
 */
interface RateLimiter {
    /**
     *  Stores number of requests which should be allowed in a given interval for a particular client.
     */
    val allowedRequestsPerInterval: Int

    /**
     *  Stores the interval in which number of requests are allowed.
     */
    val intervalInSeconds: Int

    /**
     *  Checks if the number of requests for a particular client exceeds, in a given interval.
     *
     *  @param key used for uniquely identifying the client. It can be an IP address of the client or some unique clientId or combination of both.
     *
     *  @throws [RateLimitExceededException] when number of allowed requests for [key] exceeds [allowedRequestsPerInterval] in the last [intervalInSeconds].
     */
    @Throws(RateLimitExceededException::class)
    suspend fun check(key: String)
}
