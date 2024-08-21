package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Serializes a [OffsetDateTime] in ISO 8601 format.
 */
class OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    /**
     *Parses a date-time with an offset, such as '2011-12-03T10:15:30+01:00'.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): OffsetDateTime {
        try {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(decoder.decodeString(), OffsetDateTime::from)
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
        PrimitiveSerialDescriptor("com.suryadigital.leo.OffsetDateTimeSerializer", PrimitiveKind.STRING)

    /**
     *Formats a date-time with an offset, such as '2011-12-03T10:15:30+01:00'.
     */
    override fun serialize(
        encoder: Encoder,
        value: OffsetDateTime,
    ) {
        encoder.encodeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value))
    }
}
