package com.suryadigital.leo.ktUtils

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Converts [Instant] to [OffsetDateTime] with UTC timezone.
 *
 * @return [OffsetDateTime] of [Instant] with UTC timezone.
 */
fun Instant.toUTCOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
}
