package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

/**
 * Get [Int] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Int] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [Int].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getInt(key: String): Int {
    return getIntOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [Int] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Int] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [Int].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getIntOrNull(key: String): Int? {
    try {
        val value = this[key] ?: return null
        val primitive = value.jsonPrimitive
        if (primitive is JsonNull) {
            return null
        }
        return primitive.int
    } catch (e: NumberFormatException) {
        throw LeoJSONException("Value for attribute $key is not a valid Int", e)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a valid Int", e)
    }
}

/**
 * Get [Int] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [Int] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Int].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getIntOrDefault(
    key: String,
    defaultValue: Int,
): Int {
    return getIntOrNull(key) ?: return defaultValue
}

/**
 * Get the list of [Int] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [Int] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [Int].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getIntListOrNull(key: String): List<Int>? {
    val array = getJsonArrayOrNull(key) ?: return null
    return array.map(Int.Companion::fromJson)
}

/**
 * Get the list of [Int] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [Int] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [Int].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getIntList(key: String): List<Int> {
    return getIntListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [Int] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [Int] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Int].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getIntListOrDefault(
    key: String,
    defaultValue: List<Int>,
): List<Int> {
    return getIntListOrNull(key) ?: defaultValue
}

/**
 * Get the [Int] value from the [jsonElement].
 *
 * @param jsonElement element of the [JsonObject] which should be parsed as a [Int].
 *
 * @return [Int] value parsed from the [jsonElement].
 *
 * @throws LeoJSONException if the [jsonElement] is not a valid [Int].
 */
@Throws(LeoJSONException::class)
fun Int.Companion.fromJson(jsonElement: JsonElement): Int {
    try {
        return jsonElement.jsonPrimitive.int
    } catch (e: NumberFormatException) {
        throw LeoJSONException("Element $jsonElement is not a valid Int", e)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Element $jsonElement is not a valid Int", e)
    }
}

/**
 * Convert the [Int] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [Int].
 */
fun Int.toJson(): JsonPrimitive = JsonPrimitive(this)
