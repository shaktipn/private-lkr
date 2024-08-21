package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Base64

/**
 * Get [ByteArray] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [ByteArray] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [ByteArray].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getByteArray(key: String): ByteArray {
    return getByteArrayOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [ByteArray] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [ByteArray] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [ByteArray].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getByteArrayOrNull(key: String): ByteArray? {
    val stringValue = getStringOrNull(key) ?: return null
    try {
        return Base64.getDecoder().decode(stringValue)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a valid ByteArray", e)
    }
}

/**
 * Get [ByteArray] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [ByteArray] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [ByteArray].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getByteArrayOrDefault(
    key: String,
    defaultValue: ByteArray,
): ByteArray {
    return getByteArrayOrNull(key) ?: return defaultValue
}

/**
 * Convert the [ByteArray] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [ByteArray].
 */
fun ByteArray.toJson(): JsonPrimitive = JsonPrimitive(Base64.getEncoder().encodeToString(this))

/**
 * Get the list of [ByteArray] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [ByteArray] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [ByteArray].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getByteArrayListOrNull(key: String): List<ByteArray>? {
    val stringList = getStringListOrNull(key) ?: return null
    return stringList.map {
        try {
            Base64.getDecoder().decode(it)
        } catch (e: IllegalArgumentException) {
            throw LeoJSONException("An element in attribute $key is not a valid ByteArray", e)
        }
    }
}

/**
 * Get the list of [ByteArray] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [ByteArray] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [ByteArray].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getByteArrayList(key: String): List<ByteArray> {
    return getByteArrayListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}
