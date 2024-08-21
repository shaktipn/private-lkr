package com.suryadigital.leo.kedwig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NetworkExceptionTest {
    private val errorMessage = "Error Message"
    private val illegalStateException = IllegalStateException()

    @Test
    fun testWithMesssageCauseSuppressionAndWritableStackTrace() {
        val exception =
            assertFailsWith<NetworkException> {
                throw NetworkException(
                    message = errorMessage,
                    cause = illegalStateException,
                    enableSuppression = true,
                    writableStackTrace = true,
                )
            }
        assertEquals(errorMessage, exception.message)
        assertNotNull(exception.cause)
        exception.cause?.let {
            assertEquals(IllegalStateException::class, it::class)
        }
        assertEquals(0, exception.suppressedExceptions.size)
        assertNotNull(exception.stackTrace)
    }

    @Test
    fun testWithMesssageCauseSuppressionAndNoWritableStackTrace() {
        val exception =
            assertFailsWith<NetworkException> {
                throw NetworkException(
                    message = errorMessage,
                    cause = illegalStateException,
                    enableSuppression = true,
                    writableStackTrace = false,
                )
            }
        assertEquals(errorMessage, exception.message)
        assertNotNull(exception.cause)
        exception.cause?.let {
            assertEquals(IllegalStateException::class, it::class)
        }
        assertEquals(0, exception.suppressedExceptions.size)
        assertFailsWith<ArrayIndexOutOfBoundsException> {
            exception.stackTrace[0] = StackTraceElement("", "", "", 1)
        }
    }

    @Test
    fun testWithMesssageAndCause() {
        val exception =
            assertFailsWith<NetworkException> {
                throw NetworkException(
                    message = errorMessage,
                    cause = illegalStateException,
                )
            }
        assertEquals(errorMessage, exception.message)
        assertNotNull(exception.cause)
        exception.cause?.let {
            assertEquals(IllegalStateException::class, it::class)
        }
    }

    @Test
    fun testWithCause() {
        val exception =
            assertFailsWith<NetworkException> {
                throw NetworkException(
                    cause = illegalStateException,
                )
            }
        assertNotNull(exception.cause)
        exception.cause?.let {
            assertEquals(IllegalStateException::class, it::class)
        }
    }

    @Test
    fun testWithMesssage() {
        val exception =
            assertFailsWith<NetworkException> {
                throw NetworkException(
                    message = errorMessage,
                )
            }
        assertEquals(errorMessage, exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun testOnlyContructor() {
        val exception =
            assertFailsWith<NetworkException> {
                throw NetworkException()
            }
        assertNull(exception.message)
        assertNull(exception.cause)
    }
}
