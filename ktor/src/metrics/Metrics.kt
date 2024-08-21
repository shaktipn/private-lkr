package com.suryadigital.leo.ktor.metrics

import com.suryadigital.leo.inlineLogger.getInlineLogger
import java.time.Duration

/**
 * Use [Metrics] class to measure how your code is performing.
 * Your web framework should create one instance of this class per request.
 * This class exposes the data it has captured through the request cycle, and allows you to post it either to a log message, or to an APM system.
 */
class Metrics(
    private val _timers: MutableMap<String, Timer> = mutableMapOf(),
) {
    /**
     * Measures the performance of the code.
     *
     * @param identifier should follow camel case notation and should not contain any spaces.
     * @param block function for which the performance should be measured.
     */
    fun <R> syncTimed(
        identifier: String,
        block: () -> R,
    ): R {
        val timer = _timers.getOrPut(identifier) { Timer(identifier) }
        timer.startTimer()
        try {
            return block()
        } finally {
            timer.stopTimer()
        }
    }

    /**
     * Measures the performance of the code.
     *
     * @param identifier should follow camel case notation and should not contain any spaces.
     * @param block function for which the performance should be measured.
     */
    suspend fun <R> timed(
        identifier: String,
        block: suspend () -> R,
    ): R {
        val timer = _timers.getOrPut(identifier) { Timer(identifier) }
        timer.startTimer()
        try {
            return block()
        } finally {
            timer.stopTimer()
        }
    }

    /**
     * Starts the timer for the given identifier.
     *
     * @param identifier should follow camel case notation and should not contain any spaces.
     */
    fun startTimer(identifier: String) {
        _timers.getOrPut(identifier) { Timer(identifier) }.startTimer()
    }

    /**
     * Stops the timer for the given identifier.
     *
     * @param identifier should follow camel case notation and should not contain any spaces.
     */
    fun stopTimer(identifier: String) {
        _timers.getOrPut(identifier) { Timer(identifier) }.stopTimer()
    }

    /**
     * List of all the timers currently measured by the [Metrics].
     *
     * All the timer keys are namespaced with `ms` to indicate that the time provided is in milliseconds.
     */
    val timers: List<Timer>
        get() = _timers.values.map { it }

    /**
     * @return the string representation for the [timers] in [Metrics] and its duration in decending order.
     */
    override fun toString(): String {
        return _timers
            .values
            .sortedByDescending(Timer::duration)
            .joinToString(" ") {
                "ms${it.identifier[0].uppercaseChar() + it.identifier.substring(1)}=${it.duration.toMillis()}"
            }
    }

    /**
     * This class is used to keep track of how much time a certain task has taken.
     *
     * @property identifier unique id which denotes the function for which the time is being tracked.
     */
    class Timer(
        val identifier: String,
        private var startTime: Long? = null,
        private var _duration: Long = 0,
        private var _count: Int = 0,
    ) {
        /**
         * Starts the timer.
         */
        fun startTimer() {
            startTime = System.nanoTime()
        }

        /**
         * Stops the timer.
         */
        fun stopTimer() {
            val startTime = startTime
            if (startTime != null) {
                _duration += System.nanoTime() - startTime
                _count += 1
            } else {
                logger.warn { "Stopped timer before starting it name=$identifier" }
            }
            this.startTime = null
        }

        /**
         * Total duration in nanoseconds between the [startTime] and the stop time.
         */
        val duration: Duration
            get() = Duration.ofNanos(_duration)

        /**
         * Number of times the timer has been stopped.
         */
        @Suppress("Unused") // TODO: Remove once test cases are implemented: https://surya-digital.atlassian.net/browse/ST-536
        val count: Int
            get() = _count

        /**
         * Checks for the equality of [Timer].
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Timer

            return identifier == other.identifier
        }

        /**
         * @return a hash code for [Timer] object.
         */
        override fun hashCode(): Int {
            return identifier.hashCode()
        }

        /**
         * @return the string representation of the [identifier] and its [duration].
         */
        override fun toString(): String {
            return "$identifier=${duration.toMillis()}"
        }
    }
}

private val logger = getInlineLogger(Metrics::class)
