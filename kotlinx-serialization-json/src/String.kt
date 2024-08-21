package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Get [String] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [String] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [String].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getString(key: String): String {
    return getStringOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [String] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [String] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [String].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getStringOrNull(key: String): String? {
    try {
        val value = this[key] ?: return null
        val primitive = value.jsonPrimitive
        if (primitive is JsonNull) {
            return null
        }
        return primitive.content
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a valid String", e)
    }
}

/**
 * Get [String] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [String] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [String].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getStringOrDefault(
    key: String,
    defaultValue: String,
): String {
    return getStringOrNull(key) ?: return defaultValue
}

/**
 * Get the list of [String] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [String] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [String].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getStringListOrNull(key: String): List<String>? {
    val array = getJsonArrayOrNull(key) ?: return null
    if (array.contains(JsonNull)) {
        return null
    }
    return array.map(String.Companion::fromJson)
}

/**
 * Get the list of [String] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [String] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [String].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getStringList(key: String): List<String> {
    return getStringListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [String] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [String] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [String].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getStringListOrDefault(
    key: String,
    defaultValue: List<String>,
): List<String> {
    return getStringListOrNull(key) ?: defaultValue
}

/**
 * Get the [String] value from the [jsonElement].
 *
 * @param jsonElement element of the [JsonObject] which should be parsed as a [String].
 *
 * @return [String] value parsed from the [jsonElement].
 *
 * @throws LeoJSONException if the [jsonElement] is not a valid [String].
 */
@Throws(LeoJSONException::class)
fun String.Companion.fromJson(jsonElement: JsonElement): String {
    try {
        val primitive = jsonElement.jsonPrimitive
        if (primitive is JsonNull) {
            throw LeoJSONException("Element $jsonElement is not a valid String")
        }
        return primitive.content
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Element $jsonElement is not a valid String", e)
    }
}

/**
 * Convert the [String] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [String].
 */
fun String.toJson(): JsonPrimitive = JsonPrimitive(this)
