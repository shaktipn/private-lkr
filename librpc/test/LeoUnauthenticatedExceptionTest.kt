package com.suryadigital.leo.rpc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LeoUnauthenticatedExceptionTest {
    private val illegalStateException = IllegalStateException()
    private val exceptionMessage = "Unauthenticated Error"

    @Test
    fun testWithMesssageCauseSuppressionAndWritableStackTrace() {
        val exception =
            LeoUnauthenticatedException(
                message = exceptionMessage,
                cause = illegalStateException,
                enableSuppression = true,
                writableStackTrace = true,
            )
        assertEquals(exceptionMessage, exception.message)
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(IllegalStateException::class, cause::class)
        assertEquals(0, exception.suppressedExceptions.size)
        assertNotNull(exception.stackTrace)
    }

    @Test
    fun testWithMesssageCauseSuppressionAndNoWritableStackTrace() {
        val exception =
            LeoUnauthenticatedException(
                message = exceptionMessage,
                cause = illegalStateException,
                enableSuppression = true,
                writableStackTrace = false,
            )
        assertEquals(exceptionMessage, exception.message)
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(IllegalStateException::class, cause::class)
        assertEquals(0, exception.suppressedExceptions.size)
        assertFailsWith<ArrayIndexOutOfBoundsException> {
            exception.stackTrace[0] = StackTraceElement("", "", "", 1)
        }
    }

    @Test
    fun testWithMesssageAndCause() {
        val exception =
            LeoUnauthenticatedException(
                message = exceptionMessage,
                cause = illegalStateException,
            )
        assertEquals(exceptionMessage, exception.message)
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(IllegalStateException::class, cause::class)
    }

    @Test
    fun testWithCause() {
        val exception =
            LeoUnauthenticatedException(
                cause = illegalStateException,
            )
        val cause = exception.cause
        assertNotNull(cause)
        assertEquals(IllegalStateException::class, cause::class)
    }

    @Test
    fun testWithMesssage() {
        val exception =
            LeoUnauthenticatedException(
                message = exceptionMessage,
            )
        assertEquals(exceptionMessage, exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun testOnlyConstructor() {
        val exception = LeoUnauthenticatedException()
        assertNull(exception.message)
        assertNull(exception.cause)
    }
}
