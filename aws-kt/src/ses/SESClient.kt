package com.suryadigital.leo.awskt.ses

import com.suryadigital.leo.awskt.exceptions.InvalidMessageIdException

/**
 * Interface defined for performing common email operations.
 */
interface SESClient {
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
    suspend fun sendEmail(email: Email): String
}
