package com.suryadigital.leo.kedwig

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RequestBuilderExceptionTest {
    @Test
    fun testWithMesssageCauseSuppressionAndWritableStackTrace() {
        val exception =
            RequestBuilderException(
                message = "message",
                cause = IllegalStateException(),
                enableSuppression = true,
                writableStackTrace = true,
            )
        assertEquals("message", exception.message)
        val cause = assertNotNull(exception.cause)
        assertEquals(IllegalStateException::class, cause::class)
        assertEquals(0, exception.suppressedExceptions.size)
        assertNotNull(exception.stackTrace)
    }

    @Test
    fun testWithMesssageCauseSuppressionAndNoWritableStackTrace() {
        val exception =
            RequestBuilderException(
                message = "message",
                cause = IllegalStateException(),
                enableSuppression = true,
                writableStackTrace = false,
            )
        assertEquals("message", exception.message)
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
            RequestBuilderException(
                message = "message",
                cause = IllegalStateException(),
            )
        assertEquals("message", exception.message)
        val cause = assertNotNull(exception.cause)
        assertEquals(IllegalStateException::class, cause::class)
    }

    @Test
    fun testWithCause() {
        val exception =
            RequestBuilderException(
                cause = IllegalStateException(),
            )
        val cause = assertNotNull(exception.cause)
        assertEquals(IllegalStateException::class, cause::class)
    }

    @Test
    fun testWithMesssage() {
        val exception =
            RequestBuilderException(
                message = "message",
            )
        assertEquals("message", exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun testOnlyContructor() {
        val exception = RequestBuilderException()
        assertNull(exception.message)
        assertNull(exception.cause)
    }
}
