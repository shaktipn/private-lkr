package com.suryadigital.leo.kedwig

internal class NoOpLogger : Logger {
    override fun debug(
        throwable: Throwable?,
        message: () -> String,
    ) {
    }

    override fun debug(throwable: Throwable) {
    }

    override fun info(
        throwable: Throwable?,
        message: () -> String,
    ) {
    }

    override fun info(throwable: Throwable) {
    }

    override fun warn(
        throwable: Throwable?,
        message: () -> String,
    ) {
    }

    override fun warn(throwable: Throwable) {
    }

    override fun error(
        throwable: Throwable?,
        message: () -> String,
    ) {
    }

    override fun error(throwable: Throwable) {
    }
}
