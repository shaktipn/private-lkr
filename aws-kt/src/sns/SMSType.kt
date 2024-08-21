package com.suryadigital.leo.awskt.sns

/**
 * Defines the type of SMS that needs to be sent.
 *
 * The following options are defined by AWS as [valid SMS types](https://docs.aws.amazon.com/sns/latest/dg/sms_publish-to-phone.html#sms_publish_console), and should be updated in case AWS changes the valid SMS types.
 */
enum class SMSType {
    /**
     * Critical messages that support customer transactions, such as one-time passcodes for multifactor authentication.
     */
    Transactional,

    /**
     * Non-critical messages, such as marketing messages.
     */
    Promotional,
}
