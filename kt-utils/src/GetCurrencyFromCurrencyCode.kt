package com.suryadigital.leo.ktUtils

import java.lang.Exception
import java.util.Currency
import java.util.Locale

/**
 * Returns the currency code (in ISO 4217 format), given a country code (in ISO 3166 format).
 *
 * For example, a country code of `IN` returns a currency code of `INR`, `MW`, returns `MWK`, etc.
 *
 * @param countryCode country code in ISO 3166 format
 * @return currency code for `countryCode` in ISO 4217 format
 */
fun getCurrencyFromCountryCode(countryCode: String): String {
    return try {
        Currency.getInstance(
            Locale(
                "",
                countryCode,
            ),
        ).currencyCode
    } catch (e: IllegalArgumentException) {
        throw InvalidCurrencyCode("Invalid locale for country code $countryCode", e)
    }
}

/**
 * Exception thrown when implementation fails to fetch the currencyCode.
 */
@Suppress("unused")
class InvalidCurrencyCode : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace,
    )
}
