import com.suryadigital.leo.firebasekt.credentialProvider.GoogleServiceCredentialProvider
import com.suryadigital.leo.testUtils.MockAsyncAPIClient
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AbstractTest {
    companion object {
        internal val config: Config = ConfigFactory.load("test.conf")
        internal val serverCredentialsFilePath = config.getString("firebase.serverCredentialsFilePath")
        internal val projectBaseUrl = config.getString("firebase.projectBaseUrl")
    }

    @BeforeTest
    fun testSetup() {
        val testModule =
            module {
                single {
                    Json {
                        isLenient = false
                        prettyPrint = true
                    }
                }
                single { config }
                single { GoogleServiceCredentialProvider(Mocks.mockGoogleAPIClient) }
            }
        startKoin {
            modules(testModule)
        }
    }

    @AfterTest
    fun testClean() {
        stopKoin()
    }
}

internal object Mocks {
    internal val mockGoogleAPIClient = MockAsyncAPIClient()
    internal val mockFirebaseClient = MockAsyncAPIClient()
    internal const val PUBLIC_KEY_URL_MOCK = "https://www.googleapis.com/robot/v1/metadata/x509/mock.iam.gserviceaccount.com"
}

internal const val FIREBASE_PROJECT_ID = "test-project-abc123"
internal const val GOOGLE_API_BASE_URL = "https://www.googleapis.com"
internal const val FCM_BASE_URL = "https://fcm.googleapis.com"
