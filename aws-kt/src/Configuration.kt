package com.suryadigital.leo.awskt

import software.amazon.awssdk.regions.Region
import java.time.Duration

// This is in top-level package because placing this file in sns package gives error `Package directive doesn't match file location. Change file's package to sns`.
// This should be moved to sns package when the solution is found.

/**
 * Container class for [com.suryadigital.leo.awskt.sns.SNSClientImpl] configuration options.
 *
 * @property smsSenderId id or brand string displayed on the recipient side.
 * @property smsMaxPriceUSD maximum amount in USD that you are willing to spend each month.
 * @property region in which the SMS should be sent.
 */
data class Configuration(
    val smsSenderId: String,
    val smsMaxPriceUSD: String,
    val region: Region,
)

/**
 * Fix TimeoutException in AWS SDK.
 * See [details](https://app.clubhouse.io/resolut-tech/story/4469/fix-timeoutexception-in-aws-sdk).
 */
internal val CONNECTION_ACQUISITION_TIMEOUT_SECONDS = Duration.ofSeconds(20)
