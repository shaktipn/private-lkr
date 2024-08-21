package com.suryadigital.leo.firebasekt

import com.suryadigital.leo.firebasekt.credentialProvider.AuthToken
import com.suryadigital.leo.firebasekt.credentialProvider.CredentialProviderException
import com.suryadigital.leo.firebasekt.credentialProvider.GoogleServiceCredentialProvider
import com.suryadigital.leo.inlineLogger.getInlineLogger
import com.suryadigital.leo.kedwig.AsyncAPIClient
import com.suryadigital.leo.kedwig.Method
import com.suryadigital.leo.kedwig.Response
import com.suryadigital.leo.kedwig.request
import com.suryadigital.leo.kotlinxserializationjson.LeoJSONException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.serializer
import java.time.Instant
import kotlin.jvm.Throws

/**
 * Send push notifications to iOS and Android devices.
 *
 * @property serverCredentialsFilePath path to server credentials file of the Firebase project. This file must be generated from the Firebase console.
 * @property firebaseProjectId unique identifier of the Firebase project.
 * @property firebaseClient [AsyncAPIClient] which contains the [baseURL](https://fcm.googleapis.com).
 * @property googleAPIClient [AsyncAPIClient] which contains the [baseURL](https://www.googleapis.com).
 */
class PushNotificationImpl(
    private val serverCredentialsFilePath: String,
    private val firebaseProjectId: String,
    private val firebaseClient: AsyncAPIClient,
    private val googleAPIClient: AsyncAPIClient,
) : PushNotification {
    private val logger = getInlineLogger(PushNotificationImpl::class)
    private val googleServiceCredentialProvider: GoogleServiceCredentialProvider =
        GoogleServiceCredentialProvider(googleAPIClient)
    private var authToken: AuthToken? = null
    private val mutex = Mutex()

    /**
     * Sends push notification to a single device.
     *
     * @param notification [NotificationInput] type which consists of data required to send a notification to the given device.
     */
    @Throws(SendNotificationFailedException::class, UnregisteredPushTokenException::class)
    override suspend fun send(notification: NotificationInput) {
        mutex.withLock {
            updateAuthToken()
        }
        val sendNotificationResponse =
            authToken?.let { getSendNotificationResponse(notification, it.token) }
                ?: throw IllegalStateException("Auth Token cannot be null.")
        if (sendNotificationResponse.statusCode != 200) {
            logger.debug { "Send push notification to device failed. Status Code : ${sendNotificationResponse.statusCode}, Body : ${sendNotificationResponse.stringBody}." }
            val errorCode = getFCMErrorCodeFromResponseBody(sendNotificationResponse.stringBody)
            if (errorCode == FCMErrorCode.UNREGISTERED) {
                throw UnregisteredPushTokenException()
            }
            throw SendNotificationFailedException(
                message = "Send push notification to device failed.",
                errorCode = errorCode,
            )
        }
    }

    private fun getFCMErrorCodeFromResponseBody(body: String): FCMErrorCode? {
        return try {
            val fcmErrorDetail =
                Json.decodeFromString<ErrorResponseDTO>(body).error.details.firstOrNull { it.type == FCM_ERROR_TYPE }
            if (fcmErrorDetail != null) {
                FCMErrorCode.valueOf(fcmErrorDetail.errorCode)
            } else {
                null
            }
        } catch (e: SerializationException) {
            throw SendNotificationFailedException("Unable to parse response body.")
        } catch (e: LeoJSONException) {
            throw SendNotificationFailedException("Unable to parse error code in response.")
        }
    }

    private suspend fun updateAuthToken() {
        try {
            if (authToken == null ||
                (authToken?.expiresAt ?: throw IllegalStateException("AuthToken cannot be null.")) < Instant.now().plusSeconds(5)
            ) {
                authToken = googleServiceCredentialProvider.getAuthToken(serverCredentialsFilePath, FIREBASE_AUTH_SCOPE)
            }
        } catch (e: CredentialProviderException) {
            throw SendNotificationFailedException("Unable to procure auth token.", e)
        }
    }

    private suspend fun getSendNotificationResponse(
        notification: NotificationInput,
        serverAuthToken: String,
    ): Response {
        val serializer = serializer<SendNotificationRequestDTO>()
        return try {
            firebaseClient.sendRequestAsync(
                request {
                    path = "/v1/projects/$firebaseProjectId/messages:send"
                    method = Method.POST
                    omitDefaultHeaders = true
                    headers {
                        header("Authorization", "Bearer $serverAuthToken")
                        header("Content-Type", "application/json")
                    }
                    body(
                        Json.encodeToString(
                            serializer,
                            SendNotificationRequestDTO(
                                message =
                                    SendNotificationMessageDTO(
                                        token = notification.pushToken,
                                        notification =
                                            NotificationDTO(
                                                body = notification.body,
                                                title = notification.title,
                                            ),
                                        data = notification.deepLink?.let(::NotificationDataDTO),
                                        android =
                                            AndroidNotificationConfigDTO(
                                                notification =
                                                    AndroidNotificationDTO(
                                                        getAndroidNotificationPriority(notification.priority),
                                                    ),
                                            ),
                                        apns =
                                            ApnsNotificationConfigDTO(
                                                payload =
                                                    ApnsPayloadDTO(
                                                        aps =
                                                            ApnsDTO(
                                                                interruptionLevel = getApnsNotificationPriority(notification.priority),
                                                            ),
                                                    ),
                                            ),
                                        fcmOptions = FcmOptionsDTO(notification.category.label),
                                    ),
                            ),
                        ),
                    )
                },
            )
        } catch (e: SerializationException) {
            throw SendNotificationFailedException("Unable to serialize JSON payload in request.", e)
        }
    }

    private fun getAndroidNotificationPriority(priority: NotificationPriority): AndroidNotificationPriorityDTO {
        return when (priority) {
            NotificationPriority.NORMAL -> AndroidNotificationPriorityDTO.PRIORITY_DEFAULT
            NotificationPriority.CRITICAL -> AndroidNotificationPriorityDTO.PRIORITY_HIGH
        }
    }

    private fun getApnsNotificationPriority(priority: NotificationPriority): String {
        return when (priority) {
            NotificationPriority.NORMAL -> InterruptionLevelDTO.ACTIVE.value
            NotificationPriority.CRITICAL -> InterruptionLevelDTO.TIME_SENSITIVE.value
        }
    }
}

