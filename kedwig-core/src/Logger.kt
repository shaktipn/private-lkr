package com.suryadigital.leo.kedwig

/**
 * Interface that defines the implementation of a Logger.
 */
interface Logger {
    /**
     * Creates a log used to denote a message required for troubleshooting and diagnosing issues.
     *
     * @param throwable exception that was thrown, if any.
     * @param message message that should be logged.
     */
    fun debug(
        throwable: Throwable? = null,
        message: () -> String,
    )

    /**
     * Creates a log used to denote a message required for troubleshooting and diagnosing issues.
     *
     * @param throwable exception that was thrown.
     */
    fun debug(throwable: Throwable)

    /**
     * Creates a log used to denote a message that is used to log that an event has occurred.
     *
     * @param throwable exception that was thrown, if any.
     * @param message message that should be logged.
     */
    fun info(
        throwable: Throwable? = null,
        message: () -> String,
    )

    /**
     * Creates a log used to denote a message that is used to log that an event has occurred.
     *
     * @param throwable exception that was thrown.
     */
    fun info(throwable: Throwable)

    /**
     * Creates a log used to denote that the application has not failed, but something has happened that might disturb one of the processes.
     *
     * @param throwable exception that was thrown, if any.
     * @param message message that should be logged.
     */
    fun warn(
        throwable: Throwable? = null,
        message: () -> String,
    )

    /**
     * Creates a log used to denote that the application has not failed, but something has happened that might disturb one of the processes.
     *
     * @param throwable exception that was thrown.
     */
    fun warn(throwable: Throwable)

    /**
     * Creates a log used to denote that an error has occurred, and is preventing some functionalities of the application from working properly.
     *
     * @param throwable exception that was thrown, if any.
     * @param message message that should be logged.
     */
    fun error(
        throwable: Throwable? = null,
        message: () -> String,
    )

    /**
     * Creates a log used to denote that an error has occurred, and is preventing some functionalities of the application from working properly.
     *
     * @param throwable exception that was thrown.
     */
    fun error(throwable: Throwable)
}
