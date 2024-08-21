package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Get [LocalDate] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [LocalDate] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [LocalDate].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLocalDate(key: String): LocalDate {
    return getLocalDateOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [LocalDate] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [LocalDate] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [LocalDate].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLocalDateOrNull(key: String): LocalDate? {
    val stringValue = getStringOrNull(key) ?: return null
    try {
        return LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: DateTimeParseException) {
        throw LeoJSONException("Value for attribute $key is not a valid LocalDate", e)
    }
}

/**
 * Get [LocalDate] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [LocalDate] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [LocalDate].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLocalDateOrDefault(
    key: String,
    defaultValue: LocalDate,
): LocalDate {
    return getLocalDateOrNull(key) ?: return defaultValue
}

/**
 * Convert the [LocalDate] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [LocalDate].
 */
fun LocalDate.toJson(): JsonPrimitive = JsonPrimitive(format(DateTimeFormatter.ISO_LOCAL_DATE))

/**
 * Get the list of [LocalDate] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [LocalDate] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [LocalDate].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLocalDateListOrNull(key: String): List<LocalDate>? {
    val stringList = getStringListOrNull(key) ?: return null
    return stringList.map {
        try {
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: DateTimeParseException) {
            throw LeoJSONException("An element in attribute $key is not a valid LocalDate", e)
        }
    }
}

/**
 * Get the list of [LocalDate] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [LocalDate] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [LocalDate].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLocalDateList(key: String): List<LocalDate> {
    return getLocalDateListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [LocalDate] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [LocalDate] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [LocalDate].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLocalDateListOrDefault(
    key: String,
    defaultValue: List<LocalDate>,
): List<LocalDate> {
    return getLocalDateListOrNull(key) ?: defaultValue
}
