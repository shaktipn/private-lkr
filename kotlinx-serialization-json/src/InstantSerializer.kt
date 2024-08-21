package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Serializes a [Instant] using [DateTimeFormatter.ISO_INSTANT] formatting.
 */
class InstantSerializer : KSerializer<Instant> {
    /**
     * Parses an instant in UTC, such as '2011-12-03T10:15:30Z'.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): Instant {
        try {
            return DateTimeFormatter.ISO_INSTANT.parse(decoder.decodeString(), Instant::from)
        } catch (e: DateTimeParseException) {
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
        PrimitiveSerialDescriptor("com.suryadigital.leo.InstantSerializer", PrimitiveKind.STRING)

    /**
     * Formats an instant in UTC, such as '2011-12-03T10:15:30Z'.
     */
    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeString(DateTimeFormatter.ISO_INSTANT.format(value))
    }
}
