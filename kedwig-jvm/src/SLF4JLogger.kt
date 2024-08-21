package com.suryadigital.leo.kedwig.jvm

import com.suryadigital.leo.kedwig.Logger

/**
 * SLF4J implementation for the [Logger] interface.
 */
class SLF4JLogger(private val logger: org.slf4j.Logger) : Logger {
    override fun debug(
        throwable: Throwable?,
        message: () -> String,
    ) {
        if (logger.isDebugEnabled) {
            logger.debug(message(), throwable)
        }
    }

    override fun debug(throwable: Throwable) {
        if (logger.isDebugEnabled) {
            logger.debug(throwable.message, throwable)
        }
    }

    override fun info(
        throwable: Throwable?,
        message: () -> String,
    ) {
        if (logger.isInfoEnabled) {
            logger.info(message(), throwable)
        }
    }

    override fun info(throwable: Throwable) {
        if (logger.isInfoEnabled) {
            logger.info(throwable.message, throwable)
        }
    }

    override fun warn(
        throwable: Throwable?,
        message: () -> String,
    ) {
        if (logger.isWarnEnabled) {
            logger.warn(message(), throwable)
        }
    }

    override fun warn(throwable: Throwable) {
        if (logger.isWarnEnabled) {
            logger.warn(throwable.message, throwable)
        }
    }

    override fun error(
        throwable: Throwable?,
        message: () -> String,
    ) {
        if (logger.isErrorEnabled) {
            logger.error(message(), throwable)
        }
    }

    override fun error(throwable: Throwable) {
        if (logger.isErrorEnabled) {
            logger.error(throwable.message, throwable)
        }
    }
}
