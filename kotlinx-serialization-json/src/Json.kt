package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/**
 * Get [JsonObject] value optionally for the given [key] in the parent [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [JsonObject] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [JsonObject].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonObjectOrNull(key: String): JsonObject? {
    try {
        val value = this[key] ?: return null
        if (value is JsonPrimitive && value is JsonNull) {
            return null
        }
        return value.jsonObject
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a JsonObject", e)
    }
}

/**
 * Get [JsonObject] value for the given [key] in the parent [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [JsonObject] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [JsonObject].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonObject(key: String): JsonObject {
    return getJsonObjectOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get the list of [JsonObject] values for the given [key] in the parent [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [JsonObject] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [JsonObject].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonArrayOrNull(key: String): JsonArray? {
    try {
        val value = this[key] ?: return null
        if (value is JsonPrimitive && value is JsonNull) {
            return null
        }
        return value.jsonArray
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a JsonArray", e)
    }
}

/**
 * Get the list of [JsonObject] values for the given [key] in the parent [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [JsonObject] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [JsonObject].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonArray(key: String): JsonArray {
    return getJsonArrayOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get the list of [JsonObject] values for the given [key] in the parent [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [JsonObject] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [JsonObject].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonObjectListOrNull(key: String): List<JsonObject>? {
    val array = getJsonArrayOrNull(key) ?: return null
    return array.map {
        try {
            it.jsonObject
        } catch (e: IllegalArgumentException) {
            throw LeoJSONException("An element in attribute $key is not a JsonObject", e)
        }
    }
}

/**
 * Get the list of [JsonObject] values for the given [key] in the parent [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [JsonObject] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [JsonObject].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonObjectList(key: String): List<JsonObject> {
    return getJsonObjectListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [JsonObject] values for the given [key] in the parent [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [JsonObject] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [JsonObject].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonObjectListOrDefault(
    key: String,
    defaultValue: List<JsonObject>,
): List<JsonObject> {
    return getJsonObjectListOrNull(key) ?: defaultValue
}

/**
 * Get the list of [JsonElement] values for the given [key] in the parent [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [JsonElement] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [JsonElement].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonElementListOrNull(key: String): List<JsonElement>? {
    val array = getJsonArrayOrNull(key) ?: return null
    return array.map { it }
}

/**
 * Get the list of [JsonElement] values for the given [key] in the parent [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [JsonElement] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [JsonElement].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getJsonElementList(key: String): List<JsonElement> {
    return getJsonElementListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}
