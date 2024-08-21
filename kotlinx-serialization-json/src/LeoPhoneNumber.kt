package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoInvalidLeoPhoneNumberException
import com.suryadigital.leo.types.LeoPhoneNumber
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Get [LeoPhoneNumber] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [LeoPhoneNumber] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [LeoPhoneNumber].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoPhoneNumber(key: String): LeoPhoneNumber {
    return getLeoPhoneNumberOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [LeoPhoneNumber] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [LeoPhoneNumber] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [LeoPhoneNumber].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoPhoneNumberOrNull(key: String): LeoPhoneNumber? {
    val stringValue = getStringOrNull(key) ?: return null
    try {
        return LeoPhoneNumber(stringValue)
    } catch (e: LeoInvalidLeoPhoneNumberException) {
        throw LeoJSONException("Value for attribute $key is not a valid LeoPhoneNumber", e)
    }
}

/**
 * Get [LeoPhoneNumber] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [LeoPhoneNumber] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [LeoPhoneNumber].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoPhoneNumberOrDefault(
    key: String,
    defaultValue: LeoPhoneNumber,
): LeoPhoneNumber {
    return getLeoPhoneNumberOrNull(key) ?: return defaultValue
}

/**
 * Convert the [LeoPhoneNumber] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [LeoPhoneNumber].
 */
fun LeoPhoneNumber.toJson(): JsonPrimitive = JsonPrimitive(value)

/**
 * Get the list of [LeoPhoneNumber] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [LeoPhoneNumber] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [LeoPhoneNumber].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoPhoneNumberListOrNull(key: String): List<LeoPhoneNumber>? {
    val stringList = getStringListOrNull(key) ?: return null
    return stringList.map {
        try {
            LeoPhoneNumber(it)
        } catch (e: LeoInvalidLeoPhoneNumberException) {
            throw LeoJSONException("An element in attribute $key is not a valid LeoPhoneNumber", e)
        }
    }
}

/**
 * Get the list of [LeoPhoneNumber] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [LeoPhoneNumber] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [LeoPhoneNumber].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoPhoneNumberList(key: String): List<LeoPhoneNumber> {
    return getLeoPhoneNumberListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [LeoPhoneNumber] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [LeoPhoneNumber] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [LeoPhoneNumber].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoPhoneNumberListOrDefault(
    key: String,
    defaultValue: List<LeoPhoneNumber>,
): List<LeoPhoneNumber> {
    return getLeoPhoneNumberListOrNull(key) ?: defaultValue
}
