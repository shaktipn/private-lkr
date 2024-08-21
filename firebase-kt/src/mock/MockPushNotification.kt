package com.suryadigital.leo.firebasekt.mock

import com.suryadigital.leo.firebasekt.NotificationInput
import com.suryadigital.leo.firebasekt.PushNotification
import com.suryadigital.leo.testUtils.ResultGenerator
import java.lang.IllegalStateException

/**
 * Mock implementation used by test cases to simulate a [PushNotification] implemenation.
 */
@Suppress("Unused") // TODO: Remove once test cases are implemented: https://surya-digital.atlassian.net/browse/ST-532
class MockPushNotification : PushNotification {
    private var sendResultGenerator: ResultGenerator<String>? = null

    /**
     * Set the mock functionality for [PushNotification.send].
     */
    fun setSendResultGenerator(resultGenerator: ResultGenerator<String>) {
        synchronized(this) {
            sendResultGenerator = resultGenerator
        }
    }

    override suspend fun send(notification: NotificationInput) {
        when (sendResultGenerator) {
            is ResultGenerator.Exception -> throw (sendResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("sendResultGenerator is not set on MockPushNotification.")
            else -> throw IllegalStateException("PushNotification.send does not return a value.")
        }
    }
}
