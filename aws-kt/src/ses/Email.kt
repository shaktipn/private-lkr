package com.suryadigital.leo.awskt.ses

import com.suryadigital.leo.types.LeoEmailId

/**
 * Container class for email content and metadata.
 *
 * @property sender [LeoEmailId] of the person from which the email should be sent.
 * @property recipient list of [LeoEmailId] of the people to which the email should be sent.
 * @property subject subject line of the email.
 * @property htmlBody content of the email body in HTML format.
 * @property textBody content of the email body in text format.
 * @property cc list of [LeoEmailId] to which the email should be cc'd.
 * @property bcc list of [LeoEmailId] to which the email should be bcc'd.
 * @property replyTo [LeoEmailId] to which the reply should be sent in case the [recipient] replies to the email.
 *
 * @throws IllegalArgumentException if the number of total recipients ([recipient]+[cc]+[bcc]) exceeds [MAXIMUM_NUMBER_OF_RECIPIENTS].
 */
data class Email(
    val sender: LeoEmailId,
    val recipient: List<LeoEmailId>,
    val subject: String,
    val htmlBody: String,
    val textBody: String,
    val cc: List<LeoEmailId>,
    val bcc: List<LeoEmailId>,
    val replyTo: LeoEmailId,
) {
    init {
        require(cc.size + bcc.size + recipient.size <= MAXIMUM_NUMBER_OF_RECIPIENTS) {
            "Total Number of Recipients exceeds $MAXIMUM_NUMBER_OF_RECIPIENTS"
        }
    }
}

/**
 * The current quota for total number of recipients is 50 that include `To`, `CC` and `BCC`.
 *
 * More information can be found [here](https://docs.aws.amazon.com/ses/latest/dg/quotas.html).
 */
private const val MAXIMUM_NUMBER_OF_RECIPIENTS = 50
