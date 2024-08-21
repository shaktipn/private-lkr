package com.suryadigital.leo.awskt.mock

import com.suryadigital.leo.awskt.ses.Email
import com.suryadigital.leo.awskt.ses.SESClient
import com.suryadigital.leo.testUtils.ResultGenerator
import java.lang.IllegalStateException

/**
 * Implementation for [SESClient] to mock its functionality.
 */
@Suppress("Unused")
class MockSESClient : SESClient {
    private var sendEmailResultGenerator: ResultGenerator<String>? = null

    /**
     * Set the mock functionality for [SESClient.sendEmail].
     */
    fun setSendEmailResultGenerator(resultGenerator: ResultGenerator<String>) {
        synchronized(this) {
            sendEmailResultGenerator = resultGenerator
        }
    }

    override suspend fun sendEmail(email: Email): String {
        return when (sendEmailResultGenerator) {
            is ResultGenerator.Response -> (sendEmailResultGenerator as ResultGenerator.Response<String>).value
            is ResultGenerator.Exception -> throw (sendEmailResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("sendEmailResultGenerator is not set on MockSESClient")
        }
    }
}
