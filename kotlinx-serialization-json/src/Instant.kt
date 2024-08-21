package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Get [Instant] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Instant] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [Instant].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getInstant(key: String): Instant {
    return getInstantOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [Instant] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Instant] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [Instant].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getInstantOrNull(key: String): Instant? {
    val stringValue = getStringOrNull(key) ?: return null
    try {
        return OffsetDateTime.parse(stringValue, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
    } catch (e: DateTimeParseException) {
        throw LeoJSONException("Value for attribute $key is not a valid Instant", e)
    }
}

/**
 * Get [Instant] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [Instant] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Instant].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getInstantOrDefault(
    key: String,
    defaultValue: Instant,
): Instant {
    return getInstantOrNull(key) ?: return defaultValue
}

/**
 * Convert the [Instant] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [Instant].
 */
fun Instant.toJson(): JsonPrimitive = JsonPrimitive(atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))

/**
 * Get the list of [Instant] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [Instant] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [Instant].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getInstantListOrNull(key: String): List<Instant>? {
    val stringList = getStringListOrNull(key) ?: return null
    return stringList.map {
        try {
            OffsetDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
        } catch (e: DateTimeParseException) {
            throw LeoJSONException("An element in attribute $key is not a valid Instant", e)
        }
    }
}

/**
 * Get a list of [Instant] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [Instant] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Instant].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getInstantListOrDefault(
    key: String,
    defaultValue: List<Instant>,
): List<Instant> {
    return getInstantListOrNull(key) ?: defaultValue
}

/**
 * Get the list of [Instant] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [Instant] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [Instant].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getInstantList(key: String): List<Instant> {
    return getInstantListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}
