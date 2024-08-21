package com.suryadigital.leo.firebasekt

import kotlin.jvm.Throws

/**
 * Interface defined for performing common push notification operations.
 */
interface PushNotification {
    /**
     * Send push notification to a single device.
     *
     * @param notification [NotificationInput] type which consists of data required to send a notification to the given device.
     *
     * @throws SendNotificationFailedException when the implementation fails to send the notification.
     */
    @Throws(SendNotificationFailedException::class)
    suspend fun send(notification: NotificationInput)
}

/**
 * Defines the metadata that needs to be passed to [PushNotification.send] method for sending the push notification.
 *
 * More information regarding Firebase Cloud Messaging REST API can be found [here](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages).
 *
 * @property pushToken registration token to which the message needs to be sent.
 * @property title title of the notification.
 * @property body body of the notification.
 * @property deepLink dynamic link to redirect the user to, based on their platform.
 * @property category [NotificationCategory] for the given notification.
 * @property priority [NotificationPriority] for the given notification.
 */
data class NotificationInput(
    val pushToken: String,
    val title: String,
    val body: String,
    val deepLink: String? = null,
    val category: NotificationCategory,
    val priority: NotificationPriority,
)

/**
 * Defines how the notification should be categorized based on the purpose of the notification.
 *
 * TODO: The enum will be refactored to remove BCN specific options like [AGENT_APPLICATION]  in [this](https://surya-digital.atlassian.net/browse/ST-566) ticket.
 *
 * @property label string identifier of the given [NotificationCategory] used to identify the category for analytics purposes.
 */
@Suppress("Unused") // TODO: Remove once test cases are implemented: https://surya-digital.atlassian.net/browse/ST-532
enum class NotificationCategory(val label: String) {
    /**
     * Denotes that the notification pertains to transaction information in BCN.
     */
    TRANSACTIONS("transactions"),

    /**
     * Denotes that the notification pertains to information regarding Agent application in BCN.
     */
    AGENT_APPLICATION("agent_application"),

    /**
     * Denotes that the notification pertains to information regarding one-time-password required for some action.
     */
    OTP("otp"),
}

/**
 * Defines how the notification should be priortized.
 */
enum class NotificationPriority {
    /**
     * Denotes that the notification is a normal notification, with default priority.
     */
    NORMAL,

    /**
     * Denotes that the notification is a critical notification, and should be handled by respective devices accordingly.
     */
    CRITICAL,
}
