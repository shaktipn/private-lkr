package com.suryadigital.leo.testUtils

/**
 * Generate response value or throw an exception - to be used along with Mocks.
 *
 * @param T expected response type.
 */
sealed class ResultGenerator<T> {
    /**
     * Denotes that the return type of [ResultGenerator] is a response.
     *
     * @param value expected response type.
     */
    class Response<T>(val value: T) : ResultGenerator<T>()

    /**
     * Denotes that the return type of [ResultGenerator] is an error.
     *
     * @param value expected exception type.
     */
    class Exception<Nothing>(val value: kotlin.Exception) : ResultGenerator<Nothing>()
}
