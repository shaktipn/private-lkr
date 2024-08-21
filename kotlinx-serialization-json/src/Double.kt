package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.abs

/**
 * Get [Double] value for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Double] value associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if the value is not a valid [Double].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getDouble(key: String): Double {
    return getDoubleOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get [Double] value optionally for the given [key] in [JsonObject].
 *
 * @param key for which the value must be fetched.
 *
 * @return [Double] value associated with that [key], or null if the [key] is not found.
 *
 * @throws LeoJSONException if the value is not a valid [Double].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getDoubleOrNull(key: String): Double? {
    try {
        val value = this[key] ?: return null
        val primitive = value.jsonPrimitive
        if (primitive is JsonNull) {
            return null
        }
        return primitive.double
    } catch (e: NumberFormatException) {
        throw LeoJSONException("Value for attribute $key is not a valid Double", e)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Value for attribute $key is not a valid Double", e)
    }
}

/**
 * Get [Double] value for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the value must be fetched.
 * @param defaultValue value that should be returned if the key is not found.
 *
 * @return [Double] value associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Double].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getDoubleOrDefault(
    key: String,
    defaultValue: Double,
): Double {
    return getDoubleOrNull(key) ?: return defaultValue
}

/**
 * Get the list of [Double] values for the given [key] in [JsonObject], or null if the key is not found. If the [JsonObject] value is empty, an empty array is returned.
 *
 * @param key for which the values must be fetched.
 *
 * @return [Double] values associated with that [key], or null.
 *
 * @throws LeoJSONException if even one of the values is not a valid [Double].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getDoubleListOrNull(key: String): List<Double>? {
    val array = getJsonArrayOrNull(key) ?: return null
    return array.map(Double.Companion::fromJson)
}

/**
 * Get the list of [Double] values for the given [key] in [JsonObject].
 *
 * @param key for which the values must be fetched.
 *
 * @return [Double] values associated with that [key].
 *
 * @throws LeoJSONException if the [key] is not found, or if even one of the values is not a valid [Double].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getDoubleList(key: String): List<Double> {
    return getDoubleListOrNull(key) ?: throw LeoJSONException("Attribute $key is missing")
}

/**
 * Get a list of [Double] values for the given [key] in [JsonObject], or [defaultValue] if the key is not found.
 *
 * @param key for which the values must be fetched.
 * @param defaultValue values that should be returned if the key is not found.
 *
 * @return [Double] values associated with that [key], or the [defaultValue].
 *
 * @throws LeoJSONException if the value is not a valid [Double].
 */
@Throws(LeoJSONException::class)
fun JsonObject.getDoubleListOrDefault(
    key: String,
    defaultValue: List<Double>,
): List<Double> {
    return getDoubleListOrNull(key) ?: defaultValue
}

/**
 * Get the [Double] value from the [jsonElement].
 *
 * @param jsonElement element of the [JsonObject] which should be parsed as a [Double].
 *
 * @return [Double] value parsed from the [jsonElement].
 *
 * @throws LeoJSONException if the [jsonElement] is not a valid [Double].
 */
@Throws(LeoJSONException::class)
fun Double.Companion.fromJson(jsonElement: JsonElement): Double {
    try {
        return jsonElement.jsonPrimitive.double
    } catch (e: NumberFormatException) {
        throw LeoJSONException("Element $jsonElement is not a valid Double", e)
    } catch (e: IllegalArgumentException) {
        throw LeoJSONException("Element $jsonElement is not a valid Double", e)
    }
}

/**
 * Convert the [Double] value into the [JsonPrimitive] for the [JsonObject].
 *
 * @return [JsonPrimitive] value converted from the [Double].
 */
fun Double.toJson(): JsonPrimitive = JsonPrimitive(this)

/**
 * Equality function to check that the different between the [Double] and another [Double] value is less than the acceptable limit.
 *
 * @param other [Double] value to which the given [Double] value must be compared.
 *
 * @return [Boolean] determining the delta. If the value is true, the difference between the value is of an acceptable margin, otherwise false.
 */
fun Double.equalsDelta(other: Double): Boolean = abs(this - other) < 0.000001

/**
 * Equality function to check that the different between the [Double] and the [other] value is less than the acceptable limit.
 *
 * @param other [Int] value to which the given [Double] value must be compared.
 *
 * @return [Boolean] determining the delta. If the value is true, the difference between the value is of an acceptable margin, otherwise false.
 */
fun Double.equalsDelta(other: Int): Boolean = abs(this - other) < 0.000001
