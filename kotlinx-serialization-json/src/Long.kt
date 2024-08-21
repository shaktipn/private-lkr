package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

/**
 * Get [Long] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Long] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [Long].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLong(key: String): Long {
    return getLongOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [Long] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Long] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [Long].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLongOrNull(key: String): Long? {
    try {
        val value = this[key] ?: return null
        val primitive = value.jsonPrimitive
        if (primitive is JsonNull) {
            return null
        }
        return primitive.long
    } catch (e: NumberFormatException) {
        throw LeoJSONException("Value for attribute $key is not a valid Long", e)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a valid Long", e)
    }
}

/**
 * Get [Long] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [Long] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Long].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLongOrDefault(
    key: String,
    defaultValue: Long,
): Long {
    return getLongOrNull(key) ?: return defaultValue
}

/**
 * Get the list of [Long] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [Long] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [Long].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLongList(key: String): List<Long> {
    return getLongListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get the list of [Long] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [Long] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [Long].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLongListOrDefault(
    key: String,
    defaultValue: List<Long>,
): List<Long> {
    return getLongListOrNull(key) ?: defaultValue
}

/**
 * Get the list of [Long] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [Long] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [Long].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLongListOrNull(key: String): List<Long>? {
    val array = getJsonArrayOrNull(key) ?: return null
    return array.map(Long.Companion::fromJson)
}

/**
 * Get the [Long] value from the [jsonElement].
 *
 * @param jsonElement element of the [JsonObject] which should be parsed as a [Long].
 *
 * @return [Long] value parsed from the [jsonElement].
 *
 * @throws LeoJSONException if the [jsonElement] is not a valid [Long].
 */
@Throws(LeoJSONException::class)
fun Long.Companion.fromJson(jsonElement: JsonElement): Long {
    try {
        return jsonElement.jsonPrimitive.long
    } catch (e: NumberFormatException) {
        throw LeoJSONException("Element $jsonElement is not a valid Long", e)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Element $jsonElement is not a valid Long", e)
    }
}

/**
 * Convert the [Long] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [Long].
 */
fun Long.toJson(): JsonPrimitive = JsonPrimitive(this)
