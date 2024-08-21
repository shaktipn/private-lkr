package com.suryadigital.leo.inlineLogger

// Inspired by: https://github.com/michaelbull/kotlin-inline-logger

// Copyright (c) 2019 Michael Bull (https://www.michael-bull.com)
//
// Permission to use, copy, modify, and/or distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
// ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
// OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

/**
 * A logger facilitating lazily-evaluated log calls via Kotlin's inline classes & functions.
 *
 * @property delegate logger implementation to which the functionality be delegated to.
 */
@JvmInline
@Suppress("unused")
value class InlineLogger(val delegate: Logger) {
    /**
     * Name of the logger class.
     */
    val name: String get() = delegate.name

    /**
     * Determines if the `TRACE` logging is enabled.
     */
    val isTraceEnabled: Boolean get() = delegate.isTraceEnabled

    /**
     * Take [Marker] data into account while determining if `TRACE` is enabled.
     */
    fun isTraceEnabled(marker: Marker?): Boolean = delegate.isTraceEnabled(marker)

    /**
     * Determines if the `DEBUG` logging is enabled.
     */
    val isDebugEnabled: Boolean get() = delegate.isDebugEnabled

    /**
     * Take [Marker] data into account while determining if `DEBUG` is enabled.
     */
    fun isDebugEnabled(marker: Marker?): Boolean = delegate.isDebugEnabled(marker)

    /**
     * Determines if the `INFO` logging is enabled.
     */
    val isInfoEnabled: Boolean get() = delegate.isInfoEnabled

    /**
     * Take [Marker] data into account while determining if `INFO` is enabled.
     */
    fun isInfoEnabled(marker: Marker?): Boolean = delegate.isInfoEnabled(marker)

    /**
     * Determines if the `WARN` logging is enabled.
     */
    val isWarnEnabled: Boolean get() = delegate.isWarnEnabled

    /**
     * Take [Marker] data into account while determining if `WARN` is enabled.
     */
    fun isWarnEnabled(marker: Marker?): Boolean = delegate.isWarnEnabled(marker)

    /**
     * Determines if the `ERROR` logging is enabled.
     */
    val isErrorEnabled: Boolean get() = delegate.isErrorEnabled

    /**
     * Take [Marker] data into account while determining if `ERROR` is enabled.
     */
    fun isErrorEnabled(marker: Marker?): Boolean = delegate.isErrorEnabled(marker)

    /**
     * Inline implementation for `TRACE` logging.
     *
     * @param msg message that needs to be logged.
     */
    inline fun trace(msg: () -> Any?) {
        if (isTraceEnabled) {
            delegate.trace(msg().toString())
        }
    }

    /**
     * Inline implementation for `TRACE` logging.
     *
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun trace(
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isTraceEnabled) {
            delegate.trace(msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `TRACE` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param msg message that needs to be logged.
     */
    inline fun trace(
        marker: Marker?,
        msg: () -> Any?,
    ) {
        if (isTraceEnabled(marker)) {
            delegate.trace(marker, msg().toString())
        }
    }

    /**
     * Inline implementation for `TRACE` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun trace(
        marker: Marker?,
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isTraceEnabled(marker)) {
            delegate.trace(marker, msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `DEBUG` logging.
     *
     * @param msg message that needs to be logged.
     */
    inline fun debug(msg: () -> Any?) {
        if (isDebugEnabled) {
            delegate.debug(msg().toString())
        }
    }

    /**
     * Inline implementation for `DEBUG` logging.
     *
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun debug(
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isDebugEnabled) {
            delegate.debug(msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `DEBUG` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param msg message that needs to be logged.
     */
    inline fun debug(
        marker: Marker?,
        msg: () -> Any?,
    ) {
        if (isDebugEnabled(marker)) {
            delegate.debug(marker, msg().toString())
        }
    }

    /**
     * Inline implementation for `DEBUG` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun debug(
        marker: Marker?,
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isDebugEnabled(marker)) {
            delegate.debug(marker, msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `INFO` logging.
     *
     * @param msg message that needs to be logged.
     */
    inline fun info(msg: () -> Any?) {
        if (isInfoEnabled) {
            delegate.info(msg().toString())
        }
    }

    /**
     * Inline implementation for `INFO` logging.
     *
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun info(
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isInfoEnabled) {
            delegate.info(msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `INFO` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param msg message that needs to be logged.
     */
    inline fun info(
        marker: Marker?,
        msg: () -> Any?,
    ) {
        if (isInfoEnabled(marker)) {
            delegate.info(marker, msg().toString())
        }
    }

    /**
     * Inline implementation for `INFO` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun info(
        marker: Marker?,
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isInfoEnabled(marker)) {
            delegate.info(marker, msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `WARN` logging.
     *
     * @param msg message that needs to be logged.
     */
    inline fun warn(msg: () -> Any?) {
        if (isWarnEnabled) {
            delegate.warn(msg().toString())
        }
    }

    /**
     * Inline implementation for `WARN` logging.
     *
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun warn(
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isWarnEnabled) {
            delegate.warn(msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `WARN` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param msg message that needs to be logged.
     */
    inline fun warn(
        marker: Marker?,
        msg: () -> Any?,
    ) {
        if (isWarnEnabled(marker)) {
            delegate.warn(marker, msg().toString())
        }
    }

    /**
     * Inline implementation for `WARN` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun warn(
        marker: Marker?,
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isWarnEnabled(marker)) {
            delegate.warn(marker, msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `ERROR` logging.
     *
     * @param msg message that needs to be logged.
     */
    inline fun error(msg: () -> Any?) {
        if (isErrorEnabled) {
            delegate.error(msg().toString())
        }
    }

    /**
     * Inline implementation for `ERROR` logging.
     *
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun error(
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isErrorEnabled) {
            delegate.error(msg().toString(), t)
        }
    }

    /**
     * Inline implementation for `ERROR` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param msg message that needs to be logged.
     */
    inline fun error(
        marker: Marker?,
        msg: () -> Any?,
    ) {
        if (isErrorEnabled(marker)) {
            delegate.error(marker, msg().toString())
        }
    }

    /**
     * Inline implementation for `ERROR` logging.
     *
     * @param marker objects used to highlight some custom information to enrich the log statements.
     * @param t exception that occurred before the message was logged, if any.
     * @param msg message that needs to be logged.
     */
    inline fun error(
        marker: Marker?,
        t: Throwable?,
        msg: () -> Any?,
    ) {
        if (isErrorEnabled(marker)) {
            delegate.error(marker, msg().toString(), t)
        }
    }
}

/**
 * Function to get the [InlineLogger] based on the class from which the function is called.
 *
 * @return [InlineLogger] object derived by using [LoggerFactory] as a delegate.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun getInlineLogger(): InlineLogger {
    val delegate = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    return InlineLogger(delegate)
}

/**
 * Function to get the [InlineLogger] based on the name which acts as an identifier for the logger.
 *
 * @param name name defined for the logger.
 *
 * @return [InlineLogger] object derived by using [LoggerFactory] as a delegate.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun getInlineLogger(name: String): InlineLogger {
    val delegate = LoggerFactory.getLogger(name)
    return InlineLogger(delegate)
}

/**
 * Function to get the [InlineLogger] based on the class which is passed as a parameter.
 *
 * @param clazz class for which the logger should be defined.
 *
 * @return [InlineLogger] object derived by using [LoggerFactory] as a delegate.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> getInlineLogger(clazz: KClass<T>): InlineLogger {
    val delegate = LoggerFactory.getLogger(clazz.java)
    return InlineLogger(delegate)
}
