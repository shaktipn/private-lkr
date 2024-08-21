package com.suryadigital.leo.awskt.sns

import com.suryadigital.leo.awskt.exceptions.InvalidMessageIdException
import kotlin.jvm.Throws

/**
 * Interface defined for performing common SNS operations.
 */
interface SNSClient {
    /**
     * Send SMS using async AWS SNS Client.
     *
     * @param message text to be sent.
     * @param phoneNumber phone number in E.164 format.
     * @param smsType [SMSType] to be sent. It can either be [SMSType.Transactional] or [SMSType.Promotional].
     *
     * Example:
     * ```
     * val snsClient by inject<SNSClient>()
     * snsClient.sendSMS(
     *      message = "Demo message",
     *      phoneNumber = "+919686920446",
     *      smsType = SMSType.Transactional,
     * )
     * ```
     *
     * @throws SendSMSFailedException if there is any IO related failure, failure to get credentials or any error in SNS AWS service.
     * @throws InvalidMessageIdException if the response does not contain messageId.
     */
    @Throws(SendSMSFailedException::class, InvalidMessageIdException::class)
    suspend fun sendSMS(
        message: String,
        phoneNumber: String,
        smsType: SMSType,
    ): String
}
