package com.suryadigital.leo.awskt.sns

import com.suryadigital.leo.awskt.CONNECTION_ACQUISITION_TIMEOUT_SECONDS
import com.suryadigital.leo.awskt.Configuration
import com.suryadigital.leo.awskt.exceptions.InvalidMessageIdException
import kotlinx.coroutines.future.await
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.PublishResponse
import software.amazon.awssdk.services.sns.model.SnsException
import java.util.concurrent.CompletableFuture
import kotlin.jvm.Throws

/**
 * Implementation for the [SNSClient].
 */
class SNSClientImpl(private val configuration: Configuration) : SNSClient {
    private val snsClient: SnsAsyncClient

    init {
        snsClient = getSNSAsyncClient(configuration.region)
    }

    /**
     * Send SMS using async AWS SNS Client.
     *
     * @param message text to be sent.
     * @param phoneNumber phone number in E.164 format.
     * @param smsType [SMSType] to be sent.
     *
     * Example:
     * ```
     * val snsClient by inject<SNSClient>()
     * snsClient.sendSMS(
     *      message = "Demo message",
     *      phoneNumber = "+919686920446",
     *      smsType = SMSType.Transactional,
     *      configuration = Configuration(
     *          smsSenderId = "DemoApp",
     *          smsMaxPriceUSD = "0.5",
     *          region = EU_WEST_3,
     *      ),
     * )
     * ```
     *
     * @throws SendSMSFailedException if there is any IO related failure, failure to get credentials or any error in SNS AWS service.
     * @throws InvalidMessageIdException if the response does not contain messageId.
     */
    @Throws(SendSMSFailedException::class, InvalidMessageIdException::class)
    override suspend fun sendSMS(
        message: String,
        phoneNumber: String,
        smsType: SMSType,
    ): String {
        try {
            val publishResponse = sendSMSAsync(message, phoneNumber, smsType).await()
            return publishResponse.messageId() ?: throw InvalidMessageIdException("Unable to acquire messageId")
        } catch (e: SdkClientException) {
            // This exception occurs when there is IO related failure, failure to get credentials, etc.
            throw SendSMSFailedException(e)
        } catch (e: SnsException) {
            // This exception occurs when there is any error in SNS aws service.
            throw SendSMSFailedException(e)
        }
    }

    /**
     * This method returns a completable future which is awaited by calling suspend function.
     * See [details](https://github.com/Kotlin/kotlinx.coroutines/tree/master/integration/kotlinx-coroutines-jdk8).
     */
    private fun sendSMSAsync(
        message: String,
        phoneNumber: String,
        smsType: SMSType,
    ): CompletableFuture<PublishResponse> {
        val smsAttributes =
            hashMapOf(
                AWS_SMS_SENDER_ID to
                    MessageAttributeValue.builder()
                        .stringValue(configuration.smsSenderId).dataType("String").build(),
                AWS_SMS_MAX_PRICE to
                    MessageAttributeValue.builder()
                        .stringValue(configuration.smsMaxPriceUSD).dataType("Number").build(),
                AWS_SMS_SMS_TYPE to
                    MessageAttributeValue.builder()
                        .stringValue("$smsType").dataType("String").build(),
            )
        return snsClient.publish(
            PublishRequest.builder()
                .message(message)
                .phoneNumber(phoneNumber)
                .messageAttributes(smsAttributes).build(),
        )
    }

    private fun getSNSAsyncClient(region: Region): SnsAsyncClient {
        val sdkAsyncHttpClient =
            NettyNioAsyncHttpClient
                .builder()
                .connectionAcquisitionTimeout(CONNECTION_ACQUISITION_TIMEOUT_SECONDS)
                .build()
        return SnsAsyncClient.builder()
            .httpClient(sdkAsyncHttpClient)
            .region(region)
            .build()
    }
}

private const val AWS_SMS_SENDER_ID = "AWS.SNS.SMS.SenderID"
private const val AWS_SMS_MAX_PRICE = "AWS.SNS.SMS.MaxPrice"
private const val AWS_SMS_SMS_TYPE = "AWS.SNS.SMS.SMSType"
