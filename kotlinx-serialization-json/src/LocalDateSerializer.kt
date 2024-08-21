package com.suryadigital.leo.kotlinxserializationjson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Serializes a [LocalDate] using [DateTimeFormatter.ISO_LOCAL_DATE] formatting.
 */
class LocalDateSerializer : KSerializer<LocalDate> {
    /**
     * Parses a date without an offset such as '2011-12-03'.
     */
    @Throws(LeoJSONException::class)
    override fun deserialize(decoder: Decoder): LocalDate {
        try {
            return DateTimeFormatter.ISO_LOCAL_DATE.parse(decoder.decodeString(), LocalDate::from)
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
        PrimitiveSerialDescriptor("com.suryadigital.leo.LocalDateSerializer", PrimitiveKind.STRING)

    /**
     * Formats a date without an offset such as '2011-12-03'.
     */
    override fun serialize(
        encoder: Encoder,
        value: LocalDate,
    ) {
        encoder.encodeString(DateTimeFormatter.ISO_LOCAL_DATE.format(value))
    }
}
