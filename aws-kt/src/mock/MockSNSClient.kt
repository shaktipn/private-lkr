package com.suryadigital.leo.awskt.mock

import com.suryadigital.leo.awskt.sns.SMSType
import com.suryadigital.leo.awskt.sns.SNSClient
import com.suryadigital.leo.testUtils.ResultGenerator
import java.lang.IllegalStateException

/**
 * Implementation for [SNSClient] to mock its functionality.
 */
@Suppress("Unused")
class MockSNSClient : SNSClient {
    private var sendSMSResultGenerator: ResultGenerator<String>? = null

    /**
     * Set the mock functionality for [SNSClient.sendSMS].
     */
    fun setSendSMSResultGenerator(resultGenerator: ResultGenerator<String>) {
        synchronized(this) {
            sendSMSResultGenerator = resultGenerator
        }
    }

    override suspend fun sendSMS(
        message: String,
        phoneNumber: String,
        smsType: SMSType,
    ): String {
        return when (sendSMSResultGenerator) {
            is ResultGenerator.Response -> (sendSMSResultGenerator as ResultGenerator.Response<String>).value
            is ResultGenerator.Exception -> throw (sendSMSResultGenerator as ResultGenerator.Exception<*>).value
            null -> throw IllegalStateException("sendSMSResultGenerator is not set on MockSNSClient")
        }
    }
}
