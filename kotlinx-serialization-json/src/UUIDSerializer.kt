package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

/**
 * Serializes a [UUID] using [String] encoding and decoding.
 */
class UUIDSerializer : KSerializer<UUID> {
    /**
     * Parses an [UUID] using [String] decoding.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): UUID {
        try {
            return UUID.fromString(decoder.decodeString())
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
        PrimitiveSerialDescriptor("com.suryadigital.leo.UUIDSerializer", PrimitiveKind.STRING)

    /**
     * Formats an [UUID] using [String] encoding.
     */
    override fun serialize(
        encoder: Encoder,
        value: UUID,
    ) {
        encoder.encodeString("$value")
    }
}
