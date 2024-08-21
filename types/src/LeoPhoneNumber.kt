package com.suryadigital.leo.types

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.lang.Exception

private val phoneNumberUtil = PhoneNumberUtil.getInstance()

/**
 * Stores a validated and normalized phone number.
 *
 * @property value phone number [String], that is accessible from the object.
 *
 * @param phoneNumber phone number [String], that is passed to [LeoPhoneNumber] class to create an object.
 * @param validRegions list of valid regions that given phone number should belong to. If `validRegions` is empty, phone number of any region is accepted.
 */
class LeoPhoneNumber
    @Throws(LeoInvalidLeoPhoneNumberException::class)
    constructor(phoneNumber: String, validRegions: List<String> = emptyList()) {
        val value: String

        init {
            for (v in validRegions.iterator()) {
                if (phoneNumberUtil.getCountryCodeForRegion(v) == 0) {
                    throw LeoInvalidLeoPhoneNumberException("Invalid region code $v in $validRegions.")
                }
            }
            val cleaned = phoneNumber.trim().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
            if (!cleaned.startsWith("+")) {
                throw LeoInvalidLeoPhoneNumberException("Phone number $phoneNumber does not start with a `+`.")
            }
            val parsed: Phonenumber.PhoneNumber
            try {
                parsed = phoneNumberUtil.parse(cleaned, null)
                val validRegionCodes: List<Int> = validRegions.map(phoneNumberUtil::getCountryCodeForRegion)
                if (validRegions.isNotEmpty() && !validRegionCodes.contains(parsed.countryCode)) {
                    throw LeoInvalidLeoPhoneNumberException("Phone number $phoneNumber is invalid.")
                }
            } catch (e: NumberParseException) {
                throw LeoInvalidLeoPhoneNumberException("Phone number $phoneNumber is invalid.", e)
            }
            if (!phoneNumberUtil.isValidNumber(parsed)) {
                throw LeoInvalidLeoPhoneNumberException("Phone number $phoneNumber is invalid.")
            }
            value = cleaned
        }

        /**
         * Checks for the equality of [LeoPhoneNumber] based on its [value].
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LeoPhoneNumber

            return value == other.value
        }

        /**
         * @return a hash code for the [LeoPhoneNumber] object based on its [value].
         */
        override fun hashCode(): Int {
            return value.hashCode()
        }

        /**
         * @return the string representation for [LeoPhoneNumber] with its [value].
         */
        override fun toString(): String {
            return "LeoPhoneNumber($value)"
        }

        /**
         * Get the region code of the current [LeoPhoneNumber].
         *
         * @return region code in [String] format.
         *
         * @throws LeoInvalidLeoPhoneNumberException if the region code is not found for the [LeoPhoneNumber].
         */
        @Throws(LeoInvalidLeoPhoneNumberException::class)
        fun getRegionCode(): String {
            val parsed = phoneNumberUtil.parse(value, null)
            // getRegionCodeForCountryCode return values like
            // `ZZ` - when no region was found for given countryCode.
            // `001` - when the countryCode entered is valid but doesn't match a specific region (such as in the case of non-geographical calling codes like 800) the value "001" will be returned (corresponding to the value for World in the UN M.49 schema).
            // return region codes like `IN`, `MW` etc.
            val regionCode = phoneNumberUtil.getRegionCodeForCountryCode(parsed.countryCode)
            if (regionCode == "ZZ") {
                throw LeoInvalidLeoPhoneNumberException("Phone number $value has invalid region code.")
            }
            return regionCode
        }

        /**
         * This functions breaks the given number down and formats it, according to the rules for the country the number is from.
         *
         * @sample "+918862403344" is formatted to "+91 88624 03344".
         * @return the formatted phonenumber based on the country.
         */
        fun getFormattedPhoneNumber(): String {
            val regionCode = getRegionCode()
            return phoneNumberUtil.formatInOriginalFormat(
                phoneNumberUtil.parseAndKeepRawInput(
                    value,
                    regionCode,
                ),
                regionCode,
            )
        }
    }

/**
 * Exception thrown when an error occurs while parsing the [LeoPhoneNumber] from the given [String].
 */
@Suppress("unused")
class LeoInvalidLeoPhoneNumberException : Exception {
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
