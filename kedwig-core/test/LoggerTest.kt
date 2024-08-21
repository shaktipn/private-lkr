package com.suryadigital.leo.kedwig

import kotlin.test.BeforeTest
import kotlin.test.Test

class LoggerTest {
    private lateinit var logger: DemoLogger
    private val message = "This is a debug message"
    private val throwable = IllegalStateException()

    class DemoLogger : Logger {
        var logThrowable: Throwable? = null
        var log: String? = null

        override fun debug(
            throwable: Throwable?,
            message: () -> String,
        ) {
            log = message()
            logThrowable = throwable
        }

        override fun debug(throwable: Throwable) {
            logThrowable = throwable
        }

        override fun info(
            throwable: Throwable?,
            message: () -> String,
        ) {
            log = message()
            logThrowable = throwable
        }

        override fun info(throwable: Throwable) {
            logThrowable = throwable
        }

        override fun warn(
            throwable: Throwable?,
            message: () -> String,
        ) {
            log = message()
            logThrowable = throwable
        }

        override fun warn(throwable: Throwable) {
            logThrowable = throwable
        }

        override fun error(
            throwable: Throwable?,
            message: () -> String,
        ) {
            log = message()
            logThrowable = throwable
        }

        override fun error(throwable: Throwable) {
            logThrowable = throwable
        }
    }

    @BeforeTest
    fun setup() {
        logger = DemoLogger()
    }

    @Test
    fun testDebug() {
        logger.debug(throwable) { message }
        assertLogAndThrowable(message = message, throwable = throwable)
    }

    @Test
    fun testDebugWithoutThrowable() {
        logger.debug { message }
        assertLogAndThrowable(message = message)
    }

    @Test
    fun testDebugWithoutMessage() {
        logger.debug(throwable)
        assertLogAndThrowable(throwable = throwable)
    }

    @Test
    fun testInfo() {
        logger.info(throwable) { message }
        assertLogAndThrowable(message = message, throwable = throwable)
    }

    @Test
    fun testInfoWithoutThrowable() {
        logger.info { message }
        assertLogAndThrowable(message = message)
    }

    @Test
    fun testInfoWithoutMessage() {
        logger.info(throwable)
        assertLogAndThrowable(throwable = throwable)
    }

    @Test
    fun testWarn() {
        logger.warn(throwable) { message }
        assertLogAndThrowable(message = message, throwable = throwable)
    }

    @Test
    fun testWarnWithoutThrowable() {
        logger.warn { message }
        assertLogAndThrowable(message = message)
    }

    @Test
    fun testWarnWithoutMessage() {
        logger.warn(throwable)
        assertLogAndThrowable(throwable = throwable)
    }

    @Test
    fun testError() {
        logger.error(throwable) { message }
        assertLogAndThrowable(message, throwable)
    }

    @Test
    fun testErrorWithoutThrowable() {
        logger.error { message }
        assertLogAndThrowable(message = message)
    }

    @Test
    fun testErrorWithoutMessage() {
        logger.error(throwable)
        assertLogAndThrowable(throwable = throwable)
    }

    @Test
    fun testNoOpLogger() {
        val noOpLogger = NoOpLogger()
        noOpLogger.debug(throwable) { message }
        noOpLogger.debug(throwable)
        noOpLogger.debug { message }
        noOpLogger.info(throwable) { message }
        noOpLogger.info(throwable)
        noOpLogger.info { message }
        noOpLogger.warn(throwable) { message }
        noOpLogger.warn(throwable)
        noOpLogger.warn { message }
        noOpLogger.error(throwable) { message }
        noOpLogger.error(throwable)
        noOpLogger.error { message }
    }

    private fun assertLogAndThrowable(
        message: String? = null,
        throwable: Throwable? = null,
    ) {
        assert(logger.log == message)
        assert(logger.logThrowable == throwable)
    }
}
