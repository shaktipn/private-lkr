package com.suryadigital.leo.ktUtils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

private class ValidException : Exception()

private class InvalidException : Exception()

private class RetryCustomType

private val validExceptionList = listOf(ValidException::class)

class RetryTest {
    @Test
    fun testNoRetry() {
        runBlocking {
            var executionCount = 0
            executeWithRetries(validExceptionList, 1) {
                executionCount++
                ::RetryCustomType
            }
            assertEquals(1, executionCount)
        }
    }

    @Test
    fun testNoRetryWithSuspendFunction() {
        runBlocking {
            var executionCount = 0
            executeWithRetries(validExceptionList, 1) {
                executionCount++
                delay(1)
                ::RetryCustomType
            }
            assertEquals(1, executionCount)
        }
    }

    @Test
    fun testMaximumRetriesThrowsValidException() {
        runBlocking {
            var executionCount = 0
            try {
                executeWithRetries(validExceptionList, 2) {
                    executionCount++
                    throw ValidException()
                }
            } catch (e: ValidException) {
                assertEquals(3, executionCount)
            }
        }
    }

    @Test
    fun testInvalidExceptionIsThrownWithZeroRetries() {
        runBlocking {
            var executionCount = 0
            try {
                executeWithRetries(validExceptionList, 2) {
                    executionCount++
                    throw InvalidException()
                }
            } catch (e: InvalidException) {
                assertEquals(1, executionCount)
            }
        }
    }

    @Test
    fun testRetryWithRetryInterval() {
        runBlocking {
            var executionCount = 0
            try {
                executeWithRetries(validExceptionList, 2, 10) {
                    executionCount++
                    throw ValidException()
                }
            } catch (e: ValidException) {
                assertEquals(3, executionCount)
            }
        }
    }
}
