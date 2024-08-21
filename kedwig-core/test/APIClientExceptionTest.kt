package com.suryadigital.leo.kedwig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class APIClientExceptionTest {
    private val errorMessage = "Error Message"
    private val illegalStateException = IllegalStateException()

    @Test
    fun testWithMesssageCauseSuppressionAndWritableStackTrace() {
        val exception =
            APIClientException(
                message = errorMessage,
                cause = illegalStateException,
                enableSuppression = true,
                writableStackTrace = true,
            )
        assertEquals(errorMessage, exception.message)
        val cause = assertNotNull(exception.cause)
        assertEquals(IllegalStateException::class, cause::class)
        assertEquals(0, exception.suppressedExceptions.size)
        assertNotNull(exception.stackTrace)
    }

    @Test
    fun testWithMesssageCauseSuppressionAndNoWritableStackTrace() {
        val exception =
            APIClientException(
                message = errorMessage,
                cause = illegalStateException,
                enableSuppression = true,
                writableStackTrace = false,
            )
        assertEquals(errorMessage, exception.message)
        val cause = assertNotNull(exception.cause)
        assertEquals(IllegalStateException::class, cause::class)
        assertEquals(0, exception.suppressedExceptions.size)
        assertFailsWith<ArrayIndexOutOfBoundsException> {
            exception.stackTrace[0] = StackTraceElement("", "", "", 1)
        }
    }

    @Test
    fun testWithMesssageAndCause() {
        val exception =
            APIClientException(
                message = errorMessage,
                cause = illegalStateException,
            )
        assertEquals(errorMessage, exception.message)
        val cause = assertNotNull(exception.cause)
        assertEquals(IllegalStateException::class, cause::class)
    }

    @Test
    fun testWithCause() {
        val exception =
            APIClientException(
                cause = illegalStateException,
            )
        val cause = assertNotNull(exception.cause)
        assertEquals(IllegalStateException::class, cause::class)
    }

    @Test
    fun testWithMesssage() {
        val exception =
            APIClientException(
                message = errorMessage,
            )
        assertEquals(errorMessage, exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun testOnlyContructor() {
        val exception = APIClientException()
        assertNull(exception.message)
        assertNull(exception.cause)
    }
}
