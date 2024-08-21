import com.suryadigital.leo.firebasekt.FCMErrorCode
import com.suryadigital.leo.firebasekt.NotificationCategory
import com.suryadigital.leo.firebasekt.NotificationInput
import com.suryadigital.leo.firebasekt.NotificationPriority
import com.suryadigital.leo.firebasekt.PushNotificationImpl
import com.suryadigital.leo.firebasekt.SendNotificationFailedException
import com.suryadigital.leo.firebasekt.UnregisteredPushTokenException
import com.suryadigital.leo.firebasekt.credentialProvider.CredentialProviderException
import com.suryadigital.leo.firebasekt.credentialProvider.GoogleServiceCredentialProvider
import com.suryadigital.leo.kedwig.AsyncAPIClient
import com.suryadigital.leo.kedwig.Response
import com.suryadigital.leo.kedwig.headers
import com.suryadigital.leo.testUtils.ResultGenerator
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.io.File
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PushNotificationTests : AbstractTest(), KoinComponent {
    @Test
    fun testPositiveSendToSingleDevice() {
        loadSuccessResponseMock()
        runBlocking {
            PushNotificationImpl(
                serverCredentialsFilePath,
                FIREBASE_PROJECT_ID,
                Mocks.mockFirebaseClient,
                Mocks.mockGoogleAPIClient,
            ).send(
                NotificationInput(
                    pushToken = "dLdx9PMbetA:APA91bHW",
                    title = "Test Title",
                    body = "Test notification",
                    deepLink = null,
                    category = NotificationCategory.TRANSACTIONS,
                    priority = NotificationPriority.CRITICAL,
                ),
            )
        }
    }

    @Test
    fun testInvalidCredentialsFileThrowsCredentialProviderException() {
        val googleServiceCredentialProvider by inject<GoogleServiceCredentialProvider>()
        runBlocking {
            val error =
                assertFailsWith<CredentialProviderException> {
                    googleServiceCredentialProvider.getAuthToken(
                        "testresources/server-creds-malformed-mock.json",
                        "",
                    )
                }
            error.message?.let { assertTrue(it.contains("Unable to parse credentials file")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testInvalidPrivateKeyThrowsCredentialProviderException() {
        val googleServiceCredentialProvider by inject<GoogleServiceCredentialProvider>()
        runBlocking {
            val error =
                assertFailsWith<CredentialProviderException> {
                    googleServiceCredentialProvider.getAuthToken(
                        "testresources/server-creds-invalid-private-key-mock.json",
                        "",
                    )
                }
            error.message?.let { assertTrue(it.contains("Unable to parse private key")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testInvalidCredentialsFilePathThrowsCredentialProviderException() {
        val googleServiceCredentialProvider by inject<GoogleServiceCredentialProvider>()
        runBlocking {
            val error =
                assertFailsWith<CredentialProviderException> {
                    googleServiceCredentialProvider.getAuthToken(
                        "testresources/server-creds-not-exists.json",
                        "",
                    )
                }
            error.message?.let { assertTrue(it.contains("Unable to read Credential file")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testInvalidPublicKeyResponseStatusCodeThrowsCredentialProviderException() {
        val googleServiceCredentialProvider by inject<GoogleServiceCredentialProvider>()
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 500,
                    headers = headers { },
                    body = ByteArray(0),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        loadKoinModules(
            module {
                single<AsyncAPIClient> { Mocks.mockGoogleAPIClient }
            },
        )
        runBlocking {
            val error =
                assertFailsWith<CredentialProviderException> {
                    googleServiceCredentialProvider.getAuthToken(
                        "testresources/server-creds-mock.json",
                        "",
                    )
                }
            error.message?.let { assertTrue(it.contains("Cannot acquire public key from public key URL")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testInvalidPublicKeyResponseBodyThrowsCredentialProviderException() {
        val googleServiceCredentialProvider by inject<GoogleServiceCredentialProvider>()
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = "{InvalidJSON}".toByteArray(),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        loadKoinModules(
            module {
                single<AsyncAPIClient> { Mocks.mockGoogleAPIClient }
            },
        )
        runBlocking {
            val error =
                assertFailsWith<CredentialProviderException> {
                    googleServiceCredentialProvider.getAuthToken(
                        "testresources/server-creds-mock.json",
                        "",
                    )
                }
            error.message?.let { assertTrue(it.contains("Unable to parse response body from public key URL")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testInvalidPublicKeyCertificateThrowsCredentialProviderException() {
        val googleServiceCredentialProvider by inject<GoogleServiceCredentialProvider>()
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("public-key-response-invalid-cert.json"),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        loadKoinModules(
            module {
                single<AsyncAPIClient> { Mocks.mockGoogleAPIClient }
            },
        )
        runBlocking {
            val error =
                assertFailsWith<CredentialProviderException> {
                    googleServiceCredentialProvider.getAuthToken(
                        "testresources/server-creds-mock.json",
                        "",
                    )
                }
            error.message?.let { assertTrue(it.contains("Unable to parse public key")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testInvalidAuthTokenResponseStatusCodeThrowsCredentialProviderException() {
        val googleServiceCredentialProvider by inject<GoogleServiceCredentialProvider>()
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("public-key-response-mock.json"),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 500,
                    headers = headers { },
                    body = ByteArray(0),
                    url = AUTH_TOKEN_REQUEST_URI.toHttpUrl(),
                ),
            ),
            URI(AUTH_TOKEN_REQUEST_URI).path,
        )
        loadKoinModules(
            module {
                factory<AsyncAPIClient> { (baseUrl: String, _: Boolean?) ->
                    when (baseUrl) {
                        GOOGLE_API_BASE_URL -> {
                            Mocks.mockGoogleAPIClient
                        }
                        else -> {
                            throw IllegalStateException("URL isn't configured in mock")
                        }
                    }
                }
            },
        )
        runBlocking {
            val error =
                assertFailsWith<CredentialProviderException> {
                    googleServiceCredentialProvider.getAuthToken(
                        "testresources/server-creds-mock.json",
                        "",
                    )
                }
            error.message?.let { assertTrue(it.contains("Unable acquire auth token")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testSendToSingleDeviceCredentialProviderErrorThrowsSendNotificationFailedException() {
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 500,
                    headers = headers { },
                    body = ByteArray(0),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        loadKoinModules(
            module {
                single<AsyncAPIClient> { Mocks.mockGoogleAPIClient }
            },
        )
        runBlocking {
            val error =
                assertFailsWith<SendNotificationFailedException> {
                    PushNotificationImpl(
                        serverCredentialsFilePath,
                        FIREBASE_PROJECT_ID,
                        Mocks.mockFirebaseClient,
                        Mocks.mockGoogleAPIClient,
                    ).send(
                        NotificationInput(
                            pushToken = "dLdx9PMbetA:APA91bHW",
                            title = "Test Title",
                            body = "Test notification",
                            deepLink = null,
                            category = NotificationCategory.TRANSACTIONS,
                            priority = NotificationPriority.CRITICAL,
                        ),
                    )
                }
            error.message?.let { assertTrue(it.contains("Unable to procure auth token")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
        }
    }

    @Test
    fun testSendReturnsInvalidStatusCodeThrowsUnregisteredPushTokenException() {
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("auth-token-response-mock.json"),
                    url = AUTH_TOKEN_REQUEST_URI.toHttpUrl(),
                ),
            ),
            URI(AUTH_TOKEN_REQUEST_URI).path,
        )
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("public-key-response-mock.json"),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        Mocks.mockFirebaseClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 404,
                    headers = headers { },
                    body = getMockResponseForPushNotification(PushNotificationUseCase.SINGLE_DEVICE_UNREGISTERED),
                    url = projectBaseUrl.toHttpUrl(),
                ),
            ),
        )
        loadKoinModules(
            module {
                factory<AsyncAPIClient> { (baseUrl: String, _: Boolean?) ->
                    when (baseUrl) {
                        GOOGLE_API_BASE_URL -> {
                            Mocks.mockGoogleAPIClient
                        }
                        FCM_BASE_URL -> {
                            Mocks.mockFirebaseClient
                        }
                        else -> {
                            throw IllegalStateException("URL isn't configured in mock")
                        }
                    }
                }
            },
        )
        runBlocking {
            assertFailsWith<UnregisteredPushTokenException> {
                PushNotificationImpl(
                    serverCredentialsFilePath,
                    FIREBASE_PROJECT_ID,
                    Mocks.mockFirebaseClient,
                    Mocks.mockGoogleAPIClient,
                ).send(
                    NotificationInput(
                        pushToken = "dLdx9PMbetA:APA91bHW",
                        title = "Test Title",
                        body = "Test notification",
                        deepLink = null,
                        category = NotificationCategory.TRANSACTIONS,
                        priority = NotificationPriority.CRITICAL,
                    ),
                )
            }
        }
    }

    @Test
    fun testSendReturnsInvalidStatusCodeThrowsSendNotificationFailedException() {
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("auth-token-response-mock.json"),
                    url = AUTH_TOKEN_REQUEST_URI.toHttpUrl(),
                ),
            ),
            URI(AUTH_TOKEN_REQUEST_URI).path,
        )
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("public-key-response-mock.json"),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        Mocks.mockFirebaseClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 429,
                    headers = headers { },
                    body = getMockResponseForPushNotification(PushNotificationUseCase.SINGLE_DEVICE_QUOTA_EXCEEDED),
                    url = projectBaseUrl.toHttpUrl(),
                ),
            ),
        )
        loadKoinModules(
            module {
                factory<AsyncAPIClient> { (baseUrl: String, _: Boolean?) ->
                    when (baseUrl) {
                        GOOGLE_API_BASE_URL -> {
                            Mocks.mockGoogleAPIClient
                        }
                        FCM_BASE_URL -> {
                            Mocks.mockFirebaseClient
                        }
                        else -> {
                            throw IllegalStateException("URL isn't configured in mock")
                        }
                    }
                }
            },
        )
        runBlocking {
            val error =
                assertFailsWith<SendNotificationFailedException> {
                    PushNotificationImpl(
                        serverCredentialsFilePath,
                        FIREBASE_PROJECT_ID,
                        Mocks.mockFirebaseClient,
                        Mocks.mockGoogleAPIClient,
                    ).send(
                        NotificationInput(
                            pushToken = "dLdx9PMbetA:APA91bHW",
                            title = "Test Title",
                            body = "Test notification",
                            deepLink = null,
                            category = NotificationCategory.TRANSACTIONS,
                            priority = NotificationPriority.CRITICAL,
                        ),
                    )
                }
            error.message?.let { assertTrue(it.contains("Send push notification to device failed")) } ?: throw IllegalStateException("Message for error $error cannot be null.")
            assertEquals(error.getErrorCode(), FCMErrorCode.QUOTA_EXCEEDED)
        }
    }

    private fun loadSuccessResponseMock() {
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("auth-token-response-mock.json"),
                    url = AUTH_TOKEN_REQUEST_URI.toHttpUrl(),
                ),
            ),
            URI(AUTH_TOKEN_REQUEST_URI).path,
        )
        Mocks.mockGoogleAPIClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = readBytesFromFile("public-key-response-mock.json"),
                    url = Mocks.PUBLIC_KEY_URL_MOCK.toHttpUrl(),
                ),
            ),
            PUBLIC_KEY_REQUEST_PATH,
        )
        Mocks.mockFirebaseClient.setAsyncResponse(
            ResultGenerator.Response(
                Response(
                    statusCode = 200,
                    headers = headers { },
                    body = getMockResponseForPushNotification(PushNotificationUseCase.SINGLE_DEVICE_SUCCESS),
                    url = projectBaseUrl.toHttpUrl(),
                ),
            ),
        )
        loadKoinModules(
            module {
                factory<AsyncAPIClient> { (baseUrl: String, _: Boolean?) ->
                    when (baseUrl) {
                        GOOGLE_API_BASE_URL -> {
                            Mocks.mockGoogleAPIClient
                        }
                        FCM_BASE_URL -> {
                            Mocks.mockFirebaseClient
                        }
                        else -> {
                            throw IllegalStateException("URL isn't configured in mock.")
                        }
                    }
                }
            },
        )
    }

    private fun getMockResponseForPushNotification(useCase: PushNotificationUseCase) =
        readBytesFromFile(
            when (useCase) {
                PushNotificationUseCase.SINGLE_DEVICE_SUCCESS -> "fcm-send-success-response-mock.json"
                PushNotificationUseCase.SINGLE_DEVICE_UNREGISTERED -> "fcm-send-error-unregistered-response-mock.json"
                PushNotificationUseCase.SINGLE_DEVICE_QUOTA_EXCEEDED -> "fcm-send-error-quota-exceeded-response-mock.json"
            },
        )

    private fun readBytesFromFile(fileName: String) = File("testresources/$fileName").readBytes()
}

private enum class PushNotificationUseCase {
    SINGLE_DEVICE_SUCCESS,
    SINGLE_DEVICE_UNREGISTERED,
    SINGLE_DEVICE_QUOTA_EXCEEDED,
}

private const val PUBLIC_KEY_REQUEST_PATH = "/robot/v1/metadata/x509/mock.iam.gserviceaccount.com"
private const val AUTH_TOKEN_REQUEST_URI = "https://www.googleapis.com/oauth2/v4/token"
