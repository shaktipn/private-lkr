package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.net.MalformedURLException
import java.net.URL

/**
 * Get [URL] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [URL] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [URL].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getURL(key: String): URL {
    return getURLOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [URL] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [URL] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [URL].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getURLOrNull(key: String): URL? {
    val stringValue = getStringOrNull(key) ?: return null
    try {
        return URL(stringValue)
    } catch (e: MalformedURLException) {
        throw LeoJSONException("Value for attribute $key is not a valid URL", e)
    }
}

/**
 * Get [URL] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [URL] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [URL].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getURLOrDefault(
    key: String,
    defaultValue: URL,
): URL {
    return getURLOrNull(key) ?: return defaultValue
}

/**
 * Convert the [URL] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [URL].
 */
fun URL.toJson(): JsonPrimitive = JsonPrimitive(this.toString())

/**
 * Get the list of [URL] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [URL] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [URL].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getURLListOrNull(key: String): List<URL>? {
    val stringList = getStringListOrNull(key) ?: return null
    return stringList.map {
        try {
            URL(it)
        } catch (e: MalformedURLException) {
            throw LeoJSONException("An element in attribute $key is not a valid URL", e)
        }
    }
}

/**
 * Get the list of [URL] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [URL] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [URL].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getURLList(key: String): List<URL> {
    return getURLListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [URL] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [URL] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [URL].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getURLListOrDefault(
    key: String,
    defaultValue: List<URL>,
): List<URL> {
    return getURLListOrNull(key) ?: defaultValue
}
