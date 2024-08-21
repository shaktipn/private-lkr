package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.UUID

/**
 * Get [UUID] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [UUID] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [UUID].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getUUID(key: String): UUID {
    return getUUIDOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [UUID] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [UUID] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [UUID].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getUUIDOrNull(key: String): UUID? {
    val stringValue = getStringOrNull(key) ?: return null
    try {
        return UUID.fromString(stringValue)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a valid UUID", e)
    }
}

/**
 * Get [UUID] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [UUID] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [UUID].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getUUIDOrDefault(
    key: String,
    defaultValue: UUID,
): UUID {
    return getUUIDOrNull(key) ?: return defaultValue
}

/**
 * Convert the [UUID] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [UUID].
 */
fun UUID.toJson(): JsonPrimitive = JsonPrimitive(this.toString())

/**
 * Get the list of [UUID] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [UUID] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [UUID].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getUUIDListOrNull(key: String): List<UUID>? {
    val stringList = getStringListOrNull(key) ?: return null
    return stringList.map {
        try {
            UUID.fromString(it)
        } catch (e: IllegalArgumentException) {
            throw LeoJSONException("An element in attribute $key is not a valid UUID", e)
        }
    }
}

/**
 * Get the list of [UUID] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [UUID] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [UUID].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getUUIDList(key: String): List<UUID> {
    return getUUIDListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [UUID] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [UUID] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [UUID].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getUUIDListOrDefault(
    key: String,
    defaultValue: List<UUID>,
): List<UUID> {
    return getUUIDListOrNull(key) ?: defaultValue
}
