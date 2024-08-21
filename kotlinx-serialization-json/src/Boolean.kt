package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

/**
 * Get [Boolean] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Boolean] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [Boolean].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getBoolean(key: String): Boolean {
    return getBooleanOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [Boolean] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Boolean] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [Boolean].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getBooleanOrNull(key: String): Boolean? {
    try {
        val value = this[key] ?: return null
        val primitive = value.jsonPrimitive
        if (primitive is JsonNull) {
            return null
        }
        return primitive.boolean
    } catch (e: IllegalStateException) {
        throw LeoJSONException("Value for attribute $key is not a valid Boolean", e)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a valid Boolean", e)
    }
}

/**
 * Get [Boolean] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [Boolean] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Boolean].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getBooleanOrDefault(
    key: String,
    defaultValue: Boolean,
): Boolean {
    return getBooleanOrNull(key) ?: return defaultValue
}

/**
 * Get the list of [Boolean] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [Boolean] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [Boolean].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getBooleanListOrNull(key: String): List<Boolean>? {
    val array = getJsonArrayOrNull(key) ?: return null
    return array.map(Boolean.Companion::fromJson)
}

/**
 * Get the list of [Boolean] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [Boolean] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [Boolean].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getBooleanList(key: String): List<Boolean> {
    return getBooleanListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [Boolean] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [Boolean] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Boolean].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getBooleanListOrDefault(
    key: String,
    defaultValue: List<Boolean>,
): List<Boolean> {
    return getBooleanListOrNull(key) ?: defaultValue
}

/**
 * Get the [Boolean] value from the [jsonElement].
 *
 * @param jsonElement element of the [JsonObject] which should be parsed as a [Boolean].
 *
 * @return [Boolean] value parsed from the [jsonElement].
 *
 * @throws LeoJSONException if the [jsonElement] is not a valid [Boolean].
 */
@Throws(LeoJSONException::class)
fun Boolean.Companion.fromJson(jsonElement: JsonElement): Boolean {
    try {
        return jsonElement.jsonPrimitive.boolean
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Element $jsonElement is not a valid Boolean", e)
    } catch (e: IllegalStateException) {
        throw LeoJSONException("Element $jsonElement is not a valid Boolean", e)
    }
}

/**
 * Convert the [Boolean] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [Boolean].
 */
fun Boolean.toJson(): JsonPrimitive = JsonPrimitive(this)
