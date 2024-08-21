package com.suryadigital.leo.kotlinxserializationjson

import com.suryadigital.leo.types.LeoEmailId
import com.suryadigital.leo.types.LeoInvalidLeoEmailIdException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.ClassCastException
import java.lang.IllegalArgumentException

/**
 * Serializes a [LeoEmailId] using [String] encoding and decoding.
 */
class LeoEmailIdSerializer : KSerializer<LeoEmailId> {
    /**
     * Parses a [LeoEmailId] using [String] decoding.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): LeoEmailId {
        try {
            return LeoEmailId(decoder.decodeString())
        } catch (e: LeoInvalidLeoEmailIdException) {
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
        PrimitiveSerialDescriptor("com.suryadigital.leo.LeoEmailIdSerializer", PrimitiveKind.STRING)

    /**
     * Formats a [LeoEmailId] using [String] encoding.
     */
    override fun serialize(
        encoder: Encoder,
        value: LeoEmailId,
    ) {
        encoder.encodeString(value.value)
    }
}
