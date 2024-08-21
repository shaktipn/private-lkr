package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.util.Base64

/**
 * Serializes a [ByteArray] using Base64 encoding and decoding.
 */
class ByteArraySerializer : KSerializer<ByteArray> {
    /**
     * Parses a [ByteArray] using [String] decoding.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): ByteArray {
        try {
            return Base64.getDecoder().decode(decoder.decodeString().ifEmpty { throw IllegalArgumentException() })
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
        PrimitiveSerialDescriptor("com.suryadigital.leo.ByteArraySerializer", PrimitiveKind.STRING)

    /**
     * Formats a [ByteArray] using base64 [String] encoding.
     */
    override fun serialize(
        encoder: Encoder,
        value: ByteArray,
    ) {
        encoder.encodeString(Base64.getEncoder().encodeToString(value))
    }
}