@Serializable
private data class SendNotificationRequestDTO(
    val message: SendNotificationMessageDTO,
)

@Serializable
private data class SendNotificationMessageDTO(
    val token: String,
    val notification: NotificationDTO,
    val data: NotificationDataDTO?,
    val android: AndroidNotificationConfigDTO,
    val apns: ApnsNotificationConfigDTO,
    @SerialName("fcm_options")
    val fcmOptions: FcmOptionsDTO,
)

@Serializable
private data class NotificationDTO(
    val body: String,
    val title: String,
)

@Serializable
private data class AndroidNotificationConfigDTO(
    val notification: AndroidNotificationDTO,
)

@Serializable
private data class NotificationDataDTO(
    val deeplink: String,
)

@Serializable
private data class AndroidNotificationDTO(
    @SerialName("notification_priority")
    val notificationPriority: AndroidNotificationPriorityDTO,
    val sound: String = "default",
)

@Serializable
private enum class AndroidNotificationPriorityDTO {
    PRIORITY_DEFAULT,
    PRIORITY_HIGH,
}

@Serializable
private data class ApnsNotificationConfigDTO(
    val payload: ApnsPayloadDTO,
)

@Serializable
private data class ApnsPayloadDTO(
    val aps: ApnsDTO,
)

@Serializable
private data class ApnsDTO(
    val sound: String = "default",
    @SerialName("interruption-level")
    val interruptionLevel: String,
)

@Serializable
private enum class InterruptionLevelDTO(val value: String) {
    TIME_SENSITIVE("time-sensitive"),
    ACTIVE("active"),
}

@Serializable
private data class FcmOptionsDTO(
    @SerialName("analytics_label")
    val analyticsLabel: String,
)

@Serializable
private data class ErrorResponseDTO(
    val error: FCMErrorDTO,
)

@Serializable
private data class FCMErrorDTO(
    val code: Int,
    val message: String,
    val status: String,
    @Serializable(with = FCMErrorDetailsDTOListSerializer::class)
    val details: List<FCMErrorDetailsDTO>,
)

@Serializable
private data class FCMErrorDetailsDTO(
    @SerialName("@type")
    val type: String,
    val errorCode: String,
)

private object FCMErrorDetailsDTOListSerializer :
    JsonTransformingSerializer<List<FCMErrorDetailsDTO>>(ListSerializer(FCMErrorDetailsDTO.serializer())) {
    // If the response is not an array, then it is a single object that should be wrapped into the array
    override fun transformDeserialize(element: JsonElement): JsonElement = if (element !is JsonArray) JsonArray(listOf(element)) else element
}

private const val FIREBASE_AUTH_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
private const val FCM_ERROR_TYPE = "type.googleapis.com/google.firebase.fcm.v1.FcmError"
