package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoInvalidLeoPhoneNumberException
import com.suryadigital.leo.types.LeoPhoneNumber
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

/**
 * Serializes a [LeoPhoneNumber] using [String] encoding and decoding.
 */
class LeoPhoneNumberSerializer : KSerializer<LeoPhoneNumber> {
    /**
     * Parses the [LeoPhoneNumber] using [String] decoding.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): LeoPhoneNumber {
        try {
            return LeoPhoneNumber(decoder.decodeString())
        } catch (e: LeoInvalidLeoPhoneNumberException) {
            throw LeoJSONException(e)
        } catch (e: ClassCastException) {
            throw LeoJSONException(e)
        } catch (e: IllegalArgumentException) {
            throw LeoJSONException(e)
        }
    }

    /**
     * Describes the structure of the serializable type.
     */
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.suryadigital.leo.LeoPhoneNumberSerializer", PrimitiveKind.STRING)

    /**
     * Formats the [LeoPhoneNumber] using [String] encoding.
     */
    override fun serialize(
        encoder: Encoder,
        value: LeoPhoneNumber,
    ) {
        encoder.encodeString(value = value.value)
    }
}
