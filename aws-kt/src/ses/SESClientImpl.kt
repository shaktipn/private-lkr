package com.suryadigital.leo.awskt.ses

import com.suryadigital.leo.awskt.CONNECTION_ACQUISITION_TIMEOUT_SECONDS
import com.suryadigital.leo.awskt.exceptions.InvalidMessageIdException
import com.suryadigital.leo.types.LeoEmailId
import kotlinx.coroutines.future.await
import org.koin.core.component.KoinComponent
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.Message
import software.amazon.awssdk.services.ses.model.SendEmailRequest
import software.amazon.awssdk.services.ses.model.SesException
import kotlin.jvm.Throws

/**
 * Implementation for the [SESClient].
 */
class SESClientImpl(private val region: Region) : SESClient, KoinComponent {
    private val snsClient: SesAsyncClient

    init {
        snsClient = getSESAsyncClient()
    }

    /**
     * Sends email to multiple recipients using AWS SES.
     * Called using [SESClient] object with specified AWS Region and Sender Email ID.
     *
     * @param email metadata and the content of the email.
     *
     * @throws SendEmailFailedException if sending email fails.
     * @throws InvalidMessageIdException if the response does not contain messageId.
     */
    @Throws(SendEmailFailedException::class, InvalidMessageIdException::class)
    override suspend fun sendEmail(email: Email): String {
        try {
            val emailRequest =
                SendEmailRequest
                    .builder()
                    .destination(
                        Destination.builder()
                            .toAddresses(email.recipient.map(LeoEmailId::value))
                            .ccAddresses(email.cc.map(LeoEmailId::value))
                            .bccAddresses(email.bcc.map(LeoEmailId::value))
                            .build(),
                    )
                    .message(
                        Message.builder()
                            .subject(getSESContent(email.subject))
                            .body(
                                Body.builder()
                                    .html(getSESContent(email.htmlBody))
                                    .text(getSESContent(email.textBody))
                                    .build(),
                            ).build(),
                    )
                    .source(email.sender.value)
                    .replyToAddresses(email.replyTo.value)
                    .build()
            val sendEmailResponse = snsClient.sendEmail(emailRequest).await()
            return sendEmailResponse.messageId() ?: throw InvalidMessageIdException("Unable to acquire messageId")
        } catch (e: SesException) {
            throw SendEmailFailedException("Unable to send email", e)
        }
    }

    private fun getSESContent(content: String) = Content.builder().data(content).build()

    private fun getSESAsyncClient(): SesAsyncClient {
        val sdkAsyncHttpClient =
            NettyNioAsyncHttpClient
                .builder()
                .connectionAcquisitionTimeout(CONNECTION_ACQUISITION_TIMEOUT_SECONDS)
                .build()
        return SesAsyncClient.builder()
            .httpClient(sdkAsyncHttpClient)
            .region(region)
            .build()
    }
}
