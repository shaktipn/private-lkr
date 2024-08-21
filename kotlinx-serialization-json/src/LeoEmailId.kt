package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoEmailId
import com.suryadigital.leo.types.LeoInvalidLeoEmailIdException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Get [LeoEmailId] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [LeoEmailId] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [LeoEmailId].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoEmailId(key: String): LeoEmailId {
    return getLeoEmailIdOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [LeoEmailId] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [LeoEmailId] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [LeoEmailId].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoEmailIdOrNull(key: String): LeoEmailId? {
    val stringValue = getStringOrNull(key) ?: return null
    try {
        return LeoEmailId(stringValue)
    } catch (e: LeoInvalidLeoEmailIdException) {
        throw LeoJSONException("Value for attribute $key is not a valid LeoEmailId", e)
    }
}

/**
 * Get [LeoEmailId] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [LeoEmailId] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [LeoEmailId].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoEmailIdOrDefault(
    key: String,
    defaultValue: LeoEmailId,
): LeoEmailId {
    return getLeoEmailIdOrNull(key) ?: return defaultValue
}

/**
 * Convert the [LeoEmailId] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [LeoEmailId].
 */
fun LeoEmailId.toJson(): JsonPrimitive = JsonPrimitive(value)

/**
 * Get the list of [LeoEmailId] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [LeoEmailId] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [LeoEmailId].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoEmailIdListOrNull(key: String): List<LeoEmailId>? {
    val stringList = getStringListOrNull(key) ?: return null
    return stringList.map {
        try {
            LeoEmailId(it)
        } catch (e: LeoInvalidLeoEmailIdException) {
            throw LeoJSONException("An element in attribute $key is not a valid LeoEmailId", e)
        }
    }
}

/**
 * Get a list of [LeoEmailId] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [LeoEmailId] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [LeoEmailId].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoEmailIdListOrDefault(
    key: String,
    defaultValue: List<LeoEmailId>,
): List<LeoEmailId> {
    return getLeoEmailIdListOrNull(key) ?: return defaultValue
}

/**
 * Get the list of [LeoEmailId] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [LeoEmailId] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [LeoEmailId].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getLeoEmailIdList(key: String): List<LeoEmailId> {
    return getLeoEmailIdListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}
